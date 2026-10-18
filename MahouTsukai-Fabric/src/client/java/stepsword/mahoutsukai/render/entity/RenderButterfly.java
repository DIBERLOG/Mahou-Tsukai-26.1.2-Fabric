package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.butterfly.ButterflyEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderButterfly extends EntityRenderer<ButterflyEntity, RenderButterfly.ButterflyRenderState> {
   private static final Identifier butterfly1 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly1.png");
   private static final Identifier butterfly2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly2.png");
   private static final Identifier butterfly3 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly3.png");
   private static final Identifier butterfly4 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly4.png");
   private static final Identifier butterfly5 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly5.png");
   private static final Identifier butterfly6 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly6.png");
   private static final Identifier butterfly7 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/butterfly7.png");
   private static final String BUILTIN_TEXT_SCOPE = "butterfly_entity";
   private static final RenderType b1 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly1, 0, true);
   private static final RenderType b2 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly2, 0, true);
   private static final RenderType b3 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly3, 0, true);
   private static final RenderType b4 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly4, 0, true);
   private static final RenderType b5 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly5, 0, true);
   private static final RenderType b6 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly6, 0, true);
   private static final RenderType b7 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("butterfly_entity", butterfly7, 0, true);

   public RenderButterfly(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public RenderButterfly.ButterflyRenderState createRenderState() {
      return new RenderButterfly.ButterflyRenderState();
   }

   public void extractRenderState(ButterflyEntity entity, RenderButterfly.ButterflyRenderState state, float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.visible = MTConfig.BUTTERFLY_EFFECT_VISIBLE_TO_OTHERS
         || Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(entity.getCasterUUID());
      state.angle = entity.prevangle + partialTicks * (entity.angle - entity.prevangle);
      state.size = entity.getSize();
      state.yaw = calcPartialYaw(entity.yRot, entity.yRotO, partialTicks);
      state.pitch = entity.xRot;

      for (int i = 0; i < state.colors.length; i++) {
         state.colors[i][0] = entity.colors[i][0];
         state.colors[i][1] = entity.colors[i][1];
         state.colors[i][2] = entity.colors[i][2];
      }
   }

   public void submit(RenderButterfly.ButterflyRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      if (state.visible) {
         poseStack.pushPose();
         poseStack.scale(state.size, state.size, state.size);
         RenderUtils.rotateQ(state.yaw, 0.0F, 1.0F, 0.0F, poseStack);
         RenderUtils.rotateQ(state.pitch, 1.0F, 0.0F, 0.0F, poseStack);
         RenderUtils.rotateQ(state.angle, 0.0F, 0.0F, 1.0F, poseStack);
         drawWing(state, poseStack, submitNodeCollector, 0.0F);
         RenderUtils.rotateQ(180.0F - 2.0F * state.angle, 0.0F, 0.0F, 1.0F, poseStack);
         drawWing(state, poseStack, submitNodeCollector, 0.0F);
         poseStack.popPose();
      }

      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public void render(ButterflyEntity entityIn, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource buf, int packedLightIn) {
      boolean z = entityIn.getCasterUUID() != null
         && Minecraft.getInstance().player != null
         && Minecraft.getInstance().player.getUUID().equals(entityIn.getCasterUUID());
      if (MTConfig.BUTTERFLY_EFFECT_VISIBLE_TO_OTHERS || z) {
         matrix.pushPose();
         float angle = entityIn.prevangle + partialTicks * (entityIn.angle - entityIn.prevangle);
         float size = entityIn.getSize();
         float yaw = calcPartialYaw(entityIn.yRot, entityIn.yRotO, partialTicks);
         matrix.scale(size, size, size);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(entityIn.xRot, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(angle, 0.0F, 0.0F, 1.0F, matrix);
         this.drawWing(entityIn, matrix, 0.0F);
         RenderUtils.rotateQ(180.0F - 2.0F * angle, 0.0F, 0.0F, 1.0F, matrix);
         this.drawWing(entityIn, matrix, 0.0F);
         matrix.popPose();
      }
   }

   private void drawWing(ButterflyEntity e, PoseStack matrix, float m) {
      this.drawTexture(m * 0.0F, matrix, 0.0F, 0.0F, 0.0F, 1.0F, b1);
      this.drawTexture(m * 0.001F, matrix, e.colors[0][0] / 255.0F, e.colors[0][1] / 255.0F, e.colors[0][2] / 255.0F, 1.0F, b2);
      this.drawTexture(m * 0.001F, matrix, e.colors[1][0] / 255.0F, e.colors[1][1] / 255.0F, e.colors[1][2] / 255.0F, 1.0F, b3);
      this.drawTexture(m * 0.001F, matrix, e.colors[2][0] / 255.0F, e.colors[2][1] / 255.0F, e.colors[2][2] / 255.0F, 1.0F, b4);
      this.drawTexture(m * 0.001F, matrix, e.colors[3][0] / 255.0F, e.colors[3][1] / 255.0F, e.colors[3][2] / 255.0F, 1.0F, b5);
      this.drawTexture(m * 0.001F, matrix, e.colors[4][0] / 255.0F, e.colors[4][1] / 255.0F, e.colors[4][2] / 255.0F, 1.0F, b6);
      this.drawTexture(m * 0.001F, matrix, e.colors[5][0] / 255.0F, e.colors[5][1] / 255.0F, e.colors[5][2] / 255.0F, 1.0F, b7);
   }

   private void drawTexture(float cy, PoseStack matrix, float r, float g, float b, float a, RenderType type) {
      int lightmapX = 240;
      int lightmapY = 240;
      Matrix4f positionMatrix = matrix.last().pose();
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      VertexConsumer bb = buffer.getBuffer(type);
      bb.addVertex(positionMatrix, 0.0F, cy, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(positionMatrix, 0.0F, cy, 1.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(positionMatrix, 1.0F, cy, 1.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(positionMatrix, 1.0F, cy, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      RenderUtils.conditionalBatchEnder(type, buffer);
   }

   private static void drawWing(RenderButterfly.ButterflyRenderState state, PoseStack matrix, SubmitNodeCollector submitNodeCollector, float m) {
      drawTexture(m * 0.0F, matrix, submitNodeCollector, 0.0F, 0.0F, 0.0F, 1.0F, b1);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[0][0] / 255.0F, state.colors[0][1] / 255.0F, state.colors[0][2] / 255.0F, 1.0F, b2);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[1][0] / 255.0F, state.colors[1][1] / 255.0F, state.colors[1][2] / 255.0F, 1.0F, b3);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[2][0] / 255.0F, state.colors[2][1] / 255.0F, state.colors[2][2] / 255.0F, 1.0F, b4);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[3][0] / 255.0F, state.colors[3][1] / 255.0F, state.colors[3][2] / 255.0F, 1.0F, b5);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[4][0] / 255.0F, state.colors[4][1] / 255.0F, state.colors[4][2] / 255.0F, 1.0F, b6);
      drawTexture(m * 0.001F, matrix, submitNodeCollector, state.colors[5][0] / 255.0F, state.colors[5][1] / 255.0F, state.colors[5][2] / 255.0F, 1.0F, b7);
   }

   private static void drawTexture(float cy, PoseStack matrix, SubmitNodeCollector submitNodeCollector, float r, float g, float b, float a, RenderType type) {
      int lightmapX = 240;
      int lightmapY = 240;
      submitNodeCollector.submitCustomGeometry(matrix, type, (pose, bb) -> {
         Matrix4f positionMatrix = pose.pose();
         bb.addVertex(positionMatrix, 0.0F, cy, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(positionMatrix, 0.0F, cy, 1.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(positionMatrix, 1.0F, cy, 1.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(positionMatrix, 1.0F, cy, 0.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      });
   }

   public static float calcPartialYaw(float yw, float pyw, float partial) {
      yw %= 360.0F;
      if (yw < 0.0F) {
         yw += 360.0F;
      }

      if (yw > 360.0F) {
         yw -= 360.0F;
      }

      pyw %= 360.0F;
      if (pyw < 0.0F) {
         pyw += 360.0F;
      }

      if (pyw > 360.0F) {
         pyw -= 360.0F;
      }

      if (yw < pyw) {
         float d1 = yw + (360.0F - pyw);
         float d2 = pyw - yw;
         return d2 < d1 ? partial * d2 + yw : partial * d1 + pyw;
      } else {
         float d1 = pyw + (360.0F - yw);
         float d2 = yw - pyw;
         return d2 < d1 ? partial * d2 + pyw : partial * d1 + yw;
      }
   }

   public static class ButterflyRenderState extends DummyEntityRenderState {
      public boolean visible;
      public float angle;
      public float size;
      public float yaw;
      public float pitch;
      public final float[][] colors = new float[6][3];
   }
}
