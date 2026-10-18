package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.FallenDownMahoujinTileEntity;

public class FallenDownMahoujin extends SingleUseMahoujinBlockTileEntity<FallenDownMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public FallenDownMahoujin() {
      super("mahoujin_fallen_down");
   }

   @Override
   public Class<FallenDownMahoujinTileEntity> getTileEntityClass() {
      return FallenDownMahoujinTileEntity.class;
   }

   @Nullable
   public FallenDownMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new FallenDownMahoujinTileEntity(blockPos, blockState);
   }
}
