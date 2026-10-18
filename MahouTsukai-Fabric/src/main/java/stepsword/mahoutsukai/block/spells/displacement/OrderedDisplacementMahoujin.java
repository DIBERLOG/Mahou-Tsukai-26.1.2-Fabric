package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.OrderedDisplacementMahoujinTileEntity;

public class OrderedDisplacementMahoujin extends MahoujinBlockTileEntity<OrderedDisplacementMahoujinTileEntity> {
   public OrderedDisplacementMahoujin() {
      super("mahoujin_ordered_displacement");
   }

   @Override
   public Class<OrderedDisplacementMahoujinTileEntity> getTileEntityClass() {
      return OrderedDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public OrderedDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new OrderedDisplacementMahoujinTileEntity(blockPos, blockState);
   }
}
