package stepsword.mahoutsukai.tile.projection;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.fluids.MurkyWaterBlock;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class PowerConsolidationMahoujinTileEntity extends MahoujinTickingTileEntity {
   public static final String LAKED = "MAHOUTSUKAI_NUM_TIMES_LAKED";
   private int numTimesLaked = 0;

   public PowerConsolidationMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.powerConsolidation.get(), p, bs);
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      output.putInt("MAHOUTSUKAI_NUM_TIMES_LAKED", this.numTimesLaked);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.numTimesLaked = input.getIntOr("MAHOUTSUKAI_NUM_TIMES_LAKED", 0);
      super.loadAdditional(input);
   }

   @Override
   public int blockCycle() {
      return MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      int radius = MTConfig.POWER_CONSOLIDATION_LAKE_RADIUS;
      if (this.numTimesLaked >= radius - 1) {
         level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
      } else {
         boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
            this.worldPosition, level, this.getCasterUUID(), MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST
         );
         if (!nearbyBatteriesGaveMana
            && (
               this.getCaster() == null
                  || PlayerManaManager.drainMana(this.getCaster(), MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST, false, false)
                     != MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST
            )) {
            if (MTConfig.RHO_AIAS_KILL_DEBUG) {
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("Power consolidation is failing because of mana.");
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("More info for debugging.");
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("getCaster():" + this.getCaster());
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("getCasterUUID():" + this.getCasterUUID());
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("Required mana:" + MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST);
               if (this.getCaster() != null) {
                  org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("Caster mana:" + Utils.getPlayerMahou(this.getCaster()));
               }
            }
         } else {
            this.createLake();
            this.numTimesLaked++;
            this.setChanged();
         }
      }
   }

   public void createLake() {
      int r = this.numTimesLaked + 1;
      Stream<BlockPos> lst = BlockPos.betweenClosedStream(this.worldPosition.offset(-r - 1, -r - 1, -r - 1), this.worldPosition.offset(r + 1, r + 1, r + 1));
      lst.forEach(p -> {
         if (p.distSqr(this.worldPosition) < r * r + 1 && p.getY() < this.worldPosition.getY() && this.level.getBlockEntity(p) == null) {
            if (this.numTimesLaked * this.numTimesLaked - p.distSqr(this.worldPosition) <= 1.0) {
               EffectUtil.tryChangeBlockState(false, p, Blocks.DIRT.defaultBlockState(), this.level, this.getCaster());
            } else {
               EffectUtil.tryChangeBlockState(false, p, ((MurkyWaterBlock)ModBlocks.murkyFluid.get()).defaultBlockState(), this.level, this.getCaster());
            }
         }
      });
   }
}
