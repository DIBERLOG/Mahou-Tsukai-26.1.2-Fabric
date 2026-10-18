package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.BoundaryBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.TangibleBoundaryMahoujinTileEntity;

public class TangibleBoundaryMahoujin extends BoundaryBlockTileEntity<TangibleBoundaryMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public TangibleBoundaryMahoujin() {
      super("mahoujin_tangible_barrier");
   }

   @Override
   public Class<TangibleBoundaryMahoujinTileEntity> getTileEntityClass() {
      return TangibleBoundaryMahoujinTileEntity.class;
   }

   @Nullable
   public TangibleBoundaryMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new TangibleBoundaryMahoujinTileEntity(blockPos, blockState);
   }

   protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof TangibleBoundaryMahoujinTileEntity) {
         ((TangibleBoundaryMahoujinTileEntity)te).doNotBarrier();
      }

      super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
   }

   @Override
   protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
      return super.getAnalogOutputSignal(blockState, worldIn, pos, direction);
   }
}
