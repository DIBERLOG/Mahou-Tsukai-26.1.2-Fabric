package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.PresenceConcealmentMahoujinTileEntity;

public class PresenceConcealmentMahoujin extends SingleUseMahoujinBlockTileEntity<PresenceConcealmentMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public PresenceConcealmentMahoujin() {
      super("mahoujin_presence_concealment");
   }

   @Override
   public Class<PresenceConcealmentMahoujinTileEntity> getTileEntityClass() {
      return PresenceConcealmentMahoujinTileEntity.class;
   }

   @Nullable
   public PresenceConcealmentMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new PresenceConcealmentMahoujinTileEntity(blockPos, blockState);
   }
}
