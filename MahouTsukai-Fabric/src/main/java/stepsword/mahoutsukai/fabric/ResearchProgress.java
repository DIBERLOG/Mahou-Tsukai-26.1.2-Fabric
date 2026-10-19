package stepsword.mahoutsukai.fabric;

import java.util.*;
import com.google.gson.*;
import net.minecraft.server.level.ServerPlayer;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

/** A guide to existing mechanics, not an extra restriction on crafting or combat. */
public final class ResearchProgress {
 public record Node(String key,String item,List<String> parents,List<String> goals,int mana,String recipe,int column,int row) {}
 private static List<GuideCatalog.Entry> source;private static List<Node> cached;
 public static List<Node> nodes(List<GuideCatalog.Entry> recipes){
  if(source==recipes&&cached!=null)return cached;
  var nodes=new ArrayList<Node>();
  nodes.add(node("welcome","guidebook",List.of(),List.of("guidebook"),0,0,0));
  nodes.add(node("tools","hammer",List.of("welcome"),List.of("hammer","dagger"),0,1,0));
  nodes.add(node("powders","powdered_quartz",List.of("tools"),List.of("powdered_quartz"),0,2,-1));
  nodes.add(node("cloth","spell_cloth",List.of("tools"),List.of("spell_cloth"),0,2,1));
  nodes.add(node("first_scroll","scroll_summon_familiar",List.of("powders","cloth"),List.of(),0,3,0));
  nodes.add(node("awakening","attuned_diamond",List.of("tools"),List.of(),0,1,3));
  nodes.add(node("mana250","scroll_mana_50",List.of("awakening"),List.of(),250,2,3));
  nodes.add(node("mana1000","scroll_mana_1000",List.of("mana250"),List.of(),1000,3,3));
  nodes.add(node("mana20000","attuned_emerald",List.of("mana1000"),List.of(),20000,4,3));
  String[] families={"familiar","eyes","boundary","exchange","displacement","projection","mystic","secret"};
  for(int f=0;f<families.length;f++){
   String family=families[f];int row=(f-3)*4;var matching=recipes.stream().filter(e->e.kind().equals("ritual")&&family(e.item()).equals(family)).sorted(Comparator.comparing(GuideCatalog.Entry::item)).toList();
   String icon=matching.isEmpty()?"mahoutsukai:guidebook":matching.getFirst().item();
   nodes.add(new Node("family_"+family,icon,List.of("first_scroll"),List.of(),0,"",4,row));
   for(int i=0;i<matching.size();i++){var recipe=matching.get(i);nodes.add(new Node("learn_"+recipe.id(),recipe.item(),List.of("family_"+family),List.of(),0,recipe.id(),5+i/3,row+i%3-1));}
  }
  nodes.add(node("helper","scroll_summon_familiar",List.of("family_familiar"),List.of(),0,8,-12));
  nodes.add(node("lake","murky_bucket",List.of("family_projection"),List.of("murky_bucket"),0,8,8));
  String[][] weapons={{"clarent","clarent"},{"caliburn","caliburn"},{"morgan","morgan"},{"nobu","nobu"},{"emrys","staff_emrys"},{"rule_breaker","rule_breaker"},{"keys","proximity_projection_keys"},{"mystic_code","mystic_code"}};
  for(int i=0;i<weapons.length;i++)nodes.add(node("weapon_"+weapons[i][0],weapons[i][1],List.of("first_scroll"),List.of(weapons[i][1]),0,9,i*2-6));
  nodes.add(node("sculk","powdered_sculk",List.of("powders"),List.of("powdered_sculk"),0,3,20));
  nodes.add(node("doom","scroll_doom",List.of("mana1000"),List.of("scroll_doom"),0,6,20));
  nodes.add(new Node("fireball","mahoutsukai:scroll_fireball",List.of("family_mystic"),List.of(),0,"craft/scroll_fireball",8,14));
  source=recipes;cached=List.copyOf(nodes);return cached;
 }
 private static Node node(String key,String item,List<String> parents,List<String> goals,int mana,int x,int y){return new Node(key,"mahoutsukai:"+item,parents,goals.stream().map(s->"mahoutsukai:"+s).toList(),mana,"",x,y);}
 private static String family(String item){
  var value=net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(item));
  if(value instanceof SpellScroll)return SpellPresentation.of(value).soundKey();
  // Ground-only circles have no inventory item; classify from the recovered tile package.
  var block=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(item));
  String type=block.getClass().getPackageName();for(String name:List.of("familiar","eyes","boundary","exchange","displacement","projection","secret"))if(type.contains("."+name))return name;
  return "mystic";
 }
 public static void discover(ServerPlayer player,List<GuideCatalog.Entry> recipes,Map<String,Integer> inventory,Set<String> known){
  var nodes=nodes(recipes);
  for(var node:nodes)if(satisfied(node,player,inventory,known))known.add("research-seen/"+node.key());
  // Earlier acquisitions stay credited when a prerequisite is completed later.
  boolean changed;do{changed=false;for(var node:nodes)if(!known.contains("research/"+node.key())&&known.contains("research-seen/"+node.key())&&node.parents().stream().allMatch(p->known.contains("research/"+p))){known.add("research/"+node.key());changed=true;}}while(changed);
 }
 private static boolean satisfied(Node node,ServerPlayer player,Map<String,Integer> inventory,Set<String> known){
  if(!node.recipe().isEmpty())return known.contains(node.recipe());
  if(node.mana()>0)return Utils.getPlayerMahou(player).getMaxMana()>=node.mana();
  if(node.key().equals("awakening"))return Utils.getPlayerMahou(player).hasMagic();
  if(node.key().equals("helper")){var helper=stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.get(player.getUUID());return helper!=null&&helper.isAlive();}
  if(node.key().equals("first_scroll")||node.key().startsWith("family_"))return inventory.keySet().stream().anyMatch(id->{var item=net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(id));return item instanceof SpellScroll&&(node.key().equals("first_scroll")||family(id).equals(node.key().substring(7)));});
  if(node.goals().isEmpty())return false;var counts=new HashMap<String,Integer>();node.goals().forEach(id->counts.merge(id,1,Integer::sum));return counts.entrySet().stream().allMatch(e->inventory.getOrDefault(e.getKey(),0)>=e.getValue());
 }
 public static void append(JsonArray rows,List<GuideCatalog.Entry> recipes,Set<String> known,boolean creative){
  for(var node:nodes(recipes)){var row=new Gson().toJsonTree(node).getAsJsonObject();row.addProperty("kind","research");row.addProperty("done",known.contains("research/"+node.key()));row.addProperty("available",node.parents().stream().allMatch(p->known.contains("research/"+p)));var entry=node.recipe().isEmpty()?null:recipes.stream().filter(e->e.id().equals(node.recipe())).findFirst().orElse(null);if(entry!=null&&(creative||known.contains(entry.id())||!entry.item().startsWith("mahoutsukai:scroll_"))){row.add("ingredients",new Gson().toJsonTree(entry.ingredients()));row.addProperty("cloth",entry.cloth());row.addProperty("entryKind",entry.kind());}rows.add(row);}
 }
}
