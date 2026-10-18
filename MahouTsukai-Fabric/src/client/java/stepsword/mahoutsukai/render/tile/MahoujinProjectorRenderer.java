package stepsword.mahoutsukai.render.tile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
public class MahoujinProjectorRenderer implements BlockEntityRenderer<MahoujinProjectorTileEntity,ProjectorRenderState> {
 public MahoujinProjectorRenderer(BlockEntityRendererProvider.Context context){}
 public ProjectorRenderState createRenderState(){return new ProjectorRenderState();}
 public void extractRenderState(MahoujinProjectorTileEntity tile,ProjectorRenderState state,float partial,net.minecraft.world.phys.Vec3 camera,net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay progress){
  BlockEntityRenderer.super.extractRenderState(tile,state,partial,camera,progress);
  state.pv=stepsword.mahoutsukai.render.enchant.ProjectorValues.readFromTag(stepsword.mahoutsukai.render.enchant.ProjectorValues.writeToTag(tile.pv));
  var mc=net.minecraft.client.Minecraft.getInstance();state.locator=mc.player!=null&&mc.player.getMainHandItem().is(stepsword.mahoutsukai.block.ModBlocks.projector.get().asItem());
  state.on=tile.getLevel()==null||!tile.getLevel().hasNeighborSignal(tile.getBlockPos());state.partialTicks=partial;
 }
 public void submit(ProjectorRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera){if(state.locator)stepsword.mahoutsukai.fabric.ProjectorClient.box(pose,collector,0xffdcdc0a);if(state.on)stepsword.mahoutsukai.render.ProjectorRenderer.render(state.pv,state.partialTicks,pose,collector);}
 public boolean shouldRenderOffScreen(){return true;}
 public int getViewDistance(){return 256;}
}
