package stepsword.mahoutsukai.render.item;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import stepsword.mahoutsukai.block.MahoujinProjector;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;

public class MahoujinProjectorItemStackRenderer extends BlockEntityItemStackRenderer<MahoujinProjectorTileEntity> {
   public MahoujinProjectorItemStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      super(p_172550_, p_172551_);
   }

   @Override
   protected boolean shouldRender(ItemStack stack) {
      return Block.byItem(stack.getItem()) instanceof MahoujinProjector;
   }

   protected MahoujinProjectorTileEntity createBlockEntity() {
      return new MahoujinProjectorTileEntity(new BlockPos(0, 0, 0), ((MahoujinProjector)ModBlocks.projector.get()).defaultBlockState());
   }
}
