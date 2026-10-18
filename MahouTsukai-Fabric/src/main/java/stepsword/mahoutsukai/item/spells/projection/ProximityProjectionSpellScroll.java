package stepsword.mahoutsukai.item.spells.projection;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class ProximityProjectionSpellScroll extends SpellScroll {
   public ProximityProjectionSpellScroll() {
      super("proximity_projection");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.PROXIMITY_PROJECTION_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         ItemStack s = new ItemStack((ItemLike)ModItems.REGISTERED.get("proximity_projection_keys"));
         s.setCount(1);
         s.enchant(user.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK), 2);
         Utils.giveOrDrop(user, s);
         return true;
      } else {
         return false;
      }
   }
}
