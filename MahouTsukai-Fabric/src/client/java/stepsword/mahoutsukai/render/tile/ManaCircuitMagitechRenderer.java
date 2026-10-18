package stepsword.mahoutsukai.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitMagitechTileEntity;

public class ManaCircuitMagitechRenderer implements BlockEntityRenderer<ManaCircuitMagitechTileEntity, ManaCircuitRenderState> {
   private static final String MAGITECH_CIRCUIT_SCOPE = "mana_circuit_magitech_inner";
   private static final Identifier inner = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final float[] colorOff = new float[]{11.0F, 51.0F, 44.0F};
   private static final float[] colorOn = new float[]{59.0F, 255.0F, 219.0F};

   public ManaCircuitMagitechRenderer(Context rendererDispatcherIn) {
   }

   public ManaCircuitRenderState createRenderState() {
      return new ManaCircuitRenderState();
   }

   public void extractRenderState(
      ManaCircuitMagitechTileEntity blockEntity,
      ManaCircuitRenderState state,
      float partialTicks,
      Vec3 cameraPosition,
      @Nullable CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      if (blockEntity.getLevel() != null) {
         state.time = ClientHandler.clientTickCounter;
      }

      state.on = blockEntity.isRedstoneOn();
   }

   public void submit(ManaCircuitRenderState state, PoseStack matrix, SubmitNodeCollector submitter, CameraRenderState camera) {
      long time = state.time;
      int modfac = 180;
      time %= modfac;
      if (time < 0L) {
         time += modfac;
      }

      float dist = (int)(time - modfac / 2);
      dist = Math.abs(dist / (modfac / 2.0F));
      int lx = 240;
      int ly = 240;
      if (!state.on) {
         dist = 0.0F;
         lx = 100;
         ly = 100;
      }

      float r = colorOff[0] + dist * (colorOn[0] - colorOff[0]);
      float g = colorOff[1] + dist * (colorOn[1] - colorOff[1]);
      float b = colorOff[2] + dist * (colorOn[2] - colorOff[2]);
      r /= 255.0F;
      g /= 255.0F;
      b /= 255.0F;
      float a = 1.0F;
      float sz = 0.96F;
      drawCircle(matrix, submitter, sz, lx, ly, r, g, b, a);
   }

   public static void drawCircle(PoseStack matrix, SubmitNodeCollector submitter, float size, int lx, int ly, float r, float g, float b, float a) {
      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mana_circuit_magitech_inner", inner, 0, false);
      float off = size / 2.0F;
      float off1 = (1.0F - size) / 2.0F;
      float a1 = off1;
      float b1 = off1 + size;
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, a1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, b1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, b1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, a1, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, a1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, b1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, b1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, a1, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, a1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, a1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, a1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, a1, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, b1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, b1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, b1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, b1, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, a1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, a1, b1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, b1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, a1, b1, a1, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, a1, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, a1, b1, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, b1, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, b1, b1, a1, 1.0F, 0.0F, lx, ly);
      });
   }
}
