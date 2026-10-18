from remaining_port_helpers import *
import json
p=main/'effects/mystic/RhoAiasSpellEffect.java';s=p.read_text().replace('world.getEntities().getAll()','world.getAllEntities()');write(p,s)
p=main/'entity/mahoujin/RhoAiasMahoujinEntity.java';s=p.read_text();a=s.index('   @Nullable\n   public AABB getBoundingBox()');b=s.index('   @Override',a);s=s[:a]+s[b:];s=s.replace(' && Utils.getRegistryKey(e, this.level()) != null','');write(p,s)
p=res/'mahoutsukai.accesswidener';s=p.read_text();line='accessible field net/minecraft/world/entity/projectile/arrow/AbstractArrow life I';write(p,s+'\n'+line+'\n') if line not in s else None
p=main/'fabric/mixin/RhoExplosionMixin.java';write(p,'''package stepsword.mahoutsukai.fabric.mixin;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.*;
import stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect;
@Mixin(ServerExplosion.class)
public abstract class RhoExplosionMixin {
 @Shadow @Final private ServerLevel level;
 @ModifyReturnValue(method="calculateExplodedPositions",at=@At("RETURN"))
 private List<BlockPos> mahoutsukai$shieldBlocks(List<BlockPos> blocks){return RhoAiasSpellEffect.protectBlocks(level,blocks);}
 @ModifyExpressionValue(method="hurtEntities",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
 private List<Entity> mahoutsukai$shieldEntities(List<Entity> entities){return RhoAiasSpellEffect.protectEntities(level,entities);}
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins']+=['RhoExplosionMixin'] if 'RhoExplosionMixin' not in j['mixins'] else [];write(p,json.dumps(j,indent=2)+'\n')
p=client/'render/entity/RenderRhoAiasMahoujinEntity.java';s=(orig/'render/entity/RenderRhoAiasMahoujinEntity.java').read_text().replace('float partialTicks) {','float partialTicks,SubmitNodeCollector buffer) {').replace('entity.yRot %','entity.getYRot() %').replace('      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();','');s=re.sub(r'      buffer.endBatch\([^;]+;\n','',s)
a=s.index('      VertexConsumer bb = buffer.getBuffer(type);');b=s.index('      float height',a);quad=s[a:b];quad=quad[quad.index('      Matrix4f m'):];quad=quad.replace('matrix.last().pose()','pose.pose()');s=s[:a]+'      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n'+quad+'      });\n'+s[b:]
a=s.index('      bb = buffer.getBuffer(type);');b=s.index('      RenderUtils.rotateQ(15.0F',a);quad=s[a:b];quad=quad[quad.index('      m ='):].replace('      m = matrix.last().pose();','      Matrix4f m = pose.pose();').replace('yy','quadY');s=s[:a]+'      final float quadY=yy;buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n'+quad+'      });\n'+s[b:];write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text();line='  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.RHO_AIAS.get(),stepsword.mahoutsukai.render.entity.RenderRhoAiasMahoujinEntity::new);';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s
anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';i=s.index(anchor);line='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.RenderRhoAiasMahoujinEntity.renderRhoAias(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());\n';s=s[:i]+line+s[i:] if line not in s else s;write(p,s)
