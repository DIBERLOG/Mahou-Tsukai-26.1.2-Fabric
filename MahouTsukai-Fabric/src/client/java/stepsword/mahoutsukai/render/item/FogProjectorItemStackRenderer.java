package stepsword.mahoutsukai.render.item;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.tile.FogProjectorTileEntity;

public class FogProjectorItemStackRenderer extends BlockEntityItemStackRenderer<FogProjectorTileEntity> {
   public FogProjectorItemStackRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      super(blockEntityRenderDispatcher, entityModelSet);
   }

   @Override
   protected boolean shouldRender(ItemStack stack) {
      return Block.byItem(stack.getItem()) == ModBlocks.fogProjector.get();
   }

   protected FogProjectorTileEntity createBlockEntity() {
      return new FogProjectorTileEntity(true);
   }
}
