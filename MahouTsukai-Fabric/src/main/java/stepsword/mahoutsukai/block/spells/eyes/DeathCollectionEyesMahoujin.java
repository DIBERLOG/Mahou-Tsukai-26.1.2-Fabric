package stepsword.mahoutsukai.block.spells.eyes;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.eyes.DeathCollectionEyesMahoujinTileEntity;

public class DeathCollectionEyesMahoujin extends SingleUseMahoujinBlockTileEntity<DeathCollectionEyesMahoujinTileEntity> {
   public DeathCollectionEyesMahoujin() {
      super("mahoujin_death_collection");
   }

   @Override
   public Class<DeathCollectionEyesMahoujinTileEntity> getTileEntityClass() {
      return DeathCollectionEyesMahoujinTileEntity.class;
   }

   @Nullable
   public DeathCollectionEyesMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DeathCollectionEyesMahoujinTileEntity(blockPos, blockState);
   }
}
