package stepsword.mahoutsukai.fabric;
import com.google.gson.*;
import java.util.*;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.attachment.v1.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
/** Server-owned book discoveries. Counts are ingredient units, including duplicate powders. */
public final class GuideCatalog {
 public record Entry(String id,String item,String kind,List<String> ingredients,boolean cloth,List<String> pattern,int output){}
 private static final net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> DOOM_RECIPE=net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE,Identifier.fromNamespaceAndPath("mahoutsukai","scroll_doom"));
 private static List<Entry> entries=List.of();
 public static final AttachmentType<List<String>> KNOWLEDGE=AttachmentRegistry.create(Identifier.fromNamespaceAndPath("mahoutsukai","guide_discoveries"),b->b.initializer(()->List.of()).persistent(com.mojang.serialization.Codec.STRING.listOf()).copyOnDeath());
 public static void init(){
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(GuideCatalog::reload);
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server,resources,success)->{if(success)reload(server);});
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.register(server->entries=List.of());
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server->{if(server.overworld().getGameTime()%20==0)for(var player:server.getPlayerList().getPlayers())discover(player);});
 }
 private static String ritualKey(List<String> ingredients){var sorted=new ArrayList<>(ingredients);Collections.sort(sorted);return "ritual/"+String.join("+",sorted);}
 public static void reload(net.minecraft.server.MinecraftServer server){
  var result=new ArrayList<Entry>();
  for(var pair:MahoujinRecipeRegistrar.recipes.entrySet()){
   var ingredients=new ArrayList<String>();for(String key:pair.getKey().getKeys())for(int i=0;i<pair.getKey().get(key);i++)ingredients.add(BuiltInRegistries.ITEM.getKey(MahoujinRecipeRegistrar.ingredients.get(key)).toString());
   String item=BuiltInRegistries.BLOCK.getKey(pair.getValue()).toString();
   if(pair.getValue() instanceof net.minecraft.world.level.block.EntityBlock block){
    var tile=block.newBlockEntity(net.minecraft.core.BlockPos.ZERO,pair.getValue().defaultBlockState());
    if(tile instanceof stepsword.mahoutsukai.tile.ScrollGiver giver){var output=giver.getItemToGive();if(!output.isEmpty())item=BuiltInRegistries.ITEM.getKey(output.getItem()).toString();}
   }
   String key=ritualKey(ingredients);boolean cloth=Boolean.TRUE.equals(MahoujinRecipeRegistrar.needsCloth.get(pair.getKey()));if(cloth)ingredients.add("mahoutsukai:spell_cloth");
   result.add(new Entry(key,item,"ritual",List.copyOf(ingredients),cloth,List.of(),1));
  }
  for(var pair:server.getResourceManager().listResources("recipe",id->id.getNamespace().equals("mahoutsukai")&&id.getPath().endsWith(".json")).entrySet()){
   try(var reader=pair.getValue().openAsReader()){
    var data=JsonParser.parseReader(reader).getAsJsonObject();String type=data.get("type").getAsString();if(!type.equals("minecraft:crafting_shaped")&&!type.equals("minecraft:crafting_shapeless"))continue;
    // Keep the physical recipe on the server, but never retain its ingredients in book data.
    if(data.getAsJsonObject("result").get("id").getAsString().equals("mahoutsukai:scroll_doom"))continue;
    var ingredients=new ArrayList<String>();var pattern=new ArrayList<String>();
    if(data.has("pattern")){var keys=data.getAsJsonObject("key");for(var row:data.getAsJsonArray("pattern")){StringBuilder line=new StringBuilder();for(char c:row.getAsString().toCharArray()){if(c==' ')line.append(" ");else{String ingredient=keys.get(String.valueOf(c)).getAsString();if(ingredient.startsWith("#"))throw new IllegalArgumentException("Tag ingredients need recipe display support");ingredients.add(ingredient);line.append(c);}}pattern.add(line.toString());}}
    else for(var ingredient:data.getAsJsonArray("ingredients"))ingredients.add(ingredient.getAsString());
    var output=data.getAsJsonObject("result");String path=pair.getKey().getPath();result.add(new Entry("craft/"+path.substring(7,path.length()-5),output.get("id").getAsString(),"craft",List.copyOf(ingredients),false,List.copyOf(pattern),output.has("count")?output.get("count").getAsInt():1));
   }catch(Exception e){org.slf4j.LoggerFactory.getLogger("mahoutsukai").warn("Book recipe display unavailable for {}: {}",pair.getKey(),e.getMessage());}
  }
  for(var item:ModItems.REGISTERED.entrySet())if(item.getValue() instanceof stepsword.mahoutsukai.item.spells.SpellScroll && result.stream().noneMatch(e->e.item().equals("mahoutsukai:"+item.getKey()))){boolean secret=item.getKey().equals("scroll_doom");result.add(new Entry((secret?"secret/":"spell/")+item.getKey(),"mahoutsukai:"+item.getKey(),secret?"secret":"spell",List.of(),false,List.of(),1));}
  for(var item:ModItems.REGISTERED.entrySet())if(result.stream().noneMatch(e->e.item().equals("mahoutsukai:"+item.getKey())))result.add(new Entry("item/"+item.getKey(),"mahoutsukai:"+item.getKey(),item.getValue() instanceof stepsword.mahoutsukai.item.ManaCapacityScroll?"loot":"special",List.of(),false,List.of(),1));
  result.sort(Comparator.comparing(Entry::id));entries=List.copyOf(result);
 }
 public static boolean meetsThreshold(List<String> ingredients,Map<String,Integer> inventory){
  if(ingredients.isEmpty())return false;var required=new HashMap<String,Integer>();ingredients.forEach(id->required.merge(id,1,Integer::sum));int found=0;
  for(var e:required.entrySet())found+=Math.min(e.getValue(),inventory.getOrDefault(e.getKey(),0));return found>=(ingredients.size()+1)/2;
 }
 public static void discover(ServerPlayer player){
  var counts=new HashMap<String,Integer>();for(int i=0;i<player.getInventory().getContainerSize();i++){var stack=player.getInventory().getItem(i);if(!stack.isEmpty())counts.merge(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),stack.getCount(),Integer::sum);}
  player.getRecipeBook().remove(DOOM_RECIPE);player.getRecipeBook().removeHighlight(DOOM_RECIPE);
  var known=new HashSet<>(player.getAttachedOrCreate(KNOWLEDGE));boolean changed=known.remove("craft/scroll_doom");
  for(var entry:entries)if(!known.contains(entry.id())&&meetsThreshold(entry.ingredients(),counts)){known.add(entry.id());changed=true;}
  int before=known.size();ResearchProgress.discover(player,entries,counts,known);
  if(changed||known.size()!=before)player.setAttached(KNOWLEDGE,List.copyOf(known));
 }
 public static String payload(ServerPlayer player){
  discover(player);var known=player.getAttachedOrCreate(KNOWLEDGE);var rows=new JsonArray();
  for(var entry:entries){var row=new Gson().toJsonTree(entry).getAsJsonObject();row.addProperty("unlocked",!entry.item().startsWith("mahoutsukai:scroll_")||entry.ingredients().isEmpty()||known.contains(entry.id())||player.isCreative());rows.add(row);}
  ResearchProgress.append(rows,entries,new HashSet<>(known));
  return rows.toString();
 }
 public static void send(ServerPlayer player){stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.GuideCatalogPacket(payload(player)));}
}
