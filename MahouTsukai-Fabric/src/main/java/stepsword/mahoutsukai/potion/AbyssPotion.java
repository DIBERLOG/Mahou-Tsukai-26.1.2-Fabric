package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class AbyssPotion extends MobEffect implements ExtraPotionInterface {
   protected AbyssPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   public static void notifyIfAbyss(LivingEntity e) {
   }

   public static void notifyNotAbyss(LivingEntity e) {
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
      }
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      notifyNotAbyss(livingEntity);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      notifyIfAbyss(livingEntity);
   }
}
