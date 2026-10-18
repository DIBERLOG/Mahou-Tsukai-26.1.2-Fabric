package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;

import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class WoundedPotion extends MobEffect {
   protected WoundedPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   public static void woundedLivingTick(LivingEntity player) {
      if (!player.level().isClientSide() && player.level().getGameTime() % 10L == 0L && EffectUtil.hasBuff(player, ModEffects.WOUNDED)) {
         ILivingMahou mahou = Utils.getLivingMahou(player);
         if (mahou != null) {
            Vec3 v = mahou.getLastWoundedPos();
            mahou.setLastWoundedPos(player.position());
            if (v != null) {
               double d2 = player.position().distanceToSqr(v);

               for (int i = 0; i < MTConfig.CLARENT_WOUND_DAMAGE_HITS; i++) {
                  if (d2 > 3.0) {
                     EffectUtil.genericAttack(player, (float)MTConfig.CLARENT_WOUND_DAMAGE, player.damageSources().generic(), player);
                  }
               }
            }
         }
      }
   }
}
