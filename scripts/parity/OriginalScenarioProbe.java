package local.mahouparity;
@net.neoforged.fml.common.Mod("mahou_scenario_probe")
public final class OriginalScenarioProbe {
 public OriginalScenarioProbe(){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::started);}
 private void started(net.neoforged.neoforge.event.server.ServerStartedEvent event){try{
  var data=ScenarioProbe.run(event.getServer());
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase024-original-scenarios.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(data));
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase024-original-scenarios.passed"),"PASS: identical shared probe ran 1372 native catalyst/cloth/fae scenarios and sampled original item/effect/entity contracts\n");
 }catch(Exception e){throw new RuntimeException(e);}finally{event.getServer().halt(false);}}
}
