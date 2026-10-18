package stepsword.mahoutsukai.tile;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.item.FaeEssence;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.util.UnorderedList;

public class MahoujinTileEntity extends UpdatingTileEntity {
   private UUID casterUUID;
   private boolean cloth;
   private boolean fay;
   public static final String ORDER_TAG = "catalyst_order";
   public static final String CASTER_ID = "caster_uuid";
   public static final String CLOTH_BOOLEAN = "cloth";
   public static final String FAY_TAG = "fay";
   private UnorderedList catalysts = new UnorderedList();

   public MahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<?>)ModTileEntities.mahoujin.get(), p, bs);
   }

   public MahoujinTileEntity(BlockEntityType type, BlockPos p, BlockState bs) {
      super(type, p, bs);
   }

   protected void saveAdditional(ValueOutput output) {
      if (this.catalysts == null) {
         this.catalysts = new UnorderedList();
      }

      output.store("catalyst_order", UnorderedList.CODEC, this.catalysts);
      output.putBoolean("fay", this.fay);
      output.putBoolean("cloth", this.cloth);
      if (this.casterUUID != null) {
         output.store("caster_uuid", UUIDUtil.CODEC, this.casterUUID);
      }

      super.saveAdditional(output);
   }

   public void loadAdditional(ValueInput input) {
      this.catalysts = new UnorderedList();
      input.read("catalyst_order", UnorderedList.CODEC).ifPresent(catalysts -> this.setCatalysts(catalysts, false));
      input.read("caster_uuid", UUIDUtil.CODEC).ifPresent(casterUUID -> this.casterUUID = casterUUID);
      this.cloth = input.getBooleanOr("cloth", false);
      this.fay = input.getBooleanOr("fay", false);
      super.loadAdditional(input);
   }

   public UnorderedList getCatalysts() {
      if (this.catalysts == null) {
         this.catalysts = new UnorderedList();
      }

      return this.catalysts;
   }

   public void setCatalysts(UnorderedList catalysts) {
      this.setCatalysts(catalysts, true);
   }

   public void setCatalysts(UnorderedList catalysts, boolean update) {
      this.catalysts = catalysts == null ? new UnorderedList() : catalysts;
      if (update) {
         this.sendUpdates();
      }
   }

   public int getNumCatalysts() {
      return this.catalysts == null ? 0 : this.catalysts.size();
   }

   public boolean addCatalyst(PowderedCatalyst catalyst) {
      return this.addCatalyst(catalyst, true);
   }

   public boolean addCatalyst(PowderedCatalyst catalyst, boolean update) {
      if (this.catalysts == null) {
         this.catalysts = new UnorderedList();
      }

      if (this.catalysts.size() >= 3) {
         if (update) {
            this.sendUpdates();
         }

         return false;
      } else {
         this.catalysts.add(catalyst.getCatalystName(), true);
         if (update) {
            this.sendUpdates();
         }

         return true;
      }
   }

   public Player getCaster() {
      if (this.casterUUID == null
         || this.casterUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))
         || this.casterUUID.equals(FaeEssence.faeID)) {
         return null;
      } else if (this.level instanceof ServerLevel && this.level.getServer() != null) {
         return this.level.getServer().getPlayerList().getPlayer(this.casterUUID);
      } else {
         return this.level != null ? this.level.getPlayerByUUID(this.casterUUID) : null;
      }
   }

   public UUID getCasterUUID() {
      return this.casterUUID;
   }

   public void setCasterUUID(UUID casterUUID) {
      this.casterUUID = casterUUID;
      this.sendUpdates();
   }

   public void setCaster(Player caster) {
      this.casterUUID = caster.getUUID();
      this.sendUpdates();
   }

   public boolean hasCloth() {
      return this.cloth;
   }

   public void setCloth(boolean c) {
      this.cloth = c;
   }

   public boolean isFay() {
      return this.fay;
   }

   public void setFay(boolean c) {
      this.fay = c;
   }
}
