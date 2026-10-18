package stepsword.mahoutsukai.item.spells.mystic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class RhoAiasSpellScroll extends SpellScroll {
   public RhoAiasSpellScroll() {
      super("rho_aias");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.RHO_AIAS_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         RhoAiasSpellEffect.spawnRhoAias(user);
         return true;
      } else {
         return false;
      }
   }
}
