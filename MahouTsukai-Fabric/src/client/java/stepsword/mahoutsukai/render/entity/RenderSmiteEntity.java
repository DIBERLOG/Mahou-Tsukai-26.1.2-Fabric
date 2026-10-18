package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.entity.mahoujin.SmiteEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderSmiteEntity extends EntityRenderer<SmiteEntity, DummyEntityRenderState> {
   private static final Identifier BEAM_TEXTURE = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");

   public RenderSmiteEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderSmite(SmiteEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      float beamLength = entity.prev_beam_len + (entity.client_beam_len - entity.prev_beam_len) * partialTicks;
      float beamSize = entity.prev_beam_size + (entity.client_beam_size - entity.prev_beam_size) * partialTicks;
      if (!(beamLength <= 0.0F) && !(beamSize <= 0.0F)) {
         matrix.pushPose();
         matrix.translate(x, y, z);
         float[] colors = entity.getColors();
         int lightmapX = 240;
         int lightmapY = 240;
         RenderType beam = (MahoujinRenderType.createScopedRunesRenderType("smite_beam", BEAM_TEXTURE, 0));
         float beamTop = entity.isCollapsing() ? beamLength : entity.maxlen;
         float innerRadius = Math.max(0.02F, beamSize - 0.12F);
         RenderUtils.renderRing(matrix, buffer, beamTop, 90.0F, innerRadius, beamLength, 48, lightmapX, lightmapY, 1.0F, 1.0F, 1.0F, 1.0F, beam);
         float time = entity.getLife() + partialTicks;
         float outerWaver = 1.0F + 0.25F * (float)Math.sin(time * 0.9F + entity.getId()) + 0.15F * (float)Math.sin(time * 2.17F + entity.getId() * 0.37F);
         float outerRadius = Math.max(beamSize * outerWaver, innerRadius + 0.08F);
         RenderUtils.renderRing(matrix, buffer, beamTop, 90.0F, outerRadius, beamLength, 48, lightmapX, lightmapY, colors[0], colors[1], colors[2], 0.4F, beam);
         matrix.popPose();
      }
   }
}
