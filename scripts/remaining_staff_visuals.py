from remaining_port_helpers import *
exec(Path('scripts/finish_weapon_visuals.py').read_text().split('for cls,entitycls,field,method,buffer in ')[0])
for cls,field,method in [('MysticStaffMahoujinEntity','EXPLOSION_MAHOUJIN','renderCircle'),('MysticStaffBeamMahoujinEntity','BEAM_MAHOUJIN','renderMysticStaffBeam'),('BeamProjectileEntity','BEAM_PROJECTILE','renderBeamProjectile')]:
 name='Render'+cls;s=(orig/('render/entity/'+name+'.java')).read_text();m=re.search(r'public static void (\w+)\('+cls,s);method=m[1]
 s=s.replace('float partialTicks) {','float partialTicks,SubmitNodeCollector buf) {',1)
 if cls=='MysticStaffBeamMahoujinEntity':
  s=s.replace('circleRender(lightmapX, lightmapY, r, g, b, a, smallcircles + 1, matrix);','circleRender(lightmapX, lightmapY, r, g, b, a, smallcircles + 1, matrix,buf);').replace('circleRender(lightmapX, lightmapY, r, g, b, a, i, matrix);','circleRender(lightmapX, lightmapY, r, g, b, a, i, matrix,buf);').replace('int index, PoseStack matrix) {','int index, PoseStack matrix,SubmitNodeCollector buf) {')
  s=s.replace('VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(type);','VertexConsumer bb = buf.getBuffer(type);').replace('buffer.addVertex(','bb.addVertex(').replace('      Minecraft.getInstance().renderBuffers().bufferSource().endBatch(type);','')
 write(client/('render/entity/'+name+'.java'),geometry(s))
 p=client/'fabric/SixFamilyClient.java';insert(p,' public static void init(){',f' public static void init(){{\n  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.{field}.get(),stepsword.mahoutsukai.render.entity.{name}::new);\n' ) if False else None
 s=p.read_text();line=f'  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.{field}.get(),stepsword.mahoutsukai.render.entity.{name}::new);';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s
 anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';line=f'   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.{cls}.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.{name}.{method}(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());\n';s=s.replace(anchor,line+anchor);write(p,s)
# AOE circle has the same original casting circle geometry, the beam flag is absent.
s=(client/'render/entity/RenderMysticStaffMahoujinEntity.java').read_text().replace('RenderMysticStaffMahoujinEntity','RenderMysticStaffAOEMahoujinEntity').replace('MysticStaffMahoujinEntity','MysticStaffAOEMahoujinEntity').replace('boolean renderBeam = entity.hasBeam();','boolean renderBeam = false;').replace('MysticStaff.getBeamSize(entity, partialTicks)','0f').replace('entity.getBeamOffset()','0').replace('entity.getBeamLength()','0');write(client/'render/entity/RenderMysticStaffAOEMahoujinEntity.java',s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text();s=s.replace(' public static void init(){',' public static void init(){\n  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.AOE_EXPLOSION_MAHOUJIN.get(),stepsword.mahoutsukai.render.entity.RenderMysticStaffAOEMahoujinEntity::new);\n  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.BUTTERFLY.get(),stepsword.mahoutsukai.render.entity.RenderButterfly::new);');anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';s=s.replace(anchor,'   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.RenderMysticStaffAOEMahoujinEntity.renderCircle(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());\n'+anchor);write(p,s)
write(client/'render/entity/RenderButterfly.java',(orig/'render/entity/RenderButterfly.java').read_text())
p=client/'render/RenderUtils.java';append(p,' public static void renderSphere(com.mojang.blaze3d.vertex.PoseStack m,net.minecraft.client.renderer.SubmitNodeCollector c,float radius,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type,float percentage){renderSphere(m,c,radius,segments,lx,ly,r,g,b,a,type,percentage,true);}\n')
p=client/'render/item/MysticStaffRenderer.java';s=(orig/'render/item/MysticStaffRenderer.java').read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace('getStandaloneModel','getModel');write(p,s)
p=client/'render/RenderBaseItem.java';insert(p,' public static void init() {',' public static final ModelKeyPair mystic_staff=new ModelKeyPair("mahoutsukai:mystic_staff_model");\n');s=p.read_text();s=s.replace(' public static void init() {',' public static void init() {\n  ModelLoadingPlugin.register(context->context.addModel(mystic_staff.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_staff_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));');write(p,s)
p=client/'render/item/MahouSpecialItemModel.java';insert(p,'            if (renderer.equals("spatial_disorientation_staff"))','            if (renderer.equals("mystic_staff")) return new MysticStaffRenderer();\n')
# Original beam sound and explosion particles stay on the client.
for rel in ['sounds/BeamMovingSound.java','render/particle/ParticleSphereExplosion.java']:
 s=(orig/rel).read_text();write(client/rel,s)
p=client/'render/particle/ParticleSphereExplosion.java';s=p.read_text();s=s.replace('float partialTicks) {','float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {');s=geometry(s);s=s.replace('BufferSource buf','SubmitNodeCollector buf');write(p,s)
# Original particle packet visual handlers (native client dispatch).
source=(orig/'networking/ClientPacketHandler.java').read_text();a=source.index('   public static void handleBakuretsu');b=source.index('   public static void insightHandler',a)
methods=source[a:b];a=source.index('   public static void beamHandler');b=source.index('   public static void faeNoiseHandler',a);methods+=source[a:b]
write(client/'client/StaffClientEffects.java','''package stepsword.mahoutsukai.client;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.networking.*;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.Bakuretsu;
import stepsword.mahoutsukai.render.particle.*;
import stepsword.mahoutsukai.sounds.BeamMovingSound;
public final class StaffClientEffects {
'''+methods+'}\n')
p=client/'fabric/SixFamilyClient.java';s=p.read_text();s=s.replace(' public static void init(){',' public static void init(){\n  ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.BakuretsuPacket.TYPE,(packet,context)->stepsword.mahoutsukai.client.StaffClientEffects.handleBakuretsu(packet));\n  ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.BeamPacket.TYPE,(packet,context)->stepsword.mahoutsukai.client.StaffClientEffects.beamHandler(packet));');write(p,s)
