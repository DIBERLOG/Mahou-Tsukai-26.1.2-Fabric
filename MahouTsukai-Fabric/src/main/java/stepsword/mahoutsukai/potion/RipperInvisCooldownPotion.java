package stepsword.mahoutsukai.potion;

import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;

public class RipperInvisCooldownPotion extends CooldownPotion implements ExtraPotionInterface {
   protected RipperInvisCooldownPotion() {
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player
         && livingEntity.getOffhandItem().getItem() == livingEntity.getMainHandItem().getItem()
         && livingEntity.getMainHandItem().getItem() == ModItems.theripper.get()) {
         int manaCost = MTConfig.RIPPER_INVIS_MANA_COST;
         if (PlayerManaManager.drainMana(player, manaCost, false, false) == manaCost) {
            EffectUtil.buff(livingEntity, ModEffects.RIPPER_INVISIBILITY, false, 2000000000, false);
            RipperInvisPotion.notifyIfInvisible(livingEntity);
         }
      }
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
   }

   public static void nudge(LivingEntity livingEntity) {
      if (!livingEntity.level().isClientSide()) {
         float radius = MTConfig.RIPPER_FOG_RANGE;
         Vec3 pos = livingEntity.position();
         AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);

         for (LivingEntity e : livingEntity.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (e.getLastHurtByMob() == livingEntity) {
               e.setLastHurtByMob(null);
            }

            if (e.getLastHurtByPlayer() == livingEntity) {
               e.setLastHurtByPlayer((UUID)null, 0);
            }

            if (e instanceof Mob && ((Mob)e).getTarget() == livingEntity) {
               ((Mob)e).setTarget(null);
            }
         }
      }
   }
}
