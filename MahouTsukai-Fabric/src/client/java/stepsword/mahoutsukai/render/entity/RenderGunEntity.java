package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.entity.mahoujin.GunEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBufferWrapper;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderGunEntity extends EntityRenderer<GunEntity, DummyEntityRenderState> {
   public static final Identifier gun = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/gun_pattern.png");
   private static final String BUILTIN_TEXT_SCOPE = "gun_mahoujin_builtin_text";

   public RenderGunEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderGun(GunEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      renderGun(x,y,z,entity.getRotationYaw(),entity.getRotationPitch(),entity.getRotationRoll(),entity.getCircleSize(),matrix,buffer,entity.getColors());
   }

   public static void renderGun(GunEntity entity, double x, double y, double z, PoseStack matrix, MultiBufferSource buffer, float partialTicks) {
      float size = entity.getCircleSize();
      float[] colors = entity.getColors();
      matrix.pushPose();
      renderGun(x, y, z, entity.getRotationYaw(), entity.getRotationPitch(), entity.getRotationRoll(), size, matrix, buffer, colors);
      matrix.popPose();
   }

   public static void renderGun(
      double x, double y, double z, float yRot, float pRot, float dRot, float size, PoseStack matrix, MultiBufferSource buffer, float[] colors
   ) {
      renderGun(
         x,
         y,
         z,
         yRot,
         pRot,
         dRot,
         size,
         matrix,
         new RenderBufferWrapper.MultiBufferSourceWrapper((MultiBufferSource)(buffer != null ? buffer : Minecraft.getInstance().renderBuffers().bufferSource())),
         colors
      );
   }

   public static void renderGun(
      double x, double y, double z, float yRot, float pRot, float dRot, float size, PoseStack matrix, SubmitNodeCollector submitNodeCollector, float[] colors
   ) {
      renderGun(x, y, z, yRot, pRot, dRot, size, matrix, new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitNodeCollector), colors);
   }

   private static void renderGun(
      double x, double y, double z, float yRot, float pRot, float dRot, float size, PoseStack matrix, RenderBufferWrapper buffer, float[] colors
   ) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(dRot, 0.0F, 0.0F, 1.0F, matrix);
      RenderUtils.rotateQ(yRot, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pRot, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(size, size, size);
      float[] bottom_barrel_color = new float[]{0.4F, 0.26F, 0.36F, 1.0F};
      float[] top_barrel_color = new float[]{0.15F, 0.1F, 0.1F, 1.0F};
      float[] ornament_color = new float[]{0.8345F, 0.7796F, 0.5051F, 1.0F};
      float[] magic_color = new float[]{colors[0], colors[1], colors[2], 1.0F};
      float[] top_barrel_measure = new float[]{1.04F, 0.025F, 0.75F, 0.0F, 0.025F, 0.025F, 0.0F, 1.0F};
      float[] bottom_barrel_measure = new float[]{1.0F, 0.04F, 0.75F, 0.04F, 0.0F, 0.04F, 0.0F, 1.0F};
      float[] ornament1_measure = new float[]{0.032F, 0.05F, 0.65F, 0.34F, -0.0025F, 0.05F, 0.25F, 0.4F};
      float[] ornament2_measure = new float[]{0.024F, 0.05F, 0.65F, 0.72F, -0.0025F, 0.05F, 0.6F, 0.75F};
      float[] ornament3_measure = new float[]{0.2F, 0.06F, 0.3F, 0.98F, 0.0F, 0.08F, 0.2F, 0.8F};
      float[] stock_measure = new float[]{0.04F, 0.04F, 0.75F, 1.18F, 0.005F, 0.055F, 0.25F, 0.5F};
      float[] circle_measure = new float[]{0.24F, 0.24F, -0.05F, 1.08F, 0.005F, 0.055F, 0.25F, 0.5F};
      float[] circle2_measure = new float[]{0.22F, 0.22F, 0.05F, 1.08F, 0.005F, 0.055F, 0.25F, 0.5F};
      int lx = 240;
      int ly = 240;
      renderBarrel(bottom_barrel_color, bottom_barrel_measure, lx, ly, matrix, buffer);
      renderBarrel(ornament_color, ornament1_measure, lx, ly, matrix, buffer);
      renderBarrel(ornament_color, ornament2_measure, lx, ly, matrix, buffer);
      renderBarrel(ornament_color, ornament3_measure, lx, ly, matrix, buffer);
      renderBarrel(top_barrel_color, top_barrel_measure, lx, ly, matrix, buffer);
      renderStock(bottom_barrel_color, stock_measure, lx, ly, matrix, buffer);
      float gameTime = Minecraft.getInstance().level != null ? (float)Minecraft.getInstance().level.getGameTime() : 0.0F;
      renderMahoujin(magic_color, circle_measure, gameTime, lx, ly, matrix, buffer);
      renderMahoujin(magic_color, circle2_measure, gameTime, lx, ly, matrix, buffer);
      matrix.popPose();
   }

   public static void renderBarrel(float[] rgba, float[] measures, int lx, int ly, PoseStack matrix, RenderBufferWrapper buf) {
      float r = rgba[0];
      float g = rgba[1];
      float b = rgba[2];
      float a = rgba[3];
      float len = measures[0];
      float size = measures[1];
      float thickness = measures[2];
      float zPos = measures[3];
      float yPos = measures[4];
      float height = measures[5];
      float u = measures[6];
      float v = measures[7];
      matrix.pushPose();
      matrix.translate(yPos, 0.0F, zPos);
      renderRectPrism(r, g, b, a, size, len, height, 240, 240, matrix, buf, u, v);
      renderRectPrism(r - 0.1F, g - 0.1F, b - 0.1F, a, size * thickness, len, height * thickness, lx, ly, matrix, buf, u, v);
      renderHollowSquare(r - 0.05F, g - 0.05F, b - 0.05F, a, size, len, height, 0.0F, thickness, lx, ly, matrix, buf);
      renderHollowSquare(r - 0.05F, g - 0.05F, b - 0.05F, a, size, len, height, 1.0F, thickness, lx, ly, matrix, buf);
      matrix.popPose();
   }

   public static void renderRectPrism(
      float r, float g, float b, float a, float size, float len, float height, int lx, int ly, PoseStack matrix, RenderBufferWrapper buf, float u, float v
   ) {
      RenderType type = MahoujinRenderType.createBoxRenderType(gun, 0);
      matrix.pushPose();
      matrix.scale(height, size, len);
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, -0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(0.0F, u).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, -0.5F, 1.0F).setColor(r, g, b, a).setUv(0.0F, v).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(0.25F, u).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, 0.5F, 1.0F).setColor(r, g, b, a).setUv(0.25F, v).setUv2(lx, ly);
         bb.addVertex(m, 0.5F, 0.5F, 0.0F).setColor(r, g, b, a).setUv(0.5F, u).setUv2(lx, ly);
         bb.addVertex(m, 0.5F, 0.5F, 1.0F).setColor(r, g, b, a).setUv(0.5F, v).setUv2(lx, ly);
         bb.addVertex(m, 0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(0.75F, u).setUv2(lx, ly);
         bb.addVertex(m, 0.5F, -0.5F, 1.0F).setColor(r, g, b, a).setUv(0.75F, v).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, -0.5F, 0.0F).setColor(r, g, b, a).setUv(1.0F, u).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, -0.5F, 1.0F).setColor(r, g, b, a).setUv(1.0F, v).setUv2(lx, ly);
      });
      matrix.popPose();
   }

   public static void renderHollowSquare(
      float r,
      float g,
      float b,
      float a,
      float size,
      float len,
      float height,
      float zPos,
      float thickness,
      int lx,
      int ly,
      PoseStack matrix,
      RenderBufferWrapper buf
   ) {
      RenderType type = MahoujinRenderType.createHollowSquareRenderType(gun, 0, false);
      matrix.pushPose();
      matrix.scale(height, size, len);
      float outer = 0.5F;
      float inner = 0.5F * thickness;
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, -inner, -inner, zPos).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -outer, -inner, zPos).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -outer, outer, zPos).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, -inner, outer, zPos).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, -inner, inner, zPos).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -inner, outer, zPos).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, outer, outer, zPos).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, outer, inner, zPos).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, inner, inner, zPos).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, outer, inner, zPos).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, outer, -outer, zPos).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, inner, -outer, zPos).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, inner, -inner, zPos).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, inner, -outer, zPos).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -outer, -outer, zPos).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, -outer, -inner, zPos).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      matrix.popPose();
   }

   public static void renderStock(float[] rgba, float[] measures, int lx, int ly, PoseStack matrix, RenderBufferWrapper buf) {
      RenderType type = MahoujinRenderType.createBoxRenderType(gun, 0);
      float r = rgba[0];
      float g = rgba[1];
      float b = rgba[2];
      float a = rgba[3];
      float len = measures[0];
      float size = measures[1];
      float thickness = measures[2];
      float zPos = measures[3];
      float yPos = measures[4];
      float height = measures[5];
      matrix.pushPose();
      matrix.translate(yPos, 0.0F, zPos);
      matrix.scale(height, size, len);
      int pieces = 20;
      float x1 = -0.5F;
      float x2 = 0.5F;
      float y1 = -0.5F;
      float y2 = 0.5F;
      float z1 = -0.5F;
      float z2 = 0.5F;
      float Ax = x1;
      float Az = z1;
      float Bx = x2;
      float Bz = z1;
      float Cx = x1;
      float Cz = z2;
      float Dx = x2;
      float Dz = z2;
      float[] diffsCz = new float[]{0.0F, 1.2F, 3.0F, 4.5F, 6.2F, 7.2F, 8.0F, 8.5F, 8.6F};
      diffsCz = new float[]{0.0F, 0.8F, 1.6F, 2.1F, 2.5F, 2.8F, 3.0F, 3.1F, 3.05F};
      diffsCz = new float[]{0.0F, 0.0F, 1.5F, 3.5F, 3.2F};
      diffsCz = new float[]{0.0F, 0.2F, 0.4F, 1.8F, 3.4F};
      float scale = 0.7F;
      float darken = 0.003F;
      float czdiff = 0.0F;
      float dzdiff = 0.0F;

      for (int p = 0; p < pieces; p++) {
         r -= darken;
         g -= darken;
         b -= darken;
         czdiff = 1.0F + parabola(p, 0.015F, 2.0F);
         dzdiff = 1.0F + parabola(p, 0.004F, 2.0F);
         Cx = x1 + parabola_bottom(dzdiff) * scale;
         Cz = z2 + dzdiff * scale;
         Dx = x2 + parabola_top(czdiff) * scale;
         Dz = z2 + czdiff * scale;
         float fr = r;
         float fg = g;
         float fb = b;
         float fAx = Ax;
         float fAz = Az;
         float fBx = Bx;
         float fBz = Bz;
         float fCx = Cx;
         float fCz = Cz;
         float fDx = Dx;
         float fDz = Dz;
         buf.submit(matrix, type, (pose, bb) -> {
            Matrix4f m = pose.pose();
            bb.addVertex(m, fAx, y1, fAz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fCx, y1, fCz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fAx, y2, fAz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fCx, y2, fCz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fBx, y2, fBz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fDx, y2, fDz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fBx, y1, fBz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fDx, y1, fDz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fAx, y1, fAz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(m, fCx, y1, fCz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         });
         Ax = Cx;
         Az = Cz;
         Bx = Dx;
         Bz = Dz;
      }

      float fr = r;
      float fg = g;
      float fb = b;
      float fCx = Cx;
      float fCz = Cz;
      float fDx = Dx;
      float fDz = Dz;
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, fCx, y1, fCz).setColor(fr, fg, fb, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, fDx, y1, fDz).setColor(fr, fg, fb, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, fCx, y2, fCz).setColor(fr, fg, fb, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, fDx, y2, fDz).setColor(fr, fg, fb, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      matrix.popPose();
   }

   public static float parabola_top(float input) {
      return parabola(input, -0.0012F, 4.0F);
   }

   public static float parabola_bottom(float input) {
      return parabola(input, -0.09F, 3.0F);
   }

   public static float parabola(float input, float scale, float power) {
      return scale * (float)Math.pow(input, power);
   }

   public static void renderMahoujin(float[] rgba, float[] measures, float angle, int lx, int ly, PoseStack matrix, RenderBufferWrapper buf) {
      float r = rgba[0];
      float g = rgba[1];
      float b = rgba[2];
      float a = rgba[3];
      float len = measures[0];
      float size = measures[1];
      float thickness = measures[2];
      float zPos = measures[3];
      float yPos = measures[4];
      float height = measures[5];
      float u = measures[6];
      float v = measures[7];
      matrix.pushPose();
      matrix.translate(yPos, thickness, zPos);
      RenderUtils.rotateQ(angle, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(size, 1.0F, size);
      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("gun_mahoujin_builtin_text", RenderUtils.mahoujin, 0, false)
      );
      buf.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      });
      buf.endBatch(type);
      matrix.popPose();
   }

   protected AABB getBoundingBoxForCulling(GunEntity entity) {
      return GunEntity.bb;
   }
}
