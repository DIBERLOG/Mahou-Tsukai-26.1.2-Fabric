package stepsword.mahoutsukai.potion;

import stepsword.mahoutsukai.config.MTConfig;

public class CaliburnMorganCooldownPotion extends CooldownPotion implements NoShowEffect {
   protected CaliburnMorganCooldownPotion() {
   }

   @Override
   public boolean shouldShow() {
      return MTConfig.SHOW_MORGAN_CALIBURN_COOLDOWN;
   }
}
