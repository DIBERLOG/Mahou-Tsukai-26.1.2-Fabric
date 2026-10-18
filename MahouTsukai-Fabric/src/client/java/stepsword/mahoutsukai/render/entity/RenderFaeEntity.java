package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.entity.fae.FaeEntity;

import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.shader.ModShaders;

/** Original fae core/glow geometry, adapted to native extraction and submission. */
public class RenderFaeEntity extends EntityRenderer<FaeEntity, FaeRenderState> {
   public static Identifier texture = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/fae.png");
   public static RenderType type = MahoujinRenderType.createCachedRenderType(
      "fae_shader_core",
      MahoujinRenderType.defaultTypeSettings(ModShaders.FAE_CORE_PIPELINE, false, true).withTexture("Sampler0", texture).useLightmap().createRenderSetup()
   );
   public static RenderType glowType = MahoujinRenderType.createCachedRenderType(
      "fae_shader_glow", MahoujinRenderType.defaultTypeSettings(ModShaders.FAE_GLOW_PIPELINE, false, true).withTexture("Sampler0", texture).createRenderSetup()
   );


 public RenderFaeEntity(Context context) { super(context); }
 public FaeRenderState createRenderState() { return new FaeRenderState(); }
 public void extractRenderState(FaeEntity entity, FaeRenderState state, float partialTicks) {
  super.extractRenderState(entity, state, partialTicks);
  var rgb = entity.getColor();
  state.red = Math.min(1.0F, rgb[0] / 255.0F * 1.25F + 0.2F);
  state.green = Math.min(1.0F, rgb[1] / 255.0F * 1.25F + 0.2F);
  state.blue = Math.min(1.0F, rgb[2] / 255.0F * 1.25F + 0.2F);
  var cameraEntity = Minecraft.getInstance().getCameraEntity();
  state.visible = cameraEntity instanceof net.minecraft.world.entity.LivingEntity living
   && living.hasEffect(stepsword.mahoutsukai.potion.ModEffects.FAY_SIGHT_EYES)
   && entity.distanceToSqr(cameraEntity) < 3600;
 }
 public void submit(FaeRenderState state, PoseStack matrix, SubmitNodeCollector collector, CameraRenderState camera) {
  super.submit(state, matrix, collector, camera);
  if (!state.visible) return;
  matrix.pushPose();
  matrix.translate(0, 0.5F, 0);
  matrix.mulPose(camera.orientation);
  matrix.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
  collector.submitCustomGeometry(matrix, glowType, (pose, buffer) ->
   addQuad(buffer, pose.pose(), 0.9F, state.red, state.green, state.blue, 0.8F));
  collector.submitCustomGeometry(matrix, type, (pose, buffer) ->
   addQuad(buffer, pose.pose(), 0.5F, state.red, state.green, state.blue, 1));
  matrix.popPose();
 }
   private static void addQuad(VertexConsumer consumer, Matrix4f pose, float scale, float red, float green, float blue, float alpha) {
      float extent = 0.5F * scale;
      float min = -extent;
      int fullBrightness = 240;
      consumer.addVertex(pose, min, 0.0F, min).setColor(red, green, blue, alpha).setUv(0.0F, 0.0F).setUv2(fullBrightness, fullBrightness);
      consumer.addVertex(pose, min, 0.0F, extent).setColor(red, green, blue, alpha).setUv(0.0F, 1.0F).setUv2(fullBrightness, fullBrightness);
      consumer.addVertex(pose, extent, 0.0F, extent).setColor(red, green, blue, alpha).setUv(1.0F, 1.0F).setUv2(fullBrightness, fullBrightness);
      consumer.addVertex(pose, extent, 0.0F, min).setColor(red, green, blue, alpha).setUv(1.0F, 0.0F).setUv2(fullBrightness, fullBrightness);
   }
}
