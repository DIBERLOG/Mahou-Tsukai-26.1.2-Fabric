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
 public int fangsSpawned,pillarsSpawned,boltsSpawned,vexesSpawned,shockwaveHits;

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
  builder.define(ARRIVAL, ARRIVAL_TICKS);
  builder.define(RECOVERY, 0);
 }

 /** Remaining wind-up ticks of the current special, mirrored to observers. */
 public int attackTicks() {
  return entityData.get(ATTACK_TICKS);
 }
 public int attackKind(){return entityData.get(ATTACK_KIND);}
 public int arrivalTicks(){return entityData.get(ARRIVAL);}
 public int recoveryTicks(){return entityData.get(RECOVERY);}
 private boolean preparing(){return arrivalTicks()>0||recoveryTicks()>0||attackTicks()>0;}
 public boolean levitating(){return isNoGravity();}

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

  boolean anyNearby = false;
  for (ServerPlayer player : List.copyOf(level.players())) {
   if (player.level() == level && player.distanceToSqr(this) <= (double)ARENA_RADIUS * ARENA_RADIUS) {
    anyNearby = true;
    break;
   }
  }
  if (anyNearby) {
   idleTicks = 0;
  } else if (++idleTicks > IDLE_DESPAWN_TICKS) {
   this.discard();
   return;
  }

  if (this.getTarget() == null) {
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
  return target!=null&&target.isAlive()&&target.level()==level()&&inArena(target)&&distanceToSqr(target)<=64*64
   &&!target.isSpectator()&&!(target instanceof Player p&&p.isCreative())&&!isAlliedTo(target);
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
  boolean ready=world.players().stream().anyMatch(p->p.isAlive()&&p.distanceToSqr(this)<32*32&&p.connection!=null&&p.connection.hasClientLoaded());
  if(!ready)return;
  if(!arrivalSoundPlayed){arrivalSoundPlayed=true;world.playSound(null,getX(),getY(),getZ(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_ARRIVAL.get(),SoundSource.HOSTILE,2F,1F);}
  int left=arrivalTicks();double progress=(ARRIVAL_TICKS-left)/(double)ARRIVAL_TICKS;
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
  super.tick();
  if (!this.level().isClientSide()) {
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
  if (active != null && active.level()==level && !active.isRemoved() && active.isAlive()) return active;
  perLevel.remove(cell);
  Vec3 spawn = RealityMarbleSpellEffect.safeMarbleSpawn(level, center, 1.5, 3);
  if (spawn == null) return null;
  // Blocks and saved entities load separately. Resolve the saved boss before making a new copy.
  level.waitForEntities(net.minecraft.world.level.ChunkPos.containing(BlockPos.containing(spawn)),0);
  var existing = level.getEntitiesOfClass(RealityMarbleBossEntity.class, new AABB(center).inflate(ARENA_RADIUS),b->b.isAlive()&&!b.isRemoved());
  for (var found : existing) {
   if (found.arena() != null && found.arena().distSqr(center) <= 64L * 64L) {
    perLevel.put(cell, found);
    return found;
   }
  }
  var boss = new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(), level);
  boss.setArena(center);
  boss.snapTo(spawn.x, spawn.y, spawn.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
  boss.setPersistenceRequired();
  if (!level.addFreshEntity(boss)) return null;
  perLevel.put(cell, boss);
  return boss;
 }

 /** Resolves freshly queued encounters too, before the level's entity index has ticked. */
 public static RealityMarbleBossEntity liveFor(ServerLevel level,UUID id){
  if(id==null)return null;
  var bosses=ACTIVE.get(level.dimension());
  if(bosses!=null)for(var boss:bosses.values())if(boss.level()==level&&boss.getUUID().equals(id)&&boss.isAlive()&&!boss.isRemoved())return boss;
  var entity=level.getEntity(id);
  return entity instanceof RealityMarbleBossEntity boss&&boss.isAlive()&&!boss.isRemoved()?boss:null;
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
