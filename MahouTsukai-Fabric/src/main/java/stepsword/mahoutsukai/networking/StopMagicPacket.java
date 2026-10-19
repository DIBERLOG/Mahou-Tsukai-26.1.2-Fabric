package stepsword.mahoutsukai.networking;
/** The packet has no target or payload: a client can cancel only their own magic. */
public record StopMagicPacket() implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
 public static final Type<StopMagicPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","stop_magic"));
 public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf,StopMagicPacket> STREAM_CODEC=net.minecraft.network.codec.StreamCodec.unit(new StopMagicPacket());
 public Type<StopMagicPacket> type(){return TYPE;}
 public static void handle(net.minecraft.server.level.ServerPlayer player){
  boolean active=stepsword.mahoutsukai.util.EffectUtil.hasBuff(player,stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES);
  if(active)stepsword.mahoutsukai.util.EffectUtil.debuff(player,stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES);
  if(player.isUsingItem())player.stopUsingItem();
  stepsword.mahoutsukai.fabric.SpellCasting.cancel(player);stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll.cancelCharge(player);
  if(active)player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("mahoutsukai.black_flame.stopped"));
 }
}
