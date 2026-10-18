package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import stepsword.mahoutsukai.config.MTConfig;

public class RipperCooldownPotion extends MobEffect implements NoShowEffect {
   protected RipperCooldownPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   @Override
   public boolean shouldShow() {
      return MTConfig.SHOW_MORGAN_CALIBURN_COOLDOWN;
   }
}
