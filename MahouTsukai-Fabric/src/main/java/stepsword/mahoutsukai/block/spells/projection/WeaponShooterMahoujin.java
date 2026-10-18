package stepsword.mahoutsukai.block.spells.projection;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.tile.projection.WeaponShooterMahoujinTileEntity;

public class WeaponShooterMahoujin extends SingleUseMahoujinBlockTileEntity<WeaponShooterMahoujinTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public WeaponShooterMahoujin() {
      super("mahoujin_weapon_shooter");
   }

   @Override
   public Class<WeaponShooterMahoujinTileEntity> getTileEntityClass() {
      return WeaponShooterMahoujinTileEntity.class;
   }

   @Nullable
   public WeaponShooterMahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new WeaponShooterMahoujinTileEntity(blockPos, blockState);
   }
}
