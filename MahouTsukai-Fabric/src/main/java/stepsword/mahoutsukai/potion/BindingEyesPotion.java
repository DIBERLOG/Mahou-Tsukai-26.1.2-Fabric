package stepsword.mahoutsukai.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;

public class BindingEyesPotion extends EyesPotion {
   protected BindingEyesPotion() {
      super(ModEffects.getColorNumber(252, 45, 225));
   }

   public static void bindingEyesPlayerTick(Player player) {
      if (EffectUtil.hasBuff(player, ModEffects.BINDING_EYES)) {
         addBindings(player);
      }

      if (isBound(player) && !continueBinding(player)) {
         EffectUtil.debuff(player, ModEffects.BOUND);
      }
   }

   public static boolean bindingEyesLivingUpdate(LivingEntity entity) {
      boolean ret = false;
      Level world = entity.level();
      if (EffectUtil.hasBuff(entity, ModEffects.BOUND)) {
         entity.swinging = false;
         if (!world.isClientSide()) {
            if (!(entity instanceof Player)) {
               ret = true;
            }

            entity.invulnerableTime--;
            if (!continueBinding(entity)) {
               EffectUtil.debuff(entity, ModEffects.BOUND);
            }
         }
      }

      return ret;
   }

   public static void addBindings(Player player) {
      int totalRange = MTConfig.MYSTIC_EYES_RANGE_FROM_USER;
      Vec3 myloc = player.position();

      for (LivingEntity tmp : player.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(myloc.x - totalRange, myloc.y - totalRange, myloc.z - totalRange, myloc.x + totalRange, myloc.y + totalRange, myloc.z + totalRange)
         )) {
         if (EffectUtil.inRange(tmp, player, tmp.position(), MTConfig.MYSTIC_EYES_RANGE_FROM_LOOK_VEC, true)
            && !ContractMahoujinTileEntity.isImmuneToSpell(player.level(), player.getUUID(), tmp)
            && !isBound(tmp)) {
            if (tmp instanceof Creeper) {
               MandatoryFun.completeFunReq(player, MandatoryFun.FUN_REQS.CREEPER_BINDING_EYES);
            }

            EffectUtil.buff(tmp, ModEffects.BOUND, false, 100);
         }
      }
   }

   public static boolean continueBinding(LivingEntity victim) {
      int totalRange = MTConfig.MYSTIC_EYES_RANGE_FROM_USER;
      Vec3 myloc = victim.position();
      boolean keepBinding = false;

      for (Player tmp : victim.level()
         .getEntitiesOfClass(
            Player.class,
            new AABB(myloc.x - totalRange, myloc.y - totalRange, myloc.z - totalRange, myloc.x + totalRange, myloc.y + totalRange, myloc.z + totalRange)
         )) {
         if (!tmp.getUUID().equals(victim.getUUID())
            && EffectUtil.hasBuff(tmp, ModEffects.BINDING_EYES)
            && EffectUtil.inRange(victim, tmp, myloc, MTConfig.MYSTIC_EYES_RANGE_FROM_LOOK_VEC, true)) {
            keepBinding = true;
         }
      }

      return keepBinding;
   }

   public static boolean isBound(LivingEntity e) {
      return EffectUtil.hasBuff(e, ModEffects.BOUND);
   }
}
