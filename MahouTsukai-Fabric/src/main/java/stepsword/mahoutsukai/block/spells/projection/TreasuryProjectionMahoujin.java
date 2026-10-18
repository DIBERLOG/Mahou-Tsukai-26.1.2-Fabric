package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.TreasuryProjectionMahoujinTileEntity;

public class TreasuryProjectionMahoujin extends SingleUseMahoujinBlockTileEntity<TreasuryProjectionMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public TreasuryProjectionMahoujin() {
      super("mahoujin_treasury_projection");
   }

   @Override
   public Class<TreasuryProjectionMahoujinTileEntity> getTileEntityClass() {
      return TreasuryProjectionMahoujinTileEntity.class;
   }

   @Nullable
   public TreasuryProjectionMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new TreasuryProjectionMahoujinTileEntity(blockPos, blockState);
   }
}
