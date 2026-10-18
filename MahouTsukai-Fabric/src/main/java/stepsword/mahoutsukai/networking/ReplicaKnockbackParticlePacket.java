package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;


public class ReplicaKnockbackParticlePacket implements CustomPacketPayload {
 public static final Type<ReplicaKnockbackParticlePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","replica_knockback_particle"));
   public double posX;
   public double posY;
   public double posZ;
   public float r;
   public float g;
   public float b;
   public static final StreamCodec<ByteBuf, ReplicaKnockbackParticlePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.DOUBLE,
      p -> p.posX,
      ByteBufCodecs.DOUBLE,
      p -> p.posY,
      ByteBufCodecs.DOUBLE,
      p -> p.posZ,
      ByteBufCodecs.FLOAT,
      p -> p.r,
      ByteBufCodecs.FLOAT,
      p -> p.g,
      ByteBufCodecs.FLOAT,
      p -> p.b,
      ReplicaKnockbackParticlePacket::new
   );

   public ReplicaKnockbackParticlePacket(double posX, double posY, double posZ, float r, float g, float b) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.r = r;
      this.g = g;
      this.b = b;
   }



   public Type<ReplicaKnockbackParticlePacket> type() {
      return TYPE;
   }
}
