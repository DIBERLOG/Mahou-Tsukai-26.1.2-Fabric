package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public abstract class BlockEntityItemStackRenderer<E extends BlockEntity> implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final CameraRenderState ITEM_CAMERA = new CameraRenderState();
   protected final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

   protected BlockEntityItemStackRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         E blockEntity = this.createBlockEntity();
         this.configureBlockEntity(blockEntity, state);
         this.submitBlockEntity(blockEntity, poseStack, submitNodeCollector);
      }
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = this.shouldRender(stack);
      return state;
   }

   protected abstract boolean shouldRender(ItemStack var1);

   protected abstract E createBlockEntity();

   protected void configureBlockEntity(E blockEntity, MagicalItemRenderState state) {
   }

   protected <S extends BlockEntityRenderState> void submitBlockEntity(E blockEntity, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      BlockEntityRenderer<E, S> renderer = this.blockEntityRenderDispatcher.getRenderer(blockEntity);
      if (renderer != null) {
         S renderState = (S)renderer.createRenderState();
         renderer.extractRenderState(blockEntity, renderState, 0.0F, Vec3.ZERO, null);
         this.blockEntityRenderDispatcher.submit(renderState, poseStack, submitNodeCollector, ITEM_CAMERA);
      }
   }
}
