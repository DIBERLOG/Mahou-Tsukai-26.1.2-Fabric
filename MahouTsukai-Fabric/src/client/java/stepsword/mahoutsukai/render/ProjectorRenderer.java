package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.enchant.ProjectorValues;
import stepsword.mahoutsukai.util.ClientImageDownloadUtil;
import stepsword.mahoutsukai.util.EffectUtil;

public class ProjectorRenderer {
   private static final String PROJECTOR_CIRCLE_SCOPE = "projector_circle";
   public static int rindex = 0;
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final Identifier runes = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/runes.png");

   public static void render(ProjectorValues pv, float partialTicks, PoseStack matrix, MultiBufferSource buffer) {
      doProjectorRender(false, pv, matrix, buffer, partialTicks, 0.0F, 0.0F, 0.0F);
   }

   public static void render(ProjectorValues pv, float partialTicks, PoseStack matrix, SubmitNodeCollector submitter) {
      doProjectorRender(false, pv, matrix, new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitter), partialTicks, 0.0F, 0.0F, 0.0F);
   }

   public static void doProjectorRender(
      boolean armor, ProjectorValues te, PoseStack matrix, MultiBufferSource buffer, float partialTicks, float addpitch, float addyaw, float headpitch
   ) {
      if (rindex == 2147483645) {
         rindex = 0;
      }

      float time = (float)ClientHandler.clientTickCounter;
      float offsetx = te.getCx();
      float offsety = te.getCy();
      float offsetz = te.getCz();
      float r = te.getCr() / 255.0F;
      float g = te.getCg() / 255.0F;
      float b = te.getCb() / 255.0F;
      float a = te.getCa() / 255.0F;
      float rotation = te.getRotation();
      float yaw = te.getCyaw();
      float pitch = te.getCpitch();
      float speed = te.getCspeed();
      float ring = te.getCringangle();
      float size = te.getCsize();
      float orbit = te.getCorbitspeed() * time;
      float gif_speed = te.getGifSpeed();
      float gif_frame = gif_speed * time;
      boolean gif_layer = te.getGifLayer();
      boolean showCircle = te.getShowCircle();
      float ringAngle = te.getCringangle();
      float height = te.getHeight();
      int rune = te.getRunes();
      boolean showRing = te.getShowRing();
      float preyaw = te.getCpreyaw();
      float prepitch = te.getCprepitch();
      int loadImage = te.getImage();
      if (!armor) {
         yaw = yaw - te.getCyspeed() + te.getCyspeed() * partialTicks;
         pitch = pitch - te.getCpspeed() + te.getCpspeed() * partialTicks;
         rotation = te.getRotationOffset() + (rotation - speed) + speed * partialTicks;
         orbit = te.corbit - te.getCorbitspeed() + te.getCorbitspeed() * partialTicks;
         gif_frame = te.gif_frame;
      } else {
         rotation = te.getRotation() + te.getCspeed() * time;
         yaw = te.getCyaw() + te.getCyspeed() * time;
         pitch = te.getCpitch() + te.getCpspeed() * time;
         yaw = yaw - te.getCyspeed() + te.getCyspeed() * partialTicks + addyaw;
         pitch = pitch - te.getCpspeed() + te.getCpspeed() * partialTicks + addpitch;
         rotation = te.getRotationOffset() + (rotation - speed) + speed * partialTicks;
         orbit = orbit - te.getCorbitspeed() + te.getCorbitspeed() * partialTicks;
      }

      if (buffer == null) {
         buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      }

      renderProjector(
         armor,
         matrix,
         buffer,
         loadImage,
         showCircle,
         gif_layer,
         gif_frame,
         size,
         r,
         g,
         b,
         a,
         ringAngle,
         height,
         rune,
         showRing,
         ring,
         prepitch,
         preyaw,
         orbit,
         pitch,
         yaw,
         rotation,
         offsetx,
         offsety,
         offsetz,
         headpitch
      );
   }

   public static void doProjectorRender(
      boolean armor, ProjectorValues te, PoseStack matrix, RenderBufferWrapper buffer, float partialTicks, float addpitch, float addyaw, float headpitch
   ) {
      if (rindex == 2147483645) {
         rindex = 0;
      }

      float time = (float)ClientHandler.clientTickCounter;
      float offsetx = te.getCx();
      float offsety = te.getCy();
      float offsetz = te.getCz();
      float r = te.getCr() / 255.0F;
      float g = te.getCg() / 255.0F;
      float b = te.getCb() / 255.0F;
      float a = te.getCa() / 255.0F;
      float rotation = te.getRotation();
      float yaw = te.getCyaw();
      float pitch = te.getCpitch();
      float speed = te.getCspeed();
      float ring = te.getCringangle();
      float size = te.getCsize();
      float orbit = te.getCorbitspeed() * time;
      float gif_speed = te.getGifSpeed();
      float gif_frame = gif_speed * time;
      boolean gif_layer = te.getGifLayer();
      boolean showCircle = te.getShowCircle();
      float ringAngle = te.getCringangle();
      float height = te.getHeight();
      int rune = te.getRunes();
      boolean showRing = te.getShowRing();
      float preyaw = te.getCpreyaw();
      float prepitch = te.getCprepitch();
      int loadImage = te.getImage();
      if (!armor) {
         yaw = yaw - te.getCyspeed() + te.getCyspeed() * partialTicks;
         pitch = pitch - te.getCpspeed() + te.getCpspeed() * partialTicks;
         rotation = te.getRotationOffset() + (rotation - speed) + speed * partialTicks;
         orbit = te.corbit - te.getCorbitspeed() + te.getCorbitspeed() * partialTicks;
         gif_frame = te.gif_frame;
      } else {
         rotation = te.getRotation() + te.getCspeed() * time;
         yaw = te.getCyaw() + te.getCyspeed() * time;
         pitch = te.getCpitch() + te.getCpspeed() * time;
         yaw = yaw - te.getCyspeed() + te.getCyspeed() * partialTicks + addyaw;
         pitch = pitch - te.getCpspeed() + te.getCpspeed() * partialTicks + addpitch;
         rotation = te.getRotationOffset() + (rotation - speed) + speed * partialTicks;
         orbit = orbit - te.getCorbitspeed() + te.getCorbitspeed() * partialTicks;
      }

      renderProjector(
         armor,
         matrix,
         buffer,
         loadImage,
         showCircle,
         gif_layer,
         gif_frame,
         size,
         r,
         g,
         b,
         a,
         ringAngle,
         height,
         rune,
         showRing,
         ring,
         prepitch,
         preyaw,
         orbit,
         pitch,
         yaw,
         rotation,
         offsetx,
         offsety,
         offsetz,
         headpitch
      );
   }

   public static void renderProjector(
      boolean enchant,
      PoseStack matrix,
      MultiBufferSource buffer,
      int loadImage,
      boolean showCircle,
      boolean gif_layer,
      float gif_frame,
      float size,
      float r,
      float g,
      float b,
      float a,
      float ringangle,
      float height,
      int rune,
      boolean showRing,
      float ring,
      float prepitch,
      float preyaw,
      float orbit,
      float pitch,
      float yaw,
      float rotation,
      float offsetx,
      float offsety,
      float offsetz,
      float headpitch
   ) {
      float[] uvs = new float[]{0.0F, 1.0F, 0.0F, 1.0F};
      GifInfo gif_first = null;
      boolean is_gif = false;
      float headsize = -0.9F;
      matrix.pushPose();
      matrix.translate(0.5, 0.5, 0.5);
      if (enchant) {
         matrix.translate(-0.5, 0.0, -0.5);
         matrix.translate(0.0F, -headsize, 0.0F);
         RenderUtils.rotateQ(headpitch, 1.0F, 0.0F, 0.0F, matrix);
         matrix.translate(0.0F, headsize, 0.0F);
      }

      matrix.mulPose(Axis.XP.rotationDegrees(prepitch));
      matrix.mulPose(Axis.YP.rotationDegrees(preyaw));
      matrix.mulPose(Axis.XP.rotationDegrees(orbit));
      matrix.translate(offsetx, offsety, offsetz);
      matrix.mulPose(Axis.YP.rotationDegrees(yaw));
      matrix.mulPose(Axis.XP.rotationDegrees(pitch));
      matrix.mulPose(Axis.YP.rotationDegrees(rotation));
      Matrix4f positionMatrix = matrix.last().pose();
      RenderType type;
      if (loadImage >= 0) {
         for (int j = 0; j < MTConfig.MAGIC_CIRCLES.size(); j++) {
            ClientImageDownloadUtil.download("circle" + j, MTConfig.MAGIC_CIRCLES.get(j), false);
         }

         if (loadImage < ClientImageDownloadUtil.lst.size()) {
            try {
               Identifier loc = ClientImageDownloadUtil.lst.get(loadImage);
               if (ClientImageDownloadUtil.gifmap.containsKey(loc)) {
                  gif_first = ClientImageDownloadUtil.gifmap.get(loc);
                  is_gif = true;
               }

               type = MahoujinRenderType.createMahoujinRenderType(loc, 0, false);
            } catch (Exception e) {
               type = MahoujinRenderType.createMahoujinRenderType(mahoujin, 0, false);
            }
         } else {
            type = MahoujinRenderType.createMahoujinRenderType(mahoujin, 0, false);
         }
      } else {
         type = MahoujinRenderType.createMahoujinRenderType(mahoujin, 0, false);
      }

      int lightmapX = 240;
      int lightmapY = 240;
      if (showCircle) {
         VertexConsumer builder = buffer.getBuffer(type);
         if (is_gif && gif_first != null) {
            if (gif_layer) {
               for (int gf = 0; gf < gif_frame; gf++) {
                  uvs = GifLoader.getUVFromGIF(gf, gif_first);
                  builder.addVertex(positionMatrix, -0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[2]).setUv2(lightmapX, lightmapY);
                  builder.addVertex(positionMatrix, -0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[3]).setUv2(lightmapX, lightmapY);
                  builder.addVertex(positionMatrix, 0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[3]).setUv2(lightmapX, lightmapY);
                  builder.addVertex(positionMatrix, 0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[2]).setUv2(lightmapX, lightmapY);
               }
            } else {
               uvs = GifLoader.getUVFromGIF((long)gif_frame, gif_first);
               builder.addVertex(positionMatrix, -0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[2]).setUv2(lightmapX, lightmapY);
               builder.addVertex(positionMatrix, -0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[3]).setUv2(lightmapX, lightmapY);
               builder.addVertex(positionMatrix, 0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[3]).setUv2(lightmapX, lightmapY);
               builder.addVertex(positionMatrix, 0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[2]).setUv2(lightmapX, lightmapY);
            }
         } else {
            builder.addVertex(positionMatrix, -0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[2]).setUv2(lightmapX, lightmapY);
            builder.addVertex(positionMatrix, -0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[0], uvs[3]).setUv2(lightmapX, lightmapY);
            builder.addVertex(positionMatrix, 0.5F * size, 0.0F, 0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[3]).setUv2(lightmapX, lightmapY);
            builder.addVertex(positionMatrix, 0.5F * size, 0.0F, -0.5F * size).setColor(r, g, b, a).setUv(uvs[1], uvs[2]).setUv2(lightmapX, lightmapY);
         }

         if (enchant) {
            ((BufferSource)buffer).endBatch(type);
         }
      }

      matrix.translate(0.0, Math.sin(EffectUtil.toRad(ringangle)) / 2.0 * height, 0.0);
      positionMatrix = matrix.last().pose();
      loadImage = rune;
      if (showRing) {
         if (loadImage < 0) {
            type = MahoujinRenderType.createScopedRunesRenderType("projector_item_runes", runes, rindex);
         } else {
            for (int j = 0; j < MTConfig.MAGIC_RUNES.size(); j++) {
               ClientImageDownloadUtil.download("runes" + j, MTConfig.MAGIC_RUNES.get(j), true);
            }

            if (loadImage < ClientImageDownloadUtil.lstRunes.size()) {
               try {
                  type = MahoujinRenderType.createScopedRunesRenderType("projector_item_runes", ClientImageDownloadUtil.lstRunes.get(loadImage), rindex);
               } catch (Exception e) {
                  type = MahoujinRenderType.createScopedRunesRenderType("projector_item_runes", runes, rindex);
               }
            } else {
               type = MahoujinRenderType.createScopedRunesRenderType("projector_item_runes", runes, rindex);
            }
         }

         renderRing(positionMatrix, 0.0, ring, size / 2.0F + 0.125F, height, 128, buffer, lightmapX, lightmapY, r, g, b, a, type);
         if (enchant) {
            ((BufferSource)buffer).endBatch(type);
         }
      }

      matrix.popPose();
   }

   public static void renderProjector(
      boolean enchant,
      PoseStack matrix,
      RenderBufferWrapper buffer,
      int loadImage,
      boolean showCircle,
      boolean gif_layer,
      float gif_frame,
      float size,
      float r,
      float g,
      float b,
      float a,
      float ringangle,
      float height,
      int rune,
      boolean showRing,
      float ring,
      float prepitch,
      float preyaw,
      float orbit,
      float pitch,
      float yaw,
      float rotation,
      float offsetx,
      float offsety,
      float offsetz,
      float headpitch
   ) {
      float[] uvs = new float[]{0.0F, 1.0F, 0.0F, 1.0F};
      GifInfo gif_first = null;
      boolean is_gif = false;
      float headsize = -0.9F;
      matrix.pushPose();
      matrix.translate(0.5, 0.5, 0.5);
      if (enchant) {
         matrix.translate(-0.5, 0.0, -0.5);
         matrix.translate(0.0F, -headsize, 0.0F);
         RenderUtils.rotateQ(headpitch, 1.0F, 0.0F, 0.0F, matrix);
         matrix.translate(0.0F, headsize, 0.0F);
      }

      matrix.mulPose(Axis.XP.rotationDegrees(prepitch));
      matrix.mulPose(Axis.YP.rotationDegrees(preyaw));
      matrix.mulPose(Axis.XP.rotationDegrees(orbit));
      matrix.translate(offsetx, offsety, offsetz);
      matrix.mulPose(Axis.YP.rotationDegrees(yaw));
      matrix.mulPose(Axis.XP.rotationDegrees(pitch));
      matrix.mulPose(Axis.YP.rotationDegrees(rotation));
      Identifier texture = mahoujin;
      if (loadImage >= 0) {
         for (int j = 0; j < MTConfig.MAGIC_CIRCLES.size(); j++) {
            ClientImageDownloadUtil.download("circle" + j, MTConfig.MAGIC_CIRCLES.get(j), false);
         }

         if (loadImage < ClientImageDownloadUtil.lst.size()) {
            try {
               Identifier loc = ClientImageDownloadUtil.lst.get(loadImage);
               if (ClientImageDownloadUtil.gifmap.containsKey(loc)) {
                  gif_first = ClientImageDownloadUtil.gifmap.get(loc);
                  is_gif = true;
               }

               texture = loc;
            } catch (Exception ignored) {
               texture = mahoujin;
            }
         }
      }

      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("projector_circle", texture, 0, false);
      int lightmapX = 240;
      int lightmapY = 240;
      if (showCircle) {
         if (is_gif && gif_first != null && !gif_layer) {
            uvs = GifLoader.getUVFromGIF((long)gif_frame, gif_first);
         }

         float[] finalUvs = uvs;
         float finalR = r;
         float finalG = g;
         float finalB = b;
         float finalA = a;
         float finalSize = size;
         GifInfo finalGifFirst = gif_first;
         boolean finalIsGif = is_gif;
         buffer.submit(
            matrix,
            type,
            (pose, builder) -> {
               if (finalIsGif && finalGifFirst != null && gif_layer) {
                  for (int gf = 0; gf < gif_frame; gf++) {
                     float[] layeredUvs = GifLoader.getUVFromGIF(gf, finalGifFirst);
                     builder.addVertex(pose.pose(), -0.5F * finalSize, 0.0F, -0.5F * finalSize)
                        .setColor(finalR, finalG, finalB, finalA)
                        .setUv(layeredUvs[0], layeredUvs[2])
                        .setUv2(lightmapX, lightmapY);
                     builder.addVertex(pose.pose(), -0.5F * finalSize, 0.0F, 0.5F * finalSize)
                        .setColor(finalR, finalG, finalB, finalA)
                        .setUv(layeredUvs[0], layeredUvs[3])
                        .setUv2(lightmapX, lightmapY);
                     builder.addVertex(pose.pose(), 0.5F * finalSize, 0.0F, 0.5F * finalSize)
                        .setColor(finalR, finalG, finalB, finalA)
                        .setUv(layeredUvs[1], layeredUvs[3])
                        .setUv2(lightmapX, lightmapY);
                     builder.addVertex(pose.pose(), 0.5F * finalSize, 0.0F, -0.5F * finalSize)
                        .setColor(finalR, finalG, finalB, finalA)
                        .setUv(layeredUvs[1], layeredUvs[2])
                        .setUv2(lightmapX, lightmapY);
                  }
               } else {
                  builder.addVertex(pose.pose(), -0.5F * finalSize, 0.0F, -0.5F * finalSize)
                     .setColor(finalR, finalG, finalB, finalA)
                     .setUv(finalUvs[0], finalUvs[2])
                     .setUv2(lightmapX, lightmapY);
                  builder.addVertex(pose.pose(), -0.5F * finalSize, 0.0F, 0.5F * finalSize)
                     .setColor(finalR, finalG, finalB, finalA)
                     .setUv(finalUvs[0], finalUvs[3])
                     .setUv2(lightmapX, lightmapY);
                  builder.addVertex(pose.pose(), 0.5F * finalSize, 0.0F, 0.5F * finalSize)
                     .setColor(finalR, finalG, finalB, finalA)
                     .setUv(finalUvs[1], finalUvs[3])
                     .setUv2(lightmapX, lightmapY);
                  builder.addVertex(pose.pose(), 0.5F * finalSize, 0.0F, -0.5F * finalSize)
                     .setColor(finalR, finalG, finalB, finalA)
                     .setUv(finalUvs[1], finalUvs[2])
                     .setUv2(lightmapX, lightmapY);
               }
            }
         );
      }

      matrix.translate(0.0, Math.sin(EffectUtil.toRad(ringangle)) / 2.0 * height, 0.0);
      loadImage = rune;
      if (showRing) {
         if (loadImage < 0) {
            type = MahoujinRenderType.createScopedRunesRenderType("projector_world_runes", runes, rindex);
         } else {
            for (int j = 0; j < MTConfig.MAGIC_RUNES.size(); j++) {
               ClientImageDownloadUtil.download("runes" + j, MTConfig.MAGIC_RUNES.get(j), true);
            }

            if (loadImage < ClientImageDownloadUtil.lstRunes.size()) {
               try {
                  type = MahoujinRenderType.createScopedRunesRenderType("projector_world_runes", ClientImageDownloadUtil.lstRunes.get(loadImage), rindex);
               } catch (Exception e) {
                  type = MahoujinRenderType.createScopedRunesRenderType("projector_world_runes", runes, rindex);
               }
            } else {
               type = MahoujinRenderType.createScopedRunesRenderType("projector_world_runes", runes, rindex);
            }
         }

         renderRing(matrix, 0.0, ring, size / 2.0F + 0.125F, height, 128, buffer, lightmapX, lightmapY, r, g, b, a, type);
      }

      matrix.popPose();
   }

   public static void renderRing(
      Matrix4f matrix4f,
      double cy,
      float flatness,
      float radius,
      float height,
      int num_segments,
      MultiBufferSource buf,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType rt
   ) {
      double theta = 6.2831852 / num_segments;
      double q = height * Math.sin(EffectUtil.toRad(flatness));
      double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      double xb = 0.0;
      double yb = 0.0;
      double zb = 0.0;
      VertexConsumer buffer = buf.getBuffer(rt);
      float squeeze = 3.0F;
      float texx = 0.0F;
      boolean wroteFirstPair = false;

      for (int i = 0; i < num_segments + 1; i++) {
         texx += squeeze * 1.0F / (num_segments + 1);
         if (texx >= 1.0F) {
            texx = squeeze * 1.0F / (num_segments + 1);
            buffer.addVertex(matrix4f, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            buffer.addVertex(matrix4f, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         }

         double tt = i * theta;
         x = -radius * Math.sin(tt);
         y = cy;
         z = radius * Math.cos(tt);
         xb = -p * Math.sin(tt);
         yb = cy - q;
         zb = p * Math.cos(tt);
         if (!wroteFirstPair) {
            buffer.addVertex(matrix4f, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
            buffer.addVertex(matrix4f, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
            buffer.addVertex(matrix4f, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
            wroteFirstPair = true;
         } else {
            buffer.addVertex(matrix4f, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
            buffer.addVertex(matrix4f, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
         }
      }

      if (wroteFirstPair) {
         buffer.addVertex(matrix4f, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
      }
   }

   public static void renderRing(
      PoseStack matrix,
      double cy,
      float flatness,
      float radius,
      float height,
      int num_segments,
      RenderBufferWrapper buf,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType rt
   ) {
      double theta = 6.2831852 / num_segments;
      double q = height * Math.sin(EffectUtil.toRad(flatness));
      double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
      buf.submit(matrix, rt, (pose, buffer) -> {
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;
         double xb = 0.0;
         double yb = 0.0;
         double zb = 0.0;
         float texx = 0.0F;
         float squeeze = 3.0F;
         boolean wroteFirstPair = false;

         for (int i = 0; i < num_segments + 1; i++) {
            texx += squeeze / (num_segments + 1);
            if (texx >= 1.0F) {
               texx = squeeze / (num_segments + 1);
               buffer.addVertex(pose.pose(), (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
               buffer.addVertex(pose.pose(), (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
            }

            double tt = i * theta;
            x = -radius * Math.sin(tt);
            y = cy;
            z = radius * Math.cos(tt);
            xb = -p * Math.sin(tt);
            yb = cy - q;
            zb = p * Math.cos(tt);
            if (!wroteFirstPair) {
               buffer.addVertex(pose.pose(), (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
               buffer.addVertex(pose.pose(), (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
               buffer.addVertex(pose.pose(), (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
               wroteFirstPair = true;
            } else {
               buffer.addVertex(pose.pose(), (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly).setColor(r, g, b, a);
               buffer.addVertex(pose.pose(), (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
            }
         }

         if (wroteFirstPair) {
            buffer.addVertex(pose.pose(), (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly).setColor(r, g, b, a);
         }
      });
   }

   public static void cacheClear() {
      var textures=new java.util.HashSet<>(ClientImageDownloadUtil.lst);
      textures.addAll(ClientImageDownloadUtil.lstRunes);
      for(var texture:textures)Minecraft.getInstance().getTextureManager().release(texture);
      ClientImageDownloadUtil.gifmap.clear();
      ClientImageDownloadUtil.lstRunes = new ArrayList<>();
      ClientImageDownloadUtil.lst = new ArrayList<>();
   }
}
