package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class GardenPacket implements CustomPacketPayload {
   public static final Type<GardenPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","garden_packet"));
   public double posX;
   public double posY;
   public double posZ;
   public static final StreamCodec<ByteBuf, GardenPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.DOUBLE, p -> p.posX, ByteBufCodecs.DOUBLE, p -> p.posY, ByteBufCodecs.DOUBLE, p -> p.posZ, GardenPacket::new
   );

   public GardenPacket(double posX, double posY, double posZ) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
   }

   public Type<GardenPacket> type() {
      return TYPE;
   }
}
