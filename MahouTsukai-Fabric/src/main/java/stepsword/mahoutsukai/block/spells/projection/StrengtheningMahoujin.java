package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.StrengtheningMahoujinTileEntity;

public class StrengtheningMahoujin extends SingleUseMahoujinBlockTileEntity<StrengtheningMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public StrengtheningMahoujin() {
      super("mahoujin_strengthening");
   }

   @Override
   public Class<StrengtheningMahoujinTileEntity> getTileEntityClass() {
      return StrengtheningMahoujinTileEntity.class;
   }

   @Nullable
   public StrengtheningMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new StrengtheningMahoujinTileEntity(blockPos, blockState);
   }
}
