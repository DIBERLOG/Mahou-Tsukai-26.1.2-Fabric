package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.item.rulebreaker.RuleBreaker;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RuleBreakerRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   public static final Identifier white = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof RuleBreaker;
      state.time = Minecraft.getInstance().level != null ? (int)Minecraft.getInstance().level.getGameTime() : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.RULE_BREAKER);
      return state;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack matrix,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         matrix.pushPose();
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
         float r = 0.5F;
         float g = 0.19F;
         float b = 0.47F;
         float a = 1.0F;
         float r2 = 0.42F;
         float g2 = 0.7F;
         float b2 = 0.76F;
         float a2 = 0.4F;
         int len = 200;
         int step = 67;
         float[][] colors = new float[len][3];
         Random rand = new Random(20L);
         colors[0][0] = r;
         colors[0][1] = g;
         colors[0][2] = b;
         boolean odd = true;
         float d = 0.04F;

         for (int f = step; f < len; f += step) {
            if (odd) {
               colors[f][0] = Mth.clamp(r2 + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
               colors[f][1] = Mth.clamp(g2 + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
               colors[f][2] = Mth.clamp(a2 + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
            } else {
               colors[f][0] = Mth.clamp(r + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
               colors[f][1] = Mth.clamp(g + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
               colors[f][2] = Mth.clamp(a + rand.nextFloat() * d - d / 2.0F, 0.0F, 1.0F);
            }

            odd = !odd;
         }

         for (int f = 1; f < len; f++) {
            if (f % step != 0) {
               int low = (f - f % step) % len;
               int high = (low + step) % len;
               colors[f][0] = Utils.avgStep(colors[low][0], colors[high][0], step, f % step);
               colors[f][1] = Utils.avgStep(colors[low][1], colors[high][1], step, f % step);
               colors[f][2] = Utils.avgStep(colors[low][2], colors[high][2], step, f % step);
            }
         }

         colors = Utils.permute(colors, Math.floorMod((int)Minecraft.getInstance().level.getGameTime(), len));
         RenderUtils.rotateQ(-90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(20.0F, 0.0F, 1.0F, 0.0F, matrix);
         float width = 0.009F;
         matrix.translate(0.68, -0.5007 - width, 1.7);
         matrix.scale(4.0F, 4.0F, 4.0F);
         matrix.pushPose();
         renderBlade(matrix, immediate, r, g, b, a, width, colors);
         matrix.popPose();
         matrix.pushPose();
         matrix.translate(0.0F, width, 0.0F);
         renderBlade(matrix, immediate, r, g, b, a, 0.0F, colors);
         matrix.popPose();
         matrix.popPose();
      }
   }

   public static void renderBlade(PoseStack matrix, BufferSource buffer, float r, float g, float b, float a, float extend, float[][] colors) {
      matrix.pushPose();
      matrix.pushPose();
      RenderUtils.rotateQ(-6.5F, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(1.0F, 1.0F, 1.0F);
      float q = extend > 0.0F ? 1.0E-5F : -1.0E-5F;
      addRBArc2(
         matrix,
         buffer,
         0.024F,
         0.035F,
         130.0F,
         -109.0F,
         10.0F,
         270.0F,
         50.0F,
         -0.0855F,
         0.0F,
         -0.281F,
         -0.156F,
         0.0F,
         -0.2325F,
         r,
         g,
         b,
         a,
         extend,
         colors,
         0,
         true
      );
      matrix.popPose();
      matrix.translate(-0.155F, -q / 2.0F, -0.291F);
      addRBArc2(
         matrix, buffer, 0.075F, 0.103F, 74.0F, 66.0F, -59.0F, -82.0F, 50.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, r, g, b, a, extend + q, colors, 50, false
      );
      matrix.translate(0.14, -q, -0.15);
      addRBArc2(
         matrix, buffer, 0.272F, 0.25F, 24.0F, 20.0F, 10.0F, 0.0F, 50.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, r, g, b, a, extend + q * 2.0F, colors, 100, true
      );
      matrix.translate(0.166, -q * 3.0F / 2.0F, 0.433);
      addRBArc2(
         matrix, buffer, 0.27F, 0.247F, 35.0F, 55.0F, 94.0F, 84.0F, 50.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, r, g, b, a, extend + q * 3.0F, colors, 150, true
      );
      matrix.popPose();
   }

   public static void addRBArc2(
      PoseStack matrix,
      BufferSource buffer,
      float radtop,
      float radbottom,
      float arctop,
      float arcbottom,
      float arcstarttop,
      float arcstartbottom,
      float triangles,
      float x,
      float y,
      float z,
      float x2,
      float y2,
      float z2,
      float r,
      float g,
      float b,
      float a,
      double extendlength,
      float[][] colors,
      int ci,
      boolean backwards
   ) {
      double ya = y;
      double[] averts = new double[(int)triangles * 2];
      double[] bverts = new double[(int)triangles * 2];
      RenderType type = MahoujinRenderType.createSphereRenderType2(white, 0);
      Pose pose = matrix.last();
      VertexConsumer buf = buffer.getBuffer(type);
      float thetaTop = EffectUtil.toRad(arctop / triangles);
      float thetaBottom = EffectUtil.toRad(arcbottom / triangles);
      float tt = EffectUtil.toRad(arcstarttop);
      float tb = EffectUtil.toRad(arcstartbottom);
      int k = 0;

      for (int i = 0; i < triangles; i++) {
         float thetaTopTmp = tt + i * thetaTop;
         float thetaBottomTmp = tb + i * thetaBottom;
         double xa = x - radtop * Math.sin(thetaTopTmp);
         double za = z + radtop * Math.cos(thetaTopTmp);
         double xb = x2 - radbottom * Math.sin(thetaBottomTmp);
         double zb = z2 + radbottom * Math.cos(thetaBottomTmp);
         int index = ci + i;
         if (backwards) {
            index = ci + ((int)triangles - 1 - i);
         }

         if (i == 0) {
            NewRenderUtils.addVertex(pose, buf, 0.0F, 0.0F, 0.0F, 0.0F, (float)xa, (float)ya, (float)za, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buf, 0.0F, 0.0F, 0.0F, 0.0F, (float)xb, y2, (float)zb, 0.0F, 1.0F, 240, 240);
         }

         NewRenderUtils.addVertex(pose, buf, colors[index][0], colors[index][1], colors[index][2], a, (float)xa, (float)ya, (float)za, 0.0F, 1.0F, 240, 240);
         NewRenderUtils.addVertex(pose, buf, colors[index][0], colors[index][1], colors[index][2], a, (float)xb, y2, (float)zb, 0.0F, 1.0F, 240, 240);
         averts[k] = xa;
         bverts[k++] = xb;
         averts[k] = za;
         bverts[k++] = zb;
      }

      RenderUtils.conditionalBatchEnder(type, buffer);
      if (!(extendlength <= 0.0)) {
         buf = buffer.getBuffer(type);
         float shadow = 0.7F;
         double yb = extendlength;

         for (int v = 0; v < averts.length; v++) {
            double xa = averts[v++];
            double za = averts[v];
            int index = ci + v / 2;
            if (backwards) {
               index = ci + (averts.length - 1 - v) / 2;
            }

            if (v == 1) {
               NewRenderUtils.addVertex(pose, buf, 0.0F, 0.0F, 0.0F, 0.0F, (float)xa, (float)ya, (float)za, 0.0F, 1.0F, 240, 240);
               NewRenderUtils.addVertex(pose, buf, 0.0F, 0.0F, 0.0F, 0.0F, (float)xa, (float)yb, (float)za, 0.0F, 1.0F, 240, 240);
            } else {
               NewRenderUtils.addVertex(
                  pose,
                  buf,
                  colors[index][0] * shadow,
                  colors[index][1] * shadow,
                  colors[index][2] * shadow,
                  a,
                  (float)xa,
                  (float)ya,
                  (float)za,
                  0.0F,
                  1.0F,
                  240,
                  240
               );
               NewRenderUtils.addVertex(
                  pose,
                  buf,
                  colors[index][0] * shadow,
                  colors[index][1] * shadow,
                  colors[index][2] * shadow,
                  a,
                  (float)xa,
                  (float)yb,
                  (float)za,
                  0.0F,
                  1.0F,
                  240,
                  240
               );
            }
         }

         for (int v = bverts.length - 1; v > 0; v--) {
            double za = bverts[v--];
            double xa = bverts[v];
            int index = ci + v / 2;
            if (backwards) {
               index = ci + (bverts.length - 1 - v) / 2;
            }

            NewRenderUtils.addVertex(
               pose,
               buf,
               colors[index][0] * shadow,
               colors[index][1] * shadow,
               colors[index][2] * shadow,
               a,
               (float)xa,
               (float)ya,
               (float)za,
               0.0F,
               1.0F,
               240,
               240
            );
            NewRenderUtils.addVertex(
               pose,
               buf,
               colors[index][0] * shadow,
               colors[index][1] * shadow,
               colors[index][2] * shadow,
               a,
               (float)xa,
               (float)yb,
               (float)za,
               0.0F,
               1.0F,
               240,
               240
            );
         }

         double xa = averts[0];
         double za = averts[1];
         int index = backwards ? ci + (bverts.length - 1) / 2 : ci;
         NewRenderUtils.addVertex(
            pose,
            buf,
            colors[index][0] * shadow,
            colors[index][1] * shadow,
            colors[index][2] * shadow,
            a,
            (float)xa,
            (float)ya,
            (float)za,
            0.0F,
            1.0F,
            240,
            240
         );
         NewRenderUtils.addVertex(
            pose,
            buf,
            colors[index][0] * shadow,
            colors[index][1] * shadow,
            colors[index][2] * shadow,
            a,
            (float)xa,
            (float)yb,
            (float)za,
            0.0F,
            1.0F,
            240,
            240
         );
         RenderUtils.conditionalBatchEnder(type, buffer);
      }
   }
}
