package stepsword.mahoutsukai.networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
public record ClarentStatePacket(int activeTicks,int cooldownTicks) implements CustomPacketPayload {
 public static final Type<ClarentStatePacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","clarent_guard"));
 public static final StreamCodec<RegistryFriendlyByteBuf,ClarentStatePacket> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,ClarentStatePacket::activeTicks,ByteBufCodecs.VAR_INT,ClarentStatePacket::cooldownTicks,ClarentStatePacket::new);
 public Type<ClarentStatePacket> type(){return TYPE;}
}
