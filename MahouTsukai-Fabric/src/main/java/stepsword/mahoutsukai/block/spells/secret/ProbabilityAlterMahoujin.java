package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity;

public class ProbabilityAlterMahoujin extends SingleUseMahoujinBlockTileEntity<ProbabilityAlterMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public ProbabilityAlterMahoujin() {
      super("mahoujin_probability_alter");
   }

   @Override
   public Class<ProbabilityAlterMahoujinTileEntity> getTileEntityClass() {
      return ProbabilityAlterMahoujinTileEntity.class;
   }

   @Nullable
   public ProbabilityAlterMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ProbabilityAlterMahoujinTileEntity(blockPos, blockState);
   }
}
