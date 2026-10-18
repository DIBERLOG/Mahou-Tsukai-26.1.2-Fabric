package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.ProjectionMahoujinTileEntity;

public class ProjectionMahoujin extends SingleUseMahoujinBlockTileEntity<ProjectionMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public ProjectionMahoujin() {
      super("mahoujin_projection");
   }

   @Override
   public Class<ProjectionMahoujinTileEntity> getTileEntityClass() {
      return ProjectionMahoujinTileEntity.class;
   }

   @Nullable
   public ProjectionMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ProjectionMahoujinTileEntity(blockPos, blockState);
   }
}
