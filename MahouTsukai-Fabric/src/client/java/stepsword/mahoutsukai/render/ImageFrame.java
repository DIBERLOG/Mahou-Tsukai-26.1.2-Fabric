package stepsword.mahoutsukai.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageFrame {
   private final int delay;
   private final BufferedImage image;
   private final String disposal;
   private final int width;
   private final int height;

   public ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
      this.image = image;
      this.delay = delay;
      this.disposal = disposal;
      this.width = width;
      this.height = height;
   }

   public ImageFrame(BufferedImage image) {
      this.image = image;
      this.delay = -1;
      this.disposal = null;
      this.width = -1;
      this.height = -1;
   }

   public BufferedImage getImage() {
      return this.image;
   }

   public int getDelay() {
      return this.delay;
   }

   public String getDisposal() {
      return this.disposal;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public static ImageFrame[] readGif(InputStream stream) throws IOException {
      ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
      Graphics2D masterGraphics = null;
      try(var imageInput=ImageIO.createImageInputStream(stream)){
      reader.setInput(imageInput);
      ArrayList<ImageFrame> frames = new ArrayList<>(2);
      int width = -1;
      int height = -1;
      IIOMetadata metadata = reader.getStreamMetadata();
      if (metadata != null) {
         IIOMetadataNode globalRoot = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
         NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
         if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
            IIOMetadataNode screenDescriptor = (IIOMetadataNode)globalScreenDescriptor.item(0);
            if (screenDescriptor != null) {
               width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
               height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
            }
         }
      }

      BufferedImage master = null;
      if(width>0 && height>0 && (long)width*height>16*1024*1024)throw new IOException("Projector GIF canvas exceeds 16 megapixels");
      int frameIndex = 0;

      while (true) {
         BufferedImage image;
         try {
            int frameWidth=reader.getWidth(frameIndex),frameHeight=reader.getHeight(frameIndex);
            long pixels=(long)Math.max(width,frameWidth)*Math.max(height,frameHeight);
            if(pixels<=0 || pixels*(frameIndex+1)>16*1024*1024)throw new IOException("Projector GIF decoded frames exceed 16 megapixels");
            image = reader.read(frameIndex);
         } catch (IndexOutOfBoundsException io) {
            return frames.toArray(new ImageFrame[frames.size()]);
         }

         if (width == -1 || height == -1) {
            width = image.getWidth();
            height = image.getHeight();
         }

         IIOMetadataNode root = (IIOMetadataNode)reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
         IIOMetadataNode gce = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
         int delay = Integer.valueOf(gce.getAttribute("delayTime"));
         String disposal = gce.getAttribute("disposalMethod");
         int x = 0;
         int y = 0;
         if (master == null) {
            master = new BufferedImage(width, height, 2);
            masterGraphics = master.createGraphics();
            masterGraphics.setBackground(new Color(0, 0, 0, 0));
         } else {
            NodeList children = root.getChildNodes();

            for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
               Node nodeItem = children.item(nodeIndex);
               if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                  NamedNodeMap map = nodeItem.getAttributes();
                  x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                  y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
               }
            }
         }

         masterGraphics.drawImage(image, x, y, null);
         BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
         frames.add(new ImageFrame(copy, delay, disposal, width, height));
         if (!disposal.equals("restoreToPrevious")) {
            if (disposal.equals("restoreToBackgroundColor")) {
               masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
            }
         } else {
            BufferedImage from = null;

            for (int i = frameIndex - 1; i >= 0; i--) {
               if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                  from = frames.get(i).getImage();
                  break;
               }
            }

            masterGraphics.dispose();
            master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
            masterGraphics = master.createGraphics();
            masterGraphics.setBackground(new Color(0, 0, 0, 0));
         }

         frameIndex++;
      }
      }finally{reader.dispose();if(masterGraphics!=null)masterGraphics.dispose();}
   }
}
