package stepsword.mahoutsukai.item.spells.familiar;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class FamiliarsGardenSpellScroll extends SpellScroll {
   public FamiliarsGardenSpellScroll() {
      super("familiars_garden");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.FAMILIARS_GARDEN_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         boolean flag = FamiliarsGardenEffect.familiarGarden(user);
         if (!flag) {
            user.sendOverlayMessage(Component.literal(ChatFormatting.RED + "Command failed"));
         }

         return flag;
      } else {
         return false;
      }
   }
}
