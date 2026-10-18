package stepsword.mahoutsukai.item.spells.displacement;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class MentalDisplacementSpellScroll extends SpellScroll {
   public MentalDisplacementSpellScroll() {
      super("mental_displacement");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.MENTAL_DISPLACEMENT_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? MentalDisplacementSpellEffect.leaveBody(user) : false;
   }
}
