package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.item.kodoku.KodokuItem;
import stepsword.mahoutsukai.entity.kodoku.KodokuEntity;
import stepsword.mahoutsukai.effects.Kodoku;
import stepsword.mahoutsukai.potion.ModEffects;
public final class SoulsKodokuIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 private static ItemStack cast(ServerPlayer player,String name) {
  var stack=new ItemStack(ModItems.REGISTERED.get("scroll_"+name));((SpellScroll)stack.getItem()).releaseUsing(stack,player.level(),player,71999);return stack;
 }
 public static void run(ServerPlayer player) {
  var world=player.level();var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  player.getInventory().clearContent();mahou.setStoredMana(1000);mahou.setMaxMana(1000);mahou.setDeathCollectionUsesLeft(0);
  var arrow=new net.minecraft.world.entity.projectile.arrow.Arrow(world,player,new ItemStack(Items.ARROW),null);arrow.setPos(550,252,550);world.addFreshEntity(arrow);
  require(mahou.getLastArrowShot().equals(arrow.getUUID()),"Native projectile load tracks original last arrow UUID");
  require(cast(player,"projectile_displacement").isEmpty() && player.getX()==550 && player.getY()==253 && mahou.getStoredMana()==950,"Projectile scroll teleports to last arrow and charges fifty");arrow.discard();
  int before=mahou.getStoredMana();require(!cast(player,"projectile_displacement").isEmpty() && mahou.getStoredMana()==before,"Missing arrow retains scroll and mana");
  var collector=DoomIntegration.player(world,"SoulCheck",GameType.SURVIVAL,550);collector.setPos(550.5,250,560.5);collector.setYRot(180);collector.setYHeadRot(180);collector.setXRot(0);
  var souls=((MahouHolder)collector).mahoutsukai$getMahou();souls.setMaxMana(1000);souls.setStoredMana(1000);souls.setDeathCollectionUsesLeft(0);
  require(cast(collector,"death_collection").isEmpty() && souls.getStoredMana()==600 && collector.hasEffect(ModEffects.DEATH_COLLECTION_EYES),"Original soul scroll costs four hundred");
  for(int i=0;i<4;i++) {
   var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(550.5,250,555.5);world.addFreshEntity(cow);cow.hurtServer(world,cow.damageSources().genericKill(),1000);cow.discard();
  }
  require(souls.getDeathCollectionUsesLeft()==1,"Four visible actual mob deaths collect one original soul");
  collector.invulnerableTime=0;collector.hurtServer(world,collector.damageSources().generic(),1000);
  require(collector.isAlive() && collector.getHealth()==12 && souls.getDeathCollectionUsesLeft()==0,"Lethal native damage consumes soul and revives at original twelve health");
  souls.setDeathCollectionUsesLeft(1);collector.invulnerableTime=0;collector.hurtServer(world,collector.damageSources().fellOutOfWorld(),1000);
  require(!collector.isAlive() && souls.getDeathCollectionUsesLeft()==1,"Void death bypasses original soul revival");collector.discard();
  var creaturePos=new BlockPos(530,250,570);
  world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(creaturePos).inflate(10),e->e.getItem().is(ModItems.REGISTERED.get("kodoku"))).forEach(Entity::discard);player.setPos(530.5,250,575.5);player.setHealth(20);Kodoku.setKodokuValue(player,5);
  var victim=EntityType.COW.create(world,EntitySpawnReason.COMMAND);victim.setPos(Vec3.atCenterOf(creaturePos));world.addFreshEntity(victim);Kodoku.setKodokuValue(victim,4);victim.hurtServer(world,player.damageSources().playerAttack(player),1000);
  require(Kodoku.getKodokuValue(player)==10 && Kodoku.getKodokuValue(victim)==0,"Actual kill transfers original Kodoku value and kill increment");victim.discard();
  var host=EntityType.COW.create(world,EntitySpawnReason.COMMAND);host.setPos(Vec3.atCenterOf(creaturePos));world.addFreshEntity(host);Kodoku.setKodokuValue(host,7);
  var wormItem=new ItemStack(ModItems.REGISTERED.get("kodoku"));player.setItemInHand(InteractionHand.MAIN_HAND,wormItem);wormItem.getItem().interactLivingEntity(wormItem,player,host,InteractionHand.MAIN_HAND);
  require(wormItem.isEmpty() && host.getPassengers().size()==1 && host.getFirstPassenger() instanceof KodokuEntity,"Original Kodoku item attaches native worm to living host");
  var worm=(KodokuEntity)host.getFirstPassenger();host.hurtServer(world,player.damageSources().playerAttack(player),1000);
  require(worm.kodoku==7 && Kodoku.getKodokuValue(player)==0,"Host death transfers accumulated value to attached worm");host.discard();worm.stopRiding();
  var output=net.minecraft.world.level.storage.TagValueOutput.createWithoutContext(net.minecraft.util.ProblemReporter.DISCARDING);worm.saveWithoutId(output);
  var reloaded=new KodokuEntity(world);reloaded.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),output.buildResult()));require(reloaded.kodoku==7,"Kodoku entity value survives native save/load");
  worm.mobInteract(player,InteractionHand.MAIN_HAND);
  var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(creaturePos).inflate(3),e->e.getItem().is(ModItems.REGISTERED.get("kodoku")));
  System.out.println("Kodoku pickup fixture worm="+worm.position()+" removed="+worm.isRemoved()+" drops="+drops.size()+" values="+drops.stream().map(e->KodokuItem.getKodoku(e.getItem())).toList());
  require(worm.isRemoved() && drops.size()==1 && KodokuItem.getKodoku(drops.getFirst().getItem())==7,"Picking up creature preserves value in original component");
  var copy=drops.getFirst().getItem().copy();KodokuItem.setKodoku(copy,3);require(KodokuItem.getKodoku(drops.getFirst().getItem())==7,"Kodoku item copies keep independent mutable state");drops.getFirst().discard();
  var batteryPos=creaturePos.offset(2,0,0);world.setBlockAndUpdate(batteryPos,stepsword.mahoutsukai.block.ModBlocks.circuit.get().defaultBlockState());var battery=(stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity)world.getBlockEntity(batteryPos);battery.setMana(0);battery.setOn(true);
  var burn=new KodokuEntity(world);burn.kodoku=3;burn.setPos(Vec3.atCenterOf(creaturePos));world.addFreshEntity(burn);burn.hurtServer(world,burn.damageSources().inFire(),1000);
  int ley=stepsword.mahoutsukai.effects.Leylines.leyDimensionValid(world.dimension().identifier()) ? stepsword.mahoutsukai.effects.Leylines.nearbyLeyValue(creaturePos) : 0;
  System.out.println("Kodoku burn fixture alive="+burn.isAlive()+" value="+burn.kodoku+" expected="+(3*(8+ley))+" actual="+battery.getMana()+" batteries="+stepsword.mahoutsukai.mana.PlayerManaManager.nearbyBatteries(creaturePos,world).stream().map(te->te.getBlockPos()+":"+((stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity)te).getMana()).toList());
  require(battery.getMana()==3*(8+ley),"Burning real Kodoku charges nearby native battery with original ley multiplier");burn.discard();world.setBlockAndUpdate(batteryPos,Blocks.AIR.defaultBlockState());
  mahou.setFaeSpawn(true);player.getFoodData().setFoodLevel(10);new ItemStack(ModItems.REGISTERED.get("faecake")).finishUsingItem(world,player);
  require(!mahou.getFaeSpawn() && player.getFoodData().getFoodLevel()==13,"Original faecake nourishes and disables fae regeneration spawns");mahou.setFaeSpawn(false);
  for(var recipe:List.of(new String[]{"projectile_displacement","ender","ender","diamond"},new String[]{"death_collection","eye","eye","iron"})) {
   ExchangeCircleIntegration.ritual(player,creaturePos,recipe[0],true,recipe[1],recipe[2],recipe[3]);world.setBlockAndUpdate(creaturePos,Blocks.AIR.defaultBlockState());
  }
  System.out.println("PASS: SOULS_KODOKU_INTEGRATION: last projectile/native teleport/cost, observed souls/revival/void, creature attachment/kill transfer/save/pickup/item isolation/battery burning and faecake");
 }
}
