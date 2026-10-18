package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.ProtectiveDisplacementMahoujinTileEntity;

public class ProtectiveDisplacementMahoujin extends SingleUseMahoujinBlockTileEntity<ProtectiveDisplacementMahoujinTileEntity> {
   public ProtectiveDisplacementMahoujin() {
      super("mahoujin_protective_displacement");
   }

   @Override
   public Class<ProtectiveDisplacementMahoujinTileEntity> getTileEntityClass() {
      return ProtectiveDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public ProtectiveDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ProtectiveDisplacementMahoujinTileEntity(blockPos, blockState);
   }
}
