package stepsword.mahoutsukai;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.util.Utils;

/** Four native server-tick stages: queued party entry, shared encounter and independent exits. */
public final class MultiplayerMarble036Checks {
 private static ServerLevel origin,marble;
 private static ServerPlayer first,second,duelist;
 private static RealityMarbleBossEntity boss,duelBoss;
 private static net.minecraft.world.level.ChunkPos arenaChunk;
 private static boolean forcedArena;
 private static UUID duelTarget;
 private static BlockPos firstReturn,secondReturn,duelReturn;
 private static final BlockPos FIRST_ALLOCATION=new BlockPos(336,120,336);
 private static final BlockPos SECOND_ALLOCATION=new BlockPos(1008,120,1008);
 private static final BlockPos DUEL_ALLOCATION=new BlockPos(1360,120,1360);
 private static void require(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 private static void register(ServerPlayer player){
  var list=player.level().getServer().getPlayerList();
  list.getPlayers().add(player);
  ((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)list).mahoutsukai$playersByUUID().put(player.getUUID(),player);
 }
 private static ServerPlayer player(String name,double x,double z,BlockPos allocation){
  var player=DoomIntegration.player(origin,name,GameType.SURVIVAL,x);register(player);
  RemainingFamiliesIntegration.ready(player,x,z);player.setXRot(90);
  Utils.getPlayerMahou(player).setEnemy(null);
  Utils.getPlayerMahou(player).setPlayerMarbleSpawn(allocation);
  Utils.getWorldMahou(origin).changePlayerSpawn(player.getUUID(),allocation);
  return player;
 }
 private static void allocations(){
  var save=Utils.getWorldMahou(origin);
  require(FIRST_ALLOCATION.equals(save.getPlayerSpawn(first.getUUID()))&&SECOND_ALLOCATION.equals(save.getPlayerSpawn(second.getUUID())),"Joining the shared fight never overwrites either player's personal allocated arena");
 }
 private static void savedOrigins(){
  require(firstReturn.equals(Utils.getPlayerMahou(first).getPlayerSavedSpawn())&&secondReturn.equals(Utils.getPlayerMahou(second).getPlayerSavedSpawn()),"Each party member retains their independent saved return position");
  require(Utils.getPlayerMahou(first).getPlayerOldDimension().equals(origin.dimension().identifier().toString())&&Utils.getPlayerMahou(second).getPlayerOldDimension().equals(origin.dimension().identifier().toString()),"Each party member retains the original dimension");
 }
 private static void aimAt(ServerPlayer player,net.minecraft.world.entity.LivingEntity target){
  double dx=target.getX()-player.getX(),dz=target.getZ()-player.getZ();
  player.setYRot((float)Math.toDegrees(Math.atan2(-dx,dz)));
  player.setXRot((float)-Math.toDegrees(Math.atan2(target.getBoundingBox().getCenter().y-player.getEyeY(),Math.hypot(dx,dz))));
 }
 private static void rejectRecast(net.minecraft.world.entity.LivingEntity target){
  first.teleportTo(target.getX(),target.getY(),target.getZ()-6);aimAt(first,target);
  require(RealityMarbleSpellEffect.getEnemy(first)==target,"The regression really aims at "+target.getType()+" before the recast");
  var position=first.position();var bossPosition=boss.position();var enemy=Utils.getPlayerMahou(first).getEnemy();
  int mana=Utils.getPlayerMahou(first).getStoredMana(),maximum=Utils.getPlayerMahou(first).getMaxMana();
  float health=first.getHealth(),bossHealth=boss.getHealth();
  require(!RealityMarbleSpellEffect.goToMarble(first),"An in-arena recast is rejected before selecting a new opponent");
  var stack=new net.minecraft.world.item.ItemStack(stepsword.mahoutsukai.item.ModItems.realityMarbleScroll.get());
  // Mystic Code calls this shared action directly instead of the standalone scroll's release guard.
  ((stepsword.mahoutsukai.item.spells.SpellScroll)stack.getItem()).useAction(stack,marble,first,true);
  require(stack.getCount()==1&&Utils.getPlayerMahou(first).getStoredMana()==mana&&Utils.getPlayerMahou(first).getMaxMana()==maximum&&first.getHealth()==health,"Rejected direct/Mystic Code recasts preserve the scroll, mana, growth and health");
  require(first.position().equals(position)&&boss.position().equals(bossPosition)&&boss.getHealth()==bossHealth,"Recasting never moves the caster or the selected Archmage, or resets his health");
  require(enemy.equals(Utils.getPlayerMahou(first).getEnemy())&&boss.getUUID().equals(Utils.getPlayerMahou(second).getEnemy()),"Recasting at an Archmage or party member keeps both original encounter links");
  require(marble.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(boss.arena()).inflate(64),Entity::isAlive).size()==1,"Targeted in-arena recasts cannot create a second Archmage");
  savedOrigins();allocations();
 }
 public static void start(ServerPlayer fixture){
  origin=fixture.level();marble=origin.getServer().getLevel(ResourceKey.create(Registries.DIMENSION,RealityMarbleSpellEffect.marble));
  require(marble!=null,"The shared encounter uses the registered Reality Marble dimension");
  var fixtureArena=FIRST_ALLOCATION.offset(12,0,12);
  var fixtureChunk=net.minecraft.world.level.ChunkPos.containing(fixtureArena);
  for(int x=fixtureChunk.x()-1;x<=fixtureChunk.x()+1;x++)for(int z=fixtureChunk.z()-1;z<=fixtureChunk.z()+1;z++)marble.getChunk(x,z);
  marble.waitForEntities(fixtureChunk,1);
  // Failed prior runs may have saved unloaded fixture entities; clear only this test's arena.
  for(var stale:marble.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(fixtureArena).inflate(80)))stale.discard();
  first=player("MarblePartyA",610.5,536.5,FIRST_ALLOCATION);
  second=player("MarblePartyB",616.5,540.5,SECOND_ALLOCATION);
  firstReturn=first.blockPosition();secondReturn=second.blockPosition();
  duelist=player("MarbleDuel",700.5,550.5,DUEL_ALLOCATION);duelReturn=duelist.blockPosition();
  var cow=EntityType.COW.create(origin,EntitySpawnReason.COMMAND);
  require(cow!=null,"The explicit duel has a real living opponent");
  cow.setPos(700.5,280,555.5);cow.setNoAi(true);cow.setNoGravity(true);require(origin.addFreshEntity(cow),"Duel opponent inserted");duelTarget=cow.getUUID();
  require(RealityMarbleSpellEffect.getEnemy(first)==null&&RealityMarbleSpellEffect.getEnemy(second)==null,"Party fixtures cast without selecting an explicit duel opponent");
  require(RealityMarbleSpellEffect.goToMarble(first),"First party scroll starts an encounter");
  var enemy=Utils.getPlayerMahou(first).getEnemy();boss=RealityMarbleBossEntity.liveFor(marble,enemy);
  require(boss!=null&&boss.isAlive(),"The first entry creates a live boss before its dimension transfer executes");boss.setNoAi(true);
  // This scenario tests exits, not chunk unloading after the final player leaves.
  arenaChunk=boss.chunkPosition();forcedArena=!marble.getForceLoadedChunks().contains(arenaChunk.pack());
  if(forcedArena)marble.setChunkForced(arenaChunk.x(),arenaChunk.z(),true);
  require(first.level()==origin,"The first dimension transfer is still queued when the second scroll is cast");
  require(RealityMarbleSpellEffect.goToMarble(second),"Near-simultaneous second scroll joins the encounter");
  require(enemy.equals(Utils.getPlayerMahou(second).getEnemy()),"Queued party casts resolve the same exact boss UUID");
  require(Utils.getPlayerMahou(first).getPlayerMarbleSpawn().distSqr(Utils.getPlayerMahou(second).getPlayerMarbleSpawn())<256,"Queued party destinations stay inside the same arena");
  first.setPos(first.getX()+1,first.getY(),first.getZ());
  aimAt(first,second);
  require(RealityMarbleSpellEffect.getEnemy(first)==second,"The queued recast now selects the other participant in the original dimension");
  require(RealityMarbleSpellEffect.goToMarble(first),"A repeated queued cast reuses its encounter even when aimed at an ally");
  require(enemy.equals(Utils.getPlayerMahou(first).getEnemy())&&enemy.equals(Utils.getPlayerMahou(second).getEnemy()),"Queued recasts cannot replace the shared Archmage with their newly selected ally");
  savedOrigins();allocations();
 }
 public static void verifyArrived(){
  require(first.level()==marble&&second.level()==marble,"Both queued casts arrive in the same actual ServerLevel");
  require(boss.isAlive()&&boss.inArena(first)&&boss.inArena(second),"Both party members share the live boss arena");
  require(boss.getUUID().equals(Utils.getPlayerMahou(first).getEnemy())&&boss.getUUID().equals(Utils.getPlayerMahou(second).getEnemy()),"Both arrived players retain the same boss opponent");
  require(marble.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(boss.arena()).inflate(64),Entity::isAlive).size()==1,"Two scrolls and a queued recast create exactly one boss");
  for(var p:List.of(first,second)){
   var floor=BlockPos.containing(p.getX(),p.getY()-.01,p.getZ());
   require(!marble.getBlockState(floor).getCollisionShape(marble,floor).isEmpty()&&marble.getFluidState(floor).isEmpty(),"Every party member lands on dry solid terrain");
  }
  rejectRecast(boss);
  rejectRecast(second);
  var cow=origin.getEntity(duelTarget);require(cow!=null,"The explicit opponent remains in the original dimension before the duel");
  duelist.setYRot(0);duelist.setXRot((float)-Math.toDegrees(Math.atan2(cow.getBoundingBox().getCenter().y-duelist.getEyeY(),5)));
  require(RealityMarbleSpellEffect.getEnemy(duelist)==cow,"The original cursor selection picks a real explicit opponent");
  require(RealityMarbleSpellEffect.goToMarble(duelist),"An explicit duel remains available during a shared boss fight");
  require(duelTarget.equals(Utils.getPlayerMahou(duelist).getEnemy()),"Explicit target duels bypass automatic boss-party joining");
  Utils.getPlayerMahou(first).setEnemy(null);RealityMarbleSpellEffect.realityMarbleLivingHurt(first);
 }
 public static void verifyFirstReturned(){
  require(first.level()==origin&&first.position().distanceToSqr(Vec3.atLowerCornerOf(firstReturn))<1,"The first participant returns to their own original position");
  require(second.level()==marble&&boss.isAlive()&&!boss.isRemoved(),"A participant leaving cannot discard the shared boss or remove the remaining player");
  require(duelist.level()==marble&&marble.getEntity(duelTarget)!=null,"The selected duel opponent follows its caster into Reality Marble");
  var duelSpawn=Utils.getPlayerMahou(duelist).getPlayerMarbleSpawn();
  require(marble.getEntitiesOfClass(RealityMarbleBossEntity.class,new AABB(duelSpawn).inflate(64),Entity::isAlive).isEmpty(),"An explicit selected-opponent duel never creates an unrelated Archmage");
  require(!boss.inArena(duelist)&&duelSpawn!=null&&Math.abs(duelSpawn.getX()-DUEL_ALLOCATION.getX())<=8&&Math.abs(duelSpawn.getZ()-DUEL_ALLOCATION.getZ())<=8,"Explicit target duels retain their own allocated arena horizontally; obsolete stored sky height is only a hint");
  var duelFloor=BlockPos.containing(duelist.getX(),duelist.getY()-.01,duelist.getZ());
  var duelSupport=marble.getBlockState(duelFloor).getCollisionShape(marble,duelFloor);
  require(!duelSupport.isEmpty()&&marble.getFluidState(duelFloor).isEmpty()&&Math.abs(duelFloor.getY()+duelSupport.max(net.minecraft.core.Direction.Axis.Y)-duelist.getY())<.01,"The independent explicit duel also arrives directly on actual dry solid terrain");
  duelBoss=RealityMarbleBossEntity.spawnFor(marble,Utils.getPlayerMahou(duelist).getPlayerMarbleSpawn().offset(12,0,12));
  require(duelBoss!=null&&duelBoss!=boss,"The original duel does not move or replace the shared boss");duelBoss.setNoAi(true);
  var deathProbe=DoomIntegration.player(marble,"MarbleDeath",GameType.SURVIVAL,boss.getX());
  deathProbe.setServerLevel(marble);deathProbe.setPos(boss.getX()+2,boss.getY(),boss.getZ());
  Utils.getPlayerMahou(deathProbe).setEnemy(boss.getUUID());
  deathProbe.invulnerableTime=0;deathProbe.hurtServer(marble,deathProbe.damageSources().genericKill(),100000);
  require(!deathProbe.isAlive()&&boss.isAlive()&&!boss.isRemoved(),"Actual participant death leaves the shared boss alive for the remaining fighter");
  require(boss.getUUID().equals(Utils.getPlayerMahou(second).getEnemy()),"Another participant's death never replaces the remaining fighter's boss link");
  marble.removePlayerImmediately(deathProbe,Entity.RemovalReason.DISCARDED);
  Utils.getPlayerMahou(second).setEnemy(null);RealityMarbleSpellEffect.realityMarbleLivingHurt(second);
  Utils.getPlayerMahou(duelist).setEnemy(null);RealityMarbleSpellEffect.realityMarbleLivingHurt(duelist);
 }
 public static void finish(){
  try{
   require(second.level()==origin&&second.position().distanceToSqr(Vec3.atLowerCornerOf(secondReturn))<1,"The second participant returns to their separate original position");
   require(duelist.level()==origin&&duelist.position().distanceToSqr(Vec3.atLowerCornerOf(duelReturn))<1,"The explicit duel also preserves its own return origin");
   require(boss.isAlive()&&!boss.isRemoved(),"The boss remains through ordinary participant exits in the loaded fixture: health="+boss.getHealth()+", removed="+boss.getRemovalReason());
   savedOrigins();allocations();
   System.out.println("PASS: MULTIPLAYER_MARBLE_036: actual queued two-player entry, targeted Archmage/ally recasts rejected without movement/mana/scroll/health changes, one shared boss, dry grounded arrivals, independent origins/returns, explicit duel without an unrelated boss, leave/death preserves fight, personal allocations unchanged");
  }finally{cleanup();}
 }
 public static void cleanup(){
  if(boss!=null)boss.discard();if(duelBoss!=null)duelBoss.discard();
  if(forcedArena&&arenaChunk!=null&&marble!=null){marble.setChunkForced(arenaChunk.x(),arenaChunk.z(),false);forcedArena=false;}
  if(origin==null)return;
  for(var level:origin.getServer().getAllLevels()){var target=duelTarget==null?null:level.getEntity(duelTarget);if(target!=null)target.discard();}
  var list=origin.getServer().getPlayerList();
  for(var p:new ServerPlayer[]{first,second,duelist})if(p!=null){
   list.getPlayers().remove(p);((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)list).mahoutsukai$playersByUUID().remove(p.getUUID());
   p.level().removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);Utils.getWorldMahou(origin).getAllSpawns().remove(p.getUUID());
  }
  Utils.getWorldMahou(origin).setDirty();
 }
}
