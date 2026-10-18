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
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;

public class ClarentRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE_1 = "clarent_renderer_1";
   private static final String RING_SCOPE_2 = "clarent_renderer_2";
   private final net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey<QuadCollection> model;

   public ClarentRenderer() {
      this(RenderBaseItem.CLARENT);
   }

   public ClarentRenderer(net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey<QuadCollection> model) {
      this.model = model;
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Clarent;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(this.model);
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
         float offsetx = 0.27F;
         float offsety = -0.2F;
         float offsetz = 0.5F;
         float r = 255.0F;
         float g = 0.0F;
         float b = 93.0F;
         float a = 1.0F;
         float rotation = 0.0F;
         float yaw = 90.0F;
         float pitch = 0.0F;
         double speed = 1.5;
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
         MahouGeometry ring1 = new RingGeometry(size / 3.69999F, 0.09F)
            .setStartingY(0.5)
            .setFlatness(ring)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("clarent_renderer_1", RenderUtils.runes, 1));
         ring1.render(matrix, submitNodeCollector);
         RenderUtils.rotateQ(-rotation, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(-pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(-yaw, 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(0.43F, 0.48F, 0.0F);
         RenderUtils.rotateQ(yaw + 180.0F, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         MahouGeometry ring2 = new RingGeometry(size / 3.7F, 0.09F)
            .setStartingY(0.5)
            .setFlatness(ring)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("clarent_renderer_2", RenderUtils.runes, 2));
         ring2.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
