package stepsword.mahoutsukai.networking;
public record SpellStatusPacket(int doomTicks) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
 public static final Type<SpellStatusPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","spell_status"));
 public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf,SpellStatusPacket> STREAM_CODEC=new net.minecraft.network.codec.StreamCodec<>(){
  public SpellStatusPacket decode(io.netty.buffer.ByteBuf b){return new SpellStatusPacket(Math.max(0,b.readInt()));}
  public void encode(io.netty.buffer.ByteBuf b,SpellStatusPacket p){b.writeInt(p.doomTicks());}
 };
 public Type<SpellStatusPacket> type(){return TYPE;}
 public static void sync(net.minecraft.server.level.ServerPlayer player){long remaining=((stepsword.mahoutsukai.doom.DoomHolder)player).mahoutsukai$getDoomReadyAt()-player.level().getServer().overworld().getGameTime();PacketHandler.sendTo(player,new SpellStatusPacket((int)Math.clamp(remaining,0,Integer.MAX_VALUE)));}
}
