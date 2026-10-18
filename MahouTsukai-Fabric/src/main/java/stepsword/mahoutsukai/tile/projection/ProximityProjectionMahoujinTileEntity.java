package stepsword.mahoutsukai.tile.projection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.ScrollGiver;

public class ProximityProjectionMahoujinTileEntity extends MahoujinTileEntity implements ScrollGiver {
   public ProximityProjectionMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.proximityProjection.get(), p, bs);
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_proximity_projection"));
   }
}
