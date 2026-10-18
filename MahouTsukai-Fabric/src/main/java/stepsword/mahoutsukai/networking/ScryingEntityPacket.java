package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;


public class ScryingEntityPacket implements CustomPacketPayload {
   public static final Type<ScryingEntityPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","scrying_entity"));
   public double posX;
   public double posY;
   public double posZ;
   public Optional<CompoundTag> entityTag;
   public static final StreamCodec<ByteBuf, ScryingEntityPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.DOUBLE,
      p -> p.posX,
      ByteBufCodecs.DOUBLE,
      p -> p.posY,
      ByteBufCodecs.DOUBLE,
      p -> p.posZ,
      ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG),
      p -> p.entityTag,
      ScryingEntityPacket::new
   );

   public ScryingEntityPacket(double posX, double posY, double posZ, Optional<CompoundTag> tag) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.entityTag = tag;
   }


   public Type<ScryingEntityPacket> type() {
      return TYPE;
   }
}
