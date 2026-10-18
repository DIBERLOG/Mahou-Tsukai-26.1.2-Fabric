"""Historical one-shot native Gandr geometry and particles; original formulas retained."""
from pathlib import Path
import re,json,shutil
base=Path('MahouTsukai-Fabric/src');orig=Path('recovered/stepsword/mahoutsukai');main=base/'main/java/stepsword/mahoutsukai';client=base/'client/java/stepsword/mahoutsukai';res=base/'main/resources'
def put(rel,s):
 p=client/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8');return p
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
def method(text,needle):
 a=text.index(needle);start=text.index('{',a);n=1;i=start+1
 while n:
  n+=(text[i]=='{')-(text[i]=='}');i+=1
 return text[a:i]
def unwrap(s,name):
 while name+'(' in s:
  a=s.index(name+'(');b=a+len(name)+1;n=1;i=b
  while n:n+=(s[i]=='(')-(s[i]==')');i+=1
  s=s[:a]+s[b:i-1]+s[i:]
 return s
put('render/DummyEntityRenderState.java',(orig/'render/DummyEntityRenderState.java').read_text(encoding='utf-8'))
s=(orig/'render/entity/RenderGandrEntity.java').read_text(encoding='utf-8');s=unwrap(s,'MahoujinRenderType.wrapCullLayer');s=s.replace('float partialTicks) {\n      float maxsize','float partialTicks,SubmitNodeCollector buf) {\n      float maxsize').replace('entity.yRot','entity.getYRot()').replace('entity.xRot','entity.getXRot()');s=s.replace('      BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();','');s=re.sub(r'\s*buf.endBatch\([^;]+\);','',s);s=s.replace('      MultiBufferSource buffer,','      SubmitNodeCollector buffer,');s=s.replace('      Matrix4f positionMatrix = matrix.last().pose();\n      VertexConsumer bb = buffer.getBuffer(type);','      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n      Matrix4f positionMatrix = pose.pose();')
s=s.replace('      matrix.popPose();\n      return type;','      });\n      matrix.popPose();\n      return type;')
s=s.replace('      for (int i = (int)(num_segments * pb);','      int firstIndex=(int)(num_segments*pb);\n      for (int i = firstIndex;')
s=s.replace('         bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F)', '         if(i==firstIndex)bb.addVertex(positionMatrix,(float)x,(float)y,(float)z).setColor(r,g,b,a).setUv(texx,0).setUv2(lx,ly);\n         bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F)')
s=s.replace('      });\n      matrix.popPose();\n      return type;', '      bb.addVertex(positionMatrix,(float)xb,(float)yb,(float)zb).setColor(r,g,b,a).setUv(texx,1).setUv2(lx,ly);\n      });\n      matrix.popPose();\n      return type;')
put('render/entity/RenderGandrEntity.java',s)
source=(orig/'render/RenderUtils.java').read_text(encoding='utf-8')
# Select the original explicit-isolation ring implementation.
a=source.index('   public static RenderType renderRing(',source.index('return renderRing(matrix, buffer, cy, flatness, radius, height, num_segments, lx, ly, r, g, b, a, type, false);'))
ring=method(source[a:],'   public static RenderType renderRing(').replace('MultiBufferSource buffer','net.minecraft.client.renderer.SubmitNodeCollector buffer')
ring=ring.replace('      Matrix4f positionMatrix = matrix.last().pose();\n      VertexConsumer bb = buffer.getBuffer(type);','      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n      org.joml.Matrix4f positionMatrix=pose.pose();')
ring=ring.replace('      matrix.popPose();\n      return type;','      bb.addVertex(positionMatrix,(float)xb,(float)yb,(float)zb).setColor(r,g,b,a).setUv(texx,1).setUv2(lx,ly);\n      });\n      matrix.popPose();\n      return type;')
# Copy original sphere loops, separating both latitude strips and neighboring submitted spheres.
a=source.index('   public static void renderSphere(',source.index('renderSphere(matrix, buf, radius, gradation, lx, ly, r, g, b, a, type, percentage, false);'))
sphere=method(source[a:],'   public static void renderSphere(').replace('MultiBufferSource buf','net.minecraft.client.renderer.SubmitNodeCollector buf')
sphere=sphere.replace('      VertexConsumer bb = buf.getBuffer(type);\n      Matrix4f m = matrix.last().pose();','      buf.submitCustomGeometry(matrix,type,(pose,bb)->{\n      org.joml.Matrix4f m=pose.pose();')
sphere=sphere[:-1]+'      });\n   }'
p=client/'render/RenderUtils.java';s=p.read_text(encoding='utf-8');a=s.rfind('}');extra='''
 public static net.minecraft.client.renderer.rendertype.RenderType renderRing(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,type,true);}
 public static void renderSphere(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,float radius,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type){renderSphere(matrix,collector,radius,segments,lx,ly,r,g,b,a,type,1,true);}
'''
ring=ring.replace('PoseStack matrix','com.mojang.blaze3d.vertex.PoseStack matrix').replace('RenderType','net.minecraft.client.renderer.rendertype.RenderType').replace('EffectUtil.','stepsword.mahoutsukai.util.EffectUtil.')
sphere=sphere.replace('PoseStack matrix','com.mojang.blaze3d.vertex.PoseStack matrix').replace('RenderType','net.minecraft.client.renderer.rendertype.RenderType')
s=s[:a]+extra+ring+'\n'+sphere+'\n'+s[a:];p.write_text(s,encoding='utf-8')
# Native particle implementation uses original six textures, motion, fade, colors and lifespan.
put('render/particle/ParticleMagic.java','''package stepsword.mahoutsukai.render.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
public final class ParticleMagic extends SingleQuadParticle {
 private static SpriteSet sprites;
 public ParticleMagic(ClientLevel world,double x,double y,double z,float scale,double vx,double vy,double vz,String texture,float size,float r,float g,float b,float a,boolean shaders){
  super(world,x,y,z,sprites.get(net.minecraft.util.RandomSource.create()));
  xd=vx;yd=vy;zd=vz;rCol=r;gCol=g;bCol=b;alpha=a;quadSize=size*.5f;float max=g==1?25+size*10:50+size*100;lifetime=(int)((int)(max/(Math.random()*.3+.6))*scale);hasPhysics=false;
 }
 public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();setPos(x+xd,y+yd,z+zd);xd*=.6;yd*=.6;zd*=.6;alpha=1-(float)age/lifetime;oRoll=roll;}
 protected Layer getLayer(){return Layer.TRANSLUCENT;}
 public int getLightColor(float partial){return 0xF000F0;}
 public static final class Provider implements ParticleProvider<net.minecraft.core.particles.SimpleParticleType>{
  public Provider(SpriteSet spriteSet){sprites=spriteSet;}
  public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type,ClientLevel world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.util.RandomSource random){return new ParticleMagic(world,x,y,z,.5f,vx,vy,vz,"textures/particle/white_magic_particle",.2f,1,0,0,1,true);}
 }
}
''')
s=(orig/'render/particle/ParticleSmokeRing.java').read_text(encoding='utf-8');s=unwrap(s,'MahoujinRenderType.wrapCullLayer');s=s.replace('MahoujinRenderType.wrapBlendLayer(', 'blend(')
s=s.replace('   public String loc;','''   private static RenderType blend(RenderType type){return stepsword.mahoutsukai.render.MahouWrappedRenderTypes.wrapBlend(type,"gandr_smoke_ring");}
   public static final java.util.Set<ParticleSmokeRing> active=java.util.concurrent.ConcurrentHashMap.newKeySet();
   public String loc;''')
