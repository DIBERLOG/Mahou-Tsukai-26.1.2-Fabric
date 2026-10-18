package stepsword.mahoutsukai.potion;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.Leylines;
import stepsword.mahoutsukai.util.EffectUtil;

public class FaySightEyesPotion extends EyesPotion {
   protected FaySightEyesPotion() {
      super(ModEffects.getColorNumber(116, 232, 0));
   }

   public static void faySightEyesPlayerTick(Player player) {
      if (player.isFallFlying()
         && Leylines.leyDimensionValid(EffectUtil.getDimension(player.level()))
         && Leylines.leyOrientation(player.blockPosition()) != Leylines.LeyLineShape.NONE
         && (!MTConfig.LEY_ELYTRA_LIMITED_TO_FAY_SIGHT || EffectUtil.hasBuff(player, ModEffects.FAY_SIGHT_EYES))) {
         Vec3 Vec3 = player.getLookAngle();
         Vec3 mot = player.getDeltaMovement();
         double motionX = mot.x;
         double motionY = mot.y;
         double motionZ = mot.z;
         motionX += Vec3.x * 0.1 + (Vec3.x * 1.5 - motionX) * 0.5;
         motionY += Vec3.y * 0.1 + (Vec3.y * 1.5 - motionY) * 0.5;
         motionZ += Vec3.z * 0.1 + (Vec3.z * 1.5 - motionZ) * 0.5;
         player.setDeltaMovement(motionX, motionY, motionZ);
         player.hurtMarked = true;
      }
   }
}
