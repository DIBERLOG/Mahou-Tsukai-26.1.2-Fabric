package stepsword.mahoutsukai.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.GifInfo;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.ThreadDownloadImageData;

public class ClientImageDownloadUtil {
   public static ArrayList<Identifier> lst = new ArrayList<>();
   public static ArrayList<Identifier> lstRunes = new ArrayList<>();
   public static java.util.Map<Identifier, GifInfo> gifmap = new java.util.concurrent.ConcurrentHashMap<>();

   public static boolean download(String textureName, String url, boolean runes) {
      Identifier texLocation = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/" + textureName + ".png");
      boolean cached = false;
      if (runes) {
         cached = lstRunes.contains(texLocation);
      } else {
         cached = lst.contains(texLocation);
      }

      if (!cached) {
         TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
         ThreadDownloadImageData object;
         if (MTConfig.CACHE_IMAGES) {
            object = new ThreadDownloadImageData(
               new File("mahoutsukai/" + (runes ? "runes" : "circles") + "/" + textureName + ".png"), url, RenderUtils.mahoujin, null, null, texLocation
            );
         } else {
            object = new ThreadDownloadImageData(null, url, RenderUtils.mahoujin, null, null, texLocation);
         }

         texturemanager.registerAndLoad(texLocation, object);
         if (runes) {
            lstRunes.add(texLocation);
         } else {
            lst.add(texLocation);
         }
      }

      return true;
   }
}
