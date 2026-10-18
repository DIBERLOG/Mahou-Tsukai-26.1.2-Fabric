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
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderMorganBallEntity extends EntityRenderer<MorganBallEntity, DummyEntityRenderState> {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");

   public RenderMorganBallEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderMorganBall(MorganBallEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      float spike_len = entity.prev_spike_len + (entity.getSpikeLength() - entity.prev_spike_len) * partialTicks;
      float sphere_size = entity.prev_sphere_size + (entity.getSphereSize() - entity.prev_sphere_size) * partialTicks;
      matrix.pushPose();
      matrix.translate(x, y, z);
      matrix.scale(sphere_size, sphere_size, sphere_size);
      float[] colors = entity.getColors();
      float outeralpha = 0.06F;
      float beam_y_off = 0.0F;
      int lightmapX = 240;
      int lightmapY = 240;
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      float r2 = colors[4];
      float g2 = colors[5];
      float b2 = colors[6];
      rotate(0.0F, 0.0F, 0.0F, matrix);
      matrix.translate(0.0F, beam_y_off, 0.0F);
      RenderType ringtype = (MahoujinRenderType.createSphereRenderType(RenderUtils.runes, 0));
      RenderType spheretype2 = (MahoujinRenderType.createSphereRenderType(beam, 1));
      RenderUtils.renderSphere(matrix, buf, sphere_size, 20, 240, 240, r2, g2, b2, 1.0F, spheretype2);
      float unscalesphere = 1.0F / sphere_size;
      matrix.scale(unscalesphere, unscalesphere, unscalesphere);
      float scalef = 1.2F;
      float unscalef = 1.0F / scalef;
      if (entity.yaws != null && entity.pitchs != null && entity.ds != null) {
         for (int i = 0; i < entity.yaws.size(); i++) {
            if (i < entity.pitchs.size() && i < entity.ds.size()) {
               RenderUtils.rotateQ(entity.yaws.get(i), 0.0F, 1.0F, 0.0F, matrix);
               RenderUtils.rotateQ(entity.pitchs.get(i), 1.0F, 0.0F, 0.0F, matrix);
               matrix.scale(0.2F, spike_len / 2.0F * entity.ds.get(i), 0.2F);
               matrix.translate(0.0F, 1.0F, 0.0F);
               matrix.scale(unscalef, unscalef, unscalef);
               renderPyramid(matrix, buf, r2, g2, b2, 1.0F, beam);
               matrix.scale(scalef, scalef, scalef);
               renderPyramid(matrix, buf, r, g, b, 1.0F, RenderUtils.runes);
               matrix.translate(0.0F, -1.0F, 0.0F);
               matrix.scale(5.0F, 2.0F / (spike_len * entity.ds.get(i)), 5.0F);
               RenderUtils.rotateQ(-entity.pitchs.get(i), 1.0F, 0.0F, 0.0F, matrix);
               RenderUtils.rotateQ(-entity.yaws.get(i), 0.0F, 1.0F, 0.0F, matrix);
            }
         }
      }

      float sz = 0.5F * sphere_size / entity.maxsize;
      float ht = 0.2F * sphere_size / entity.maxsize;
      float roll = (float)ClientHandler.clientTickCounter;
      RenderUtils.rotateQ(80.0F, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(45.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, sphere_size + sz, ht, 32, 240, 240, r, g, b, 1.0F, ringtype);
      RenderUtils.rotateQ(-roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-45.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-80.0F, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(80.0F, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-45.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, sphere_size + sz, ht, 32, 240, 240, r, g, b, 1.0F, ringtype);
      RenderUtils.rotateQ(-roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(45.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-80.0F, 0.0F, 1.0F, 0.0F, matrix);
      matrix.popPose();
   }

   public static void renderPyramid(PoseStack stack, SubmitNodeCollector buf, float r, float g, float b, float a, Identifier loc) {
      RenderType type = (MahoujinRenderType.createTriangleRenderType(loc, 0));
      int lx = 240;
      int ly = 240;
      buf.submitCustomGeometry(stack,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), 0.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 0.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 0.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 0.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
   }

   public static void rotate(float yaw, float pitch, float roll, PoseStack matrix) {
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
   }
}
