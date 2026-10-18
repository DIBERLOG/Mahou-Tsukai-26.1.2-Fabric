package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
   @Nullable
   private final File cacheFile;
   private final String imageUrl;
   @Nullable
   private Thread imageThread;
   private volatile boolean closed;
   private GifInfo gifInfo;
   private final Runnable onDownloaded;
   public Identifier loc;

   public ThreadDownloadImageData(
      @Nullable File cacheFileIn,
      String imageUrlIn,
      Identifier textureIdentifier,
      @Nullable BufferedImage imageBufferIn,
      @Nullable Runnable runnable,
      Identifier loc
   ) {
      super(textureIdentifier);
      this.cacheFile = cacheFileIn;
      this.imageUrl = imageUrlIn;
      this.onDownloaded = runnable;
      this.loc = loc;
   }

   public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
      if (this.imageThread == null) {
         NativeImage nativeImage = null;
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);

            try (FileInputStream fileInputStream = new FileInputStream(this.cacheFile)) {
               nativeImage = this.decode(fileInputStream);
            } catch (IOException exception) {
               LOGGER.warn("Invalid projector cache; retrying download", exception);
            }
         }

         if (nativeImage != null) {
            if(gifInfo!=null)stepsword.mahoutsukai.util.ClientImageDownloadUtil.gifmap.put(loc,gifInfo);
            return new TextureContents(nativeImage, null);
         }

         this.loadTextureFromServer();
      }

      return super.loadContents(resourceManager);
   }

   protected void loadTextureFromServer() {
      this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet()) {
         @Override
         public void run() {
            HttpURLConnection httpurlconnection = null;
            ThreadDownloadImageData.LOGGER
               .debug("Downloading http texture from {} to {}", ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile);

            try {
               httpurlconnection = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getInstance().getProxy());
               httpurlconnection.setConnectTimeout(10000);
               httpurlconnection.setReadTimeout(10000);
               httpurlconnection.setDoInput(true);
               httpurlconnection.setRequestProperty("Accept", "image/*");
               httpurlconnection.setDoOutput(false);
               httpurlconnection.connect();
               if (httpurlconnection.getResponseCode() / 100 == 2) {
                  InputStream inputstream;
                  if (ThreadDownloadImageData.this.cacheFile != null) {
                     try(InputStream source=httpurlconnection.getInputStream()) {
                        byte[] data=source.readNBytes(16*1024*1024+1);
                        if(data.length>16*1024*1024)throw new IOException("Projector image exceeds 16 MiB");
                        FileUtils.writeByteArrayToFile(ThreadDownloadImageData.this.cacheFile,data);
                     }
                     inputstream = new FileInputStream(ThreadDownloadImageData.this.cacheFile);
                  } else {
                     inputstream = httpurlconnection.getInputStream();
                  }

                  try (InputStream stream = inputstream) {
                     byte[] bytes = stream.readNBytes(16 * 1024 * 1024 + 1);
                     if(bytes.length > 16 * 1024 * 1024) throw new IOException("Projector image exceeds 16 MiB");
                     InputStream newInputStream = new ByteArrayInputStream(bytes);
                     if (GifLoader.isGIF(bytes)) {
                        NativeImage gif = GifLoader.loadGif(newInputStream, info->ThreadDownloadImageData.this.gifInfo=info);
                        if (gif != null) {
                           ThreadDownloadImageData.this.loadCallback(gif);
                           return;
                        }
                     } else {
                        NativeImage nativeimage1 = ThreadDownloadImageData.this.load(newInputStream);
                        if (nativeimage1 != null) {
                           ThreadDownloadImageData.this.loadCallback(nativeimage1);
                           return;
                        }
                     }

                     return;
                  }
               }
            } catch (Exception exception) {
               ThreadDownloadImageData.LOGGER.error("Couldn't download http texture", exception);
               return;
            } finally {
               if (httpurlconnection != null) {
                  httpurlconnection.disconnect();
               }
            }
         }
      };
      this.imageThread.setDaemon(true);
      this.imageThread.start();
   }

   private void loadCallback(NativeImage image) {
      Minecraft.getInstance().execute(() -> {
         if(closed){image.close();return;}
         if(gifInfo!=null)stepsword.mahoutsukai.util.ClientImageDownloadUtil.gifmap.put(loc,gifInfo);
         this.apply(new TextureContents(image, null));
         if(this.onDownloaded!=null)this.onDownloaded.run();
      });
   }

   private NativeImage decode(InputStream stream) throws IOException {
      byte[] bytes=stream.readNBytes(16*1024*1024+1);
      if(bytes.length>16*1024*1024)throw new IOException("Projector image exceeds 16 MiB");
      var input=new ByteArrayInputStream(bytes);
      return GifLoader.isGIF(bytes)?GifLoader.loadGif(input,info->gifInfo=info):load(input);
   }

   @Override public void close(){closed=true;super.close();}

   @Nullable
   private NativeImage load(InputStream inputStream) {
      NativeImage nativeimage = null;

      try {
         nativeimage = NativeImage.read(inputStream);
      } catch (Exception exception) {
         LOGGER.warn("Error while loading the skin texture", exception);
      }

      return nativeimage;
   }
}
