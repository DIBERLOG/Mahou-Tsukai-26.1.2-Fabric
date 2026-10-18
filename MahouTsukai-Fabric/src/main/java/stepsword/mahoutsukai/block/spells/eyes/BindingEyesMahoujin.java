package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.BindingEyesMahoujinTileEntity;

public class BindingEyesMahoujin extends SingleUseMahoujinBlockTileEntity<BindingEyesMahoujinTileEntity> {
   public BindingEyesMahoujin() {
      super("mahoujin_mystic_eyes");
   }

   @Override
   public Class<BindingEyesMahoujinTileEntity> getTileEntityClass() {
      return BindingEyesMahoujinTileEntity.class;
   }

   @Nullable
   public BindingEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new BindingEyesMahoujinTileEntity(blockPos, blockState);
   }
}
