package stepsword.mahoutsukai.render.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import stepsword.mahoutsukai.render.*;
public final class ProjectedFogParticle extends Particle {
 private static final float[] WHITE={1,1,1,1};
 private final float[] rgba;private final float size,softness,rotation;private final boolean shaders,ageColor;private boolean moving;
 private ProjectedFogParticle(ClientLevel level,double x,double y,double z){
  super(level,x,y,z);lifetime=801;hasPhysics=false;float color=.8f+.2f*random.nextFloat();rgba=new float[]{color,color,color,0};size=(8+random.nextFloat()*12-2)*1.3f;rotation=random.nextFloat()*360;softness=.07f;shaders=true;ageColor=true;
 }
 public static Particle ambient(ClientLevel level,double x,double y,double z){return new ProjectedFogParticle(level,x,y,z);}
 private ProjectedFogParticle(ClientLevel level,net.minecraft.core.BlockPos pos,stepsword.mahoutsukai.util.FogProjectorValues v){
  super(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5);lifetime=801;hasPhysics=false;ageColor=false;moving=true;rotation=random.nextFloat()*360;size=v.particle_fog_size;softness=v.particle_fog_thickness;shaders=v.particle_fog_shaders;
  rgba=new float[]{v.particle_fog_color_r/255f,v.particle_fog_color_g/255f,v.particle_fog_color_b/255f,v.particle_fog_color_a/255f};
  var d=new net.minecraft.world.phys.Vec3(v.particle_fog_direction_x,v.particle_fog_direction_y,v.particle_fog_direction_z).normalize();d=new net.minecraft.world.phys.Vec3(v.particle_fog_random_x?random.nextDouble()*2-1:d.x,v.particle_fog_random_y?random.nextDouble()*2-1:d.y,v.particle_fog_random_z?random.nextDouble()*2-1:d.z).normalize().scale(v.particle_fog_speed);xd=d.x;yd=d.y;zd=d.z;
 }
 public static void spawn(ClientLevel level,net.minecraft.core.BlockPos pos,stepsword.mahoutsukai.util.FogProjectorValues v){net.minecraft.client.Minecraft.getInstance().particleEngine.add(new ProjectedFogParticle(level,pos,v));}
 public ParticleRenderType getGroup(){return ParticleRenderType.NO_RENDER;}
 public void tick(){
  ++age;if(random.nextFloat()<.001f||!moving){xd=random.nextFloat()*.01f-.005f;zd=random.nextFloat()*.01f-.005f;moving=true;}if(ageColor)yd=0;
  xo=x;yo=y;zo=z;x+=xd;y+=yd;z+=zd;
  if(ageColor)rgba[3]=age<40?age/40f*.5f:age>730?(800-age)/70f*.5f:.5f;
  if(age>800)remove();
 }
 public void render(net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext context){
  var mc=net.minecraft.client.Minecraft.getInstance();var camera=mc.gameRenderer.getMainCamera().position();float partial=mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);var pose=context.poseStack();pose.pushPose();pose.translate(xo+(x-xo)*partial-camera.x,yo+(y-yo)*partial-camera.y,zo+(z-zo)*partial-camera.z);RenderUtils.billboard(pose,rotation,partial);pose.scale(size,size,size);
  var base=MahoujinRenderType.createBuiltInTextMahoujinRenderType("projected_fog",net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","textures/particle/depth.png"),0,false);
  var type=shaders&&!RenderUtils.turnOffMahouShaders()?FogRenderLayer.wrap(base,WHITE,softness):base;
  context.submitNodeCollector().submitCustomGeometry(pose,type,(nativePose,bb)->{var matrix=nativePose.pose();for(int i=0;i<4;i++){float px=(i<2?-.5f:.5f),pz=(i==0||i==3?-.5f:.5f);bb.addVertex(matrix,px,0,pz).setColor(rgba[0],rgba[1],rgba[2],rgba[3]).setUv(i<2?0:1,i==0||i==3?0:1).setUv2(240,240);}});pose.popPose();
 }
}
