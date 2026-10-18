package stepsword.mahoutsukai.block;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.potion.BlackFlameEyesPotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class BlackFireBlock extends BlockBase {
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty WEST = BooleanProperty.create("west");
   public static final BooleanProperty UPPER = BooleanProperty.create("up");
   public static final Random RNG = new Random();
   private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION
      .entrySet()
      .stream()
      .filter(facingProperty -> facingProperty.getKey() != Direction.DOWN)
      .collect(Util.toMap());

   protected BlackFireBlock() {
      super(
         Properties.ofFullCopy(Blocks.FIRE).noCollision().noLootTable().randomTicks().strength(0.0F).lightLevel(state -> 15).sound(SoundType.WOOL),
         "black_fire"
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(AGE, 0))
                        .setValue(NORTH, false))
                     .setValue(EAST, false))
                  .setValue(SOUTH, false))
               .setValue(WEST, false))
            .setValue(UPPER, false)
      );
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.getStateForPlacement(context.getLevel(), context.getClickedPos());
   }

   public BlockState getStateForPlacement(LevelReader blockReader, BlockPos pos) {
      BlockPos blockpos = pos.below();
      BlockState blockstate = blockReader.getBlockState(blockpos);
      if (!this.canCatchFire(blockReader, pos, Direction.UP) && !Block.canSupportRigidBlock(blockReader, blockpos)) {
         BlockState blockstate1 = this.defaultBlockState();

         for (Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
            if (booleanproperty != null) {
               blockstate1 = (BlockState)blockstate1.setValue(booleanproperty, this.canCatchFire(blockReader, pos.relative(direction), direction.getOpposite()));
            }
         }

         return blockstate1;
      } else {
         return this.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.below();
      return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, Direction.UP) || this.areNeighborsFlammable(worldIn, pos);
   }

   private boolean areNeighborsFlammable(LevelReader worldIn, BlockPos pos) {
      for (Direction direction : Direction.values()) {
         if (this.canCatchFire(worldIn, pos.relative(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (!worldIn.isClientSide()) {
         worldIn.scheduleTick(pos, state.getBlock(), 6);
      }
   }

   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE, NORTH, EAST, SOUTH, WEST, UPPER});
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource p_60554_) {
      int i = (Integer)state.getValue(AGE);
      this.tick(state, level, pos, p_60554_);
      if (i >= 15) {
         level.removeBlock(pos, false);
      }

      super.randomTick(state, level, pos, p_60554_);
   }

   public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
      worldIn.scheduleTick(pos, state.getBlock(), 20);
      if (worldIn != null && worldIn.isRaining()) {
         worldIn.removeBlock(pos, false);
      }

      if ((worldIn.getGameTime() % 50000L) % MTConfig.BLACK_FLAME_UPDATE_TICKS == 0L) {
         if (worldIn.canSpreadFireAround(pos)) {
            if (!worldIn.hasChunksAt(pos.offset(-2,-2,-2),pos.offset(2,2,2))) {
               return;
            }

            if (!this.canPlaceBlockAt(worldIn, pos)) {
               worldIn.removeBlock(pos, false);
            }

            Block block = worldIn.getBlockState(pos.below()).getBlock();
            if (Utils.isBlockAir(worldIn, pos.below()) || block == ModBlocks.blackFireBlock.get()) {
               worldIn.removeBlock(pos, false);
               return;
            }

            int i = (Integer)state.getValue(AGE);
            // Native block states are immutable; the actual bounded update is performed below.
            if (i >= MTConfig.BLACK_FLAME_DEATH_AGE && worldIn.getBlockState(pos.below()).getBlock() != Blocks.NETHERRACK) {
               worldIn.removeBlock(pos, false);
               return;
            }

            if (this.canDie(worldIn, pos)) {
               worldIn.removeBlock(pos, false);
            } else {
               if (i < 15) {
                  state = (BlockState)state.setValue(AGE, i + 1);
                  worldIn.setBlock(pos, state, 4);
               }

               if (!this.canNeighborCatchFire(worldIn, pos)) {
                  if (!worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP) || i > 3) {
                     worldIn.removeBlock(pos, false);
                  }

                  return;
               }

               if (!this.canCatchFire(worldIn, pos.below(), Direction.UP) && i == 15 && rand.nextInt(4) == 0) {
                  worldIn.removeBlock(pos, false);
                  return;
               }
            }
         } else {
            worldIn.removeBlock(pos, false);
         }
      }
   }

   protected boolean canDie(Level worldIn, BlockPos pos) {
      return worldIn.isRaining();
   }

   @Deprecated
   public int getFlammability(Block blockIn) {
      return 50 + RNG.nextInt(20);
   }

   @Deprecated
   public int getEncouragement(Block blockIn) {
      return 50 + RNG.nextInt(20);
   }

   private boolean canNeighborCatchFire(Level worldIn, BlockPos pos) {
      for (Direction enumfacing : Direction.values()) {
         if (this.canCatchFire(worldIn, pos.relative(enumfacing), enumfacing.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   protected void entityInside(BlockState p_state, Level p_level, BlockPos p_pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
      if (entity instanceof LivingEntity) {
         EffectUtil.buff((LivingEntity)entity, ModEffects.BLACK_BURNING, false, MTConfig.BLACK_FLAME_DEBUFF_TIME);
         BlackFlameEyesPotion.notifyIfBurning((LivingEntity)entity);
      }
   }

   @Deprecated
   public boolean canCatchFire(LevelReader worldIn, BlockPos pos) {
      return this.canCatchFire(worldIn, pos, Direction.UP);
   }

   public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
      return this.canNeighborCatchFire(worldIn, pos);
   }

   public boolean canCatchFire(LevelReader world, BlockPos pos, Direction face) {
      Block b = world.getBlockState(pos).getBlock();
      BlockEntity te = world.getBlockEntity(pos);
      return te == null
         && b != ModBlocks.invisibleBarrierBlock.get()
         && !EffectUtil.unchangableBlock(
            b,
            Arrays.asList(
               Blocks.AIR, Blocks.ICE, Blocks.SNOW, Blocks.WATER, Blocks.LAVA, Blocks.CAVE_AIR, Blocks.VOID_AIR, (Block)ModBlocks.blackFireBlock.get()
            )
         );
   }

   protected boolean isRandomlyTicking(BlockState pState) {
      return true;
   }
}
