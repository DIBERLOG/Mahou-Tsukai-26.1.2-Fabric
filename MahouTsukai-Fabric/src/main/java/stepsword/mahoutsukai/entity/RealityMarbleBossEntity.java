package stepsword.mahoutsukai.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.effects.projection.RealityMarbleShrine;
import stepsword.mahoutsukai.dataattachments.worldsave.RealityMarbleEncounters;
import stepsword.mahoutsukai.fabric.GuideCatalog;
import stepsword.mahoutsukai.item.ModItems;

/** Illager Archmage in Reality Marble; retains the original entity ID for existing saves.
 * Native cuboid rig and animations are exported from illager_archmage/ by build_archmage.py.
 */
public class RealityMarbleBossEntity extends Monster {
 public static final String entityName = "reality_marble_boss";
 public static final String REWARD_KEY = "marble-boss-reward";
 public static final int ARENA_RADIUS = 160;
 public static final int SPECIAL_INTERVAL = 80;
 public static final int CAST_TICKS = 32;
 public static final int ARRIVAL_TICKS = 50;
 public static final int RECOVERY_TICKS = 12;
 public static final int VEX_LIMIT = 3;
 public static final int PULL_INTERVAL = 220;
 public static final int IDLE_DESPAWN_TICKS = 600;
 public static final int RESET_FIGHT_TICKS = 200;
 public static final int WAKE_DISTANCE = 10;
 public static final int STUCK_TICKS = 40;
 public static final int ESCAPE_COOLDOWN = 100;
 private Vec3 pursuitAnchor;
 private UUID pursuitTarget;
 private int stuckTicks,escapeCooldown;
 /** Session registry of the live boss per arena, so re-entering can never stack a copy. */
 private static final java.util.Map<net.minecraft.resources.ResourceKey<Level>, java.util.Map<BlockPos, RealityMarbleBossEntity>> ACTIVE = new java.util.concurrent.ConcurrentHashMap<>();

