package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.SpiralGeometry;

public class RenderRhongomyniadEntity extends EntityRenderer<RhongomyniadEntity, RenderRhongomyniadEntity.RhongomyniadRenderState> {
   private static final String WORLD_RUNES_SCOPE = "rhongomyniad_entity_runes";

   public RenderRhongomyniadEntity(Context renderManager) {
      super(renderManager);
   }

   public RenderRhongomyniadEntity.RhongomyniadRenderState createRenderState() {
      return new RenderRhongomyniadEntity.RhongomyniadRenderState();
   }

   public void extractRenderState(RhongomyniadEntity entity, RenderRhongomyniadEntity.RhongomyniadRenderState state, float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.rhongomyniad.key);
   }

   public void submit(
      RenderRhongomyniadEntity.RhongomyniadRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera
   ) {
      poseStack.pushPose();
      poseStack.scale(3.0F, 3.0F, 3.0F);
      RenderUtils.rotateQ(45.0F, 0.0F, 0.0F, 1.0F, poseStack);
      poseStack.translate(0.0F, 0.1875F, 0.0F);
      RenderUtils.rotateQ(-45.0F, 0.0F, 0.0F, 1.0F, poseStack);
      poseStack.scale(0.4F, 0.4F, 0.4F);
      RenderBaseItem.render(state.model, poseStack, submitNodeCollector, null, true);
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderRhongomyniad(RhongomyniadEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      float yaw = entity.yy % 360.0F;
      float roll = entity.getRotationRoll();
      float pitch = entity.getRotationPitch();
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      float prevYaw = entity.py % 360.0F;
      if (prevYaw < 0.0F) {
         prevYaw += 360.0F;
      }

      if (prevYaw - yaw > 300.0F) {
         yaw += 360.0F;
      }

      if (prevYaw - yaw < 300.0F && prevYaw - yaw > 0.0F) {
         prevYaw = yaw;
      }

      yaw = entity.py + partialTicks * (yaw - prevYaw);
      matrix.pushPose();
      matrix.scale(3.0F, 3.0F, 3.0F);
      RenderUtils.rotateQ(45.0F, 0.0F, 0.0F, 1.0F, matrix);

      matrix.popPose();
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      float ring = 90.0F;
      float size = 60.0F;
      RenderType runes1 = MahoujinRenderType.createScopedRunesRenderType("rhongomyniad_entity_runes", RenderUtils.runes, 0);
      float h = 2.5F;
      float radius = size / 3.0F;
      float[] colors = new float[]{0.7490196F, 0.94509804F, 0.9843137F, 1.0F};
      MahouGeometry spiral1 = new SpiralGeometry(ring - 20.0F, radius, h).setColor(colors).setType(runes1);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      float[] var10000 = new float[]{1.0F, 0.9607843F, 0.60784316F, 1.0F};
      float mult = 1.1F;
      float newyaw = mult * entity.py + partialTicks * (mult * entity.yy - mult * entity.py);
      RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(-newyaw, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(-60.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(-60.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(-60.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(-60.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      RenderUtils.rotateQ(-60.0F, 0.0F, 1.0F, 0.0F, matrix);
      spiral1.render(matrix, buffer);

      matrix.popPose();
   }

   public static class RhongomyniadRenderState extends DummyEntityRenderState {
      public QuadCollection model;
   }
}
