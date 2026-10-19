package stepsword.mahoutsukai;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.fabric.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;

/** Exercise every signature's actual vertex writer, not just its registry declaration. */
public final class ActivationVisualChecks {
 private static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 public static void run()throws Exception{
  var vertices=new AtomicInteger();var submissions=new AtomicInteger();
  var writer=(VertexConsumer)Proxy.newProxyInstance(ActivationVisualChecks.class.getClassLoader(),new Class[]{VertexConsumer.class},(proxy,method,args)->{
   if(method.getName().equals("addVertex"))vertices.incrementAndGet();
   if(args!=null)for(var arg:args){if(arg instanceof Float f)require(Float.isFinite(f),"Non-finite activation vertex");if(arg instanceof org.joml.Matrix4f matrix)require(matrix.isFinite(),"Non-finite activation transform");}
   return method.getReturnType().isAssignableFrom(VertexConsumer.class)?proxy:null;
  });
  var collector=(SubmitNodeCollector)Proxy.newProxyInstance(ActivationVisualChecks.class.getClassLoader(),new Class[]{SubmitNodeCollector.class},(proxy,method,args)->{
   if(method.getName().equals("submitCustomGeometry")){
    submissions.incrementAndGet();var pose=(PoseStack)args[0];require(pose.last().pose().isFinite(),"Non-finite seal pose");
    ((SubmitNodeCollector.CustomGeometryRenderer)args[2]).render(pose.last(),writer);
   }
   return null;
  });
  int count=0;var motifs=EnumSet.noneOf(SpellPresentation.Motif.class);
  for(var item:ModItems.REGISTERED.values())if(item instanceof SpellScroll){
   count++;var style=SpellPresentation.of(item);require(SpellPresentation.hasExplicitMotif(item),"Scroll has explicit activation motif");motifs.add(style.motif());
   for(int stage=0;stage<3;stage++){
    int before=vertices.get();SpellActivationRenderer.draw(new PoseStack(),collector,new Vec3(1,2,3),stage==0?new Vec3(0,0,1):null,style,stage==0?8:24,1,.8F,stage==2,false);
    int written=vertices.get()-before;require(written>100&&written<16000,"Activation mesh stays present and bounded: "+style.motif()+" "+written);
   }
   int before=vertices.get();SpellActivationRenderer.draw(new PoseStack(),collector,Vec3.ZERO,new Vec3(0,1,0),style,20,1,.8F,false,true);require(vertices.get()-before<600,"Minimal particle preference uses a small activation mesh");
  }
  require(motifs.size()==SpellPresentation.Motif.values().length,"All twelve activation motifs exercised");
  var client=net.minecraft.client.Minecraft.getInstance();var projectile=new stepsword.mahoutsukai.fireball.FireballEntity(stepsword.mahoutsukai.entity.ModEntities.FIREBALL.get(),client.level);projectile.setDeltaMovement(new Vec3(0,.3,1.2));
  var renderer=(stepsword.mahoutsukai.render.entity.FireballRenderer)client.getEntityRenderDispatcher().getRenderer(projectile);var state=renderer.createRenderState();renderer.extractRenderState(projectile,state,.5F);
  var pose=new PoseStack();pose.translate(1,2,3);var beforePose=new org.joml.Matrix4f(pose.last().pose());int beforeVertices=vertices.get();renderer.submit(state,pose,collector,new net.minecraft.client.renderer.state.level.CameraRenderState());
  require(pose.last().pose().equals(beforePose)&&vertices.get()-beforeVertices>100,"Native Fireball projectile renderer produces finite geometry and restores its pose");
  java.nio.file.Files.writeString(java.nio.file.Path.of("activation-geometry-check.passed"),"PASS: "+count+" actual scroll renderers, "+motifs.size()+" motifs, "+submissions.get()+" native geometry submissions; finite vertices, bounded meshes, reduced detail and Fireball projectile geometry\n");
 }
}
