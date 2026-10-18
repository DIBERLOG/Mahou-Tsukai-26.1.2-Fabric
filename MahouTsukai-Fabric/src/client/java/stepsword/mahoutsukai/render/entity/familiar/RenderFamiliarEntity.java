package stepsword.mahoutsukai.render.entity.familiar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.render.MahoujinRenderType;

public class RenderFamiliarEntity extends MobRenderer<FamiliarEntity, FamiliarRenderState, ModelFamiliar> {
   static Identifier t1 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/item/familiar-texture1.png");
   static Identifier t2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/item/familiar-texture2.png");
   private static final RenderType FAMILIAR_BODY = MahoujinRenderType.createMahoujinRenderType(t1, 0, false);
   private static final RenderType FAMILIAR_FACE = MahoujinRenderType.createMahoujinRenderType(t2, 0, false);

   public RenderFamiliarEntity(Context renderManagerIn) {
      super(renderManagerIn, new ModelFamiliar(), 0.3F);
   }

   public Identifier getTextureLocation(FamiliarRenderState state) {
      return t1;
   }

   public Identifier getTextureLocation(FamiliarEntity entity) {
      return t2;
   }

   public FamiliarRenderState createRenderState() {
      return new FamiliarRenderState();
   }

   public void extractRenderState(FamiliarEntity entity, FamiliarRenderState state, float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      float flap = Mth.lerp(partialTicks, entity.oFlap, entity.flap);
      float flapSpeed = Mth.lerp(partialTicks, entity.oFlapSpeed, entity.flapSpeed);
      state.flapAngle = (Mth.sin(flap) + 1.0F) * flapSpeed;
      state.familiarPose = entity.isFlying() ? ModelFamiliar.State.FLYING : ModelFamiliar.State.STANDING;
      float[] rgb = entity.getColor();
      state.color = ARGB.colorFromFloat(1.0F, rgb[0], rgb[1], rgb[2]);
   }

   protected int getModelTint(FamiliarRenderState state) {
      return state.color;
   }

   protected @Nullable RenderType getRenderType(FamiliarRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
      return null;
   }

   public void submit(FamiliarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      {
         if (!state.isInvisible) {
            poseStack.pushPose();
            if (state.hasPose(Pose.SLEEPING)) {
               Direction bedOrientation = state.bedOrientation;
               if (bedOrientation != null) {
                  float headOffset = state.eyeHeight - 0.1F;
                  poseStack.translate(-bedOrientation.getStepX() * headOffset, 0.0F, -bedOrientation.getStepZ() * headOffset);
               }
            }

            float scale = state.scale;
            poseStack.scale(scale, scale, scale);
            this.setupRotations(state, poseStack, state.bodyRot, scale);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(state, poseStack);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            ((ModelFamiliar)this.model).setupAnim(state);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            int color = state.color;
            submitNodeCollector.submitCustomGeometry(
               poseStack,
               FAMILIAR_BODY,
               (pose, buffer) -> {this.model.setupAnim(state);renderParts(this.model.root(), poseStackFrom(pose), buffer, state.lightCoords, overlayCoords, color, false);}
            );
            submitNodeCollector.submitCustomGeometry(
               poseStack,
               FAMILIAR_FACE,
               (pose, buffer) -> {this.model.setupAnim(state);renderParts(this.model.root(), poseStackFrom(pose), buffer, state.lightCoords, overlayCoords, -1, true);}
            );
            poseStack.popPose();
         }

         super.submit(state, poseStack, submitNodeCollector, camera);
      }
   }

   private static PoseStack poseStackFrom(com.mojang.blaze3d.vertex.PoseStack.Pose pose) {
      PoseStack stack = new PoseStack();
      stack.last().set(pose);
      return stack;
   }

   private static void renderParts(ModelPart root, PoseStack poseStack, VertexConsumer buffer, int light, int overlay, int color, boolean faceOnly) {
      root.visit(poseStack, (pose, path, cubeIndex, cube) -> {
         if (faceOnly) {
            renderExpandedCubeWithFullTexture(pose, buffer, cube, light, overlay, color, 0.02F);
         } else {
            renderCubeWithFullTexture(pose, buffer, cube, light, overlay, color);
         }
      });
   }

