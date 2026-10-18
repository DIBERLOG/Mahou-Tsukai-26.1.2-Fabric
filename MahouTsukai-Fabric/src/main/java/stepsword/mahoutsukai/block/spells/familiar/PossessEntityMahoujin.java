package stepsword.mahoutsukai.block.spells.familiar;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.familiar.PossessEntityMahoujinTileEntity;

public class PossessEntityMahoujin extends SingleUseMahoujinBlockTileEntity<PossessEntityMahoujinTileEntity> {
   public PossessEntityMahoujin() {
      super("mahoujin_possess_entity");
   }

   @Override
   public Class<PossessEntityMahoujinTileEntity> getTileEntityClass() {
      return PossessEntityMahoujinTileEntity.class;
   }

   @Nullable
   public PossessEntityMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new PossessEntityMahoujinTileEntity(blockPos, blockState);
   }
}
