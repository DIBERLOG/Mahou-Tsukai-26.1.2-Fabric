package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.BlackFlameEyesMahoujinTileEntity;

public class BlackFlameEyesMahoujin extends SingleUseMahoujinBlockTileEntity<BlackFlameEyesMahoujinTileEntity> {
   public BlackFlameEyesMahoujin() {
      super("mahoujin_black_flame");
   }

   @Override
   public Class<BlackFlameEyesMahoujinTileEntity> getTileEntityClass() {
      return BlackFlameEyesMahoujinTileEntity.class;
   }

   @Nullable
   public BlackFlameEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new BlackFlameEyesMahoujinTileEntity(blockPos, blockState);
   }
}
