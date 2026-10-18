package stepsword.mahoutsukai.item.spells.familiar;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class SummonFamiliarSpellScroll extends SpellScroll {
   public SummonFamiliarSpellScroll() {
      super("summon_familiar");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.SUMMON_FAMILIAR_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         SummonFamiliarSpellEffect.summonFamiliar(user);
         return true;
      } else {
         return false;
      }
   }
}
