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
import stepsword.mahoutsukai.dataattachments.worldsave.RealityMarbleEncounters;

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
   verifyDormancyAndWake(level,boss,player,victim);

   var saved=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());
   boss.saveWithoutId(saved);
   var reloaded=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
   reloaded.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),saved.buildResult()));
   require(reloaded.arena()!=null&&reloaded.arena().equals(boss.arena())&&reloaded.getMaxHealth()==800.0F,"Boss arena and health survive native save/load");
   boss=verifyEncounterPersistence(level,boss,player);

   // The first special is now the evoker fang attack; the warning must precede entity creation.
   victim.teleportTo(boss.getX()+2.5,boss.getY(),boss.getZ()+2.5);
   boss.setTarget(victim);
   require(boss.arrivalTicks()==50,"The arena boss starts with a synced fifty-tick arrival");
   for(int tick=0;tick<10;tick++)boss.tick();
   require(boss.arrivalTicks()==50&&boss.attackTicks()==0&&boss.sonicBursts==0,"Arrival waits for a nearby client's load acknowledgement and cannot cast early");
   clientReady(player,true);clientReady(victim,true);
   float playerHealth=player.getHealth(),victimHealth=victim.getHealth();
   var visualOnly=net.minecraft.world.entity.LightningBolt.class.getDeclaredField("visualOnly");visualOnly.setAccessible(true);
   String arrivalOwner="ArchmageOwner:"+boss.getUUID();
   for(int remaining=50;remaining>0;remaining--){
    require(boss.arrivalTicks()==remaining,"Arena entrance stays synced on every loaded-player tick");
    boss.tick();
    require(boss.attackTicks()==0&&boss.sonicBursts==0&&boss.fangsSpawned==0&&boss.sonicHits==0,"The full entrance precedes all special attacks");
    require(player.getHealth()==playerHealth&&victim.getHealth()==victimHealth,"The entrance also prevents an ordinary melee attack");
    for(var flash:level.getEntitiesOfClass(net.minecraft.world.entity.LightningBolt.class,boss.getBoundingBox().inflate(12),e->e.entityTags().contains(arrivalOwner))){
     require(visualOnly.getBoolean(flash),"Every introductory lightning bolt is native visual-only lightning");
     flash.tick();require(flash.getBlocksSetOnFire()==0&&flash.getHitEntities().findAny().isEmpty(),"Intro lightning cannot ignite blocks or hurt nearby fighters");
    }
   }
   require(boss.arrivalTicks()==0,"Arrival ends after exactly fifty acknowledged ticks");
   require(boss.arrivalFlashes==3&&boss.arrivalSounds==1&&boss.arrivalLaughs==1,"Three harmless lightning flashes and one evil arrival/laugh precede every first attack");
   boss.awaken();boss.tick();
   require(boss.arrivalTicks()==0&&boss.arrivalFlashes==3&&boss.arrivalSounds==1&&boss.arrivalLaughs==1,"Reawakening cannot replay the intro or delay the fight");
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
   verifyContinuousFight(level,boss,player,victim);

   // Real victory grants Morgan exactly once per player, even after another fight.
   int before=morganCount(player);
   boss.hurtServer(level,level.damageSources().playerAttack(player),100000.0F);
   require(!boss.isAlive(),"Boss dies to a real player attack");
   require(RealityMarbleEncounters.get(level).near(CENTER)==null,"Actual boss death clears its persisted encounter pointer");
   require(morganCount(player)==before+1,"Winning the fight grants one Morgan, attackers="+boss.attackerCount()+" grants="+boss.rewardGrants+" missing="+boss.rewardMissing+" already="+boss.rewardAlreadyGiven+" morgan="+morganCount(player));
   require(player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).contains(RealityMarbleBossEntity.REWARD_KEY),"Reward is recorded in persistent discoveries");

   var second=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(second!=null&&!second.getUUID().equals(boss.getUUID()),"A later fight is a new boss, not a copy of the dead one");
   boss.remove(Entity.RemovalReason.DISCARDED);
   require(RealityMarbleEncounters.get(level).near(CENTER).boss().equals(second.getUUID()),"A removed old instance cannot erase the replacement boss's persisted pointer");
   second.hurtServer(level,level.damageSources().playerAttack(player),100000.0F);
   require(morganCount(player)==before+1,"A second victory never duplicates the one-time Morgan reward");

   // Empty arena: after the idle grace period the boss removes itself.
   var third=RealityMarbleBossEntity.spawnFor(level,CENTER);
   require(third!=null,"Boss respawns for the cleanup check");
   player.teleportTo(CENTER.getX()+400.5,CENTER.getY()+1,CENTER.getZ()+400.5);
   victim.teleportTo(CENTER.getX()+401.5,CENTER.getY()+1,CENTER.getZ()+400.5);
   for(int tick=0;tick<RealityMarbleBossEntity.IDLE_DESPAWN_TICKS+20&&!third.isRemoved();tick++)third.tick();
   require(third.isRemoved(),"Boss cleans itself up when the arena stays empty");
   require(RealityMarbleEncounters.get(level).near(CENTER)==null,"Actual discard also clears the persisted encounter pointer");
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

 private static void verifyDormancyAndWake(ServerLevel level,RealityMarbleBossEntity boss,ServerPlayer player,ServerPlayer victim){
  var playerPos=player.position();var victimPos=victim.position();
  player.teleportTo(CENTER.getX()-40,boss.getY(),CENTER.getZ()-40);victim.teleportTo(CENTER.getX()-44,boss.getY(),CENTER.getZ()-40);
  var position=boss.position();float health=player.getHealth(),victimHealth=victim.getHealth();
  require(!boss.awakened()&&boss.arrivalTicks()==0,"A fresh Archmage waits in his idle stance, without starting his arrival");
  boss.setTarget(victim);require(boss.getTarget()==null,"A dormant Archmage cannot acquire a target");
  for(int tick=0;tick<RealityMarbleBossEntity.SPECIAL_INTERVAL+RealityMarbleBossEntity.CAST_TICKS+40;tick++)boss.tick();
  require(!boss.awakened()&&boss.getTarget()==null&&boss.position().distanceToSqr(position)<.01&&boss.sonicBursts==0&&player.getHealth()==health&&victim.getHealth()==victimHealth,"Waiting away from the entrance preserves the boss stance and never attacks a fighter");
  var dormant=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());boss.saveWithoutId(dormant);
  var restored=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
  restored.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),dormant.buildResult()));
  require(!restored.awakened()&&restored.arrivalTicks()==0,"A dormant save reloads dormant instead of replaying an entrance");
  var ranged=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);ranged.setPos(boss.getX()+30,boss.getY(),boss.getZ());
  ranged.setInvulnerable(true);
  require(!ranged.hurtServer(level,level.damageSources().playerAttack(player),1)&&!ranged.awakened(),"Rejected incoming damage cannot wake the boss");
  ranged.setInvulnerable(false);
  require(ranged.hurtServer(level,level.damageSources().playerAttack(player),1)&&ranged.awakened()&&ranged.arrivalTicks()==50,"An accepted distant attack wakes the boss and starts his one-time intro");ranged.discard();
  var entrance=stepsword.mahoutsukai.effects.projection.RealityMarbleShrine.entrance(level,boss.arena());
  var approach=entrance==null?boss.position():net.minecraft.world.phys.Vec3.atBottomCenterOf(entrance);
  player.teleportTo(approach.x,approach.y,approach.z);
  boss.tick();
  require(boss.awakened()&&boss.arrivalTicks()==50,"Approaching the temple entrance wakes the Archmage, while client loading still holds the intro");
  require(!boss.prepareAtShrine(),"An awakened existing fight cannot be moved back into its shrine");
  var awake=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());boss.saveWithoutId(awake);
  restored.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),awake.buildResult()));
  require(restored.awakened()&&restored.arrivalTicks()==50,"Awakening and its remaining intro survive a native save reload");
  player.teleportTo(playerPos.x,playerPos.y,playerPos.z);victim.teleportTo(victimPos.x,victimPos.y,victimPos.z);
 }

 private static void verifyContinuousFight(ServerLevel level,RealityMarbleBossEntity boss,ServerPlayer player,ServerPlayer victim)throws Exception{
  var playerPos=player.position();var victimPos=victim.position();float health=boss.getHealth();
  player.teleportTo(CENTER.getX()+150,boss.getY(),CENTER.getZ());victim.teleportTo(CENTER.getX()+151,boss.getY(),CENTER.getZ());
  boss.setHealth(700);
  var ai=RealityMarbleBossEntity.class.getDeclaredMethod("customServerAiStep",ServerLevel.class);ai.setAccessible(true);
  for(int tick=0;tick<RealityMarbleBossEntity.IDLE_DESPAWN_TICKS+20;tick++){boss.setTarget(null);ai.invoke(boss,level);}
  require(boss.isAlive()&&!boss.isRemoved()&&boss.getHealth()==700&&boss.getTarget()!=null,"A living linked fighter keeps the awakened fight active and pursuing without automatic healing or idle removal");
  boss.setHealth(health);player.teleportTo(playerPos.x,playerPos.y,playerPos.z);victim.teleportTo(victimPos.x,victimPos.y,victimPos.z);
 }

 private static RealityMarbleBossEntity verifyEncounterPersistence(ServerLevel level,RealityMarbleBossEntity boss,ServerPlayer participant){
  var position=boss.position();var data=RealityMarbleEncounters.get(level);
  require(data.near(CENTER)!=null&&data.near(CENTER).boss().equals(boss.getUUID()),"A natural spawn records its exact arena and boss UUID");
  // Put the saved entity outside the old nearby scan. Recovery must follow its persisted chunk.
  boss.setPos(CENTER.getX()+400.5,position.y,CENTER.getZ()+100.5);boss.tick();
  var remote=boss.chunkPosition();
  require(data.near(CENTER).chunk().equals(remote),"The pointer follows a live boss crossing chunk boundaries");
  var encoded=RealityMarbleEncounters.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,data).getOrThrow();
  var decoded=RealityMarbleEncounters.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,encoded).getOrThrow();
  require(decoded.near(CENTER).equals(data.near(CENTER))&&decoded.byBoss(boss.getUUID()).chunk().equals(remote),"Native SavedData codec round-trips the arena, UUID and remote chunk");
  var saved=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());
  boss.saveWithoutId(saved);
  boss.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
  require(data.near(CENTER)!=null&&data.near(CENTER).chunk().equals(remote),"A chunk unload retains the saved opponent pointer");
  // Native entity deserialization/reinsertion models the entity manager restoring that chunk.
  level.getChunk(remote.x(),remote.z());level.waitForEntities(remote,0);
  var restored=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
  restored.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),saved.buildResult()));
  require(level.addFreshEntity(restored),"The native saved opponent is restored after unload");
  var recovered=RealityMarbleBossEntity.spawnFor(level,CENTER);
  require(recovered==restored&&recovered.getUUID().equals(boss.getUUID()),"Re-entry resolves the saved UUID in its remote chunk instead of creating a second Archmage");
  require(recovered.arrivalTicks()==50&&recovered.getHealth()==boss.getHealth(),"Recovery preserves the existing boss state");
  var duplicateTag=saved.buildResult().copy();
  duplicateTag.put("UUID",net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,java.util.UUID.randomUUID()).getOrThrow());
  duplicateTag.put("mahoutsukai_arena",net.minecraft.core.BlockPos.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,CENTER.offset(1,0,1)).getOrThrow());
  var duplicate=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
  duplicate.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),duplicateTag));
  require(data.near(CENTER).boss().equals(recovered.getUUID()),"A late old-world duplicate cannot steal the canonical UUID during native NBT loading");
  var duplicateSave=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());
  duplicate.saveWithoutId(duplicateSave);
  require(data.near(CENTER).boss().equals(recovered.getUUID()),"Saving the late duplicate also leaves the canonical pointer unchanged");
  var participantMahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(participant);
  participantMahou.setEnemy(duplicate.getUUID());
  float canonicalHealth=recovered.getHealth();
  require(!level.addFreshEntity(duplicate),"Fabric's native load boundary rejects the duplicate before registration, without waiting for an entity tick");
  require(duplicate.isRemoved()&&recovered.isAlive()&&!recovered.isRemoved()&&recovered.getHealth()==canonicalHealth,"Load reconciliation removes only the later duplicate without damaging the canonical Archmage");
  require(data.near(CENTER).boss().equals(recovered.getUUID())&&level.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(CENTER).inflate(512),Entity::isAlive).size()==1,"Old-world reconciliation leaves one living boss and preserves its canonical saved UUID");
  require(recovered.getUUID().equals(participantMahou.getEnemy()),"A participant formerly linked to the old duplicate is redirected to the canonical encounter");
  recovered.setPos(position);recovered.tick();
  require(data.near(CENTER).chunk().equals(recovered.chunkPosition()),"Returning movement updates the persisted chunk again");
  return recovered;
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
