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
import stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderRhoAiasMahoujinEntity extends EntityRenderer<RhoAiasMahoujinEntity, DummyEntityRenderState> {
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "rho_aias_mahoujin_builtin_text";

   public RenderRhoAiasMahoujinEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderRhoAias(RhoAiasMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      int life = entity.getLife();
      float size = entity.getCircleSize();
      float yaw = entity.getYRot() % 360.0F;
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
      matrix.scale(size, size, size);
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
      int lightmapX = 240;
      int lightmapY = 240;
      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      float r2 = colors[4];
      float g2 = colors[5];
      float b2 = colors[6];

      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("rho_aias_mahoujin_builtin_text", mahoujin, 0, false);
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      Matrix4f m = pose.pose();
      bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r2, g2, b2, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r2, g2, b2, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r2, g2, b2, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r2, g2, b2, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      float height = 0.125F;
      float space = height / 4.0F;
      float ringsize = 0.5F + space;
      float flatness = 10.0F;
      float ff = flatness;
      double yOffset = (height + space) * Math.sin(flatness);
      float yy = 0.0F;
      float innersizeOffset = 0.2F;
      int index = 0;
      RenderType t2 = RenderUtils.renderRing(matrix, buffer, yy, ff * 1.8F, ringsize, height, 128, lightmapX, lightmapY, r2, g2, b2, a, index++);
      ringsize = (float)(ringsize + (height + space + 0.08));
      ff += flatness;
      yy = (float)(yy + yOffset);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize, height, 128, lightmapX, lightmapY, r2, g2, b2, a, index++);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize - innersizeOffset, height, 128, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(15.0F, 0.0F, 1.0F, 0.0F, matrix);
      type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("rho_aias_mahoujin_builtin_text", mahoujin, 1, false);
      final float quadY=yy;buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      Matrix4f m = pose.pose();
      bb.addVertex(m, -0.5F, quadY, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, -0.5F, quadY, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, quadY, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, quadY, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      RenderUtils.rotateQ(15.0F, 0.0F, 1.0F, 0.0F, matrix);
      ringsize = (float)(ringsize + (height + space + 0.08));
      ff += flatness;
      yy = (float)(yy + yOffset);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize, height, 128, lightmapX, lightmapY, r2, g2, b2, a, index++);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize - innersizeOffset, height, 128, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.rotateQ(15.0F, 0.0F, 1.0F, 0.0F, matrix);
      ringsize += height + space;
      ff += flatness;
      yy = (float)(yy + yOffset);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize, height, 128, lightmapX, lightmapY, r2, g2, b2, a, index++);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize - innersizeOffset, height, 128, lightmapX, lightmapY, r, g, b, a, index++);
      RenderUtils.rotateQ(15.0F, 0.0F, 1.0F, 0.0F, matrix);
      ringsize += height + space * 2.0F;
      ff += flatness;
      yy = (float)(yy + yOffset * 2.0);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize, height, 128, lightmapX, lightmapY, r2, g2, b2, a, index++);
      t2 = RenderUtils.renderRing(matrix, buffer, yy, ff, ringsize - innersizeOffset, height, 128, lightmapX, lightmapY, r, g, b, a, index++);
      matrix.popPose();
   }
}
