package stepsword.mahoutsukai;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.exchange.*;
import stepsword.mahoutsukai.fabric.MahouHolder;
public final class ExchangeCircleIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 public static MahoujinTileEntity ritual(ServerPlayer player,BlockPos pos,String expected,boolean cloth,String...powders) {
  var world=player.level();world.setBlockAndUpdate(pos,ModBlocks.mahoujin.get().defaultBlockState());
  ((MahoujinTileEntity)world.getBlockEntity(pos)).setCloth(cloth);
  var hit=new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false);
  for(String powder:powders) {
   var stack=new ItemStack(ModItems.REGISTERED.get("powdered_"+powder));player.setItemInHand(InteractionHand.MAIN_HAND,stack);
   world.getBlockState(pos).useItemOn(stack,world,player,InteractionHand.MAIN_HAND,hit);
   require(stack.isEmpty(),"Catalyst consumed for "+expected);
  }
  require(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock()).getPath().equals("mahoujin_"+expected),"Original recipe creates "+expected);
  var tile=(MahoujinTileEntity)world.getBlockEntity(pos);require(tile.getCasterUUID().equals(player.getUUID()),"Ritual binds original caster");return tile;
 }
 public static void run(ServerPlayer player) {
  var world=player.level();var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(player.getUUID(),player);
  player.getInventory().clearContent();player.setPos(580.5,250,580.5);mahou.setMaxMana(1000);mahou.setStoredMana(100);
  var pos=new BlockPos(580,250,580);
  var chronal=(ChronalExchangeMahoujinTileEntity)ritual(player,pos,"chronal_exchange",false,"emerald","emerald","quartz");
  require(chronal.getManaFromTime(0)==10,"Chronal initial original phase gains ten");chronal.doManaGenOrLoss();require(mahou.getStoredMana()==110,"Chronal adds mana to caster");
  chronal.setTime(12000);require(chronal.getManaFromTime(0)==-10,"Chronal half-cycle loses ten");chronal.doManaGenOrLoss();require(mahou.getStoredMana()==100,"Chronal drains original mana sources");
  var saved=chronal.saveWithoutMetadata(world.registryAccess());
  var restored=new ChronalExchangeMahoujinTileEntity(pos,chronal.getBlockState());
  restored.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),saved));
  require(restored.getManaFromTime(0)==-10,"Chronal phase persists in native block entity NBT");
  var catalyst=(CatalystExchangeMahoujinTileEntity)ritual(player,pos,"catalyst_exchange",false,"emerald","emerald","emerald");
  var input=new net.minecraft.world.entity.item.ItemEntity(world,580,250,580,new ItemStack(ModItems.REGISTERED.get("powdered_iron")));
  int before=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2)).size();
  catalyst.ifItemFoundAbove(input);
  require(input.isRemoved() && mahou.getStoredMana()==50,"Catalyst exchange consumes original and costs fifty");
  var generated=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2));
  require(generated.size()==before+1 && generated.getLast().getItem().getItem() instanceof stepsword.mahoutsukai.item.catalysts.PowderedCatalyst,"Catalyst exchange creates original randomized catalyst");generated.getLast().discard();
  var durability=(DurabilityExchangeMahoujinTileEntity)ritual(player,pos,"durability_exchange",false,"emerald","emerald","diamond");
  mahou.setStoredMana(0);var tool=new ItemStack(Items.IRON_PICKAXE);var dropped=new net.minecraft.world.entity.item.ItemEntity(world,580,250,580,tool);
  durability.consumeDurabilityOnTop(dropped);require(tool.getDamageValue()==10 && mahou.getStoredMana()==10,"Dropped tool exchanges ten durability per original configured cycle cap");
  world.setBlockAndUpdate(pos.below(),Blocks.CHEST.defaultBlockState());var chest=(net.minecraft.world.level.block.entity.ChestBlockEntity)world.getBlockEntity(pos.below());
  chest.setItem(0,new ItemStack(Items.IRON_PICKAXE));mahou.setStoredMana(0);durability.consumeDurabilityBelow();
  require(chest.getItem(0).getDamageValue()==10 && mahou.getStoredMana()==10,"Fabric storage transaction damages chest tool and restores mana");
  stepsword.mahoutsukai.config.MTConfig.DURABILITY_EXCHANGE_BLACKLIST=java.util.List.of("minecraft:iron_pickaxe");durability.consumeDurabilityBelow();
  require(chest.getItem(0).getDamageValue()==10 && mahou.getStoredMana()==10,"Original item blacklist prevents exchange");stepsword.mahoutsukai.config.MTConfig.DURABILITY_EXCHANGE_BLACKLIST=java.util.List.of();
  var alchemical=(AlchemicalExchangeMahoujinTileEntity)ritual(player,pos,"alchemical_exchange",false,"emerald","emerald","gold");
  for(var block:BlockPos.betweenClosed(pos.offset(-2,-1,-2),pos.offset(2,-1,2))) world.setBlockAndUpdate(block,Blocks.AIR.defaultBlockState());
  var target=pos.below();world.setBlockAndUpdate(target,Blocks.DIRT.defaultBlockState());mahou.setStoredMana(1000);
  var indices=new java.util.ArrayList<Integer>(java.util.Collections.nCopies(10,0));
  alchemical.transformLayer(1,indices,player);require(mahou.getStoredMana()==984,"Alchemical class transformation spends sixteen per eligible block");
  var blocked=pos.offset(1,-1,0);world.setBlockAndUpdate(blocked,Blocks.DIRT.defaultBlockState());
  net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((w,p,b,s,te)->!b.equals(blocked));
  var original=world.getBlockState(blocked);alchemical.transformLayer(1,indices,player);require(world.getBlockState(blocked)==original,"Native claim cancellation prevents alchemical block replacement");
  world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());dropped.discard();player.getInventory().clearContent();
  // All three defensive scrolls must also be obtainable through original cloth ritual clicks.
  String[][] recipes={{"damage_exchange","emerald","emerald","iron"},{"immunity_exchange","emerald","emerald","eye"},{"protective_displacement","ender","ender","iron"}};
  for(var recipe:recipes) {
   ritual(player,pos,recipe[0],true,recipe[1],recipe[2],recipe[3]);player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
   world.getBlockState(pos).useWithoutItem(world,player,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
   var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_"+recipe[0])));
   require(world.getBlockState(pos).isAir() && drops.size()==1,"Native ritual produces "+recipe[0]+" scroll");drops.forEach(e->e.discard());
  }
  players.remove(player.getUUID());
  System.out.println("PASS: EXCHANGE_CIRCLE_INTEGRATION: seven original recipes, scroll binding, chronal phase/gain/loss/save, catalysts, dropped/chest durability, blacklist, alchemy and claim cancellation");
 }
}
