package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.BorrowedAuthorityMahoujinTileEntity;

public class BorrowedAuthorityMahoujin extends SingleUseMahoujinBlockTileEntity<BorrowedAuthorityMahoujinTileEntity> {
   public BorrowedAuthorityMahoujin() {
      super("mahoujin_borrowed_authority");
   }

   @Override
   public Class<BorrowedAuthorityMahoujinTileEntity> getTileEntityClass() {
      return BorrowedAuthorityMahoujinTileEntity.class;
   }

   @Nullable
   public BorrowedAuthorityMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new BorrowedAuthorityMahoujinTileEntity(blockPos, blockState);
   }
}
