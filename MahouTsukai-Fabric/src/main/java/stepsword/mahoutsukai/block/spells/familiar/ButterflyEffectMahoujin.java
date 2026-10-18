package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.ButterflyEffectMahoujinTileEntity;

public class ButterflyEffectMahoujin extends SingleUseMahoujinBlockTileEntity<ButterflyEffectMahoujinTileEntity> {
   public ButterflyEffectMahoujin() {
      super("mahoujin_butterfly_effect");
   }

   @Override
   public Class<ButterflyEffectMahoujinTileEntity> getTileEntityClass() {
      return ButterflyEffectMahoujinTileEntity.class;
   }

   @Nullable
   public ButterflyEffectMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ButterflyEffectMahoujinTileEntity(blockPos, blockState);
   }
}
