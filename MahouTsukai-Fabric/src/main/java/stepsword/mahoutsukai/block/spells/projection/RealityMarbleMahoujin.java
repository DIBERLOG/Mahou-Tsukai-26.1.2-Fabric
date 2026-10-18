package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.RealityMarbleMahoujinTileEntity;

public class RealityMarbleMahoujin extends SingleUseMahoujinBlockTileEntity<RealityMarbleMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public RealityMarbleMahoujin() {
      super("mahoujin_reality_marble");
   }

   @Override
   public Class<RealityMarbleMahoujinTileEntity> getTileEntityClass() {
      return RealityMarbleMahoujinTileEntity.class;
   }

   @Nullable
   public RealityMarbleMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new RealityMarbleMahoujinTileEntity(blockPos, blockState);
   }
}
