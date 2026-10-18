package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.BoundaryBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.DrainLifeBoundaryMahoujinTileEntity;

public class DrainLifeBoundaryMahoujin extends BoundaryBlockTileEntity<DrainLifeBoundaryMahoujinTileEntity> {
   public DrainLifeBoundaryMahoujin() {
      super("mahoujin_drain_life_barrier");
   }

   @Override
   public Class<DrainLifeBoundaryMahoujinTileEntity> getTileEntityClass() {
      return DrainLifeBoundaryMahoujinTileEntity.class;
   }

   @Nullable
   public DrainLifeBoundaryMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DrainLifeBoundaryMahoujinTileEntity(blockPos, blockState);
   }
}
