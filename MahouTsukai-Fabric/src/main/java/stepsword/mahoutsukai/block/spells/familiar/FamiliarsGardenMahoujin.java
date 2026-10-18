package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.FamiliarsGardenMahoujinTileEntity;

public class FamiliarsGardenMahoujin extends SingleUseMahoujinBlockTileEntity<FamiliarsGardenMahoujinTileEntity> {
   public FamiliarsGardenMahoujin() {
      super("mahoujin_familiars_garden");
   }

   @Override
   public Class<FamiliarsGardenMahoujinTileEntity> getTileEntityClass() {
      return FamiliarsGardenMahoujinTileEntity.class;
   }

   @Nullable
   public FamiliarsGardenMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new FamiliarsGardenMahoujinTileEntity(blockPos, blockState);
   }
}
