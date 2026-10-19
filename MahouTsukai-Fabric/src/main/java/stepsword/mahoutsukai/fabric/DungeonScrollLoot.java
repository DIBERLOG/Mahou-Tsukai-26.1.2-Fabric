package stepsword.mahoutsukai.fabric;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
/** Additive loot: leaves vanilla and other mods' chest pools intact. */
public final class DungeonScrollLoot {
 private static final java.util.Set<String> CHESTS=java.util.Set.of("simple_dungeon","abandoned_mineshaft","desert_pyramid","jungle_temple","stronghold_library","stronghold_corridor","stronghold_crossing","ancient_city","woodland_mansion","bastion_treasure","end_city_treasure","pillager_outpost");
 public static boolean eligible(net.minecraft.resources.Identifier id){return id.getNamespace().equals("minecraft") && id.getPath().startsWith("chests/") && (CHESTS.contains(id.getPath().substring(7))||id.getPath().startsWith("chests/trial_chambers/reward"));}
 public static void init(){net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register((key,builder,source,registries)->{
  if(!eligible(key.identifier()))return;
  var mana=LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.65F));
  int[] amounts={50,100,250,500,1000},weights={40,30,12,5,1};
  for(int i=0;i<amounts.length;i++)mana.add(LootItem.lootTableItem(stepsword.mahoutsukai.item.ModItems.REGISTERED.get("scroll_mana_"+amounts[i])).setWeight(weights[i]));
  builder.withPool(mana);
  var spells=LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemRandomChanceCondition.randomChance(0.3F));
  stepsword.mahoutsukai.item.ModItems.REGISTERED.forEach((id,item)->{if(item instanceof stepsword.mahoutsukai.item.spells.SpellScroll)spells.add(LootItem.lootTableItem(item).setWeight(id.equals("scroll_doom")?1:10));});
  builder.withPool(spells);
 });}
}
