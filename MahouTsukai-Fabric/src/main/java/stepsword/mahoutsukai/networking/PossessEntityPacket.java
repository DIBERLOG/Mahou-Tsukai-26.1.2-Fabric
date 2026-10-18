package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class PossessEntityPacket implements CustomPacketPayload {
   public static final Type<PossessEntityPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","possess_entity"));
   public int entityId = 0;
   public int chunkx = 0;
   public int chunkz = 0;
   public static final StreamCodec<ByteBuf, PossessEntityPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.chunkx, ByteBufCodecs.INT, p -> p.chunkz, ByteBufCodecs.INT, p -> p.entityId, PossessEntityPacket::new
   );

   public PossessEntityPacket(int x, int z, int entity) {
      this.entityId = entity;
      this.chunkx = x;
      this.chunkz = z;
   }


   public Type<PossessEntityPacket> type() {
      return TYPE;
   }
}
