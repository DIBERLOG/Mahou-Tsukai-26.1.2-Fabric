package stepsword.mahoutsukai.fabric;
/** Level follows persistent maximum mana; extends beyond 20,000 without a separate XP ledger. */
public final class MageProgression {
 private static final int[] THRESHOLDS={100,250,500,1000,2000,3500,5000,7500,10000,15000,20000};
 public static int level(int mana){if(mana>=20000)return 11+(mana-20000)/5000;int n=1;for(int i=0;i<THRESHOLDS.length;i++)if(mana>=THRESHOLDS[i])n=i+1;return n;}
 public static int nextLevelMana(int mana){int level=level(mana);return level>=11?20000+(level-10)*5000:THRESHOLDS[level];}
 public static int regenBonus(int mana){return (level(mana)-1)*2;}
 public static void restoreFromFood(net.minecraft.world.entity.player.Player player,int nutrition){
  if(player.level().isClientSide()||nutrition<=0)return;
  var state=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
  state.setStoredMana((int)Math.min(state.getMaxMana(),(long)state.getStoredMana()+nutrition*5L+level(state.getMaxMana())));
  stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou((net.minecraft.server.level.ServerPlayer)player,state);
 }
}
