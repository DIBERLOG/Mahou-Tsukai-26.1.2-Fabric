package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraft.server.level.ServerPlayer;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.tile.displacement.*;
import stepsword.mahoutsukai.potion.*;
public final class DisplacementEyesIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 private static ItemStack cast(ServerPlayer player,String name) {
  var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_"+name));((SpellScroll)scroll.getItem()).releaseUsing(scroll,player.level(),player,71999);return scroll;
 }
 public static void run(ServerPlayer player) {
  var world=player.level();var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(player.getUUID(),player);
  player.setPos(480.5,250,575.5);mahou.setStoredMana(1000);mahou.setMaxMana(1000);
  var p1=new BlockPos(480,250,570);var p2=p1.offset(4,0,0);var p3=p1.offset(8,0,0);
  var first=(OrderedDisplacementMahoujinTileEntity)ExchangeCircleIntegration.ritual(player,p1,"ordered_displacement",false,"ender","ender","ender");first.setTeleporterId(10);
  var second=(OrderedDisplacementMahoujinTileEntity)ExchangeCircleIntegration.ritual(player,p2,"ordered_displacement",false,"ender","ender","ender");second.setTeleporterId(20);
  var third=(OrderedDisplacementMahoujinTileEntity)ExchangeCircleIntegration.ritual(player,p3,"ordered_displacement",false,"ender","ender","ender");third.setTeleporterId(30);
  require(first.getTarget().equals(p2) && second.getTarget().equals(p3) && third.getTarget().equals(p1),"Original ordered circles follow creation sequence and wrap");
  var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(Vec3.atCenterOf(p1));world.addFreshEntity(cow);first.ifEntityFoundAbove(cow);
  require(cow.getX()==p2.getX()+0.5 && mahou.getStoredMana()==960 && !second.preConditions(),"Ordered transport costs forty and enforces receiving cooldown");
  for(int i=0;i<15;i++) second.mahoujinTick(world,p2,second.getBlockState(),second);require(second.preConditions(),"Ordered receiving cooldown expires");
  cow.setShiftKeyDown(true);second.ifEntityFoundAbove(cow);require(cow.getX()==p2.getX()+0.5 && mahou.getStoredMana()==960,"Sneaking entity does not trigger ordered transport");cow.setShiftKeyDown(false);
  for(var pos:List.of(p1,p2,p3)) world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
  var aPos=new BlockPos(500,250,570);var bPos=aPos.offset(4,0,0);
  var a=(EquivalentDisplacementMahoujinTileEntity)ExchangeCircleIntegration.ritual(player,aPos,"equivalent_displacement",false,"ender","ender","emerald");
  var b=(EquivalentDisplacementMahoujinTileEntity)ExchangeCircleIntegration.ritual(player,bPos,"equivalent_displacement",false,"ender","ender","emerald");
  player.setShiftKeyDown(true);
  for(var pos:List.of(aPos,bPos)) net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.invoker().interact(player,world,net.minecraft.world.InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
  player.setShiftKeyDown(false);
  require(a.getTargetLocation().equals(bPos) && b.getTargetLocation().equals(aPos) && mahou.getMemorizedEDMTE()==null,"Native shift-click binds equivalent circle pair in both directions");
  cow.setPos(Vec3.atCenterOf(aPos));var pig=EntityType.PIG.create(world,EntitySpawnReason.COMMAND);pig.setPos(Vec3.atCenterOf(bPos));world.addFreshEntity(pig);
  mahou.setStoredMana(1000);a.ifEntityFoundAbove(cow);
  require(cow.getX()==bPos.getX()+0.5 && pig.getX()==aPos.getX()+0.5 && mahou.getStoredMana()==940,"Equivalent displacement exchanges actual entities for sixty mana");
  require(!a.preConditions() && !b.preConditions(),"Equivalent swap cools both circles");a.loadChunks();a.releaseChunks();
  var saved=a.saveWithoutMetadata(world.registryAccess());var restored=new EquivalentDisplacementMahoujinTileEntity(aPos,a.getBlockState());
  restored.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),saved));
  require(restored.getTargetLocation().equals(bPos) && restored.getTargetDimension().equals(world.dimension().identifier()),"Equivalent link survives native save/load");
  cow.discard();pig.discard();for(var pos:List.of(aPos,bPos))world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
  // A real server-side player and native world entity ticks exercise gaze binding.
  var binder=DoomIntegration.player(world,"EyesCheck",GameType.SURVIVAL,470);binder.setPos(470.5,250,540.5);binder.setYRot(180);binder.setYHeadRot(180);binder.setXRot(8);
  players.put(binder.getUUID(),binder);var binderMahou=((MahouHolder)binder).mahoutsukai$getMahou();binderMahou.setStoredMana(1000);binderMahou.setMaxMana(1000);
  var creep=EntityType.CREEPER.create(world,EntitySpawnReason.COMMAND);creep.setPos(470.5,250,537.5);creep.setYRot(0);creep.setYHeadRot(0);world.addFreshEntity(creep);
  require(cast(binder,"mystic_eyes").isEmpty() && binderMahou.getStoredMana()==680 && binder.hasEffect(ModEffects.BINDING_EYES),"Binding eyes scroll costs three hundred twenty and gives original effect");
  BindingEyesPotion.bindingEyesPlayerTick(binder);require(creep.hasEffect(ModEffects.BOUND),"Original gaze/facing binds hostile creeper");
  require(stepsword.mahoutsukai.config.MandatoryFun.meetsFunReq(binder,stepsword.mahoutsukai.config.MandatoryFun.FUN_REQS.CREEPER_BINDING_EYES),"Binding actual creeper records original progression marker");
  int ticks=creep.tickCount;world.tickNonPassenger(creep);require(creep.tickCount==ticks+1,"World tick retains native tick counter while skipping bound mob tick");
  binder.setYRot(0);binder.setYHeadRot(0);creep.setYRot(0);world.tickNonPassenger(creep);require(!creep.hasEffect(ModEffects.BOUND),"Looking away releases original binding");
  stepsword.mahoutsukai.util.EffectUtil.buff(binder,ModEffects.BOUND,false,100);
  require(net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.invoker().interact(binder,world,net.minecraft.world.InteractionHand.MAIN_HAND)==net.minecraft.world.InteractionResult.FAIL,"Bound player cannot use held item");binder.removeEffect(ModEffects.BOUND);binder.removeEffect(ModEffects.BINDING_EYES);creep.discard();
  binder.setYRot(180);binder.setYHeadRot(180);binder.setXRot(8);binder.setHealth(10);binderMahou.setStoredMana(1000);
  var target=EntityType.COW.create(world,EntitySpawnReason.COMMAND);target.setPos(470.5,250,535.5);target.setHealth(10);world.addFreshEntity(target);
  var retribution=cast(binder,"retribution");require(retribution.isEmpty() && binder.getHealth()==20 && target.getHealth()==5 && binderMahou.getStoredMana()==400,"Retribution swaps health ratios and charges original absolute health difference");
  binder.setHealth(10);target.setHealth(10);binderMahou.setStoredMana(100);require(!cast(binder,"retribution").isEmpty() && binder.getHealth()==10 && target.getHealth()==10 && binderMahou.getStoredMana()==100,"Insufficient retribution mana changes nothing and retains scroll");
  var ritualPos=new BlockPos(475,250,540);ExchangeCircleIntegration.ritual(binder,ritualPos,"mystic_eyes",true,"eye","eye","gold");world.setBlockAndUpdate(ritualPos,Blocks.AIR.defaultBlockState());ExchangeCircleIntegration.ritual(binder,ritualPos,"retribution",true,"emerald","iron","diamond");world.setBlockAndUpdate(ritualPos,Blocks.AIR.defaultBlockState());
  target.discard();binder.discard();players.remove(binder.getUUID());players.remove(player.getUUID());
  System.out.println("PASS: DISPLACEMENT_EYES_INTEGRATION: ordered sequence/cost/cooldown/sneak, equivalent shift binding/swap/persistence/tickets, binding gaze/native mob tick/release/item block/progression and retribution ratios/cost/insufficient");
 }
}