   private static void renderCubeWithFullTexture(
      com.mojang.blaze3d.vertex.PoseStack.Pose pose, VertexConsumer buffer, Cube cube, int light, int overlay, int color
   ) {
      Matrix4f matrix = pose.pose();
      Vector3f scratch = new Vector3f();

      for (Polygon polygon : cube.polygons) {
         Vector3f normal = pose.transformNormal(polygon.normal(), scratch);
         float nx = normal.x();
         float ny = normal.y();
         float nz = normal.z();
         Vertex[] vertices = polygon.vertices();
         addVertex(matrix, buffer, vertices[0], color, 0.0F, 0.0F, overlay, light, nx, ny, nz, scratch);
         addVertex(matrix, buffer, vertices[1], color, 0.0F, 1.0F, overlay, light, nx, ny, nz, scratch);
         addVertex(matrix, buffer, vertices[2], color, 1.0F, 1.0F, overlay, light, nx, ny, nz, scratch);
         addVertex(matrix, buffer, vertices[3], color, 1.0F, 0.0F, overlay, light, nx, ny, nz, scratch);
      }
   }

   private static void addVertex(
      Matrix4f matrix,
      VertexConsumer buffer,
      Vertex vertex,
      int color,
      float u,
      float v,
      int overlay,
      int light,
      float nx,
      float ny,
      float nz,
      Vector3f scratch
   ) {
      Vector3f pos = matrix.transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), scratch);
      buffer.addVertex(pos.x(), pos.y(), pos.z(), color, u, v, overlay, light, nx, ny, nz);
   }

   private static void renderExpandedCubeWithFullTexture(
      com.mojang.blaze3d.vertex.PoseStack.Pose pose, VertexConsumer buffer, Cube cube, int light, int overlay, int color, float expand
   ) {
      float minX = (cube.minX - expand) / 16.0F;
      float minY = (cube.minY - expand) / 16.0F;
      float minZ = (cube.minZ - expand) / 16.0F;
      float maxX = (cube.maxX + expand) / 16.0F;
      float maxY = (cube.maxY + expand) / 16.0F;
      float maxZ = (cube.maxZ + expand) / 16.0F;
      emitQuad(pose, buffer, color, overlay, light, 1.0F, 0.0F, 0.0F, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ);
      emitQuad(pose, buffer, color, overlay, light, -1.0F, 0.0F, 0.0F, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ);
      emitQuad(pose, buffer, color, overlay, light, 0.0F, 1.0F, 0.0F, maxX, maxY, minZ, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ);
      emitQuad(pose, buffer, color, overlay, light, 0.0F, -1.0F, 0.0F, maxX, minY, maxZ, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ);
      emitQuad(pose, buffer, color, overlay, light, 0.0F, 0.0F, 1.0F, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ);
      emitQuad(pose, buffer, color, overlay, light, 0.0F, 0.0F, -1.0F, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ);
   }

   private static void emitQuad(
      com.mojang.blaze3d.vertex.PoseStack.Pose pose,
      VertexConsumer buffer,
      int color,
      int overlay,
      int light,
      float nx,
      float ny,
      float nz,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3
   ) {
      Vector3f normal = pose.transformNormal(nx, ny, nz, new Vector3f());
      Matrix4f matrix = pose.pose();
      Vector3f scratch = new Vector3f();
      addVertex(matrix, buffer, x0, y0, z0, color, 0.0F, 0.0F, overlay, light, normal, scratch);
      addVertex(matrix, buffer, x1, y1, z1, color, 0.0F, 1.0F, overlay, light, normal, scratch);
      addVertex(matrix, buffer, x2, y2, z2, color, 1.0F, 1.0F, overlay, light, normal, scratch);
      addVertex(matrix, buffer, x3, y3, z3, color, 1.0F, 0.0F, overlay, light, normal, scratch);
   }

   private static void addVertex(
      Matrix4f matrix, VertexConsumer buffer, float x, float y, float z, int color, float u, float v, int overlay, int light, Vector3f normal, Vector3f scratch
   ) {
      Vector3f pos = matrix.transformPosition(x, y, z, scratch);
      buffer.addVertex(pos.x(), pos.y(), pos.z(), color, u, v, overlay, light, normal.x(), normal.y(), normal.z());
   }
}
