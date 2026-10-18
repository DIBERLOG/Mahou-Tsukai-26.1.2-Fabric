package stepsword.mahoutsukai.tile.boundary;

import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;

public class AlarmBoundaryMahoujinTileEntity extends BoundaryMahoujinTileEntity implements AboveChecker {
   public boolean detected = false;
   public String alarmName = null;

   public AlarmBoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.alarmBoundary.get(), p, bs);
   }

   @Override
   public int getManaCost() {
      return MTConfig.ALARM_BARRIER_MANA_COST;
   }

   @Override
   public int getManaCycle() {
      return MTConfig.ALARM_BARRIER_MANA_CYCLE;
   }

   @Override
   public int getBarrierCycle() {
      return MTConfig.ALARM_BARRIER_CYCLE;
   }

   @Override
   public int getBarrierRadius() {
      return MTConfig.ALARM_BARRIER_RADIUS;
   }

   public String getAlarmName() {
      return this.alarmName;
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      if (this.getAlarmName() != null) {
         output.putString("alarm_name", this.alarmName);
      }

      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.alarmName = (String)input.getString("alarm_name").orElse(null);
      super.loadAdditional(input);
   }

   @Override
   public void doBarrier(List<LivingEntity> entities) {
      Player caster = this.getCaster();
      this.detected = false;
      if (this.entitiesInBarrier != null && caster != null) {
         for (LivingEntity e : entities) {
            if (!ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), e)) {
               this.detected = true;
            }

            if (!this.entitiesInBarrier.contains(e.getUUID())) {
               this.soundAlarm(e, caster);
            }
         }
      }

      this.sendUpdates();
   }

   public void soundAlarm(LivingEntity entityIn, Player caster) {
      if (caster != null) {
         String alarmName = this.getAlarmName();
         if (alarmName == null) {
            alarmName = "";
         } else {
            alarmName = alarmName + ": ";
         }

         if (entityIn instanceof Player && !ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), entityIn)) {
            if (MTConfig.ALARM_ACTION_BAR_MESSAGES) {
               caster.sendOverlayMessage(
                  Component.literal(alarmName + entityIn.getDisplayName().getString() + " has entered your boundary.").withStyle(ChatFormatting.RED)
               );
            } else {
               caster.sendSystemMessage(
                  Component.literal(alarmName + entityIn.getDisplayName().getString() + " has entered your boundary.").withStyle(ChatFormatting.RED)
               );
            }
         } else if (!ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), entityIn)) {
            if (MTConfig.ALARM_ACTION_BAR_MESSAGES) {
               caster.sendOverlayMessage(
                  Component.literal(alarmName + entityIn.getDisplayName().getString() + " has entered your boundary.").withStyle(ChatFormatting.RED)
               );
            } else {
               caster.sendSystemMessage(
                  Component.literal(alarmName + entityIn.getDisplayName().getString() + " has entered your boundary.").withStyle(ChatFormatting.RED)
               );
            }
         }
      }
   }

   @Override
   public void doNotBarrier() {
      if (this.detected) {
         this.detected = false;
         this.sendUpdates();
      }
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_alarm"));
   }

   @Override
   public List<Item> checkForItem() {
      return Arrays.asList(Items.NAME_TAG);
   }

   @Override
   public Class<? extends Entity> checkForEntity() {
      return ItemEntity.class;
   }

   @Override
   public boolean ifItemFoundAbove(ItemEntity item) {
      if (item.getOwner() != null && item.getOwner().getUUID().equals(this.getCasterUUID())) {
         this.alarmName = item.getItem().getHoverName().getString();
         this.sendUpdates();
      }

      return false;
   }
}
