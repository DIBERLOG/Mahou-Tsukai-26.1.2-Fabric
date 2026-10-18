package stepsword.mahoutsukai.block;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import stepsword.mahoutsukai.tile.TickingTileEntity;

public abstract class BlockTileEntity<TE extends BlockEntity> extends BlockBase implements EntityBlock {
   public BlockTileEntity(Properties material, String name) {
      super(material, name);
   }

   public abstract Class<TE> getTileEntityClass();

   public TE getTileEntity(Level world, BlockPos pos) {
      return (TE)world.getBlockEntity(pos);
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TE createTileEntity(BlockPos pos, BlockState bs, Level world) {
      return (TE)this.newBlockEntity(pos, bs);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
      return (a, b, c, tile) -> {
         if (tile instanceof TickingTileEntity tte) {
            ((TickingTileEntity)tile).tick(a, b, c, tte);
         }
      };
   }
}

