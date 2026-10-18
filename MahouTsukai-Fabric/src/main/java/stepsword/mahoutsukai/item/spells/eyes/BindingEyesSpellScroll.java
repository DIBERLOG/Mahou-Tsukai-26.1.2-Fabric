package stepsword.mahoutsukai.item.spells.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class BindingEyesSpellScroll extends SpellScroll {
   public BindingEyesSpellScroll() {
      super("mystic_eyes");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.MYSTIC_EYES_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.BINDING_EYES, true, MTConfig.MYSTIC_EYES_TIME);
         return true;
      } else {
         return false;
      }
   }
}
