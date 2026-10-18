package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.MentalDisplacementMahoujinTileEntity;

public class MentalDisplacementMahoujin extends SingleUseMahoujinBlockTileEntity<MentalDisplacementMahoujinTileEntity> {
   public MentalDisplacementMahoujin() {
      super("mahoujin_mental_displacement");
   }

   @Override
   public Class<MentalDisplacementMahoujinTileEntity> getTileEntityClass() {
      return MentalDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public MentalDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MentalDisplacementMahoujinTileEntity(blockPos, blockState);
   }
}
