package stepsword.mahoutsukai.item.spells.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class BlackFlameEyesSpellScroll extends SpellScroll {
   public BlackFlameEyesSpellScroll() {
      super("black_flame");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.BLACK_FLAME_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.BLACK_FLAME_EYES, true, MTConfig.BLACK_FLAME_TIME);
         return true;
      } else {
         return false;
      }
   }
}
