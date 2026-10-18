package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.MysticStaffMahoujinTileEntity;

public class MysticStaffMahoujin extends SingleUseMahoujinBlockTileEntity<MysticStaffMahoujinTileEntity> {
   public MysticStaffMahoujin() {
      super("mahoujin_mystic_staff");
   }

   @Override
   public Class<MysticStaffMahoujinTileEntity> getTileEntityClass() {
      return MysticStaffMahoujinTileEntity.class;
   }

   @Nullable
   public MysticStaffMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MysticStaffMahoujinTileEntity(blockPos, blockState);
   }
}
