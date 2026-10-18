from pathlib import Path
m=Path('MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai')
def w(n,s):
 p=m/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
w('doom/DoomHolder.java','''package stepsword.mahoutsukai.doom;
/** Per-player persistent cooldown, in overworld game ticks. */
public interface DoomHolder {
 long mahoutsukai$getDoomReadyAt();
 void mahoutsukai$setDoomReadyAt(long tick);
}
''')
p=m/'fabric/mixin/PlayerMahouMixin.java';s=p.read_text().replace('implements MahouHolder','implements MahouHolder, stepsword.mahoutsukai.doom.DoomHolder').replace('    @Inject(method = "stopSleepInBed"','''    @Unique private long mahoutsukai$doomReadyAt;
    public long mahoutsukai$getDoomReadyAt() { return mahoutsukai$doomReadyAt; }
    public void mahoutsukai$setDoomReadyAt(long tick) { mahoutsukai$doomReadyAt = tick; }

    @Inject(method = "stopSleepInBed"''').replace('        input.child("mahoutsukai:player_mahou")','''        mahoutsukai$doomReadyAt = input.getLongOr("mahoutsukai:doom_ready_at", 0);
        input.child("mahoutsukai:player_mahou")''').replace('        MahouStorage.writeNBT','        output.putLong("mahoutsukai:doom_ready_at", mahoutsukai$doomReadyAt);\n        MahouStorage.writeNBT');p.write_text(s)
