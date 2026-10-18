from pathlib import Path
import re,shutil
root=Path(__file__).resolve().parents[1];orig=root/'recovered/stepsword/mahoutsukai';c=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
def put(name,s):
 p=c/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def edit(name,a,b):
 p=c/name;s=p.read_text();assert a in s,(name,a);put(name,s.replace(a,b))
for name in ['render/ClipWrappedRenderLayer.java','render/ClipSubmitNodeCollector.java','render/FogRenderLayer.java']:
 s=(orig/name).read_text().replace('stepsword.mahoutsukai.mixin.','stepsword.mahoutsukai.fabric.mixin.')
 s=s.replace('delegate.pipeline()\n         .toBuilder()', 'PipelineCopies.copy(delegate.pipeline())')
 put(name,s)
edit('render/MahoujinRenderType.java','public final class MahoujinRenderType {','''public final class MahoujinRenderType {
 public static RenderType wrapClipLayer(RenderType type,boolean enabled,double pitch,double yaw,double x,double y,double z,double progress,double size,Matrix4f matrix){return ClipWrappedRenderLayer.wrap(enabled,pitch,yaw,x,y,z,progress,size,type,matrix);}
''')
# The collector proxy preserves the actual ordered interface returned by order().
edit('render/ClipSubmitNodeCollector.java','new Class[]{SubmitNodeCollector.class},\n               new ClipSubmitNodeCollector.Handler(result','new Class[]{OrderedSubmitNodeCollector.class},\n               new ClipSubmitNodeCollector.Handler(result')
s=(c/'render/entity/RenderGateWeaponProjectile.java').read_text()
s=s.replace('      state.item.submit(matrix,submitNodeCollector,15728880,OverlayTexture.NO_OVERLAY,state.outlineColor);','''      var clipped=stepsword.mahoutsukai.render.ClipSubmitNodeCollector.wrap(submitNodeCollector,state.showCircle,135-state.rpitch,state.ryaw-90,state.x,state.y,state.z,state.yTranslate,scale,clipMatrix);
      state.item.submit(matrix,clipped,15728880,OverlayTexture.NO_OVERLAY,state.outlineColor);''')
put('render/entity/RenderGateWeaponProjectile.java',s)

# Per-value grouping prevents one projector's uniform from overwriting another's.
s=(c/'render/FogRenderLayer.java').read_text()
s=s.replace('   private FogRenderLayer() {','''   private static final java.util.List<RenderType> TYPE_POOL=new java.util.ArrayList<>();
   private static final java.util.Map<java.util.List<Float>,RenderType> FRAME_TYPES=new java.util.HashMap<>();
   private static com.mojang.blaze3d.textures.GpuTexture depthCopy;
   private static com.mojang.blaze3d.textures.GpuTextureView depthView;
   private static boolean copiedDepth;
   public static com.mojang.blaze3d.textures.GpuTextureView depth(){return depthView;}
   public static void copyDepth(RenderType type){
      if(copiedDepth||getFogSlice(type)==null)return;
      var source=Minecraft.getInstance().getMainRenderTarget().getDepthTexture();if(source==null)return;
      int width=source.getWidth(0),height=source.getHeight(0);
      if(depthCopy==null||depthCopy.getWidth(0)!=width||depthCopy.getHeight(0)!=height){
         if(depthView!=null)depthView.close();if(depthCopy!=null)depthCopy.close();
         depthCopy=com.mojang.blaze3d.systems.RenderSystem.getDevice().createTexture("Mahou fog depth snapshot",com.mojang.blaze3d.textures.GpuTexture.USAGE_TEXTURE_BINDING|com.mojang.blaze3d.textures.GpuTexture.USAGE_COPY_DST,source.getFormat(),width,height,1,1);
         depthView=com.mojang.blaze3d.systems.RenderSystem.getDevice().createTextureView(depthCopy);
      }
      com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source,depthCopy,0,0,0,0,0,width,height);copiedDepth=true;
   }
   private FogRenderLayer() {''')
