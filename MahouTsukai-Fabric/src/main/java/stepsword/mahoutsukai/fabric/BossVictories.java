package stepsword.mahoutsukai.fabric;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.util.Utils;

/** Server-owned victory credit; native titles work with the player's normal HUD and language. */
public final class BossVictories {
 private record Notice(Component name,UUID arenaBoss,int theme){}
 private static final AttachmentType<Set<UUID>> CONTRIBUTORS=AttachmentRegistry.createDefaulted(id("boss_contributors"),HashSet::new);
 private static final AttachmentType<Boolean> ANNOUNCED=AttachmentRegistry.createDefaulted(id("boss_victory_announced"),()->false);
 private static final AttachmentType<Notice> NOTICE=AttachmentRegistry.create(id("boss_victory_notice"));
 private static Identifier id(String path){return Identifier.fromNamespaceAndPath("mahoutsukai",path);}
 public static boolean supported(LivingEntity boss){
  var type=boss.getType();
  return boss instanceof RealityMarbleBossEntity||type==EntityType.ENDER_DRAGON||type==EntityType.WITHER||type==EntityType.WARDEN||type==EntityType.ELDER_GUARDIAN;
 }
 public static int theme(LivingEntity boss){return boss.getType()==EntityType.ENDER_DRAGON?1:boss.getType()==EntityType.WITHER?2:boss.getType()==EntityType.WARDEN?3:boss.getType()==EntityType.ELDER_GUARDIAN?4:0;}
 public static void init(){
  ServerLivingEntityEvents.AFTER_DAMAGE.register((entity,source,base,taken,blocked)->{if(taken>0)recordDamage(entity,source);});
  ServerLivingEntityEvents.AFTER_DEATH.register(BossVictories::onDeath);
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var player:server.getPlayerList().getPlayers())tick(player);});
 }
 public static void recordDamage(LivingEntity entity,DamageSource source){
  if(supported(entity)&&source.getEntity() instanceof ServerPlayer player)entity.getAttachedOrCreate(CONTRIBUTORS).add(player.getUUID());
 }
 public static void onDeath(LivingEntity boss,DamageSource source){
  if(!(boss.level() instanceof ServerLevel world)||!supported(boss)||boss.getAttachedOrElse(ANNOUNCED,false))return;
  boss.setAttached(ANNOUNCED,true);
  var contributors=new HashSet<>(boss.getAttachedOrCreate(CONTRIBUTORS));
  if(source.getEntity() instanceof ServerPlayer player)contributors.add(player.getUUID());
  if(boss.getLastHurtByPlayer() instanceof ServerPlayer player)contributors.add(player.getUUID());
  for(var player:world.getServer().getPlayerList().getPlayers()){
   boolean arena=boss instanceof RealityMarbleBossEntity&&RealityMarbleSpellEffect.inMarble(player.level())&&boss.getUUID().equals(Utils.getPlayerMahou(player).getEnemy());
   boolean credited=contributors.contains(player.getUUID())&&player.level()==world&&player.distanceToSqr(boss)<192*192;
   if(player.isAlive()&&!player.isSpectator()&&(arena||credited)){
    player.setAttached(NOTICE,new Notice(boss.getDisplayName().copy(),arena?boss.getUUID():null,theme(boss)));
    if(boss instanceof RealityMarbleBossEntity){var achievement=world.getServer().getAdvancements().get(id("archmage_victory"));if(achievement!=null)player.getAdvancements().award(achievement,"defeat_archmage");}
   }
  }
  boss.removeAttached(CONTRIBUTORS);
 }
 public static void tick(ServerPlayer player){
  var notice=player.getAttached(NOTICE);
  if(notice==null||!player.isAlive()||player.isRemoved()||player.connection==null)return;
  if(notice.arenaBoss()!=null&&RealityMarbleSpellEffect.inMarble(player.level())){
   var mahou=Utils.getPlayerMahou(player);
   if(mahou.getEnemy()!=null&&!notice.arenaBoss().equals(mahou.getEnemy())){player.removeAttached(NOTICE);return;}
   mahou.setEnemy(null);
   player.setAttached(NOTICE,new Notice(notice.name(),null,notice.theme()));
   RealityMarbleSpellEffect.realityMarbleLivingHurt(player);
   return;
  }
  if(!player.connection.hasClientLoaded())return;
  player.removeAttached(NOTICE);
  if(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player,stepsword.mahoutsukai.networking.BossVictoryPacket.TYPE)){
   net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player,new stepsword.mahoutsukai.networking.BossVictoryPacket(notice.theme(),notice.name()));
  }else{
   player.connection.send(new ClientboundSetTitlesAnimationPacket(12,85,30));
   player.connection.send(new ClientboundSetSubtitleTextPacket(Component.translatable("mahoutsukai.victory.over",notice.name()).withStyle(ChatFormatting.LIGHT_PURPLE)));
   player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable("mahoutsukai.victory.title").withStyle(ChatFormatting.GOLD,ChatFormatting.BOLD)));
  }
  player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE),SoundSource.PLAYERS,player.getX(),player.getY(),player.getZ(),.7F,1,player.getRandom().nextLong()));
  player.level().sendParticles(player,ParticleTypes.TOTEM_OF_UNDYING,false,false,player.getX(),player.getY()+1.2,player.getZ(),24,.65,.7,.65,.12);
 }
}
