package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.circuits.ManaCircuitBlock;
import stepsword.mahoutsukai.block.circuits.ManaCircuitMagitechBlock;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitMagitechTileEntity;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;

public class ManaCircuitItemStackRenderer extends BlockEntityItemStackRenderer<BlockEntity> {
   public ManaCircuitItemStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      super(p_172550_, p_172551_);
   }

   @Override
   protected boolean shouldRender(ItemStack itemStackIn) {
      Block block = Block.byItem(itemStackIn.getItem());
      return block == ModBlocks.circuit.get() || block == ModBlocks.circuitMagitech.get();
   }

   @Override
   protected BlockEntity createBlockEntity() {
      return new ManaCircuitTileEntity(new BlockPos(0, 0, 0), ((ManaCircuitBlock)ModBlocks.circuit.get()).defaultBlockState());
   }

   @Override
   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      Block block = Block.byItem(stack.getItem());
      state.render = block == ModBlocks.circuit.get() || block == ModBlocks.circuitMagitech.get();
      if (block == ModBlocks.circuitMagitech.get()) {
         state.colorIndex = 1;
      }

      return state;
   }

   @Override
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
         if (state.colorIndex == 1) {
            this.submitBlockEntity(
               new ManaCircuitMagitechTileEntity(
                  new BlockPos(0, 0, 0), ((ManaCircuitMagitechBlock)ModBlocks.circuitMagitech.get()).defaultBlockState()
               ),
               poseStack,
               submitNodeCollector
            );
         } else {
            ManaCircuitTileEntity te = new ManaCircuitTileEntity(
               new BlockPos(0, 0, 0), ((ManaCircuitBlock)ModBlocks.circuit.get()).defaultBlockState()
            );
            te.setOn(false);
            te.setMana(0);
            te.setOwnerUUID(null);
            this.submitBlockEntity(te, poseStack, submitNodeCollector);
         }
      }
   }
}
