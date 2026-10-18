package stepsword.mahoutsukai.item.spells.displacement;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.displacement.ProjectileDisplacementSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class ProjectileDisplacementSpellScroll extends SpellScroll {
   public ProjectileDisplacementSpellScroll() {
      super("projectile_displacement");
   }

   @Override
   public int getInitialManaCost() {
      return 0;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? ProjectileDisplacementSpellEffect.teleportToLastArrow(user) : false;
   }
}
