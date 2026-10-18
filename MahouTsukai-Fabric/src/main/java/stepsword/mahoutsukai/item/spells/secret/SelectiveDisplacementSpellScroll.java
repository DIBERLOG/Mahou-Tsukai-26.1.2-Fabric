package stepsword.mahoutsukai.item.spells.secret;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class SelectiveDisplacementSpellScroll extends SpellScroll {
   public SelectiveDisplacementSpellScroll() {
      super("selective_displacement");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.SELECTIVE_DISPLACEMENT_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.SELECTIVE_DISPLACEMENT, false, MTConfig.SELECTIVE_DISPLACEMENT_TIME);
         return true;
      } else {
         return false;
      }
   }
}
