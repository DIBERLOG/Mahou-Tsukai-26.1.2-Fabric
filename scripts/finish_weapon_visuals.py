from remaining_port_helpers import *
# Convert immediate original vertex runs into snapshots collected by the native renderer.
def geometry(s):
 s=s.replace('MahoujinRenderType.wrapCullLayer(','(')
 s=re.sub(r'\s*BufferSource (\w+) = Minecraft.getInstance\(\).renderBuffers\(\).bufferSource\(\);','',s)
 s=re.sub(r'(\w+)\.endBatch\((RenderUtils.renderRing[^;]+)\);',r'\2;',s)
 s=re.sub(r'\s*(\w+)\.endBatch\([^;]+;','',s)
 s=s.replace('BufferSource buf,','SubmitNodeCollector buf,').replace('BufferSource buffer,','SubmitNodeCollector buffer,')
 out=[];lines=s.splitlines();buf=typ=pose=None;i=0
 while i<len(lines):
  line=lines[i];m=re.search(r'(?:VertexConsumer )?bb = (\w+)\.getBuffer\((\w+)\);',line)
  if m:buf,typ=m.groups();i+=1;continue
  m=re.search(r'(?:Matrix4f )?m = (\w+)\.last\(\).pose\(\);',line)
  if m:pose=m[1];i+=1;continue
  if line.strip().startswith('bb.addVertex('):
   assert buf and typ and pose,line
   out.append(f'      {buf}.submitCustomGeometry({pose},{typ},(nativePose,bb)->{{')
   while i<len(lines) and lines[i].strip().startswith('bb.addVertex('):out.append(lines[i].replace('bb.addVertex(m,','bb.addVertex(nativePose.pose(),'));i+=1
   out.append('      });');continue
  out.append(line);i+=1
 return '\n'.join(out)+'\n'
for cls,entitycls,field,method,buffer in [('RenderSmiteEntity','SmiteEntity','SMITE','renderSmite','buffer'),('RenderMorganBallEntity','MorganBallEntity','MORGAN_BALL','renderMorganBall','buf'),('RenderGateChainEntity','GateChainEntity','GATE_CHAIN','renderCircle','buf')]:
 p=client/('render/entity/'+cls+'.java');s=(orig/('render/entity/'+cls+'.java')).read_text().replace('float partialTicks) {',f'float partialTicks,SubmitNodeCollector {buffer}) {{');write(p,geometry(s))
 p=client/'fabric/SixFamilyClient.java';s=p.read_text();line=f'  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.{field}.get(),stepsword.mahoutsukai.render.entity.{cls}::new);';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s
 anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';line=f'   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.{entitycls}.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.{cls}.{method}(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());\n';s=s.replace(anchor,line+anchor) if line not in s else s;write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text();lines='''  stepsword.mahoutsukai.entity.mahoujin.SmiteEntity.clientSmoke=value->{var mc=net.minecraft.client.Minecraft.getInstance();if(mc.level==null)return;var smoke=new stepsword.mahoutsukai.render.particle.ParticleSmokeRing(mc.level,new java.awt.Color(value.r(),value.g(),value.b()),value.entity().getX(),value.entity().getY()+value.y(),value.entity().getZ());smoke.sz=Math.max(.25f,value.size()*.7f);smoke.trackingEntityId=value.entity().getId();smoke.trackingYOffset=value.y();mc.particleEngine.add(smoke);};
''';s=s.replace(' public static void init(){',' public static void init(){\n'+lines) if lines not in s else s;write(p,s)
# Original red lightning for Morgan rage shares the real procedural lightning implementation.
p=main/'render/particle/ModParticles.java';insert(p,' public static void init()', ''' private static final net.minecraft.core.particles.SimpleParticleType RED_LIGHTNING_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","particle_red_lightning"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> RED_LIGHTNING=()->RED_LIGHTNING_TYPE;
''')
write(res/'assets/mahoutsukai/particles/particle_red_lightning.json','{"textures": []}\n');write(client/'client/MorganClientEffect.java',(orig/'client/MorganClientEffect.java').read_text())
p=client/'fabric/SixFamilyClient.java';s=p.read_text();line='  net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(stepsword.mahoutsukai.render.particle.ModParticles.RED_LIGHTNING.get(),(type,world,x,y,z,vx,vy,vz,random)->new stepsword.mahoutsukai.render.particle.ParticleLightning(world,new java.awt.Color(255,0,0),new java.awt.Color(255,255,255),x,y,z,.5f,vx,vy,vz));';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s
line='stepsword.mahoutsukai.client.BorrowedAuthorityClientEffect.borrowedAuthorityParticlesLivingUpdate(entity);';s=s.replace('for(var entity:mc.level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,mc.player.getBoundingBox().inflate(64)))'+line,'for(var entity:mc.level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,mc.player.getBoundingBox().inflate(64))){'+line+'stepsword.mahoutsukai.client.MorganClientEffect.morganParticlesLivingUpdate(entity);}')
write(p,s)
# Existing original Y binding activates the held sword ability.
p=client/'Keybinds.java';s=p.read_text().replace(' public static KeyMapping selectiveDisplacement;',' public static KeyMapping selectiveDisplacement;\n public static KeyMapping changeMysticCode;');s=s.replace('  selectiveDisplacement=','  changeMysticCode=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.changeMysticCode",89,category));\n  selectiveDisplacement=');write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text();line='''  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player==null || mc.screen!=null)return;if(stepsword.mahoutsukai.Keybinds.changeMysticCode.isDown() && !mc.player.hasEffect(ModEffects.CM_COOLDOWN)){var item=mc.player.getMainHandItem().getItem();if(item instanceof stepsword.mahoutsukai.item.caliburn.Caliburn)ClientPlayNetworking.send(new ItemAbilityPacket(ItemAbilityPacket.ABILITY.CALIBURN));else if(item instanceof stepsword.mahoutsukai.item.morgan.Morgan)ClientPlayNetworking.send(new ItemAbilityPacket(ItemAbilityPacket.ABILITY.MORGAN));}});
''';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s;write(p,s)
p=main/'fabric/mixin/RuleBreakerDurabilityMixin.java';s=p.read_text();line='''  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || ((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.WEAPON_SHOOTER_DURABILITY);
''';s=s.replace('  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)',line+'  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)') if line not in s else s;write(p,s)
p=main/'fabric/mixin/ProximityRepairMixin.java';s=p.read_text().replace('right.getItem() instanceof ProximityProjectionKeys)','right.getItem() instanceof ProximityProjectionKeys || left.getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || right.getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || left.getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan || right.getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan)');write(p,s)
# All referenced original textures remain unchanged.
shutil.copytree('recovered/assets/mahoutsukai/textures',res/'assets/mahoutsukai/textures',dirs_exist_ok=True)
# Only registered sound entries enter the playable pack.
import json
source=json.loads(Path('recovered/assets/mahoutsukai/sounds.json').read_text());p=res/'assets/mahoutsukai/sounds.json';current=json.loads(p.read_text());names=re.findall(r'register\("([^"]+)"',(main/'sounds/ModSounds.java').read_text())
for name in names:
 if name not in source:continue
 current[name]=source[name]
 for sound in source[name].get('sounds',[]):
  id=sound if isinstance(sound,str) else sound['name'];path=id.split(':',1)[-1]+'.ogg';src=Path('recovered/assets/mahoutsukai/sounds')/path
  if src.exists():target=res/'assets/mahoutsukai/sounds'/path;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(src,target)
write(p,json.dumps(current,indent=2)+'\n')
config()
