package stepsword.mahoutsukai.block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.tile.FogProjectorTileEntity;
public class FogProjector extends BlockTileEntity<FogProjectorTileEntity> {
 public FogProjector() {super(Properties.ofFullCopy(Blocks.GLASS).noOcclusion(),"fog_projector");}
 public Class<FogProjectorTileEntity> getTileEntityClass() {return FogProjectorTileEntity.class;}
 public FogProjectorTileEntity newBlockEntity(BlockPos pos,BlockState state) {return new FogProjectorTileEntity(pos,state);}
 
}
