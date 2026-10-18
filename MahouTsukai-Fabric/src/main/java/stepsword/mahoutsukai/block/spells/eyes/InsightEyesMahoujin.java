package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.InsightEyesMahoujinTileEntity;

public class InsightEyesMahoujin extends SingleUseMahoujinBlockTileEntity<InsightEyesMahoujinTileEntity> {
   public InsightEyesMahoujin() {
      super("mahoujin_insight");
   }

   @Override
   public Class<InsightEyesMahoujinTileEntity> getTileEntityClass() {
      return InsightEyesMahoujinTileEntity.class;
   }

   @Nullable
   public InsightEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new InsightEyesMahoujinTileEntity(blockPos, blockState);
   }
}
