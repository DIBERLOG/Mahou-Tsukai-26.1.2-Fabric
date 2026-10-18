package stepsword.mahoutsukai.block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
public class MahoujinProjector extends BlockTileEntity<MahoujinProjectorTileEntity> {
 public MahoujinProjector() {super(Properties.ofFullCopy(Blocks.GLASS).noOcclusion(),"mahoujin_projector");}
 public Class<MahoujinProjectorTileEntity> getTileEntityClass() {return MahoujinProjectorTileEntity.class;}
 public MahoujinProjectorTileEntity newBlockEntity(BlockPos pos,BlockState state) {return new MahoujinProjectorTileEntity(pos,state);}
 public void setPlacedBy(net.minecraft.world.level.Level level,BlockPos pos,BlockState state,net.minecraft.world.entity.LivingEntity placer,net.minecraft.world.item.ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(placer instanceof net.minecraft.world.entity.player.Player player && level.getBlockEntity(pos) instanceof MahoujinProjectorTileEntity tile){tile.caster=player.getUUID();tile.sendUpdates();}}
}
