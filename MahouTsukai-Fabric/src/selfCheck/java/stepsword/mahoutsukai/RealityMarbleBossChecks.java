package stepsword.mahoutsukai;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.fabric.GuideCatalog;
import stepsword.mahoutsukai.item.ModItems;

/**
 * Native checks for the Reality Marble boss: registration, one boss per arena, boss bar wiring,
 * arena bounds, real attacks, one-time Morgan reward, persistence and empty-arena cleanup.
 */
public final class RealityMarbleBossChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static final BlockPos CENTER=new BlockPos(100,120,100);

 public static void run(ServerPlayer fixture)throws Exception{
  var key=net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,RealityMarbleSpellEffect.marble);
  ServerLevel level=fixture.level().getServer().getLevel(key);
  require(level!=null,"Reality Marble dimension exists on the server");
  require(RealityMarbleSpellEffect.inMarble(level),"Marble dimension is recognised by the spell effect");
  require(RealityMarbleBossEntity.spawnFor(fixture.level(),CENTER)==null,"Boss never spawns outside the Reality Marble dimension");
  int chunkX=CENTER.getX()>>4,chunkZ=CENTER.getZ()>>4;
  level.setChunkForced(chunkX,chunkZ,true);
  level.getChunk(chunkX,chunkZ);
  for(int x=chunkX-1;x<=chunkX+1;x++)for(int z=chunkZ-1;z<=chunkZ+1;z++)level.getChunk(x,z);
  level.waitForEntities(net.minecraft.world.level.ChunkPos.containing(CENTER),1);
  RealityMarbleBossEntity.discardAll(level);
  for(var stale:level.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(CENTER).inflate(512)))stale.discard();
  ServerPlayer player=DoomIntegration.player(level,"MarbleBossFighter",GameType.SURVIVAL,CENTER.getX());
  ServerPlayer victim=DoomIntegration.player(level,"MarbleBossVictim",GameType.SURVIVAL,CENTER.getX());
  // Fixture players are constructed by the server helper with the overworld level; move them to the arena level.
  player.setServerLevel(level);
  victim.setServerLevel(level);
  require(player.level().dimension().equals(level.dimension()),"A: player level after pin "+player.level().dimension());
  try{
   for(int dx=-6;dx<=6;dx++)for(int dz=-6;dz<=6;dz++){
    level.setBlockAndUpdate(CENTER.offset(dx,0,dz),Blocks.STONE.defaultBlockState());
    for(int dy=1;dy<=5;dy++)level.setBlockAndUpdate(CENTER.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
   }
   player.teleportTo(CENTER.getX()+.5,CENTER.getY()+1,CENTER.getZ()+.5);
   victim.teleportTo(CENTER.getX()+5.5,CENTER.getY()+1,CENTER.getZ()+5.5);
   clientReady(player,false);clientReady(victim,false);
   for(var fighter:java.util.List.of(player,victim)){fighter.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(1000);fighter.setHealth(1000);}
   require(player.level().dimension().equals(level.dimension()),"B: player level after teleport "+player.level().dimension());

   var boss=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(boss!=null,"Boss spawns in the Reality Marble");
   // Match real scroll entry: damage in Marble returns players with no encounter opponent.
   stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).setEnemy(boss.getUUID());
   stepsword.mahoutsukai.util.Utils.getPlayerMahou(victim).setEnemy(boss.getUUID());
   require(boss.getMaxHealth()==800.0F&&boss.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)==16.0,"Boss health and attack attributes");
   require(boss.inArena(player),"Player at the arena centre is inside the boss arena");
   var far=new BlockPos(CENTER.getX()+500,CENTER.getY(),CENTER.getZ()+500);
   var farProbe=createProbe(level,far);
   require(!boss.inArena(farProbe),"Player far outside the arena is not a valid target");
   var repeat=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(repeat!=null&&repeat.getUUID().equals(boss.getUUID()),"Re-entering does not stack a second boss");

   boss.startSeenByPlayer(player);
   require(boss.bossBarPlayerCount()==1,"Boss bar tracks a nearby player");
   boss.stopSeenByPlayer(player);
   require(boss.bossBarPlayerCount()==0,"Boss bar stops tracking on leave");

   var saved=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());
   boss.saveWithoutId(saved);
   var reloaded=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
   reloaded.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),saved.buildResult()));
   require(reloaded.arena()!=null&&reloaded.arena().equals(boss.arena())&&reloaded.getMaxHealth()==800.0F,"Boss arena and health survive native save/load");

   // The first special is now the evoker fang attack; the warning must precede entity creation.
   victim.teleportTo(boss.getX()+2.5,boss.getY(),boss.getZ()+2.5);
   boss.setTarget(victim);
   require(boss.arrivalTicks()==50,"The arena boss starts with a synced fifty-tick arrival");
   for(int tick=0;tick<10;tick++)boss.tick();
   require(boss.arrivalTicks()==50&&boss.attackTicks()==0&&boss.sonicBursts==0,"Arrival waits for a nearby client's load acknowledgement and cannot cast early");
   clientReady(player,true);clientReady(victim,true);
   float playerHealth=player.getHealth(),victimHealth=victim.getHealth();
   for(int remaining=50;remaining>0;remaining--){
    require(boss.arrivalTicks()==remaining,"Arena entrance stays synced on every loaded-player tick");
    boss.tick();
    require(boss.attackTicks()==0&&boss.sonicBursts==0&&boss.fangsSpawned==0&&boss.sonicHits==0,"The full entrance precedes all special attacks");
    require(player.getHealth()==playerHealth&&victim.getHealth()==victimHealth,"The entrance also prevents an ordinary melee attack");
   }
   require(boss.arrivalTicks()==0,"Arrival ends after exactly fifty acknowledged ticks");
   boss.setTarget(victim);
   int telegraph=0;
   for(int tick=0;tick<175;tick++){
    boss.tick();
    if(telegraph==0&&boss.sonicBursts>0)telegraph=boss.attackTicks();
    if(boss.attackTicks()>0)require(boss.fangsSpawned==0&&boss.sonicHits==0,"Special never creates fangs or deals area damage before the first warning ends");
   }
   require(boss.sonicBursts==1&&boss.attackKind()==1&&boss.fangsSpawned>0,"Boss opens with native evoker fangs for the survival player inside the arena (bursts="+boss.sonicBursts+", fangs="+boss.fangsSpawned+", boss="+boss.position()+", victim="+victim.position()+", target="+boss.getTarget()+", arrival="+boss.arrivalTicks()+", alive="+boss.isAlive()+")");
   require(telegraph>=30,"Boss attack telegraph ticks are synced to observers (got "+telegraph+")");
   // The fixture player has no ticking client, so player HP is verified in game; the same damage
   // source is proven here on a real creature that lives in the level.
   var cow=net.minecraft.world.entity.EntityType.COW.create(level,net.minecraft.world.entity.EntitySpawnReason.COMMAND);
   require(cow!=null,"Cow fixture created");
   cow.setPos(boss.getX()+1.0,boss.getY(),boss.getZ());
   cow.setHealth(20.0F);
   require(level.addFreshEntity(cow),"Cow fixture inserted");
   float cowBefore=cow.getHealth();
   cow.hurtServer(level,level.damageSources().mobAttack(boss),10.0F);
   require(cow.getHealth()<cowBefore,"Boss damage source really hurts a living target ("+cowBefore+" -> "+cow.getHealth()+")");
   cow.discard();
   boss.setTarget(null);

   // The next AI step must reject the far player; it may immediately choose another arena target.
   boss.setTarget(farProbe);
   boss.tick();
   var nextTarget=boss.getTarget();
   String targetInfo=nextTarget==null?"none":nextTarget.getName().getString()+" at "+nextTarget.position()+" in "+nextTarget.level().dimension().identifier();
   require(nextTarget!=farProbe,"Boss drops the player outside its arena (actual target: "+targetInfo+")");
   require(nextTarget==null||(nextTarget.isAlive()&&nextTarget.level()==level&&boss.inArena(nextTarget)),"Any immediately selected replacement is alive and inside this arena level (actual target: "+targetInfo+")");
   level.removePlayerImmediately(farProbe,Entity.RemovalReason.DISCARDED);

   // Real victory grants Morgan exactly once per player, even after another fight.
   int before=morganCount(player);
   boss.hurtServer(level,level.damageSources().playerAttack(player),100000.0F);
   require(!boss.isAlive(),"Boss dies to a real player attack");
   require(morganCount(player)==before+1,"Winning the fight grants one Morgan, attackers="+boss.attackerCount()+" grants="+boss.rewardGrants+" missing="+boss.rewardMissing+" already="+boss.rewardAlreadyGiven+" morgan="+morganCount(player));
   require(player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).contains(RealityMarbleBossEntity.REWARD_KEY),"Reward is recorded in persistent discoveries");

   var second=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(second!=null&&!second.getUUID().equals(boss.getUUID()),"A later fight is a new boss, not a copy of the dead one");
   second.hurtServer(level,level.damageSources().playerAttack(player),100000.0F);
   require(morganCount(player)==before+1,"A second victory never duplicates the one-time Morgan reward");

   // Empty arena: after the idle grace period the boss removes itself.
   var third=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(third!=null,"Boss respawns for the cleanup check");
   player.teleportTo(CENTER.getX()+400.5,CENTER.getY()+1,CENTER.getZ()+400.5);
   victim.teleportTo(CENTER.getX()+401.5,CENTER.getY()+1,CENTER.getZ()+400.5);
   for(int tick=0;tick<RealityMarbleBossEntity.IDLE_DESPAWN_TICKS+20&&!third.isRemoved();tick++)third.tick();
   require(third.isRemoved(),"Boss cleans itself up when the arena stays empty");
   System.out.println("PASS: REALITY_MARBLE_BOSS: registration, one boss per arena, boss bar, arena bounds, client-acknowledged fifty-tick entrance without melee or specials, real attacks, one-time Morgan reward, persistence and empty-arena cleanup");
  }finally{
   RealityMarbleBossEntity.discardAll(level);
   for(var boss:level.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(CENTER).inflate(512)))boss.discard();
   player.getInventory().clearContent();
   player.setAttached(GuideCatalog.KNOWLEDGE,player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).stream().filter(k->!k.equals(RealityMarbleBossEntity.REWARD_KEY)).toList());
   for(int dx=-6;dx<=6;dx++)for(int dz=-6;dz<=6;dz++)for(int dy=0;dy<=5;dy++)level.setBlockAndUpdate(CENTER.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
   level.removePlayerImmediately(player,Entity.RemovalReason.DISCARDED);
   level.removePlayerImmediately(victim,Entity.RemovalReason.DISCARDED);
   level.setChunkForced(chunkX,chunkZ,false);
  }
 }

 private static int morganCount(ServerPlayer player){
  int count=0;
  for(int slot=0;slot<player.getInventory().getContainerSize();slot++)if(player.getInventory().getItem(slot).is(ModItems.morgan.get()))count+=player.getInventory().getItem(slot).getCount();
  return count;
 }

 private static void clientReady(ServerPlayer player,boolean ready){
  var level=player.level();
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false)){
   @Override public boolean hasClientLoaded(){return ready;}
  };
 }

 /** A real survival player used only to probe arena membership; it is removed by the caller. */
 private static ServerPlayer createProbe(ServerLevel level,BlockPos pos){
  var probe=DoomIntegration.player(level,"MarbleProbe",GameType.SURVIVAL,pos.getX());
  probe.teleportTo(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5);
  return probe;
 }
}
