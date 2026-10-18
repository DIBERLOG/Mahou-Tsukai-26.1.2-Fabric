package stepsword.mahoutsukai.tile.boundary;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;

public class GravityBoundaryMahoujinTileEntity extends BoundaryMahoujinTileEntity {
   private static final boolean livingOnly = false;

   public GravityBoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.gravityBoundary.get(), false, p, bs);
   }

   @Override
   public int getManaCost() {
      return MTConfig.GRAVITY_BARRIER_MANA_COST;
   }

   @Override
   public int getManaCycle() {
      return MTConfig.GRAVITY_BARRIER_MANA_CYCLE;
   }

   @Override
   public int getBarrierCycle() {
      return MTConfig.GRAVITY_BARRIER_CYCLE;
   }

   @Override
   public int getBarrierRadius() {
      return MTConfig.GRAVITY_BARRIER_RADIUS;
   }

   @Override
   public void doBarrierNonLiving(List<Entity> entities) {
      for (Entity e : entities) {
         if (!ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), e)) {
            double topspeed = MTConfig.GRAVITY_BARRIER_MAX;
            double speed_increase = MTConfig.GRAVITY_BARRIER_FACTOR;
            Vec3 motion = e.getDeltaMovement();
            double motionX = motion.x();
            double motionY = motion.y();
            double motionZ = motion.z();
            motionX *= 0.4;
            if (!e.onGround()) {
               if (motionY > 0.0) {
                  motionY *= 0.4;
               } else {
                  motionY *= speed_increase;
               }

               motionY = Mth.clamp(motionY, -topspeed, topspeed);
            }

            motionZ *= 0.4;
            e.setDeltaMovement(new Vec3(motionX, motionY, motionZ));
            e.hurtMarked = true;
         }
      }
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_gravity"));
   }
}
