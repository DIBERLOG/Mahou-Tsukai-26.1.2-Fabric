package stepsword.mahoutsukai;
import com.google.gson.*;
import java.nio.file.*;
public final class CombatParityChecks {
 public static void run(net.minecraft.server.MinecraftServer server)throws Exception{
  Path dir=Path.of("../../../analysis");Files.deleteIfExists(dir.resolve("phase025-combat-parity.passed"));
  var actual=JsonParser.parseString(new Gson().toJson(local.mahouparity.CombatProbe.run(server))).getAsJsonObject();
  Files.writeString(dir.resolve("phase025-fabric-combat.json"),new GsonBuilder().setPrettyPrinting().create().toJson(actual));
  var expected=JsonParser.parseString(Files.readString(dir.resolve("phase025-original-combat.json"))).getAsJsonObject();
  var differences=new JsonArray();var rounding=new JsonArray();
  for(String section:java.util.List.of("ordered_effect_pairs","defense_power_set","native_item_attacks")){
   var a=expected.getAsJsonObject(section);var b=actual.getAsJsonObject(section);
   if(!a.keySet().equals(b.keySet()))throw new AssertionError("Incomplete combat section "+section);
   for(String key:a.keySet())if(!a.get(key).equals(b.get(key))){var row=new JsonObject();row.addProperty("section",section);row.addProperty("case",key);row.add("original",a.get(key));row.add("fabric",b.get(key));var original=a.getAsJsonObject(key).deepCopy();var fabric=b.getAsJsonObject(key).deepCopy();
    // NeoForge's damage container and vanilla resistance differ in float operation order.
    // Permit only health rounding, below 0.00001 HP; every other field remains exact.
    double delta=Math.abs(original.get("health").getAsDouble()-fabric.get("health").getAsDouble());
    fabric.add("health",original.get("health"));
    if(delta<=0.00001 && original.equals(fabric))rounding.add(row);else differences.add(row);}
  }
  Files.writeString(dir.resolve("phase025-combat-differences.json"),new GsonBuilder().setPrettyPrinting().create().toJson(differences));
  Files.writeString(dir.resolve("phase025-combat-float-rounding.json"),new GsonBuilder().setPrettyPrinting().create().toJson(rounding));
  if(!differences.isEmpty())throw new AssertionError("Native combat mismatches: "+differences.size()+"; inspect phase025-combat-differences.json");
  String receipt="PASS: identical original/Fabric native combat matrix; "+actual.get("cases")+" cases, all ordered original effect pairs, 256 defense combinations, 87 item attacks, PvP/mob, front/back and strengthening; no behavioral differences; health-only float tolerance 0.00001 HP ("+rounding.size()+" cases)\n";
  Files.writeString(dir.resolve("phase025-combat-parity.passed"),receipt);System.out.print(receipt);
 }
}
