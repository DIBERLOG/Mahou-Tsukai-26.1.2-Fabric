package stepsword.mahoutsukai.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.util.EffectUtil;

public class AuthorityJumpPotion extends CooldownPotion {
   private static final double AIR_HORIZONTAL_DRAG = 0.91;

   protected AuthorityJumpPotion() {
   }

   public static void authorityJumpLivingTick(LivingEntity player) {
      if (!player.level().isClientSide()) {
         if (EffectUtil.noMoreSoaring(player) && EffectUtil.hasBuff(player, ModEffects.AUTHORITY_JUMP)) {
            EffectUtil.debuff(player, ModEffects.AUTHORITY_JUMP);
         } else if (!EffectUtil.noMoreSoaring(player) && EffectUtil.hasBuff(player, ModEffects.AUTHORITY_JUMP)) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1.0989010989010988, 1.0, 1.0989010989010988));
            player.hurtMarked = true;
            if (player instanceof Player) {
               ((ServerPlayer)player).connection.aboveGroundTickCount = 0;
            }
         }
      }
   }
}
