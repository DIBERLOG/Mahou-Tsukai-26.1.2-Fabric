package stepsword.mahoutsukai.tile.boundary;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class DisplacementBoundaryMahoujinTileEntity extends BoundaryMahoujinTileEntity {
   private static final boolean livingOnly = true;

   public DisplacementBoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.displacementBoundary.get(), true, p, bs);
   }

   @Override
   public int getManaCost() {
      return MTConfig.DISPLACEMENT_BARRIER_MANA_COST;
   }

   @Override
   public int getManaCycle() {
      return MTConfig.DISPLACEMENT_BARRIER_MANA_CYCLE;
   }

   @Override
   public int getBarrierCycle() {
      return MTConfig.DISPLACEMENT_BARRIER_CYCLE;
   }

   @Override
   public int getBarrierRadius() {
      return MTConfig.DISPLACEMENT_BARRIER_RADIUS;
   }

   @Override
   public void doBarrier(List<LivingEntity> entities) {
      double x1 = this.worldPosition.getX() - this.getBarrierRadius() + 1;
      double x2 = this.worldPosition.getX() + this.getBarrierRadius() - 1;
      double z1 = this.worldPosition.getZ() - this.getBarrierRadius() + 1;
      double z2 = this.worldPosition.getZ() + this.getBarrierRadius() - 1;
      Player caster = this.getCaster();

      for (Entity e : entities) {
         if (!ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), e) && !(e instanceof ArmorStand)) {
            double x = e.getX();
            double y = e.getY();
            double z = e.getZ();
            Vec3 lv = e.getLookAngle().normalize();
            double lx = lv.x;
            double lz = lv.z;
            if (lx * lx + lz * lz < 1.0E-8) {
               double yaw = Math.toRadians(e.getYRot());
               lx = -Math.sin(yaw);
               lz = Math.cos(yaw);
            }

            BlockPos newPos = this.getDestination(this, x, y, z, lx, lz);
            BlockPos oldPos = Utils.toBlockPos(x, y, z);
            boolean doSwitch;
            if (oldPos.getX() > x1 && oldPos.getX() < x2 && oldPos.getZ() > z1 && oldPos.getZ() < z2) {
               doSwitch = true;
            } else {
               doSwitch = false;
            }

            if (doSwitch) {
               BlockPos ascendedPos = this.getAscendedDestination(newPos, this.level, e);
               if (ascendedPos != null) {
                  newPos = ascendedPos;
               }

               if (!this.level.isClientSide()) {
                  if (e instanceof ServerPlayer && !((ServerPlayer)e).isSpectator() && !((ServerPlayer)e).isCreative()) {
                     ((ServerPlayer)e).connection.teleport(newPos.getX(), newPos.getY(), newPos.getZ(), e.getYRot(), e.getXRot());
                  } else {
                     e.teleportTo(newPos.getX(), newPos.getY(), newPos.getZ());
                  }
               }
            }
         }
      }
   }

   public BlockPos getDestination(DisplacementBoundaryMahoujinTileEntity te, double x, double y, double z, double lx, double lz) {
      BlockPos ret = null;
      double nx = x;
      double ny = y;
      double nz = z;
      Vec3 look = new Vec3(lx, 0.0, lz);
      if (look.lengthSqr() < 1.0E-8) {
         look = new Vec3(1.0, 0.0, 0.0);
      }

      look = look.normalize();
      lx = look.x;
      lz = look.z;
      if (te != null) {
         double x1 = te.getBlockPos().getX() - this.getBarrierRadius();
         double x2 = x1 + this.getBarrierRadius() * 2;
         double z1 = te.getBlockPos().getZ() - this.getBarrierRadius();

         for (double z2 = z1 + this.getBarrierRadius() * 2; nx > x1 && nx < x2 && nz > z1 && nz < z2; nz += lz) {
            nx += lx;
         }
      }

      return Utils.toBlockPos(nx, ny, nz);
   }

   public BlockPos getAscendedDestination(BlockPos p, Level w, Entity e) {
      for (BlockPos tmp = p; tmp.getY() < w.getMaxY(); tmp = tmp.above()) {
         if (this.okPosition(tmp, w, e)) {
            return tmp;
         }
      }

      return null;
   }

   public boolean okPosition(BlockPos p, Level w, Entity e) {
      for (int i = 0; i < e.getBbHeight(); i++) {
         BlockPos tmp = p.offset(0, i, 0);
         if (!Utils.isBlockAir(this.level, tmp)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_displacement"));
   }
}
