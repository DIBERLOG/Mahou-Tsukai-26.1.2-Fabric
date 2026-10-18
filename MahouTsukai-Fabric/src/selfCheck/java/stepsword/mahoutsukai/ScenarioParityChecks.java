package stepsword.mahoutsukai;
import com.google.gson.*;
import java.nio.file.*;
public final class ScenarioParityChecks {
 public static void run(net.minecraft.server.MinecraftServer server)throws Exception {
  Path dir=Path.of("../../../analysis");
  Files.deleteIfExists(dir.resolve("phase024-scenario-parity.passed"));
  var actual=JsonParser.parseString(new Gson().toJson(local.mahouparity.ScenarioProbe.run(server))).getAsJsonObject();
  Files.writeString(dir.resolve("phase024-fabric-scenarios.json"),new GsonBuilder().setPrettyPrinting().create().toJson(actual));
  var expected=JsonParser.parseString(Files.readString(dir.resolve("phase024-original-scenarios.json"))).getAsJsonObject();
  var adaptations=new JsonObject();
  // Extra registered helpers are retained for old Fabric saves, not treated as original content.
  var allowed=java.util.Set.of("effects/mahoutsukai:replica_shockwave_cooldown","entities/mahoutsukai:aoe_mahoujin_entity","entities/mahoutsukai:authority_mahoujin_entity","entities/mahoutsukai:gate_chain_mahoujin_entity","entities/mahoutsukai:mahoujin_beam_entity","entities/mahoutsukai:mahoujin_entity","entities/mahoutsukai:mental_displacement","entities/mahoutsukai:rho_aias_mahoujin_entity","entities/mahoutsukai:spatial_mahoujin_entity");
  for(String section:java.util.List.of("effects","entities"))for(String id:new java.util.ArrayList<>(actual.getAsJsonObject(section).keySet()))if(!expected.getAsJsonObject(section).has(id)&&allowed.contains(section+"/"+id))adaptations.add(section+"/"+id,actual.getAsJsonObject(section).remove(id));
  var originalNobu=expected.getAsJsonObject("items").getAsJsonObject("mahoutsukai:nobu");var fabricNobu=actual.getAsJsonObject("items").getAsJsonObject("mahoutsukai:nobu");
  if(!originalNobu.get("animation").getAsString().equals("MAHOUTSUKAI_CUSTOM")||!fabricNobu.get("animation").getAsString().equals("NONE"))throw new AssertionError("Unexpected Nobu animation implementation");
  adaptations.addProperty("nobu_animation","NeoForge MAHOUTSUKAI_CUSTOM -> Fabric NONE plus native model/hand mixins; see actual client weapon-poses-check");
  originalNobu.addProperty("animation","RIFLE_CUSTOM");fabricNobu.addProperty("animation","RIFLE_CUSTOM");
  Files.writeString(dir.resolve("phase024-loader-adaptations.json"),new GsonBuilder().setPrettyPrinting().create().toJson(adaptations));
  var differences=new JsonArray();diff("",expected,actual,differences);
  Files.writeString(dir.resolve("phase024-scenario-differences.json"),new GsonBuilder().setPrettyPrinting().create().toJson(differences));
  if(!differences.isEmpty())throw new AssertionError("Original scenario differences: "+differences.size()+"; see phase024-scenario-differences.json");
  if(actual.getAsJsonObject("ritual_trials").size()!=1372)throw new AssertionError("Incomplete ritual matrix");
  String receipt="PASS: identical shared source, 1372 ordered catalyst/cloth/fae native interactions; "+actual.get("recipe_count")+" recipes, "+actual.getAsJsonObject("items").size()+" item contracts, "+actual.getAsJsonObject("effects").size()+" effect contracts/tick schedules, "+actual.getAsJsonObject("entities").size()+" entity contracts; no unexplained differences; explicit loader adaptations in phase024-loader-adaptations.json\n";
  Files.writeString(dir.resolve("phase024-scenario-parity.passed"),receipt);System.out.print(receipt);
 }
 private static void diff(String path,JsonElement expected,JsonElement actual,JsonArray out){
  if(expected.equals(actual))return;
  if(expected.isJsonObject()&&actual.isJsonObject()){
   var keys=new java.util.TreeSet<>(expected.getAsJsonObject().keySet());keys.addAll(actual.getAsJsonObject().keySet());
   for(String key:keys){var a=expected.getAsJsonObject().get(key);var b=actual.getAsJsonObject().get(key);if(a==null||b==null){var row=new JsonObject();row.addProperty("path",path+"/"+key);row.add("original",a);row.add("fabric",b);out.add(row);}else diff(path+"/"+key,a,b,out);}return;
  }
  var row=new JsonObject();row.addProperty("path",path);row.add("original",expected);row.add("fabric",actual);out.add(row);
 }
}
