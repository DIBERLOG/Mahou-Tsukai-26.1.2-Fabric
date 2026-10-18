package stepsword.mahoutsukai.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.tile.boundary.InvisibleBarrierBlockTileEntity;

public class InvisibleBarrierBlockRenderer implements BlockEntityRenderer<InvisibleBarrierBlockTileEntity, InvisibleBarrierRenderState> {
   public static Color fullyLit = new Color(25, 243, 255, 255);

   public InvisibleBarrierBlockRenderer(Context rendererDispatcherIn) {
   }

   public InvisibleBarrierRenderState createRenderState() {
      return new InvisibleBarrierRenderState();
   }

   public void extractRenderState(
      InvisibleBarrierBlockTileEntity blockEntity,
      InvisibleBarrierRenderState state,
      float partialTicks,
      Vec3 cameraPosition,
      @Nullable CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      Direction[] facings = new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.DOWN};

      for (int i = 0; i < facings.length; i++) {
         state.times[i] = blockEntity.getTimeSinceCollision(facings[i]);
      }
   }

   public void submit(InvisibleBarrierRenderState state, PoseStack matrixStack, SubmitNodeCollector submitter, CameraRenderState camera) {
      matrixStack.pushPose();
      Direction[] facings = new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.DOWN};
      int cmp = 20;

      for (int i = 0; i < facings.length; i++) {
         if (state.times[i] < cmp) {
            float percent = 1.0F * (float)(cmp - state.times[i]);
            percent /= cmp * 1.0F;
            percent = Mth.clamp(percent, 0.0F, 1.0F);
            this.renderSide(matrixStack, percent, facings[i], submitter);
         }
      }

      matrixStack.popPose();
   }

   public void renderSide(PoseStack matrix, float percent, Direction facing, SubmitNodeCollector submitter) {
      Color c = new Color(
         (int)Math.floor(fullyLit.getRed() * percent),
         (int)Math.floor(fullyLit.getGreen() * percent),
         (int)Math.floor(fullyLit.getBlue() * percent),
         (int)Math.floor(fullyLit.getAlpha() * percent)
      );
      int r = c.getRed();
      int g = c.getGreen();
      int b = c.getBlue();
      int a = c.getAlpha();
      float z1 = 0.0F;
      float y1 = 0.0F;
      float x1 = 0.0F;
      float x2 = x1 + 1.0F;
      float y2 = y1 + 1.0F;
      float z2 = z1 + 1.0F;
      RenderType type = MahoujinRenderType.createMahoujinRenderType(Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/barrier_hit.png"), 0, false);
      float a1;
      float a2;
      float a3;
      float b1;
      float b2;
      float b3;
      float c1;
      float c2;
      float c3;
      float d1;
      float d2;
      float d3;
      switch (facing) {
         case UP:
            d3 = z1;
            a3 = z1;
            b1 = x1;
            a1 = x1;
            c3 = z2;
            b3 = z2;
            d1 = x2;
            c1 = x2;
            d2 = y2;
            c2 = y2;
            b2 = y2;
            a2 = y2;
            break;
         case DOWN:
            c3 = z1;
            b3 = z1;
            b1 = x1;
            a1 = x1;
            d3 = z2;
            a3 = z2;
            d1 = x2;
            c1 = x2;
            d2 = y1;
            c2 = y1;
            b2 = y1;
            a2 = y1;
            break;
         case EAST:
            c3 = z1;
            d3 = z1;
            c2 = y1;
            b2 = y1;
            b3 = z2;
            a3 = z2;
            d2 = y2;
            a2 = y2;
            d1 = x2;
            c1 = x2;
            b1 = x2;
            a1 = x2;
            break;
         case WEST:
            b3 = z1;
            a3 = z1;
            c2 = y1;
            b2 = y1;
            c3 = z2;
            d3 = z2;
            d2 = y2;
            a2 = y2;
            d1 = x1;
            c1 = x1;
            b1 = x1;
            a1 = x1;
            break;
         case SOUTH:
            b1 = x1;
            a1 = x1;
            c2 = y1;
            b2 = y1;
            d1 = x2;
            c1 = x2;
            d2 = y2;
            a2 = y2;
            d3 = z2;
            c3 = z2;
            b3 = z2;
            a3 = z2;
            break;
         case NORTH:
            d1 = x1;
            c1 = x1;
            c2 = y1;
            b2 = y1;
            b1 = x2;
            a1 = x2;
            d2 = y2;
            a2 = y2;
            d3 = z1;
            c3 = z1;
            b3 = z1;
            a3 = z1;
            break;
         default:
            throw new IllegalArgumentException(facing.toString());
      }

      int lx = 240;
      int ly = 240;
      submitter.submitCustomGeometry(matrix,type,(pose,buf)-> {
      Matrix4f positionMatrix=pose.pose();
      buf.addVertex(positionMatrix, a1, a2, a3).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, b1, b2, b3).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, c1, c2, c3).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, d1, d2, d3).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, a1, a2, a3).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, d1, d2, d3).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, c1, c2, c3).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      buf.addVertex(positionMatrix, b1, b2, b3).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
      });
   }
}
