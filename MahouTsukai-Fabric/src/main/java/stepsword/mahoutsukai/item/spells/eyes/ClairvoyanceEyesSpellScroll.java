package stepsword.mahoutsukai.item.spells.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class ClairvoyanceEyesSpellScroll extends SpellScroll {
   public ClairvoyanceEyesSpellScroll() {
      super("prediction");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.CLAIRVOYANCE_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.CLAIRVOYANCE, true, MTConfig.CLAIRVOYANCE_TIME);
         return true;
      } else {
         return false;
      }
   }
}
