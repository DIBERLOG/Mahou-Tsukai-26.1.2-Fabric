package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.util.Codecs;

public class SparkExplosionPacket implements CustomPacketPayload {
 public static final Type<SparkExplosionPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","spark_explosion"));
   public Vec3 pos;
   public List<Float> color;
   public static final StreamCodec<ByteBuf, SparkExplosionPacket> STREAM_CODEC = StreamCodec.composite(
      Codecs.VEC3_STREAM, p -> p.pos, ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), p -> p.color, SparkExplosionPacket::new
   );

   public SparkExplosionPacket(Vec3 e, List<Float> b) {
      this.pos = e;
      this.color = b;
   }



   public Type<SparkExplosionPacket> type() {
      return TYPE;
   }
}
