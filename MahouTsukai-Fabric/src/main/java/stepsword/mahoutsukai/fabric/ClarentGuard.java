package stepsword.mahoutsukai.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.networking.ClarentStatePacket;

/** Deadlines belong to the player, so replacing the sword or reconnecting cannot reset them. */
public final class ClarentGuard {
 public static final int ACTIVE_TICKS=300, COOLDOWN_TICKS=600;
 public static final AttachmentType<Long> ACTIVE_UNTIL=AttachmentRegistry.create(id("clarent_active_until"),b->b.initializer(()->0L).persistent(Codec.LONG));
 public static final AttachmentType<Long> READY_AT=AttachmentRegistry.create(id("clarent_ready_at"),b->b.initializer(()->0L).persistent(Codec.LONG).copyOnDeath());
 private static Identifier id(String path){return Identifier.fromNamespaceAndPath("mahoutsukai",path);}
 private static long now(ServerPlayer p){return p.level().getServer().overworld().getGameTime();}
 public static int activeTicks(ServerPlayer p){return (int)Math.clamp(p.getAttachedOrElse(ACTIVE_UNTIL,0L)-now(p),0,ACTIVE_TICKS);}
 public static int cooldownTicks(ServerPlayer p){return p.getAttachedOrElse(ACTIVE_UNTIL,0L)>0?0:(int)Math.clamp(p.getAttachedOrElse(READY_AT,0L)-now(p),0,COOLDOWN_TICKS);}
 public static boolean begin(ServerPlayer p){
  if(!p.isAlive()||p.isSpectator()||p.getAttachedOrElse(READY_AT,0L)>now(p))return false;
  p.setAttached(ACTIVE_UNTIL,now(p)+ACTIVE_TICKS);
  // Reserve the full deadline as a fallback if the server stops during a block.
  p.setAttached(READY_AT,now(p)+ACTIVE_TICKS+COOLDOWN_TICKS);sync(p);return true;
 }
 public static boolean canBlock(LivingEntity entity){return entity instanceof ServerPlayer p&&p.isAlive()&&p.isUsingItem()&&p.getUsedItemHand()==net.minecraft.world.InteractionHand.MAIN_HAND&&p.getMainHandItem().getItem() instanceof Clarent&&p.getUseItem().getItem() instanceof Clarent&&activeTicks(p)>0;}
 public static void finish(ServerPlayer p){
  long until=p.getAttachedOrElse(ACTIVE_UNTIL,0L);if(until<=0)return;
  p.removeAttached(ACTIVE_UNTIL);p.setAttached(READY_AT,Math.min(now(p),until)+COOLDOWN_TICKS);
  applyCooldown(p);sync(p);
 }
 private static void applyCooldown(ServerPlayer p){int ticks=cooldownTicks(p);if(ticks>0)p.getCooldowns().addCooldown(new ItemStack(ModItems.REGISTERED.get("clarent")),ticks);}
 private static void sync(ServerPlayer p){if(p.connection!=null&&ServerPlayNetworking.canSend(p,ClarentStatePacket.TYPE))ServerPlayNetworking.send(p,new ClarentStatePacket(activeTicks(p),cooldownTicks(p)));}
 public static void tick(ServerPlayer p){
   if(p.getAttachedOrElse(ACTIVE_UNTIL,0L)>0&&(!canBlock(p))){finish(p);if(p.isUsingItem()&&p.getUseItem().getItem() instanceof Clarent)p.stopUsingItem();}
   if(p.tickCount%20==0&&(activeTicks(p)>0||cooldownTicks(p)>0||p.getMainHandItem().getItem() instanceof Clarent))sync(p);
 }
 public static void init(){
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerList().getPlayers())tick(p);});
  ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->finish(handler.player));
  ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{if(entity instanceof ServerPlayer p)finish(p);});
  ServerPlayerEvents.JOIN.register(p->{finish(p);applyCooldown(p);sync(p);});
  ServerPlayerEvents.AFTER_RESPAWN.register((old,p,alive)->{
   // Attachment transfer shares this event; either listener order must retain the deadline.
   finish(old);p.setAttached(READY_AT,Math.max(old.getAttachedOrElse(READY_AT,0L),p.getAttachedOrElse(READY_AT,0L)));
   p.removeAttached(ACTIVE_UNTIL);applyCooldown(p);sync(p);
  });
 }
}
