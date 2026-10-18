package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.FaySightEyesMahoujinTileEntity;

public class FaySightEyesMahoujin extends SingleUseMahoujinBlockTileEntity<FaySightEyesMahoujinTileEntity> {
   public FaySightEyesMahoujin() {
      super("mahoujin_fay_sight");
   }

   @Override
   public Class<FaySightEyesMahoujinTileEntity> getTileEntityClass() {
      return FaySightEyesMahoujinTileEntity.class;
   }

   @Nullable
   public FaySightEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new FaySightEyesMahoujinTileEntity(blockPos, blockState);
   }
}