s=s.replace('      FOG_UNIFORMS.endFrame();','      FOG_UNIFORMS.endFrame();ACTIVE_FOGS.clear();FRAME_TYPES.clear();copiedDepth=false;')
s=s.replace('      RenderType wrapped = WRAPPED_FOG_TYPES.computeIfAbsent(delegate, FogRenderLayer::createWrappedType);','''      var key=java.util.List.of(rgba[0],rgba[1],rgba[2],rgba[3],softness);
      RenderType wrapped=FRAME_TYPES.get(key);
      if(wrapped==null){int slot=FRAME_TYPES.size();while(TYPE_POOL.size()<=slot)TYPE_POOL.add(createWrappedType(delegate));wrapped=TYPE_POOL.get(slot);FRAME_TYPES.put(key,wrapped);}''')
s=s.replace('delegate.name + "_with_fog"','"projector_fog_"+TYPE_POOL.size()')
s=s.replace('.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))','.withVertexShader(Identifier.fromNamespaceAndPath("mahoutsukai","core/fog"))\n         .withFragmentShader(Identifier.fromNamespaceAndPath("mahoutsukai","core/fog"))\n         .withSampler("Sampler1").withUniform("MahouFog",com.mojang.blaze3d.shaders.UniformType.UNIFORM_BUFFER)\n         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))')
put('render/FogRenderLayer.java',s)
put('fabric/mixin/MahouShaderUniformMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.rendertype.RenderType;
import stepsword.mahoutsukai.render.*;
@Mixin(RenderType.class)
public abstract class MahouShaderUniformMixin {
 @Inject(method="draw",at=@At("HEAD"))
 private void mahoutsukai$depth(com.mojang.blaze3d.vertex.MeshData mesh,CallbackInfo ci){FogRenderLayer.copyDepth((RenderType)(Object)this);}
 @Inject(method="draw",at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/systems/RenderPass;setVertexBuffer(ILcom/mojang/blaze3d/buffers/GpuBuffer;)V"))
 private void mahoutsukai$uniforms(com.mojang.blaze3d.vertex.MeshData mesh,CallbackInfo ci,@Local com.mojang.blaze3d.systems.RenderPass pass){
  var type=(RenderType)(Object)this;var clip=ClipWrappedRenderLayer.getClipSlice(type);if(clip!=null)pass.setUniform("MahouClip",clip);
  var fog=FogRenderLayer.getFogSlice(type);if(fog!=null){pass.setUniform("MahouFog",fog);if(FogRenderLayer.depth()!=null)pass.bindTexture("Sampler1",FogRenderLayer.depth(),com.mojang.blaze3d.systems.RenderSystem.getSamplerCache().getClampToEdge(com.mojang.blaze3d.textures.FilterMode.NEAREST));}
 }
}
''')
put('fabric/mixin/MahouShaderFrameMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.renderer.GameRenderer.class)
public abstract class MahouShaderFrameMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="render",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void mahoutsukai$frame(net.minecraft.client.DeltaTracker delta,boolean tick,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){stepsword.mahoutsukai.render.ClipWrappedRenderLayer.endFrame();stepsword.mahoutsukai.render.FogRenderLayer.endFrame();}
}
''')
put('render/particle/ProjectedFogParticle.java','''package stepsword.mahoutsukai.render.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import stepsword.mahoutsukai.render.*;
public final class ProjectedFogParticle extends Particle {
 public static final java.util.Set<ProjectedFogParticle> active=new java.util.LinkedHashSet<>();
 private final float[] rgba;private final float size,softness;private final boolean shaders;
 private ProjectedFogParticle(ClientLevel level,net.minecraft.core.BlockPos pos,stepsword.mahoutsukai.util.FogProjectorValues v){
  super(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5);lifetime=800;hasPhysics=false;size=v.particle_fog_size;softness=v.particle_fog_thickness;shaders=v.particle_fog_shaders;
  rgba=new float[]{v.particle_fog_color_r/255f,v.particle_fog_color_g/255f,v.particle_fog_color_b/255f,v.particle_fog_color_a/255f};
  var d=new net.minecraft.world.phys.Vec3(v.particle_fog_direction_x,v.particle_fog_direction_y,v.particle_fog_direction_z).normalize();d=new net.minecraft.world.phys.Vec3(v.particle_fog_random_x?random.nextDouble()*2-1:d.x,v.particle_fog_random_y?random.nextDouble()*2-1:d.y,v.particle_fog_random_z?random.nextDouble()*2-1:d.z).normalize().scale(v.particle_fog_speed);xd=d.x;yd=d.y;zd=d.z;active.add(this);
 }
 public static void spawn(ClientLevel level,net.minecraft.core.BlockPos pos,stepsword.mahoutsukai.util.FogProjectorValues v){net.minecraft.client.Minecraft.getInstance().particleEngine.add(new ProjectedFogParticle(level,pos,v));}
 public ParticleRenderType getGroup(){return ParticleRenderType.NO_RENDER;}
 public void tick(){xo=x;yo=y;zo=z;x+=xd;y+=yd;z+=zd;if(++age>=lifetime)remove();}
 public void remove(){super.remove();active.remove(this);}
 public void render(net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext context){
  var mc=net.minecraft.client.Minecraft.getInstance();var camera=mc.gameRenderer.getMainCamera().position();float partial=mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);var pose=context.poseStack();pose.pushPose();pose.translate(xo+(x-xo)*partial-camera.x,yo+(y-yo)*partial-camera.y,zo+(z-zo)*partial-camera.z);RenderUtils.billboard(pose,0,partial);pose.scale(size,size,size);
  var base=MahoujinRenderType.createMahoujinRenderType(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","textures/particle/depth.png"),0,false);
  var type=shaders&&!RenderUtils.turnOffMahouShaders()?FogRenderLayer.wrap(base,rgba,softness):base;
  context.submitNodeCollector().submitCustomGeometry(pose,type,(nativePose,bb)->{var matrix=nativePose.pose();for(int i=0;i<4;i++){float px=(i<2?-.5f:.5f),pz=(i==0||i==3?-.5f:.5f);bb.addVertex(matrix,px,0,pz).setColor(shaders?1:rgba[0],shaders?1:rgba[1],shaders?1:rgba[2],shaders?1:rgba[3]).setUv(i<2?0:1,i==0||i==3?0:1).setUv2(240,240);}});pose.popPose();
 }
}
''')
edit('fabric/ProjectorClient.java','stepsword.mahoutsukai.render.particle.ParticleFog.spawnProjector(', 'stepsword.mahoutsukai.render.particle.ProjectedFogParticle.spawn(')
edit('fabric/ProjectorClient.java','  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(ProjectorClient::renderArmor);','''  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(ProjectorClient::renderArmor);
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{for(var particle:java.util.List.copyOf(stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active))particle.render(context);});''')
edit('fabric/ProjectorClient.java','fogPositions.clear();stepsword.mahoutsukai.client.ClientHandler.tint=false;','fogPositions.clear();stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active.clear();stepsword.mahoutsukai.client.ClientHandler.tint=false;')
p=root/'MahouTsukai-Fabric/src/main/resources/mahoutsukai.fabric.mixins.json';s=p.read_text().replace('  "client": [','  "client": [\n    "MahouShaderUniformMixin",\n    "MahouShaderFrameMixin",');p.write_text(s)
for name in ['clip.vsh','clip.fsh','fog.vsh','fog.fsh']:
 source=root/'recovered/assets/mahoutsukai/shaders/core'/name;target=root/'MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai/shaders/core'/name;shutil.copy2(source,target)
print('Connected original clipping and soft-depth fog shaders through native GPU uniforms')
