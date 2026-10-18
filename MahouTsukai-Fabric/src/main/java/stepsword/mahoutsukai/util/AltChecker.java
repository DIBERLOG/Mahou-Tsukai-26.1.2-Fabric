package stepsword.mahoutsukai.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.ProfileResolver;

public class AltChecker {
   public static HashMap<String, String> alts = null;

   public static void readAltsMap() {
      if (alts == null) {
         try {
            alts = new HashMap<>();
            File alt_file = new File("mahoutsukai/alts.json");
            InputStream inputstream = new FileInputStream(alt_file);
            String alt_list = new String(inputstream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(alt_list).getAsJsonObject();

            for (String key : json.keySet()) {
               JsonElement mapped = json.get(key);
               String name = mapped.getAsString();
               alts.put(name, key);
               alts.put(key, name);
            }
         } catch (Exception var8) {
         }
      }
   }

   public static boolean isSamePlayer(UUID uuid, UUID uuid2, ServerLevel level) {
      if (uuid == null || uuid2 == null) {
         return false;
      }

      if (uuid.equals(uuid2)) {
         return true;
      }

      readAltsMap();
      if(alts.isEmpty())return false;
      ProfileResolver cache = level.getServer().services().profileResolver();
      if (cache != null) {
         GameProfile profile1 = (GameProfile)cache.fetchById(uuid).orElse(null);
         GameProfile profile2 = (GameProfile)cache.fetchById(uuid2).orElse(null);
         if (profile1 != null && profile2 != null && lookupAlt(profile1.name(), profile2.name())) {
            return true;
         }

         if (lookupAlt(uuid.toString(), uuid2.toString())) {
            return true;
         }
      }

      return false;
   }

   public static boolean lookupAlt(String s1, String s2) {
      return alts.getOrDefault(s1, "").equals(s2);
   }
}
