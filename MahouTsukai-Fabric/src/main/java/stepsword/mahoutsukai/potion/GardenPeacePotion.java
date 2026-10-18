package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GardenPeacePotion extends MobEffect {
   protected GardenPeacePotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 225));
   }
}
