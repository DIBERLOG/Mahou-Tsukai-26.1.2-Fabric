package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import stepsword.mahoutsukai.enchant.MahoujinProjectorEnchant;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class ProjectorRightClickPacket implements CustomPacketPayload {
   ProjectorRightClickPacket.ARMOR_ACTION action;
   BlockPos pos;
   public static final StreamCodec<ByteBuf, ProjectorRightClickPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ProjectorRightClickPacket>() {
      public ProjectorRightClickPacket decode(ByteBuf buf) {
         ProjectorRightClickPacket packet = new ProjectorRightClickPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, ProjectorRightClickPacket packet) {
         packet.encode(buf);
      }
   };

   public ProjectorRightClickPacket() {
   }

   public ProjectorRightClickPacket(ProjectorRightClickPacket.ARMOR_ACTION action, BlockPos pos) {
      this.action = action;
      this.pos = pos;
   }

   public void decode(ByteBuf buf) {
      this.action = readAction(buf.readInt());
      this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.action.ordinal());
      buf.writeInt(this.pos.getX());
      buf.writeInt(this.pos.getY());
      buf.writeInt(this.pos.getZ());
   }

   public static final Type<ProjectorRightClickPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","projector_right_click"));
   private static ARMOR_ACTION readAction(int value){if(value<0||value>=ARMOR_ACTION.values().length)throw new IllegalArgumentException("Invalid projector armor action");return ARMOR_ACTION.values()[value];}
   public static void handle(ProjectorRightClickPacket message,ServerPlayer player){
      if(player.isSpectator()||message.action==null||message.pos==null||!player.level().hasChunkAt(message.pos)||!player.level().mayInteract(player,message.pos)||player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(message.pos))>64)return;
      if(player.level().getBlockEntity(message.pos) instanceof MahoujinProjectorTileEntity projector && stepsword.mahoutsukai.util.Utils.isArmor(player.getMainHandItem()))MahoujinProjectorEnchant.setProjectorNBT(player.getMainHandItem(),projector,message.action);
   }

   public Type<ProjectorRightClickPacket> type() {
      return TYPE;
   }

   public enum ARMOR_ACTION {
      REPLACE,
      ADD,
      REMOVE;
   }
}
