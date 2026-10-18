package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;


public class ActingPacket implements CustomPacketPayload {
 public static final Type<ActingPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","acting"));
   public int entityids;
   public int copy_id;
   public boolean acting;
   public static final StreamCodec<ByteBuf, ActingPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.acting, ByteBufCodecs.INT, p -> p.copy_id, ActingPacket::new
   );

   public ActingPacket(int e, boolean b, int copy) {
      this.entityids = e;
      this.acting = b;
      this.copy_id = copy;
   }



   public Type<ActingPacket> type() {
      return TYPE;
   }
}
