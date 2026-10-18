package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.SwapFamiliarMahoujinTileEntity;

public class SwapFamiliarMahoujin extends SingleUseMahoujinBlockTileEntity<SwapFamiliarMahoujinTileEntity> {
   public SwapFamiliarMahoujin() {
      super("mahoujin_swap_familiar");
   }

   @Override
   public Class<SwapFamiliarMahoujinTileEntity> getTileEntityClass() {
      return SwapFamiliarMahoujinTileEntity.class;
   }

   @Nullable
   public SwapFamiliarMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new SwapFamiliarMahoujinTileEntity(blockPos, blockState);
   }
}
