package stepsword.mahoutsukai.tile.boundary;

import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.UpdatingTileEntity;

public class InvisibleBarrierBlockTileEntity extends UpdatingTileEntity {
   private UUID casterUUID;
   public BlockPos boundaryCircle;
   public long timeN = 21L;
   public long timeE = 21L;
   public long timeW = 21L;
   public long timeS = 21L;
   public long timeU = 21L;
   public long timeD = 21L;
   public static final int fadeTicks = 20;
   public static int updateWhiteListTicks = 20;
   public static final String CASTER_ID = "caster_uuid";
   public static final String TIME_SINCE_COLLISION = "time_since_last_collision";
   public static final String ORIGINAL_BOUNDARY_CIRCLE_POS = "original_boundary_circle_pos";
   public static final String WHITELIST_TAG = "whitelist";
   public HashSet<UUID> whitelist;

   public InvisibleBarrierBlockTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<?>)ModTileEntities.invisibleBarrier.get(), p, bs);
   }

   protected void saveAdditional(ValueOutput output) {
      if (this.casterUUID != null) {
         output.store("caster_uuid", UUIDUtil.CODEC, this.casterUUID);
      }

      output.putLong("time_since_last_collisionN", this.timeN);
      output.putLong("time_since_last_collisionE", this.timeE);
      output.putLong("time_since_last_collisionW", this.timeW);
      output.putLong("time_since_last_collisionS", this.timeS);
      output.putLong("time_since_last_collisionU", this.timeU);
      output.putLong("time_since_last_collisionD", this.timeD);
      if (this.boundaryCircle != null) {
         output.store("original_boundary_circle_pos", BlockPos.CODEC, this.boundaryCircle);
      }

      if (this.whitelist != null) {
         CompoundTag tmp = new CompoundTag();
         int z = 0;

         for (UUID uuid : this.whitelist) {
            tmp.store("MAHOUTSUKAI_WHITELIST_" + z++, UUIDUtil.CODEC, uuid);
         }

         output.store("whitelist", CompoundTag.CODEC, tmp);
      }

      super.saveAdditional(output);
   }

   protected void loadAdditional(ValueInput input) {
      input.read("caster_uuid", UUIDUtil.CODEC).ifPresent(uuid -> this.casterUUID = uuid);
      this.timeN = input.getLongOr("time_since_last_collisionN", 21L);
      this.timeE = input.getLongOr("time_since_last_collisionE", 21L);
      this.timeW = input.getLongOr("time_since_last_collisionW", 21L);
      this.timeS = input.getLongOr("time_since_last_collisionS", 21L);
      this.timeU = input.getLongOr("time_since_last_collisionU", 21L);
      this.timeD = input.getLongOr("time_since_last_collisionD", 21L);
      this.boundaryCircle = (BlockPos)input.read("original_boundary_circle_pos", BlockPos.CODEC).orElse(null);
      input.read("whitelist", CompoundTag.CODEC).ifPresent(nbt -> {
         if (this.whitelist == null) {
            this.whitelist = new HashSet<>();
         }

         for (String s : nbt.keySet()) {
            nbt.read(s, UUIDUtil.CODEC).ifPresent(this.whitelist::add);
         }
      });
      super.loadAdditional(input);
   }

   public long getTimeSinceCollision(Direction facing) {
      if (this.level != null) {
         if (facing == Direction.NORTH) {
            return this.level.getGameTime() - this.timeN;
         }

         if (facing == Direction.EAST) {
            return this.level.getGameTime() - this.timeE;
         }

         if (facing == Direction.WEST) {
            return this.level.getGameTime() - this.timeW;
         }

         if (facing == Direction.SOUTH) {
            return this.level.getGameTime() - this.timeS;
         }

         if (facing == Direction.DOWN) {
            return this.level.getGameTime() - this.timeD;
         }

         if (facing == Direction.UP) {
            return this.level.getGameTime() - this.timeU;
         }
      }

      return 21L;
   }

   public void setTimeOfCollision(long t, Direction facing) {
      if (facing == Direction.NORTH) {
         this.timeN = t;
      }

      if (facing == Direction.EAST) {
         this.timeE = t;
      }

      if (facing == Direction.WEST) {
         this.timeW = t;
      }

      if (facing == Direction.SOUTH) {
         this.timeS = t;
      }

      if (facing == Direction.DOWN) {
         this.timeD = t;
      }

      if (facing == Direction.UP) {
         this.timeU = t;
      }

      this.sendUpdates();
   }

   public Player getCaster() {
      try {
         return this.level.getPlayerByUUID(this.casterUUID);
      } catch (Exception e) {
         return null;
      }
   }

   public UUID getCasterUUID() {
      return this.casterUUID;
   }

   public void setCasterUUID(UUID casterUUID) {
      this.casterUUID = casterUUID;
      this.sendUpdates();
   }

   public void setBoundaryCircle(BlockPos p) {
      this.boundaryCircle = p;
      this.sendUpdates();
   }

   public void setCaster(Player caster) {
      this.casterUUID = caster.getUUID();
      this.sendUpdates();
   }
}
