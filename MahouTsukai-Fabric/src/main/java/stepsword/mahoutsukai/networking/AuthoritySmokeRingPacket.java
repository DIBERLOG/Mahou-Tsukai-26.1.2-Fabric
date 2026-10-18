package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record AuthoritySmokeRingPacket(double posX, double posY, double posZ, double directionX, double directionY, double directionZ)
   implements CustomPacketPayload {
   public static final Type<AuthoritySmokeRingPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","authority_smoke_ring"));
   public static final StreamCodec<ByteBuf, AuthoritySmokeRingPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::posX,
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::posY,
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::posZ,
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::directionX,
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::directionY,
      ByteBufCodecs.DOUBLE,
      AuthoritySmokeRingPacket::directionZ,
      AuthoritySmokeRingPacket::new
   );


   public Type<AuthoritySmokeRingPacket> type() {
      return TYPE;
   }
}
