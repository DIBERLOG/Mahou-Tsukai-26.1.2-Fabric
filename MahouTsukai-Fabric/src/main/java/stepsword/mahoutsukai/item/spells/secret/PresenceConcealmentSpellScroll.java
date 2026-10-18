package stepsword.mahoutsukai.item.spells.secret;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class PresenceConcealmentSpellScroll extends SpellScroll {
   public PresenceConcealmentSpellScroll() {
      super("presence_concealment");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.RIPPER_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         ItemStack s = new ItemStack((ItemLike)ModItems.theripper.get());
         s.setCount(1);
         Utils.giveOrDrop(user, s);
         s = new ItemStack((ItemLike)ModItems.theripper.get());
         s.setCount(1);
         Utils.giveOrDrop(user, s);
         return true;
      } else {
         return false;
      }
   }
}
