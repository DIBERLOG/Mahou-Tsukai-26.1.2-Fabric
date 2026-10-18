from pathlib import Path
import re, json, shutil
root=Path(__file__).resolve().parents[1]
orig=root/'recovered/stepsword/mahoutsukai'
main=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
def put(base,name,s):
 p=base/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def edit(base,name,a,b):
 s=(base/name).read_text(encoding='utf-8');assert a in s,(name,a);put(base,name,s.replace(a,b))
for name in ['render/ProjectorRenderer.java','render/GifInfo.java','render/GifLoader.java','render/ThreadDownloadImageData.java','util/ClientImageDownloadUtil.java','render/tile/ProjectorRenderState.java']:
 s=(orig/name).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable')
 put(client,name,s)
# Bound remote texture reads; URLs come from the local client configuration.
edit(client,'render/ThreadDownloadImageData.java','               httpurlconnection.setDoInput(true);','               httpurlconnection.setConnectTimeout(10000);\n               httpurlconnection.setReadTimeout(10000);\n               httpurlconnection.setDoInput(true);')
edit(client,'render/ThreadDownloadImageData.java','byte[] bytes = stream.readAllBytes();','byte[] bytes = stream.readNBytes(16 * 1024 * 1024 + 1);\n                     if(bytes.length > 16 * 1024 * 1024) throw new IOException("Projector image exceeds 16 MiB");')
edit(client,'render/ThreadDownloadImageData.java','FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadImageData.this.cacheFile);','''try(InputStream source=httpurlconnection.getInputStream()) {
                        byte[] data=source.readNBytes(16*1024*1024+1);
                        if(data.length>16*1024*1024)throw new IOException("Projector image exceeds 16 MiB");
                        FileUtils.writeByteArrayToFile(ThreadDownloadImageData.this.cacheFile,data);
                     }''')

s=(orig/'render/entity/RenderAuthorityMahoujinEntity.java').read_text()
s=s.replace('float partialTicks) {','float partialTicks,SubmitNodeCollector buffer) {')
s=s.replace('      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();\n      VertexConsumer bb = buffer.getBuffer(type);\n      Matrix4f m = matrix.last().pose();','      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n      Matrix4f m=pose.pose();')
s=s.replace('      float height = 0.125F;', '      });\n      float height = 0.125F;').replace('      buffer.endBatch(type);','')
put(client,'render/entity/RenderAuthorityMahoujinEntity.java',s)
s=(orig/'render/entity/RenderRhongomyniadEntity.java').read_text()
s=s.replace('float partialTicks) {\n      matrix.pushPose();','float partialTicks,SubmitNodeCollector buffer) {\n      matrix.pushPose();')
s=s.replace('      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();','').replace('      buffer.endBatch(runes1);','')
put(client,'render/entity/RenderRhongomyniadEntity.java',s)

edit(client,'render/RenderBaseItem.java',' public static void init() {',''' public static final ModelKeyPair rhongomyniad=new ModelKeyPair("mahoutsukai:item/rhongomyniad");
 public static void init() {
  ModelLoadingPlugin.register(context->context.addModel(rhongomyniad.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/rhongomyniad"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));''')
put(client,'render/tile/MahoujinProjectorRenderer.java','''package stepsword.mahoutsukai.render.tile;
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
  state.on=tile.getLevel()==null||!tile.getLevel().hasNeighborSignal(tile.getBlockPos());state.partialTicks=partial;
 }
 public void submit(ProjectorRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera){if(state.on)stepsword.mahoutsukai.render.ProjectorRenderer.render(state.pv,state.partialTicks,pose,collector);}
 public boolean shouldRenderOffScreen(){return true;}
 public int getViewDistance(){return 256;}
}
''')

