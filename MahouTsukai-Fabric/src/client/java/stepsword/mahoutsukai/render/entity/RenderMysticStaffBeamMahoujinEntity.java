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
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderMysticStaffBeamMahoujinEntity extends EntityRenderer<MysticStaffBeamMahoujinEntity, DummyEntityRenderState> {
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final Identifier beam_start = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particles/white-circle.png");
   private static final String BUILTIN_TEXT_SCOPE = "mystic_staff_beam_mahoujin_builtin_text";

   public RenderMysticStaffBeamMahoujinEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderBeam(MysticStaffBeamMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      float beam_len = entity.prev_beam_len + (entity.getBeamLength() - entity.prev_beam_len) * partialTicks;
      float beam_size = entity.prev_beam_size + (entity.getBeamSize() - entity.prev_beam_size) * partialTicks;
      float mini_speed = entity.getRotationSpeed();
      float mini_roll = entity.getRotationRoll();
      float sphere_size = entity.prev_sphere_size + (entity.getSphereSize() - entity.prev_sphere_size) * partialTicks;
      float size = entity.getCircleSize();
      entity.yRotO %= 360.0F;
      if (entity.yRotO < 0.0F) {
         entity.yRotO += 360.0F;
      }

      if (entity.yRotO > 360.0F) {
         entity.yRotO -= 360.0F;
      }

      float m = entity.getRotationYaw() - entity.yRotO;
      if (m > 180.0F) {
         m -= 360.0F;
      }

      if (m < -180.0F) {
         m += 360.0F;
      }

      float yaw = entity.yRotO + m * partialTicks;
      float pitch = entity.xRotO + (entity.getRotationPitch() - entity.xRotO) * partialTicks;
      matrix.pushPose();
      matrix.translate(x, y, z);
      matrix.scale(size, size, size);
      float[] colors = entity.getColors();
      float outeralpha = 0.3F;
      float beam_y_off = -0.7F;
      int lightmapX = 240;
      int lightmapY = 240;
      float smallsize = size * 0.25F;
      int smallcircles = 6;
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      rotate(0.0F, pitch, yaw, matrix);
      circleRender(lightmapX, lightmapY, r, g, b, a, smallcircles + 1, matrix,buf);
      unrotate(0.0F, pitch, yaw, matrix);
      rotate(entity.prev_mini_roll + partialTicks * (mini_roll - entity.prev_mini_roll), pitch, yaw, matrix);

      for (int i = 0; i < smallcircles; i++) {
         if (entity.getDying() < MysticStaffBeamMahoujinEntity.dyingticks + i * 2 && entity.getLife() > i * 2) {
            float angle = 360.0F / smallcircles;
            RenderUtils.rotateQ(angle, 0.0F, 1.0F, 0.0F, matrix);
            matrix.translate(0.8F, 0.0F, 0.0F);
            matrix.scale(smallsize / size, smallsize / size, smallsize / size);
            circleRender(lightmapX, lightmapY, r, g, b, a, i, matrix,buf);
            matrix.scale(size / smallsize, size / smallsize, size / smallsize);
            matrix.translate(-0.8F, 0.0F, 0.0F);
         }
      }

      unrotate(mini_roll - mini_speed + partialTicks * mini_speed, pitch, yaw, matrix);
      rotate(0.0F, pitch, yaw, matrix);
      matrix.translate(0.0F, beam_y_off, 0.0F);
      RenderType beamtype1 = (MahoujinRenderType.createBeamRenderType(beam, 0));
      RenderType beamtype2 = (MahoujinRenderType.createBeamRenderType(beam, 1));
      RenderType spheretype1 = (MahoujinRenderType.createSphereRenderType(beam, 2));
      RenderType spheretype2 = (MahoujinRenderType.createSphereRenderType(beam, 3));
      float baseInnerRadius = Math.max(0.0F, beam_size - 0.1F);
      float waverTime = entity.getLife() + partialTicks;
      float waverStrength = entity.getLife() > 45 && entity.getDying() <= 0 ? 1.0F : 0.0F;
      float innerWaver = 1.0F
         + waverStrength * 0.008F * (float)Math.sin(waverTime * 0.81F + entity.getId() * 1.31F)
         + waverStrength * 0.006F * (float)Math.sin(waverTime * 2.23F + entity.getId() * 0.43F)
         + waverStrength * 0.004F * (float)Math.sin(waverTime * 3.79F + entity.getId() * 1.89F)
         + waverStrength * 0.002F * (float)Math.sin(waverTime * 6.17F + entity.getId() * 0.77F);
      float innerRadius = baseInnerRadius * innerWaver;
      float outerRadius = beam_size;
      float innerSphereSize = Math.max(sphere_size - 0.1F, 0.0F) * innerWaver;
      RenderUtils.renderSphere(matrix, buf, innerSphereSize, 20, lightmapX, lightmapY, 1.0F, 1.0F, 1.0F, 0.99F, spheretype1);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, innerRadius, beam_len, 64, lightmapX, lightmapY, 1.0F, 1.0F, 1.0F, 0.99F, beamtype1);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, outerRadius, beam_len, 64, lightmapX, lightmapY, r, g, b, outeralpha, beamtype2);
      RenderUtils.renderSphere(matrix, buf, sphere_size, 20, lightmapX, lightmapY, r, g, b, outeralpha, spheretype2);
      matrix.popPose();
   }

   public static void rotate(float yaw, float pitch, float roll, PoseStack matrix) {
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
   }

   public static void unrotate(float yaw, float pitch, float roll, PoseStack matrix) {
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-roll, 0.0F, 1.0F, 0.0F, matrix);
   }

   public static void circleRender(int lightmapX, int lightmapY, float r, float g, float b, float a, int index, PoseStack matrix,SubmitNodeCollector buf) {
      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("mystic_staff_beam_mahoujin_builtin_text", mahoujin, index, false)
      );
      buf.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });

   }
}
