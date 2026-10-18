package stepsword.mahoutsukai.networking;
import stepsword.mahoutsukai.config.MTConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.tile.FogProjectorTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class FogProjectorUpdatePacket implements CustomPacketPayload {
   BlockPos pos;
   public float particle_fog_size = 8.0F;
   public int particle_fog_color_r = 229;
   public int particle_fog_color_g = 238;
   public int particle_fog_color_b = 229;
   public int particle_fog_color_a = 255;
   public float particle_fog_thickness = 0.07F;
   public float particle_fog_speed = 0.002F;
   public boolean particle_fog_random_x = true;
   public boolean particle_fog_random_y = true;
   public boolean particle_fog_random_z = true;
   public float particle_fog_direction_x = 0.0F;
   public float particle_fog_direction_y = 0.0F;
   public float particle_fog_direction_z = 0.0F;
   public boolean particle_fog_shaders = true;
   public int particle_fog_frequency_num = 1;
   public int particle_fog_frequency_ticks = 20;
   public float camera_fog_radius = 0.0F;
   public int camera_fog_color_r = 200;
   public int camera_fog_color_g = 200;
   public int camera_fog_color_b = 200;
   public float camera_fog_strength = 5.0F;
   public boolean camera_fog_toggle = false;
   public static final StreamCodec<ByteBuf, FogProjectorUpdatePacket> STREAM_CODEC = new StreamCodec<ByteBuf, FogProjectorUpdatePacket>() {
      public FogProjectorUpdatePacket decode(ByteBuf buf) {
         FogProjectorUpdatePacket packet = new FogProjectorUpdatePacket();
         packet.decode(new FriendlyByteBuf(buf));
         return packet;
      }

      public void encode(ByteBuf buf, FogProjectorUpdatePacket packet) {
         packet.encode(new FriendlyByteBuf(buf));
      }
   };

   public FogProjectorUpdatePacket() {
   }

   public FogProjectorUpdatePacket(
      BlockPos pos,
      float particle_fog_size,
      int particle_fog_color_r,
      int particle_fog_color_g,
      int particle_fog_color_b,
      int particle_fog_color_a,
      float particle_fog_thickness,
      float particle_fog_speed,
      boolean particle_fog_random_x,
      boolean particle_fog_random_y,
      boolean particle_fog_random_z,
      float particle_fog_direction_x,
      float particle_fog_direction_y,
      float particle_fog_direction_z,
      boolean particle_fog_shaders,
      int particle_fog_frequency_num,
      int particle_fog_frequency_ticks,
      float camera_fog_radius,
      int camera_fog_color_r,
      int camera_fog_color_g,
      int camera_fog_color_b,
      float camera_fog_strength,
      boolean camera_fog_toggle
   ) {
      this.pos = pos;
      this.particle_fog_size = particle_fog_size;
      this.particle_fog_color_r = particle_fog_color_r;
      this.particle_fog_color_g = particle_fog_color_g;
      this.particle_fog_color_b = particle_fog_color_b;
      this.particle_fog_color_a = particle_fog_color_a;
      this.particle_fog_thickness = particle_fog_thickness;
      this.particle_fog_speed = particle_fog_speed;
      this.particle_fog_random_x = particle_fog_random_x;
      this.particle_fog_random_y = particle_fog_random_y;
      this.particle_fog_random_z = particle_fog_random_z;
      this.particle_fog_direction_x = particle_fog_direction_x;
      this.particle_fog_direction_y = particle_fog_direction_y;
      this.particle_fog_direction_z = particle_fog_direction_z;
      this.particle_fog_shaders = particle_fog_shaders;
      this.particle_fog_frequency_num = particle_fog_frequency_num;
      this.particle_fog_frequency_ticks = particle_fog_frequency_ticks;
      this.camera_fog_radius = camera_fog_radius;
      this.camera_fog_color_r = camera_fog_color_r;
      this.camera_fog_color_g = camera_fog_color_g;
      this.camera_fog_color_b = camera_fog_color_b;
      this.camera_fog_strength = camera_fog_strength;
      this.camera_fog_toggle = camera_fog_toggle;
   }

   public void decode(FriendlyByteBuf buf) {
      this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      this.particle_fog_size = buf.readFloat();
      this.particle_fog_color_r = buf.readInt();
      this.particle_fog_color_g = buf.readInt();
      this.particle_fog_color_b = buf.readInt();
      this.particle_fog_color_a = buf.readInt();
      this.particle_fog_thickness = buf.readFloat();
      this.particle_fog_speed = buf.readFloat();
      this.particle_fog_random_x = buf.readBoolean();
      this.particle_fog_random_y = buf.readBoolean();
      this.particle_fog_random_z = buf.readBoolean();
      this.particle_fog_direction_x = buf.readFloat();
      this.particle_fog_direction_y = buf.readFloat();
      this.particle_fog_direction_z = buf.readFloat();
      this.particle_fog_shaders = buf.readBoolean();
      this.particle_fog_frequency_num = buf.readInt();
      this.particle_fog_frequency_ticks = buf.readInt();
      this.camera_fog_radius = buf.readFloat();
      this.camera_fog_color_r = buf.readInt();
      this.camera_fog_color_g = buf.readInt();
      this.camera_fog_color_b = buf.readInt();
      this.camera_fog_strength = buf.readFloat();
      this.camera_fog_toggle = buf.readBoolean();
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.pos.getX());
      buf.writeInt(this.pos.getY());
      buf.writeInt(this.pos.getZ());
      buf.writeFloat(this.particle_fog_size);
      buf.writeInt(this.particle_fog_color_r);
      buf.writeInt(this.particle_fog_color_g);
      buf.writeInt(this.particle_fog_color_b);
      buf.writeInt(this.particle_fog_color_a);
      buf.writeFloat(this.particle_fog_thickness);
      buf.writeFloat(this.particle_fog_speed);
      buf.writeBoolean(this.particle_fog_random_x);
      buf.writeBoolean(this.particle_fog_random_y);
      buf.writeBoolean(this.particle_fog_random_z);
      buf.writeFloat(this.particle_fog_direction_x);
      buf.writeFloat(this.particle_fog_direction_y);
      buf.writeFloat(this.particle_fog_direction_z);
      buf.writeBoolean(this.particle_fog_shaders);
      buf.writeInt(this.particle_fog_frequency_num);
      buf.writeInt(this.particle_fog_frequency_ticks);
      buf.writeFloat(this.camera_fog_radius);
      buf.writeInt(this.camera_fog_color_r);
      buf.writeInt(this.camera_fog_color_g);
      buf.writeInt(this.camera_fog_color_b);
      buf.writeFloat(this.camera_fog_strength);
      buf.writeBoolean(this.camera_fog_toggle);
   }

   public static final Type<FogProjectorUpdatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","fog_projector_update"));
   public static void handle(FogProjectorUpdatePacket message, ServerPlayer player) {
      if (!valid(message, player)) return;
      updateTileEntity(message,player.level());
   }
   private static boolean valid(FogProjectorUpdatePacket message, ServerPlayer player) {
      if (player.isSpectator() || message.pos == null || !player.level().hasChunkAt(message.pos) || player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(message.pos))>64 || !player.level().mayInteract(player,message.pos)) return false;
      return inRange(message.particle_fog_size,0.0F,30.0F)
         && inRange(message.particle_fog_color_r,0,255)
         && inRange(message.particle_fog_color_g,0,255)
         && inRange(message.particle_fog_color_b,0,255)
         && inRange(message.particle_fog_color_a,0,255)
         && inRange(message.particle_fog_thickness,0.0F,1.0F)
         && inRange(message.particle_fog_speed,0.0F,255.0F)
         && inRange(message.particle_fog_direction_x,-2.0F,2.0F)
         && inRange(message.particle_fog_direction_y,-2.0F,2.0F)
         && inRange(message.particle_fog_direction_z,-2.0F,2.0F)
         && inRange(message.particle_fog_frequency_num,0,100)
         && inRange(message.particle_fog_frequency_ticks,0,1000000)
         && inRange(message.camera_fog_radius,0.0F,(float)MTConfig.FOG_PROJECTOR_MAX_RADIUS)
         && inRange(message.camera_fog_color_r,0,255)
         && inRange(message.camera_fog_color_g,0,255)
         && inRange(message.camera_fog_color_b,0,255)
         && inRange(message.camera_fog_strength,0.0F,60);
   }

   private static boolean inRange(double value,double min,double max){return Double.isFinite(value) && value>=min && value<=max;}

   public static void updateTileEntity(FogProjectorUpdatePacket message, Level world) {
      if (world.getBlockEntity(message.pos) instanceof FogProjectorTileEntity) {
         FogProjectorTileEntity projector = (FogProjectorTileEntity)world.getBlockEntity(message.pos);
         if (projector != null) {
            projector.set_particle_fog_size(message.particle_fog_size);
            projector.set_particle_fog_color_r(message.particle_fog_color_r);
            projector.set_particle_fog_color_g(message.particle_fog_color_g);
            projector.set_particle_fog_color_b(message.particle_fog_color_b);
            projector.set_particle_fog_color_a(message.particle_fog_color_a);
            projector.set_particle_fog_thickness(message.particle_fog_thickness);
            projector.set_particle_fog_speed(message.particle_fog_speed);
            projector.set_particle_fog_random_x(message.particle_fog_random_x);
            projector.set_particle_fog_random_y(message.particle_fog_random_y);
            projector.set_particle_fog_random_z(message.particle_fog_random_z);
            projector.set_particle_fog_direction_x(message.particle_fog_direction_x);
            projector.set_particle_fog_direction_y(message.particle_fog_direction_y);
            projector.set_particle_fog_direction_z(message.particle_fog_direction_z);
            projector.set_particle_fog_shaders(message.particle_fog_shaders);
            projector.set_particle_fog_frequency_num(message.particle_fog_frequency_num);
            projector.set_particle_fog_frequency_ticks(message.particle_fog_frequency_ticks);
            projector.set_camera_fog_radius(message.camera_fog_radius);
            projector.set_camera_fog_color_r(message.camera_fog_color_r);
            projector.set_camera_fog_color_g(message.camera_fog_color_g);
            projector.set_camera_fog_color_b(message.camera_fog_color_b);
            projector.set_camera_fog_strength(message.camera_fog_strength);
            projector.set_camera_fog_toggle(message.camera_fog_toggle);
            projector.sendUpdates();
         }
      }
   }

   public Type<FogProjectorUpdatePacket> type() {
      return TYPE;
   }
}
