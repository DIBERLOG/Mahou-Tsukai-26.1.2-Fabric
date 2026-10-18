package stepsword.mahoutsukai.block.circuits;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.block.BlockTileEntity;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitMagitechTileEntity;

public class ManaCircuitMagitechBlock extends BlockTileEntity<ManaCircuitMagitechTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   public static final EnumProperty<Direction> FACING = EnumProperty.create("opposite", Direction.class);
   private static final EnumProperty<Direction> FACING_HORIZ = HorizontalDirectionalBlock.FACING;

   public ManaCircuitMagitechBlock() {
      super(Properties.ofFullCopy(Blocks.GLASS), "mana_circuit_magitech");
   }

   @Override
   public Class<ManaCircuitMagitechTileEntity> getTileEntityClass() {
      return ManaCircuitMagitechTileEntity.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ManaCircuitMagitechTileEntity(blockPos, blockState);
   }

   public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult result) {
      return (InteractionResult)(ManaCircuitBlock.chargeCircuitBlock(worldIn, pos, playerIn) ? InteractionResult.SUCCESS : InteractionResult.FAIL);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)((BlockState)super.getStateForPlacement(ctx).setValue(FACING, ctx.getClickedFace()))
         .setValue(FACING_HORIZ, ctx.getHorizontalDirection());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, FACING_HORIZ});
   }
}

