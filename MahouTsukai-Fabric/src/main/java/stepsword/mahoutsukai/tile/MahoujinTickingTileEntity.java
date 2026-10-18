package stepsword.mahoutsukai.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MahoujinTickingTileEntity extends MahoujinTileEntity implements BlockEntityTicker<MahoujinTickingTileEntity> {
   public int tickCounter = 0;

   public MahoujinTickingTileEntity(BlockPos p, BlockState bs) {
      super(p, bs);
   }

   public MahoujinTickingTileEntity(BlockEntityType type, BlockPos p, BlockState bs) {
      super(type, p, bs);
   }

   public int blockCycle() {
      return 1;
   }

   public void tick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (!level.isClientSide()) {
         if (blockEntity instanceof AboveChecker ac) {
            ac.tickAC(level, blockEntity);
         }

         if (this.tickCounter >= this.blockCycle()) {
            this.mahoujinTick(level, blockPos, blockState, blockEntity);
            this.tickCounter = 0;
         }

         this.tickCounter++;
      } else {
         this.mahoujinClientTick(level, blockPos, blockState, blockEntity);
      }
   }

   public void mahoujinTick(Level level, BlockPos pos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
   }

   public void mahoujinClientTick(Level level, BlockPos pos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
   }
}
