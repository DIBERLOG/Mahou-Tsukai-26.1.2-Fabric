package stepsword.mahoutsukai.item.spells.displacement;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.displacement.ProtectiveDisplacementSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class ProtectiveDisplacementSpellScroll extends SpellScroll {
   public ProtectiveDisplacementSpellScroll() {
      super("protective_displacement");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.PROTECTIVE_DISPLACEMENT_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? ProtectiveDisplacementSpellEffect.addProtectionToUser(user) : false;
   }
}
