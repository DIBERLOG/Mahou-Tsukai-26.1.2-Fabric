package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ScrollPotion extends MobEffect implements NoShowEffect {
   protected ScrollPotion(int liquidColorIn) {
      super(MobEffectCategory.NEUTRAL, liquidColorIn);
   }

   @Override
   public boolean shouldShow() {
      return true;
   }
}
