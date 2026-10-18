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
import stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderAuthorityMahoujinEntity extends EntityRenderer<AuthorityMahoujinEntity, DummyEntityRenderState> {
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "authority_mahoujin_builtin_text";
   static RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("authority_mahoujin_builtin_text", mahoujin, 0, false);

   public RenderAuthorityMahoujinEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderAuthority(AuthorityMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
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
      matrix.pushPose();
      matrix.translate(x, y, z);
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
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      Matrix4f m=pose.pose();
      bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      float height = 0.125F;
      float space = height / 4.0F;
      float ringsize = 0.5F + space;
      float flatness = 10.0F;
      double yOffset = (height + space) * Math.sin(flatness);
      double yy = 0.0;
      float innersizeOffset = 0.2F;
      RenderUtils.renderRing(matrix, buffer, yy, 0.0F, ringsize, height, 128, lightmapX, lightmapY, r, g, b, a, 0);

      matrix.popPose();
   }
}
