package stepsword.jousting;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public record LanceSettingsPacket(String json) implements CustomPacketPayload {
 public static final Type<LanceSettingsPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("jousting","settings"));
 public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf,LanceSettingsPacket> CODEC=net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.map(LanceSettingsPacket::new,LanceSettingsPacket::json);
 public Type<LanceSettingsPacket> type(){return TYPE;}
}
