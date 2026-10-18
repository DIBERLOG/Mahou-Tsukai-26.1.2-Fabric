package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import stepsword.mahoutsukai.util.EffectUtil;

public class NewRenderUtils {
   public static void addVertex(
      Pose pose, VertexConsumer builder, float r, float g, float b, float a, float x, float y, float z, float u, float v, int lx, int ly
   ) {
      builder.addVertex(pose, x, y, z)
         .setColor(r, g, b, a)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(lx | ly << 16)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   public static void renderRing(
      String scope,
      PoseStack matrix,
      SubmitNodeCollector submitter,
      double cy,
      float flatness,
      float radius,
      float height,
      int numSegments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a
   ) {
      RenderType type = MahoujinRenderType.createScopedRunesRenderType(scope, RenderUtils.runes, 0);
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         double theta = 6.2831852 / numSegments;
         double q = height * Math.sin(EffectUtil.toRad(flatness));
         double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;
         double xb = 0.0;
         double yb = 0.0;
         double zb = 0.0;
         float texx = 0.0F;
         float squeeze = 3.0F;
         boolean wroteFirstPair = false;

         for (int i = 0; i < numSegments + 1; i++) {
            texx += squeeze / (numSegments + 1);
            if (texx >= 1.0F) {
               texx = squeeze / (numSegments + 1);
               addVertex(pose, buffer, r, g, b, a, (float)x, (float)y, (float)z, 0.0F, 0.0F, lx, ly);
               addVertex(pose, buffer, r, g, b, a, (float)xb, (float)yb, (float)zb, 0.0F, 1.0F, lx, ly);
            }

            double tt = i * theta;
            x = -radius * Math.sin(tt);
            y = cy;
            z = radius * Math.cos(tt);
            xb = -p * Math.sin(tt);
            yb = cy - q;
            zb = p * Math.cos(tt);
            if (!wroteFirstPair) {
               addVertex(pose, buffer, r, g, b, a, (float)x, (float)y, (float)z, texx, 0.0F, lx, ly);
               addVertex(pose, buffer, r, g, b, a, (float)x, (float)y, (float)z, texx, 0.0F, lx, ly);
               addVertex(pose, buffer, r, g, b, a, (float)xb, (float)yb, (float)zb, texx, 1.0F, lx, ly);
               wroteFirstPair = true;
            } else {
               addVertex(pose, buffer, r, g, b, a, (float)x, (float)y, (float)z, texx, 0.0F, lx, ly);
               addVertex(pose, buffer, r, g, b, a, (float)xb, (float)yb, (float)zb, texx, 1.0F, lx, ly);
            }
         }

         if (wroteFirstPair) {
            addVertex(pose, buffer, r, g, b, a, (float)xb, (float)yb, (float)zb, texx, 1.0F, lx, ly);
         }
      });
   }
}
