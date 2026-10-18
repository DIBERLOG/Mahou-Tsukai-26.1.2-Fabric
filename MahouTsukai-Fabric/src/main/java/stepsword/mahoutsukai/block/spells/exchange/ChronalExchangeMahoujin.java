package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity;

public class ChronalExchangeMahoujin extends MahoujinBlockTileEntity<ChronalExchangeMahoujinTileEntity> {
   public ChronalExchangeMahoujin() {
      super("mahoujin_chronal_exchange");
   }

   @Override
   public Class<ChronalExchangeMahoujinTileEntity> getTileEntityClass() {
      return ChronalExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public ChronalExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ChronalExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
