package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity;

public class AlchemicalExchangeMahoujin extends MahoujinBlockTileEntity<AlchemicalExchangeMahoujinTileEntity> {
   public AlchemicalExchangeMahoujin() {
      super("mahoujin_alchemical_exchange");
   }

   @Override
   public Class<AlchemicalExchangeMahoujinTileEntity> getTileEntityClass() {
      return AlchemicalExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public AlchemicalExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new AlchemicalExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
