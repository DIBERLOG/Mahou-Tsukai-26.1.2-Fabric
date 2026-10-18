package stepsword.mahoutsukai.render.tile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import stepsword.mahoutsukai.tile.FogProjectorTileEntity;
public class FogProjectorRenderer implements BlockEntityRenderer<FogProjectorTileEntity,ProjectorRenderState>{
 public FogProjectorRenderer(BlockEntityRendererProvider.Context context){}
 public ProjectorRenderState createRenderState(){return new ProjectorRenderState();}
 public void extractRenderState(FogProjectorTileEntity tile,ProjectorRenderState state,float partial,net.minecraft.world.phys.Vec3 camera,net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay progress){
  BlockEntityRenderer.super.extractRenderState(tile,state,partial,camera,progress);var mc=net.minecraft.client.Minecraft.getInstance();state.locator=tile.showBox||mc.player!=null&&mc.player.getMainHandItem().is(stepsword.mahoutsukai.block.ModBlocks.fogProjector.get().asItem());
 }
 public void submit(ProjectorRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera){if(state.locator)stepsword.mahoutsukai.fabric.ProjectorClient.box(pose,collector,0xff0adc0a);}
}
