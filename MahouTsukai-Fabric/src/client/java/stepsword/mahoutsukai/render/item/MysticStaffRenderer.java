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
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;
import stepsword.mahoutsukai.render.geometry.SphereGeometry;

public class MysticStaffRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE_1 = "mystic_staff_renderer_1";
   private static final String RING_SCOPE_2 = "mystic_staff_renderer_2";

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof MysticStaff;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.mystic_staff.key);
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
         float offsetx = 0.25F;
         float offsety = 0.75F;
         float offsetz = 0.5F;
         float r = 1.0F;
         float g = 0.2F;
         float b = 0.2F;
         float a = 1.0F;
         float r2 = 1.0F;
         float g2 = 0.2F;
         float b2 = 0.2F;
         float a2 = 0.2F;
         float rr = 1.0F;
         float gr = 0.47F;
         float br = 0.22F;
         float rotation = 0.0F;
         float yaw = 90.0F;
         float pitch = -45.0F;
         double speed = MTConfig.POWER_CONSOLIDATION_CALIBURN_RING_SPEED;
         float ring = 90.0F;
         float size = 0.06F;
         float pull = (float)(state.time % 360L);
         rotation = (float)(rotation + pull * speed);
         float secondrotation = (float)(360.0 - pull * speed % 360.0);
         rotation %= 360.0F;
         secondrotation %= 360.0F;
         matrix.translate(offsetx, offsety, offsetz);
         int lightmapX = 240;
         int lightmapY = 240;
         matrix.pushPose();
         RenderUtils.rotateQ(90.0F, 0.0F, 0.0F, 1.0F, matrix);
         RenderUtils.rotateQ(pull * 4.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pull * 4.0F, 0.0F, 0.0F, 1.0F, matrix);
         matrix.translate(1.0F - offsetx, -offsety, 0.0F);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         float[] colors = new float[]{rr, gr, br, a};
         MahouGeometry ring1 = new RingGeometry(size * 1.3F, 0.015F)
            .setStartingY(1.074F)
            .setFlatness(90.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("mystic_staff_renderer_1", RenderUtils.runes, 0));
         ring1.render(matrix, submitNodeCollector);
         matrix.popPose();
         matrix.pushPose();
         RenderUtils.rotateQ(pull * 2.0F, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pull * 2.0F, 0.0F, 0.0F, 1.0F, matrix);
         matrix.translate(1.0F - offsetx, -offsety, 0.0F);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         MahouGeometry ring2 = new RingGeometry(size * 1.3F, 0.015F)
            .setStartingY(1.074F)
            .setFlatness(90.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("mystic_staff_renderer_2", RenderUtils.runes, 0));
         ring2.render(matrix, submitNodeCollector);
         matrix.popPose();
         float[] colors1 = new float[]{r, g, b, a};
         MahouGeometry sphere1 = new SphereGeometry(size).setGradation(20).setColor(colors1).setLx(lightmapX).setLy(lightmapY);
         sphere1.render(matrix, submitNodeCollector);
         float[] colors2 = new float[]{r2, g2, b2, a2};
         MahouGeometry sphere2 = new SphereGeometry(size * 1.2F).setGradation(20).setColor(colors2).setLx(lightmapX).setLy(lightmapY);
         sphere2.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
