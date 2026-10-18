package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.util.ClientImageDownloadUtil;
import stepsword.mahoutsukai.util.Utils;

public class GifLoader {
   public static boolean isGIF(byte[] bytes) {
      return bytes.length>=6 && bytes[0]=='G' && bytes[1]=='I' && bytes[2]=='F' && bytes[3]=='8' && (bytes[4]=='7'||bytes[4]=='9') && bytes[5]=='a';
   }

   public static NativeImage loadGif(InputStream input, Identifier loc) {
      return loadGif(input,info->ClientImageDownloadUtil.gifmap.put(loc,info));
   }

   public static NativeImage loadGif(InputStream input, java.util.function.Consumer<GifInfo> onDecoded) {
      NativeImage ret = null;

      try {
         try {
            ImageFrame[] frames = ImageFrame.readGif(input);
            int count = frames.length;
            int height = 0;
            int width = 0;

            for (int f = 0; f < frames.length; f++) {
               if (frames[f].getWidth() > width) {
                  width = frames[f].getWidth();
               }

               if (frames[f].getHeight() > height) {
                  height = frames[f].getHeight();
               }
            }

            if (width > height) {
               height = width;
            }

            int images_per_side = (int)Math.ceil(Math.sqrt(count));
            int side = images_per_side * height;
            if(count==0 || (long)side*side>16*1024*1024)throw new java.io.IOException("Projector GIF atlas exceeds 16 megapixels");
            BufferedImage sheet = new BufferedImage(side, side, 2);
            int index = 0;
            int q = 0;
            int m = 0;

            while (index < count) {
               BufferedImage frame = frames[index].getImage();
               joinBufferedImage(sheet, frame, q * width, m * height);
               if (++q == images_per_side) {
                  q = 0;
                  m++;
               }

               index++;
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(sheet, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            ret = NativeImage.read(is);
            onDecoded.accept(new GifInfo(width, height, count));
         } catch (Exception e) {
            Utils.err(e.toString());
         }
      } catch (Exception ex) {
         Utils.err(ex.toString());
      }

      return ret;
   }

   public static void joinBufferedImage(BufferedImage sheet, BufferedImage img, int startw, int starth) {
      Graphics2D g2 = sheet.createGraphics();
      Color oldColor = g2.getColor();
      g2.setColor(oldColor);
      g2.drawImage(img, null, starth, startw);
      g2.dispose();
   }

   public static float[] getUVFromGIF(long frame, GifInfo gif) {
      frame %= gif.framecount;
      float[] ret = new float[4];
      int images_per_side = (int)Math.ceil(Math.sqrt(gif.framecount));
      float unit = 1.0F / images_per_side;
      float u1 = (float)(frame / images_per_side) * unit;
      float u2 = Math.min(1.0F, u1 + unit);
      float v1 = (int)(frame % images_per_side) * unit;
      float v2 = Math.min(1.0F, v1 + unit);
      ret[0] = u1;
      ret[1] = u2;
      ret[2] = v1;
      ret[3] = v2;
      return ret;
   }
}