s=s.replace('      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);','      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);active.add(this);')
s=s.replace('PoseStack matrix, float partialTicks) {','PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {').replace('         BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();','')
put('render/particle/ParticleSmokeRing.java',s)
# Preserve original launch/hit particle algorithms and Rodrigues-style rotation helper.
source=(orig/'networking/ClientPacketHandler.java').read_text(encoding='utf-8');parts=[method(source,'   public static void magicParticleHandler('),method(source,'   private static void spawnGandrLaunchSmokeRing('),method(source,'   public static Vec3 rotateVecAroundVec(')]
put('fabric/GandrClientParticles.java','''package stepsword.mahoutsukai.fabric;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket;
import stepsword.mahoutsukai.render.particle.ParticleMagic;
import stepsword.mahoutsukai.render.particle.ParticleSmokeRing;
public final class GandrClientParticles {
'''+ '\n'.join(parts)+'\n}\n')
# Config for the original ring quality.
p=main/'config/MTConfig.java';s=p.read_text(encoding='utf-8').replace('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES','    public static int SMOKE_RING_PARTICLE_QUALITY=24;\n    private static final java.util.Map<String,double[]> ORIGINAL_RANGES').replace('ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n        java.util.Map.entry("SMOKE_RING_PARTICLE_QUALITY",new double[]{2,64}),');p.write_text(s,encoding='utf-8')
p=main/'render/particle/ModParticles.java';s=p.read_text(encoding='utf-8').replace(' public static void init() {}',' private static final net.minecraft.core.particles.SimpleParticleType MAGIC_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","native_magic"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));\n public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> MAGIC=()->MAGIC_TYPE;\n public static void init() {}');p.write_text(s,encoding='utf-8')
p=res/'assets/mahoutsukai/particles/native_magic.json';p.write_text(json.dumps({'textures':['mahoutsukai:white_magic_particle'+str(i) for i in range(1,7)]})+'\n',encoding='utf-8')
for i in range(1,7):
 f=f'assets/mahoutsukai/textures/particle/white_magic_particle{i}.png';shutil.copyfile(Path('recovered')/f,res/f)
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.gandr.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.GANDR.get(),stepsword.mahoutsukai.render.entity.RenderGandrEntity::new);
  net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(stepsword.mahoutsukai.render.particle.ModParticles.MAGIC.get(),stepsword.mahoutsukai.render.particle.ParticleMagic.Provider::new);
  stepsword.mahoutsukai.entity.mahoujin.GandrEntity.clientParticles=entity->{var rgb=entity.getColors();var mc=net.minecraft.client.Minecraft.getInstance();mc.particleEngine.add(new stepsword.mahoutsukai.render.particle.ParticleMagic(mc.level,entity.getX(),entity.getY(),entity.getZ(),.5f,0,0,0,"textures/particle/white_magic_particle",.2f,rgb[0],rgb[1],rgb[2],1,true));};
  ClientPlayNetworking.registerGlobalReceiver(MagicCircleBoomParticlePacket.TYPE,(packet,context)->GandrClientParticles.magicParticleHandler(packet));
  ClientPlayNetworking.registerGlobalReceiver(SettingsMahouPacket.TYPE,(packet,context)->stepsword.mahoutsukai.util.Utils.getSettingsMahou(context.player()).copyMahou(packet.mahou));
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{
   var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null)return;var camera=mc.gameRenderer.getMainCamera().position();float partial=mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.RenderGandrEntity.renderGandr(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());
   var iterator=stepsword.mahoutsukai.render.particle.ParticleSmokeRing.active.iterator();while(iterator.hasNext()){var particle=iterator.next();if(!particle.isAlive()){iterator.remove();continue;}stepsword.mahoutsukai.render.particle.ParticleSmokeRing.renderSmokeRing(particle,camera.x,camera.y,camera.z,context.poseStack(),partial,context.submitNodeCollector());}
  });
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->stepsword.mahoutsukai.render.particle.ParticleSmokeRing.active.clear());''');p.write_text(s,encoding='utf-8')
