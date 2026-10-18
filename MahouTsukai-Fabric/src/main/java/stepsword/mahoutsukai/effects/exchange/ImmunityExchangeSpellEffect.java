package stepsword.mahoutsukai.effects.exchange;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;
public class ImmunityExchangeSpellEffect {
 public static LivingEntity getEnemy(Player player) { return (LivingEntity)Utils.selectStrictTarget(player,e->e instanceof LivingEntity); }
 public static boolean buffImmunityExchange(Player user) {
  LivingEntity target=getEnemy(user);
  EffectUtil.buff(target != null ? target : user,ModEffects.IMMUNITY_EXCHANGE,true,MTConfig.IMMUNITY_EXCHANGE_TIME);
  return true;
 }
}
