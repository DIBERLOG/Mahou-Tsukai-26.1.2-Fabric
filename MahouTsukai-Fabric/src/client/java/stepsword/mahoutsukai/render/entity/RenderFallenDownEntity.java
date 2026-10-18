package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderFallenDownEntity extends EntityRenderer<FallenDownEntity, DummyEntityRenderState> {
   private static final String BUILTIN_TEXT_SCOPE = "fallen_down_mahoujin_builtin_text";

   public RenderFallenDownEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderFallenDown(FallenDownEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      matrix.translate(0.0, 1.5, 0.0);
      float time = (float)ClientHandler.clientTickCounter * 1.3F;
      float ticksToFullSize = 50.0F;
      float growth = Math.max(0.0F, Math.min(ticksToFullSize, entity.getLife()));
      growth /= ticksToFullSize;
      float rotleft = time;
      float rotright = -time;
      float timeColor = Math.abs((float)(entity.level().getOverworldClockTime() % 24000L) - 6000.0F) / 12000.0F;
      float r1 = 0.19215687F;
      float g1 = 0.3529412F;
      float b1 = 0.7058824F;
      float a1 = 1.0F;
      float r2 = 0.27058825F;
      float g2 = 0.7921569F;
      float b2 = 1.0F;
      float a2 = 1.0F;
      float r = r1 + (r2 - r1) * timeColor;
      float g = g1 + (g2 - g1) * timeColor;
      float b = b1 + (b2 - b1) * timeColor;
      float a = a1 + (a2 - a1) * timeColor;
      r = 0.3F;
      g = 0.8F;
      b = 1.0F;
      a = 1.0F;
      int lightmapX = 240;
      int lightmapY = 240;
      int index = 0;

      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("fallen_down_mahoujin_builtin_text", RenderUtils.mahoujin, 0, false)
      );
      renderPlayerLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      if (growth > 0.0F) {
         renderFirstLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      if (growth > 0.0F) {
         renderSecondLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      if (growth > 0.0F) {
         renderThirdLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      if (growth > 0.0F) {
         renderFourthLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      if (growth > 0.0F) {
         renderFifthLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      if (growth > 0.0F) {
         renderSixthLayer(matrix, rotright, rotleft, buffer, type, r, g, b, a, lightmapX, lightmapY, index, growth);
      }

      matrix.popPose();
   }

   public static void renderPlayerLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      matrix.pushPose();
      RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
      RenderType t2 = RenderUtils.renderRingCull(matrix, buffer, 0.6, 90.0F, 2.0F, 0.2F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
      t2 = RenderUtils.renderRingCull(matrix, buffer, 1.2, 45.0F, 1.4F, 0.2F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      matrix.translate(0.0, 1.2, 0.0);
      RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(2.0F, 2.0F, 2.0F);
      renderCircle(buffer,type, matrix, r, g, b, a, lightmapX, lightmapY);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
      matrix.translate(0.0F, 0.0F, 0.0F);
      int num_circles = 4;

      for (int i = 0; i < num_circles; i++) {
         matrix.pushPose();
         RenderUtils.rotateQ(360 / num_circles * i, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(70.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(0.0F, 2.0F, 0.0F);
         matrix.scale(1.0F, 1.0F, 1.0F);
         renderCircle(buffer,type, matrix, r, g, b, a, lightmapX, lightmapY);
         matrix.popPose();
      }

      matrix.popPose();
   }

   public static void renderFirstLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float prevLevel = 10.0F;
      float threshold = 0.0F;
      float endThreshold = 0.5F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      matrix.pushPose();
      RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
      RenderType t2 = RenderUtils.renderRingCull(matrix, buffer, 4.2, 0.0F, newgrowth * 4.0F, 0.2F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
      t2 = RenderUtils.renderRingCull(matrix, buffer, 4.0, 0.0F, newgrowth * 2.0F, 1.0F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
      matrix.translate(0.0F, 2.0F, 0.0F);
      int num_circles = 8;

      for (int i = 0; i < num_circles; i++) {
         matrix.pushPose();
         RenderUtils.rotateQ(360 / num_circles * i, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(50.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(0.0F, 5.0F, 0.0F);
         matrix.scale(newgrowth * 1.2F, newgrowth * 1.2F, newgrowth * 1.2F);
         if (i % 2 == 0) {
            renderCircle(buffer,type, matrix, r, g, b, a, lightmapX, lightmapY);
         } else {
            t2 = RenderUtils.renderRingCull(matrix, buffer, 0.0, 45.0F, newgrowth * 0.6F, 0.3F, 32, lightmapX, lightmapY, r, g, b, a, index++);
            matrix.pushPose();
            matrix.translate(0.0, 0.1, 0.0);
            t2 = RenderUtils.renderRingCull(matrix, buffer, 0.0, 0.0F, newgrowth * 0.3F, 0.1F, 32, lightmapX, lightmapY, r, g, b, a, index++);
            matrix.popPose();
            matrix.pushPose();
            matrix.translate(0.0, 0.2, 0.0);
            t2 = RenderUtils.renderRingCull(matrix, buffer, 0.0, 0.0F, newgrowth * 0.4F, 0.1F, 32, lightmapX, lightmapY, r, g, b, a, index++);
            matrix.popPose();
            t2 = RenderUtils.renderRing(matrix, buffer, 0.0, 45.0F, newgrowth * 0.6F, 0.2F, 32, lightmapX, lightmapY, r, g, b, a, index++, RenderUtils.beam);
         }
         matrix.popPose();
      }

      matrix.popPose();
   }

   public static void renderSecondLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float prevLevel = 10.0F;
      float threshold = 0.0F;
      float endThreshold = 0.5F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      matrix.pushPose();
      RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
      RenderType t2 = RenderUtils.renderRingCull(matrix, buffer, 10.0, 0.0F, newgrowth * 6.0F, 0.5F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
      t2 = RenderUtils.renderRingCull(matrix, buffer, 10.0, 0.0F, newgrowth * 4.0F, 1.5F, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
      matrix.pushPose();
      RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
      matrix.translate(0.0F, newgrowth * 3.0F, 0.0F);
      int num_circles = 6;

      for (int i = 0; i < num_circles; i++) {
         matrix.pushPose();
         RenderUtils.rotateQ(360 / num_circles * i, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(60.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(0.0F, 9.0F, 0.0F);
         matrix.scale(newgrowth * 4.0F, newgrowth * 4.0F, newgrowth * 4.0F);
         renderCircle(buffer,type, matrix, r, g, b, a, lightmapX, lightmapY);
         matrix.popPose();
      }

      matrix.popPose();
   }

   public static void renderThirdLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float prevLevel = 10.0F;
      float threshold = 0.6F;
      float endThreshold = 0.7F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, prevLevel + 5.0F * newgrowth, 0.0F, 6.0F + newgrowth * 2.0F, newgrowth * 0.8F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, prevLevel + 5.0F * newgrowth, 0.0F, 4.0F, newgrowth * 2.0F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }
   }

   public static void renderFourthLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float prevLevel = 10.0F;
      float threshold = 0.5F;
      float endThreshold = 0.6F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, prevLevel + 2.0F * newgrowth, 0.0F, 2.0F + newgrowth * 2.0F, newgrowth * 2.0F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 5.0 + 2.5 * newgrowth, 90.0F, 2.0F + newgrowth * 4.0F, newgrowth * 0.6F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }
   }

   public static void renderFifthLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float threshold = 0.7F;
      float endThreshold = 0.75F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 15.0F + 3.0F * newgrowth, 90.0F, 8.0F + newgrowth * 6.0F, newgrowth * 1.5F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 10.0F + 4.0F * newgrowth, 90.0F, 4.0F + newgrowth * 7.0F, newgrowth * 1.2F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         matrix.translate(0.0F, 15.0F + 3.0F * newgrowth, 0.0F);
         RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
         matrix.scale(16.0F + newgrowth * 2.0F, 16.0F + newgrowth * 2.0F, 16.0F + newgrowth * 2.0F);
         renderCircle(buffer,type, matrix, r, g, b, a, lightmapX, lightmapY);
         matrix.popPose();
      }
   }

   public static void renderSixthLayer(
      PoseStack matrix,
      float rotright,
      float rotleft,
      SubmitNodeCollector buffer,
      RenderType type,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      int index,
      float growth
   ) {
      float threshold = 0.75F;
      float endThreshold = 0.8F;
      float newgrowth = (growth - threshold) / (endThreshold - threshold);
      newgrowth = Math.min(newgrowth, 1.0F);
      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 10.0F + 3.0F * newgrowth, 0.0F, 8.0F + newgrowth * 8.0F, newgrowth * 3.0F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotleft, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 18.0F + 3.0F * newgrowth, 0.0F, 9.0F + newgrowth * 12.0F, newgrowth * 3.2F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }

      if (growth > threshold) {
         matrix.pushPose();
         RenderUtils.rotateQ(rotright, 0.0F, 1.0F, 0.0F, matrix);
         RenderType t2 = RenderUtils.renderRingCull(
            matrix, buffer, 18.0F + 3.0F * newgrowth, 0.0F, 9.0F + newgrowth * 4.5F, newgrowth * 5.2F, 128, lightmapX, lightmapY, r, g, b, a, index++
         );
         matrix.popPose();
      }
   }

   public static void renderCircle(SubmitNodeCollector buffer,RenderType type, PoseStack matrix, float r, float g, float b, float a, int lightmapX, int lightmapY) {
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      Matrix4f m = pose.pose();
      bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
   }
}
