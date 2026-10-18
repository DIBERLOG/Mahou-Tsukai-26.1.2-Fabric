package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.entity.mahoujin.LightningEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderLightningEntity extends EntityRenderer<LightningEntity, DummyEntityRenderState> {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");

   public RenderLightningEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderLightning(LightningEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      float[] colors = entity.getColor();
      int lightmapX = 240;
      int lightmapY = 240;
      float w = entity.getBeamSize();
      float r2 = colors[0];
      float g2 = colors[1];
      float b2 = colors[2];
      float a2 = colors[3];
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      float a = 1.0F;
      r2 = 0.2F;
      g2 = 0.5F;
      b2 = 1.0F;
      a2 = 0.2F;
      RenderUtils.rotateQ(entity.yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(entity.pitch, 1.0F, 0.0F, 0.0F, matrix);
      if (entity.getZap()) {
         RenderUtils.rotateQ((float)(-ClientHandler.clientTickCounter % 40L), 0.0F, 1.0F, 0.0F, matrix);
      } else {
         RenderUtils.rotateQ((float)(ClientHandler.clientTickCounter % 40L), 0.0F, 1.0F, 0.0F, matrix);
      }
      float th = 0.0F;
      matrix.pushPose();
      if (entity.getHasBall()) {
         RenderType sphere = (MahoujinRenderType.createSphereRenderType(beam, 0));
         RenderUtils.renderSphere(matrix, buf, w * 5.0F, 14, lightmapX, lightmapY, r, g, b, a, sphere);
      }

      matrix.pushPose();

      for (int i = 0; i < entity.segments; i++) {
         RenderType ringType = (MahoujinRenderType.createScopedRunesRenderType("lightning_inner", beam, 0));
         float pit = entity.prevpitchs[i] + (entity.pitchs[i] - entity.prevpitchs[i]) * partialTicks;
         RenderUtils.rotateQ(pit, 1.0F, 0.0F, 0.0F, matrix);
         float h = entity.prevreallengths[i] + (entity.reallengths[i] - entity.prevreallengths[i]) * partialTicks;
         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w, h, 20, 240, 240, r, g, b, a, ringType);
         matrix.translate(0.0F, -h, 0.0F);
         th -= h;
         RenderUtils.rotateQ(-pit, 1.0F, 0.0F, 0.0F, matrix);
      }

      matrix.popPose();

      for (int i = 0; i < entity.segments; i++) {
         RenderType ringType2 = (MahoujinRenderType.createScopedRunesRenderType("lightning_outer", beam, 1));
         float pit = entity.prevpitchs[i] + (entity.pitchs[i] - entity.prevpitchs[i]) * partialTicks;
         RenderUtils.rotateQ(pit, 1.0F, 0.0F, 0.0F, matrix);
         float h = entity.prevreallengths[i] + (entity.reallengths[i] - entity.prevreallengths[i]) * partialTicks;
         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w * 2.8F, h, 20, 240, 240, r2, g2, b2, a2, ringType2);
         matrix.translate(0.0F, -h, 0.0F);
         th -= h;
         RenderUtils.rotateQ(-pit, 1.0F, 0.0F, 0.0F, matrix);
      }

      matrix.popPose();
      if (entity.getHasBall()) {
         RenderType sphere = (MahoujinRenderType.createSphereRenderType(beam, 0));
         RenderUtils.renderSphere(matrix, buf, w * 6.9F, 14, lightmapX, lightmapY, r2, g2, b2, a2 / 2.0F, sphere);
      }

      matrix.popPose();
   }
}
