package stepsword.mahoutsukai;
import com.google.gson.*;
import java.nio.file.*;
public final class CombatParityChecks {
 public static void run(net.minecraft.server.MinecraftServer server)throws Exception{
  Path dir=Path.of("../../../analysis");Files.deleteIfExists(dir.resolve("phase026-combat-parity.passed"));
  var actual=JsonParser.parseString(new Gson().toJson(local.mahouparity.CombatProbe.run(server))).getAsJsonObject();
  Files.writeString(dir.resolve("phase026-fabric-combat.json"),new GsonBuilder().setPrettyPrinting().create().toJson(actual));
  var expected=JsonParser.parseString(Files.readString(dir.resolve("phase025-original-combat.json"))).getAsJsonObject();
  var differences=new JsonArray();var rounding=new JsonArray();var balance=new JsonArray();
  for(String section:java.util.List.of("ordered_effect_pairs","defense_power_set","native_item_attacks")){
   var a=expected.getAsJsonObject(section);var b=actual.getAsJsonObject(section);
   if(!a.keySet().equals(b.keySet()))throw new AssertionError("Incomplete combat section "+section);
   for(String key:a.keySet())if(!a.get(key).equals(b.get(key))){var row=new JsonObject();row.addProperty("section",section);row.addProperty("case",key);row.add("original",a.get(key));row.add("fabric",b.get(key));var original=a.getAsJsonObject(key).deepCopy();var fabric=b.getAsJsonObject(key).deepCopy();
    // NeoForge's damage container and vanilla resistance differ in float operation order.
    // Permit only health rounding, below 0.00001 HP; every other field remains exact.
    boolean changed=false;
    // The shared fixture starts Clarent at 8 absorbed power. Original shield handling erased it;
    // user-requested 0.26 rules retain it. These exact expected deltas preserve all other fields.
    if(section.equals("native_item_attacks")&&key.startsWith("mahoutsukai:clarent/")){
     var parts=key.split("/");boolean player=Boolean.parseBoolean(parts[1]);int mask=Integer.parseInt(parts[2]),facing=Integer.parseInt(parts[3]);
     if(mask==64&&facing==0){original.addProperty("health",original.get("health").getAsFloat()-8);changed=true;}
     if(mask==255&&!player&&facing==0){original.addProperty("health",original.get("health").getAsFloat()-0.448F);changed=true;}
     if(mask==64&&player&&facing==180){original.getAsJsonObject("target_wear").addProperty("MAINHAND",10);changed=true;}
     if(mask==255&&player){original.addProperty("mana",original.get("mana").getAsInt()+160);changed=true;}
    }
    double delta=Math.abs(original.get("health").getAsDouble()-fabric.get("health").getAsDouble());
    fabric.add("health",original.get("health"));
    if(delta<=0.00001 && original.equals(fabric)){if(changed)balance.add(row);else rounding.add(row);}else differences.add(row);}
  }
  Files.writeString(dir.resolve("phase026-combat-differences.json"),new GsonBuilder().setPrettyPrinting().create().toJson(differences));
  Files.writeString(dir.resolve("phase026-combat-float-rounding.json"),new GsonBuilder().setPrettyPrinting().create().toJson(rounding));
  Files.writeString(dir.resolve("phase026-combat-intended-balance.json"),new GsonBuilder().setPrettyPrinting().create().toJson(balance));
  if(balance.size()!=12)throw new AssertionError("Expected exactly 12 fixture cases changed by retained Clarent charge, got "+balance.size());
  if(!differences.isEmpty())throw new AssertionError("Native combat mismatches: "+differences.size()+"; inspect phase026-combat-differences.json");
  String receipt="PASS: original/Fabric native combat matrix with 12 explicitly asserted retained-Clarent-charge changes; "+actual.get("cases")+" cases, all ordered original effect pairs, 256 defense combinations, 87 item attacks, PvP/mob, front/back and strengthening; no unexplained behavioral differences; health-only float tolerance 0.00001 HP ("+rounding.size()+" cases)\n";
  Files.writeString(dir.resolve("phase026-combat-parity.passed"),receipt);System.out.print(receipt);
 }
}
