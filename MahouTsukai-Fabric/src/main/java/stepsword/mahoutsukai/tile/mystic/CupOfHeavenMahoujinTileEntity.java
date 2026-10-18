package stepsword.mahoutsukai.tile.mystic;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.ScrollGiver;
import stepsword.mahoutsukai.util.NodeNetworkUtil;

public class CupOfHeavenMahoujinTileEntity extends MahoujinTileEntity implements ScrollGiver {
   private int nearby = 1;
   private double random = 0.0;

   public CupOfHeavenMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.cupOfHeaven.get(), p, bs);
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.cupOfHeavenScroll.get());
   }

   public void buildNetwork(UUID uuid, Consumer<HashSet<BlockPos>> callback) {
      if (uuid != null) {
         int distance = MTConfig.HEAVENS_CUP_NETWORK_DISTANCE;
         NodeNetworkUtil.buildNetworkIterative(
            uuid,
            null,
            null,
            this.worldPosition,
            this.level,
            distance,
            MTConfig.HEAVENS_CUP_BATCH_SIZE,
            a -> a instanceof CupOfHeavenMahoujinTileEntity && uuid.equals(((CupOfHeavenMahoujinTileEntity)a).getCasterUUID()),
            callback
         );
      }
   }
}
