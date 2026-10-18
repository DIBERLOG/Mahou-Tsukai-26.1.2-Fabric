package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class MahoujinProjectorUpdatePacket implements CustomPacketPayload {
   int r;
   int g;
   int b;
   int a;
   int image;
   int runes;
   float x;
   float y;
   float z;
   float size;
   float speed;
   float yaw;
   float pitch;
   float ring;
   float height;
   float yspeed;
   float pspeed;
   float roffset;
   float orbitspeed;
   float prepitch;
   float preyaw;
   float gifspeed;
   boolean sc;
   boolean sr;
   boolean gif_layer;
   BlockPos pos;
   public static final StreamCodec<ByteBuf, MahoujinProjectorUpdatePacket> STREAM_CODEC = new StreamCodec<ByteBuf, MahoujinProjectorUpdatePacket>() {
      public MahoujinProjectorUpdatePacket decode(ByteBuf buf) {
         MahoujinProjectorUpdatePacket packet = new MahoujinProjectorUpdatePacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, MahoujinProjectorUpdatePacket packet) {
         packet.encode(buf);
      }
   };

   public MahoujinProjectorUpdatePacket() {
   }

   public MahoujinProjectorUpdatePacket(
      int r,
      int g,
      int b,
      int a,
      float x,
      float y,
      float z,
      float size,
      float speed,
      float yaw,
      float pitch,
      float ring,
      boolean sc,
      boolean sr,
      BlockPos pos,
      int image,
      int runes,
      float height,
      float yspeed,
      float pspeed,
      float roffset,
      float prepitch,
      float preyaw,
      float orbitspeed,
      float gifspeed,
      boolean gif_layer
   ) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.x = x;
      this.y = y;
      this.z = z;
      this.size = size;
      this.speed = speed;
      this.yaw = yaw;
      this.pitch = pitch;
      this.ring = ring;
      this.pos = pos;
      this.sc = sc;
      this.sr = sr;
      this.image = image;
      this.runes = runes;
      this.height = height;
      this.yspeed = yspeed;
      this.pspeed = pspeed;
      this.roffset = roffset;
      this.prepitch = prepitch;
      this.preyaw = preyaw;
      this.orbitspeed = orbitspeed;
      this.gifspeed = gifspeed;
      this.gif_layer = gif_layer;
   }

   public void decode(ByteBuf buf) {
      this.r = buf.readInt();
      this.g = buf.readInt();
      this.b = buf.readInt();
      this.a = buf.readInt();
      this.x = buf.readFloat();
      this.y = buf.readFloat();
      this.z = buf.readFloat();
      this.size = buf.readFloat();
      this.speed = buf.readFloat();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.ring = buf.readFloat();
      this.sc = buf.readBoolean();
      this.sr = buf.readBoolean();
      this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      this.image = buf.readInt();
      this.runes = buf.readInt();
      this.height = buf.readFloat();
      this.yspeed = buf.readFloat();
      this.pspeed = buf.readFloat();
      this.roffset = buf.readFloat();
      this.orbitspeed = buf.readFloat();
      this.preyaw = buf.readFloat();
      this.prepitch = buf.readFloat();
      this.gifspeed = buf.readFloat();
      this.gif_layer = buf.readBoolean();
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.r);
      buf.writeInt(this.g);
      buf.writeInt(this.b);
      buf.writeInt(this.a);
      buf.writeFloat(this.x);
      buf.writeFloat(this.y);
      buf.writeFloat(this.z);
      buf.writeFloat(this.size);
      buf.writeFloat(this.speed);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeFloat(this.ring);
      buf.writeBoolean(this.sc);
      buf.writeBoolean(this.sr);
      buf.writeInt(this.pos.getX());
      buf.writeInt(this.pos.getY());
      buf.writeInt(this.pos.getZ());
      buf.writeInt(this.image);
      buf.writeInt(this.runes);
      buf.writeFloat(this.height);
      buf.writeFloat(this.yspeed);
      buf.writeFloat(this.pspeed);
      buf.writeFloat(this.roffset);
      buf.writeFloat(this.orbitspeed);
      buf.writeFloat(this.preyaw);
      buf.writeFloat(this.prepitch);
      buf.writeFloat(this.gifspeed);
      buf.writeBoolean(this.gif_layer);
   }

   public static final Type<MahoujinProjectorUpdatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mahoujin_projector_update"));
   public static void handle(MahoujinProjectorUpdatePacket message, ServerPlayer player) {
      if (!valid(message, player)) return;
      updateTileEntity(message,player.level(),player);
   }
   private static boolean valid(MahoujinProjectorUpdatePacket message, ServerPlayer player) {
      if (player.isSpectator() || message.pos == null || !player.level().hasChunkAt(message.pos) || player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(message.pos))>64 || !player.level().mayInteract(player,message.pos)) return false;
      float offsetcap=(float)Math.abs(stepsword.mahoutsukai.config.MTConfig.PROJECTOR_OFFSET_CAP),sizecap=(float)Math.abs(stepsword.mahoutsukai.config.MTConfig.PROJECTOR_SIZE_CAP);
      return inRange(message.r,0,255)
         && inRange(message.g,0,255)
         && inRange(message.b,0,255)
         && inRange(message.a,0,255)
         && inRange(message.x,-offsetcap,offsetcap)
         && inRange(message.y,-offsetcap,offsetcap)
         && inRange(message.z,-offsetcap,offsetcap)
         && inRange(message.size,0,sizecap)
         && inRange(message.speed,-360,360)
         && inRange(message.yaw,0,360)
         && inRange(message.pitch,0,360)
         && inRange(message.ring,0,90)
         && inRange(message.height,-200,200)
         && inRange(message.image,-1,255)
         && inRange(message.runes,-1,255)
         && inRange(message.yspeed,-360,360)
         && inRange(message.pspeed,-360,360)
         && inRange(message.roffset,-360,360)
         && inRange(message.orbitspeed,-360,360)
         && inRange(message.prepitch,0,360)
         && inRange(message.preyaw,0,360)
         && inRange(message.gifspeed,-360,360);
   }

   private static boolean inRange(double value,double min,double max){return Double.isFinite(value) && value>=min && value<=max;}

   public static void updateTileEntity(MahoujinProjectorUpdatePacket message, Level world, Player player) {
      if (world.getBlockEntity(message.pos) instanceof MahoujinProjectorTileEntity) {
         MahoujinProjectorTileEntity projector = (MahoujinProjectorTileEntity)world.getBlockEntity(message.pos);
         if (projector != null && MahoujinProjectorTileEntity.canEdit(player, projector)) {
            projector.pv.cr = message.r;
            projector.pv.cg = message.g;
            projector.pv.cb = message.b;
            projector.pv.ca = message.a;
            projector.pv.cx = message.x;
            projector.pv.cy = message.y;
            projector.pv.cz = message.z;
            projector.pv.csize = message.size;
            projector.pv.cspeed = message.speed;
            projector.pv.cyawspeed = message.yspeed;
            projector.pv.cpitspeed = message.pspeed;
            projector.pv.cyaw = message.yaw;
            projector.pv.cpitch = message.pitch;
            projector.pv.cringangle = message.ring;
            projector.pv.showRing = message.sr;
            projector.pv.showCircle = message.sc;
            projector.pv.image = message.image;
            projector.pv.runes = message.runes;
            projector.pv.height = message.height;
            projector.pv.rotationoffset = message.roffset;
            projector.pv.corbitspeed = message.orbitspeed;
            projector.pv.cpreyaw = message.preyaw;
            projector.pv.cprepitch = message.prepitch;
            projector.pv.gif_speed = message.gifspeed;
            projector.pv.gif_layer = message.gif_layer;
            projector.sendUpdates();
         }
      }
   }

   public Type<MahoujinProjectorUpdatePacket> type() {
      return TYPE;
   }
}
