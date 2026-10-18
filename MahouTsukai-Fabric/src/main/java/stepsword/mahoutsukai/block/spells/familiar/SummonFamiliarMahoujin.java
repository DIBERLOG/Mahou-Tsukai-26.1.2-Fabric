package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.SummonFamiliarMahoujinTileEntity;

public class SummonFamiliarMahoujin extends SingleUseMahoujinBlockTileEntity<SummonFamiliarMahoujinTileEntity> {
   public SummonFamiliarMahoujin() {
      super("mahoujin_summon_familiar");
   }

   @Override
   public Class<SummonFamiliarMahoujinTileEntity> getTileEntityClass() {
      return SummonFamiliarMahoujinTileEntity.class;
   }

   @Nullable
   public SummonFamiliarMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new SummonFamiliarMahoujinTileEntity(blockPos, blockState);
   }
}
