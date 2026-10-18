package stepsword.mahoutsukai.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ImmunityExchangePotion extends ScrollPotion {
   protected ImmunityExchangePotion() {
      super(ModEffects.getColorNumber(252, 45, 225));
   }

   /** NaN means cancel the incoming damage event, as in the original highest-priority handler. */
   public static float immunityExchangeLivingHurt(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount) {
      if (!EffectUtil.hasBuff(entity,ModEffects.IMMUNITY_EXCHANGE) || source == entity.damageSources().fellOutOfWorld()) return amount;
      IMahou playerMahou = entity instanceof Player player ? Utils.getPlayerMahou(player) : null;
      ILivingMahou livingMahou = entity instanceof Player ? null : Utils.getLivingMahou(entity);
      if (playerMahou == null && livingMahou == null) return amount;
      String last=playerMahou != null ? playerMahou.getImmunityDamageType() : livingMahou.getImmunityDamageType();
      boolean cancel=last != null && !last.isEmpty() && last.equals(source.getMsgId());
      if(last != null && !last.isEmpty() && !cancel) amount *= 2;
      if(playerMahou != null) playerMahou.setImmunityDamageType(source.getMsgId());
      else livingMahou.setImmunityDamageType(source.getMsgId());
      if(entity instanceof ServerPlayer player) PlayerManaManager.updateClientMahou(player,playerMahou);
      return cancel ? Float.NaN : amount;
   }
}
