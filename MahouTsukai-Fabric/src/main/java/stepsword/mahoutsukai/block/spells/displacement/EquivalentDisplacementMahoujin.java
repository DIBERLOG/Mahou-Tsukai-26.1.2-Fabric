package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.EquivalentDisplacementMahoujinTileEntity;

public class EquivalentDisplacementMahoujin extends MahoujinBlockTileEntity<EquivalentDisplacementMahoujinTileEntity> {
   public EquivalentDisplacementMahoujin() {
      super("mahoujin_equivalent_displacement");
   }

   @Override
   public Class<EquivalentDisplacementMahoujinTileEntity> getTileEntityClass() {
      return EquivalentDisplacementMahoujinTileEntity.class;
   }

   @Nullable
   public EquivalentDisplacementMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new EquivalentDisplacementMahoujinTileEntity(blockPos, blockState);
   }

   protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
      if (world.getBlockEntity(pos) instanceof EquivalentDisplacementMahoujinTileEntity te) {
         te.releaseChunks();
      }

      super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
      return (a, b, c, tile) -> {
         if (tile instanceof EquivalentDisplacementMahoujinTileEntity te) {
            te.tick(a, b, c, (EquivalentDisplacementMahoujinTileEntity)tile);
         }
      };
   }
}
