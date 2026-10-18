package stepsword.mahoutsukai.block.spells.boundary;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.BoundaryBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.AlarmBoundaryMahoujinTileEntity;

public class AlarmBoundaryMahoujin extends BoundaryBlockTileEntity<AlarmBoundaryMahoujinTileEntity> {
   public AlarmBoundaryMahoujin() {
      super("mahoujin_alarm_barrier");
   }

   @Override
   public Class<AlarmBoundaryMahoujinTileEntity> getTileEntityClass() {
      return AlarmBoundaryMahoujinTileEntity.class;
   }

   @Nullable
   public AlarmBoundaryMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new AlarmBoundaryMahoujinTileEntity(blockPos, blockState);
   }

   @Override
   protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
      BlockEntity te = worldIn.getBlockEntity(pos);
      return te instanceof AlarmBoundaryMahoujinTileEntity && ((AlarmBoundaryMahoujinTileEntity)te).detected
         ? 15
         : super.getAnalogOutputSignal(blockState, worldIn, pos, direction);
   }
}
