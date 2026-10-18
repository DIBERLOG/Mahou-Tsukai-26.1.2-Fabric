package stepsword.mahoutsukai.item.spells.projection;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.EffectUtil;

public class RealityMarbleSpellScroll extends SpellScroll {
   public RealityMarbleSpellScroll() {
      super("reality_marble");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.MARBLE_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? RealityMarbleSpellEffect.goToMarble(user) : false;
   }

   @Override
   public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      if (!EffectUtil.getDimension(worldIn).toString().equals("mahoutsukai:reality_marble")) {
         this.useAction(stack, worldIn, entityLiving, true);
      }

      return true;
   }
}
