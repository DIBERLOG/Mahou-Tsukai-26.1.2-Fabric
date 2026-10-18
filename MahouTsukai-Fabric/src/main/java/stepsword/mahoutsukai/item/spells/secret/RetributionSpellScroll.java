package stepsword.mahoutsukai.item.spells.secret;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.secret.RetributionSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class RetributionSpellScroll extends SpellScroll {
   public RetributionSpellScroll() {
      super("retribution");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.RETRIBUTION_MANA_PER_DIFFERENCE;
   }

   @Override
   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide() && entityLiving instanceof Player user) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (matchCaster(user, scrollMahou) && this.shouldConsume(user, scrollMahou, consume, stack)) {
            stack.shrink(1);
         }
      }
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return RetributionSpellEffect.retribution(user);
   }
}
