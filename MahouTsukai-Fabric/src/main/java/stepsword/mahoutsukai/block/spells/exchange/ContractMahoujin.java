package stepsword.mahoutsukai.block.spells.exchange;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;

public class ContractMahoujin extends MahoujinBlockTileEntity<ContractMahoujinTileEntity> {
   public ContractMahoujin() {
      super("mahoujin_contract");
   }

   @Override
   public Class<ContractMahoujinTileEntity> getTileEntityClass() {
      return ContractMahoujinTileEntity.class;
   }

   @Nullable
   public ContractMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ContractMahoujinTileEntity(blockPos, blockState);
   }
}
