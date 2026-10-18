package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.RaiseEnclosureBarrierMahoujinTileEntity;

public class RaiseEnclosureBoundaryMahoujin extends SingleUseMahoujinBlockTileEntity<RaiseEnclosureBarrierMahoujinTileEntity> {
   public RaiseEnclosureBoundaryMahoujin() {
      super("mahoujin_raise_enclosure");
   }

   @Override
   public Class<RaiseEnclosureBarrierMahoujinTileEntity> getTileEntityClass() {
      return RaiseEnclosureBarrierMahoujinTileEntity.class;
   }

   @Nullable
   public RaiseEnclosureBarrierMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new RaiseEnclosureBarrierMahoujinTileEntity(blockPos, blockState);
   }
}
