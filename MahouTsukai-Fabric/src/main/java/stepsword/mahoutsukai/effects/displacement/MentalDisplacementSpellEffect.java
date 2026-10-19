package stepsword.mahoutsukai.effects.displacement;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
      if (!player.level().addFreshEntity(entity)) return false;
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

   /**
    * Moves the body to where the controlled spirit stopped. The destination is re-validated on the
    * server and clamped to the legal travel radius, so a stale or forged packet cannot move the
    * player into a wall, out of the world, into an unloaded chunk or further than allowed.
    */
   public static void arrive(Player player, Vec3 target) {
      if (!(player instanceof ServerPlayer server) || target == null) return;
      if (!Double.isFinite(target.x) || !Double.isFinite(target.y) || !Double.isFinite(target.z)) return;
      ServerLevel level = (ServerLevel)server.level();
      Vec3 safe = safePoint(level, target);
      if (safe == null) {
         server.sendOverlayMessage(Component.translatable("mahoutsukai.mental.blocked"));
         return;
      }
      Vec3 origin = server.position();
      int range = MTConfig.MENTAL_DISPLACEMENT_RANGE;
      Vec3 clamped = new Vec3(
         Mth.clamp(safe.x, origin.x - range, origin.x + range),
         Mth.clamp(safe.y, origin.y - range, origin.y + range),
         Mth.clamp(safe.z, origin.z - range, origin.z + range)
      );
      Vec3 finalPoint = safePoint(level, clamped);
      if (finalPoint == null) {
         server.sendOverlayMessage(Component.translatable("mahoutsukai.mental.blocked"));
         return;
      }
      server.teleportTo(finalPoint.x, finalPoint.y, finalPoint.z);
      server.sendOverlayMessage(Component.translatable("mahoutsukai.mental.arrived"));
   }

   /** First position near {@code target} with two free blocks and support below, or {@code null}. */
   private static Vec3 safePoint(ServerLevel level, Vec3 target) {
      int x = Mth.floor(target.x);
      int z = Mth.floor(target.z);
      int minY = level.getMinY() + 1;
      int maxY = level.getMaxY() - 2;
      if (minY >= maxY) return null;
      int y = Mth.clamp(Mth.floor(target.y), minY, maxY);
      BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
      for (int step = 0; step <= 8; step++) {
         int[] signs = step == 0 ? new int[]{0} : new int[]{-1, 1};
         for (int sign : signs) {
            int candidateY = Mth.clamp(y + sign * step, minY, maxY);
            cursor.set(x, candidateY, z);
            if (!level.hasChunkAt(cursor)) continue;
            if (standable(level, cursor)) return new Vec3(x + 0.5, candidateY, z + 0.5);
         }
      }
      return null;
   }

   private static boolean standable(ServerLevel level, BlockPos pos) {
      if(!level.getWorldBorder().isWithinBounds(pos))return false;
      if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) return false;
      return !level.getBlockState(pos.below()).getCollisionShape(level,pos.below()).isEmpty();
   }

   public static int checkDistance(BlockPos a, BlockPos b) {
      return a != null && b != null ? Math.max(Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())), Math.abs(a.getZ() - b.getZ())) : 0;
   }
}
