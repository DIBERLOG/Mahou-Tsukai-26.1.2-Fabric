package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class BlackFirePacket implements CustomPacketPayload {
   public static final Type<BlackFirePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","black_fire_packet"));
   public int entityids;
   public boolean onFire;
   public static final StreamCodec<ByteBuf, BlackFirePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.onFire, BlackFirePacket::new
   );

   public BlackFirePacket(int e, boolean b) {
      this.entityids = e;
      this.onFire = b;
   }

   public Type<BlackFirePacket> type() {
      return TYPE;
   }
}
