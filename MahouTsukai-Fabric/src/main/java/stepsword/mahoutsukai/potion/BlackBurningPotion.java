package stepsword.mahoutsukai.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BlackBurningPotion extends MobEffect implements ExtraPotionInterface {
   public static Identifier effectIcon = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/effects/black_burning.png");

   protected BlackBurningPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(116, 232, 0));
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      BlackFlameEyesPotion.notifyNotBurning(livingEntity);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      BlackFlameEyesPotion.notifyIfBurning(livingEntity);
   }
}
