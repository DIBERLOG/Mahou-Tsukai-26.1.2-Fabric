package stepsword.mahoutsukai.fabric;

import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.networking.SpellVisualPacket;

/** Bounded client-only geometry: no entities or per-particle network traffic. */
public final class SpellVisuals {
 private static final class Burst {
  final SpellVisualPacket packet;int age;
  Burst(SpellVisualPacket packet){this.packet=packet;}
 }
 private static final int BURST_TICKS=40;
 private static final List<Burst> bursts=new ArrayList<>();
 private static Object lastWorld;
 private static int budget;
 public static void init(){
  ClientPlayNetworking.registerGlobalReceiver(SpellVisualPacket.TYPE,(packet,context)->{
   if(!Double.isFinite(packet.x())||!Double.isFinite(packet.y())||!Double.isFinite(packet.z())||packet.family()>=SpellPresentation.Family.values().length)return;
   if(bursts.size()>=32)bursts.removeFirst();bursts.add(new Burst(packet));
  });
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{bursts.clear();lastWorld=null;});
  ClientTickEvents.END_CLIENT_TICK.register(SpellVisuals::tick);
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{
   var client=Minecraft.getInstance();if(client.player==null||client.level==null||client.screen!=null)return;
   boolean minimal=client.options.particles().get().toString().equals("MINIMAL");
   float partial=client.getDeltaTracker().getGameTimeDeltaPartialTick(false);var camera=client.gameRenderer.getMainCamera().position();int casters=0;
   var players=client.level.players();
   for(int index=-1;index<players.size();index++){
    var player=index<0?client.player:players.get(index);if(index>=0&&player==client.player)continue;
    if(!player.isUsingItem()||player.distanceToSqr(client.player)>=48*48)continue;
    var stack=SpellCasting.active(player.getUseItem());if(stack.isEmpty()||!SpellPresentation.supports(stack.getItem()))continue;
    if(player.getUseItem().getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode&&!SpellCasting.accepts(stack))continue;
    float age=player.getTicksUsingItem()+partial,progress=Math.min(1,age/SpellCasting.requiredTicks(stack));var style=SpellPresentation.of(stack.getItem());
    var position=player.getPosition(partial);float radius=.65F+progress*.5F;
    seal(context.poseStack(),context.submitNodeCollector(),position.add(0,.06,0).subtract(camera),null,radius,age*1.7F,style,.8F);
    var look=player.getViewVector(partial);var hand=player.getEyePosition(partial).add(look.scale(1.35)).add(0,-.18,0);
    seal(context.poseStack(),context.submitNodeCollector(),hand.subtract(camera),look,.22F+progress*.23F,-age*2.3F,style,.5F);
    // A body-height motif can intersect the first-person camera; its hand seal stays visible.
    if(player!=client.player||!client.options.getCameraType().isFirstPerson())SpellActivationRenderer.draw(context.poseStack(),context.submitNodeCollector(),position.add(0,.08,0).subtract(camera),null,style,age,.6F+progress*.55F,.25F+progress*.65F,false,minimal);
    if(!minimal)SpellActivationRenderer.draw(context.poseStack(),context.submitNodeCollector(),hand.subtract(camera),look,style,age,.2F+progress*.17F,.5F,false,false);
    if(++casters>=8)break;
   }
   int shown=0;for(var burst:List.copyOf(bursts)){var p=burst.packet;var origin=new Vec3(p.x(),p.y(),p.z());if(client.player.position().distanceToSqr(origin)>48*48)continue;
    float time=(burst.age+partial)/BURST_TICKS;if(time>=1)continue;
    var style=new SpellPresentation(SpellPresentation.Family.values()[p.family()],p.color(),p.accent(),p.seed());
    seal(context.poseStack(),context.submitNodeCollector(),origin.add(0,.09+time*.15,0).subtract(camera),null,.7F+time*1.7F,time*45,style,(1-time)*.8F);
    if(!client.options.getCameraType().isFirstPerson()||camera.distanceToSqr(origin)>4)SpellActivationRenderer.draw(context.poseStack(),context.submitNodeCollector(),origin.add(0,.1,0).subtract(camera),null,style,burst.age+partial,.65F+Math.min(1,time*2)*.8F,(float)Math.pow(1-time,1.2),true,minimal);
    if(++shown>=8)break;
   }
  });
 }
 private static void tick(Minecraft client){
  if(client.level!=lastWorld){bursts.clear();lastWorld=client.level;}
  if(client.level==null||client.player==null||client.isPaused())return;
  boolean minimal=client.options.particles().get().toString().equals("MINIMAL");budget=minimal?80:360;
  // Reserve the local casting particles before nearby release effects use the shared budget.
  charge(client.player,minimal);
  for(var iterator=bursts.iterator();iterator.hasNext();){var burst=iterator.next();
   if(++burst.age>BURST_TICKS){iterator.remove();continue;}
   if(burst.age%2==0&&client.player.position().distanceToSqr(new Vec3(burst.packet.x(),burst.packet.y(),burst.packet.z()))<48*48)burst(burst);
  }
  // Nearby tracked players use the remaining budget. First person sees the hand seal too.
  int casters=1;for(var player:client.level.players())if(player!=client.player&&player.distanceToSqr(client.player)<48*48&&player.isUsingItem()){
   charge(player,minimal);if(++casters>=8)break;
  }
 }
 private static void point(ParticleOptions type,Vec3 pos,Vec3 velocity){
  if(budget--<=0)return;
  var client=Minecraft.getInstance();
  if(type instanceof DustParticleOptions dust){
   var color=dust.getColor();
   var particle=new stepsword.mahoutsukai.render.particle.ParticleMagic(client.level,pos.x,pos.y,pos.z,.2F,velocity.x,velocity.y,velocity.z,"textures/particle/white_magic_particle",dust.getScale()*.16F,color.x,color.y,color.z,.8F,true);
   particle.setLifetime(10);client.particleEngine.add(particle);
  }else client.level.addParticle(type,pos.x,pos.y,pos.z,velocity.x,velocity.y,velocity.z);
 }
 private static void seal(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,Vec3 position,Vec3 normal,float radius,float rotation,SpellPresentation style,float alpha){
  pose.pushPose();pose.translate(position.x,position.y,position.z);
  if(normal!=null)pose.mulPose(new org.joml.Quaternionf().rotationTo(0,1,0,(float)normal.x,(float)normal.y,(float)normal.z));
  pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
  float r=((style.color()>>16)&255)/255F,g=((style.color()>>8)&255)/255F,b=(style.color()&255)/255F;
  var type=stepsword.mahoutsukai.render.MahoujinRenderType.createMahoujinRenderType(stepsword.mahoutsukai.render.RenderUtils.mahoujin,0,false);
  collector.submitCustomGeometry(pose,type,(matrix,vertices)->{
   var m=matrix.pose();vertices.addVertex(m,-radius,0,-radius).setColor(r,g,b,alpha).setUv(0,0).setUv2(240,240);
   vertices.addVertex(m,-radius,0,radius).setColor(r,g,b,alpha).setUv(0,1).setUv2(240,240);
   vertices.addVertex(m,radius,0,radius).setColor(r,g,b,alpha).setUv(1,1).setUv2(240,240);
   vertices.addVertex(m,radius,0,-radius).setColor(r,g,b,alpha).setUv(1,0).setUv2(240,240);
  });
  pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-rotation*2));
  var runes=stepsword.mahoutsukai.render.MahoujinRenderType.createScopedRunesRenderType("spell_presentation",stepsword.mahoutsukai.render.RenderUtils.runes,0);
  stepsword.mahoutsukai.render.RenderUtils.renderRing(pose,collector,.012,0,radius*1.03F,radius*.16F,64,240,240,((style.accent()>>16)&255)/255F,((style.accent()>>8)&255)/255F,(style.accent()&255)/255F,alpha*.8F,runes);
  // A family-specific outer sigil: hexagonal wards, projection squares, secret pentagrams.
  int sides=switch(style.family()){case DOOM->9;case PROJECTION,EXCHANGE->4;case DISPLACEMENT->3;case FAMILIAR,BOUNDARY->6;case SECRET->5;default->8;};
  int step=switch(style.family()){case DOOM->4;case FAMILIAR,SECRET->2;case MYSTIC->3;default->1;};
  var plain=stepsword.mahoutsukai.render.MahoujinRenderType.createMahoujinRenderType(stepsword.mahoutsukai.render.RenderUtils.beam,0,false);
  collector.submitCustomGeometry(pose,plain,(matrix,vertices)->{
   for(int i=0;i<sides;i++){
    double a=2*Math.PI*i/sides+Math.floorMod(style.seed(),7)*Math.PI/14,bAngle=2*Math.PI*(i+step)/sides+Math.floorMod(style.seed(),7)*Math.PI/14;
    double x1=Math.cos(a)*radius*1.27,z1=Math.sin(a)*radius*1.27,x2=Math.cos(bAngle)*radius*1.27,z2=Math.sin(bAngle)*radius*1.27;
    if(style.family()==SpellPresentation.Family.EYES){z1*=.5;z2*=.5;}
    stroke(vertices,matrix.pose(),x1,z1,x2,z2,radius*.009,r,g,b,alpha*.65F);
   }
  });
  if(style.family()==SpellPresentation.Family.DISPLACEMENT||style.family()==SpellPresentation.Family.DOOM){
   pose.pushPose();pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(55));
   stepsword.mahoutsukai.render.RenderUtils.renderRing(pose,collector,0,0,radius*.95F,radius*.08F,48,240,240,r,g,b,alpha*.45F,runes);pose.popPose();
  }
  pose.popPose();
 }
 private static void stroke(com.mojang.blaze3d.vertex.VertexConsumer vertices,org.joml.Matrix4f matrix,double x1,double z1,double x2,double z2,double width,float r,float g,float b,float alpha){
  double length=Math.hypot(x2-x1,z2-z1);if(length<.0001)return;double nx=-(z2-z1)/length*width,nz=(x2-x1)/length*width;
  vertices.addVertex(matrix,(float)(x1+nx),.02F,(float)(z1+nz)).setColor(r,g,b,alpha).setUv(0,0).setUv2(240,240);
  vertices.addVertex(matrix,(float)(x2+nx),.02F,(float)(z2+nz)).setColor(r,g,b,alpha).setUv(1,0).setUv2(240,240);
  vertices.addVertex(matrix,(float)(x2-nx),.02F,(float)(z2-nz)).setColor(r,g,b,alpha).setUv(1,1).setUv2(240,240);
  vertices.addVertex(matrix,(float)(x1-nx),.02F,(float)(z1-nz)).setColor(r,g,b,alpha).setUv(0,1).setUv2(240,240);
 }
 private static DustParticleOptions dust(int color,float size){return new DustParticleOptions(color,size);}
 private static void ring(Vec3 origin,Vec3 u,Vec3 v,double radius,double rotation,int count,ParticleOptions particle){
  for(int i=0;i<count;i++){double angle=rotation+Math.PI*2*i/count;point(particle,origin.add(u.scale(Math.cos(angle)*radius)).add(v.scale(Math.sin(angle)*radius)),Vec3.ZERO);}
 }
 private static void charge(Player player,boolean minimal){
  if(!player.isUsingItem()||player.isSpectator())return;
  var held=player.getUseItem();var stack=SpellCasting.active(held);
  if(stack.isEmpty()||!SpellPresentation.supports(stack.getItem()))return;
  if(held.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode&&!SpellCasting.accepts(stack))return;
  int age=player.getTicksUsingItem();if(age<1||age%2!=0)return;
  var style=SpellPresentation.of(stack.getItem());double progress=Math.min(1,age/(double)SpellCasting.requiredTicks(stack));
  double turn=age*.065+Math.floorMod(style.seed(),31),radius=.65+progress*.5;
  var floor=player.position().add(0,.09,0);var primary=dust(style.color(),.65F);var accent=dust(style.accent(),.48F);
  int count=minimal?12:28;
  ring(floor,new Vec3(1,0,0),new Vec3(0,0,1),radius,turn,count,primary);
  ring(floor.add(0,.035,0),new Vec3(1,0,0),new Vec3(0,0,1),radius*.72,-turn,count/2,accent);
  if(minimal)return;
  // A rotating star and radial rune dashes; each spell has a stable 3–7 point signature.
  int corners=3+Math.floorMod(style.seed(),5);
  for(int i=0;i<corners;i++){
   double a=turn+Math.PI*2*i/corners,b=turn+Math.PI*2*(i+2)/corners;
   var from=floor.add(Math.cos(a)*radius,0,Math.sin(a)*radius);var to=floor.add(Math.cos(b)*radius,0,Math.sin(b)*radius);
   for(int k=0;k<3;k++)point(accent,from.lerp(to,k/3.0),Vec3.ZERO);
   point(primary,floor.add(Math.cos(a)*(radius+.12),.03,Math.sin(a)*(radius+.12)),new Vec3(0,.012,0));
  }
  // Vertical seal by the casting hand, plus two ascending orbital threads.
  var look=player.getLookAngle().normalize();var u=look.cross(new Vec3(0,1,0));if(u.lengthSqr()<.001)u=new Vec3(1,0,0);u=u.normalize();var v=u.cross(look).normalize();
  var hand=player.getEyePosition().add(look.scale(1.35)).add(u.scale(.18)).add(0,-.2,0);
  ring(hand,u,v,.2+progress*.22,-turn,16,primary);
  for(int i=0;i<6;i++){double a=turn*1.8+i*Math.PI/3,height=((age*.035+i/6.0)%1)*1.8;
   point(primary,player.position().add(Math.cos(a)*radius*.8,height,Math.sin(a)*radius*.8),new Vec3(0,.018,0));
  }
  if(age%6==0)point(dust(style.accent(),.45F),hand,look.scale(.025));
 }
 private static void burst(Burst burst){
  var p=burst.packet;var family=SpellPresentation.Family.values()[p.family()];double time=burst.age/(double)BURST_TICKS;
  var origin=new Vec3(p.x(),p.y(),p.z());var primary=dust(p.color(),(float)(.85-.35*time));var accent=dust(p.accent(),.65F);
  double radius=.3+2.1*Math.sin(time*Math.PI/2);
  switch(family){
   case DOOM,DISPLACEMENT->{
    double sign=family==SpellPresentation.Family.DOOM?-1:1;
    ring(origin.add(0,.55+time*.4,0),new Vec3(1,0,0),new Vec3(0,.45,.9),radius,sign*time*3,28,primary);
    ring(origin.add(0,.55,0),new Vec3(.7,.7,0),new Vec3(0,0,1),radius*.8,-sign*time*4,20,accent);
   }
   case FAMILIAR,EXCHANGE->{
    for(int i=0;i<24;i++){double a=i*Math.PI/6+time*6,h=i/24.0*1.9;
     point(i%2==0?primary:accent,origin.add(Math.cos(a)*radius*.55,h+time*.5,Math.sin(a)*radius*.55),new Vec3(0,.035,0));}
    ring(origin.add(0,.12,0),new Vec3(1,0,0),new Vec3(0,0,1),radius,time,24,primary);
   }
   case EYES->{
    ring(origin.add(0,1.45,0),new Vec3(1,0,0),new Vec3(0,0,1),radius*.7,time*2,32,primary);
    ring(origin.add(0,.15,0),new Vec3(1,0,0),new Vec3(0,0,1),radius,-time*2,20,accent);
   }
   case BOUNDARY->{
    for(int i=0;i<32;i++){double a=Math.PI*2*i/32;double r=radius/Math.max(Math.abs(Math.cos(a)),Math.abs(Math.sin(a)));
     point(primary,origin.add(Math.cos(a)*r,.12+time*.5,Math.sin(a)*r),Vec3.ZERO);}
    ring(origin.add(0,.15,0),new Vec3(1,0,0),new Vec3(0,0,1),radius*.7,0,20,accent);
   }
   case PROJECTION->{
    ring(origin.add(0,.16,0),new Vec3(1,0,0),new Vec3(0,0,1),radius,-time,32,primary);
    for(int i=0;i<8;i++){double a=i*Math.PI/4+time;for(int k=0;k<3;k++)point(accent,origin.add(Math.cos(a)*radius*.7,.3+k*.24+time*.6,Math.sin(a)*radius*.7),new Vec3(0,.02,0));}
   }
   default->{
    ring(origin.add(0,.14,0),new Vec3(1,0,0),new Vec3(0,0,1),radius,time*2,32,primary);
    ring(origin.add(0,.55+time,0),new Vec3(1,0,0),new Vec3(0,0,1),radius*.55,-time*2,16,accent);
   }
  }
  if(burst.age==2)for(int i=0;i<12;i++){double a=i*Math.PI/6;point(dust(p.accent(),.5F),origin.add(0,.75,0),new Vec3(Math.cos(a)*.08,.04,Math.sin(a)*.08));}
 }
}
