package stepsword.mahoutsukai.networking;
public record GuideCatalogPacket(String json) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
 public static final Type<GuideCatalogPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","guide_catalog"));
 public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf,GuideCatalogPacket> STREAM_CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.stringUtf8(524288),GuideCatalogPacket::json,GuideCatalogPacket::new);
 public Type<GuideCatalogPacket> type(){return TYPE;}
}
