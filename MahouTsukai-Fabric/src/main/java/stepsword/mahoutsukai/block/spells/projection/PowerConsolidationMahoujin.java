package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.PowerConsolidationMahoujinTileEntity;

public class PowerConsolidationMahoujin extends MahoujinBlockTileEntity<PowerConsolidationMahoujinTileEntity> {
   public PowerConsolidationMahoujin() {
      super("mahoujin_power_consolidation");
   }

   @Override
   public Class<PowerConsolidationMahoujinTileEntity> getTileEntityClass() {
      return PowerConsolidationMahoujinTileEntity.class;
   }

   @Nullable
   public PowerConsolidationMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new PowerConsolidationMahoujinTileEntity(blockPos, blockState);
   }
}
