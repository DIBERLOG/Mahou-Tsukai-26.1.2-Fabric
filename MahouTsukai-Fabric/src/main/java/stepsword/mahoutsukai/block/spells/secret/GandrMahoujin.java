package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.GandrMahoujinTileEntity;

public class GandrMahoujin extends SingleUseMahoujinBlockTileEntity<GandrMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public GandrMahoujin() {
      super("mahoujin_gandr");
   }

   @Override
   public Class<GandrMahoujinTileEntity> getTileEntityClass() {
      return GandrMahoujinTileEntity.class;
   }

   @Nullable
   public GandrMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new GandrMahoujinTileEntity(blockPos, blockState);
   }
}
