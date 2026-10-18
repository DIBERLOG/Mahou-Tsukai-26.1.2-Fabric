package stepsword.mahoutsukai.item.spells.projection;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class WeaponShooterSpellScroll extends SpellScroll {
   public WeaponShooterSpellScroll() {
      super("weapon_shooter");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.WEAPON_SHOOTER_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         Utils.giveOrDrop(user, new ItemStack((ItemLike)ModItems.weaponShooterBow.get()));
         return true;
      } else {
         return false;
      }
   }
}
