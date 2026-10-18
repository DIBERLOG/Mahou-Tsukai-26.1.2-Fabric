package stepsword.mahoutsukai.block.spells;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.tile.boundary.BoundaryMahoujinTileEntity;

public class BoundaryBlockTileEntity<T extends BoundaryMahoujinTileEntity> extends SingleUseMahoujinBlockTileEntity<T> {
   public BoundaryBlockTileEntity(String name) {
      super(name);
   }

   @Override
   public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult hit) {
      if (!worldIn.isClientSide() && this.getTileEntity(worldIn, pos) instanceof BoundaryMahoujinTileEntity mte) {
         if (mte.hasCloth()) {
            return super.useWithoutItem(state, worldIn, pos, playerIn, hit);
         }

         mte.toggle(playerIn);
      }

      return InteractionResult.SUCCESS;
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
      BlockEntity te = worldIn.getBlockEntity(pos);
      if (te instanceof BoundaryMahoujinTileEntity) {
         return ((BoundaryMahoujinTileEntity)te).shouldIPerformBarrier() ? 1 : 0;
      } else {
         return 0;
      }
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
      return (a, b, c, tile) -> {
         if (tile instanceof BoundaryMahoujinTileEntity) {
            ((BoundaryMahoujinTileEntity)tile).tick(a, b, c, (BoundaryMahoujinTileEntity)tile);
         }
      };
   }
}
