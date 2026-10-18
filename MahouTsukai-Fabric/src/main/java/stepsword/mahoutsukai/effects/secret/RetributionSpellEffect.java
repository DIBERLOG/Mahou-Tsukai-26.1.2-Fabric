package stepsword.mahoutsukai.effects.secret;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class RetributionSpellEffect {
   public static boolean retribution(Player user) {
      Entity target = Utils.selectEntityNearCursor(
         user,
         MTConfig.GLOBAL_LOOK_RANGE,
         user.level(),
         a -> a instanceof LivingEntity, true
      );
      if (target instanceof LivingEntity living) {
         float h = living.getHealth();
         float mh = living.getMaxHealth();
         float hu = user.getHealth();
         float mhu = user.getMaxHealth();
         float new_user_health = h * mhu / mh;
         float new_target_health = hu * mh / mhu;
         int mana = MTConfig.RETRIBUTION_MANA_PER_DIFFERENCE;
         if (MTConfig.RETRIBUTION_MANA_CHARGED_PER_PERCENT) {
            mana = (int)(mana * Math.abs(h / mh - hu / mhu));
         } else {
            mana = (int)(mana * Math.abs(new_target_health - new_user_health));
         }

         boolean found = false;
         if (net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()) != null) {
            String regname = Utils.getRegistryName(target);

            for (int i = 0; i < MTConfig.RETRIBUTION_BLACKLIST.size(); i++) {
               if (regname.equals(MTConfig.RETRIBUTION_BLACKLIST.get(i))) {
                  found = true;
               }
            }

            if (!found
               && target.isAlive()
               && user.isAlive()
               && new_user_health > 0.0F
               && new_target_health > 0.0F
               && PlayerManaManager.drainMana(user, mana, false, false) == mana) {
               living.setHealth(new_target_health);
               user.setHealth(new_user_health);
               return true;
            }
         }
      }

      return false;
   }
}
