package stepsword.mahoutsukai.tile.boundary;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ScrollGiver;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class BoundaryMahoujinTileEntity extends MahoujinTickingTileEntity implements ScrollGiver {
   private static final String SWITCH_ON = "SWITCH_ON";
   private static final int TICKS_PER_SECOND = 20;
   public HashSet<UUID> entitiesInBarrier = null;
   private boolean switchOn = false;
   private boolean performBarrierFunction = false;
   private int tickCounter = 0;
   private int manaTickCounter = 0;
   private boolean livingOnly = true;
   private boolean off = false;

   public BoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super(p, bs);
      this.livingOnly = true;
   }

   public BoundaryMahoujinTileEntity(boolean l, BlockPos p, BlockState bs) {
      super(p, bs);
      this.livingOnly = l;
   }

   public BoundaryMahoujinTileEntity(BlockEntityType type, BlockPos p, BlockState bs) {
      super(type, p, bs);
      this.livingOnly = true;
   }

   public BoundaryMahoujinTileEntity(BlockEntityType type, boolean livingOnly, BlockPos p, BlockState bs) {
      this(type, p, bs);
      this.livingOnly = livingOnly;
   }

   public int getManaCost() {
      return 0;
   }

   public int getManaCycle() {
      return 20;
   }

   public int getBarrierCycle() {
      return 20;
   }

   public int getBarrierRadius() {
      return 10;
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      output.putBoolean("SWITCH_ON", this.switchOn);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.switchOn = input.getBooleanOr("SWITCH_ON", false);
      super.loadAdditional(input);
   }

   public void toggle(Player e) {
      if (ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), e) || this.isFay()) {
         if (this.isFay()) {
            this.setCaster(e);
         }

         this.switchOn = !this.switchOn;
         this.performBarrierFunction = this.switchOn;
         this.manaTickCounter = 0;
         this.tickCounter = 0;
         this.sendUpdates();
      }
   }

   public boolean getSwitchOn() {
      return this.switchOn;
   }

   @Override
   public void tick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      super.tick(level, blockPos, blockState, blockEntity);
      if (!level.isClientSide()) {
         if (this.manaTickCounter == this.getManaCycle() * 20) {
            this.manaTickCounter = 0;
            Player caster = this.getCaster();
            boolean redstoneSwitchOn = this.shouldIPerformBarrier();
            if (caster != null && redstoneSwitchOn) {
               IMahou mahou = Utils.getPlayerMahou(caster);
               if (mahou != null && caster.level().getGameTime() - mahou.getCancelTime() < this.getManaCycle() * 20 * 3) {
                  this.toggle(caster);
               }
            }

            boolean nearbyBatteriesGaveMana = redstoneSwitchOn
               && PlayerManaManager.getManaFromBatteriesFirst(this.worldPosition, level, this.getCasterUUID(), this.getManaCost());
            if (!nearbyBatteriesGaveMana && caster == null && redstoneSwitchOn) {
               redstoneSwitchOn = false;
               this.manaTickCounter = 0;
               this.tickCounter = 0;
               this.sendUpdates();
            }

            if (!redstoneSwitchOn) {
               this.performBarrierFunction = false;
            } else if (!nearbyBatteriesGaveMana) {
               this.performBarrierFunction = PlayerManaManager.drainMana(caster, this.getManaCost(), false, true, true, true) == this.getManaCost();
            } else {
               this.performBarrierFunction = true;
            }

            if (this.performBarrierFunction && !ActiveCircleConfig.tryToOperate(this, this.getCasterUUID())) {
               this.performBarrierFunction = false;
               if (this.getCaster() != null) {
                  PlayerHelp.sendHelpMessage(this.getCaster(), PlayerHelp.Message.CIRCLE_TOO_MANY);
                  this.toggle(caster);
               }
            }

            level.updateNeighbourForOutputSignal(this.worldPosition, level.getBlockState(this.worldPosition).getBlock());
         }

         if (this.tickCounter == this.getBarrierCycle()) {
            this.tickCounter = 0;
            if (this.performBarrierFunction) {
               AABB aabb = new AABB(
                  this.worldPosition.getX() - this.getBarrierRadius(),
                  this.worldPosition.getY() - this.getBarrierRadius(),
                  this.worldPosition.getZ() - this.getBarrierRadius(),
                  this.worldPosition.getX() + this.getBarrierRadius(),
                  this.worldPosition.getY() + this.getBarrierRadius(),
                  this.worldPosition.getZ() + this.getBarrierRadius()
               );
               if (this.livingOnly) {
                  List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, aabb, Entity::isAlive);
                  this.doBarrier(entities);
                  this.entitiesInBarrier = new HashSet<>(
                     entities.stream().map(Entity::getUUID).collect(Collectors.toSet())
                  );
               } else {
                  List<Entity> entities = this.level.getEntitiesOfClass(Entity.class, aabb, Entity::isAlive);
                  this.doBarrierNonLiving(entities);
               }

               this.off = false;
            } else {
               if (!this.off) {
                  this.doNotBarrier();
               }

               this.off = true;
            }
         }

         this.tickCounter++;
         this.manaTickCounter++;
      }
   }

   public void doBarrier(List<LivingEntity> lst) {
   }

   public void doNotBarrier() {
   }

   public boolean shouldIPerformBarrier() {
      boolean powered = this.level.hasNeighborSignal(this.worldPosition);
      if (this.getCasterUUID() == null) {
         return false;
      } else {
         return powered ? !this.switchOn : this.switchOn;
      }
   }

   public void doBarrierNonLiving(List<Entity> lst) {
   }
}
