package stepsword.mahoutsukai.effects.displacement;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.Utils;

public class ProtectiveDisplacementSpellEffect {
   public static boolean addProtectionToUser(Player player) {
      if (player != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            mahou.setProtectiveDisplacement(mahou.getProtectiveDisplacement() + 5);
            return true;
         }
      }

      return false;
   }

   public static boolean protectiveDisplacementProjectileImpact(net.minecraft.world.entity.projectile.Projectile projectile, net.minecraft.world.phys.HitResult hit) {
      if (!projectile.level().isClientSide()
         && hit != null
         && hit instanceof EntityHitResult
         && ((EntityHitResult)hit).getEntity() instanceof Player) {
         Player player = (Player)((EntityHitResult)hit).getEntity();
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            int protD = mahou.getProtectiveDisplacement();
            if (protD > 0) {
               int i = 0;

               while (i < 256 && !teleportRandomly(player, player.level())) {
                  i++;
               }

               mahou.setProtectiveDisplacement(protD - 1);
               return true;
            }
         }
      }

      return false;
   }

   protected static boolean teleportRandomly(Player player, Level world) {
      RandomSource rand = player.getRandom();
      double d0 = player.getX() + (rand.nextDouble() - 0.5) * 6.0;
      double d1 = player.getY() + (rand.nextInt(4) - 2);
      double d2 = player.getZ() + (rand.nextDouble() - 0.5) * 6.0;
      return teleportTo(d0, d1, d2, player, world);
   }

   private static boolean teleportTo(double x, double y, double z, Player player, Level world) {
      boolean flag = attemptTeleport(x, y, z, player, world);
      if (flag) {
         world.playSound(null, player.xo, player.yo, player.zo, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      }

      return flag;
   }

   public static boolean attemptTeleport(double x, double y, double z, Player player, Level world) {
      double d0 = x;
      double d1 = y;
      double d2 = z;
      BlockPos bp = Utils.toBlockPos(new Vec3(d0, d1, d2));
      if (Utils.isBlockAir(world, bp.offset(0, 1, 0)) && Utils.isBlockAir(world, bp.offset(0, 2, 0)) && !Utils.isBlockAir(world, bp)) {
         MahouTsukaiTeleporter.teleport(player, d0, d1 + 1.0, d2, EffectUtil.getDimension(player.level()));
         return true;
      } else {
         return false;
      }
   }
}
