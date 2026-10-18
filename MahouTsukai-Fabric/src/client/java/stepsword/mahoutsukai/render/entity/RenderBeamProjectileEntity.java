package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.mahoujin.BeamProjectileEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderBeamProjectileEntity extends EntityRenderer<BeamProjectileEntity, DummyEntityRenderState> {
   static RenderType sphere1 = (MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 0));
   static RenderType sphere2 = (MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 1));
   static RenderType sphere3 = (MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 2));
   static RenderType sphere4 = (MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 3));
   static RenderType beam1 = (MahoujinRenderType.createBeamRenderType(RenderUtils.beam, 4));
   static RenderType beam2 = (MahoujinRenderType.createBeamRenderType(RenderUtils.beam, 5));

   public RenderBeamProjectileEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderBeamProjectile(BeamProjectileEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors[3];
      float r2 = colors[4];
      float g2 = colors[5];
      float b2 = colors[6];
      float a2 = colors[7];
      float radius = entity.getRadius();
      float length = entity.getLength();
      matrix.pushPose();
      matrix.translate(x, y, z);
      Vec3 velocity = entity.getDeltaMovement();
      if (velocity.lengthSqr() > 1.0E-7) {
         double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
         float velocityYaw = 180.0F + (float)Math.toDegrees(Math.atan2(velocity.x, velocity.z));
         float velocityPitch = 90.0F + (float)Math.toDegrees(Math.atan2(velocity.y, horizontalSpeed));
         RenderUtils.rotateQ(velocityYaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(velocityPitch, 1.0F, 0.0F, 0.0F, matrix);
      } else {
         RenderUtils.rotateQ(entity.getRotationYaw(), 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(entity.getRotationPitch(), 1.0F, 0.0F, 0.0F, matrix);
      }
      float buffer = 0.02F;
      float factor = Math.max(4.0F, 4.0F * length * length);
      matrix.scale(1.0F, factor, 1.0F);
      RenderUtils.renderSphere(matrix, buf, radius - buffer, 32, 240, 240, r2, g2, b2, a2, sphere1, 0.52F);
      matrix.translate(0.0F, -length / factor, 0.0F);
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.renderSphere(matrix, buf, radius - buffer, 32, 240, 240, r2, g2, b2, a2, sphere3, 0.52F);
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
      matrix.translate(0.0F, length / factor, 0.0F);
      matrix.scale(1.0F, 1.0F / factor, 1.0F);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, Math.max(0.0F, radius - buffer), length, 64, 240, 240, r2, g2, b2, a2, beam1);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, radius, length, 64, 240, 240, r, g, b, a, beam2);
      matrix.scale(1.0F, factor, 1.0F);
      RenderUtils.renderSphere(matrix, buf, radius, 32, 240, 240, r, g, b, a, sphere2, 0.52F);
      matrix.translate(0.0F, -length / factor, 0.0F);
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.renderSphere(matrix, buf, radius, 32, 240, 240, r, g, b, a, sphere4, 0.52F);
      matrix.popPose();
   }
}
