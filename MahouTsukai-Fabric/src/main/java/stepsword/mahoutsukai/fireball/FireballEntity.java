package stepsword.mahoutsukai.fireball;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity;

/** Combat-only fireball: native projectile collision and damage hooks, without terrain fire. */
public final class FireballEntity extends SmallFireball {
 public static final float DIRECT_DAMAGE=12, SPLASH_DAMAGE=8;
 public static final double BLAST_RADIUS=3, SPEED=1.2, MAX_DISTANCE=96;
 public static final int LIFETIME_TICKS=80, BURN_SECONDS=4;
 private int age;
 private double distance;

 public FireballEntity(EntityType<? extends FireballEntity> type,Level level){
  super(type,level);accelerationPower=0;setNoGravity(true);
 }
 public int age(){return age;}
 public double distanceTravelled(){return distance;}
 @Override protected float getInertia(){return 1;}
 @Override protected boolean shouldBurn(){return false;}
 @Override protected ParticleOptions getTrailParticle(){return ParticleTypes.FLAME;}
 @Override protected void onDeflection(boolean fromAttack){super.onDeflection(fromAttack);accelerationPower=0;}

 @Override public void tick(){
  if(level() instanceof ServerLevel world){
   if(age>=LIFETIME_TICKS||distance>=MAX_DISTANCE){discard();return;}
   double remaining=MAX_DISTANCE-distance;
   if(getDeltaMovement().length()>remaining)setDeltaMovement(getDeltaMovement().normalize().scale(remaining));
   Vec3 end=position().add(getDeltaMovement());
   // Check every chunk crossed before native collision can inspect it; never ticket distant chunks.
   int minX=BlockPos.containing(Math.min(getX(),end.x),getY(),Math.min(getZ(),end.z)).getX()>>4;
   int maxX=BlockPos.containing(Math.max(getX(),end.x),getY(),Math.max(getZ(),end.z)).getX()>>4;
   int minZ=BlockPos.containing(Math.min(getX(),end.x),getY(),Math.min(getZ(),end.z)).getZ()>>4;
   int maxZ=BlockPos.containing(Math.max(getX(),end.x),getY(),Math.max(getZ(),end.z)).getZ()>>4;
   for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)if(!world.hasChunk(x,z)){discard();return;}
   if(world.getFluidState(blockPosition()).is(FluidTags.WATER)){quench(world);return;}
   var water=world.clip(new ClipContext(position(),end,ClipContext.Block.COLLIDER,ClipContext.Fluid.ANY,this));
   if(water.getType()!=HitResult.Type.MISS&&world.getFluidState(water.getBlockPos()).is(FluidTags.WATER)){
    // A mob can stand before the water boundary within the same movement step.
    // Preserve native entity/block collision order instead of quenching beyond that first hit.
    var collision=ProjectileUtil.getHitResultOnMoveVector(this,this::canHitEntity,getClipType());
    if(collision.getType()==HitResult.Type.MISS||position().distanceToSqr(water.getLocation())<=position().distanceToSqr(collision.getLocation())){
     setPos(water.getLocation());quench(world);return;
    }
   }
  }
  Vec3 before=position();super.tick();
  if(!level().isClientSide()){
   age++;distance+=position().distanceTo(before);
   if(age>=LIFETIME_TICKS||distance>=MAX_DISTANCE-.000001)discard();
  }
 }

 @Override protected void onHit(HitResult hit){
  if(!(level() instanceof ServerLevel world)||isRemoved())return;
  // Deliberately bypass SmallFireball.onHitBlock: no fire blocks, explosion or block destruction.
  LivingEntity primary=hit instanceof EntityHitResult entityHit&&entityHit.getEntity() instanceof LivingEntity living?living:null;
  Vec3 center=hit.getLocation().subtract(getDeltaMovement().normalize().scale(.03));
  if(primary!=null)damage(world,primary,DIRECT_DAMAGE);
  for(var target:world.getEntitiesOfClass(LivingEntity.class,new AABB(center,center).inflate(BLAST_RADIUS))){
   if(target==primary||!target.isAlive()||target.isSpectator())continue;
   double radius=target.getBoundingBox().getCenter().distanceTo(center);
   if(radius>=BLAST_RADIUS)continue;
   var ray=world.clip(new ClipContext(center,target.getBoundingBox().getCenter(),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));
   if(ray.getType()==HitResult.Type.MISS)damage(world,target,(float)(SPLASH_DAMAGE*(1-radius/BLAST_RADIUS)));
  }
  impact(world,hit.getLocation());discard();
 }
 private void damage(ServerLevel world,LivingEntity target,float amount){
  Entity caster=getOwner();
  if(caster instanceof net.minecraft.world.entity.Mob&&(caster==target||caster.isAlliedTo(target)))return;
  if(target.fireImmune()||target.isSpectator()||GateWeaponProjectileEntity.shouldAvoid(caster,target))return;
  if(caster instanceof Player shooter&&target instanceof Player player&&!shooter.canHarmPlayer(player))return;
  if(target.hurtServer(world,damageSources().fireball(this,caster),amount))target.igniteForSeconds(BURN_SECONDS);
 }
 private void quench(ServerLevel world){
  world.playSound(null,getX(),getY(),getZ(),SoundEvents.FIRE_EXTINGUISH,SoundSource.PLAYERS,.8F,1.2F);
  world.sendParticles(ParticleTypes.CLOUD,getX(),getY(),getZ(),14,.3,.25,.3,.035);discard();
 }
 private void impact(ServerLevel world,Vec3 pos){
  world.playSound(null,pos.x,pos.y,pos.z,SoundEvents.GENERIC_EXPLODE.value(),SoundSource.PLAYERS,1.3F,1.3F);
  world.playSound(null,pos.x,pos.y,pos.z,SoundEvents.FIRECHARGE_USE,SoundSource.PLAYERS,.7F,.7F);
  world.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH,0xFFFFA43B),pos.x,pos.y,pos.z,1,0,0,0,0);
  world.sendParticles(ParticleTypes.EXPLOSION,pos.x,pos.y,pos.z,1,0,0,0,0);
  world.sendParticles(ParticleTypes.FLAME,pos.x,pos.y,pos.z,30,.5,.5,.5,.12);
  world.sendParticles(ParticleTypes.LAVA,pos.x,pos.y,pos.z,8,.2,.2,.2,.03);
  world.sendParticles(ParticleTypes.LARGE_SMOKE,pos.x,pos.y,pos.z,16,.5,.4,.5,.05);
  for(int i=0;i<24;i++){
   double angle=i*Math.PI/12;
   world.sendParticles(ParticleTypes.FLAME,pos.x+Math.cos(angle)*.7,pos.y+.05,pos.z+Math.sin(angle)*.7,0,Math.cos(angle),.08,Math.sin(angle),.18);
  }
 }
 @Override protected void addAdditionalSaveData(ValueOutput output){
  super.addAdditionalSaveData(output);output.putInt("mahoutsukai_fireball_age",age);output.putDouble("mahoutsukai_fireball_distance",distance);
 }
 @Override protected void readAdditionalSaveData(ValueInput input){
  super.readAdditionalSaveData(input);age=Math.clamp(input.getIntOr("mahoutsukai_fireball_age",0),0,LIFETIME_TICKS);
  double saved=input.getDoubleOr("mahoutsukai_fireball_distance",0);distance=Double.isFinite(saved)?Math.clamp(saved,0,MAX_DISTANCE):MAX_DISTANCE;accelerationPower=0;
 }
}
