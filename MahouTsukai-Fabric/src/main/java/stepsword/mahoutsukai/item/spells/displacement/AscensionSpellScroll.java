package stepsword.mahoutsukai.item.spells.displacement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.displacement.AscensionSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class AscensionSpellScroll extends SpellScroll {
   public AscensionSpellScroll() {
      super("ascension");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.ASCENSION_SCROLL_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         BlockPos upos = user.blockPosition();
         return AscensionSpellEffect.teleportEntityToSurface(upos.getX(), upos.getZ(), user, upos.getY(), user, user.getUUID(), false);
      } else {
         return false;
      }
   }
}
