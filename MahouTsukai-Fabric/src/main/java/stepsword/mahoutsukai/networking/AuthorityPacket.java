package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class AuthorityPacket implements CustomPacketPayload {
   public static final Type<AuthorityPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","authority"));
   public int entityids;
   public boolean authority;
   public static final StreamCodec<ByteBuf, AuthorityPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityids, ByteBufCodecs.BOOL, p -> p.authority, AuthorityPacket::new
   );

   public AuthorityPacket(int e, boolean b) {
      this.entityids = e;
      this.authority = b;
   }


   public Type<AuthorityPacket> type() {
      return TYPE;
   }
}
