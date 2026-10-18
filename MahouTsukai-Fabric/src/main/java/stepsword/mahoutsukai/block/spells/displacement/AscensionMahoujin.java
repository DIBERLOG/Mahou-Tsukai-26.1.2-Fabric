package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.AscensionMahoujinTileEntity;

public class AscensionMahoujin extends SingleUseMahoujinBlockTileEntity<AscensionMahoujinTileEntity> {
   public AscensionMahoujin() {
      super("mahoujin_ascension");
   }

   @Override
   public Class<AscensionMahoujinTileEntity> getTileEntityClass() {
      return AscensionMahoujinTileEntity.class;
   }

   @Nullable
   public AscensionMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new AscensionMahoujinTileEntity(blockPos, blockState);
   }
}
