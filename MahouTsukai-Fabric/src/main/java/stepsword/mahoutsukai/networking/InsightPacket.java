package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import stepsword.mahoutsukai.util.ByteBufUtils;

public class InsightPacket implements CustomPacketPayload {
   public static final Type<InsightPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","insight"));
   public CompoundTag inventory;
   public CompoundTag effects;
   public CompoundTag mainhand;
   public int id;
   public int mainhandindex;
   public boolean hasTarget;
   public boolean block;
   public static final StreamCodec<ByteBuf, InsightPacket> STREAM_CODEC = new StreamCodec<ByteBuf, InsightPacket>() {
      public InsightPacket decode(ByteBuf buf) {
         InsightPacket packet = new InsightPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, InsightPacket packet) {
         packet.encode(buf);
      }
   };

   public InsightPacket() {
   }

   public InsightPacket(CompoundTag inv, CompoundTag effects, CompoundTag mainhand, boolean hasTarget, boolean block, int eid, int mainhandindex) {
      this.inventory = inv;
      this.id = eid;
      this.block = block;
      this.hasTarget = hasTarget;
      this.effects = effects;
      this.mainhand = mainhand;
      this.mainhandindex = mainhandindex;
   }

   public void decode(ByteBuf buf) {
      this.id = buf.readInt();
      this.block = buf.readBoolean();
      this.hasTarget = buf.readBoolean();
      this.mainhandindex = buf.readInt();
      String inventoryString = ByteBufUtils.readUTF8String(buf);
      String effectString = ByteBufUtils.readUTF8String(buf);
      String mainhandString = ByteBufUtils.readUTF8String(buf);

      try {
         this.inventory = inventoryString == null ? new CompoundTag() : TagParser.parseCompoundFully(inventoryString);
      } catch (Exception e) {
         this.inventory = new CompoundTag();
      }

      try {
         this.effects = effectString == null ? new CompoundTag() : TagParser.parseCompoundFully(effectString);
      } catch (Exception e) {
         this.effects = new CompoundTag();
      }

      try {
         this.mainhand = mainhandString == null ? new CompoundTag() : TagParser.parseCompoundFully(mainhandString);
      } catch (Exception e) {
         this.mainhand = new CompoundTag();
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeBoolean(this.block);
      buf.writeBoolean(this.hasTarget);
      buf.writeInt(this.mainhandindex);
      ByteBufUtils.writeUTF8String(buf, this.inventory == null ? new CompoundTag().toString() : this.inventory.toString());
      ByteBufUtils.writeUTF8String(buf, this.effects == null ? new CompoundTag().toString() : this.effects.toString());
      ByteBufUtils.writeUTF8String(buf, this.mainhand == null ? new CompoundTag().toString() : this.mainhand.toString());
   }


   public Type<InsightPacket> type() {
      return TYPE;
   }
}
