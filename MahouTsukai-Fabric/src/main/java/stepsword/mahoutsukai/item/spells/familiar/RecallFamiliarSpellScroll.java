package stepsword.mahoutsukai.item.spells.familiar;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.familiar.RecallFamiliarEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class RecallFamiliarSpellScroll extends SpellScroll {
   public RecallFamiliarSpellScroll() {
      super("recall_familiar");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.RECALL_FAMILIAR_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         boolean flag = RecallFamiliarEffect.recallFamiliar(user, false);
         if (!flag) {
            user.sendOverlayMessage(Component.literal(ChatFormatting.RED + "Command failed"));
         }

         return flag;
      } else {
         return false;
      }
   }
}
