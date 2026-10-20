package stepsword.mahoutsukai;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.fireball.FireballEntity;

/** Native combat checks, called only after the isolated overworld chunks have begun ticking. */
public final class ArchmageCombat034Checks {
 private static final BlockPos CENTER=new BlockPos(576,250,552);
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static Method begin;
 private static Method release;
 private static Field effects;

 @SuppressWarnings("unchecked")
 private static List<Entity> effects(RealityMarbleBossEntity boss)throws Exception{
  return new ArrayList<>(((Map<Entity,Integer>)effects.get(boss)).keySet());
 }
 private static <T extends Entity>List<T> effects(RealityMarbleBossEntity boss,Class<T> kind)throws Exception{
  return effects(boss).stream().filter(e->kind.isInstance(e)&&!e.isRemoved()).map(kind::cast).toList();
 }
 private static RealityMarbleBossEntity boss(ServerLevel level){
  var boss=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
  boss.awaken();
  boss.setArena(CENTER);boss.setNoAi(true);boss.setPos(CENTER.getX()+.5,CENTER.getY(),CENTER.getZ()+.5);
  require(level.addFreshEntity(boss),"Combat boss inserted into the native level");return boss;
 }
 private static ServerPlayer player(ServerLevel level,String name,GameType mode,Vec3 pos){
  var player=DoomIntegration.player(level,name,mode,pos.x);player.setServerLevel(level);player.teleportTo(pos.x,pos.y,pos.z);player.setNoGravity(true);
  // A real client sends the load acknowledgement. This isolated fixture has no client transport.
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false)){
   @Override public boolean hasClientLoaded(){return true;}
  };
  player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);player.setHealth(1000);
  return player;
 }
 private static void tick(RealityMarbleBossEntity boss,int ticks,boolean children)throws Exception{
  for(int i=0;i<ticks;i++){
   if(children)for(var entity:effects(boss))if(!entity.isRemoved())entity.tick();
   boss.tick();
  }
 }
 private static void cast(RealityMarbleBossEntity boss,ServerPlayer target,int kind)throws Exception{
  boss.setTarget(target);
  var before=effects(boss);
  int spawned=boss.fangsSpawned+boss.pillarsSpawned+boss.boltsSpawned+boss.vexesSpawned;
  int hits=boss.sonicHits+boss.shockwaveHits;
  begin.invoke(boss,boss.level());
  require(boss.attackKind()==kind&&boss.attackTicks()==RealityMarbleBossEntity.CAST_TICKS,"Special "+kind+" starts the synced 32-tick warning");
  for(int i=0;i<RealityMarbleBossEntity.CAST_TICKS-1;i++){
   boss.tick();
   require(boss.fangsSpawned+boss.pillarsSpawned+boss.boltsSpawned+boss.vexesSpawned==spawned&&boss.sonicHits+boss.shockwaveHits==hits,"Special "+kind+" cannot create its attack or damage before the warning ends");
  }
  require(boss.attackTicks()==1,"Special "+kind+" keeps its final warning tick");
  boss.tick();
  require(boss.attackTicks()==0,"Special "+kind+" releases exactly after its 32nd tick");
  require(boss.recoveryTicks()==12,"Special "+kind+" begins a synced twelve-tick recovery pose");
  int releasedCycle=boss.sonicBursts;
  begin.invoke(boss,boss.level());
  require(boss.attackTicks()==0&&boss.sonicBursts==releasedCycle,"Special "+kind+" cannot restart during its recovery pose");
  for(var entity:effects(boss))if(!before.contains(entity)){
   require(!entity.isRemoved()&&entity.level()==boss.level(),"Created special is a live native entity in the boss level");
   require(entity.entityTags().contains("ArchmageOwner:"+boss.getUUID()),"Every transient carries its exact boss ownership tag");
  }
 }

 private static void finishRecovery(RealityMarbleBossEntity boss)throws Exception{
  int cycle=boss.sonicBursts,kind=boss.attackKind();
  require(boss.recoveryTicks()==12,"Recovery starts with exactly twelve remaining ticks");
  for(int remaining=12;remaining>0;remaining--){
   require(boss.recoveryTicks()==remaining,"Recovery countdown remains synced on each server tick");
   begin.invoke(boss,boss.level());
   require(boss.attackTicks()==0&&boss.sonicBursts==cycle&&boss.attackKind()==kind,"Every recovery tick rejects restarting or advancing the special cycle");
   boss.tick();
  }
  require(boss.recoveryTicks()==0,"Recovery expires exactly after twelve server ticks");
 }

 public static void run(ServerPlayer fixture)throws Exception{
  ServerLevel level=fixture.level();
  require(level.getDifficulty()!=net.minecraft.world.Difficulty.PEACEFUL,"Combat fixture requires a non-peaceful native difficulty");
  require(RealityMarbleBossEntity.CAST_TICKS==32&&RealityMarbleBossEntity.SPECIAL_INTERVAL==80,"Boss keeps the documented 1.6-second warning and four-second cadence");
  begin=RealityMarbleBossEntity.class.getDeclaredMethod("sonicBurst",ServerLevel.class);begin.setAccessible(true);
  release=RealityMarbleBossEntity.class.getDeclaredMethod("releaseBurst",ServerLevel.class);release.setAccessible(true);
  effects=RealityMarbleBossEntity.class.getDeclaredField("effects");effects.setAccessible(true);
  var savedBlocks=new LinkedHashMap<BlockPos,BlockState>();
  var players=new ArrayList<ServerPlayer>();var creatures=new ArrayList<Entity>();var bosses=new ArrayList<RealityMarbleBossEntity>();
  for(var mutable:BlockPos.betweenClosed(CENTER.offset(-7,-1,-8),CENTER.offset(14,7,10))){
   var pos=mutable.immutable();savedBlocks.put(pos,level.getBlockState(pos));
   level.setBlock(pos,pos.getY()==CENTER.getY()-1?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState(),18);
  }
  try{
   var target=player(level,"ArchmageTarget034",GameType.SURVIVAL,new Vec3(584.5,250,552.5));players.add(target);
   var creative=player(level,"ArchmageCreative034",GameType.CREATIVE,new Vec3(580.5,250,555.5));players.add(creative);
   var spectator=player(level,"ArchmageSpectator034",GameType.SPECTATOR,new Vec3(581.5,250,555.5));players.add(spectator);
   var far=player(level,"ArchmageFar034",GameType.SURVIVAL,new Vec3(607.5,250,552.5));players.add(far);
   var veto=player(level,"ArchmageVeto034",GameType.SURVIVAL,new Vec3(582.5,250,549.5));veto.setInvulnerable(true);players.add(veto);
   var boss=boss(level);bosses.add(boss);

   boss.setTarget(target);
   require(boss.arrivalTicks()==50,"An awakened archmage has a synced fifty-tick entrance");
   begin.invoke(boss,level);
   require(boss.attackTicks()==0&&boss.sonicBursts==0,"A valid target cannot interrupt the entrance with a special");
   for(int remaining=50;remaining>0;remaining--){
    require(boss.arrivalTicks()==remaining,"Entrance countdown advances only once per ready server tick");
    boss.tick();
    require(boss.attackTicks()==0&&boss.sonicBursts==0&&effects(boss).stream().allMatch(e->e instanceof net.minecraft.world.entity.LightningBolt),"Every entrance tick permits only the verified visual lightning, without attack entities or special casts");
   }
   require(boss.arrivalTicks()==0,"Archmage finishes the entrance exactly after fifty ready ticks");

   // Invalid targets must not spend the special cycle or start a misleading animation.
   for(var invalid:java.util.Arrays.asList(null,creative,spectator,far)){
    if(invalid==far)far.teleportTo(CENTER.getX()+RealityMarbleBossEntity.ARENA_RADIUS+20,250,CENTER.getZ());
    boss.setTarget(invalid);begin.invoke(boss,level);
    require(boss.attackTicks()==0&&boss.attackKind()==0&&boss.sonicBursts==0&&effects(boss).isEmpty(),"Empty, creative, spectator and out-of-arena targets never start a special");
   }
   far.teleportTo(607.5,250,552.5);

   cast(boss,target,1);
   var fangs=effects(boss,EvokerFangs.class);
   require(!fangs.isEmpty()&&fangs.size()<=16&&boss.fangsSpawned==fangs.size(),"Evoker attack creates a bounded set of real fang entities");
   for(var fang:fangs)require(fang.getOwner()==boss,"Native evoker fangs credit the archmage owner");
   finishRecovery(boss);
   var cow=EntityType.COW.create(level,EntitySpawnReason.COMMAND);require(cow!=null,"Fang target fixture creates");
   cow.setPos(fangs.getFirst().position().add(0,.05,0));cow.setNoAi(true);cow.setNoGravity(true);level.addFreshEntity(cow);creatures.add(cow);
   float cowBefore=cow.getHealth();tick(boss,50,true);
   require(cow.getHealth()<cowBefore,"Native fang warmup and collision hurt a living target");cow.discard();
   tick(boss,70,true);require(effects(boss).isEmpty(),"Fangs leave no persistent boss effects after their lifetime");

   target.setPos(584.5,250,552.5);target.setDeltaMovement(Vec3.ZERO);target.invulnerableTime=0;
   cast(boss,target,2);
   var pillars=effects(boss,Display.BlockDisplay.class);
   require(pillars.size()==6&&boss.pillarsSpawned==6,"Pillar attack creates six actual block displays");
   for(var pillar:pillars)require(pillar.entityTags().contains("ArchmagePillar")&&!pillar.getBlockState().isAir(),"Pillars contain real stone block geometry and their effect tag");
   var terrain=new LinkedHashMap<BlockPos,BlockState>();for(var pos:savedBlocks.keySet())terrain.put(pos,level.getBlockState(pos));
   tick(boss,75,true);require(pillars.stream().allMatch(Entity::isRemoved),"All temporary pillars expire after the rise and 60-tick lifetime");
   for(var entry:terrain.entrySet())require(level.getBlockState(entry.getKey()).equals(entry.getValue()),"Pillar appearance and cleanup preserve every arena block");
   require(creative.getHealth()==1000&&spectator.getHealth()==1000&&far.getHealth()==1000&&veto.getHealth()==1000,"Pillars respect creative, spectator, range and native damage veto");

   target.setPos(584.5,250,552.5);target.setDeltaMovement(Vec3.ZERO);target.invulnerableTime=0;
   cast(boss,target,3);
   var bolts=effects(boss,FireballEntity.class);
   require(bolts.size()==3&&boss.boltsSpawned==3,"Golden volley launches exactly three native terrain-safe projectiles");
   for(var bolt:bolts)require(bolt.getOwner()==boss&&bolt.entityTags().contains("ArchmageBolt")&&bolt.getDeltaMovement().lengthSqr()>0,"Bolts have native ownership, visual tag and real travel velocity");
   // Use a real bolt and cow collision, independent of the fixture player's network state.
   var hitCow=EntityType.COW.create(level,EntitySpawnReason.COMMAND);require(hitCow!=null,"Bolt target fixture creates");
   hitCow.setNoAi(true);hitCow.setNoGravity(true);hitCow.setPos(580.5,250.5,558.5);level.addFreshEntity(hitCow);creatures.add(hitCow);
   var bolt=bolts.getFirst();bolt.setPos(580.5,251.2,555.5);bolt.setDeltaMovement(0,0,FireballEntity.SPEED);
   for(int i=0;i<8&&!bolt.isRemoved();i++)bolt.tick();
   require(bolt.isRemoved()&&hitCow.getHealth()<hitCow.getMaxHealth(),"Archmage bolt collides with and damages a native creature");hitCow.discard();
   tick(boss,85,true);require(bolts.stream().allMatch(Entity::isRemoved),"All golden bolts are bounded by their native 80-tick lifetime");
   for(var entry:terrain.entrySet())require(level.getBlockState(entry.getKey()).equals(entry.getValue()),"Golden volley never destroys blocks or places fire");

   // Create the jump fixture after projectile checks so earlier attacks cannot contaminate its HP.
   var jumper=player(level,"ArchmageJump034",GameType.SURVIVAL,new Vec3(580.5,251.3,552.5));players.add(jumper);
   // The shockwave is delayed after charge, expands over time and never hurts one player twice.
   target.setPos(584.5,250,552.5);target.setDeltaMovement(Vec3.ZERO);target.invulnerableTime=0;target.setHealth(1000);
   veto.setDeltaMovement(Vec3.ZERO);veto.invulnerableTime=0;
   int hits=boss.shockwaveHits;float targetBefore=target.getHealth();
   cast(boss,target,4);
   require(target.getHealth()==targetBefore&&boss.shockwaveHits==hits,"Shockwave does not hit a distant target on the release tick");
   for(int i=0;i<24;i++){
    // Removing invulnerability between frames makes the one-hit rule observable.
    target.invulnerableTime=0;boss.tick();
   }
   require(target.getHealth()<targetBefore&&boss.shockwaveHits==hits+1,"Expanding shockwave reaches the target and damages it exactly once");
   require(creative.getHealth()==1000&&spectator.getHealth()==1000&&far.getHealth()==1000,"Shockwave excludes creative, spectator and players outside its radius");
   require(jumper.getHealth()==1000,"Jumping above the ground wave avoids its damage");
   require(veto.getHealth()==1000&&veto.getDeltaMovement().lengthSqr()==0,"Rejected native damage also rejects shockwave knockback");

   target.setPos(584.5,250,552.5);target.setDeltaMovement(Vec3.ZERO);target.invulnerableTime=0;
   cast(boss,target,5);
   var vexes=effects(boss,Vex.class);require(vexes.size()==3&&boss.vexesSpawned==3,"Summon creates exactly three native vexes");
   for(var vex:vexes)require(vex.getOwner()==boss&&vex.getBoundOrigin()!=null&&vex.getTarget()==target,"Vexes retain their native boss owner, arena origin and intended victim");
   release.invoke(boss,level);require(effects(boss,Vex.class).size()==3&&boss.vexesSpawned==3,"Repeating a summon cannot stack more than three owned vexes");
   tick(boss,201,false);require(vexes.stream().allMatch(Entity::isRemoved),"Boss enforces a strict 200-tick vex lifetime even without vanilla death animation");

   require(boss.getMainHandItem().is(stepsword.mahoutsukai.item.ModItems.emrysStaff.get()),"Archmage really equips the player's Emrys ItemStack");
   cast(boss,target,6);
   var lightning=effects(boss,stepsword.mahoutsukai.entity.mahoujin.LightningEntity.class);
   require(!lightning.isEmpty()&&lightning.size()<=3&&boss.emrysCasts==lightning.size(),"Sixth special creates bounded native Emrys bolts");
   float healthBeforeEmrys=target.getHealth();
   for(var beam:lightning){beam.tickCount=9;beam.tick();}
   require(target.getHealth()<healthBeforeEmrys,"Real Emrys lightning damages the intended survival target");
   require(creative.getHealth()==1000&&spectator.getHealth()==1000&&far.getHealth()==1000,"Emrys excludes creative, spectator and distant targets");
   tick(boss,20,false);require(lightning.stream().allMatch(Entity::isRemoved),"Emrys effects expire and cannot outlive the encounter");

   // Wrap the cycle and leave live effects behind before removal: every owned entity must go.
   target.setPos(584.5,250,552.5);cast(boss,target,1);var remaining=effects(boss);finishRecovery(boss);begin.invoke(boss,level);
   require(boss.attackTicks()==32,"Save fixture includes an unfinished second special");
   var save=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());boss.saveWithoutId(save);
   require(!remaining.isEmpty(),"Cleanup fixture has live owned effects");boss.discard();
   require(remaining.stream().allMatch(Entity::isRemoved)&&effects(boss).isEmpty(),"Removing the archmage immediately cleans all his transient entities");
   var restored=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);bosses.add(restored);
   restored.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),save.buildResult()));
   require(restored.getUUID().equals(boss.getUUID())&&restored.attackTicks()==0,"Native reload preserves owner identity and cancels an incomplete cast");
   var orphan=new Display.BlockDisplay(EntityType.BLOCK_DISPLAY,level);orphan.setPos(580,250,552);orphan.setBlockState(Blocks.DEEPSLATE_BRICKS.defaultBlockState());orphan.addTag("ArchmageOwner:"+restored.getUUID());orphan.addTag("ArchmagePillar");require(level.addFreshEntity(orphan),"Saved orphan effect fixture inserted");creatures.add(orphan);
   var foreign=new Display.BlockDisplay(EntityType.BLOCK_DISPLAY,level);foreign.setPos(581,250,552);foreign.setBlockState(Blocks.DEEPSLATE_BRICKS.defaultBlockState());foreign.addTag("ArchmageOwner:"+java.util.UUID.randomUUID());foreign.addTag("ArchmagePillar");require(level.addFreshEntity(foreign),"Foreign owner effect fixture inserted");creatures.add(foreign);
   tick(restored,6,false);
   require(orphan.isRemoved()&&!foreign.isRemoved(),"Reload reclaims saved transient entities only for the exact archmage owner");
   System.out.println("PASS: ARCHMAGE_COMBAT_043: fifty-tick entrance, 32-tick warnings, twelve-tick recovery, six-special cycle including real equipped Emrys lightning and damage, owned fangs, terrain-safe pillars, native firebolts, shockwave, capped vexes, strict lifetimes and owner cleanup");
  }finally{
   for(var boss:bosses)boss.discard();for(var creature:creatures)creature.discard();
   for(var player:players)level.removePlayerImmediately(player,Entity.RemovalReason.DISCARDED);
   for(var entry:savedBlocks.entrySet())level.setBlock(entry.getKey(),entry.getValue(),18);
  }
 }
}
