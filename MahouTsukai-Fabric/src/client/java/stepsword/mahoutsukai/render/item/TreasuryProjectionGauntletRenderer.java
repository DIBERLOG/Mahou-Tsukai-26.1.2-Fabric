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
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.spells.projection.TreasuryProjection.TreasuryProjectionGauntlet;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;

public class TreasuryProjectionGauntletRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE = "treasury_projection_gauntlet_renderer";

   public TreasuryProjectionGauntletRenderer() {
   }

   public TreasuryProjectionGauntletRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof TreasuryProjectionGauntlet;
      state.time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.treasuryGauntlet.key);
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
         float r = 1.0F;
         float g = 1.0F;
         float b = 0.5882353F;
         float a = 1.0F;
         float rotation = (float)state.time * (float)MTConfig.MYSTIC_CODE_RING_SPEED % 360.0F;
         float pitch = 90.0F;
         float ring = 90.0F;
         float size = 1.0F;
         int lightmapX = 240;
         int lightmapY = 240;
         matrix.translate(0.5, 0.5, 0.0);
         RenderUtils.rotateQ(0.0F, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         float[] colors = new float[]{r, g, b, a};
         MahouGeometry ring1 = new RingGeometry(size / 4.5F, 0.04F)
            .setStartingY(0.5)
            .setFlatness(ring)
            .setNumSegments(48)
            .setColor(colors)
            .setType(MahoujinRenderType.createScopedRunesRenderType("treasury_projection_gauntlet_renderer", RenderUtils.runes, 0))
            .setLx(lightmapX)
            .setLy(lightmapY);
         ring1.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