p=m/'fabric/MahouFabric.java';s=p.read_text();key='        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->';idx=s.index(key);end=s.index('        CommandRegistrationCallback',idx);s=s[:idx]+'''        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ((MahouHolder) newPlayer).mahoutsukai$getMahou().copyMahou(((MahouHolder) oldPlayer).mahoutsukai$getMahou());
            ((stepsword.mahoutsukai.doom.DoomHolder)newPlayer).mahoutsukai$setDoomReadyAt(
                ((stepsword.mahoutsukai.doom.DoomHolder)oldPlayer).mahoutsukai$getDoomReadyAt());
        });
'''+s[end:];p.write_text(s)
w('doom/DoomScroll.java','''package stepsword.mahoutsukai.doom;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;
/** User's additional spell, not recovered Mahou Tsukai code. */
public final class DoomScroll extends SpellScroll {
 public static final int COOLDOWN = 20 * 60 * 10;
 public DoomScroll() { super("doom"); }
 @Override public void useAction(ItemStack stack, Level level, LivingEntity entity, boolean consume) {
  if (!(entity instanceof ServerPlayer player)) return;
  long now = level.getServer().overworld().getGameTime();
  var holder = (DoomHolder)player;
  if (holder.mahoutsukai$getDoomReadyAt() > now) {
   player.sendOverlayMessage(Component.translatable("mahoutsukai.doom.cooldown", (holder.mahoutsukai$getDoomReadyAt() - now + 19) / 20));
   return;
  }
  if (!SpellScroll.matchCaster(player, Utils.getScrollMahou(stack))) return;
  var mahou = Utils.getPlayerMahou(player);
  // Personal mana only: empty players cannot cast a nuclear spell from an incidental battery.
  if (!player.isCreative() && (mahou.getStoredMana() < 100 || mahou.getStoredMana() < mahou.getMaxMana() * 0.95)) {
   player.sendOverlayMessage(Component.translatable("mahoutsukai.doom.mana"));
   return;
  }
  var hit = player.pick(96, 1, false);
  if (hit.getType() == HitResult.Type.MISS) {
   player.sendOverlayMessage(Component.translatable("mahoutsukai.doom.target"));
   return;
  }
  DoomEntity doom = new DoomEntity(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(), level);
  doom.setPos(hit.getLocation());
  doom.setOwner(player.getUUID());
  if (!level.addFreshEntity(doom)) return;
  if (!player.isCreative()) {
   int cost = Math.max(1, (int)Math.ceil(mahou.getStoredMana() * 0.95));
   mahou.setStoredMana(mahou.getStoredMana() - cost);
   stepsword.mahoutsukai.mana.PlayerManaManager.updateManaCounter(player, cost);
  }
  holder.mahoutsukai$setDoomReadyAt(now + COOLDOWN);
  player.getCooldowns().addCooldown(stack, COOLDOWN);
  if (consume && (!player.isCreative() || stepsword.mahoutsukai.config.MTConfig.CREATIVE_MODE_SPELLS)) stack.shrink(1);
  level.playSound(null, doom.blockPosition(), net.minecraft.sounds.SoundEvents.ENDER_DRAGON_GROWL,
   net.minecraft.sounds.SoundSource.PLAYERS, 2, 0.5F);
 }
}
''')
w('doom/DoomEntity.java','''package stepsword.mahoutsukai.doom;

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

/** Persistent, bounded work queue. User-authored addition; never vanilla createExplosion(40). */
public final class DoomEntity extends Entity {
 public static final int PREPARATION_TICKS = 160;
 public static final int BLOCK_RADIUS = 40;
 public static final int LETHAL_RADIUS = 60;
 public static final int CHECKS_PER_TICK = 4096;
 public static final int BREAKS_PER_TICK = 512;
 private static final int WIDTH = BLOCK_RADIUS * 2 + 1;
 private static final int VOLUME = WIDTH * WIDTH * WIDTH;
 private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(DoomEntity.class, EntityDataSerializers.INT);
 private UUID owner;
 private int cursor;
 private boolean detonated;
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
   if (age % 5 == 0) world.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 12, getZ(), 30, 8, 8, 8, 0.05);
   return;
  }
  if (!detonated) {
   detonated = true;
   detonate(world);
  }
  clearNextBatch(world);
  if (cursor >= VOLUME && age >= PREPARATION_TICKS + 40) discard();
 }
 private Player caster(ServerLevel world) {
  if (owner == null) return null;
  var real = world.getServer().getPlayerList().getPlayer(owner);
  if (real != null && real.level() == world) return real;
  // Claims see the original UUID even if its player has died, disconnected or changed dimension.
  return new stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer(world, "DOOM", owner);
 }
 private void detonate(ServerLevel world) {
  var caster = caster(world);
  var source = caster == null ? damageSources().magic() : damageSources().indirectMagic(this, caster);
  for (var target : world.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(LETHAL_RADIUS))) {
   if (target.distanceToSqr(position()) > LETHAL_RADIUS * LETHAL_RADIUS || target.isSpectator()
    || target instanceof Player player && player.isCreative()) continue;
   // Native damage preserves totems and installed damage hooks. Other mods may grant immunity.
   target.hurtServer(world, source, 1000000);
  }
  world.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10, 0.6F);
  world.sendParticles(ParticleTypes.FLASH, getX(), getY() + 12, getZ(), 1, 0, 0, 0, 0);
  world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 12, getZ(), 1, 0, 0, 0, 0);
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
   if (world.destroyBlock(pos, false, caster, 3)) destroyed++;
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
''')
p=m/'entity/ModEntities.java';s=p.read_text().replace(' public static void init()', ''' private static final Identifier DOOM_ID = Identifier.fromNamespaceAndPath("mahoutsukai", "doom");
 private static final EntityType<stepsword.mahoutsukai.doom.DoomEntity> DOOM_TYPE = Registry.register(BuiltInRegistries.ENTITY_TYPE, DOOM_ID,
  EntityType.Builder.<stepsword.mahoutsukai.doom.DoomEntity>of(stepsword.mahoutsukai.doom.DoomEntity::new, MobCategory.MISC)
   .sized(1, 1).clientTrackingRange(256).updateInterval(1).fireImmune().build(ResourceKey.create(Registries.ENTITY_TYPE, DOOM_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.doom.DoomEntity>> DOOM = () -> DOOM_TYPE;
 public static void init()''');# preserve original fae velocity tracking flag using native Fabric builder.
s=s.replace('EntityType.Builder.<FaeEntity>of(FaeEntity::new, MobCategory.CREATURE).sized(0.5F, 0.5F)\n   .clientTrackingRange(200).updateInterval(3)', 'net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<FaeEntity>create(MobCategory.CREATURE, FaeEntity::new)\n   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.5F, 0.5F)).trackable(200, 3, false)');p.write_text(s)
p=m/'item/ModItems.java';s=p.read_text().replace('    static {','    static {\n        register("scroll_doom", stepsword.mahoutsukai.doom.DoomScroll::new);',1);p.write_text(s)
