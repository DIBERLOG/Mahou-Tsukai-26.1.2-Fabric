package stepsword.mahoutsukai.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.util.EffectUtil;

public class WallStickPotion extends CooldownPotion {
   protected WallStickPotion() {
   }

   public static void wallStickLivingTick(LivingEntity living) {
      if (EffectUtil.hasBuff(living, ModEffects.WALL_STICK)) {
         boolean onWallOrGround = onWallOrGround(living);
         if (onWallOrGround && !living.onGround()) {
            living.setDeltaMovement(0.0, living.getDeltaMovement().y * 0.03, 0.0);
         }
      }
   }

   public static boolean onWallOrGround(LivingEntity living) {
      BlockPos pos = living.blockPosition();
      boolean bottom = adjacentWall(pos, living.level());
      boolean top = adjacentWall(pos.above(), living.level());
      return bottom || top || living.onGround();
   }

   public static boolean adjacentWall(BlockPos pos, Level level) {
      boolean n = !level.getBlockState(pos.north()).isAir();
      boolean e = !level.getBlockState(pos.east()).isAir();
      boolean w = !level.getBlockState(pos.west()).isAir();
      boolean s = !level.getBlockState(pos.south()).isAir();
      return n || e || w || s;
   }
}
