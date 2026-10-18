package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;
import stepsword.mahoutsukai.util.Utils;

public class MorganRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE_1 = "morgan_renderer_1";
   private static final String RING_SCOPE_2 = "morgan_renderer_2";
   private static final String RING_SCOPE_3 = "morgan_renderer_3";

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Morgan;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.morgan.key);
      state.r = 255.0F;
      state.g = 50.0F;
      state.b = 50.0F;
      state.a = 1.0F;
      float[] colors = Utils.getColor(stack);
      if (colors != null && colors.length > 2) {
         state.r = colors[0];
         state.g = colors[1];
         state.b = colors[2];
      }

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
         float r = state.r;
         float g = state.g;
         float b = state.b;
         float a = state.a;
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
         int lightmapX = 240;
         int lightmapY = 240;
         r /= 255.0F;
         g /= 255.0F;
         b /= 255.0F;
         float[] colors = new float[]{r, g, b, a};
         MahouGeometry ring1 = new RingGeometry(size / 8.9F, 0.05F)
            .setStartingY(0.907F)
            .setFlatness(ring - 20.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("morgan_renderer_1", RenderUtils.runes, 0));
         ring1.render(matrix, submitNodeCollector);
         RenderUtils.rotateQ(rotation, 0.0F, -1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(secondrotation, 0.0F, 1.0F, 0.0F, matrix);
         MahouGeometry ring2 = new RingGeometry(size / 6.8F, 0.05F)
            .setStartingY(0.7F)
            .setFlatness(ring - 10.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("morgan_renderer_2", RenderUtils.runes, 0));
         ring2.render(matrix, submitNodeCollector);
         RenderUtils.rotateQ(secondrotation, 0.0F, -1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         MahouGeometry ring3 = new RingGeometry(size / 3.7F, 0.09F)
            .setStartingY(0.5)
            .setFlatness(ring - 4.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("morgan_renderer_3", RenderUtils.runes, 0));
         ring3.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
