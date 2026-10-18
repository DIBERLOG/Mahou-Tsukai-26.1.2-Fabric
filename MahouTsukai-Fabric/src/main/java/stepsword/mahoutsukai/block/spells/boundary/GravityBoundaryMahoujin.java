package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.BoundaryBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.GravityBoundaryMahoujinTileEntity;

public class GravityBoundaryMahoujin extends BoundaryBlockTileEntity<GravityBoundaryMahoujinTileEntity> {
   public GravityBoundaryMahoujin() {
      super("mahoujin_gravity_barrier");
   }

   @Override
   public Class<GravityBoundaryMahoujinTileEntity> getTileEntityClass() {
      return GravityBoundaryMahoujinTileEntity.class;
   }

   @Nullable
   public GravityBoundaryMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new GravityBoundaryMahoujinTileEntity(blockPos, blockState);
   }
}
