package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.entity.MahouLightningBoltEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderMahouBolt extends EntityRenderer<MahouLightningBoltEntity, DummyEntityRenderState> {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");

   public RenderMahouBolt(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderBolt(MahouLightningBoltEntity entityIn, float partialTicks, PoseStack matrix, SubmitNodeCollector buf) {
      float[] lens = new float[11];
      float[] pits = new float[11];
      float[] yaws = new float[11];
      Random random = new Random(entityIn.boltVertex);
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      float a = 1.0F;
      float r2 = 0.2F;
      float g2 = 0.5F;
      float b2 = 1.0F;
      float a2 = 0.2F;

      for (int i = 0; i < lens.length; i++) {
         lens[i] = random.nextInt(6) + 5;
         pits[i] = random.nextInt(60) - 60;
         yaws[i] = random.nextInt(360);
      }

      float w = 0.05F;
      matrix.pushPose();
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
      matrix.pushPose();

      for (int i = 0; i < lens.length; i++) {
         RenderUtils.rotateQ(yaws[i], 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pits[i], 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w, lens[i], 20, 240, 240, r, g, b, a, 0, beam);
         matrix.translate(0.0F, -lens[i], 0.0F);
         RenderUtils.rotateQ(-pits[i], 1.0F, 0.0F, 0.0F, matrix);
      }

      matrix.popPose();
      matrix.pushPose();

      for (int i = 0; i < lens.length; i++) {
         RenderUtils.rotateQ(yaws[i], 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pits[i], 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w * 2.8F, lens[i], 20, 240, 240, r2, g2, b2, a2, 0, beam);
         matrix.translate(0.0F, -lens[i], 0.0F);
         RenderUtils.rotateQ(-pits[i], 1.0F, 0.0F, 0.0F, matrix);
      }

      matrix.popPose();
      matrix.popPose();
   }

   public Identifier getTextureLocation(MahouLightningBoltEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public boolean shouldRender(MahouLightningBoltEntity livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
      return true;
   }
}
