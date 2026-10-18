package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.ReversionEyesMahoujinTileEntity;

public class ReversionEyesMahoujin extends SingleUseMahoujinBlockTileEntity<ReversionEyesMahoujinTileEntity> {
   public ReversionEyesMahoujin() {
      super("mahoujin_reversion_eyes");
   }

   @Override
   public Class<ReversionEyesMahoujinTileEntity> getTileEntityClass() {
      return ReversionEyesMahoujinTileEntity.class;
   }

   @Nullable
   public ReversionEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ReversionEyesMahoujinTileEntity(blockPos, blockState);
   }
}
