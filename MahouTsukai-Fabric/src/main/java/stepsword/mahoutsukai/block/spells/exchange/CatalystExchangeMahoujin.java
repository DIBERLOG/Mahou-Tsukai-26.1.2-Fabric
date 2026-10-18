package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.CatalystExchangeMahoujinTileEntity;

public class CatalystExchangeMahoujin extends MahoujinBlockTileEntity<CatalystExchangeMahoujinTileEntity> {
   public CatalystExchangeMahoujin() {
      super("mahoujin_catalyst_exchange");
   }

   @Override
   public Class<CatalystExchangeMahoujinTileEntity> getTileEntityClass() {
      return CatalystExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public CatalystExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new CatalystExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
