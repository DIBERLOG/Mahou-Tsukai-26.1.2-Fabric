package stepsword.mahoutsukai.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;
import stepsword.mahoutsukai.networking.DonePossessingPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.potion.BoundPotion;
import stepsword.mahoutsukai.util.Utils;

public class PossessEntityClientEffect {
   public static Entity possessionHold = null;

   public static void possessEntityInputUpdate(ClientInput mi, Player player) {
      if (player != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null && mahou.isPossessing() && isActuallyPossessing(player)) {
            mi.keyPresses = BoundPotion.STANDING_STILL;
         }
      }
   }

   public static boolean possessEntityRenderHand() {
      IMahou mahou = Utils.getPlayerMahou(Minecraft.getInstance().player);
      return mahou != null && mahou.isPossessing() && isActuallyPossessing(Minecraft.getInstance().player);
   }

   public static void possessEntityMouseInput() {
      Player player = Minecraft.getInstance().player;
      IMahou mahou = Utils.getPlayerMahou(Minecraft.getInstance().player);
      if (mahou != null && mahou.isPossessing() && isActuallyPossessing(player)) {
         KeyMapping k = Minecraft.getInstance().options.keyAttack;
         if (k.consumeClick()) {
            k.setDown(false);
            possessEntityDisallowedAction(Minecraft.getInstance().player);
         }
      }
   }

   public static boolean isActuallyPossessing(Player player) {
      return Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity
         ? false
         : !(Minecraft.getInstance().getCameraEntity() instanceof Player) || !Minecraft.getInstance().getCameraEntity().getUUID().equals(player.getUUID());
   }

   public static boolean possessEntityDisallowedAction(Player player) {
      boolean ret = false;
      if (player != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (player.level().isClientSide()) {
            if (mahou != null && mahou.isPossessing()) {
               Entity entity = Minecraft.getInstance().getCameraEntity();
               Minecraft.getInstance().setCameraEntity(player);
               if (entity != null) {
                  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new DonePossessingPacket());
                  ret = true;
               }
            }
         } else if (mahou != null && mahou.isPossessing()) {
            ret = true;
         }
      }

      return ret;
   }

}
