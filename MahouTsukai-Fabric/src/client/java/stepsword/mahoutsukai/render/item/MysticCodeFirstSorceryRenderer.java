package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.client.ItemUseRenderBridge;
import stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery;
import stepsword.mahoutsukai.render.RenderBaseItem;

public class MysticCodeFirstSorceryRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private final RenderBaseItem.ModelKeyPair model;

   public MysticCodeFirstSorceryRenderer() {
      this(RenderBaseItem.mysticCodeFirstSorcery);
   }

   public MysticCodeFirstSorceryRenderer(RenderBaseItem.ModelKeyPair model) {
      this.model = model;
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof MysticCodeFirstSorcery;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(this.model.key);
      state.stack = stack;
      ItemDisplayContext transform = ItemUseRenderBridge.getDisplayContext(stack);
      state.transform = transform != null ? transform : ItemDisplayContext.NONE;
      state.useTicks = ItemUseRenderBridge.getUseTicks(stack);
      state.r = 230.0F;
      state.g = 40.0F;
      state.b = 40.0F;
      state.a = 1.0F;
      state.r = 255.0F;
      state.g = 215.0F;
      state.b = 0.0F;
      state.colorIndex = 5;
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
         if (state.useTicks > 0.0F) {
            MysticCodeRenderer.renderMysticCode(state, matrix, submitNodeCollector);
         }

         matrix.popPose();
      }
   }
}
