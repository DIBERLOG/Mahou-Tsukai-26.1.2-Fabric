package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RevertedPotion extends MobEffect implements ExtraPotionInterface {
   protected RevertedPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(242, 7, 23));
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      ReversionEyesPotion.notifyNotReverted(livingEntity);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
   }
}
