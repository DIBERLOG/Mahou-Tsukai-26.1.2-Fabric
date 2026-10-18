package stepsword.mahoutsukai.tile.circuits;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.fabric.MahouIds;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.UpdatingTileEntity;

public class ManaCircuitTileEntity extends UpdatingTileEntity {
   private boolean on = false;
   private int storedMana = 0;
   private UUID ownerUUID = null;
   private boolean fae = false;
   public static final String ON_TAG = "MAHOUTSUKAI_ON_TAG";
   public static final String MANA_TAG = "MAHOUTSUKAI_MANA_TAG";
   public static final String OWNER_TAG = "MAHOUTSUKAI_OWNER_TAG";
   public static final String FAE_TAG = "MAHOUTSUKAI_FAE_TAG";

   public ManaCircuitTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<?>)ModTileEntities.circuit.get(), p, bs);
   }

   public ManaCircuitTileEntity(BlockEntityType<? extends ManaCircuitTileEntity> type, BlockPos p, BlockState bs) {
      super(type, p, bs);
   }

   public int getMaxMana() {
      return MTConfig.MANA_CIRCUIT_CAPACITY;
   }

   protected void loadAdditional(ValueInput input) {
      this.storedMana = input.getIntOr("MAHOUTSUKAI_MANA_TAG", 0);
      this.on = input.getBooleanOr("MAHOUTSUKAI_ON_TAG", true);
      this.ownerUUID = (UUID)input.read("MAHOUTSUKAI_OWNER_TAG", UUIDUtil.CODEC).orElse(null);
      this.fae = input.getBooleanOr("MAHOUTSUKAI_FAE_TAG", false);
      super.loadAdditional(input);
   }

   protected void saveAdditional(ValueOutput output) {
      output.putInt("MAHOUTSUKAI_MANA_TAG", this.storedMana);
      output.putBoolean("MAHOUTSUKAI_ON_TAG", this.on);
      if (this.ownerUUID != null) {
         output.store("MAHOUTSUKAI_OWNER_TAG", UUIDUtil.CODEC, this.ownerUUID);
      }

      output.putBoolean("MAHOUTSUKAI_FAE_TAG", this.fae);
      super.saveAdditional(output);
   }

   protected void applyImplicitComponents(DataComponentGetter input) {
      super.applyImplicitComponents(input);
      this.storedMana = (Integer)input.getOrDefault(DataComponents.MANA_CIRCUIT_MANA_COMPONENT, 0);
      this.ownerUUID = (UUID)input.getOrDefault(DataComponents.MANA_CIRCUIT_OWNER_COMPONENT, MahouIds.FAE_UUID);
   }

   protected void collectImplicitComponents(Builder builder) {
      builder.set(DataComponents.MANA_CIRCUIT_MANA_COMPONENT, this.storedMana);
      if (this.ownerUUID != null) builder.set(DataComponents.MANA_CIRCUIT_OWNER_COMPONENT, this.ownerUUID);
   }

   public boolean isOn() {
      return this.on;
   }

   public void setOn(boolean b) {
      this.on = b;
   }

   public int getMana() {
      return this.storedMana;
   }

   public void setMana(int m) {
      this.storedMana = m;
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(UUID m) {
      this.ownerUUID = m;
   }

   public void setFae(boolean b) {
      this.fae = b;
   }

   public boolean getFae() {
      return this.fae;
   }

   public boolean isRedstoneOn() {
      boolean z = this.isOn();
      return this.level != null && this.worldPosition != null && this.level.hasNeighborSignal(this.worldPosition) ? !z : z;
   }
}
