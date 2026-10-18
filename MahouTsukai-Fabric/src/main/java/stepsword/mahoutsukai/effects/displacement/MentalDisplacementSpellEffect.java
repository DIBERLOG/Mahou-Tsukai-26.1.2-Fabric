package stepsword.mahoutsukai.effects.displacement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.DoneMentalPacket;
import stepsword.mahoutsukai.util.Utils;

public class MentalDisplacementSpellEffect {
   public static boolean leaveBody(Player player) {
      MentalDisplacementEntity entity = new MentalDisplacementEntity(
         player.level(), Utils.toBlockPos(new Vec3(player.getX(), player.getY(), player.getZ())), player
      );
      entity.setOwnerId(player);
      player.level().addFreshEntity(entity);
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         mahou.setMentalDisplacementPos(Utils.toBlockPos(new Vec3(player.getX(), player.getY(), player.getZ())));
         if (player instanceof ServerPlayer) {
            PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
         }
      }

      return true;
   }

   public static boolean mentalDisplacementServer(Player player) {
      boolean ret = false;
      if (player != null) {
         ret = true;
         DoneMentalPacket.onMessage(null, player);
      }

      return ret;
   }

   public static void mentalDisplacementLivingUpdate(Entity e) {
      if (e instanceof Player) {
         IMahou mahou = Utils.getPlayerMahou((Player)e);
         if (mahou != null
            && mahou.getMentalDisplacementPos() != null
            && checkDistance(Utils.toBlockPos(new Vec3(e.getX(), e.getY(), e.getZ())), mahou.getMentalDisplacementPos()) >= MTConfig.MENTAL_DISPLACEMENT_RANGE
            && !e.level().isClientSide()) {
            mahou.setMentalDisplacementPos(null);
         }
      }
   }

   public static int checkDistance(BlockPos a, BlockPos b) {
      return a != null && b != null ? Math.max(Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())), Math.abs(a.getZ() - b.getZ())) : 0;
   }
}
