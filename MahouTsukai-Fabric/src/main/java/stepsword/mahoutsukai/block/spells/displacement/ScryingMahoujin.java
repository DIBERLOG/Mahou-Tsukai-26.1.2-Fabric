package stepsword.mahoutsukai.block.spells.displacement;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.displacement.ScryingMahoujinTileEntity;

public class ScryingMahoujin extends MahoujinBlockTileEntity<ScryingMahoujinTileEntity> {
   public ScryingMahoujin() {
      super("mahoujin_scrying");
   }

   @Override
   public Class<ScryingMahoujinTileEntity> getTileEntityClass() {
      return ScryingMahoujinTileEntity.class;
   }

   @Nullable
   public ScryingMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ScryingMahoujinTileEntity(blockPos, blockState);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
      return (a, b, c, tile) -> {
         if (tile instanceof ScryingMahoujinTileEntity) {
            ((ScryingMahoujinTileEntity)tile).tick(a, b, c, (ScryingMahoujinTileEntity)tile);
         }
      };
   }
}
