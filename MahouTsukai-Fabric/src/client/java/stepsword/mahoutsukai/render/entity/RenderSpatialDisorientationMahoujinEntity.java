package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity;

import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderSpatialDisorientationMahoujinEntity extends EntityRenderer<SpatialDisorientationMahoujinEntity, DummyEntityRenderState> {
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "spatial_disorientation_mahoujin_builtin_text";

   public RenderSpatialDisorientationMahoujinEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderSpatial(SpatialDisorientationMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      matrix.pushPose();
      int life = entity.getLife();
      float size = entity.getCircleSize();
      float yaw = entity.yRot % 360.0F;
      float roll = entity.getRotationRoll();
      float pitch = entity.getRotationPitch();
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      float prevYaw = entity.yRotO % 360.0F;
      if (prevYaw < 0.0F) {
         prevYaw += 360.0F;
      }

      if (prevYaw - yaw > 300.0F) {
         yaw += 360.0F;
      }

      if (prevYaw - yaw < 300.0F && prevYaw - yaw > 0.0F) {
         prevYaw = yaw;
      }

      yaw = entity.yRotO + partialTicks * (yaw - prevYaw);
      matrix.translate(x, y, z);
      matrix.scale(size, size, size);
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);

      renderAll(entity, matrix, buf);
      matrix.popPose();
   }

   public static void renderAll(SpatialDisorientationMahoujinEntity entity, PoseStack matrix, SubmitNodeCollector buffer) {
      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("spatial_disorientation_mahoujin_builtin_text", mahoujin, 0, true)
      );

      int lightmapX = 240;
      int lightmapY = 240;
      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();
      bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      double cy = -1.5;
      float f = 0.0F;
      float rad = 0.8F;
      float h = 0.125F;
      int segments = 128;
      int index = 0;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad * 2.0F, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.2F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad * 2.0F, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.2F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad * 2.0F, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.2F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad * 2.0F, h, segments, lightmapX, lightmapY, r, g, b, a, index++);
      cy = 0.0;
      f = 0.0F;
      rad = 0.7F;
      h = 0.125F;
      int var51 = 128;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.15F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.15F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.3;
      f += 20.0F;
      rad -= 0.15F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.4;
      f += 15.0F;
      rad -= 0.15F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.5;
      f += 10.0F;
      rad -= 0.05F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
      cy += 0.6;
      f += 5.0F;
      rad -= 0.02F;
      RenderUtils.renderRingCull(matrix, buffer, cy, f, rad, h, var51, lightmapX, lightmapY, r, g, b, a, index++);
   }

}
