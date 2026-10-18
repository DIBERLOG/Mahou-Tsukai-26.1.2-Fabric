package stepsword.mahoutsukai.fabric;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import stepsword.mahoutsukai.tile.*;
public final class ProjectorClient {
 public static final java.util.Set<BlockPos> fogPositions=new java.util.HashSet<>();
 public static void box(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,int color){
  collector.submitCustomGeometry(pose,stepsword.mahoutsukai.render.MahoujinRenderType.createLinesNoDepthRenderType(),(nativePose,vertices)->{var matrix=new com.mojang.blaze3d.vertex.PoseStack();matrix.last().set(nativePose);net.minecraft.client.renderer.ShapeRenderer.renderShape(matrix,vertices,net.minecraft.world.phys.shapes.Shapes.block(),0,0,0,color,2);});
 }
 public static void renderArmor(net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext context){
  var mc=Minecraft.getInstance();if(mc.player==null)return;var camera=mc.gameRenderer.getMainCamera().position();float partial=mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
  for(var living:mc.level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,mc.player.getBoundingBox().inflate(128))){
   if(living==mc.getCameraEntity()&&mc.options.getCameraType().isFirstPerson())continue;
   for(var slot:net.minecraft.world.entity.EquipmentSlotGroup.ARMOR){var enchant=living.getItemBySlot(slot).get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);if(enchant==null)continue;
    for(var values:enchant.getPv()){var pose=context.poseStack();pose.pushPose();var pos=living.getPosition(partial);pose.translate(pos.x-camera.x,pos.y-camera.y,pos.z-camera.z);
     float yaw=slot==net.minecraft.world.entity.EquipmentSlot.HEAD?net.minecraft.util.Mth.rotLerp(partial,living.yHeadRotO,living.yHeadRot):net.minecraft.util.Mth.rotLerp(partial,living.yBodyRotO,living.yBodyRot);
     stepsword.mahoutsukai.render.RenderUtils.rotateQ(-yaw,0,1,0,pose);
     stepsword.mahoutsukai.render.ProjectorRenderer.doProjectorRender(true,values,pose,new stepsword.mahoutsukai.render.RenderBufferWrapper.SubmitNodeCollectorWrapper(context.submitNodeCollector()),partial,0,0,slot==net.minecraft.world.entity.EquipmentSlot.HEAD?living.getXRot():0);pose.popPose();
    }
   }
  }
 }
 public static void init(){
  net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack,context,type,lines)->{stepsword.mahoutsukai.render.gui.RenderStrengthening.strengthenTooltip(stack,lines);stepsword.mahoutsukai.render.gui.RenderPowerConsolidationInnateCap.innateCapTooltip(stack,lines);stepsword.mahoutsukai.render.gui.RenderRuleBreakerModulus.modulusTooltip(stack,lines);});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player!=null&&mc.screen==null&&stepsword.mahoutsukai.Keybinds.settingsGUI.consumeClick())mc.setScreen(new stepsword.mahoutsukai.render.gui.PlayerSettingsGUI(mc.level,mc.player.getUUID()));});
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{var mc=Minecraft.getInstance();if(mc.getCameraEntity()!=null)stepsword.mahoutsukai.render.overlay.RenderLeyLines.leyRender(mc.getCameraEntity(),context.poseStack(),mc.getDeltaTracker().getGameTimeDeltaPartialTick(false),context.submitNodeCollector());});
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(ProjectorClient::renderArmor);
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{for(var particle:java.util.List.copyOf(stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active))particle.render(context);});
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(ModTileEntities.fogProjector.get(),stepsword.mahoutsukai.render.tile.FogProjectorRenderer::new);
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.AUTHORITY.get(),stepsword.mahoutsukai.render.entity.RenderAuthorityMahoujinEntity::new);
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.RHONGOMYNIAD.get(),stepsword.mahoutsukai.render.entity.RenderRhongomyniadEntity::new);
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(ModTileEntities.projector.get(),stepsword.mahoutsukai.render.tile.MahoujinProjectorRenderer::new);
  net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,level,hand,hit)->{
   if(!level.isClientSide()||player.isSpectator()||stepsword.mahoutsukai.potion.BoundPotion.boundRightClickBlock(player))return InteractionResult.PASS;
   var tile=level.getBlockEntity(hit.getBlockPos());
   if(player.getItemInHand(hand).is(net.minecraft.world.item.Items.STICK))return InteractionResult.PASS;
   if(tile instanceof MahoujinProjectorTileEntity && stepsword.mahoutsukai.util.Utils.isArmor(player.getMainHandItem())){
    var action=stepsword.mahoutsukai.Keybinds.changeMysticCode.isDown()?stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.ADD:stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REPLACE;
    if(stepsword.mahoutsukai.Keybinds.drawMahoujin.isDown())action=stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REMOVE;
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.ProjectorRightClickPacket(action,hit.getBlockPos()));return InteractionResult.SUCCESS;
   }
   if(tile instanceof MahoujinProjectorTileEntity){Minecraft.getInstance().setScreen(new stepsword.mahoutsukai.render.gui.MahoujinProjectorScreen(level,hit.getBlockPos()));return InteractionResult.SUCCESS;}
   if(tile instanceof FogProjectorTileEntity){Minecraft.getInstance().setScreen(new stepsword.mahoutsukai.render.gui.FogProjectorScreen(level,hit.getBlockPos()));return InteractionResult.SUCCESS;}
   return InteractionResult.PASS;
  });
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register((level,chunk)->{for(var tile:chunk.getBlockEntities().values())if(tile instanceof FogProjectorTileEntity)fogPositions.add(tile.getBlockPos().immutable());});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD.register((level,chunk)->fogPositions.removeIf(pos->net.minecraft.world.level.ChunkPos.containing(pos).equals(chunk.getPos())));
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,mc)->{fogPositions.clear();stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active.clear();stepsword.mahoutsukai.client.ClientHandler.tint=false;stepsword.mahoutsukai.render.ProjectorRenderer.cacheClear();});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.level==null||mc.player==null)return;
   boolean abyss=mc.getCameraEntity() instanceof net.minecraft.world.entity.LivingEntity living&&living.hasEffect(stepsword.mahoutsukai.potion.ModEffects.ABYSS);
   if(stepsword.mahoutsukai.client.ClientHandler.tint!=abyss){stepsword.mahoutsukai.client.ClientHandler.tint=abyss;mc.levelRenderer.allChanged();}
   // Newly placed block entities can arrive after chunk loading.
   if(mc.level.getGameTime()%20==0){var center=mc.player.chunkPosition();for(int x=center.x()-2;x<=center.x()+2;x++)for(int z=center.z()-2;z<=center.z()+2;z++){var chunk=mc.level.getChunkSource().getChunk(x,z,false);if(chunk!=null)for(var tile:chunk.getBlockEntities().values())if(tile instanceof FogProjectorTileEntity)fogPositions.add(tile.getBlockPos().immutable());}}
   fogPositions.removeIf(pos->!mc.level.hasChunkAt(pos)||!(mc.level.getBlockEntity(pos) instanceof FogProjectorTileEntity));
   for(var pos:fogPositions){if(mc.level.hasNeighborSignal(pos)||mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos))>128*128)continue;var tile=(FogProjectorTileEntity)mc.level.getBlockEntity(pos);var v=tile.pv;
    if(v.particle_fog_frequency_ticks>0&&mc.level.getGameTime()%v.particle_fog_frequency_ticks==0)for(int i=0;i<Math.min(100,v.particle_fog_frequency_num);i++)stepsword.mahoutsukai.render.particle.ProjectedFogParticle.spawn(mc.level,pos,v);
   }
  });
 }
 public static void applyFog(net.minecraft.world.entity.Entity camera,net.minecraft.client.renderer.fog.FogData fog){
  if(camera==null)return;double weight=1,r=fog.color.x,g=fog.color.y,b=fog.color.z,strength=0;
  for(var pos:fogPositions){if(!camera.level().hasChunkAt(pos)||camera.level().hasNeighborSignal(pos)||!(camera.level().getBlockEntity(pos) instanceof FogProjectorTileEntity tile))continue;var v=tile.pv;
   double d=camera.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(pos));if(!v.camera_fog_toggle||v.camera_fog_radius<=0||d>=v.camera_fog_radius)continue;
   double attenuation=1-d/v.camera_fog_radius;
   double w=2*attenuation;weight+=w;r+=v.camera_fog_color_r/255.0*w;g+=v.camera_fog_color_g/255.0*w;b+=v.camera_fog_color_b/255.0*w;
   // The original doubles only the colour weight, not the camera fog strength.
   strength+=v.camera_fog_strength*attenuation;
  }
  if(weight==1)return;fog.color.set((float)(r/weight),(float)(g/weight),(float)(b/weight),fog.color.w);
  double scale=Math.pow(1-Math.min(strength,60)/60,4);float start=fog.environmentalStart,end=fog.environmentalEnd;
  fog.environmentalStart=start==0?0:Math.min(start,Math.max(1,(float)(start*scale)));fog.environmentalEnd=Math.min(end,Math.max(4,(float)(end*scale)));
 }
}
