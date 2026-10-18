package stepsword.mahoutsukai.effects.projection;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class MorganEffect {
   public static void morganLivingDeath(LivingEntity target,net.minecraft.world.damagesource.DamageSource source) {
      if (source.getEntity() instanceof Player) {
         Player player = (Player)source.getEntity();
         ItemStack s = player.getMainHandItem();

         if (player.level() != null && !player.level().isClientSide()) {
            if (s.getItem() instanceof Caliburn
               && (MTConfig.POWER_CONSOLIDATION_PROJECTIONS_ALLOWED || !ProjectionSpellEffect.getProjected(s))
               && canTransform(target, player)) {
               ItemStack m = new ItemStack((ItemLike)ModItems.morgan.get());
               SwordMahou mahou = Utils.getSwordMahou(m);
               SwordMahou old_mahou = Utils.getSwordMahou(s);
               if (MTConfig.POWER_CONSOLIDATION_TIERS.size() <= 0) {
                  mahou.setAttackDamage(Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, old_mahou.getAttackDamage()));
               } else {
                  mahou.setInnateCap(Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, old_mahou.getInnateCap()));
                  mahou.setAttackDamage((float)Math.min(mahou.getInnateCap(), old_mahou.getAttackDamage()));
               }

               Utils.setSwordMahou(m, mahou);
               m.setDamageValue(s.getDamageValue());
               turnSwordIntoItem(player, m);
               ((MahouTrigger)ModTriggers.MORGAN).trigger((ServerPlayer)player);
            }

            if (s.getItem() instanceof Morgan && target instanceof AbstractVillager) {
               List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.MORGAN);
               Utils.color(
                  s,
                  settings.get(0).intValue(),
                  settings.get(1).intValue(),
                  settings.get(2).intValue(),
                  settings.get(3).intValue(),
                  settings.get(4).intValue(),
                  settings.get(5).intValue()
               );
               int mana = MTConfig.MORGAN_UPGRADE_MANA_COST;
               if (PlayerManaManager.drainMana(player, mana, false, false) == mana) {
                  s.setDamageValue(s.getDamageValue() - MTConfig.MORGAN_HEAL_FACTOR);
                  SwordMahou morgan_mahou = Utils.getSwordMahou(s);
                  if (MTConfig.POWER_CONSOLIDATION_TIERS.size() <= 0) {
                     morgan_mahou.setInnateCap(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP);
                  }

                  if (target.isBaby()) {
                     morgan_mahou.setAttackDamage(
                        (float)Math.min(morgan_mahou.getInnateCap(), morgan_mahou.getAttackDamage() + (float)MTConfig.MORGAN_CHILD_INCREASE)
                     );
                  } else {
                     morgan_mahou.setAttackDamage(
                        (float)Math.min(morgan_mahou.getInnateCap(), morgan_mahou.getAttackDamage() + (float)MTConfig.MORGAN_ADULT_INCREASE)
                     );
                  }

                  Utils.setSwordMahou(s, morgan_mahou);
                  Utils.updateHeldItem(player);
                  ((MahouTrigger)ModTriggers.BREADCRUMBS).trigger((ServerPlayer)player);
               }
            }
         }
      }
   }

   public static void secretCalibur(Player player) {
      if (player instanceof ServerPlayer) {
         boolean hasMorgan = player.getMainHandItem().getItem() instanceof Morgan || player.getOffhandItem().getItem() instanceof Morgan;
         boolean hasCaliburn = player.getMainHandItem().getItem() instanceof Caliburn || player.getOffhandItem().getItem() instanceof Caliburn;
         if (hasMorgan && hasCaliburn) {
            ((MahouTrigger)ModTriggers.SECRET_CALIBUR).trigger((ServerPlayer)player);
         }
      }
   }

   public static boolean canTransform(LivingEntity e, Player player) {
      boolean tameable = e instanceof TamableAnimal;
      boolean isTamed = true;
      if (tameable) {
         isTamed = ((TamableAnimal)e).getOwnerReference() != null && ((TamableAnimal)e).getOwnerReference().getUUID().equals(player.getUUID());
      }

      return isTamed && whitelistedEntity(e);
   }

   public static boolean whitelistedEntity(LivingEntity e) {
      for (String s : MTConfig.MORGAN_TRANSFORM_ENTITY_WHITELIST) {
         if (Utils.getRegistryKey(e, e.level()) != null && s.equals(Utils.getRegistryName(e))) {
            return true;
         }
      }

      return false;
   }

   public static void turnSwordIntoItem(Player player, ItemStack change) {
      player.getInventory().removeItemNoUpdate(player.getInventory().getSelectedSlot());
      if (!player.getInventory().add(player.getInventory().getSelectedSlot(), change)) {
         player.drop(change, false);
      }
   }
}
