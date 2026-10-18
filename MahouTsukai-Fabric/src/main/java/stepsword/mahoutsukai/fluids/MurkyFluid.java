package stepsword.mahoutsukai.fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.item.ModItems;
/** Original BaseFlowingFluid defaults, with water-like Fabric physics supplied by the water fluid tag. */
public abstract class MurkyFluid extends WaterFluid {
 public static final Identifier STILL=Identifier.fromNamespaceAndPath("mahoutsukai","block/murky_still");
 public static final Identifier FLOW=Identifier.fromNamespaceAndPath("mahoutsukai","block/murky_flow");
 public static final int COLOR=0xE6003C00;
 public Fluid getFlowing(){return ModFluids.MURKY_FLOW;}
 public Fluid getSource(){return ModFluids.MURKY_STILL;}
 public Item getBucket(){return ModItems.murkyBucket.get();}
 protected boolean canConvertToSource(ServerLevel world){return false;}
 public int getTickDelay(LevelReader world){return 5;}
 public int getSlopeFindDistance(LevelReader world){return 4;}
 public int getDropOff(LevelReader world){return 1;}
 protected float getExplosionResistance(){return 1;}
 public void animateTick(Level world,BlockPos pos,FluidState state,RandomSource random){}
 public net.minecraft.core.particles.ParticleOptions getDripParticle(){return null;}
 protected void beforeDestroyingBlock(LevelAccessor world,BlockPos pos,BlockState state){Block.dropResources(state,world,pos,state.hasBlockEntity()?world.getBlockEntity(pos):null);}
 public BlockState createLegacyBlock(FluidState state){return ModBlocks.murkyFluid.get().defaultBlockState().setValue(LiquidBlock.LEVEL,getLegacyLevel(state));}
 public boolean isSame(Fluid fluid){return fluid==ModFluids.MURKY_STILL || fluid==ModFluids.MURKY_FLOW;}
 public static final class Source extends MurkyFluid {
  public int getAmount(FluidState state){return 8;}
  public boolean isSource(FluidState state){return true;}
 }
 public static final class Flowing extends MurkyFluid {
  public Flowing(){registerDefaultState(getStateDefinition().any().setValue(LEVEL,7));}
  protected void createFluidStateDefinition(StateDefinition.Builder<Fluid,FluidState> builder){super.createFluidStateDefinition(builder);builder.add(LEVEL);}
  public int getTickDelay(LevelReader world){return 20;}
  public int getAmount(FluidState state){return state.getValue(LEVEL);}
  public boolean isSource(FluidState state){return false;}
 }
}
