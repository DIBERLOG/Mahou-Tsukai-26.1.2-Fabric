package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class MagicCircleBoomParticlePacket implements CustomPacketPayload {
   public static final Type<MagicCircleBoomParticlePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","magic_circle_boom_particle"));
   public double posX;
   public double posY;
   public double posZ;
   public double lookX;
   public double lookY;
   public double lookZ;
   public double speed;
   public boolean randomspeed;
   public int color;
   public int boom_mode;
   public int particles;
   public boolean big;
   public float r;
   public float g;
   public float b;
   public float r2;
   public float g2;
   public float b2;
   public static final StreamCodec<ByteBuf, MagicCircleBoomParticlePacket> STREAM_CODEC = new StreamCodec<ByteBuf, MagicCircleBoomParticlePacket>() {
      public MagicCircleBoomParticlePacket decode(ByteBuf buf) {
         MagicCircleBoomParticlePacket packet = new MagicCircleBoomParticlePacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, MagicCircleBoomParticlePacket packet) {
         packet.encode(buf);
      }
   };

   public MagicCircleBoomParticlePacket() {
   }

   public MagicCircleBoomParticlePacket(
      double posX,
      double posY,
      double posZ,
      double lookX,
      double lookY,
      double lookZ,
      double speed,
      int color,
      int boom_mode,
      int particles,
      boolean randomspeed,
      boolean big,
      float r,
      float g,
      float b,
      float r2,
      float g2,
      float b2
   ) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.lookX = lookX;
      this.lookY = lookY;
      this.lookZ = lookZ;
      this.speed = speed;
      this.color = color;
      this.boom_mode = boom_mode;
      this.particles = particles;
      this.randomspeed = randomspeed;
      this.big = big;
      this.r = r;
      this.g = g;
      this.b = b;
      this.r2 = r2;
      this.g2 = g2;
      this.b2 = b2;
   }

   public void decode(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.lookX = buf.readDouble();
      this.lookY = buf.readDouble();
      this.lookZ = buf.readDouble();
      this.speed = buf.readDouble();
      this.color = buf.readInt();
      this.boom_mode = buf.readInt();
      this.particles = buf.readInt();
      this.randomspeed = buf.readBoolean();
      this.big = buf.readBoolean();
      this.r = buf.readFloat();
      this.g = buf.readFloat();
      this.b = buf.readFloat();
      this.r2 = buf.readFloat();
      this.g2 = buf.readFloat();
      this.b2 = buf.readFloat();
   }

   public void encode(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeDouble(this.lookX);
      buf.writeDouble(this.lookY);
      buf.writeDouble(this.lookZ);
      buf.writeDouble(this.speed);
      buf.writeInt(this.color);
      buf.writeInt(this.boom_mode);
      buf.writeInt(this.particles);
      buf.writeBoolean(this.randomspeed);
      buf.writeBoolean(this.big);
      buf.writeFloat(this.r);
      buf.writeFloat(this.g);
      buf.writeFloat(this.b);
      buf.writeFloat(this.r2);
      buf.writeFloat(this.g2);
      buf.writeFloat(this.b2);
   }


   public Type<MagicCircleBoomParticlePacket> type() {
      return TYPE;
   }
}
