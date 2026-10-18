package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.DurabilityExchangeMahoujinTileEntity;

public class DurabilityExchangeMahoujin extends MahoujinBlockTileEntity<DurabilityExchangeMahoujinTileEntity> {
   public DurabilityExchangeMahoujin() {
      super("mahoujin_durability_exchange");
   }

   @Override
   public Class<DurabilityExchangeMahoujinTileEntity> getTileEntityClass() {
      return DurabilityExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public DurabilityExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DurabilityExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
