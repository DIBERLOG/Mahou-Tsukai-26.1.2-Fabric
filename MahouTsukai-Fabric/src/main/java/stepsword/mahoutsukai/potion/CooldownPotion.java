package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CooldownPotion extends MobEffect implements NoShowEffect {
   protected CooldownPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   @Override
   public boolean shouldShow() {
      return false;
   }
}
