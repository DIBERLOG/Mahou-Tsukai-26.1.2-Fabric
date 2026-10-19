package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.fireball.FireballEntity;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

/** A full-bright molten core and a small, bounded animated flame tail. */
public final class FireballRenderer extends EntityRenderer<FireballEntity,FireballRenderer.State> {
 public static final class State extends EntityRenderState {float age;Vec3 direction=Vec3.ZERO;}
 public FireballRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public State createRenderState(){return new State();}
 @Override public void extractRenderState(FireballEntity entity,State state,float partial){super.extractRenderState(entity,state,partial);state.age=entity.tickCount+partial;state.direction=entity.getDeltaMovement();}
 @Override public void submit(State state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera){
  pose.pushPose();
  var direction=state.direction.lengthSqr()>.0001?state.direction.normalize():new Vec3(0,1,0);
  pose.mulPose(new org.joml.Quaternionf().rotationTo(0,1,0,(float)direction.x,(float)direction.y,(float)direction.z));
  float pulse=1+.055F*(float)Math.sin(state.age*.7);
  var material=MahoujinRenderType.createMahoujinRenderType(RenderUtils.beam,4,false);
  var sphere=MahoujinRenderType.createSphereRenderType(RenderUtils.beam,4);
  RenderUtils.renderSphere(pose,collector,.23F*pulse,16,240,240,1,.93F,.55F,1,sphere);
  RenderUtils.renderSphere(pose,collector,.32F*pulse,16,240,240,1,.24F,.025F,.42F,sphere);
  pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(state.age*9));
  var runes=MahoujinRenderType.createScopedRunesRenderType("fireball_halo",RenderUtils.runes,4);
  RenderUtils.renderRing(pose,collector,0,0,.40F,.055F,40,240,240,1,.62F,.12F,.8F,runes);
  collector.submitCustomGeometry(pose,material,(matrix,vertices)->{
   for(int i=0;i<9;i++){
    double angle=i*Math.PI*2/9+state.age*.11,radius=.21,length=.85+.4*Math.sin(state.age*.23+i);
    var base=new Vec3(Math.cos(angle)*radius,0,Math.sin(angle)*radius);var side=new Vec3(-Math.sin(angle)*.065,0,Math.cos(angle)*.065);var tip=new Vec3(-Math.cos(angle)*.08,-length,-Math.sin(angle)*.08);
    vertex(vertices,matrix.pose(),base.add(side),1,.72F,.13F,.9F);vertex(vertices,matrix.pose(),tip,1,.07F,.01F,0);vertex(vertices,matrix.pose(),tip.add(0,-.08,0),1,.07F,.01F,0);vertex(vertices,matrix.pose(),base.subtract(side),1,.4F,.03F,.65F);
   }
  });
  pose.popPose();
 }
 private static void vertex(VertexConsumer writer,Matrix4f pose,Vec3 point,float r,float g,float b,float a){writer.addVertex(pose,(float)point.x,(float)point.y,(float)point.z).setColor(r,g,b,a).setUv(.5F,.5F).setUv2(240,240);}
}
