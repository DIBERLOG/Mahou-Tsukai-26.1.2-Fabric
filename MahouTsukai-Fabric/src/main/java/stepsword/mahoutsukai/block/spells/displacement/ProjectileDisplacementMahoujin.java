package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.ProjectileDisplacementMahoujinTileEntity;

public class ProjectileDisplacementMahoujin extends SingleUseMahoujinBlockTileEntity<ProjectileDisplacementMahoujinTileEntity> {
   public ProjectileDisplacementMahoujin() {
      super("mahoujin_projectile_displacement");
   }

   @Override
   public Class<ProjectileDisplacementMahoujinTileEntity> getTileEntityClass() {
      return ProjectileDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public ProjectileDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ProjectileDisplacementMahoujinTileEntity(blockPos, blockState);
   }
}
