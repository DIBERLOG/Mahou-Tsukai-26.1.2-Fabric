package stepsword.mahoutsukai.item.spells.familiar;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class ButterflyEffectSpellScroll extends SpellScroll {
   public ButterflyEffectSpellScroll() {
      super("butterfly_effect");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.BUTTERFLY_EFFECT_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.FORESIGHT, true, MTConfig.BUTTERFLY_EFFECT_DURATION);
         return true;
      } else {
         return false;
      }
   }
}
