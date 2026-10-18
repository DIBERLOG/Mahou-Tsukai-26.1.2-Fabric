package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import stepsword.mahoutsukai.config.MTConfig;

public class BleedingPotion extends MobEffect implements NoShowEffect {
   protected BleedingPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   @Override
   public boolean shouldShow() {
      return MTConfig.SHOW_BLEEDING;
   }
}
