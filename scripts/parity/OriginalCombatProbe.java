package local.mahouparity;
@net.neoforged.fml.common.Mod("mahou_combat_probe")
public final class OriginalCombatProbe {
 public OriginalCombatProbe(){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::started);}
 private void started(net.neoforged.neoforge.event.server.ServerStartedEvent event){try{
  var data=CombatProbe.run(event.getServer());
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase025-original-combat.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(data));
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase025-original-combat.passed"),"PASS: unmodified original JAR, shared native combat probe, "+data.get("cases")+" controlled cases\n");
 }catch(Exception e){throw new RuntimeException(e);}finally{event.getServer().halt(false);}}
}
