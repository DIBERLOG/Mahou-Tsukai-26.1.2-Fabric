package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.CupOfHeavenMahoujinTileEntity;

public class CupOfHeavenMahoujin extends SingleUseMahoujinBlockTileEntity<CupOfHeavenMahoujinTileEntity> {
   public CupOfHeavenMahoujin() {
      super("mahoujin_cup_of_heaven");
   }

   @Override
   public Class<CupOfHeavenMahoujinTileEntity> getTileEntityClass() {
      return CupOfHeavenMahoujinTileEntity.class;
   }

   @Nullable
   public CupOfHeavenMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new CupOfHeavenMahoujinTileEntity(blockPos, blockState);
   }
}
