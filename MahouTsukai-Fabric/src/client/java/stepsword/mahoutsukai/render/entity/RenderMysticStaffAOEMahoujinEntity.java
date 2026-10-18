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
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class RenderMysticStaffAOEMahoujinEntity extends EntityRenderer<MysticStaffAOEMahoujinEntity, EntityRenderState> {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "mystic_staff_mahoujin_builtin_text";
   public static final int loadUpTime = 20;

   public RenderMysticStaffAOEMahoujinEntity(Context renderManager) {
      super(renderManager);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }

   public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
   }

   public static void renderCircle(MysticStaffAOEMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      int life = entity.getLife();
      float size = entity.getCircleSize();
      float yaw = entity.yRot % 360.0F;
      float roll = entity.getRotationRoll();
      float pitch = entity.getRotationPitch();
      boolean renderBeam = false;
      if (life < 20) {
         size = (life + partialTicks) / 20.0F * size;
      }

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
      RenderType type = (
         MahoujinRenderType.createBuiltInTextMahoujinRenderType("mystic_staff_mahoujin_builtin_text", mahoujin, 0, true)
      );
      int lightmapX = 240;
      int lightmapY = 240;
      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      buf.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      if (renderBeam) {
         float beamSize = 0f;
         matrix.scale(1.0F / size, 1.0F / size, 1.0F / size);
         matrix.translate(-0.5, -0.5, -0.5);
         if (colors[1] < 0.26F) {
            colors[1] = (float)(colors[1] + 0.33);
         }

         matrix.translate(0.5, 0.0, 0.5);
         renderBeamSegment(matrix, buf, (int)0, (int)0, beamSize, beamSize + 0.2, r, g, b);
      }

      matrix.popPose();
   }

   public static void renderBeamSegment(
      PoseStack matrix, SubmitNodeCollector buf, int yOffset, int height, double beamRadius, double glowRadius, float r, float g, float b
   ) {
      RenderType beamtype1 = (MahoujinRenderType.createBeamRenderType(beam, 0));
      RenderType beamtype2 = (MahoujinRenderType.createBeamRenderType(beam, 1));
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
      int lightmapX = 240;
      int lightmapY = 240;
      RenderUtils.renderRing(
         matrix, buf, -yOffset, 90.0F, (float)Math.max(0.0, beamRadius - 0.1F), height, 64, lightmapX, lightmapY, 1.0F, 1.0F, 1.0F, 0.99F, beamtype1
      );
      RenderUtils.renderRing(matrix, buf, -yOffset, 90.0F, (float)glowRadius, height, 64, lightmapX, lightmapY, r, g, b, 0.3F, beamtype2);
      RenderUtils.rotateQ(-180.0F, 1.0F, 0.0F, 0.0F, matrix);
   }
}
