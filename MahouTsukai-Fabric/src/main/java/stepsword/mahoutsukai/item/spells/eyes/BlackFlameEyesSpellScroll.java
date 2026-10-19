package stepsword.mahoutsukai.item.spells.eyes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class BlackFlameEyesSpellScroll extends SpellScroll {
   public BlackFlameEyesSpellScroll() {
      super("black_flame");
   }

   @Override public net.minecraft.world.InteractionResult use(net.minecraft.world.level.Level level,Player player,net.minecraft.world.InteractionHand hand){
    if(player.isShiftKeyDown() && EffectUtil.hasBuff(player,ModEffects.BLACK_FLAME_EYES)){
     if(!level.isClientSide()){EffectUtil.debuff(player,ModEffects.BLACK_FLAME_EYES);player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("mahoutsukai.black_flame.stopped"));}
     return net.minecraft.world.InteractionResult.SUCCESS;
    }
    return super.use(level,player,hand);
   }
   @Override
   public int getInitialManaCost() {
      return MTConfig.BLACK_FLAME_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (scrollMahou != null) {
         EffectUtil.buff(user, ModEffects.BLACK_FLAME_EYES, true, MTConfig.BLACK_FLAME_TIME);
         return true;
      } else {
         return false;
      }
   }
}
