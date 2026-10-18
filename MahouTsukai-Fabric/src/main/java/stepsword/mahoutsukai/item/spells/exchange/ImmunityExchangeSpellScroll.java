package stepsword.mahoutsukai.item.spells.exchange;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.exchange.ImmunityExchangeSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class ImmunityExchangeSpellScroll extends SpellScroll {
   public ImmunityExchangeSpellScroll() {
      super("immunity_exchange");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.IMMUNITY_EXCHANGE_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? ImmunityExchangeSpellEffect.buffImmunityExchange(user) : false;
   }
}
