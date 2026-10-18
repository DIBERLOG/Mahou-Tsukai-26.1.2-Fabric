package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.RhoAiasMahoujinTileEntity;

public class RhoAiasMahoujin extends SingleUseMahoujinBlockTileEntity<RhoAiasMahoujinTileEntity> {
   public RhoAiasMahoujin() {
      super("mahoujin_rho_aias");
   }

   @Override
   public Class<RhoAiasMahoujinTileEntity> getTileEntityClass() {
      return RhoAiasMahoujinTileEntity.class;
   }

   @Nullable
   public RhoAiasMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new RhoAiasMahoujinTileEntity(blockPos, blockState);
   }
}
