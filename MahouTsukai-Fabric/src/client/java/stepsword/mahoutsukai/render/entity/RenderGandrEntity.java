package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.entity.mahoujin.GandrEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.Utils;

/**
 * One coherent curse form: a luminous core inside a soft halo, with sparks spiralling inward while
 * it charges. The earlier vertical rune ring and rotating ribbon arcs read as an odd red oval, so
 * they are gone; the shape stays small enough that the first-person camera is never covered.
 */
public class RenderGandrEntity extends EntityRenderer<GandrEntity, DummyEntityRenderState> {
   public RenderGandrEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderGandr(GandrEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buf) {
      float maxsize = 0.1F;
      float yaw = entity.getYRot();
      float pitch = entity.getXRot();
      if (!entity.getFired() && entity.getCasterUUID() != null) {
         LivingEntity caster = entity.level().getPlayerByUUID(entity.getCasterUUID());
         if (caster != null) {
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().position();
            Vec3 look = caster.getViewVector(partialTicks).normalize().scale(1.1F);
            Vec3 start = caster.getEyePosition(partialTicks).add(look);
            x = start.x - camera.x;
            y = start.y - camera.y;
            z = start.z - camera.z;
            yaw = Utils.yawPartial(caster.yRotO, caster.getYRot(), partialTicks);
            pitch = caster.xRotO + (caster.getXRot() - caster.xRotO) * partialTicks;
         }
      }

      float[] colors = entity.getColors();
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float r2 = colors[4];
      float g2 = colors[5];
      float b2 = colors[6];
      // A fully black secondary colour previously hid the entire hot core behind an opaque shell.
      if(r2+g2+b2<.08F){r2=Math.max(.55F,r);g2=Math.max(.08F,g);b2=Math.max(.04F,b);}
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      float liferatio = maxsize * entity.getLife() / entity.getGrowingLife();
      liferatio = Math.min(maxsize, liferatio);
      RenderType sphere1 = MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 0);
      RenderType sphere2 = MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 1);

      matrix.scale(1.6F, 1.6F, 1.6F);
      boolean fired = entity.getFired();
      if (fired) {
         float firedProgress = Math.min(1.0F, (float)entity.ticksSinceFired / entity.firedProgressLife);
         matrix.scale(1.0F - 0.2F * firedProgress, 1.0F - 0.2F * firedProgress, 1.0F + 0.4F * firedProgress);
      }

      // Halo, core and inner hot spot share one centre, so the shape reads as a single charged orb.
      RenderUtils.renderSphere(matrix, buf, liferatio * 1.35F, 32, 240, 240, r, g, b, 0.10F, sphere2);
      RenderUtils.renderSphere(matrix, buf, liferatio * 0.9F, 32, 240, 240, r2, g2, b2, 1.0F, sphere1);
      RenderUtils.renderSphere(matrix, buf, liferatio * 0.42F, 32, 240, 240,
         Math.min(1.0F, r2 + 0.35F), Math.min(1.0F, g2 + 0.35F), Math.min(1.0F, b2 + 0.35F), 0.85F, sphere1);

      long time = ClientHandler.clientTickCounter;
      if (!fired) {
         // Charge sparks tighten towards the core as the cast completes.
         float charge = Math.min(1.0F, entity.getLife() / (float)Math.max(1, entity.getGrowingLife()));
         float orbit = liferatio * (2.1F - 1.15F * charge);
         for (int i = 0; i < 8; i++) {
            double angle = time * 0.11 + i * Math.PI / 4.0;
            float wobble = (float)(0.10 * Math.sin(time * 0.17 + i * 1.7));
            matrix.pushPose();
            matrix.translate(
               (float)(Math.cos(angle) * orbit),
               (float)(Math.sin(angle * 1.4) * orbit * 0.4) + wobble * orbit,
               (float)(Math.sin(angle) * orbit)
            );
            RenderUtils.renderSphere(matrix, buf, liferatio * (0.10F + 0.05F * charge), 20, 240, 240, r2, g2, b2, 0.85F, sphere1);
            matrix.popPose();
         }
      }

      matrix.popPose();
   }
}
