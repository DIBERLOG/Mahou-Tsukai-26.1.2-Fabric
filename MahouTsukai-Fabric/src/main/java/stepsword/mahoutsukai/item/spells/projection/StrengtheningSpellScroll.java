package stepsword.mahoutsukai.item.spells.projection;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class StrengtheningSpellScroll extends SpellScroll {
   public StrengtheningSpellScroll() {
      super("strengthening");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.STRENGTHENING_MANA_COST;
   }

   @Override
   public void useAction(ItemStack stack, Level level, LivingEntity entity, boolean consume) {
      if (!level.isClientSide() && entity instanceof Player player) {
         ItemStack target = StrengtheningSpellEffect.findStackToStrengthen(player);
         if (!StrengtheningSpellEffect.canStrengthen(target, level)) {
            return;
         }
      }

      super.useAction(stack, level, entity, consume);
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         ItemStack s = StrengtheningSpellEffect.findStackToStrengthen(user);
         if (s != null && !s.isEmpty()) {
            return StrengtheningSpellEffect.setStrengthened(s, user.level());
         }
      }

      return false;
   }
}
