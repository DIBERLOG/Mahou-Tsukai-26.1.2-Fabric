package stepsword.mahoutsukai.item.spells.mystic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.BorrowedAuthorityPotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class BorrowedAuthoritySpellScroll extends SpellScroll {
   public BorrowedAuthoritySpellScroll() {
      super("borrowed_authority");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.AUTHORITY_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.BORROWED_AUTHORITY, true, MTConfig.AUTHORITY_TIME);
         BorrowedAuthorityPotion.notifyIfAuthority(user);
         return true;
      } else {
         return false;
      }
   }
}
