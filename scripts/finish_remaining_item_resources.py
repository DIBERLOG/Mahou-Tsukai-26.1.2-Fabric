from remaining_port_helpers import *
# Actual original recipes and registered audio, no new balance recipes.
import json
for name in ['guidebook','mystic_code','mystic_code_first_sorcery','william_book_cloning']:
 rel='data/mahoutsukai/recipe/'+name+'.json';write(res/rel,(Path('recovered')/rel).read_text(encoding='utf-8'))
p=res/'assets/mahoutsukai/sounds.json';data=json.loads(p.read_text(encoding='utf-8'));original=json.loads(Path('recovered/assets/mahoutsukai/sounds.json').read_text(encoding='utf-8'))
for name in re.findall(r'register\("([^"]+)"',(main/'sounds/ModSounds.java').read_text(encoding='utf-8')):
 if name not in original:continue
 data[name]=original[name]
 for sound in original[name].get('sounds',[]):
  id=sound if isinstance(sound,str) else sound['name'];rel='assets/mahoutsukai/sounds/'+id.split(':',1)[-1]+'.ogg';src=Path('recovered')/rel
  if src.exists():target=res/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(src,target)
write(p,json.dumps(data,indent=2)+'\n')
# Original procedural gun smoke, sparks and shield shockwave particles.
for rel in ['render/particle/ParticleSphere.java']:
 if not (client/rel).exists():write(client/rel,(orig/rel).read_text(encoding='utf-8'))
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  ClientPlayNetworking.registerGlobalReceiver(SparkExplosionPacket.TYPE,(packet,context)->{if(packet.color.size()!=8)return;float[] colors=new float[8];for(int i=0;i<8;i++)colors[i]=packet.color.get(i);for(int i=0;i<4;i++){var effect=new stepsword.mahoutsukai.render.particle.ParticleSphereExplosion(context.client().level,colors,packet.pos.x,packet.pos.y,packet.pos.z);effect.maxRadius=10;context.client().particleEngine.add(effect);}});
  ClientPlayNetworking.registerGlobalReceiver(ReplicaKnockbackParticlePacket.TYPE,(packet,context)->{var mc=context.client();mc.particleEngine.add(new stepsword.mahoutsukai.render.particle.ParticleSphere(mc.level,packet.posX,packet.posY,packet.posZ,.4f,0,0,0,stepsword.mahoutsukai.render.RenderUtils.beam.toString(),.32f,packet.r,packet.g,packet.b,.4f,false,1.2f));mc.particleEngine.add(new stepsword.mahoutsukai.render.particle.ParticleSphere(mc.level,packet.posX,packet.posY,packet.posZ,.4f,0,0,0,stepsword.mahoutsukai.render.RenderUtils.beam.toString(),.34f,1,1,1,.2f,false,1.2f));});
  stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity.clientSmoke=beam->{var c=beam.getColors();var effect=new stepsword.mahoutsukai.render.particle.ParticleSmokeRing(net.minecraft.client.Minecraft.getInstance().level,new java.awt.Color(c[0],c[1],c[2]),beam.getX(),beam.getY(),beam.getZ());effect.yaw=beam.getRotationYaw();effect.pitch=beam.getRotationPitch();net.minecraft.client.Minecraft.getInstance().particleEngine.add(effect);};
  stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity.clientLightning=beam->{if(beam.getFired())return;var c=beam.getColors();var v=beam.position();var q=new net.minecraft.world.phys.Vec3(1,1,1).normalize().yRot((float)Math.toRadians(beam.getRandom().nextInt(360))).xRot((float)Math.toRadians(beam.getRandom().nextInt(360))).scale(.3).add(v);var effect=new stepsword.mahoutsukai.render.particle.ParticleLightning(net.minecraft.client.Minecraft.getInstance().level,new java.awt.Color(c[0],c[1],c[2]),new java.awt.Color(c[4],c[5],c[6]),q.x,q.y,q.z,beam.getLength(),v.x,v.y,v.z);effect.yawrot=beam.getRandom().nextInt(360);net.minecraft.client.Minecraft.getInstance().particleEngine.add(effect);};
''');anchor='   for(var particle:stepsword.mahoutsukai.render.particle.ParticleSphereExplosion.active)';s=s.replace(anchor,'   for(var particle:stepsword.mahoutsukai.render.particle.ParticleSphere.active)stepsword.mahoutsukai.render.particle.ParticleSphere.renderParticleSphere(particle,camera.x,camera.y,camera.z,context.poseStack(),partial,context.submitNodeCollector());\n'+anchor);write(p,s)
