package stepsword.mahoutsukai.tile.boundary;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.ScrollGiver;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RaiseEnclosureBarrierMahoujinTileEntity extends MahoujinTickingTileEntity implements ScrollGiver {
   public static final String RAISED = "MAHOUTSUKAI_NUM_TIMES_RAISED";
   private int numTimesRaised = 0;

   public RaiseEnclosureBarrierMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.raiseEnclosureBoundary.get(), p, bs);
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      output.putInt("MAHOUTSUKAI_NUM_TIMES_RAISED", this.numTimesRaised);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.numTimesRaised = input.getIntOr("MAHOUTSUKAI_NUM_TIMES_RAISED", 0);
      super.loadAdditional(input);
   }

   @Override
   public int blockCycle() {
      return MTConfig.ENCLOSURE_BARRIER_CYCLE;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (this.numTimesRaised >= MTConfig.ENCLOSURE_BARRIER_RADIUS - 1) {
         EffectUtil.tryChangeBlockState(true, this.worldPosition, Blocks.AIR.defaultBlockState(), level, this.getCaster());
      } else {
         boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
            this.worldPosition, level, this.getCasterUUID(), MTConfig.ENCLOSURE_BARRIER_MANA_COST
         );
         if (nearbyBatteriesGaveMana
            || this.getCaster() != null
               && PlayerManaManager.drainMana(this.getCaster(), MTConfig.ENCLOSURE_BARRIER_MANA_COST, false, true) == MTConfig.ENCLOSURE_BARRIER_MANA_COST) {
            this.raiseEnclosure();
            this.numTimesRaised++;
         }
      }
   }

   public void raiseEnclosure() {
      int bottom = MTConfig.ENCLOSURE_BARRIER_RADIUS - this.numTimesRaised - 1;
      Stream<BlockPos> box = BlockPos.betweenClosedStream(
         this.worldPosition.offset(-MTConfig.ENCLOSURE_BARRIER_RADIUS, -bottom, -MTConfig.ENCLOSURE_BARRIER_RADIUS),
         this.worldPosition.offset(MTConfig.ENCLOSURE_BARRIER_RADIUS, -bottom, MTConfig.ENCLOSURE_BARRIER_RADIUS)
      );
      box.forEach(
         tmp -> {
            if (tmp.getY() == this.worldPosition.getY() - bottom
               && (
                  tmp.getX() == this.worldPosition.getX() - MTConfig.ENCLOSURE_BARRIER_RADIUS
                     || tmp.getX() == this.worldPosition.getX() + MTConfig.ENCLOSURE_BARRIER_RADIUS
                     || tmp.getZ() == this.worldPosition.getZ() - MTConfig.ENCLOSURE_BARRIER_RADIUS
                     || tmp.getZ() == this.worldPosition.getZ() + MTConfig.ENCLOSURE_BARRIER_RADIUS
               )) {
               int k = this.checkValidColumn(tmp);
               if (k > 0) {
                  for (int i = k; i > 0; i--) {
                     BlockPos p = tmp.offset(0, i, 0);
                     EffectUtil.tryChangeBlockState(false, p, this.level.getBlockState(p.offset(0, -1, 0)), this.level, this.getCaster());
                  }

                  EffectUtil.tryChangeBlockState(false, tmp, Blocks.AIR.defaultBlockState(), this.level, this.getCaster());
               }
            }
         }
      );
   }

   public int checkValidColumn(BlockPos p) {
      for (int k = this.level.getMinY(); k < this.level.getMaxY() - p.getY() - 1; k++) {
         if (Utils.isBlockAir(this.level, p.offset(0, k, 0)) && k >= MTConfig.ENCLOSURE_BARRIER_RADIUS - 1) {
            return k;
         }

         if (this.level.getBlockEntity(p.offset(0, k, 0)) != null) {
            return -1;
         }

         if (EffectUtil.unchangableBlock(this.level.getBlockState(p.offset(0, k, 0)).getBlock())) {
            return -1;
         }
      }

      return -1;
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_raise_enclosure"));
   }
}
