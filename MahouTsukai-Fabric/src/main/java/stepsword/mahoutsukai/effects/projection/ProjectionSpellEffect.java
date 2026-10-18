package stepsword.mahoutsukai.effects.projection;

import java.util.HashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class ProjectionSpellEffect {
   public static boolean projection(Player caster, ItemStack toProject) {
      int setDamage = MTConfig.PROJECTION_DURABILITY_FACTOR;
      if (toProject != null) {
         if (canProject(toProject, caster.level())) {
            ItemStack stack = toProject.copy();
            stack.setCount(1);
            if (stack.isDamageableItem()) {
               int maxDamage = stack.getMaxDamage();
               if (MTConfig.PROJECTION_DURABILITY_FLAT) {
                  setDamage = maxDamage - setDamage;
               }

               setDamage = Math.max(0, Math.min(setDamage, maxDamage - 1));
               stack.setDamageValue(setDamage);
               stack = removeDisallowedEnchants(stack);
               StrengtheningSpellEffect.reduceStrengthened(stack, StrengtheningSpellEffect.getStrengthened(stack));
               setProjected(stack);
               return Utils.giveOrDrop(caster, stack);
            }
         } else {
            projectionHelp(caster, toProject);
         }
      }

      return false;
   }

   public static void setProjected(ItemStack s) {
      ProjectedMahou mahou = Utils.getProjectedMahou(s);
      mahou.setProjected(true);
      Utils.setProjectedMahou(s, mahou);
   }

   public static boolean getProjected(ItemStack s) {
      if (Utils.hasProjectedMahou(s)) {
         ProjectedMahou mahou = Utils.getProjectedMahou(s);
         return mahou.getProjected();
      } else {
         return false;
      }
   }

   /** Native anvil result hook; original shared-enchantment minimum prevents upgrades by copies. */
   public static void projectionRepairAnvil(ItemStack left,ItemStack right,ItemStack output) {
      if ((getProjected(left) || getProjected(right)) && !MTConfig.PROJECTION_ANVIL_CANCEL && !output.isEmpty()) {
         ItemEnchantments enchs = EnchantmentHelper.getEnchantmentsForCrafting(output);
         ItemEnchantments enchsleft = EnchantmentHelper.getEnchantmentsForCrafting(left);
         ItemEnchantments enchsright = EnchantmentHelper.getEnchantmentsForCrafting(right);
         EnchantmentHelper.updateEnchantments(output, outputEnchants -> {
            for (Holder<Enchantment> enchantment : enchs.keySet()) {
               if (enchsleft.keySet().contains(enchantment) && enchsright.keySet().contains(enchantment))
                  outputEnchants.set(enchantment, Math.min(enchsleft.getLevel(enchantment), enchsright.getLevel(enchantment)));
            }
         });
      }
   }

   public static void projectionHelp(Player caster, ItemStack stack) {
      if (!stack.isEmpty() && !stack.isDamageableItem()) {
         PlayerHelp.sendHelpMessage(caster, PlayerHelp.Message.PROJECTION_NO_UNBREAKABLE);
      } else {
         PlayerHelp.sendHelpMessage(caster, PlayerHelp.Message.PROJECTION_NOT_ALLOWED);
      }
   }

   public static ItemStack memorize(Player caster) {
      if (caster != null) {
         Entity e = Utils.selectEntityNearCursor(
            caster,
            MTConfig.GLOBAL_LOOK_RANGE,
            caster.level(),
            a -> Utils.getPredicate(new HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.SCROLL_PROJECTION)), a, caster),
            Utils.nearAllowed(new HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.SCROLL_PROJECTION)))
         );
         if (e instanceof LivingEntity living) {
            for (ItemStack tmp : Utils.getEquipmentSlotList(living)) {
               if (canProject(tmp, caster.level())) {
                  return tmp;
               }

               projectionHelp(caster, tmp);
            }
         } else if (e instanceof ItemEntity ie) {
            if (canProject(ie.getItem(), caster.level())) {
               return ie.getItem();
            }

            projectionHelp(caster, ie.getItem());
         } else if (e instanceof ItemFrame frame) {
            if (canProject(frame.getItem(), caster.level())) {
               return frame.getItem();
            }

            projectionHelp(caster, frame.getItem());
         } else if (e instanceof ThrownTrident) {
            if (canProject(e.getWeaponItem(), caster.level())) {
               return e.getWeaponItem();
            }

            projectionHelp(caster, e.getWeaponItem());
         }
      }

      return null;
   }

   public static boolean canProject(ItemStack stack, Level level) {
      Item tmpi = stack.getItem();
      boolean ret = tmpi instanceof BowItem
         || Utils.isWeapon(tmpi)
         || Utils.isTool(stack)
         || tmpi instanceof ShieldItem
         || tmpi instanceof ShearsItem
         || tmpi instanceof FishingRodItem
         || tmpi instanceof TridentItem
         || tmpi instanceof CrossbowItem;
      return ret && !disallowed(tmpi, level) && stack.isDamageableItem() || EffectUtil.inItemBlacklist(tmpi, MTConfig.PROJECTION_EXPLICIT_WHITELIST, level);
   }

   public static boolean disallowed(Item tmpi, Level level) {
      if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi) == null) {
         return false;
      }

      HashSet<String> disallowed = new HashSet<>(MTConfig.PROJECTION_BLACKLIST);
      return MTConfig.PROJECTION_IS_BLACKLIST
         ? disallowed.contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi).toString()) || disallowed.contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi).getNamespace())
         : !disallowed.contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi).toString()) && !disallowed.contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tmpi).getNamespace());
   }

   public static ItemStack removeDisallowedEnchants(ItemStack stack) {
      ItemStack copy = stack.copy();
      HashSet<String> disallowed = new HashSet<>(MTConfig.PROJECTION_ENCHANTMENT_BLACKLIST);
      ItemEnchantments itemenchantments = EnchantmentHelper.updateEnchantments(stack, e -> e.removeIf(ee -> disallowed.contains(ee.getRegisteredName())));
      EnchantmentHelper.setEnchantments(copy, itemenchantments);
      return copy;
   }
}
