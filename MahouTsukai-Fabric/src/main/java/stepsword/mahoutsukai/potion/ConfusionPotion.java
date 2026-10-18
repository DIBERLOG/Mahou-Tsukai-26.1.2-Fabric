package stepsword.mahoutsukai.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

import stepsword.mahoutsukai.util.EffectUtil;

public class ConfusionPotion extends MobEffect {
   protected ConfusionPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(22, 22, 22));
   }

   public static void confusionLivingUpdate(LivingEntity living) {
      if (living.level().getGameTime() % 20L == 0L && EffectUtil.hasBuff(living, ModEffects.CONFUSION) && !(living instanceof Player)) {
         living.setLastHurtByMob(null);
         if (living instanceof Mob) {
            ((Mob)living).setTarget(null);
         }

         if (living instanceof PathfinderMob) {
            ((PathfinderMob)living)
               .getNavigation()
               .moveTo(
                  living.getX() + living.getRandom().nextInt(20) - 10.0,
                  living.getY() + living.getRandom().nextInt(10) - 5.0,
                  living.getZ() + living.getRandom().nextInt(20) - 10.0,
                  2.0
               );
         }
      }
   }
}
