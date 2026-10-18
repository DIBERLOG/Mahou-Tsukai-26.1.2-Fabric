package stepsword.mahoutsukai.block.spells.mystic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.mystic.DamageReplicationMahoujinTileEntity;

public class DamageReplicationMahoujin extends SingleUseMahoujinBlockTileEntity<DamageReplicationMahoujinTileEntity> {
   public DamageReplicationMahoujin() {
      super("mahoujin_damage_replication");
   }

   @Override
   public Class<DamageReplicationMahoujinTileEntity> getTileEntityClass() {
      return DamageReplicationMahoujinTileEntity.class;
   }

   @Nullable
   public DamageReplicationMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new DamageReplicationMahoujinTileEntity(blockPos, blockState);
   }
}
