package stepsword.mahoutsukai.potion;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.item.rulebreaker.ProbabilityAlterRandom;
import stepsword.mahoutsukai.util.EffectUtil;

public class ProbabilityAlterPotion extends MobEffect {
   protected ProbabilityAlterPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   public static void applyProbabilityAlter(LivingEntity target) {
      boolean isPA = EffectUtil.hasBuff(target, ModEffects.PROBABILITY_ALTER);
      if (target.getRandom() instanceof ProbabilityAlterRandom && !isPA) {
         target.random = RandomSource.create(target.level().getRandom().nextInt());
      }
   }

   public static void createProbabilityAlter(LivingEntity target, int designation, int modulus) {
      target.random = new ProbabilityAlterRandom(designation, modulus);
   }
}
