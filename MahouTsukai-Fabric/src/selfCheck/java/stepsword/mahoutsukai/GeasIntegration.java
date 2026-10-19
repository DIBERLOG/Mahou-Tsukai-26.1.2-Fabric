package stepsword.mahoutsukai;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
public final class GeasIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 public static void run(ServerPlayer player,ItemStack scroll) {
  var world=player.level();var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  mahou.setGeas(new java.util.HashSet<>());mahou.setStoredMana(100);
  player.setPos(548.5,250,532.5);player.setYRot(180);player.setYHeadRot(180);player.setXRot(8);
  var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(548.5,250,527.5);world.addFreshEntity(cow);
  require(stepsword.mahoutsukai.util.Utils.selectGeasTarget(player)==cow,"Original strict Geas cursor selects aimed entity");
  var wall=new BlockPos(548,251,530);world.setBlockAndUpdate(wall,Blocks.STONE.defaultBlockState());
  require(stepsword.mahoutsukai.util.Utils.selectGeasTarget(player)==null,"Opaque wall blocks Geas selection");
  world.setBlockAndUpdate(wall,Blocks.AIR.defaultBlockState());
  int before=stepsword.mahoutsukai.mana.PlayerManaManager.getManaRegen(player);
  ((SpellScroll)scroll.getItem()).releaseUsing(scroll,world,player,71900);
  require(scroll.isEmpty() && mahou.getStoredMana()==85 && GeasEffect.hasGeas(player,cow),"Original Geas scroll creates vow, spends 30 and consumes scroll");
  require(stepsword.mahoutsukai.mana.PlayerManaManager.getManaRegen(player)==before+3,"Real Geas adds original regeneration bonus");
  float health=cow.getHealth();cow.hurtServer(world,player.damageSources().playerAttack(player),5);
  require(cow.getHealth()==health,"Native incoming damage hook enforces Geas");
  require(net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.invoker().interact(player,world,net.minecraft.world.InteractionHand.MAIN_HAND,cow,null)
   ==net.minecraft.world.InteractionResult.FAIL,"Native attack callback enforces Geas before attacking");
  var second=new ItemStack(ModItems.REGISTERED.get("scroll_geas"));
  ((SpellScroll)second.getItem()).releaseUsing(second,world,player,71900);
  require(!second.isEmpty() && mahou.getStoredMana()==70 && !GeasEffect.hasGeas(player,cow),"Removing existing vow still costs mana but retains original scroll");
  var diamond=new ItemEntity(world,548.5,250,532.5,new ItemStack(Items.DIAMOND));
  require(GeasEffect.addGeas(player,diamond),"Item vow created");
  diamond.playerTouch(player);
  require(!diamond.getItem().isEmpty() && stepsword.mahoutsukai.config.MandatoryFun.meetsFunReq(player,stepsword.mahoutsukai.config.MandatoryFun.FUN_REQS.GEAS_PICKUP_CANCEL),"Native pickup hook refuses vowed item and saves original fun marker");
  require(!GeasEffect.addGeas(player,diamond),"Item vow toggles off");diamond.playerTouch(player);
  require(player.getInventory().contains(new ItemStack(Items.DIAMOND)),"Pickup works after vow removal");
  var pig=EntityType.PIG.create(world,EntitySpawnReason.COMMAND);pig.setCustomName(net.minecraft.network.chat.Component.literal("Daisy"));
  GeasEffect.addGeas(player,pig);
  require(GeasEffect.hasGeas(player,pig) && GeasEffect.getUniqueGeasCount(player)==0,"Named vows do not grant type-wide regeneration bonus");
  var burn=new ItemEntity(world,548.5,250,532.5,new ItemStack(ModItems.REGISTERED.get("scroll_geas")));
  burn.setThrower(player);
  require(burn.getOwner()==player,"Native dropped scroll retains thrower");
  burn.hurtServer(world,player.damageSources().inFire(),10);
  require(mahou.getGeas()==null,"Burning owner scroll clears vows through native destruction hook");
  mahou.setGeas(new java.util.HashSet<>());cow.discard();pig.discard();diamond.discard();burn.discard();
  System.out.println("PASS: GEAS_INTEGRATION: original ritual scroll, cursor/wall, create/remove, mana/regen, attack/damage, pickup, named vows, burned scroll");
 }
}
