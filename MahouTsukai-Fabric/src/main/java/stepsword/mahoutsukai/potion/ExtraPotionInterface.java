package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public interface ExtraPotionInterface {
   void onRemove(LivingEntity var1, MobEffect var2);

   void onAdd(LivingEntity var1, MobEffect var2);
}
