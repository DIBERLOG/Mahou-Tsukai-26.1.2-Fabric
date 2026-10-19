package stepsword.mahoutsukai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.fabric.ArchmageDefeat;
import stepsword.mahoutsukai.fireball.FireballEntity;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.Utils;

/** Real Fabric death/respawn events, native copy/persistence, and captured private sound packets. */
public final class ArchmageDefeat036Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static ServerPlayer player(ServerLevel level,String name){
  var p=new ServerPlayer(level.getServer(),level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),name),net.minecraft.server.level.ClientInformation.createDefault());
  p.setServerLevel(level);p.setPos(1664.5,84,1664.5);p.setGameMode(GameType.SURVIVAL);
  capture(p,new AtomicBoolean(true),new ArrayList<>());return p;
 }
 private static void capture(ServerPlayer player,AtomicBoolean loaded,List<ClientboundSoundPacket> sounds){
  var level=player.level();
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false)){
   @Override public boolean hasClientLoaded(){return loaded.get();}
   @Override public void send(net.minecraft.network.protocol.Packet<?> packet){if(packet instanceof ClientboundSoundPacket sound)sounds.add(sound);}
  };
 }
 private static long laughs(List<ClientboundSoundPacket> sounds){return sounds.stream().filter(s->s.getSound().value()==ModSounds.ARCHMAGE_DEFEAT_LAUGH.get()).count();}
 private static boolean pending(ServerPlayer player){return player.getAttachedOrElse(ArchmageDefeat.PENDING,false);}
 private static net.minecraft.nbt.CompoundTag save(ServerPlayer p){
  var saved=TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,p.level().registryAccess());p.saveWithoutId(saved);return saved.buildResult();
 }
 private static void load(ServerPlayer p,net.minecraft.nbt.CompoundTag tag){p.load(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,p.level().registryAccess(),tag));}

 public static void run(ServerPlayer fixture){
  var marble=fixture.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION,RealityMarbleSpellEffect.marble));
  require(marble!=null,"Defeat fixture has the actual Reality Marble dimension");
  require(ArchmageDefeat.PENDING.isPersistent()&&ArchmageDefeat.PENDING.copyOnDeath(),"Defeat state uses a persistent native attachment copied on death");
  var boss=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),marble);boss.setArena(new BlockPos(1664,84,1664));boss.setPos(1666.5,84,1664.5);
  var foreign=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),marble);
  var dead=player(marble,"ArchmageDefeat036");
  var respawned=player(fixture.level(),"ArchmageRespawn036");
  var relogged=player(fixture.level(),"ArchmageRelog036");
  var consumed=player(fixture.level(),"ArchmageConsumed036");
  var aliveReturn=player(fixture.level(),"ArchmageAlive036");
  var probe=player(marble,"ArchmageSources036");
  var vex=EntityType.VEX.create(marble,EntitySpawnReason.COMMAND);
  require(vex!=null,"Native vex source fixture exists");vex.setOwner(boss);
  var bolt=new FireballEntity(ModEntities.FIREBALL.get(),marble);bolt.setOwner(boss);
  var fangs=new EvokerFangs(marble,1664.5,84,1664.5,0,0,boss);
  try{
   Utils.getPlayerMahou(dead).setEnemy(boss.getUUID());
   dead.setHealth(0);dead.die(marble.damageSources().mobAttack(boss));
   require(pending(dead),"An actual player death to the encounter archmage records the taunt through Fabric AFTER_DEATH");
   var deathSave=save(dead);

   load(relogged,deathSave);relogged.setHealth(20);
   var relogSounds=new ArrayList<ClientboundSoundPacket>();capture(relogged,new AtomicBoolean(true),relogSounds);
   require(pending(relogged),"Pending defeat survives the native player save/load boundary");
   ArchmageDefeat.tick(relogged);
   require(laughs(relogSounds)==0,"Loading a player save never plays a respawn taunt on ordinary login");

   var loaded=new AtomicBoolean(false);var sounds=new ArrayList<ClientboundSoundPacket>();capture(respawned,loaded,sounds);
   respawned.restoreFrom(dead,false);
   ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(dead,respawned,false);
   require(respawned.isAlive()&&pending(respawned)&&!pending(dead),"Fabric respawn transfers the persistent defeat to the new living player and clears the old instance");
   for(int i=0;i<10;i++)ArchmageDefeat.tick(respawned);
   require(laughs(sounds)==0&&pending(respawned),"Taunt waits while the respawned client's world is not loaded");
   loaded.set(true);ArchmageDefeat.tick(respawned);
   require(laughs(sounds)==1&&!pending(respawned),"The first loaded respawn tick sends one private laugh and consumes its pending state");
   var sound=sounds.stream().filter(s->s.getSound().value()==ModSounds.ARCHMAGE_DEFEAT_LAUGH.get()).findFirst().orElseThrow();
   require(sound.getSource()==net.minecraft.sounds.SoundSource.HOSTILE&&sound.getVolume()==1&&sound.getPitch()==1,"The private native taunt uses the intended sound category and volume");
   for(int i=0;i<40;i++)ArchmageDefeat.tick(respawned);
   require(laughs(sounds)==1,"Later ticks cannot repeat a consumed respawn laugh");

   load(consumed,save(respawned));consumed.setHealth(20);
   var consumedSounds=new ArrayList<ClientboundSoundPacket>();capture(consumed,new AtomicBoolean(true),consumedSounds);ArchmageDefeat.tick(consumed);
   require(!pending(consumed)&&laughs(consumedSounds)==0,"A consumed taunt stays consumed after native save/reload");
   probe.setAttached(ArchmageDefeat.PENDING,true);
   ArchmageDefeat.afterRespawn(probe,consumed,false);
   ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(probe,consumed,false);
   ArchmageDefeat.tick(consumed);
   require(laughs(consumedSounds)==1&&!pending(consumed),"The taunt also survives when its respawn handler precedes Fabric attachment transfer");
   aliveReturn.restoreFrom(relogged,true);
   var aliveSounds=new ArrayList<ClientboundSoundPacket>();capture(aliveReturn,new AtomicBoolean(true),aliveSounds);
   ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(relogged,aliveReturn,true);ArchmageDefeat.tick(aliveReturn);
   require(laughs(aliveSounds)==0&&!pending(aliveReturn),"An alive dimension-return respawn cannot trigger the death taunt");

   Utils.getPlayerMahou(probe).setEnemy(boss.getUUID());
   for(var source:List.of(marble.damageSources().mobAttack(vex),marble.damageSources().indirectMagic(bolt,null),marble.damageSources().indirectMagic(fangs,null))){
    ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(probe,source);
    require(pending(probe),"Owned native vex, projectile and evoker-fang deaths count as the exact archmage's defeat");
   }
   ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(probe,marble.damageSources().fall());
   require(!pending(probe),"An unrelated death clears stale defeat state instead of queuing a laugh");
   ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(probe,marble.damageSources().mobAttack(foreign));
   require(!pending(probe),"A different archmage cannot taunt another boss's encounter victim");
   probe.setServerLevel(fixture.level());
   ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(probe,marble.damageSources().mobAttack(boss));
   require(!pending(probe),"Deaths outside Reality Marble never queue an encounter taunt");
   System.out.println("PASS: ARCHMAGE_DEFEAT_036: native death and copy-on-death respawn events, private laugh exactly once after client load, save/reload, ordinary-login and alive-return rejection, exact boss/vex/projectile/fang ownership and unrelated-death rejection");
  }finally{
   for(var p:List.of(dead,respawned,relogged,consumed,aliveReturn,probe))p.discard();
   boss.discard();foreign.discard();vex.discard();bolt.discard();fangs.discard();
  }
 }
}
