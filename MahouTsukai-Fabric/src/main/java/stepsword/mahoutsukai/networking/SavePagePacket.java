package stepsword.mahoutsukai.networking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public record SavePagePacket(int page) implements CustomPacketPayload {
 public static final Type<SavePagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","save_page"));
 public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf,SavePagePacket> STREAM_CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.INT,SavePagePacket::page,SavePagePacket::new);
 public Type<SavePagePacket> type(){return TYPE;}
}
