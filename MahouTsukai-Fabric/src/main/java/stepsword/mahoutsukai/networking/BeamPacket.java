package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;


public class BeamPacket implements CustomPacketPayload {
   public int entityId = 0;
   public static final StreamCodec<ByteBuf, BeamPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, p -> p.entityId, BeamPacket::new);

   public BeamPacket(int e) {
      this.entityId = e;
   }


   public static final Type<BeamPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","beam"));
   public Type<BeamPacket> type() {
      return TYPE;
   }
}
