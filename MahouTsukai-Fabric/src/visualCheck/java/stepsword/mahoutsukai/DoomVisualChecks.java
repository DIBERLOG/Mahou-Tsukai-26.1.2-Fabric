package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.*;
import stepsword.mahoutsukai.render.entity.*;
public final class DoomVisualChecks {
 private static Boolean savedHideGui,savedPause;
 public static void run(Minecraft mc)throws Exception{
  var doom=new stepsword.mahoutsukai.doom.DoomEntity(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(),mc.level);
  var renderer=(DoomRenderer)mc.getEntityRenderDispatcher().getRenderer(doom);
  for(float age:new float[]{0,70,140,159,160,175,209,210}){
   var groups=new ArrayList<List<org.joml.Vector3f>>();
   var collector=(SubmitNodeCollector)java.lang.reflect.Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(),new Class[]{SubmitNodeCollector.class},(proxy,method,args)->{
    if(method.getName().equals("submitCustomGeometry")){
     var vertices=new ArrayList<org.joml.Vector3f>();groups.add(vertices);
     var sink=(VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class[]{VertexConsumer.class},(p,m,a)->{
      if(m.getName().equals("addVertex")){
       var v=a[0] instanceof org.joml.Matrix4f transform?transform.transformPosition(new org.joml.Vector3f((Float)a[1],(Float)a[2],(Float)a[3])):new org.joml.Vector3f((Float)a[0],(Float)a[1],(Float)a[2]);
       if(!v.isFinite())throw new AssertionError("Non-finite DOOM geometry");vertices.add(v);
      }
      return p;
     });
     ((SubmitNodeCollector.CustomGeometryRenderer)args[2]).render(((PoseStack)args[0]).last(),sink);
    }
    return null;
   });
   var state=new DoomRenderState();state.age=age;var pose=new PoseStack();pose.translate(11,29,-7);var before=new org.joml.Matrix4f(pose.last().pose());
   renderer.submit(state,pose,collector,new net.minecraft.client.renderer.state.level.CameraRenderState());
   if(!pose.last().pose().equals(before))throw new AssertionError("DOOM leaked a pose transform");
   if(age<210 && groups.isEmpty() || age>=210 && !groups.isEmpty())throw new AssertionError("Missing or stale DOOM phase geometry");
   if(age==140){
    float[] low={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY},high={Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY};
    for(var v:groups.getFirst())for(int i=0;i<3;i++){low[i]=Math.min(low[i],v.get(i));high[i]=Math.max(high[i],v.get(i));}
    float[] center={11,29,-7};for(int i=0;i<3;i++)if(Math.abs((low[i]+high[i])/2-center[i])>.001||Math.abs(high[i]-low[i]-56)>.001)throw new AssertionError("Core is not 56 blocks wide at the exact target: "+Arrays.toString(low)+Arrays.toString(high));
    if(groups.size()!=3)throw new AssertionError("Missing counter-rotating accretion disk");
   }
  }
  java.nio.file.Files.writeString(java.nio.file.Path.of("doom-visual-check.passed"),"PASS: actual native DoomRenderer geometry at 8 animation ages, finite coordinates, pose restoration, two accretion disks, 56-block core centred exactly at supplied target, collapse/shockwave and end-of-animation cleanup\n");
 }
 public static void stage(Minecraft mc,int age){
  if(savedHideGui==null){savedHideGui=mc.options.hideGui;savedPause=mc.options.pauseOnLostFocus;}
  var old=mc.level.getEntity(-24000);if(old!=null)old.discard();
  var doom=new stepsword.mahoutsukai.doom.DoomEntity(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(),mc.level);
  var tag=new net.minecraft.nbt.CompoundTag();tag.putInt("age",age);
  doom.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,mc.level.registryAccess(),tag));
  doom.setId(-24000);doom.setPos(mc.player.getEyePosition().add(mc.player.getViewVector(1).scale(90)));mc.level.addEntity(doom);
  org.slf4j.LoggerFactory.getLogger("mahou-doom-fixture").info("DOOM stage age={}, position={}, resident={}, chunkLoaded={}",doom.age(),doom.position(),mc.level.getEntity(-24000)==doom,mc.level.hasChunkAt(doom.blockPosition()));
  mc.options.hideGui=true;mc.options.pauseOnLostFocus=false;mc.setScreen(null);
 }
 public static void clear(Minecraft mc){var doom=mc.level.getEntity(-24000);if(doom instanceof stepsword.mahoutsukai.doom.DoomEntity)doom.discard();if(savedHideGui!=null){mc.options.hideGui=savedHideGui;mc.options.pauseOnLostFocus=savedPause;savedHideGui=savedPause=null;}}
}
