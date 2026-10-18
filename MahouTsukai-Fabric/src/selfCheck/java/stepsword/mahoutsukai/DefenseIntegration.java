package stepsword.mahoutsukai;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.potion.ModEffects;
public final class DefenseIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 private static void cast(ServerPlayer player,String name) {
  var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_"+name));
  ((stepsword.mahoutsukai.item.spells.SpellScroll)scroll.getItem()).releaseUsing(scroll,player.level(),player,71999);
  require(scroll.isEmpty(),"Original "+name+" scroll consumed");
 }
 public static void run(ServerPlayer player) {
  var world=player.level();var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  player.getInventory().clearContent();player.setPos(570.5,250,570.5);player.setYRot(180);player.setYHeadRot(180);player.setXRot(8);
  mahou.setGeas(new java.util.HashSet<>());mahou.setMaxMana(1000);mahou.setStoredMana(1000);mahou.setDamageExchangeUses(0);
  cast(player,"damage_exchange");require(mahou.getStoredMana()==960 && mahou.getDamageExchangeUses()==5,"Damage Exchange costs 40 and grants five uses");
  player.setHealth(20);player.invulnerableTime=0;
  player.hurtServer(world,player.damageSources().generic(),4);
  require(player.getHealth()==19 && mahou.getStoredMana()==1000 && mahou.getDamageExchangeUses()==4,"Native hit converts original four damage into 80 mana capped at max and one damage");
  require(player.hasEffect(ModEffects.BLEEDING),"Incoming hit retains original bleeding behavior");
  mahou.setStoredMana(0);player.invulnerableTime=0;player.hurtServer(world,player.damageSources().fellOutOfWorld(),1);
  require(mahou.getDamageExchangeUses()==4 && mahou.getStoredMana()==0,"Void damage bypasses Damage Exchange without using a charge");
  mahou.setDamageExchangeUses(0);mahou.setStoredMana(1000);player.setHealth(20);player.invulnerableTime=0;
  cast(player,"immunity_exchange");require(mahou.getStoredMana()==600 && player.hasEffect(ModEffects.IMMUNITY_EXCHANGE),"Immunity scroll targets self when cursor is empty");
  player.hurtServer(world,player.damageSources().generic(),2);require(player.getHealth()==18,"First immunity damage type deals normal damage");
  player.invulnerableTime=0;player.hurtServer(world,player.damageSources().generic(),2);require(player.getHealth()==18,"Repeated type fully cancels native damage");
  player.invulnerableTime=0;player.hurtServer(world,player.damageSources().magic(),2);require(player.getHealth()==14,"Changed type doubles native damage");
  player.removeEffect(ModEffects.IMMUNITY_EXCHANGE);mahou.setStoredMana(1000);
  var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(570.5,250,565.5);world.addFreshEntity(cow);
  cast(player,"immunity_exchange");require(cow.hasEffect(ModEffects.IMMUNITY_EXCHANGE) && !player.hasEffect(ModEffects.IMMUNITY_EXCHANGE),"Original strict cursor applies immunity to target");
  cow.hurtServer(world,cow.damageSources().generic(),2);cow.invulnerableTime=0;cow.hurtServer(world,cow.damageSources().generic(),2);
  require(cow.getHealth()==8 && stepsword.mahoutsukai.util.Utils.getLivingMahou(cow).getImmunityDamageType().equals("generic"),"Mob immunity uses persistent original LivingMahou state");cow.discard();
  mahou.setStoredMana(1000);mahou.setProtectiveDisplacement(0);cast(player,"protective_displacement");
  require(mahou.getStoredMana()==950 && mahou.getProtectiveDisplacement()==5,"Protective scroll costs 50 and grants five charges");
  for(BlockPos pos:BlockPos.betweenClosed(566,249,566,574,249,574)) world.setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());
  var arrow=new net.minecraft.world.entity.projectile.arrow.Arrow(world,player,new ItemStack(net.minecraft.world.item.Items.ARROW),null);
  player.setHealth(20);player.invulnerableTime=0;
  ((stepsword.mahoutsukai.checks.mixin.ProjectileImpactInvoker)arrow).mahoutsukai$impact(new net.minecraft.world.phys.EntityHitResult(player));
  require(player.getHealth()==20 && mahou.getProtectiveDisplacement()==4 && !arrow.isRemoved(),"Native projectile collision cancels, teleports and spends one charge");
  var output=net.minecraft.world.level.storage.TagValueOutput.createWithoutContext(net.minecraft.util.ProblemReporter.DISCARDING);
  player.saveWithoutId(output);require(output.buildResult().toString().contains("mahoutsukai"),"Original defensive state participates in native player saving");
  mahou.setProtectiveDisplacement(0);player.removeEffect(ModEffects.IMMUNITY_EXCHANGE);mahou.setImmunityDamageType("");player.setHealth(20);arrow.discard();
  System.out.println("PASS: DEFENSE_INTEGRATION: scroll costs/charges, native damage conversion, void exclusion, player/mob immunity sequence and native projectile collision");
 }
}
