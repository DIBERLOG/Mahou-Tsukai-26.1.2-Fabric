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
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;

public class CaliburnRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE_1 = "caliburn_renderer_1";
   private static final String RING_SCOPE_2 = "caliburn_renderer_2";
   private static final String RING_SCOPE_3 = "caliburn_renderer_3";

   public CaliburnRenderer() {
   }

   public CaliburnRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Caliburn;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.caliburn.key);
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
         matrix.pushPose();
         RenderType type = null;
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, type, true);
         float offsetx = 0.0F;
         float offsety = 0.0F;
         float offsetz = 0.5F;
         float rotation = 0.0F;
         float yaw = 90.0F;
         float pitch = 45.0F;
         double speed = MTConfig.POWER_CONSOLIDATION_CALIBURN_RING_SPEED;
         float ring = 90.0F;
         float size = 1.0F;
         float pull = (float)(state.time % 360L);
         rotation = (float)(rotation + pull * speed);
         float secondrotation = (float)(360.0 - pull * speed % 360.0);
         rotation %= 360.0F;
         secondrotation %= 360.0F;
         matrix.translate(offsetx, offsety, offsetz);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         float[] colors = new float[]{1.0F, 0.8627451F, 0.0F, 1.0F};
         MahouGeometry ring1 = new RingGeometry(size / 8.9F, 0.05F)
            .setStartingY(0.907F)
            .setFlatness(ring - 20.0F)
            .setColor(colors)
            .setType(MahoujinRenderType.createScopedRunesRenderType("caliburn_renderer_1", RenderUtils.runes, 0));
         MahouGeometry ring2 = new RingGeometry(size / 6.8F, 0.05F)
            .setStartingY(0.7F)
            .setFlatness(ring - 10.0F)
            .setColor(colors)
            .setType(MahoujinRenderType.createScopedRunesRenderType("caliburn_renderer_2", RenderUtils.runes, 0));
         MahouGeometry ring3 = new RingGeometry(size / 3.7F, 0.09F)
            .setStartingY(0.5)
            .setFlatness(ring - 4.0F)
            .setColor(colors)
            .setType(MahoujinRenderType.createScopedRunesRenderType("caliburn_renderer_3", RenderUtils.runes, 0));
         ring1.render(matrix, submitNodeCollector);
         RenderUtils.rotateQ(rotation, 0.0F, -1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(secondrotation, 0.0F, 1.0F, 0.0F, matrix);
         ring2.render(matrix, submitNodeCollector);
         RenderUtils.rotateQ(secondrotation, 0.0F, -1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         ring3.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