# Client-owned projector registry is cleared on disconnect; no global world scan per tick.
put(client,'fabric/ProjectorClient.java','''package stepsword.mahoutsukai.fabric;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import stepsword.mahoutsukai.tile.*;
public final class ProjectorClient {
 public static final java.util.Set<BlockPos> fogPositions=new java.util.HashSet<>();
 public static void init(){
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.AUTHORITY.get(),stepsword.mahoutsukai.render.entity.RenderAuthorityMahoujinEntity::new);
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.RHONGOMYNIAD.get(),stepsword.mahoutsukai.render.entity.RenderRhongomyniadEntity::new);
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(ModTileEntities.projector.get(),stepsword.mahoutsukai.render.tile.MahoujinProjectorRenderer::new);
  net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,level,hand,hit)->{
   if(!level.isClientSide()||player.isSpectator())return InteractionResult.PASS;
   var tile=level.getBlockEntity(hit.getBlockPos());
   if(tile instanceof MahoujinProjectorTileEntity){Minecraft.getInstance().setScreen(new stepsword.mahoutsukai.render.gui.MahoujinProjectorScreen(level,hit.getBlockPos()));return InteractionResult.SUCCESS;}
   if(tile instanceof FogProjectorTileEntity){Minecraft.getInstance().setScreen(new stepsword.mahoutsukai.render.gui.FogProjectorScreen(level,hit.getBlockPos()));return InteractionResult.SUCCESS;}
   return InteractionResult.PASS;
  });
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register((level,chunk)->{for(var tile:chunk.getBlockEntities().values())if(tile instanceof FogProjectorTileEntity)fogPositions.add(tile.getBlockPos().immutable());});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD.register((level,chunk)->fogPositions.removeIf(pos->net.minecraft.world.level.ChunkPos.containing(pos).equals(chunk.getPos())));
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,mc)->fogPositions.clear());
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.level==null||mc.player==null)return;
   // Newly placed block entities can arrive after chunk loading.
   if(mc.level.getGameTime()%20==0){var center=mc.player.chunkPosition();for(int x=center.x()-2;x<=center.x()+2;x++)for(int z=center.z()-2;z<=center.z()+2;z++){var chunk=mc.level.getChunkSource().getChunk(x,z,false);if(chunk!=null)for(var tile:chunk.getBlockEntities().values())if(tile instanceof FogProjectorTileEntity)fogPositions.add(tile.getBlockPos().immutable());}}
   fogPositions.removeIf(pos->!mc.level.hasChunkAt(pos)||!(mc.level.getBlockEntity(pos) instanceof FogProjectorTileEntity));
   for(var pos:fogPositions){if(mc.level.hasNeighborSignal(pos)||mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos))>128*128)continue;var tile=(FogProjectorTileEntity)mc.level.getBlockEntity(pos);var v=tile.pv;
    if(v.particle_fog_frequency_ticks>0&&mc.level.getGameTime()%v.particle_fog_frequency_ticks==0)for(int i=0;i<Math.min(100,v.particle_fog_frequency_num);i++)mc.level.addParticle(stepsword.mahoutsukai.render.particle.ModParticles.FOG1.get(),pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,0,0,0);
   }
  });
 }
 public static void applyFog(net.minecraft.world.entity.Entity camera,net.minecraft.client.renderer.fog.FogData fog){
  if(camera==null)return;double weight=1,r=fog.color.x,g=fog.color.y,b=fog.color.z,strength=0;
  for(var pos:fogPositions){if(!camera.level().hasChunkAt(pos)||camera.level().hasNeighborSignal(pos)||!(camera.level().getBlockEntity(pos) instanceof FogProjectorTileEntity tile))continue;var v=tile.pv;
   double d=camera.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(pos));if(!v.camera_fog_toggle||v.camera_fog_radius<=0||d>=v.camera_fog_radius)continue;
   double w=1-d/v.camera_fog_radius;weight+=w;r+=v.camera_fog_color_r/255.0*w;g+=v.camera_fog_color_g/255.0*w;b+=v.camera_fog_color_b/255.0*w;strength+=v.camera_fog_strength*w;
  }
  if(weight==1)return;fog.color.set((float)(r/weight),(float)(g/weight),(float)(b/weight),fog.color.w);
  double scale=Math.pow(1-Math.min(strength,60)/60,4);float start=fog.environmentalStart,end=fog.environmentalEnd;
  fog.environmentalStart=start==0?0:Math.min(start,Math.max(1,(float)(start*scale)));fog.environmentalEnd=Math.min(end,Math.max(4,(float)(end*scale)));
 }
}
''')
edit(client,'fabric/MahouFabricClient.java','        SixFamilyClient.init();','        SixFamilyClient.init();\n        ProjectorClient.init();')
edit(client,'fabric/mixin/PowerLakeFogMixin.java','  stepsword.mahoutsukai.render.overlay.RenderPowerConsolidation.apply(camera.entity(),result.getReturnValue());','  stepsword.mahoutsukai.render.overlay.RenderPowerConsolidation.apply(camera.entity(),result.getReturnValue());\n  stepsword.mahoutsukai.fabric.ProjectorClient.applyFog(camera.entity(),result.getReturnValue());')
edit(client,'fabric/SixFamilyClient.java','   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity.class', '''   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.RenderAuthorityMahoujinEntity.renderAuthority(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());
   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.RenderRhongomyniadEntity.renderRhongomyniad(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());
   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity.class''')
edit(main,'config/MTConfig.java','public final class MTConfig {','''public final class MTConfig {
 public static java.util.List<String> MAGIC_CIRCLES=java.util.List.of();
 public static java.util.List<String> MAGIC_RUNES=java.util.List.of();
 public static boolean CACHE_IMAGES=true;''')
for a,b in [('"projector_values"','"projector_values_mahou"'),('"fog_projector_values"','"fog_projector_values_mahou"')]:
 edit(main,'datacomponents/DataComponents.java',a,b)
# Existing snapshots contain the complete original assets, without editing the source JAR.
res=root/'MahouTsukai-Fabric/src/main/resources'
assets=root/'recovered/assets/mahoutsukai'
for category in ['models','textures','blockstates','items']:
 if not (assets/category).exists():continue
 for path in (assets/category).rglob('*'):
  if path.is_file() and any(word in path.name for word in ['projector','rhongomyniad']):
   dest=res/'assets/mahoutsukai'/path.relative_to(assets);dest.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(path,dest)
for id in ['mahoujin_projector','fog_projector']:
 put(res,f'assets/mahoutsukai/items/{id}.json',json.dumps({'model':{'type':'minecraft:model','model':f'mahoutsukai:block/{id}'}})+'\n')
print('Connected projector screens/rendering, original GIF textures, camera fog and remaining entity renderers')
