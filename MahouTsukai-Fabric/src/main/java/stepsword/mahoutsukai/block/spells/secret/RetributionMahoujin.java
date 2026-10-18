package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.RetributionMahoujinTileEntity;

public class RetributionMahoujin extends SingleUseMahoujinBlockTileEntity<RetributionMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public RetributionMahoujin() {
      super("mahoujin_retribution");
   }

   @Override
   public Class<RetributionMahoujinTileEntity> getTileEntityClass() {
      return RetributionMahoujinTileEntity.class;
   }

   @Nullable
   public RetributionMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new RetributionMahoujinTileEntity(blockPos, blockState);
   }
}
