package stepsword.mahoutsukai.item.spells.familiar;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.familiar.PossessEntityEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class PossessEntitySpellScroll extends SpellScroll {
   public PossessEntitySpellScroll() {
      super("possess_entity");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.POSSESS_ENTITY_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? PossessEntityEffect.possessLookingAt(user) : false;
   }
}
