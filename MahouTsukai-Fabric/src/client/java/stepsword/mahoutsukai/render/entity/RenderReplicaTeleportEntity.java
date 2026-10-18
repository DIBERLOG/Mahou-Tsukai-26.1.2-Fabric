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
import stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;

public class RenderReplicaTeleportEntity extends EntityRenderer<ReplicaTeleportMahoujinEntity, DummyEntityRenderState> {
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final String BUILTIN_TEXT_SCOPE = "replica_teleport_mahoujin_builtin_text";

   public RenderReplicaTeleportEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderReplica(ReplicaTeleportMahoujinEntity entity, double x, double y, double z, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      float[] rgba = entity.getColors();
      float size = entity.getCircleSize() * 2.0F;
      float r = rgba[0];
      float g = rgba[1];
      float b = rgba[2];
      float a = rgba[3];
      int lightmapX = 240;
      int lightmapY = 240;
      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("replica_teleport_mahoujin_builtin_text", mahoujin, 0, false);
      matrix.scale(size, size, size);
      buffer.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
      bb.addVertex(nativePose.pose(), 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      matrix.popPose();
   }
}
