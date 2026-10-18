from remaining_port_helpers import *
import json
# Native extra models expose the same original named references to each special renderer.
p=client/'render/RenderBaseItem.java';s=p.read_text();anchor=' public static void init() {';fields=''' public static final class ModelKeyPair {public final ExtraModelKey<QuadCollection> key;public ModelKeyPair(String name){key=ExtraModelKey.create(()->name);}}
''';models={'caliburn':'caliburn_model','morgan':'morgan_model','treasuryGauntlet':'treasury_projection_gauntlet_model','standby':'weapon_projectile_bow_standby'}
for field,name in models.items():fields+=f' public static final ModelKeyPair {field}=new ModelKeyPair("mahoutsukai:{name}");\n'
s=s.replace(anchor,fields+anchor) if 'class ModelKeyPair' not in s else s
for field,name in models.items():
 line=f'  ModelLoadingPlugin.register(context->context.addModel({field}.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/{name}"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));';s=s.replace(anchor,anchor+'\n'+line) if line not in s else s
write(p,s)
# Preserve actual use time for both local and remote held item rendering.
write(client/'client/ItemUseRenderBridge.java','''package stepsword.mahoutsukai.client;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
public final class ItemUseRenderBridge {
 private record Context(ItemStack stack,ItemDisplayContext display,float ticks){}
 private static final ThreadLocal<java.util.ArrayDeque<Context>> contexts=ThreadLocal.withInitial(java.util.ArrayDeque::new);
 public static void push(LivingEntity entity,ItemStack stack,ItemDisplayContext display){contexts.get().push(new Context(stack,display,entity!=null && entity.isUsingItem() && ItemStack.matches(entity.getUseItem(),stack)?entity.getTicksUsingItem():0));}
 public static void popContext(){var values=contexts.get();values.pop();if(values.isEmpty())contexts.remove();}
 public static float getUseTicks(ItemStack stack){var values=contexts.get();return values.isEmpty()?0:values.peek().ticks();}
 public static ItemDisplayContext getDisplayContext(ItemStack stack){var values=contexts.get();return values.isEmpty()?ItemDisplayContext.NONE:values.peek().display();}
}
''')
write(client/'fabric/mixin/NativeItemUseContextMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
@Mixin(ItemModelResolver.class)
public abstract class NativeItemUseContextMixin {
 @WrapMethod(method="updateForTopItem")
 private void mahoutsukai$context(ItemStackRenderState state,ItemStack stack,ItemDisplayContext display,Level level,ItemOwner owner,int seed,Operation<Void> original){stepsword.mahoutsukai.client.ItemUseRenderBridge.push(owner instanceof LivingEntity living?living:null,stack,display);try{original.call(state,stack,display,level,owner,seed);}finally{stepsword.mahoutsukai.client.ItemUseRenderBridge.popContext();}}
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['client']+=['NativeItemUseContextMixin'] if 'NativeItemUseContextMixin' not in j['client'] else [];write(p,json.dumps(j,indent=2)+'\n')
for cls in ['CaliburnRenderer','MorganRenderer','TreasuryProjectionGauntletRenderer','WeaponProjectileBowRenderer']:
 p=client/('render/item/'+cls+'.java');s=(orig/('render/item/'+cls+'.java')).read_text().replace('.getStandaloneModel(','.getModel(')
 if cls=='WeaponProjectileBowRenderer':
  s=s.replace('import stepsword.mahoutsukai.integration.ShaderIntegration;','').replace('import stepsword.mahoutsukai.render.IEntityAwareRenderer;','').replace('implements IEntityAwareRenderer, SpecialModelRenderer','implements SpecialModelRenderer')
  a=s.index('   @Override\n   public void render(LivingEntity');b=s.index('   private static void renderAmmo(\n      MagicalItemRenderState',a);s=s[:a]+s[b:]
  s=s.replace('return transform == ItemDisplayContext.NONE ? type : MahoujinRenderType.wrapCullLayer(ShaderIntegration.shaderify(type, transform));','return type;')
  a=s.index('      RenderType type = transform == ItemDisplayContext.NONE');b=s.index('      buffer.submit',a);s=s[:a]+'      RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.mahoujin,0,false);\n'+s[b:]
 write(p,s)
write(client/'render/geometry/SpiralGeometry.java',(orig/'render/geometry/SpiralGeometry.java').read_text())
p=client/'render/item/MahouSpecialItemModel.java';s=p.read_text()
for id,cls in [('caliburn','CaliburnRenderer'),('morgan','MorganRenderer'),('treasury_projection_gauntlet','TreasuryProjectionGauntletRenderer'),('weapon_projectile_bow','WeaponProjectileBowRenderer')]:
 line=f'            if (renderer.equals("{id}")) return new {cls}();';s=s.replace('            if (renderer.equals("rule_breaker"))',line+'\n            if (renderer.equals("rule_breaker"))') if line not in s else s
write(p,s)
# Original projectile item render state; the separate shader clipping pass remains part of global shader work.
for cls in ['RenderWeaponProjectile','RenderGateWeaponProjectile']:
 p=client/('render/entity/'+cls+'.java');s=(orig/('render/entity/'+cls+'.java')).read_text().replace('import stepsword.mahoutsukai.MahouTsukaiMod;','');s=re.sub(r'MahouTsukaiMod.jousting.treasuryProjectionRotation\([^;]+\)','0.0F',s)
 if cls=='RenderGateWeaponProjectile':
  a=s.index('      RenderUtils.renderClippedItem(');b=s.index('\n      );',a)+9;s=s[:a]+'      state.item.submit(matrix,submitNodeCollector,15728880,OverlayTexture.NO_OVERLAY,state.outlineColor);'+s[b:]
 write(p,s)
append(client/'render/RenderUtils.java',''' public static org.joml.Quaternionf getQuaternion(org.joml.Vector3f axis,float angle,boolean degrees){return new org.joml.Quaternionf().rotationAxis(degrees?(float)Math.toRadians(angle):angle,axis.x,axis.y,axis.z);}
''')
# Vanilla resource identities for all newly registered weapons, quest book, effects and advancements.
for id in ['caliburn','morgan','weapon_projectile_bow','treasury_projection_gauntlet','funbook']:
 for folder in ['items','models/item']:
  path=f'assets/mahoutsukai/{folder}/{id}.json';source=Path('recovered')/path
  if source.exists():target=res/path;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
for id in ['caliburn_model','morgan_model','treasury_projection_gauntlet_model','weapon_projectile_bow_standby']:
 path=f'assets/mahoutsukai/models/item/{id}.json';target=res/path;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered')/path,target)
for id in ['cm_cooldown','rage']:
 path=f'assets/mahoutsukai/textures/mob_effect/{id}.png';source=Path('recovered')/path
 if source.exists():shutil.copyfile(source,res/path)
for id in ['morgan','breadcrumbs','secret_calibur','sword_in_the_lake','treasury_projection']:
 path=f'data/mahoutsukai/advancement/{id}.json';source=Path('recovered')/path
 if source.exists():target=res/path;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
# Actual client packets and native renderer registrations.
p=client/'fabric/SixFamilyClient.java';s=p.read_text();lines='''  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.WEAPON_PROJECTILE.get(),stepsword.mahoutsukai.render.entity.RenderWeaponProjectile::new);
  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.GATE_WEAPON_PROJECTILE.get(),stepsword.mahoutsukai.render.entity.RenderGateWeaponProjectile::new);
  ClientPlayNetworking.registerGlobalReceiver(RagePacket.TYPE,(packet,context)->{if(context.client().level.getEntity(packet.entityids) instanceof net.minecraft.world.entity.LivingEntity entity){if(packet.rage)stepsword.mahoutsukai.util.EffectUtil.buff(entity,ModEffects.RAGE,false,stepsword.mahoutsukai.config.MTConfig.MORGAN_RAGE_TIME);else entity.removeEffect(ModEffects.RAGE);}});
''';s=s.replace(' public static void init(){',' public static void init(){\n'+lines) if lines not in s else s;write(p,s)
config()
