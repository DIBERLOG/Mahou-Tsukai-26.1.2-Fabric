package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.RecallFamiliarMahoujinTileEntity;

public class RecallFamiliarMahoujin extends SingleUseMahoujinBlockTileEntity<RecallFamiliarMahoujinTileEntity> {
   public RecallFamiliarMahoujin() {
      super("mahoujin_recall_familiar");
   }

   @Override
   public Class<RecallFamiliarMahoujinTileEntity> getTileEntityClass() {
      return RecallFamiliarMahoujinTileEntity.class;
   }

   @Nullable
   public RecallFamiliarMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new RecallFamiliarMahoujinTileEntity(blockPos, blockState);
   }
}
