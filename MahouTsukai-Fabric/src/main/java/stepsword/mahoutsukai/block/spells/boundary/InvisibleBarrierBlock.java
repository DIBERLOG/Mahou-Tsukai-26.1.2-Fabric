package stepsword.mahoutsukai.block.spells.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import stepsword.mahoutsukai.block.BlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.InvisibleBarrierBlockTileEntity;
import stepsword.mahoutsukai.tile.boundary.TangibleBoundaryMahoujinTileEntity;

public class InvisibleBarrierBlock extends BlockTileEntity<InvisibleBarrierBlockTileEntity> {
   AABB collisionCaster = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   VoxelShape castershape = Shapes.create(this.collisionCaster);
   AABB collisionElse = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   VoxelShape elseshape = Shapes.create(this.collisionElse);

   public InvisibleBarrierBlock() {
      super(Properties.of().forceSolidOn().noOcclusion().strength(5.0F, 10.0F), "invisible_barrier_block");
   }

   public List<ItemStack> getDrops(BlockState p_287732_, net.minecraft.world.level.storage.loot.LootParams.Builder p_287596_) {
      return new ArrayList<>();
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
   }

   public void onProjectileHit(Level worldIn, BlockState state, BlockHitResult result, Projectile entityIn) {
      this.handleEntityInside(worldIn, result.getBlockPos(), entityIn);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
      this.handleEntityInside(worldIn, pos, entityIn);
   }

   private void handleEntityInside(Level worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isClientSide() && entityIn != null && entityIn.isAlive()) {
         UUID entUUID = entityIn.getUUID();
         InvisibleBarrierBlockTileEntity te = (InvisibleBarrierBlockTileEntity)worldIn.getBlockEntity(pos);
         if (te != null) {
            UUID casterUUID = te.getCasterUUID();
            if (te.whitelist != null && te.whitelist.contains(entUUID) || entUUID.equals(casterUUID)) {
               return;
            }

            Direction side = Direction.NORTH;
            if (entityIn.getX() < pos.getX()) {
               side = Direction.WEST;
            } else if (entityIn.getX() > pos.getX() + 1) {
               side = Direction.EAST;
            } else if (entityIn.getY() < pos.getY()) {
               side = Direction.DOWN;
            } else if (entityIn.getY() > pos.getY() + 1) {
               side = Direction.UP;
            } else if (entityIn.getZ() < pos.getZ()) {
               side = Direction.NORTH;
            } else if (entityIn.getZ() > pos.getZ() + 1) {
               side = Direction.SOUTH;
            }

            te.setTimeOfCollision(worldIn.getGameTime(), side);
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      if (context instanceof EntityCollisionContext) {
         Entity e = ((EntityCollisionContext)context).getEntity();
         if (e != null && e.isAlive()) {
            UUID entUUID = e.getUUID();
            InvisibleBarrierBlockTileEntity te = (InvisibleBarrierBlockTileEntity)reader.getBlockEntity(pos);
            if (te != null) {
               UUID casterUUID = te.getCasterUUID();
               if ((te.whitelist == null || !te.whitelist.contains(entUUID)) && !entUUID.equals(casterUUID)) {
                  return this.elseshape;
               }
            }

            if (e.isCrouching()) {
               return this.elseshape;
            }
         }
      }

      return this.castershape;
   }

   public boolean collisionExtendsVertically(BlockState state, BlockGetter world, BlockPos pos, Entity collidingEntity) {
      return true;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
      if (world.getBlockEntity(pos) instanceof InvisibleBarrierBlockTileEntity barrier) {
         try {
            if (world.getBlockEntity(barrier.boundaryCircle) instanceof TangibleBoundaryMahoujinTileEntity tangible) {
               int radius = tangible.getBarrierRadius();
               boolean onBoundaryFace = radius == Math.abs(pos.getX() - barrier.boundaryCircle.getX())
                  || radius == Math.abs(pos.getY() - barrier.boundaryCircle.getY())
                  || radius == Math.abs(pos.getZ() - barrier.boundaryCircle.getZ());
               if (!tangible.shouldIPerformBarrier() || !onBoundaryFace) {
                  world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
               }
            } else {
               world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
         } catch (Exception e) {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
         }
      }
   }

   @Override
   public Class<InvisibleBarrierBlockTileEntity> getTileEntityClass() {
      return InvisibleBarrierBlockTileEntity.class;
   }

   @Nullable
   public InvisibleBarrierBlockTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new InvisibleBarrierBlockTileEntity(blockPos, blockState);
   }

   protected boolean propagatesSkylightDown(BlockState state) {
      return true;
   }

   protected VoxelShape getOcclusionShape(BlockState state) {
      return this.castershape;
   }

   public boolean hasDynamicShape() {
      return true;
   }
}
