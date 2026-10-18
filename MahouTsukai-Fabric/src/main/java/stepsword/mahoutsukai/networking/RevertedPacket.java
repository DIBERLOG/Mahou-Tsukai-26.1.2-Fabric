package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class RevertedPacket implements CustomPacketPayload {
   public static final Type<RevertedPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","reverted"));
   public int entityids;
   public boolean reverted;
   public static final StreamCodec<ByteBuf, RevertedPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.reverted, RevertedPacket::new
   );

   public RevertedPacket(int e, boolean b) {
      this.entityids = e;
      this.reverted = b;
   }


   public Type<RevertedPacket> type() {
      return TYPE;
   }
}
