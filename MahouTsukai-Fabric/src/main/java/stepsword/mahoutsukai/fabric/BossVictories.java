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
 public static final int LOOT_TICKS=120*20;
 /** Absolute server game time persists through saves/rejoins; it is never reset by login. */
 public static final AttachmentType<Long> LOOT_RETURN_AT=AttachmentRegistry.create(id("boss_loot_return_at"),b->b.initializer(()->0L).persistent(com.mojang.serialization.Codec.LONG).copyOnDeath());
 private record Notice(Component name,int theme){}
 private static final AttachmentType<Set<UUID>> CONTRIBUTORS=AttachmentRegistry.createDefaulted(id("boss_contributors"),HashSet::new);
 private static final AttachmentType<Boolean> ANNOUNCED=AttachmentRegistry.createDefaulted(id("boss_victory_announced"),()->false);
 private static final AttachmentType<Notice> NOTICE=AttachmentRegistry.create(id("boss_victory_notice"));
 private static final AttachmentType<Integer> LOOT_SENT=AttachmentRegistry.createDefaulted(id("boss_loot_return_sent"),()->-1);
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
  if(boss instanceof RealityMarbleBossEntity archmage&&archmage.arena()!=null)
   stepsword.mahoutsukai.effects.projection.RealityMarbleShrine.rewardChest(world,archmage.arena());
  var contributors=new HashSet<>(boss.getAttachedOrCreate(CONTRIBUTORS));
  if(source.getEntity() instanceof ServerPlayer player)contributors.add(player.getUUID());
  if(boss.getLastHurtByPlayer() instanceof ServerPlayer player)contributors.add(player.getUUID());
  String victory=switch(theme(boss)){case 1->"ender_dragon";case 2->"wither";case 3->"warden";case 4->"elder_guardian";default->"archmage";};
  var achievement=world.getServer().getAdvancements().get(id(victory+"_victory"));
  for(var player:world.getServer().getPlayerList().getPlayers()){
   boolean arena=boss instanceof RealityMarbleBossEntity&&RealityMarbleSpellEffect.inMarble(player.level())&&boss.getUUID().equals(Utils.getPlayerMahou(player).getEnemy());
   boolean credited=contributors.contains(player.getUUID())&&player.level()==world&&player.distanceToSqr(boss)<192*192;
   if(player.isAlive()&&!player.isSpectator()&&(arena||credited)){
    player.setAttached(NOTICE,new Notice(boss.getDisplayName().copy(),theme(boss)));
    if(arena){
     player.setAttached(LOOT_RETURN_AT,world.getServer().overworld().getGameTime()+LOOT_TICKS);
     player.removeAttached(LOOT_SENT);
     Utils.getPlayerMahou(player).setEnemy(null);
     player.sendSystemMessage(Component.translatable("mahoutsukai.victory.loot_hint"));
    }
    if(achievement!=null)player.getAdvancements().award(achievement,"defeat_"+victory);
   }
  }
  boss.removeAttached(CONTRIBUTORS);
 }
 public static void tick(ServerPlayer player){
  tickLootReturn(player);
  var notice=player.getAttached(NOTICE);
  if(notice==null||!player.isAlive()||player.isRemoved()||player.connection==null)return;
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
 /** Keep the normal missing-opponent return path quiet until the loot deadline. */
 public static boolean awaitingLoot(ServerPlayer player){
  return RealityMarbleSpellEffect.inMarble(player.level())&&player.getAttachedOrElse(LOOT_RETURN_AT,0L)>player.level().getServer().overworld().getGameTime();
 }
 public static boolean lootWindowActive(ServerPlayer player){return player.getAttachedOrElse(LOOT_RETURN_AT,0L)>0;}
 private static void tickLootReturn(ServerPlayer player){
  long deadline=player.getAttachedOrElse(LOOT_RETURN_AT,0L);
  if(deadline<=0)return;
  if(!RealityMarbleSpellEffect.inMarble(player.level())){
   player.removeAttached(LOOT_RETURN_AT);player.removeAttached(LOOT_SENT);sendLootReturn(player,0);return;
  }
  if(!player.isAlive()||player.isRemoved()||player.connection==null)return;
  long remaining=deadline-player.level().getServer().overworld().getGameTime();
  if(remaining<=0){
   player.removeAttached(LOOT_RETURN_AT);player.removeAttached(LOOT_SENT);sendLootReturn(player,0);
   Utils.getPlayerMahou(player).setEnemy(null);
   RealityMarbleSpellEffect.realityMarbleLivingHurt(player);return;
  }
  if(!player.connection.hasClientLoaded())return;
  int seconds=(int)((remaining+19)/20);
  if(seconds!=player.getAttachedOrElse(LOOT_SENT,-1)){
   player.setAttached(LOOT_SENT,seconds);sendLootReturn(player,(int)Math.min(remaining,LOOT_TICKS));
  }
 }
 private static void sendLootReturn(ServerPlayer player,int ticks){
  if(player.connection==null)return;
  if(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player,stepsword.mahoutsukai.networking.LootReturnPacket.TYPE))
   net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player,new stepsword.mahoutsukai.networking.LootReturnPacket(ticks));
  else if(ticks>0)player.sendOverlayMessage(Component.translatable("mahoutsukai.victory.return_in",String.format(java.util.Locale.ROOT,"%d:%02d",(ticks+19)/1200,((ticks+19)/20)%60)));
 }
}
