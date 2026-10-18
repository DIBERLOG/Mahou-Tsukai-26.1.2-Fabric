package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.SpatialDisorientationMahoujinTileEntity;

public class SpatialDisorientationMahoujin extends SingleUseMahoujinBlockTileEntity<SpatialDisorientationMahoujinTileEntity> {
   public SpatialDisorientationMahoujin() {
      super("mahoujin_spatial_disorientation");
   }

   @Override
   public Class<SpatialDisorientationMahoujinTileEntity> getTileEntityClass() {
      return SpatialDisorientationMahoujinTileEntity.class;
   }

   @Nullable
   public SpatialDisorientationMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new SpatialDisorientationMahoujinTileEntity(blockPos, blockState);
   }
}
