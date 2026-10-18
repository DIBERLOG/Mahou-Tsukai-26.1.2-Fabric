package stepsword.mahoutsukai;

import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import stepsword.mahoutsukai.render.*;
import stepsword.mahoutsukai.util.ClientImageDownloadUtil;
import net.minecraft.resources.Identifier;

public final class ProjectorImageChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void run(net.minecraft.client.Minecraft mc) throws Exception {
  var cache=Files.createTempFile("mahou-gif-cache-",".gif");
  var id=Identifier.fromNamespaceAndPath("mahoutsukai","test/offline_gif");
  ThreadDownloadImageData texture=null;
  try {
   var writer=ImageIO.getImageWritersByFormatName("gif").next();
   try(var output=ImageIO.createImageOutputStream(cache.toFile())){
    writer.setOutput(output);writer.prepareWriteSequence(null);
    for(int color:new int[]{0xffff0000,0xff0000ff}){
     var frame=new BufferedImage(4,4,BufferedImage.TYPE_INT_ARGB);for(int x=0;x<4;x++)for(int y=0;y<4;y++)frame.setRGB(x,y,color);
     writer.writeToSequence(new IIOImage(frame,null,null),null);
    }
    writer.endWriteSequence();
   }finally{writer.dispose();}
   texture=new ThreadDownloadImageData(cache.toFile(),"http://127.0.0.1:1/unavailable",RenderUtils.mahoujin,null,null,id);
   try(var contents=texture.loadContents(mc.getResourceManager())){
    var info=ClientImageDownloadUtil.gifmap.get(id);require(info!=null&&info.framecount==2,"Cached GIF did not restore its animation metadata without network");
    var image=contents.image();require(image.getWidth()==8&&image.getHeight()==8,"Cached GIF atlas has wrong dimensions");
    for(int frame=0;frame<2;frame++){
     var uv=GifLoader.getUVFromGIF(frame,info);int x=(int)((uv[0]+uv[1])*.5*image.getWidth()),y=(int)((uv[2]+uv[3])*.5*image.getHeight());
     require((image.getPixel(x,y)&0xffffff)==(frame==0?0xff0000:0x0000ff),"GIF frame UV does not select the corresponding decoded colour");
    }
   }
   require(!GifLoader.isGIF(new byte[]{1,2})&&GifLoader.isGIF("GIF87a".getBytes(java.nio.charset.StandardCharsets.US_ASCII)),"GIF signatures reject valid 87a or read short input");
   byte[] oversized=Files.readAllBytes(cache);for(int i=6;i<10;i++)oversized[i]=(byte)255;boolean rejected=false;
   try{ImageFrame.readGif(new java.io.ByteArrayInputStream(oversized));}catch(java.io.IOException expected){rejected=true;}
   require(rejected,"Oversized GIF canvas was not rejected before frame allocation");
   mc.getTextureManager().register(id,texture);ClientImageDownloadUtil.lst.add(id);ProjectorRenderer.cacheClear();
   require(ClientImageDownloadUtil.lst.isEmpty()&&ClientImageDownloadUtil.gifmap.isEmpty(),"Disconnect cache cleanup retains old animation data");
   var image=new com.mojang.blaze3d.platform.NativeImage(2,2,false);
   var callback=ThreadDownloadImageData.class.getDeclaredMethod("loadCallback",com.mojang.blaze3d.platform.NativeImage.class);callback.setAccessible(true);callback.invoke(texture,image);
   require(image.isClosed()&&!ClientImageDownloadUtil.gifmap.containsKey(id),"Late download resurrects a closed texture or leaks its image");
   Files.writeString(Path.of("projector-image-check.passed"),"PASS: real cached two-frame GIF decodes offline, both frame colours match renderer UVs, 87a/short signatures, disconnect texture release and late completion image cleanup\n");
  }finally{if(texture!=null)texture.close();ClientImageDownloadUtil.gifmap.remove(id);ClientImageDownloadUtil.lst.remove(id);Files.deleteIfExists(cache);}
 }
}
