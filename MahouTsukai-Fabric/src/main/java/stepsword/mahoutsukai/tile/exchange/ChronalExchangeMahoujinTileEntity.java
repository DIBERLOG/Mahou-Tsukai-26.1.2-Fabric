package stepsword.mahoutsukai.tile.exchange;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.Utils;

public class ChronalExchangeMahoujinTileEntity extends MahoujinTickingTileEntity {
   private long dayCounter = 0L;
   private static final String DAY_COUNTER_TAG = "MAHOUTSUKAI_DAY_COUNTER_TAG";

   public ChronalExchangeMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.chronalExchange.get(), p, bs);
   }

   @Override
   public void tick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      super.tick(level, blockPos, blockState, blockEntity);
      if (!level.isClientSide()) {
         this.dayCounter++;
         this.dayCounter %= 24000L;
         if (this.dayCounter < 0L) {
            this.dayCounter += 24000L;
         }
      }
   }

   @Override
   public int blockCycle() {
      return MTConfig.CHRONAL_EXCHANGE_BLOCK_CYCLE;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (ActiveCircleConfig.tryToOperate(this, this.getCasterUUID())) {
         this.doManaGenOrLoss();
      }
   }

   public void setTime(long t) {
      this.dayCounter = t;
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      output.putLong("MAHOUTSUKAI_DAY_COUNTER_TAG", this.dayCounter);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.dayCounter = input.getLongOr("MAHOUTSUKAI_DAY_COUNTER_TAG", 0L);
      super.loadAdditional(input);
   }

   public void doManaGenOrLoss() {
      Player player = this.getCaster();
      if (player != null && this.level != null) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            int restore = this.calculateMana();
            if (restore < 0) {
               PlayerManaManager.drainMana(player, -restore, false, false, false, true);
            } else {
               int storedMana = mahou.getStoredMana();
               mahou.setStoredMana(Math.min(storedMana + restore, mahou.getMaxMana()));
            }
         }
      }
   }

   public int calculateMana() {
      return this.getManaFromTime(this.level.getOverworldClockTime());
   }

   public int getManaFromTime(long t) {
      float smaller = -MTConfig.CHRONAL_EXCHANGE_MANA_GAIN_LOSS;
      float diff = 2 * MTConfig.CHRONAL_EXCHANGE_MANA_GAIN_LOSS;
      long now = t % 24000L;
      if (now < 0L) {
         now += 24000L;
      }

      float distance = (float)(this.dayCounter % 24000L);
      if (distance < 0.0F) {
         distance += 24000.0F;
      }

      float ratio = Math.abs(distance - 12000.0F) / 12000.0F;
      return (int)(smaller + diff * ratio);
   }

   public boolean timeInRange(long x, long y) {
      return x < y + 6000L && x >= y - 6000L;
   }
}
