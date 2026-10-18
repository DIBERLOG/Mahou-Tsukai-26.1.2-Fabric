package stepsword.mahoutsukai.effects.exchange;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class DamageExchangeSpellEffect {
   public static boolean addProtectionToUser(Player player) {
      if (player != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            mahou.setDamageExchangeUses(Math.min(MTConfig.DAMAGE_EXCHANGE_CAP, mahou.getDamageExchangeUses() + 5));
            if (player instanceof ServerPlayer) {
               PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
            }

            return true;
         }
      }

      return false;
   }

   public static float damageExchangeLivingHurt(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount) {
      if (!entity.level().isClientSide() && entity instanceof Player) {
         Player player = (Player)entity;
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            int protD = mahou.getDamageExchangeUses();
            if (protD > 0 && !(source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL))) {
               boolean canConvert = MTConfig.DAMAGE_EXCHANGE_MANA_GAIN >= 0;
               if (MTConfig.DAMAGE_EXCHANGE_MANA_GAIN > 0) {
                  mahou.setStoredMana(
                     Math.max(0, Math.min(mahou.getStoredMana() + MTConfig.DAMAGE_EXCHANGE_MANA_GAIN * (int)Math.floor(amount), mahou.getMaxMana()))
                  );
               } else if (MTConfig.DAMAGE_EXCHANGE_MANA_GAIN < 0) {
                  canConvert = PlayerManaManager.drainMana(player, MTConfig.DAMAGE_EXCHANGE_MANA_GAIN, false, false) == MTConfig.DAMAGE_EXCHANGE_MANA_GAIN;
               }

               if (canConvert) {
                  amount = (float)MTConfig.DAMAGE_EXCHANGE_REDUCE_TO;
                  mahou.setDamageExchangeUses(protD - 1);
                  if (player instanceof ServerPlayer) {
                     PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
                  }
               }
            }
         }
      }
      return amount;
   }
}
