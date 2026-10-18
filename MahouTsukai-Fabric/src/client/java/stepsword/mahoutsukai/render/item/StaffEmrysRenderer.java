package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;
import stepsword.mahoutsukai.render.geometry.SphereGeometry;

public class StaffEmrysRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE = "staff_emrys_renderer";

   public StaffEmrysRenderer() {
   }

   public StaffEmrysRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = true;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.staff.key);
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
         float offsetx = 0.04F;
         float offsety = 0.96F;
         float offsetz = 0.5F;
         float r = 1.0F;
         float g = 1.0F;
         float b = 1.0F;
         float a = 1.0F;
         float r2 = 0.2F;
         float g2 = 0.5F;
         float b2 = 1.0F;
         float a2 = 0.2F;
         float rotation = 0.0F;
         float yaw = 90.0F;
         float pitch = -45.0F;
         double speed = MTConfig.POWER_CONSOLIDATION_CALIBURN_RING_SPEED;
         float size = 0.11F;
         float pull = (float)(state.time % 360L);
         rotation = (float)(rotation + pull * speed);
         float secondrotation = (float)(360.0 - pull * speed % 360.0);
         rotation %= 360.0F;
         secondrotation %= 360.0F;
         matrix.translate(offsetx, offsety, offsetz);
         int lightmapX = 240;
         int lightmapY = 240;
         matrix.pushPose();
         matrix.translate(1.0F - offsetx, -offsety, 0.0F);
         RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         float[] colors = new float[]{r2, g2, b2, a};
         MahouGeometry ring1 = new RingGeometry(size * 1.3F, 0.03F)
            .setStartingY(1.36F)
            .setFlatness(90.0F)
            .setColor(colors)
            .setLx(lightmapX)
            .setLy(lightmapY)
            .setType(MahoujinRenderType.createScopedRunesRenderType("staff_emrys_renderer", RenderUtils.runes, 0));
         ring1.render(matrix, submitNodeCollector);
         matrix.popPose();
         float[] colors1 = new float[]{r, g, b, a};
         MahouGeometry sphere1 = new SphereGeometry(size).setColor(colors1).setLx(lightmapX).setLy(lightmapY);
         sphere1.render(matrix, submitNodeCollector);
         float[] colors2 = new float[]{r2, g2, b2, a2};
         MahouGeometry sphere2 = new SphereGeometry(size * 1.2F).setColor(colors2).setLx(lightmapX).setLy(lightmapY);
         sphere2.render(matrix, submitNodeCollector);
         matrix.popPose();
      }
   }
}
