package stepsword.mahoutsukai.fabric;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
/** Additive loot: leaves vanilla and other mods' chest pools intact. */
public final class DungeonScrollLoot {
 private static final java.util.Set<String> CHESTS=java.util.Set.of("simple_dungeon","abandoned_mineshaft","desert_pyramid","jungle_temple","stronghold_library","stronghold_corridor","stronghold_crossing","ancient_city","woodland_mansion","bastion_treasure","end_city_treasure","pillager_outpost");
 /** Book-heavy chests where a written artifact is thematically at home. */
 private static final java.util.Set<String> LIBRARIES=java.util.Set.of("stronghold_library","woodland_mansion","ancient_city");
 /** A modest written artifact: enough style and fame to work immediately, not a blank item. */
 public static final int WILLIAM_STYLE=12,WILLIAM_FAME=3;
 public static boolean eligible(net.minecraft.resources.Identifier id){return id.getNamespace().equals("minecraft") && id.getPath().startsWith("chests/") && (CHESTS.contains(id.getPath().substring(7))||id.getPath().startsWith("chests/trial_chambers/reward"));}
 public static boolean library(net.minecraft.resources.Identifier id){return id.getNamespace().equals("minecraft") && id.getPath().startsWith("chests/") && (LIBRARIES.contains(id.getPath().substring(7))||id.getPath().startsWith("chests/trial_chambers/reward"));}
 /** A found William must arrive as a working artifact, with real fiction data and readable content. */
 public static net.minecraft.world.item.ItemStack foundWilliam(){
  var stack=new net.minecraft.world.item.ItemStack(stepsword.mahoutsukai.item.ModItems.william.get());
  stack.set(DataComponents.FICTION_COMPONENT,new FictionMahou(WILLIAM_FAME,WILLIAM_STYLE,stepsword.mahoutsukai.fabric.MahouIds.FAE_UUID));
  stack.set(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(
   Filterable.passThrough("Excerpt"),
   "Unknown",
   0,
   List.of(Filterable.passThrough(Component.translatable("mahoutsukai.william.found_page"))),
   true));
  return stack;
 }
 public static void init(){net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register((key,builder,source,registries)->{
  if(!eligible(key.identifier()))return;
  var mana=LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.65F));
  int[] amounts={50,100,250,500,1000},weights={40,30,12,5,1};
  for(int i=0;i<amounts.length;i++)mana.add(LootItem.lootTableItem(stepsword.mahoutsukai.item.ModItems.REGISTERED.get("scroll_mana_"+amounts[i])).setWeight(weights[i]));
  builder.withPool(mana);
  var spells=LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.3F));
  stepsword.mahoutsukai.item.ModItems.REGISTERED.forEach((id,item)->{if(item instanceof stepsword.mahoutsukai.item.spells.SpellScroll)spells.add(LootItem.lootTableItem(item).setWeight(id.equals("scroll_doom")?1:10));});
  builder.withPool(spells);
  if(library(key.identifier())){
   var fiction=LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.05F));
   var entry=LootItem.lootTableItem(stepsword.mahoutsukai.item.ModItems.william.get());
   entry.apply(SetComponentsFunction.setComponent(DataComponents.FICTION_COMPONENT,new FictionMahou(WILLIAM_FAME,WILLIAM_STYLE,MahouIds.FAE_UUID)));
   entry.apply(SetComponentsFunction.setComponent(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(
    Filterable.passThrough("Excerpt"),"Unknown",0,
    List.of(Filterable.passThrough(Component.translatable("mahoutsukai.william.found_page"))),true)));
   entry.setWeight(1);
   fiction.add(entry);
   builder.withPool(fiction);
  }
 });}
}
