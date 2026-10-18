package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.DamageExchangeMahoujinTileEntity;

public class DamageExchangeMahoujin extends SingleUseMahoujinBlockTileEntity<DamageExchangeMahoujinTileEntity> {
   public DamageExchangeMahoujin() {
      super("mahoujin_damage_exchange");
   }

   @Override
   public Class<DamageExchangeMahoujinTileEntity> getTileEntityClass() {
      return DamageExchangeMahoujinTileEntity.class;
   }

   @Nullable
   public DamageExchangeMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DamageExchangeMahoujinTileEntity(blockPos, blockState);
   }
}
