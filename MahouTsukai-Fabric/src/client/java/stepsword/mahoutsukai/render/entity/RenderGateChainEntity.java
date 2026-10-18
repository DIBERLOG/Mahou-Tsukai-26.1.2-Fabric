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
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.mahoujin.GateChainEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderGateChainEntity extends EntityRenderer<GateChainEntity, DummyEntityRenderState> {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "gate_chain_mahoujin_builtin_text";

   public RenderGateChainEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderCircle(GateChainEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      float size = entity.getCircleSize();
      float yaw = (entity.yRot + 180.0F) % 360.0F;
      float pitch = (entity.xRot + 90.0F) % 360.0F;
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      float prevYaw = (entity.yRotO + 180.0F) % 360.0F;
      if (prevYaw < 0.0F) {
         prevYaw += 360.0F;
      }

      if (prevYaw - yaw > 300.0F) {
         yaw += 360.0F;
      }

      if (prevYaw - yaw < 300.0F && prevYaw - yaw > 0.0F) {
         prevYaw = yaw;
      }

      yaw = prevYaw + partialTicks * (yaw - prevYaw);
      float len = entity.lastlen + (entity.getLength() - entity.lastlen) * partialTicks;
      matrix.scale(size, size, size);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("gate_chain_mahoujin_builtin_text", mahoujin, 0, false)
      );
      int lightmapX = 240;
      int lightmapY = 240;
      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = 1.0F;
      if (entity.level() != null) {
         RenderUtils.rotateQ((float)ClientHandler.clientTickCounter, 0.0F, 1.0F, 0.0F, matrix);
      }

      buf.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      matrix.translate(0.0, 0.08, 0.0);
      buf.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.4F, 0.0F, -0.4F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), -0.4F, 0.0F, 0.4F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.4F, 0.0F, 0.4F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.4F, 0.0F, -0.4F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      RenderUtils.rotateQ((float)(-ClientHandler.clientTickCounter), 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ((float)(-ClientHandler.clientTickCounter), 0.0F, 1.0F, 0.0F, matrix);
      RenderType type2 = (MahoujinRenderType.createScopedRunesRenderType("gate_chain_head", RenderUtils.runes, 0));
      RenderUtils.renderRing(matrix, buf, 0.0, 45.0F, 0.4F, 0.06F, 17, 240, 240, r, g, b, a, type2);
      RenderUtils.rotateQ((float)ClientHandler.clientTickCounter, 0.0F, 1.0F, 0.0F, matrix);
      matrix.translate(0.0, -0.08, 0.0);
      renderChain(matrix, buf, r, g, b, a, len);
      matrix.popPose();
   }

   public static void renderChain(PoseStack m, SubmitNodeCollector buf, float r, float g, float b, float a, float len) {
      float radius = 0.1F;
      float linkdist = 0.04F;
      float height = 0.1F;
      float overallscale = 0.9F;
      m.translate(0.0F, -len - radius, 0.0F);
      renderPyramid(m, buf, r, g, b, a);
      m.translate(0.0F, len + radius, 0.0F);
      int k = 0;

      for (float i = len - radius; i > 0.0F; k++) {
         m.translate(0.0F, -i, 0.0F);
         RenderUtils.rotateQ(90 * k, 0.0F, 1.0F, 0.0F, m);
         RenderUtils.rotateQ(90.0F, 1.0F, 0.0F, 0.0F, m);
         scaleM(m, 0.55F, overallscale, overallscale, false);
         m.translate(0.0F, height / 2.0F, 0.0F);
         RenderType type = (MahoujinRenderType.createScopedRunesRenderType("gate_chain_link", RenderUtils.runes, k));
         RenderUtils.renderRing(m, buf, 0.0, 90.0F, radius, height, MTConfig.CHAIN_QUALITY, 240, 240, r, g, b, a, type);
         m.translate(0.0F, -height / 2.0F, 0.0F);
         scaleM(m, 0.55F, overallscale, overallscale, true);
         RenderUtils.rotateQ(-90.0F, 1.0F, 0.0F, 0.0F, m);
         RenderUtils.rotateQ(-90 * k, 0.0F, 1.0F, 0.0F, m);
         m.translate(0.0F, i, 0.0F);
         i -= 2.0F * radius * overallscale - linkdist;
      }
   }

   public static void renderPyramid(PoseStack stack, SubmitNodeCollector buf, float r, float g, float b, float a) {
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, stack);
      scaleM(stack, 0.08F, 0.15F, 0.08F, false);
      RenderType type = (MahoujinRenderType.createTriangleRenderType(RenderUtils.runes, 0));
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
      RenderType type2 = (MahoujinRenderType.createMahoujinRenderType(RenderUtils.runes, 0, false));
      buf.submitCustomGeometry(stack,type2,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, -1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -1.0F, -1.0F, 1.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
      });
      scaleM(stack, 0.08F, 0.15F, 0.08F, true);
      RenderUtils.rotateQ(-180.0F, 1.0F, 0.0F, 0.0F, stack);
   }

   public static void scaleM(PoseStack m, float x, float y, float z, boolean unscale) {
      if (unscale) {
         m.scale(1.0F / x, 1.0F / y, 1.0F / z);
      } else {
         m.scale(x, y, z);
      }
   }
}
