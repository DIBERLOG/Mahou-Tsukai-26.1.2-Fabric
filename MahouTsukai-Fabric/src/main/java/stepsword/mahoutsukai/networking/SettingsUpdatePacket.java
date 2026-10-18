package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.settingsmahou.ISettingsMahou;
import stepsword.mahoutsukai.util.Utils;

public class SettingsUpdatePacket implements CustomPacketPayload {
   int spell;
   List<Integer> valuesInt;
   public static final StreamCodec<ByteBuf, SettingsUpdatePacket> STREAM_CODEC = new StreamCodec<ByteBuf, SettingsUpdatePacket>() {
      public SettingsUpdatePacket decode(ByteBuf buf) {
         SettingsUpdatePacket packet = new SettingsUpdatePacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, SettingsUpdatePacket packet) {
         packet.encode(buf);
      }
   };

   public SettingsUpdatePacket() {
   }

   public SettingsUpdatePacket(int spell, List<Integer> valuesInt) {
      this.spell = spell;
      this.valuesInt = valuesInt;
   }

   public void decode(ByteBuf buf) {
      this.spell = buf.readInt();
      int sz = buf.readInt();
      if(sz<0||sz>6)throw new IllegalArgumentException("Invalid settings list length");
      this.valuesInt = new ArrayList<>();

      for (int i = 0; i < sz; i++) {
         this.valuesInt.add(buf.readInt());
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.spell);
      buf.writeInt(this.valuesInt.size());

      for (int i = 0; i < this.valuesInt.size(); i++) {
         buf.writeInt(this.valuesInt.get(i));
      }
   }

   public static final Type<SettingsUpdatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","settings_update"));
   public static void handle(SettingsUpdatePacket message,net.minecraft.server.level.ServerPlayer player){
      var spells=stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.values();
      if(message.spell<0||message.spell>=spells.length||message.valuesInt==null)return;
      var spell=spells[message.spell];int count=(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.hasPrimary(spell)?3:0)+(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.hasSecondary(spell)?3:0);
      if(message.valuesInt.size()!=count||message.valuesInt.stream().anyMatch(v->v==null||v<0||v>255))return;
      var values=new java.util.ArrayList<>(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.getIntDefaults().get(message.spell));for(int i=0;i<count;i++)values.set(i,message.valuesInt.get(i));
      Utils.getSettingsMahou(player).setIntSettings(message.spell,values);
      stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(player);
   }

   public Type<SettingsUpdatePacket> type() {
      return TYPE;
   }
}
