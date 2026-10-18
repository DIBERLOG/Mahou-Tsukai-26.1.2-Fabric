package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.BoundaryBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.DisplacementBoundaryMahoujinTileEntity;

public class DisplacementBoundaryMahoujin extends BoundaryBlockTileEntity<DisplacementBoundaryMahoujinTileEntity> {
   public DisplacementBoundaryMahoujin() {
      super("mahoujin_displacement_barrier");
   }

   @Override
   public Class<DisplacementBoundaryMahoujinTileEntity> getTileEntityClass() {
      return DisplacementBoundaryMahoujinTileEntity.class;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DisplacementBoundaryMahoujinTileEntity(blockPos, blockState);
   }
}
