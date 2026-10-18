package stepsword.mahoutsukai.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.util.EffectUtil;

public class FlungPotion extends CooldownPotion {
   protected FlungPotion() {
   }

   public static void flungLivingTick(LivingEntity player) {
      if (!player.level().isClientSide()) {
         if (EffectUtil.noMoreSoaring(player) && EffectUtil.hasBuff(player, ModEffects.FLUNG)) {
            EffectUtil.debuff(player, ModEffects.FLUNG);
         } else if (!EffectUtil.noMoreSoaring(player) && EffectUtil.hasBuff(player, ModEffects.FLUNG)) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1.0869565217391304, 1.0, 1.0869565217391304));
            player.hurtMarked = true;
            if (player instanceof Player) {
               ((ServerPlayer)player).connection.aboveGroundTickCount = 0;
            }
         }
      }
   }
}
