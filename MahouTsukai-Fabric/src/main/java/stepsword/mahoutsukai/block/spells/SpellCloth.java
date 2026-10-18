package stepsword.mahoutsukai.block.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import stepsword.mahoutsukai.block.BlockBase;

public class SpellCloth extends BlockBase {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

   public SpellCloth() {
      super(Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion(), "spell_cloth");
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return Shapes.create(boundingBox);
   }

   public VoxelShape getOcclusionShape(BlockState state) {
      return Shapes.create(boundingBox);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return Shapes.create(boundingBox);
   }

   public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return Shapes.create(boundingBox);
   }
}
