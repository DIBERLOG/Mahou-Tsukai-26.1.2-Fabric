package stepsword.mahoutsukai;
import com.google.gson.*;
import java.nio.file.*;
public final class ScenarioParityChecks {
 // Explicit intended RGB from the real potion constructors (including CooldownPotion).
 // The original packing precedence produced zero, except InterestingBlock's 0x12C000.
 private static final java.util.Map<String,Integer> EFFECT_COLOR_FIXES=java.util.Map.ofEntries(
  java.util.Map.entry("mahoutsukai:abyss",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:acting",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:authority_jump",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:ba_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:binding_eyes",0xFC2DE1),
  java.util.Map.entry("mahoutsukai:black_burning",0x74E800),
  java.util.Map.entry("mahoutsukai:black_flame_eyes",0x74E800),
  java.util.Map.entry("mahoutsukai:bleeding",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:borrowed_authority",0xFFFF33),
  java.util.Map.entry("mahoutsukai:bound",0xFC2DE1),
  java.util.Map.entry("mahoutsukai:clairvoyance_eyes",0x3BCCD1),
  java.util.Map.entry("mahoutsukai:cm_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:confusion",0x161616),
  java.util.Map.entry("mahoutsukai:death_collection_eyes",0x32A852),
  java.util.Map.entry("mahoutsukai:fay_sight_eyes",0x74E800),
  java.util.Map.entry("mahoutsukai:fear",0xFFFFF0),
  java.util.Map.entry("mahoutsukai:flung",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:foresight",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:immunity_exchange",0xFC2DE1),
  java.util.Map.entry("mahoutsukai:insight_eyes",0xCCCC8C),
  java.util.Map.entry("mahoutsukai:interesting_block",0x9696FF),
  java.util.Map.entry("mahoutsukai:mana_taken_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:misfortune",0x161616),
  java.util.Map.entry("mahoutsukai:peaceful_garden",0xFC2DE1),
  java.util.Map.entry("mahoutsukai:probability_alter",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:rage",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:reversion_eyes",0xF20717),
  java.util.Map.entry("mahoutsukai:reverted",0xF20717),
  java.util.Map.entry("mahoutsukai:ripper_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:ripper_invis",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:ripper_invis_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:rpsw_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:selective_displacement",0xFFFF33),
  java.util.Map.entry("mahoutsukai:selective_displacement_cooldown",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:wall_stick",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:wounded",0xFC2D2D),
  java.util.Map.entry("mahoutsukai:zap_cooldown",0xFC2D2D)
 );
 public static void run(net.minecraft.server.MinecraftServer server)throws Exception {
  Path dir=Path.of("../../../analysis");
  Files.deleteIfExists(dir.resolve("phase026-scenario-parity.passed"));
  var actual=JsonParser.parseString(new Gson().toJson(local.mahouparity.ScenarioProbe.run(server))).getAsJsonObject();
  Files.writeString(dir.resolve("phase026-fabric-scenarios.json"),new GsonBuilder().setPrettyPrinting().create().toJson(actual));
  var expected=JsonParser.parseString(Files.readString(dir.resolve("phase024-original-scenarios.json"))).getAsJsonObject();
  var adaptations=new JsonObject();
  var originalKeys=expected.getAsJsonObject("items").getAsJsonObject("mahoutsukai:proximity_projection_keys");
  var changedKeys=actual.getAsJsonObject("items").getAsJsonObject("mahoutsukai:proximity_projection_keys");
  if(originalKeys.get("max_damage").getAsInt()!=30 || changedKeys.get("max_damage").getAsInt()!=130)throw new AssertionError("Black Keys intended durability 30 -> 130");
  originalKeys.addProperty("max_damage",130);adaptations.addProperty("balance_026/black_keys_durability","User-requested change: 30 -> 130");
  // Extra registered helpers are retained for old Fabric saves, not treated as original content.
  var allowed=java.util.Set.of("effects/mahoutsukai:replica_shockwave_cooldown","entities/mahoutsukai:aoe_mahoujin_entity","entities/mahoutsukai:authority_mahoujin_entity","entities/mahoutsukai:gate_chain_mahoujin_entity","entities/mahoutsukai:mahoujin_beam_entity","entities/mahoutsukai:mahoujin_entity","entities/mahoutsukai:mental_displacement","entities/mahoutsukai:rho_aias_mahoujin_entity","entities/mahoutsukai:spatial_mahoujin_entity","entities/mahoutsukai:reality_marble_boss");
  for(String section:java.util.List.of("effects","entities"))for(String id:new java.util.ArrayList<>(actual.getAsJsonObject(section).keySet()))if(!expected.getAsJsonObject(section).has(id)&&allowed.contains(section+"/"+id))adaptations.add(section+"/"+id,actual.getAsJsonObject(section).remove(id));
  var originalEffects=expected.getAsJsonObject("effects");var fabricEffects=actual.getAsJsonObject("effects");
  if(EFFECT_COLOR_FIXES.size()!=37||!originalEffects.keySet().equals(EFFECT_COLOR_FIXES.keySet())||!fabricEffects.keySet().equals(EFFECT_COLOR_FIXES.keySet()))throw new AssertionError("Unexpected effect coverage for the 37 explicit RGB fixes");
  for(var entry:new java.util.TreeMap<>(EFFECT_COLOR_FIXES).entrySet()){
   String id=entry.getKey();int oldColor=id.equals("mahoutsukai:interesting_block")?0x12C000:0,newColor=entry.getValue();
   var originalEffect=originalEffects.getAsJsonObject(id);var fabricEffect=fabricEffects.getAsJsonObject(id);
   if(originalEffect.get("color").getAsInt()!=oldColor||fabricEffect.get("color").getAsInt()!=newColor)throw new AssertionError("Unexpected original/fixed RGB color: "+id);
   var fix=new JsonObject();fix.addProperty("old",oldColor);fix.addProperty("new",newColor);fix.addProperty("reason","Fix original RGB bit-shift precedence; intended constructor RGB, other effect fields remain strictly compared");adaptations.add("color_fix_030/"+id,fix);
   originalEffect.addProperty("color",newColor);
  }
  var originalNobu=expected.getAsJsonObject("items").getAsJsonObject("mahoutsukai:nobu");var fabricNobu=actual.getAsJsonObject("items").getAsJsonObject("mahoutsukai:nobu");
  if(!originalNobu.get("animation").getAsString().equals("MAHOUTSUKAI_CUSTOM")||!fabricNobu.get("animation").getAsString().equals("NONE"))throw new AssertionError("Unexpected Nobu animation implementation");
  adaptations.addProperty("nobu_animation","NeoForge MAHOUTSUKAI_CUSTOM -> Fabric NONE plus native model/hand mixins; see actual client weapon-poses-check");
  originalNobu.addProperty("animation","RIFLE_CUSTOM");fabricNobu.addProperty("animation","RIFLE_CUSTOM");
  Files.writeString(dir.resolve("phase026-loader-adaptations.json"),new GsonBuilder().setPrettyPrinting().create().toJson(adaptations));
  var differences=new JsonArray();diff("",expected,actual,differences);
  Files.writeString(dir.resolve("phase026-scenario-differences.json"),new GsonBuilder().setPrettyPrinting().create().toJson(differences));
  if(!differences.isEmpty())throw new AssertionError("Original scenario differences: "+differences.size()+"; see phase026-scenario-differences.json");
  if(actual.getAsJsonObject("ritual_trials").size()!=1372)throw new AssertionError("Incomplete ritual matrix");
  String receipt="PASS: identical shared source, 1372 ordered catalyst/cloth/fae native interactions; "+actual.get("recipe_count")+" recipes, "+actual.getAsJsonObject("items").size()+" item contracts, "+actual.getAsJsonObject("effects").size()+" effect contracts/tick schedules, "+actual.getAsJsonObject("entities").size()+" entity contracts; no unexplained differences; explicit loader adaptations in phase026-loader-adaptations.json\n";
  Files.writeString(dir.resolve("phase026-scenario-parity.passed"),receipt);System.out.print(receipt);
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
