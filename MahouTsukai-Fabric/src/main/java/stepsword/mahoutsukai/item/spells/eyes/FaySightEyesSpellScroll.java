package stepsword.mahoutsukai.item.spells.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FaySightEyesSpellScroll extends SpellScroll {
   public FaySightEyesSpellScroll() {
      super("fay_sight");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.FAY_SIGHT_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         IMahou mahou = Utils.getPlayerMahou(user);
         if (mahou != null) {
            mahou.setFaeSpawn(true);
         }

         EffectUtil.buff(user, ModEffects.FAY_SIGHT_EYES, true, MTConfig.FAY_SIGHT_TIME, false);
         return true;
      } else {
         return false;
      }
   }
}
