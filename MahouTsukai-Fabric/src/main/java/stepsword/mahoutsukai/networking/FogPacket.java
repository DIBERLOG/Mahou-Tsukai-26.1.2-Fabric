package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class FogPacket implements CustomPacketPayload {
   public static final Type<FogPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","fog"));
   public int eid = 0;
   public double range = 0.0;
   public double space = 0.0;
   public static final StreamCodec<ByteBuf, FogPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.eid, ByteBufCodecs.DOUBLE, p -> p.range, ByteBufCodecs.DOUBLE, p -> p.space, FogPacket::new
   );

   public FogPacket(int eid, double range, double space) {
      this.eid = eid;
      this.range = range;
      this.space = space;
   }


   public Type<FogPacket> type() {
      return TYPE;
   }
}
