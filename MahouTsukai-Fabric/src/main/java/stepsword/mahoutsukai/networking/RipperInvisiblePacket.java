package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class RipperInvisiblePacket implements CustomPacketPayload {
   public static final Type<RipperInvisiblePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","ripper_invisible"));
   public int entityids;
   public boolean invisible;
   public static final StreamCodec<ByteBuf, RipperInvisiblePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.invisible, RipperInvisiblePacket::new
   );

   public RipperInvisiblePacket(int e, boolean b) {
      this.entityids = e;
      this.invisible = b;
   }


   public Type<RipperInvisiblePacket> type() {
      return TYPE;
   }
}
