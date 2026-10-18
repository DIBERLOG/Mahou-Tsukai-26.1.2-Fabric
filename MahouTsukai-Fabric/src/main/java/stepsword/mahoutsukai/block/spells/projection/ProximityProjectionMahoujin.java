package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.ProximityProjectionMahoujinTileEntity;

public class ProximityProjectionMahoujin extends SingleUseMahoujinBlockTileEntity<ProximityProjectionMahoujinTileEntity> {
   public ProximityProjectionMahoujin() {
      super("mahoujin_proximity_projection");
   }

   @Override
   public Class<ProximityProjectionMahoujinTileEntity> getTileEntityClass() {
      return ProximityProjectionMahoujinTileEntity.class;
   }

   @Nullable
   public ProximityProjectionMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ProximityProjectionMahoujinTileEntity(blockPos, blockState);
   }
}
