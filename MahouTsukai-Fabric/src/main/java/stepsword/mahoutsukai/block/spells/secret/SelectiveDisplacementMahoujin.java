package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.SelectiveDisplacementMahoujinTileEntity;

public class SelectiveDisplacementMahoujin extends SingleUseMahoujinBlockTileEntity<SelectiveDisplacementMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public SelectiveDisplacementMahoujin() {
      super("mahoujin_selective_displacement");
   }

   @Override
   public Class<SelectiveDisplacementMahoujinTileEntity> getTileEntityClass() {
      return SelectiveDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public SelectiveDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new SelectiveDisplacementMahoujinTileEntity(blockPos, blockState);
   }
}
