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
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;

public class ProximityProjectionKeysRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   public ProximityProjectionKeysRenderer() {
   }

   public ProximityProjectionKeysRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = true;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(RenderBaseItem.RED_KEY);
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
         RenderUtils.rotateQ(-90.0F, 0.0F, 0.0F, 1.0F, matrix);
         matrix.translate(-0.3, -0.3, 0.08);
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         matrix.translate(0.0, 0.0, 0.1);
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         matrix.translate(0.0, 0.0, 0.1);
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, null, true);
         matrix.popPose();
      }
   }
}
