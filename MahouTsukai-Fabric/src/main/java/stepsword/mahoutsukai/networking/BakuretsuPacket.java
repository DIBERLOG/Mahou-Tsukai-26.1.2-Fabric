package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;


public class BakuretsuPacket implements CustomPacketPayload {
   public double posX;
   public double posY;
   public double posZ;
   public int strength;
   public float motionX;
   public float motionY;
   public float motionZ;
   public float r;
   public float g;
   public float b;
   public static final StreamCodec<ByteBuf, BakuretsuPacket> STREAM_CODEC = new StreamCodec<ByteBuf, BakuretsuPacket>() {
      public BakuretsuPacket decode(ByteBuf buf) {
         BakuretsuPacket packet = new BakuretsuPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, BakuretsuPacket packet) {
         packet.encode(buf);
      }
   };

   public BakuretsuPacket() {
   }

   public BakuretsuPacket(double posX, double posY, double posZ, int strength, float motionX, float motionY, float motionZ, float[] c) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.strength = strength;
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.r = c[0];
      this.g = c[1];
      this.b = c[2];
   }

   public void decode(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.strength = buf.readInt();
      this.motionX = buf.readFloat();
      this.motionY = buf.readFloat();
      this.motionZ = buf.readFloat();
      this.r = buf.readFloat();
      this.g = buf.readFloat();
      this.b = buf.readFloat();
   }

   public void encode(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeInt(this.strength);
      buf.writeFloat(this.motionX);
      buf.writeFloat(this.motionY);
      buf.writeFloat(this.motionZ);
      buf.writeFloat(this.r);
      buf.writeFloat(this.g);
      buf.writeFloat(this.b);
   }


   public static final Type<BakuretsuPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","bakuretsu"));
   public Type<BakuretsuPacket> type() {
      return TYPE;
   }
}
