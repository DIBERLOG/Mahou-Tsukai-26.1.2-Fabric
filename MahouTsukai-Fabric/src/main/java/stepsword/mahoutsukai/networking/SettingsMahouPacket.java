package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import stepsword.mahoutsukai.dataattachments.settingsmahou.ISettingsMahou;
import stepsword.mahoutsukai.dataattachments.settingsmahou.SettingsMahou;
import stepsword.mahoutsukai.util.Utils;

public class SettingsMahouPacket implements CustomPacketPayload {
   public static final Type<SettingsMahouPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","settings_mahou"));
   public ISettingsMahou mahou;
   public static final StreamCodec<ByteBuf, SettingsMahouPacket> STREAM_CODEC = new StreamCodec<ByteBuf, SettingsMahouPacket>() {
      public SettingsMahouPacket decode(ByteBuf buf) {
         SettingsMahouPacket packet = new SettingsMahouPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, SettingsMahouPacket packet) {
         packet.encode(buf);
      }
   };

   public SettingsMahouPacket() {
   }

   public SettingsMahouPacket(ISettingsMahou mahou) {
      this.mahou = mahou;
   }

   public void decode(ByteBuf buf) {
      this.mahou = new SettingsMahou();
      int sz = buf.readInt();

      for (int i = 0; i < sz; i++) {
         this.mahou.setIntSettings(buf.readInt(), Utils.readIntListFromBuffer(buf));
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.mahou.getIntSettingsKeySet().size());

      for (Integer i : this.mahou.getIntSettingsKeySet()) {
         buf.writeInt(i);
         Utils.writeIntListToBuffer(buf, this.mahou.getIntSettings(i));
      }
   }


   public Type<SettingsMahouPacket> type() {
      return TYPE;
   }
}
