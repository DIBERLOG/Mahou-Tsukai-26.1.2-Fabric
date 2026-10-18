package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.Utils;

public class DonePossessingPacket implements CustomPacketPayload {
   public static final Type<DonePossessingPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","done_possessing"));
   public static final StreamCodec<ByteBuf, DonePossessingPacket> STREAM_CODEC = StreamCodec.unit(new DonePossessingPacket());


   public static void handleDonePossess(DonePossessingPacket message, Player player) {
      if (player != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            mahou.setPossessing(false);
            if(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(serverPlayer,mahou);
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof DonePossessingPacket;
   }

   public Type<DonePossessingPacket> type() {
      return TYPE;
   }
}
