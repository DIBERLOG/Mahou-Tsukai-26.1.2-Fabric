package stepsword.mahoutsukai.doom;

import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.util.EffectUtil;

/** One-tick, claim-aware detonation with a wider gravity well during preparation. */
public final class DoomEntity extends Entity {
 public static final int PREPARATION_TICKS = 160;
 public static final float VISUAL_RADIUS = 28;
 public static float visualRadius(float age) {
  float progress = Math.clamp(age / 140F, 0, 1);
  return 0.6F + (VISUAL_RADIUS - 0.6F) * (progress * progress * (3 - 2 * progress));
 }
 public static final int BLOCK_RADIUS = 40;
 public static final int LETHAL_RADIUS = 60;
 public static final int PULL_RADIUS = 96;
 public static final int CHECKS_PER_TICK = 81 * 81 * 81;
 public static final int BREAKS_PER_TICK = CHECKS_PER_TICK;
 private static final int WIDTH = BLOCK_RADIUS * 2 + 1;
 private static final int VOLUME = WIDTH * WIDTH * WIDTH;
 private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(DoomEntity.class, EntityDataSerializers.INT);
 private UUID owner;
 private int cursor;
 private boolean detonated;
 private Player cachedCaster;
 public DoomEntity(EntityType<? extends DoomEntity> type, Level level) {
  super(type, level);
  noPhysics = true;
  setNoGravity(true);
 }
 public void setOwner(UUID id) { owner = id; }
 public UUID getOwner() { return owner; }
 public int age() { return entityData.get(AGE); }
 public int workCursor() { return cursor; }
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(AGE, 0); }
 @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { return false; }
 @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 512 * 512; }
 @Override public void tick() {
  super.tick();
  if (!(level() instanceof ServerLevel world)) return;
  int age = age() + 1;
  entityData.set(AGE, age);
  if (age < PREPARATION_TICKS) {
   attract(world,age);
   float radius = visualRadius(age);
   if (age % 4 == 0) {
    // Send a bounded set of inward particle trails rather than a large volume each tick.
    for (int arm = 0; arm < 12; arm++) {
     double angle = age * 0.09 + arm * Math.PI / 6;
     double x = Math.cos(angle) * radius * 1.7, z = Math.sin(angle) * radius * 1.7;
     world.sendParticles(ParticleTypes.REVERSE_PORTAL, true, false, getX() + x, getY() + Math.sin(angle * 2) * radius * 0.18, getZ() + z, 0, -x, 0, -z, 0.035);
    }
   }
   if(age==1)world.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.DOOM_ARRIVAL_HORN.get(),SoundSource.PLAYERS,8,1F);
   if (age % 20 == 0) world.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 8, 0.5F + age / 320F);
   int interval = age < 80 ? 20 : age < 120 ? 10 : 5;
   if (age % interval == 0) world.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 8, 0.6F + age / 200F);
   if (age == 120) world.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, 8, 0.65F);
   return;
  }
  if (!detonated) {
   detonated = true;
   detonate(world);
  }
  clearNextBatch(world);
  if (cursor >= VOLUME && age >= PREPARATION_TICKS + 60) discard();
 }
 public void attract(ServerLevel world,int age) {
  var targets=new java.util.LinkedHashSet<LivingEntity>(world.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(PULL_RADIUS)));
  targets.addAll(world.players());
  for(var target:targets){
   if(!target.isAlive()||target.isSpectator())continue;
   var delta=position().subtract(target.position().add(0,target.getBbHeight()*.4,0));double distance=delta.length();if(distance>PULL_RADIUS||distance<.5)continue;
   double force=(.045+.20*(1-distance/PULL_RADIUS))*(.5+.5*Math.min(1,age/(double)PREPARATION_TICKS));
   var velocity=target.getDeltaMovement().scale(.92).add(delta.scale(force/distance));if(velocity.lengthSqr()>4)velocity=velocity.normalize().scale(2);
   target.setDeltaMovement(velocity);target.hurtMarked=true;target.fallDistance=0;
   if(target instanceof ServerPlayer player&&age%2==0)player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(target));
  }
 }
 private Player caster(ServerLevel world) {
  if (owner == null) return null;
  var real = world.getServer().getPlayerList().getPlayer(owner);
  if (real != null && real.level() == world) return real;
  // Claims see the original UUID even if its player has died, disconnected or changed dimension.
  if (cachedCaster == null || !cachedCaster.getUUID().equals(owner))
   cachedCaster = new stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer(world, "DOOM", owner);
  return cachedCaster;
 }
 private void detonate(ServerLevel world) {
  var caster = caster(world);
  var damageType = world.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
   .getOrThrow(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,
    net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "doom")));
  var source = new DamageSource(damageType, this, caster);
  for (var target : world.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(LETHAL_RADIUS))) {
   if (target.distanceToSqr(position()) > LETHAL_RADIUS * LETHAL_RADIUS || target.isSpectator()
    || target instanceof Player player && player.isCreative()) continue;
   // Native damage preserves totems and installed damage hooks. Other mods may grant immunity.
   target.hurtServer(world, source, 1000000);
  }
  world.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 10, 0.6F);
  world.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 8, 0.55F);
  world.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT_HORN.get(),SoundSource.PLAYERS,8,1F);
  world.sendParticles(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.FLASH, 0xFFA96CFF), true, false, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
  for(int ring=0;ring<3;ring++)for(int i=0;i<48;i++){
   double angle=i*Math.PI/24, radius=8+ring*14;
   world.sendParticles(ParticleTypes.END_ROD,true,false,getX()+Math.cos(angle)*radius,getY()+(ring-1)*5,getZ()+Math.sin(angle)*radius,0,Math.cos(angle),.18*(ring-1),Math.sin(angle),.6);
  }
  world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,true,false,getX(),getY(),getZ(),160,12,8,12,.35);
  world.sendParticles(ParticleTypes.LARGE_SMOKE,true,false,getX(),getY(),getZ(),100,8,6,8,.2);
  world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, true, false, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
 }
 private void clearNextBatch(ServerLevel world) {
  var caster = caster(world);
  int destroyed = 0;
  int checked = 0;
  var origin = blockPosition();
  while (cursor < VOLUME && checked++ < CHECKS_PER_TICK && destroyed < BREAKS_PER_TICK) {
   int index = cursor++;
   int x = index % WIDTH - BLOCK_RADIUS;
   int y = index / WIDTH % WIDTH - BLOCK_RADIUS;
   int z = index / (WIDTH * WIDTH) - BLOCK_RADIUS;
   if (x*x + y*y + z*z > BLOCK_RADIUS * BLOCK_RADIUS) continue;
   var pos = origin.offset(x, y, z);
   // Never load chunks for the blast, and never change a block without its claim-aware player context.
   if (caster == null || !world.hasChunkAt(pos)) continue;
   var state = world.getBlockState(pos);
   if (state.isAir() || EffectUtil.unchangableBlock(state.getBlock()) || state.getDestroySpeed(world, pos) < 0) continue;
   if (!world.mayInteract(caster, pos) || !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, caster, pos, state, world.getBlockEntity(pos))) continue;
   // Single tick; native section updates coalesce the changed blocks, without per-block break particles.
   if (world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 18)) destroyed++;
  }
 }
 @Override protected void readAdditionalSaveData(ValueInput input) {
  owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
  entityData.set(AGE, Math.max(0, input.getIntOr("age", 0)));
  cursor = Math.clamp(input.getIntOr("cursor", 0), 0, VOLUME);
  detonated = input.getBooleanOr("detonated", false);
 }
 @Override protected void addAdditionalSaveData(ValueOutput output) {
  if (owner != null) output.store("owner", UUIDUtil.CODEC, owner);
  output.putInt("age", age());
  output.putInt("cursor", cursor);
  output.putBoolean("detonated", detonated);
 }
}
