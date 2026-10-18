package stepsword.jousting;
import com.google.gson.*;
import stepsword.jousting.config.JConfig;
public final class LanceSettings {
 public static double value(String key){try{return ((Number)JConfig.class.getField(key).get(null)).doubleValue();}catch(ReflectiveOperationException e){throw new IllegalArgumentException(key,e);}}
 public static JsonObject json(){var json=new JsonObject();for(var field:JConfig.class.getFields())json.addProperty(field.getName(),value(field.getName()));return json;}
 public static void apply(JsonObject json){
  try{for(var entry:json.entrySet()){var field=JConfig.class.getField(entry.getKey());double value=entry.getValue().getAsDouble();
   if(!Double.isFinite(value)||value<0||value>10000000||field.getType()==int.class && (value!=Math.rint(value)||value<1))throw new IllegalArgumentException("Invalid lance setting: "+entry.getKey());
  }
  for(var entry:json.entrySet()){var field=JConfig.class.getField(entry.getKey());if(field.getType()==int.class)field.setInt(null,entry.getValue().getAsInt());else field.setDouble(null,entry.getValue().getAsDouble());}
  }catch(ReflectiveOperationException e){throw new IllegalArgumentException("Unknown Jousting setting",e);}
 }
 public static void load(){var path=net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("jousting-fabric.json");try{if(java.nio.file.Files.exists(path))apply(JsonParser.parseString(java.nio.file.Files.readString(path)).getAsJsonObject());else java.nio.file.Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(json()));}catch(java.io.IOException e){throw new IllegalStateException("Cannot load Jousting settings",e);}}
}
