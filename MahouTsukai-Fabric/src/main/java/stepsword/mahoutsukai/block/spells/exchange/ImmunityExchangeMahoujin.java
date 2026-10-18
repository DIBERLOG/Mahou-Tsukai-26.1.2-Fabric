package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.ImmunityExchangeMahoujinTileEntity;

public class ImmunityExchangeMahoujin extends SingleUseMahoujinBlockTileEntity<ImmunityExchangeMahoujinTileEntity> {
   public ImmunityExchangeMahoujin() {
      super("mahoujin_immunity_exchange");
   }

   @Override
   public Class<ImmunityExchangeMahoujinTileEntity> getTileEntityClass() {
      return ImmunityExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public ImmunityExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ImmunityExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
