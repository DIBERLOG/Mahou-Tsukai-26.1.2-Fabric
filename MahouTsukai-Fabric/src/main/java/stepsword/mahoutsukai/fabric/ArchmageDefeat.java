package stepsword.mahoutsukai.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.Projectile;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.Utils;

/** A private taunt after a real archmage defeat, delivered once the respawn client can hear it. */
public final class ArchmageDefeat {
 public static final AttachmentType<Boolean> PENDING=AttachmentRegistry.create(Identifier.fromNamespaceAndPath("mahoutsukai","archmage_defeat_pending"),b->b.initializer(()->false).persistent(Codec.BOOL).copyOnDeath());
 private static final AttachmentType<Boolean> ARMED=AttachmentRegistry.createDefaulted(Identifier.fromNamespaceAndPath("mahoutsukai","archmage_defeat_respawned"),()->false);

 public static void init(){
  ServerLivingEntityEvents.AFTER_DEATH.register(ArchmageDefeat::recordDeath);
  ServerPlayerEvents.AFTER_RESPAWN.register(ArchmageDefeat::afterRespawn);
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var player:server.getPlayerList().getPlayers())tick(player);});
 }

 public static void recordDeath(LivingEntity living,DamageSource source){
  if(!(living instanceof ServerPlayer player))return;
  player.removeAttached(PENDING);player.removeAttached(ARMED);
  if(!RealityMarbleSpellEffect.inMarble(player.level()))return;
  var mahou=Utils.getPlayerMahou(player);
  if(mahou==null||mahou.getEnemy()==null)return;
  var boss=owner(source.getEntity());
  if(boss==null)boss=owner(source.getDirectEntity());
  if(boss!=null&&boss.level()==player.level()&&boss.getUUID().equals(mahou.getEnemy()))player.setAttached(PENDING,true);
 }

 public static void afterRespawn(ServerPlayer oldPlayer,ServerPlayer newPlayer,boolean alive){
  // Fabric's attachment transfer shares this event; either callback order must retain the taunt.
  boolean pending=!alive&&(oldPlayer.getAttachedOrElse(PENDING,false)||newPlayer.getAttachedOrElse(PENDING,false));
  oldPlayer.removeAttached(PENDING);oldPlayer.removeAttached(ARMED);
  newPlayer.removeAttached(ARMED);
  if(pending){newPlayer.setAttached(PENDING,true);newPlayer.setAttached(ARMED,true);}else newPlayer.removeAttached(PENDING);
 }

 public static void tick(ServerPlayer player){
  if(!player.isAlive()||player.isRemoved()||!player.getAttachedOrElse(ARMED,false)||!player.getAttachedOrElse(PENDING,false)
     ||player.connection==null||!player.connection.hasClientLoaded())return;
  player.removeAttached(PENDING);player.removeAttached(ARMED);
  // 26.1.2 has no playNotifySound: send the native sound packet only to the respawned player.
  player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.ARCHMAGE_DEFEAT_LAUGH.get()),SoundSource.HOSTILE,
   player.getX(),player.getY(),player.getZ(),1,1,player.level().getRandom().nextLong()));
 }

 private static RealityMarbleBossEntity owner(Entity actor){
  for(int depth=0;actor!=null&&depth<3;depth++){
   if(actor instanceof RealityMarbleBossEntity boss)return boss;
   actor=actor instanceof Vex vex?vex.getOwner():actor instanceof EvokerFangs fangs?fangs.getOwner():actor instanceof Projectile projectile?projectile.getOwner():null;
  }
  return null;
 }
}
