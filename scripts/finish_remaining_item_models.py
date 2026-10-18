from remaining_port_helpers import *
p=main/'item/william/WrittenEvaluation.java';s=p.read_text(encoding='utf-8');line=' private static final org.slf4j.Logger LOGGER=org.slf4j.LoggerFactory.getLogger("mahoutsukai");';s=s.replace(line,'');s=s.replace('public class WrittenEvaluation {','public class WrittenEvaluation {\n'+line);write(p,s)
p=main/'entity/mahoujin/WilliamEntity.java';s=p.read_text(encoding='utf-8').replace('      } else if (ModItems.rhongomyniad != null) {\n         this.setStack(new ItemStack(ModItems.rhongomyniad.get()));','');write(p,s)
append(main/'util/Utils.java',' public static void setFictionMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.fiction.FictionMahou value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT,value.copy());}\n')
p=res/'mahoutsukai.accesswidener';s=p.read_text(encoding='utf-8');s+='\naccessible method net/minecraft/world/entity/Entity collide (Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;\n';write(p,s)
# Native extra models, preserving all original item display variants.
for cls in ['MysticCodeRenderer','MysticCodeFirstSorceryRenderer','StaffEmrysRenderer','ReplicaRenderer','NobuRenderer']:
 write(client/('render/item/'+cls+'.java'),(orig/('render/item/'+cls+'.java')).read_text(encoding='utf-8').replace('.getStandaloneModel(','.getModel('))
p=client/'render/RenderBaseItem.java';s=p.read_text(encoding='utf-8');original=(orig/'render/RenderBaseItem.java').read_text(encoding='utf-8')
models={field:model for field,model in re.findall(r'public static RenderBaseItem.ModelKeyPair (\w+) = new RenderBaseItem.ModelKeyPair\(\s*Identifier.fromNamespaceAndPath\("mahoutsukai", "([^"]+)"\)',original) if field.startswith(('mysticCode','replica')) or field=='staff'}
for field,model in models.items():
 s=s.replace(' public static void init() {',f' public static final ModelKeyPair {field}=new ModelKeyPair("mahoutsukai:{model}");\n public static void init() {{')
 s=s.replace(' public static void init() {',f' public static void init() {{\n  ModelLoadingPlugin.register(context->context.addModel({field}.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","{model}"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));')
write(p,s)
# Copy all unmodified referenced model parents recursively.
import json
ids=['guidebook','mystic_code','mystic_code_first_sorcery','staff_emrys','replica','nobu','william']
modelsToCopy=list(models.values())
for id in ids:
 rel=f'assets/mahoutsukai/items/{id}.json';src=Path('recovered')/rel
 if src.exists():write(res/rel,src.read_text(encoding='utf-8'))
 modelsToCopy.append('item/'+id)
 defrefs=json.loads(src.read_text(encoding='utf-8')) if src.exists() else {}
 def visit(data):
  if isinstance(data,dict):
   for key,value in data.items():
    if key in ['base','model'] and isinstance(value,str) and value.startswith('mahoutsukai:'):modelsToCopy.append(value.split(':',1)[1])
    visit(value)
  elif isinstance(data,list):
   for value in data:visit(value)
 visit(defrefs)
seen=set()
while modelsToCopy:
 model=modelsToCopy.pop()
 if model in seen:continue
 seen.add(model);rel='assets/mahoutsukai/models/'+model+'.json';src=Path('recovered')/rel
 if not src.exists():continue
 data=json.loads(src.read_text(encoding='utf-8'));write(res/rel,src.read_text(encoding='utf-8'));parent=data.get('parent','')
 if parent.startswith('mahoutsukai:'):modelsToCopy.append(parent.split(':',1)[1])
# Dynamic max damage follows configuration just as the original extensions did.
p=main/'fabric/mixin/RuleBreakerDurabilityMixin.java';s=p.read_text(encoding='utf-8');anchor='  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)'
for cls,field in [('mysticcode.MysticCodeFirstSorcery','MYSTIC_CODE_FIRST_SORCERY_DURABILITY'),('replica.Replica','REPLICA_DURABILITY'),('nobu.Nobu','NOBU_DURABILITY')]:
 if hasattr(str,'foo'):pass
 s=s.replace(anchor,f'  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.{cls})result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.{field});\n'+anchor)
write(p,s)
config()
