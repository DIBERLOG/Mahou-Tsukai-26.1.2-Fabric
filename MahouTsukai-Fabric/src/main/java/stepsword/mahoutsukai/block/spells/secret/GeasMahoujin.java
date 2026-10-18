package stepsword.mahoutsukai.block.spells.secret;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.secret.GeasMahoujinTileEntity;

public class GeasMahoujin extends SingleUseMahoujinBlockTileEntity<GeasMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public GeasMahoujin() {
      super("mahoujin_geas");
   }

   @Override
   public Class<GeasMahoujinTileEntity> getTileEntityClass() {
      return GeasMahoujinTileEntity.class;
   }

   @Nullable
   public GeasMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new GeasMahoujinTileEntity(blockPos, blockState);
   }
}
