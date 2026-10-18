package stepsword.mahoutsukai.tile.circuits;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.tile.ModTileEntities;

public class ManaCircuitMagitechTileEntity extends ManaCircuitTileEntity {
   public ManaCircuitMagitechTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<? extends ManaCircuitTileEntity>)ModTileEntities.circuitMagitech.get(), p, bs);
   }

   @Override
   public int getMaxMana() {
      return MTConfig.MANA_CIRCUIT_MAGITECH_CAPACITY;
   }
}
