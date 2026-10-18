package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;

public class MentalDisplacementUpdatePacket implements CustomPacketPayload {
   public static final Type<MentalDisplacementUpdatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mental_displacement_update"));
   public int entityId;
   public double x;
   public double y;
   public double z;
   public double vx;
   public double vy;
   public double vz;
   public double yaw;
   public double pitch;
   public static final StreamCodec<ByteBuf, MentalDisplacementUpdatePacket> STREAM_CODEC = new StreamCodec<ByteBuf, MentalDisplacementUpdatePacket>() {
      public MentalDisplacementUpdatePacket decode(ByteBuf buf) {
         MentalDisplacementUpdatePacket packet = new MentalDisplacementUpdatePacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, MentalDisplacementUpdatePacket packet) {
         packet.encode(buf);
      }
   };

   public MentalDisplacementUpdatePacket() {
   }

   public MentalDisplacementUpdatePacket(Entity e, double x, double y, double z, double vx, double vy, double vz, double yaw, double pitch) {
      this.entityId = e.getId();
      this.x = x;
      this.y = y;
      this.z = z;
      this.vx = vx;
      this.vy = vy;
      this.vz = vz;
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public void decode(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.vx = buf.readDouble();
      this.vy = buf.readDouble();
      this.vz = buf.readDouble();
      this.yaw = buf.readDouble();
      this.pitch = buf.readDouble();
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.vx);
      buf.writeDouble(this.vy);
      buf.writeDouble(this.vz);
      buf.writeDouble(this.yaw);
      buf.writeDouble(this.pitch);
   }


   public static void updatePosition(MentalDisplacementEntity entity, MentalDisplacementUpdatePacket packet) {
      entity.updatePacket(packet);
   }

   public Type<MentalDisplacementUpdatePacket> type() {
      return TYPE;
   }
 public static boolean handleNative(MentalDisplacementUpdatePacket packet,net.minecraft.server.level.ServerPlayer player){
  if(!(player.level().getEntity(packet.entityId) instanceof MentalDisplacementEntity entity) || !player.getUUID().equals(entity.getOwnerId()))return false;
  double[] values={packet.x,packet.y,packet.z,packet.vx,packet.vy,packet.vz,packet.yaw,packet.pitch};for(double value:values)if(!Double.isFinite(value))return false;
  var pos=new net.minecraft.world.phys.Vec3(packet.x,packet.y,packet.z);int range=stepsword.mahoutsukai.config.MTConfig.MENTAL_DISPLACEMENT_RANGE;
  if(Math.abs(packet.x)>30000000 || Math.abs(packet.z)>30000000 || packet.y<player.level().getMinY()-64 || packet.y>player.level().getMaxY()+64 || pos.distanceToSqr(entity.position())>256 || stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect.checkDistance(player.blockPosition(),net.minecraft.core.BlockPos.containing(pos))>range || new net.minecraft.world.phys.Vec3(packet.vx,packet.vy,packet.vz).lengthSqr()>256)return false;
  entity.updatePacket(packet);return true;
 }

}
