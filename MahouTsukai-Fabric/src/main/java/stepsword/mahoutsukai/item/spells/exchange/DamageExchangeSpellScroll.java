package stepsword.mahoutsukai.item.spells.exchange;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.exchange.DamageExchangeSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class DamageExchangeSpellScroll extends SpellScroll {
   public DamageExchangeSpellScroll() {
      super("damage_exchange");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.DAMAGE_EXCHANGE_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? DamageExchangeSpellEffect.addProtectionToUser(user) : false;
   }
}
