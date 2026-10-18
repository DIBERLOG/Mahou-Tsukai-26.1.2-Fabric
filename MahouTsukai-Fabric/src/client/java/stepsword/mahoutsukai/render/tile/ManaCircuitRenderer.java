package stepsword.mahoutsukai.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;

public class ManaCircuitRenderer implements BlockEntityRenderer<ManaCircuitTileEntity, ManaCircuitRenderState> {
   private static final int[][] colors = new int[][]{
      {255, 0, 0}, {255, 106, 0}, {255, 204, 0}, {207, 255, 15}, {91, 255, 15}, {19, 227, 0}, {0, 240, 176}, {5, 219, 247}, {65, 32, 250}, {255, 71, 231}
   };

   public ManaCircuitRenderer(Context rendererDispatcherIn) {
   }

   public ManaCircuitRenderState createRenderState() {
      return new ManaCircuitRenderState();
   }

   public void extractRenderState(
      ManaCircuitTileEntity blockEntity, ManaCircuitRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      if (blockEntity.getLevel() != null) {
         state.time = ClientHandler.clientTickCounter;
      }

      state.on = blockEntity.isRedstoneOn();
   }

   public void submit(ManaCircuitRenderState state, PoseStack matrix, SubmitNodeCollector submitter, CameraRenderState camera) {
      matrix.pushPose();
      matrix.translate(0.5F, 0.5F, 0.5F);
      long time = state.time;
      int lx = 240;
      int ly = 240;
      int offset = 100;
      float a = 0.8F;
      int rings = 6;
      float[][] rgbs = new float[rings + 1][3];
      float[] heights = new float[]{0.02F, 0.02F, 0.04F, 0.04F, 0.05F, 0.05F};
      float[] pitches = new float[rings + 1];
      float[] yaws = new float[rings + 1];
      float[] rotations = new float[rings + 1];
      float colortime = (float)(time % 1000L);
      float ranges = 1000.0F / colors.length;

      for (int i = 0; i < rgbs.length; i++) {
         float cval = (colortime + offset * i) % 1000.0F;
         cval /= ranges;
         rgbs[i] = valToColor(cval, ranges);
         yaws[i] = valToYaw(time + offset * i);
         rotations[i] = valToYaw(time + (offset + 50) * i);
         pitches[i] = valToYaw(time + (offset + 90) * i);
      }

      float[] scales;
      if (!state.on) {
         scales = new float[]{0.2F, 0.11F, 0.11F, 0.11F, 0.11F, 0.11F, 0.11F};

         for (int i = 0; i < rgbs.length; i++) {
            for (int j = 0; j < rgbs[i].length; j++) {
               rgbs[i][j] = 180.0F;
            }
         }
      } else {
         scales = new float[]{0.2F, 0.11F, 0.11F, 0.2F, 0.2F, 0.4F, 0.4F};
      }

      rotate(matrix, yaws[0], pitches[0], rotations[0]);
      drawCircle(matrix, submitter, scales[0], lx, ly, rgbs[0][0], rgbs[0][1], rgbs[0][2], a);
      unrotate(matrix, yaws[0], pitches[0], rotations[0]);

      for (int i = 1; i < rings + 1; i++) {
         rotate(matrix, yaws[i], pitches[i], rotations[i]);
         NewRenderUtils.renderRing(
            "mana_circuit_ring_" + i,
            matrix,
            submitter,
            heights[i - 1] * 0.5F,
            90.0F,
            scales[i],
            heights[i - 1],
            32,
            lx,
            ly,
            rgbs[i][0],
            rgbs[i][1],
            rgbs[i][2],
            a
         );
         unrotate(matrix, yaws[i], pitches[i], rotations[i]);
      }

      for (int i = 0; i < 2; i++) {
         int yaw = 45;
         if (i % 2 == 0) {
            yaw = -135;
         }

         rotate(matrix, yaw, 45.0F, valToYaw(time + (offset + 90) * i));
         NewRenderUtils.renderRing(
            "mana_circuit_orbit_" + i, matrix, submitter, heights[5] * 0.5F, 90.0F, scales[6], heights[5], 32, lx, ly, rgbs[i][0], rgbs[i][1], rgbs[i][2], a
         );
         unrotate(matrix, yaw, 45.0F, valToYaw(time + (offset + 90) * i));
      }

      matrix.popPose();
   }

   public static float[] valToColor(float val, float ranges) {
      int z = (int)Math.floor(val);
      z %= colors.length;
      int[] arr = colors[z];
      int y = z - 1 % colors.length;
      if (y < 0) {
         y += colors.length;
      }

      int[] arr2 = colors[y];
      float remainder = val * ranges % ranges / ranges;
      int rn = (int)(arr2[0] + remainder * (arr[0] - arr2[0]));
      int gn = (int)(arr2[1] + remainder * (arr[1] - arr2[1]));
      int bn = (int)(arr2[2] + remainder * (arr[2] - arr2[2]));
      return new float[]{rn / 255.0F, gn / 255.0F, bn / 255.0F};
   }

   public static float valToYaw(long val) {
      return (float)val * 2.0F % 360.0F;
   }

   public static void rotate(PoseStack matrix, float yaw, float pitch, float rotation) {
      matrix.mulPose(Axis.YP.rotationDegrees(yaw));
      matrix.mulPose(Axis.XP.rotationDegrees(pitch));
      matrix.mulPose(Axis.YP.rotationDegrees(rotation));
   }

   public static void unrotate(PoseStack matrix, float yaw, float pitch, float rotation) {
      matrix.mulPose(Axis.YP.rotationDegrees(-rotation));
      matrix.mulPose(Axis.XP.rotationDegrees(-pitch));
      matrix.mulPose(Axis.YP.rotationDegrees(-yaw));
   }

   public static void drawCircle(PoseStack matrix, SubmitNodeCollector submitter, float size, int lx, int ly, float r, float g, float b, float a) {
      RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.mahoujin, 0, false);
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, -0.5F * size, 0.0F, -0.5F * size, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, -0.5F * size, 0.0F, 0.5F * size, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, 0.5F * size, 0.0F, 0.5F * size, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, 0.5F * size, 0.0F, -0.5F * size, 1.0F, 0.0F, lx, ly);
      });
   }
}
