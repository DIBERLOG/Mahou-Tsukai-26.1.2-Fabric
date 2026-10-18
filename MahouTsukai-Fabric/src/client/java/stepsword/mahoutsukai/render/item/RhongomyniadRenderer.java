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
import stepsword.mahoutsukai.item.lance.Rhongomyniad;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.SpiralGeometry;

public class RhongomyniadRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RUNE_SCOPE_1 = "rhongomyniad_renderer_1";
   private static final String RUNE_SCOPE_2 = "rhongomyniad_renderer_2";
   private static final String RUNE_SCOPE_3 = "rhongomyniad_renderer_3";
   private static final String RUNE_SCOPE_4 = "rhongomyniad_renderer_4";

   public RhongomyniadRenderer() {
   }

   public RhongomyniadRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Rhongomyniad;
      state.time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.rhongomyniad.key);
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
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         matrix.popPose();
         float rotation = 0.0F;
         float yaw = 0.0F;
         float pitch = 180.0F;
         float offsetx = 0.53F;
         float offsety = 0.5F;
         float offsetz = 0.52F;
         float a = 1.0F;
         double speed = MTConfig.POWER_CONSOLIDATION_CALIBURN_RING_SPEED * 8.0;
         float ring = 90.0F;
         float size = 1.0F;
         float pull = (float)(state.time % 360L);
         rotation = (float)(rotation + pull * speed);
         rotation %= 360.0F;
         matrix.pushPose();
         matrix.translate(offsetx, offsety, offsetz);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(-rotation, 0.0F, 1.0F, 0.0F, matrix);
         int lightmapX = 240;
         int lightmapY = 240;
         float r = 191.0F;
         float g = 241.0F;
         float b = 251.0F;
         r /= 255.0F;
         g /= 255.0F;
         b /= 255.0F;
         RenderType runes1 = MahoujinRenderType.createScopedRunesRenderType("rhongomyniad_renderer_1", RenderUtils.runes, 0);
         RenderType runes2 = MahoujinRenderType.createScopedRunesRenderType("rhongomyniad_renderer_2", RenderUtils.runes, 0);
         RenderType runes3 = MahoujinRenderType.createScopedRunesRenderType("rhongomyniad_renderer_3", RenderUtils.runes, 0);
         RenderType runes4 = MahoujinRenderType.createScopedRunesRenderType("rhongomyniad_renderer_4", RenderUtils.runes, 0);
         float texs = rotation % 360.0F / 360.0F;
         texs %= 1.0F;
         renderSpiral(matrix, submitNodeCollector, 0.907F, ring, size / 3.0F, 0.2F, 48, lightmapX, lightmapY, r, g, b, a, runes1, -0.052F, 0.03F, texs);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         renderSpiral(matrix, submitNodeCollector, 0.907F, ring - 20.0F, size / 3.0F, 0.2F, 48, lightmapX, lightmapY, r, g, b, a, runes2, -0.052F, 0.03F, texs);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         renderSpiral(matrix, submitNodeCollector, 0.907F, ring - 20.0F, size / 3.0F, 0.2F, 48, lightmapX, lightmapY, r, g, b, a, runes3, -0.052F, 0.03F, texs);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         renderSpiral(matrix, submitNodeCollector, 0.907F, ring - 20.0F, size / 3.0F, 0.2F, 48, lightmapX, lightmapY, r, g, b, a, runes4, -0.052F, 0.03F, texs);
         matrix.popPose();
      }
   }

   private static void renderSpiral(
      PoseStack matrix,
      SubmitNodeCollector submitNodeCollector,
      double cy,
      float flatness,
      float radius,
      float height,
      int segments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType type,
      float lengthen,
      float innerRadius,
      float texs
   ) {
      SpiralGeometry spiral = new SpiralGeometry(flatness, radius, height);
      spiral.setStartingY(cy);
      spiral.setNumSegments(segments);
      spiral.setLengthen(lengthen);
      spiral.setInnerRadius(innerRadius);
      spiral.setTexs(texs);
      spiral.setColor(new float[]{r, g, b, a});
      spiral.setLx(lx);
      spiral.setLy(ly);
      spiral.setType(type);
      spiral.render(matrix, submitNodeCollector);
   }
}
