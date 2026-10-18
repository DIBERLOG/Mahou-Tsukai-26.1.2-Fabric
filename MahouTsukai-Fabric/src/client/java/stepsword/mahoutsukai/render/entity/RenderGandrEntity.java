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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.entity.mahoujin.GandrEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

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
      float a = colors[3];
      float r2 = colors[4];
      float g2 = colors[5];
      float b2 = colors[6];
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      float liferatio = maxsize * entity.getLife() / entity.getGrowingLife();
      float liferatioinv = maxsize;
      liferatio = Math.min(maxsize, liferatio);
      liferatioinv = Math.max(0.0F, liferatioinv);
      RenderType sphere1 = MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 0);
      RenderType sphere2 = MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 1);

      matrix.scale(1.6F, 1.6F, 1.6F);
      if (entity.getFired()) {
         float firedProgress = (float)entity.ticksSinceFired / entity.firedProgressLife;
         firedProgress = Math.min(1.0F, firedProgress);
         matrix.scale(1.0F - 0.2F * firedProgress, 1.0F - 0.2F * firedProgress, 1.0F + 0.4F * firedProgress);
      } else {
         double xshake;
         double yshake;
         float ringrot;
         if (entity.getLife() < entity.getGrowingLife()) {
            ringrot = entity.getLife();
            xshake = 0.0;
            yshake = 0.0;
         } else {
            xshake = Math.random() * 0.003F - 0.0015F;
            yshake = Math.random() * 0.003F - 0.0015F;
            ringrot = entity.getGrowingLife();
         }

         matrix.pushPose();
         matrix.translate(xshake, yshake, -0.15F);
         RenderType ring1 = MahoujinRenderType.createScopedRunesRenderType("gandr_ring", RenderUtils.runes, 7);
         RenderUtils.rotateQ(90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(ringrot, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.renderRing(matrix, buf, 0.0, 0.0F, 0.2F - 0.001F * ringrot, 0.05F, 64, 240, 240, r, g, b, 1.0F, ring1);
         matrix.popPose();
      }

      RenderUtils.renderSphere(matrix, buf, liferatio * 0.9F, 32, 240, 240, r2, g2, b2, 1.0F, sphere1);
      RenderUtils.renderSphere(matrix, buf, liferatio * 1.1F, 32, 240, 240, r, g, b, 0.14F, sphere2);
      long time = ClientHandler.clientTickCounter;
      float repeat = 200.0F;
      float progress = (float)time % repeat / repeat;
      if (entity.inited) {
         float[] progressStarts = entity.progressstarts;
         float[] progressEnds = entity.progressends;

         for (int j = 0; j < entity.swirls; j++) {
            if (progressInRange(progress, progressStarts[j], progressEnds[j])) {
               RenderType arc1 = MahoujinRenderType.createScopedRunesRenderType("gandr_arc_inner", RenderUtils.beam, 2);
               RenderType arc2 = MahoujinRenderType.createScopedRunesRenderType("gandr_arc_outer", RenderUtils.beam, 3);
               float thisProgress;
               if (progressStarts[j] < progressEnds[j]) {
                  thisProgress = (progress - progressStarts[j]) / (progressEnds[j] - progressStarts[j]);
               } else {
                  float range = progressEnds[j] + 1.0F - progressStarts[j];
                  if (progress < progressEnds[j]) {
                     thisProgress = (progress + 1.0F - progressStarts[j]) / range;
                  } else {
                     thisProgress = (progress - progressStarts[j]) / range;
                  }
               }

               float progressFront = thisProgress + 0.7F;
               float progressBack = thisProgress;
               float angle = entity.angles[j];
               matrix.pushPose();
               if (!entity.getFired()) {
                  RenderUtils.rotateQ(entity.angles[j], 0.0F, 1.0F, 0.0F, matrix);
                  RenderUtils.rotateQ(entity.angles[(j + 1) % entity.swirls], 1.0F, 0.0F, 0.0F, matrix);
                  renderPartialArcSpiral(
                     matrix,
                     buf,
                     liferatioinv * 0.2F,
                     90.0F,
                     liferatioinv * 1.8F,
                     liferatioinv * 0.5F,
                     64,
                     240,
                     240,
                     r,
                     g,
                     b,
                     0.7F,
                     arc1,
                     progressFront,
                     progressBack,
                     liferatio * 0.9F
                  );
               } else {
                  progressFront = 1.0F - thisProgress * 0.55F;
                  progressBack = 1.0F - thisProgress * 0.55F - 0.1F;
                  RenderUtils.rotateQ(-90.0F, 0.0F, 1.0F, 0.0F, matrix);
                  RenderUtils.rotateQ(entity.angles[j], 1.0F, 0.0F, 0.0F, matrix);
                  renderPartialArcSpiral(
                     matrix,
                     buf,
                     liferatioinv * 0.2F,
                     90.0F,
                     liferatioinv * 1.8F,
                     liferatioinv * 0.5F,
                     64,
                     240,
                     240,
                     r,
                     g,
                     b,
                     0.5F,
                     arc1,
                     progressFront,
                     progressBack,
                     liferatio * 0.9F
                  );
                  RenderUtils.rotateQ(entity.angles[(j + 1) % entity.swirls], 1.0F, 0.0F, 0.0F, matrix);
                  renderPartialArcSpiral(
                     matrix,
                     buf,
                     liferatioinv * 0.2F,
                     90.0F,
                     liferatioinv * 1.8F,
                     liferatioinv * 0.5F,
                     64,
                     240,
                     240,
                     r,
                     g,
                     b,
                     0.5F,
                     arc2,
                     progressFront,
                     progressBack,
                     liferatio * 0.9F
                  );
               }

               matrix.popPose();
            }
         }
      }

      matrix.popPose();
   }

   public static boolean progressInRange(float p, float s, float e) {
      return s < e ? p > s && p < e : p > s || p < e;
   }

   public static RenderType renderPartialArcSpiral(
      PoseStack matrix,
      SubmitNodeCollector buffer,
      double cy,
      float flatness,
      float radius,
      float height,
      int num_segments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType type,
      float pf,
      float pb,
      float innerradius
   ) {
      matrix.pushPose();
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      Matrix4f positionMatrix = pose.pose();
      double theta = 6.2831852 / num_segments;
      double q = height * Math.sin(EffectUtil.toRad(flatness));
      double h2 = height;
      double r2 = radius;
      double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
      double offset = 0.0;
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      double xb = 0.0;
      double yb = 0.0;
      double zb = 0.0;
      float squeeze = 3.0F;
      float texx = 0.0F;

      int firstIndex=(int)(num_segments*pb);
      for (int i = firstIndex; i < Math.min(num_segments * pf + 1.0F, num_segments + 1); i++) {
         r2 = innerradius + (radius - innerradius) * ((float)(num_segments - i) / num_segments);
         h2 = height * ((float)(num_segments - i) / num_segments);
         p = r2 + h2 * Math.cos(EffectUtil.toRad(flatness));
         q = h2 * Math.sin(EffectUtil.toRad(flatness));
         offset = (height - q) / 2.0;
         texx += squeeze * 1.0F / (num_segments + 1);
         if (texx >= 1.0F) {
            texx = squeeze * 1.0F / (num_segments + 1);
            bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         }

         double tt = i * theta;
         x = -r2 * Math.sin(tt);
         y = cy - offset;
         z = r2 * Math.cos(tt);
         xb = -p * Math.sin(tt);
         yb = cy - q - offset;
         zb = p * Math.cos(tt);
         if(i==firstIndex)bb.addVertex(positionMatrix,(float)x,(float)y,(float)z).setColor(r,g,b,a).setUv(texx,0).setUv2(lx,ly);
         bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly);
         bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly);
      }

      bb.addVertex(positionMatrix,(float)xb,(float)yb,(float)zb).setColor(r,g,b,a).setUv(texx,1).setUv2(lx,ly);
      });
      matrix.popPose();
      return type;
   }
}
