package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.ClairvoyanceEyesMahoujinTileEntity;

public class ClairvoyanceEyesMahoujin extends SingleUseMahoujinBlockTileEntity<ClairvoyanceEyesMahoujinTileEntity> {
   public ClairvoyanceEyesMahoujin() {
      super("mahoujin_prediction");
   }

   @Override
   public Class<ClairvoyanceEyesMahoujinTileEntity> getTileEntityClass() {
      return ClairvoyanceEyesMahoujinTileEntity.class;
   }

   @Nullable
   public ClairvoyanceEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ClairvoyanceEyesMahoujinTileEntity(blockPos, blockState);
   }
}
