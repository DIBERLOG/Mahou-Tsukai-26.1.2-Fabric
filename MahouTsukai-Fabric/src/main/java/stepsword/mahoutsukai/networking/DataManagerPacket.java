package stepsword.mahoutsukai.networking;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;

import stepsword.mahoutsukai.util.Utils;

public class DataManagerPacket implements CustomPacketPayload {
   public static final Type<DataManagerPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","data_manager"));
   public BlockPos pos;
   public List<DataValue<?>> dataManagerEntries;
   public static final StreamCodec<RegistryFriendlyByteBuf, DataManagerPacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DataManagerPacket>() {
      public DataManagerPacket decode(RegistryFriendlyByteBuf buf) {
         DataManagerPacket packet = new DataManagerPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(RegistryFriendlyByteBuf buf, DataManagerPacket packet) {
         packet.encode(buf);
      }
   };

   public DataManagerPacket() {
   }

   public DataManagerPacket(BlockPos pos, List<DataValue<?>> dataManagerEntries) {
      this.pos = pos;
      this.dataManagerEntries = dataManagerEntries;
   }

   public void decode(RegistryFriendlyByteBuf buf) {
      this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

      try {
         List<DataValue<?>> list = new ArrayList<>();

         int i;
         while ((i = buf.readUnsignedByte()) != 255) {
            list.add(DataValue.read(buf, i));
         }

         this.dataManagerEntries = list;
      } catch (Exception e) {
         Utils.err(e.toString());
      }
   }

   public void encode(RegistryFriendlyByteBuf buf) {
      buf.writeInt(this.pos.getX());
      buf.writeInt(this.pos.getY());
      buf.writeInt(this.pos.getZ());

      for (DataValue<?> datavalue : this.dataManagerEntries) {
         datavalue.write(buf);
      }

      buf.writeByte(255);
   }


   public Type<DataManagerPacket> type() {
      return TYPE;
   }
}
