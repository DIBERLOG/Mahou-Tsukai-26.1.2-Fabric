package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.item.replica.Replica;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderBufferWrapper;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.entity.RenderGateChainEntity;
import stepsword.mahoutsukai.util.EffectUtil;

public class ReplicaRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final Identifier gem = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final String CIRCLE_SCOPE = "replica_renderer_1";
   private static final String ARC_SCOPE = "replica_renderer_2";
   private final RenderBaseItem.ModelKeyPair model;

   public ReplicaRenderer() {
      this(RenderBaseItem.replica);
   }

   public ReplicaRenderer(RenderBaseItem.ModelKeyPair model) {
      this.model = model;
   }

   public ReplicaRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      this();
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Replica;
      state.time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(this.model.key);
      state.stack = stack;
      return state;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack matrix,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         RenderBufferWrapper buffer = new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitNodeCollector);
         matrix.pushPose();
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         float r = 0.93F;
         float g = 0.51F;
         float b = 0.93F;
         float a = 0.8F;
         float r2 = 1.0F;
         float g2 = 0.58F;
         float b2 = 1.0F;
         float a2 = 0.4F;
         int circles = 0;

         for (int i = 0; i < 6; i++) {
            String s = Replica.getDamageTypeForSlot(state.stack, i);
            if (!s.equals("EMPTY")) {
               circles++;
            }
         }

         float theta = 360.0F / circles;
         RenderType type = MahoujinRenderType.createScopedMahoujinRenderType("replica_renderer_1", RenderUtils.mahoujin, 6, false);
         float circleR = 1.0F;
         float circleG = 0.0F;
         matrix.pushPose();
         matrix.scale(0.42038885F, 0.3509252F, 0.71608615F);
         matrix.translate(1.275, 0.13, 0.85);
         matrix.scale(0.2F, 0.2F, 0.2F);
         RenderUtils.rotateQ(-90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         float degreesForCircle = 38.0F;

         for (int i = 0; i < circles; i++) {
            matrix.pushPose();
            RenderUtils.rotateQ(theta * i + (float)state.time, 0.0F, 1.0F, 0.0F, matrix);
            matrix.translate(1.9, 0.0, 0.0);
            float circleB = (float)((Math.sin(Math.toRadians((theta * i + (float)state.time) % 360.0F)) + 1.0) / 2.0);
            renderCircle(buffer, type, matrix, circleR, circleG, circleB, a, 130, 130);
            matrix.popPose();
            matrix.pushPose();
            RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, matrix);
            RenderUtils.rotateQ(-theta * i - (float)state.time - 288.0F, 0.0F, 1.0F, 0.0F, matrix);
            if (circles == 3 || circles == 6) {
               RenderUtils.rotateQ(2.0F, 0.0F, 1.0F, 0.0F, matrix);
            }

            RenderType colorRing = MahoujinRenderType.createScopedRunesRenderType("replica_renderer_2", RenderUtils.runes, 0);
            float degrees = theta - degreesForCircle;
            renderColorChangeARC(
               matrix, buffer, 0.01, 0.0F, 1.6F, 0.2F, 40, 130, 130, 1.0F, 0.0F, circleB, 1.0F, colorRing, degrees, theta * i + (float)state.time
            );
            matrix.popPose();
         }

         matrix.popPose();
         matrix.pushPose();
         matrix.translate(0.535, 0.045F, 0.58);
         RenderUtils.rotateQ(-90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         matrix.scale(0.45F, 0.94F, 0.4F);
         renderPyramid(matrix, buffer, r, g, b, a, r2, g2, b2, a2, 0, 0.35F);
         matrix.popPose();
         r = 0.93F;
         g = 0.06F;
         b = 0.06F;
         a = 0.8F;
         r2 = 1.0F;
         g2 = 0.13F;
         b2 = 0.13F;
         a2 = 0.4F;
         matrix.pushPose();
         matrix.translate(0.535, 0.045F, 0.58);
         RenderUtils.rotateQ(-90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         matrix.scale(0.56F, 1.0F, 0.52F);
         renderPyramid(matrix, buffer, r, g, b, a2, r2, g2, b2, a2, 1, 0.4F);
         matrix.popPose();
         matrix.popPose();
      }
   }

   public static void renderPyramid(
      PoseStack stack, RenderBufferWrapper buffer, float r, float g, float b, float a, float r2, float g2, float b2, float a2, int z, float height
   ) {
      RenderUtils.rotateQ(180.0F, 1.0F, 0.0F, 0.0F, stack);
      RenderGateChainEntity.scaleM(stack, 0.08F, 0.15F, 0.08F, false);
      RenderType type = MahoujinRenderType.createTriangleRenderType(gem, z);
      int lx = 240;
      int ly = 240;
      buffer.submit(stack, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, 0.0F, height, 0.0F).setColor(r2, g2, b2, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, 1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
         bb.addVertex(m, 1.0F, 0.0F, 0.0F).setColor(r2, g2, b2, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, height, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, 1.0F, 0.0F, 0.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, -1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, height, 0.0F).setColor(r2, g2, b2, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, -1.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
         bb.addVertex(m, -1.0F, 0.0F, 0.0F).setColor(r2, g2, b2, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, height, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, -1.0F, 0.0F, 0.0F).setColor(r, g, b, a).setUv(0.5F, 0.5F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, 1.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      });
      RenderType type2 = MahoujinRenderType.createMahoujinRenderType(gem, 0, false);
      buffer.submit(stack, type2, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, 1.0F, 0.0F, 0.0F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, -1.0F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, -1.0F, 0.0F, 0.0F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
         bb.addVertex(m, 0.0F, 0.0F, 1.0F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
      });
      RenderGateChainEntity.scaleM(stack, 0.08F, 0.15F, 0.08F, true);
      RenderUtils.rotateQ(-180.0F, 1.0F, 0.0F, 0.0F, stack);
   }

   public static void renderCircle(
      RenderBufferWrapper buffer, RenderType type, PoseStack matrix, float r, float g, float b, float a, int lightmapX, int lightmapY
   ) {
      buffer.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
   }

   public static RenderType renderColorChangeARC(
      PoseStack matrix,
      RenderBufferWrapper buffer,
      double cy,
      float flatness,
      float radius,
      float height,
      int numSegments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType type,
      float arcfinish,
      float anglestart
   ) {
      matrix.pushPose();
      double theta = 6.2831852 / numSegments;
      double q = height * Math.sin(EffectUtil.toRad(flatness));
      double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
      double stop = EffectUtil.toRad(arcfinish);
      buffer.submit(matrix, type, (pose, bb) -> {
         Matrix4f positionMatrix = pose.pose();
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;
         double xb = 0.0;
         double yb = 0.0;
         double zb = 0.0;
         float texx = 0.0F;
         float squeeze = 3.0F;

         for (int i = 0; i < numSegments + 1; i++) {
            texx += squeeze * 1.0F / (numSegments + 1);
            if (texx >= 1.0F) {
               texx = squeeze * 1.0F / (numSegments + 1);
               bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
               bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
            }

            double tt = i * theta;
            x = -radius * Math.sin(tt);
            y = cy;
            z = radius * Math.cos(tt);
            xb = -p * Math.sin(tt);
            yb = cy - q;
            zb = p * Math.cos(tt);
            float circleB = (float)((Math.sin(Math.toRadians((theta * i + anglestart) % 360.0)) + 1.0) / 2.0);
            bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, circleB, a).setUv(texx, 0.0F).setUv2(lx, ly);
            bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, circleB, a).setUv(texx, 1.0F).setUv2(lx, ly);
            if (tt > stop) {
               break;
            }
         }
      });
      matrix.popPose();
      return type;
   }
}
