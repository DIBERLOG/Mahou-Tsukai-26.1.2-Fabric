package stepsword.mahoutsukai.effects;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.entity.kodoku.KodokuEntity;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class Kodoku {
   public static void kodokuEntityDeath(LivingEntity victim, net.minecraft.world.damagesource.DamageSource source) {
      Entity e = source.getEntity();
      if (e instanceof LivingEntity && !e.level().isClientSide() && !(victim instanceof KodokuEntity)) {
         LivingEntity target = victim;
         int bonus = 0;
         if (e instanceof Mob) {
            bonus = (int)(target.getHealth() * MTConfig.KODOKU_HEALTH_FACTOR_MOB) + (int)(target.getArmorValue() * MTConfig.KODOKU_ARMOR_FACTOR_MOB);
         } else {
            bonus = (int)(target.getHealth() * MTConfig.KODOKU_HEALTH_FACTOR) + (int)(target.getArmorValue() * MTConfig.KODOKU_ARMOR_FACTOR);
         }

         int kodoku_transfer = getKodokuValue(target) + bonus + 1 + getKodokuValue((LivingEntity)e);

         for (int i = 0; i < target.getPassengers().size(); i++) {
            Entity k = (Entity)target.getPassengers().get(i);
            if (k instanceof KodokuEntity) {
               ((KodokuEntity)k).kodoku = getKodokuValue(target);
               kodoku_transfer = 0;
               break;
            }
         }

         setKodokuValue((LivingEntity)e, kodoku_transfer);
         setKodokuValue(target, 0);
      }

      if (victim instanceof KodokuEntity && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA))) {
         int base = 8;
         if (Leylines.leyDimensionValid(EffectUtil.getDimension(victim.level()))) {
            base += Leylines.nearbyLeyValue(victim.blockPosition());
         }

         PlayerManaManager.chargeAnyNearbyBatteries(
            victim.blockPosition(),
            victim.level(),
            PlayerManaManager.nearbyBatteries(victim.blockPosition(), victim.level()),
            ((KodokuEntity)victim).kodoku * base
         );
      }

      LivingEntity target = victim;
      if (target != null) {
         setKodokuValue(target, 0);
      }
   }

   public static int getKodokuValue(LivingEntity living) {
      ILivingMahou mahou = Utils.getLivingMahou(living);
      return mahou == null ? 0 : mahou.getKodoku();
   }

   public static void setKodokuValue(LivingEntity living, int u) {
      ILivingMahou mahou = Utils.getLivingMahou(living);
      if (mahou != null) {
         mahou.setKodoku(u);
      }
   }
}
