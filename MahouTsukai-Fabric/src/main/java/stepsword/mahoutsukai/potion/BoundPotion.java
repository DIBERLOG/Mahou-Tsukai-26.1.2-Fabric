package stepsword.mahoutsukai.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.util.EffectUtil;

public class BoundPotion extends MobEffect {
   public static Input STANDING_STILL = new Input(false, false, false, false, false, false, false);

   protected BoundPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 225));
   }

   public static boolean boundLeftClickBlock(Player player) {
      return EffectUtil.hasBuff(player, ModEffects.BOUND);
   }

   public static boolean boundRightClickBlock(Player player) {
      return EffectUtil.hasBuff(player, ModEffects.BOUND);
   }

   public static boolean boundRightClickItem(Player player) {
      return EffectUtil.hasBuff(player, ModEffects.BOUND);
   }

   public static boolean boundAttackFromPlayer(Player player) {
      return EffectUtil.hasBuff(player, ModEffects.BOUND);
   }

}
