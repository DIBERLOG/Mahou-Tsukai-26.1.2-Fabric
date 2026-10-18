package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class RagePacket implements CustomPacketPayload {
   public static final Type<RagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","rage"));
   public int entityids;
   public boolean rage;
   public static final StreamCodec<ByteBuf, RagePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.rage, RagePacket::new
   );

   public RagePacket(int e, boolean b) {
      this.entityids = e;
      this.rage = b;
   }


   public Type<RagePacket> type() {
      return TYPE;
   }
}