 private final ServerBossEvent bossEvent = new ServerBossEvent(
  UUID.randomUUID(),
  Component.translatable("entity.mahoutsukai." + entityName),
  BossEvent.BossBarColor.PURPLE,
  BossEvent.BossBarOverlay.PROGRESS
 );
 /** Synced telegraph so observers see the wind-up before a special lands. */
 private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> ATTACK_TICKS =
  net.minecraft.network.syncher.SynchedEntityData.defineId(RealityMarbleBossEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
 private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> ATTACK_KIND =
  net.minecraft.network.syncher.SynchedEntityData.defineId(RealityMarbleBossEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
 private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> ARRIVAL =
  net.minecraft.network.syncher.SynchedEntityData.defineId(RealityMarbleBossEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
 private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> RECOVERY =
  net.minecraft.network.syncher.SynchedEntityData.defineId(RealityMarbleBossEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
 private int levitationTicks;
 private final java.util.Map<net.minecraft.world.entity.Entity,Integer> effects=new java.util.LinkedHashMap<>();
 private Vec3 castPoint;
 private Vec3 waveCenter;
 private int waveTicks;
 private final Set<UUID> waveVictims=new HashSet<>();
 private boolean recoverEffects;
 private int orphanRecoveryTicks;
 private boolean arrivalSoundPlayed;
 private boolean awakened;
 private BlockPos shrineEntrance;
 public int fangsSpawned,pillarsSpawned,boltsSpawned,vexesSpawned,shockwaveHits;
 public int arrivalFlashes,arrivalSounds,arrivalLaughs;

 private final Set<UUID> attackers = new HashSet<>();
 private final java.util.Map<UUID, ServerPlayer> attackerRefs = new java.util.HashMap<>();
 private BlockPos arena;
 private int specialCooldown = SPECIAL_INTERVAL;
 private int pullCooldown = PULL_INTERVAL;
 private int idleTicks;
 private int idleCombatTicks;
 /** Diagnostics for native checks: how often the special ran and how many players it hit. */
 public int sonicBursts;
 public int sonicHits;
 public int rewardGrants;
 public int rewardMissing;
 public int rewardAlreadyGiven;

 public int attackerCount() {
  return attackers.size();
 }

 public RealityMarbleBossEntity(EntityType<? extends RealityMarbleBossEntity> type, Level level) {
  super(type, level);
  this.xpReward = 120;
  this.setPersistenceRequired();
  this.setHealth(this.getMaxHealth());
  bossEvent.setDarkenScreen(false);
  bossEvent.setCreateWorldFog(false);
 }

 @Override
 protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
  super.defineSynchedData(builder);
  builder.define(ATTACK_TICKS, 0);
  builder.define(ATTACK_KIND, 0);
  builder.define(ARRIVAL, 0);
  builder.define(RECOVERY, 0);
 }

 /** Remaining wind-up ticks of the current special, mirrored to observers. */
 public int attackTicks() {
  return entityData.get(ATTACK_TICKS);
 }
 public int attackKind(){return entityData.get(ATTACK_KIND);}
 public int arrivalTicks(){return entityData.get(ARRIVAL);}
 public int recoveryTicks(){return entityData.get(RECOVERY);}
 private boolean preparing(){return !awakened||arrivalTicks()>0||recoveryTicks()>0||attackTicks()>0;}
 public boolean levitating(){return isNoGravity();}
 public boolean awakened(){return awakened;}
 /** Enter combat once; subsequent approaches or hits never restart the entrance. */
 public void awaken(){
  if(awakened)return;
  awakened=true;entityData.set(ARRIVAL,ARRIVAL_TICKS);arrivalSoundPlayed=false;
 }
 @Override public void setTarget(@org.jetbrains.annotations.Nullable LivingEntity target){super.setTarget(awakened?target:null);}

 /** Only fresh spawns call this automatically; existing saved fights keep their positions. */
 public boolean prepareAtShrine(){
  if(awakened||arena==null||!(level() instanceof ServerLevel level))return false;
  RealityMarbleShrine.ensure(level,arena);
  var waiting=RealityMarbleShrine.bossWaitingPosition(level,arena);
  if(waiting==null)return false;
  Vec3 feet=Vec3.atBottomCenterOf(waiting);double half=getBbWidth()/2.0;
  var body=new AABB(feet.x-half,feet.y+.001,feet.z-half,feet.x+half,feet.y+getBbHeight(),feet.z+half);
  var below=waiting.below();
  if(level.getBlockState(below).getCollisionShape(level,below).isEmpty()||!level.getFluidState(below).isEmpty()
   ||!level.noCollision(this,body)||level.containsAnyLiquid(body))return false;
  shrineEntrance=RealityMarbleShrine.entrance(level,arena);
  snapTo(feet.x,feet.y,feet.z,180,0);setDeltaMovement(Vec3.ZERO);fallDistance=0;setNoGravity(false);
  getNavigation().stop();setTarget(null);entityData.set(ARRIVAL,0);
  return true;
 }
 private void awakenOnApproach(ServerLevel level){
  if(awakened||isNoAi())return;
  if(shrineEntrance==null&&arena!=null)shrineEntrance=RealityMarbleShrine.entrance(level,arena);
  Vec3 entrance=shrineEntrance==null?position():Vec3.atBottomCenterOf(shrineEntrance);
  for(var player:List.copyOf(level.players()))if(player.isAlive()&&!player.isCreative()&&!player.isSpectator()&&inArena(player)
    &&player.position().distanceToSqr(entrance)<=WAKE_DISTANCE*WAKE_DISTANCE){awaken();setTarget(player);return;}
 }

 public static AttributeSupplier.Builder createAttributes() {
  return Monster.createMonsterAttributes()
   .add(Attributes.MAX_HEALTH, 800.0)
   .add(Attributes.ARMOR, 8.0)
   .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
   .add(Attributes.MOVEMENT_SPEED, 0.32)
   .add(Attributes.FOLLOW_RANGE, 64.0)
   .add(Attributes.ATTACK_DAMAGE, 16.0);
 }

 @Override
 protected void registerGoals() {
  goalSelector.addGoal(1, new FloatGoal(this));
  goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true){
   @Override public boolean canUse(){return !preparing()&&super.canUse();}
   @Override public boolean canContinueToUse(){return !preparing()&&super.canContinueToUse();}
  });
  goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.55){
   @Override public boolean canUse(){return !preparing()&&super.canUse();}
   @Override public boolean canContinueToUse(){return !preparing()&&super.canContinueToUse();}
  });
  goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 32.0F));
  targetSelector.addGoal(1, new HurtByTargetGoal(this));
  targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
 }

 public void setArena(BlockPos arena) {
  this.arena = arena;
 }

 public BlockPos arena() {
  return arena;
 }

 /** Visible players on the boss bar, used by native checks and diagnostics. */
 public int bossBarPlayerCount() {
  return bossEvent.getPlayers().size();
 }

 /** True when the target is inside this boss's arena; an unset arena accepts the whole level. */
 public boolean inArena(LivingEntity entity) {
  return arena == null || entity.blockPosition().distSqr(arena) <= (long)ARENA_RADIUS * ARENA_RADIUS;
 }

 @Override
 public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
  if (!(amount > 0) || isInvulnerableTo(level,source)) return false;
  // Record accepted participation before super: a lethal hit calls die() synchronously.
  boolean fresh=source.getEntity() instanceof ServerPlayer p && !attackers.contains(p.getUUID());
  if (source.getEntity() instanceof ServerPlayer player) {
   attackers.add(player.getUUID());
   attackerRefs.put(player.getUUID(), player);
   idleCombatTicks = 0;
  }
  boolean accepted=super.hurtServer(level, source, amount);
  if(accepted&&isAlive()){
   awaken();
   if(source.getEntity() instanceof LivingEntity attacker&&attacker.level()==level&&inArena(attacker))setTarget(attacker);
  }
  if(!accepted && fresh && source.getEntity() instanceof ServerPlayer p){attackers.remove(p.getUUID());attackerRefs.remove(p.getUUID());}
  return accepted;
 }

 @Override
 protected void customServerAiStep(ServerLevel level) {
  super.customServerAiStep(level);
  var target = this.getTarget();
  if (target != null && (!target.isAlive() || target.level() != level || !inArena(target))) {
   this.setTarget(null);
   this.getNavigation().stop();
  }

  boolean anyNearby = false,linkedParticipant=false;
  ServerPlayer linkedTarget=null;
  for (ServerPlayer player : List.copyOf(level.players())) {
   if(player.level()!=level||!player.isAlive()||player.isSpectator())continue;
   var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
   if(inArena(player)&&mahou!=null&&getUUID().equals(mahou.getEnemy())){
    linkedParticipant=true;
    if(!player.isCreative()&&(linkedTarget==null||player.distanceToSqr(this)<linkedTarget.distanceToSqr(this)))linkedTarget=player;
   }
   if(player.distanceToSqr(this)<=(double)ARENA_RADIUS*ARENA_RADIUS)anyNearby=true;
  }
  if (anyNearby||linkedParticipant) {
   idleTicks = 0;
  } else if (++idleTicks > IDLE_DESPAWN_TICKS) {
   this.discard();
   return;
  }

  if(!awakened){setTarget(null);getNavigation().stop();setDeltaMovement(0,getDeltaMovement().y,0);return;}
  if(getTarget()==null&&linkedTarget!=null)setTarget(linkedTarget);
  if (this.getTarget() == null&&!linkedParticipant) {
   if (++idleCombatTicks > RESET_FIGHT_TICKS) {
    idleCombatTicks = 0;
    if (this.getHealth() < this.getMaxHealth()) this.setHealth(this.getMaxHealth());
   }
  } else {
   idleCombatTicks = 0;
  }

  if (arrivalTicks()>0||recoveryTicks()>0){getNavigation().stop();setDeltaMovement(0,getDeltaMovement().y,0);return;}
  if (attackTicks()>0){getNavigation().stop();if(validTarget(getTarget()))getLookControl().setLookAt(getTarget(),30,30);}
  else if (validTarget(getTarget()) && --specialCooldown<=0){specialCooldown=getHealth()<getMaxHealth()*.5F?50:SPECIAL_INTERVAL;sonicBurst(level);}
 }

 private boolean validTarget(LivingEntity target){
  return awakened&&target!=null&&target.isAlive()&&target.level()==level()&&inArena(target)&&distanceToSqr(target)<=64*64
   &&!target.isSpectator()&&!(target instanceof Player p&&p.isCreative())&&!isAlliedTo(target);
 }

 /** A failed path can already be done, so observe actual movement instead of navigation state. */
 private void tickStuckRecovery(ServerLevel level){
  if(escapeCooldown>0)escapeCooldown--;
  var target=getTarget();
  if(!isAlive()||isNoAi()||preparing()||isNoGravity()||isPassenger()||target==null||!target.isAlive()||target.level()!=level
    ||!inArena(target)||target.isSpectator()||target instanceof Player p&&p.isCreative()||isAlliedTo(target)||isWithinMeleeAttackRange(target)){
   pursuitAnchor=null;pursuitTarget=null;stuckTicks=0;return;
  }
  if(pursuitAnchor==null||!target.getUUID().equals(pursuitTarget)||position().subtract(pursuitAnchor).horizontalDistanceSqr()>=.75*.75){
   pursuitAnchor=position();pursuitTarget=target.getUUID();stuckTicks=0;return;
  }
  if(++stuckTicks<STUCK_TICKS||escapeCooldown>0)return;
  stuckTicks=0;escapeCooldown=40;
  Vec3 origin=position();double start=random.nextDouble()*Math.PI*2;
  for(int radius=5;radius>=2;radius--)for(int step=0;step<16;step++)for(int dy:new int[]{0,1,-1,2,-2}){
   double angle=start+step*Math.PI/8;
   var feet=new Vec3(origin.x+Math.cos(angle)*radius,Math.floor(origin.y)+dy,origin.z+Math.sin(angle)*radius);
   if(!safeEscape(level,feet))continue;
   getNavigation().stop();teleportTo(feet.x,feet.y,feet.z);setDeltaMovement(Vec3.ZERO);fallDistance=0;
   pursuitAnchor=position();escapeCooldown=ESCAPE_COOLDOWN;
   for(var point:List.of(origin,feet)){
    level.playSound(null,point.x,point.y,point.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.HOSTILE,.8F,.75F);
    level.sendParticles(ParticleTypes.PORTAL,point.x,point.y+1.5,point.z,35,.45,1,.45,.1);
    level.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD166,1F),point.x,point.y+1.5,point.z,18,.4,.9,.4,.02);
   }
   return;
  }
 }

 private boolean safeEscape(ServerLevel level,Vec3 feet){
  if(feet.distanceToSqr(position())>25.000001||feet.y<level.getMinY()+1||feet.y+getBbHeight()>level.getMaxY())return false;
  if(arena!=null&&BlockPos.containing(feet).distSqr(arena)>(long)ARENA_RADIUS*ARENA_RADIUS)return false;
  double half=getBbWidth()/2.0;
  var body=new AABB(feet.x-half,feet.y+.001,feet.z-half,feet.x+half,feet.y+getBbHeight(),feet.z+half);
  if(!level.getWorldBorder().isWithinBounds(body)||!level.hasChunksAt(BlockPos.containing(body.minX,feet.y-1,body.minZ),BlockPos.containing(body.maxX,body.maxY,body.maxZ))
    ||!level.noCollision(this,body)||level.containsAnyLiquid(body)||!level.getEntities(this,body,e->e instanceof LivingEntity&&e.isAlive()).isEmpty())return false;
  for(double x:new double[]{feet.x-half,feet.x,feet.x+half})for(double z:new double[]{feet.z-half,feet.z,feet.z+half}){
   var floor=BlockPos.containing(x,feet.y-.01,z);
   if(!level.getFluidState(floor).isEmpty()||!level.getBlockState(floor).isFaceSturdy(level,floor,net.minecraft.core.Direction.UP))return false;
  }
  return true;
 }
 @Override protected boolean considersEntityAsAlly(net.minecraft.world.entity.Entity other){
  return super.considersEntityAsAlly(other)||other instanceof net.minecraft.world.entity.monster.Vex v&&v.getOwner()==this;
 }
 @Override protected net.minecraft.sounds.SoundEvent getAmbientSound(){return SoundEvents.EVOKER_AMBIENT;}
 @Override protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source){return SoundEvents.EVOKER_HURT;}
 @Override protected net.minecraft.sounds.SoundEvent getDeathSound(){return SoundEvents.EVOKER_DEATH;}

 /** Every attack has a server-owned, visible 32-tick wind-up before any effect is released. */
 private void sonicBurst(ServerLevel level){
  if(!isAlive()||!validTarget(getTarget())||preparing())return;
  sonicBursts++;castPoint=getTarget().position();
  entityData.set(ATTACK_KIND,attackKind()%5+1);entityData.set(ATTACK_TICKS,CAST_TICKS);
  getNavigation().stop();
  level.playSound(null,getX(),getY(),getZ(),attackKind()==5?SoundEvents.EVOKER_PREPARE_SUMMON:SoundEvents.EVOKER_PREPARE_ATTACK,SoundSource.HOSTILE,1.5F,.8F);
  level.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_CHARGE.get(),SoundSource.HOSTILE,1.5F,1F);
 }
 private void releaseBurst(ServerLevel level){
  if(!isAlive()||!validTarget(getTarget()))return;
  if(castPoint==null)castPoint=getTarget().position();
  level.playSound(null,getX(),getY(),getZ(),SoundEvents.EVOKER_CAST_SPELL,SoundSource.HOSTILE,1.5F,1F);
  level.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_RELEASE.get(),SoundSource.HOSTILE,1.4F,1F);
  switch(attackKind()){
   case 1->fangLine(level);
   case 2->stonePillars(level);
   case 3->fireVolley(level);
   case 4->{waveCenter=position();waveTicks=20;waveVictims.clear();levitationTicks=30;setNoGravity(true);
    level.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_SHOCKWAVE.get(),SoundSource.HOSTILE,2,1);}
   case 5->summonVexes(level);
  }
  entityData.set(RECOVERY,RECOVERY_TICKS);
 }
 private String effectTag(){return "ArchmageOwner:"+getUUID();}
 private boolean addEffect(ServerLevel level,net.minecraft.world.entity.Entity entity,int life){
  entity.addTag(effectTag());
  if(!level.addFreshEntity(entity))return false;
  effects.put(entity,life);return true;
 }
 private BlockPos ground(ServerLevel level,double x,double y,double z){
  var pos=BlockPos.containing(x,y+2,z);
  for(int dy=0;dy<=8;dy++){
   var at=pos.below(dy);if(!level.hasChunkAt(at)||!level.getWorldBorder().isWithinBounds(at))return null;
   if(level.getBlockState(at).isAir()&&level.getBlockState(at.below()).isFaceSturdy(level,at.below(),net.minecraft.core.Direction.UP))return at;
  }
  return null;
 }
 private void fangLine(ServerLevel level){
  double angle=Math.atan2(castPoint.z-getZ(),castPoint.x-getX());
  for(int i=0;i<16;i++){
   double distance=1.25*(i+1);var at=ground(level,getX()+Math.cos(angle)*distance,castPoint.y,getZ()+Math.sin(angle)*distance);
   if(at==null)continue;
   var fang=new net.minecraft.world.entity.projectile.EvokerFangs(level,at.getX()+.5,at.getY(),at.getZ()+.5,(float)angle,i*2,this);
   if(addEffect(level,fang,80))fangsSpawned++;
  }
 }
 private void stonePillars(ServerLevel level){
  for(int i=0;i<6;i++){
   double angle=i*Math.PI/3;var at=ground(level,castPoint.x+Math.cos(angle)*3,castPoint.y,castPoint.z+Math.sin(angle)*3);if(at==null)continue;
   var pillar=new net.minecraft.world.entity.Display.BlockDisplay(net.minecraft.world.entity.EntityType.BLOCK_DISPLAY,level);
   pillar.setBlockState(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.defaultBlockState());
   pillar.setPos(at.getX()-.1,at.getY()-3,at.getZ()-.1);pillar.setNoGravity(true);pillar.addTag("ArchmagePillar");
   pillar.setTransformation(new com.mojang.math.Transformation(new org.joml.Vector3f(),new org.joml.Quaternionf(),new org.joml.Vector3f(1.2F,3F,1.2F),new org.joml.Quaternionf()));
   pillar.setPosRotInterpolationDuration(2);pillar.setViewRange(2);
   if(addEffect(level,pillar,72))pillarsSpawned++;
  }
  level.playSound(null,castPoint.x,castPoint.y,castPoint.z,SoundEvents.STONE_BREAK,SoundSource.HOSTILE,1.6F,.6F);
 }
 private void fireVolley(ServerLevel level){
  var start=getEyePosition().add(getViewVector(1).scale(1.6));
  var aim=getTarget().getEyePosition().subtract(start).normalize();
  for(int i=-1;i<=1;i++){
   var bolt=new stepsword.mahoutsukai.fireball.FireballEntity(ModEntities.FIREBALL.get(),level);
   bolt.setOwner(this);bolt.setPos(start);bolt.setDeltaMovement(aim.yRot(i*.16F).scale(.85));bolt.addTag("ArchmageBolt");
   if(addEffect(level,bolt,80))boltsSpawned++;
  }
  level.playSound(null,getX(),getY(),getZ(),SoundEvents.FIRECHARGE_USE,SoundSource.HOSTILE,1.5F,.8F);
 }
 private void summonVexes(ServerLevel level){
  long owned=effects.keySet().stream().filter(e->e.isAlive()&&e instanceof net.minecraft.world.entity.monster.Vex).count();
  for(int i=0;i<VEX_LIMIT-owned;i++){
   var vex=net.minecraft.world.entity.EntityType.VEX.create(level,net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);if(vex==null)continue;
   var pos=blockPosition().offset(i-1,2,2);vex.snapTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);
   vex.finalizeSpawn(level,level.getCurrentDifficultyAt(pos),net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED,null);
   vex.setOwner(this);vex.setBoundOrigin(blockPosition());vex.setLimitedLife(200);vex.setTarget(getTarget());
   if(addEffect(level,vex,200))vexesSpawned++;
  }
 }
 private boolean hit(ServerLevel level,LivingEntity victim,float damage){
  if(!validTarget(victim)||!hasLineOfSight(victim)||!victim.hurtServer(level,damageSources().mobAttack(this),damage))return false;
  var away=victim.position().subtract(position()).normalize();victim.push(away.x*.55,.35,away.z*.55);victim.hurtMarked=true;sonicHits++;return true;
 }
 private void tickEffects(ServerLevel level){
  // Native display entities survive saves; reclaim only this boss's stale effects after reload.
  if(recoverEffects&&--orphanRecoveryTicks<=0){recoverEffects=false;for(var e:level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,getBoundingBox().inflate(ARENA_RADIUS),e->e.entityTags().contains(effectTag())))e.discard();}
  for(var iterator=effects.entrySet().iterator();iterator.hasNext();){
   var entry=iterator.next();var e=entry.getKey();int left=entry.getValue()-1;
   if(e.isRemoved()||left<=0){e.discard();iterator.remove();continue;}entry.setValue(left);
   if(e instanceof net.minecraft.world.entity.Display.BlockDisplay&&e.entityTags().contains("ArchmagePillar")){
    if(left>=60){e.setPos(e.getX(),e.getY()+.25,e.getZ());
     level.sendParticles(ParticleTypes.ENCHANT,e.getX()+.6,e.getY()+3,e.getZ()+.6,3,.5,.15,.5,.1);
     if(left==60)for(var victim:level.getEntitiesOfClass(LivingEntity.class,new AABB(e.getX()-.4,e.getY(),e.getZ()-.4,e.getX()+1.6,e.getY()+4,e.getZ()+1.6),this::validTarget))hit(level,victim,12);
    }
   }
   if(e.entityTags().contains("ArchmageBolt")&&tickCount%2==0)level.sendParticles(ParticleTypes.END_ROD,e.getX(),e.getY(),e.getZ(),1,.07,.07,.07,.01);
  }
  if(waveTicks>0&&waveCenter!=null){
   double radius=(21-waveTicks)*.65;waveTicks--;
   for(int i=0;i<48;i++){double a=i*Math.PI/24;level.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD166,1.15F),waveCenter.x+Math.cos(a)*radius,waveCenter.y+.2,waveCenter.z+Math.sin(a)*radius,1,0,.03,0,0);}
   for(var victim:level.getEntitiesOfClass(LivingEntity.class,new AABB(waveCenter,waveCenter).inflate(14),this::validTarget)){
    double distance=Math.sqrt(victim.position().multiply(1,0,1).distanceToSqr(waveCenter.multiply(1,0,1)));
    if(Math.abs(victim.getY()-waveCenter.y)>.9||Math.abs(distance-radius)>.8||waveVictims.contains(victim.getUUID()))continue;
    waveVictims.add(victim.getUUID());if(hit(level,victim,10))shockwaveHits++;
   }
  }
 }
 private void clearEffects(){for(var e:List.copyOf(effects.keySet()))e.discard();effects.clear();waveTicks=0;waveVictims.clear();}

 private void tickArrival(ServerLevel world){
  if(arrivalTicks()<=0||!isAlive())return;
  getNavigation().stop();setDeltaMovement(0,getDeltaMovement().y,0);
  // Start only once an observer has finished loading this dimension, so entry cannot hide the intro.
  boolean ready=world.players().stream().anyMatch(p->{
   if(!p.isAlive()||p.connection==null||!p.connection.hasClientLoaded()||!inArena(p))return false;
   var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(p);
   return p.distanceToSqr(this)<=64*64||attackers.contains(p.getUUID())||mahou!=null&&getUUID().equals(mahou.getEnemy());
  });
  if(!ready)return;
  if(!arrivalSoundPlayed){
   arrivalSoundPlayed=true;arrivalSounds++;
   world.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_ARRIVAL.get(),SoundSource.HOSTILE,2F,.85F);
   world.playSound(null,getX(),getY(),getZ(),SoundEvents.EVOKER_PREPARE_ATTACK,SoundSource.HOSTILE,1.4F,.6F);
  }
  int left=arrivalTicks();double progress=(ARRIVAL_TICKS-left)/(double)ARRIVAL_TICKS;
  if(left==50||left==36||left==20){
   var flash=new net.minecraft.world.entity.LightningBolt(EntityType.LIGHTNING_BOLT,world);
   flash.setPos(getX()+(left==36?-6:left==20?6:0),getY(),getZ()+(left==50?0:-4));
   flash.setVisualOnly(true);if(addEffect(world,flash,8))arrivalFlashes++;
  }
  if(left==32){arrivalLaughs++;world.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_DEFEAT_LAUGH.get(),SoundSource.HOSTILE,1.8F,.85F);}
  if(left%2==0){
   for(int i=0;i<18;i++){
    double angle=i*Math.PI/9+progress*Math.PI*2,radius=1.6+.4*Math.sin(progress*Math.PI);
    world.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD166,1.05F),getX()+Math.cos(angle)*radius,getY()+.08,getZ()+Math.sin(angle)*radius,1,0,.015,0,0);
   }
   for(int i=0;i<3;i++){double angle=progress*Math.PI*6+i*Math.PI*2/3;world.sendParticles(ParticleTypes.END_ROD,getX()+Math.cos(angle)*1.1,getY()+progress*3,getZ()+Math.sin(angle)*1.1,1,0,0,0,0);}
  }
  if(left==1)world.sendParticles(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.FLASH,0xFFFFD166),getX(),getY()+1.7,getZ(),1,0,0,0,0);
  entityData.set(ARRIVAL,left-1);
 }

 @Override
 public void tick() {
  if(!reconcileEncounter())return;
  if(isAlive()&&level() instanceof ServerLevel level)awakenOnApproach(level);
  super.tick();
  if (!this.level().isClientSide()) {
   tickStuckRecovery((ServerLevel)level());
   if(isAlive())rememberEncounter();
   if(isAlive())tickEffects((ServerLevel)level());else clearEffects();
   tickArrival((ServerLevel)level());
   if(recoveryTicks()>0)entityData.set(RECOVERY,recoveryTicks()-1);
   if(attackTicks()>0&&tickCount%4==0){
    var world=(ServerLevel)level();double a=tickCount*.24;
    for(int i=0;i<8;i++){double angle=a+i*Math.PI/4;world.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD166,1.1F),getX()+Math.cos(angle)*1.25,getY()+.15,getZ()+Math.sin(angle)*1.25,1,0,.06,0,0);}
    if(attackKind()==2&&castPoint!=null)for(int i=0;i<6;i++){double angle=i*Math.PI/3;world.sendParticles(ParticleTypes.CRIT,castPoint.x+Math.cos(angle)*3,castPoint.y+.1,castPoint.z+Math.sin(angle)*3,2,.3,.02,.3,0);}
   }
   bossEvent.setProgress(Math.clamp(this.getHealth() / this.getMaxHealth(), 0.0F, 1.0F));
   int telegraph = entityData.get(ATTACK_TICKS);
   if (isAlive() && telegraph > 0){entityData.set(ATTACK_TICKS, telegraph - 1);if(telegraph==1)releaseBurst((ServerLevel)this.level());}
   if(levitationTicks>0){levitationTicks--;setDeltaMovement(getDeltaMovement().x,.025,getDeltaMovement().z);}
   else if(isNoGravity())setNoGravity(false);
  }
 }

 /** No chunk loading here: used by Fabric ALLOW_LOAD before registration, even in non-ticking chunks. */
 public boolean reconcileEncounter(){
  if(isAlive()&&level() instanceof ServerLevel level&&!rememberEncounter()){
   var canonical=RealityMarbleEncounters.get(level).near(arena);
   if(canonical!=null)for(var player:List.copyOf(level.players())){
    var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
    if(mahou!=null&&getUUID().equals(mahou.getEnemy())){
     mahou.setEnemy(canonical.boss());stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(player,mahou);
    }
   }
   discard();return false;
  }
  return true;
 }

 @Override
 public void startSeenByPlayer(ServerPlayer player) {
  super.startSeenByPlayer(player);
  bossEvent.addPlayer(player);
 }

 @Override
 public void stopSeenByPlayer(ServerPlayer player) {
  super.stopSeenByPlayer(player);
  bossEvent.removePlayer(player);
 }

 @Override
 public void remove(RemovalReason reason) {
  clearEffects();
  if(reason==RemovalReason.UNLOADED_TO_CHUNK||reason==RemovalReason.UNLOADED_WITH_PLAYER)rememberEncounter();
  else forgetEncounter();
  super.remove(reason);
  bossEvent.removeAllPlayers();
  if (arena != null) {
   var perLevel = ACTIVE.get(this.level().dimension());
   if (perLevel != null) perLevel.remove(cell(arena), this);
  }
 }

 private static BlockPos cell(BlockPos pos) {
  return new BlockPos(pos.getX() >> 8, pos.getY() >> 8, pos.getZ() >> 8);
 }

 @Override
 public void die(DamageSource source) {
  clearEffects();
  forgetEncounter();
  if (!this.level().isClientSide() && this.level() instanceof ServerLevel level) {
   for (UUID id : attackers) {
    ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
    if (player == null) player = attackerRefs.get(id);
    if (player == null) {
     for (ServerPlayer candidate : level.players()) {
      if (candidate.getUUID().equals(id)) {
       player = candidate;
       break;
      }
     }
    }
    if (player == null || player.isRemoved()) { rewardMissing++; continue; }
    // Group rule: every player who damaged this boss receives the reward once, even if they
    // stepped out of the arena before the killing blow. A departed attacker is simply skipped.
    var known = new HashSet<>(player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE));
    if (!known.add(REWARD_KEY)) { rewardAlreadyGiven++; continue; }
    player.setAttached(GuideCatalog.KNOWLEDGE, List.copyOf(known));
    rewardGrants++;
    ItemStack morgan = new ItemStack(ModItems.morgan.get());
    if (!player.getInventory().add(morgan)) player.drop(morgan, false);
    player.sendSystemMessage(Component.translatable("mahoutsukai.marble.boss_reward"));
   }
  }
  super.die(source);
 }

 @Override
 protected void readAdditionalSaveData(ValueInput input) {
  super.readAdditionalSaveData(input);
  if(input.getIntOr("mahoutsukai_archmage_balance",0)<37){
   float ratio=getHealth()/getMaxHealth();
   if(getAttribute(Attributes.MAX_HEALTH).getBaseValue()==400){getAttribute(Attributes.MAX_HEALTH).setBaseValue(800);setHealth(ratio*getMaxHealth());}
   if(getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()==.26)getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.32);
   if(getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()==14)getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16);
  }
  recoverEffects=true;orphanRecoveryTicks=5;entityData.set(ATTACK_TICKS,0);entityData.set(RECOVERY,0);
  entityData.set(ARRIVAL,Math.clamp(input.getIntOr("mahoutsukai_arrival_ticks",0),0,ARRIVAL_TICKS));
  arrivalSoundPlayed=arrivalTicks()<ARRIVAL_TICKS;
  this.arena = input.read("mahoutsukai_arena", BlockPos.CODEC).orElse(null);
  this.attackers.clear();
  input.read("mahoutsukai_attackers", UUIDUtil.CODEC.listOf()).ifPresent(list -> attackers.addAll(list));
  this.specialCooldown = Math.clamp(input.getIntOr("mahoutsukai_special", SPECIAL_INTERVAL),1,SPECIAL_INTERVAL);
  this.pullCooldown = Math.max(1, input.getIntOr("mahoutsukai_pull", PULL_INTERVAL));
  awakened=input.getBooleanOr("mahoutsukai_archmage_awakened",getHealth()<getMaxHealth()||!attackers.isEmpty());
  shrineEntrance=input.read("mahoutsukai_archmage_entrance",BlockPos.CODEC).orElse(null);
  if(!awakened)entityData.set(ARRIVAL,0);
  if(isAlive())rememberEncounter();
 }

 @Override
 protected void addAdditionalSaveData(ValueOutput output) {
  super.addAdditionalSaveData(output);
  if (arena != null) output.store("mahoutsukai_arena", BlockPos.CODEC, arena);
  output.store("mahoutsukai_attackers", UUIDUtil.CODEC.listOf(), new ArrayList<>(attackers));
  output.putInt("mahoutsukai_special", specialCooldown);
  output.putInt("mahoutsukai_pull", pullCooldown);
  output.putInt("mahoutsukai_arrival_ticks",arrivalTicks());
  output.putInt("mahoutsukai_archmage_balance",37);
  output.putBoolean("mahoutsukai_archmage_awakened",awakened);
  if(shrineEntrance!=null)output.store("mahoutsukai_archmage_entrance",BlockPos.CODEC,shrineEntrance);
  if(isAlive())rememberEncounter();
 }

 private boolean rememberEncounter(){
  if(arena!=null&&isAlive()&&level() instanceof ServerLevel level&&RealityMarbleSpellEffect.inMarble(level))
   return RealityMarbleEncounters.get(level).remember(arena,getUUID(),chunkPosition());
  return true;
 }
 private void forgetEncounter(){
  if(arena!=null&&level() instanceof ServerLevel level&&RealityMarbleSpellEffect.inMarble(level))
   RealityMarbleEncounters.get(level).forget(arena,getUUID());
 }
 private static RealityMarbleBossEntity loadEncounter(ServerLevel level,RealityMarbleEncounters.Encounter encounter){
  if(encounter==null)return null;
  level.getChunk(encounter.chunk().x(),encounter.chunk().z());
  level.waitForEntities(encounter.chunk(),0);
  var entity=level.getEntity(encounter.boss());
  if(entity instanceof RealityMarbleBossEntity boss&&boss.isAlive()&&!boss.isRemoved()){
   ACTIVE.computeIfAbsent(level.dimension(),key->new java.util.concurrent.ConcurrentHashMap<>()).put(cell(boss.arena()==null?encounter.arena():boss.arena()),boss);
   boss.rememberEncounter();
   return boss;
  }
  // Only a fully loaded remembered chunk can establish that this saved opponent is gone.
  RealityMarbleEncounters.get(level).forget(encounter.arena(),encounter.boss());
  return null;
 }

 /**
  * Spawns one boss per arena. Re-entering the dimension never stacks copies: an existing boss
  * within the arena radius is reused.
  */
 public static RealityMarbleBossEntity spawnFor(ServerLevel level, BlockPos center) {
  if (!RealityMarbleSpellEffect.inMarble(level)) return null;
  var perLevel = ACTIVE.computeIfAbsent(level.dimension(), key -> new java.util.concurrent.ConcurrentHashMap<>());
  BlockPos cell = cell(center);
  RealityMarbleBossEntity active = perLevel.get(cell);
  if (active != null && active.level()==level && !active.isRemoved() && active.isAlive()) {
   if(active.rememberEncounter())return active;
   active.discard();
  }
  perLevel.remove(cell);
  var recovered=loadEncounter(level,RealityMarbleEncounters.get(level).near(center));
  if(recovered!=null){perLevel.put(cell,recovered);return recovered;}
  Vec3 spawn = RealityMarbleSpellEffect.safeMarbleSpawn(level, center, 1.5, 3);
  if (spawn == null) return null;
  // Blocks and saved entities load separately. Resolve the saved boss before making a new copy.
  level.waitForEntities(net.minecraft.world.level.ChunkPos.containing(BlockPos.containing(spawn)),0);
  recovered=loadEncounter(level,RealityMarbleEncounters.get(level).near(center));
  if(recovered!=null){perLevel.put(cell,recovered);return recovered;}
  var existing = level.getEntitiesOfClass(RealityMarbleBossEntity.class, new AABB(center).inflate(ARENA_RADIUS),b->b.isAlive()&&!b.isRemoved());
  for (var found : existing) {
   if (found.arena() != null && found.arena().distSqr(center) <= 64L * 64L) {
    if(!found.rememberEncounter()){found.discard();continue;}
    perLevel.put(cell, found);
    return found;
   }
  }
  var boss = new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(), level);
  boss.setArena(center);
  boss.snapTo(spawn.x, spawn.y, spawn.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
  boss.prepareAtShrine();
  // Shrine generation can load an older saved opponent from another chunk; keep that boss.
  recovered=loadEncounter(level,RealityMarbleEncounters.get(level).near(center));
  if(recovered!=null){perLevel.put(cell,recovered);return recovered;}
  boss.setPersistenceRequired();
  if (!level.addFreshEntity(boss)){boss.forgetEncounter();return null;}
  perLevel.put(cell, boss);
  boss.rememberEncounter();
  return boss;
 }

 /** Resolves freshly queued encounters too, before the level's entity index has ticked. */
 public static RealityMarbleBossEntity liveFor(ServerLevel level,UUID id){
  if(id==null)return null;
  var bosses=ACTIVE.get(level.dimension());
  if(bosses!=null)for(var boss:bosses.values())if(boss.level()==level&&boss.getUUID().equals(id)&&boss.isAlive()&&!boss.isRemoved())return boss;
  var entity=level.getEntity(id);
  if(entity instanceof RealityMarbleBossEntity boss&&boss.isAlive()&&!boss.isRemoved())return boss.rememberEncounter()?boss:null;
  return loadEncounter(level,RealityMarbleEncounters.get(level).byBoss(id));
 }

 /** Removes every live arena boss of this level; used by native checks and server cleanup. */
 public static void discardAll(ServerLevel level) {
  var perLevel = ACTIVE.remove(level.dimension());
  if (perLevel == null) return;
  for (RealityMarbleBossEntity boss : new java.util.ArrayList<>(perLevel.values())) {
   if (boss != null) boss.discard();
  }
 }

}
