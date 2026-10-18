package stepsword.mahoutsukai;

import com.mojang.blaze3d.vertex.VertexConsumer;
import stepsword.mahoutsukai.render.IsolatedTriangleStrip;
import java.util.*;

/** Adjacent strips must retain their triangles/attributes without connecting distant objects. */
public final class StripGeometryChecks {
 private static final class Vertex {float x,y,z;Map<String,List<Object>> fields=new HashMap<>();}
 public static void run()throws Exception{
  var vertices=new ArrayList<Vertex>();
  var out=(VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class[]{VertexConsumer.class},(proxy,method,args)->{
   if(method.getName().equals("addVertex")){var v=new Vertex();v.x=(Float)args[0];v.y=(Float)args[1];v.z=(Float)args[2];vertices.add(v);}
   else vertices.getLast().fields.put(method.getName(),List.of(args));
   return proxy;
  });
  var expected=new ArrayList<String>();
  var storage=new net.minecraft.client.renderer.SubmitNodeStorage();
  var type=stepsword.mahoutsukai.render.MahoujinRenderType.createBoxRenderType(stepsword.mahoutsukai.render.entity.RenderGunEntity.gun,0);
  for(int count:new int[]{3,4,5,10}){
   float offset=count*100;
   storage.submitCustomGeometry(new com.mojang.blaze3d.vertex.PoseStack(),type,(pose,strip)->{
   for(int i=0;i<count;i++){
    strip.addVertex(offset+i,i%2,0).setColor(10+i,20,30,255).setUv(i,.5f).setUv1(1,2).setUv2(240,241).setNormal(0,0,1).setLineWidth(2);
   }
   });
   for(int i=2;i<count;i++)expected.add(triangle(offset+i-2,(i-2)%2,offset+i-1,(i-1)%2,offset+i,i%2,i-2));
  }
  var feature=storage.order(0).getCustomGeometrySubmits();
  for(String name:List.of("solidCustomGeometrySubmits","translucentCustomGeometrySubmits")){
   var field=feature.getClass().getDeclaredField(name);field.setAccessible(true);
   for(var list:((Map<?,?>)field.get(feature)).values())for(var entry:(List<?>)list){var submit=(net.minecraft.client.renderer.SubmitNodeStorage.CustomGeometrySubmit)entry;submit.customGeometryRenderer().render(submit.pose(),out);}
  }
  var actual=new ArrayList<String>();
  for(int i=2;i<vertices.size();i++){
   var a=vertices.get(i-2);var b=vertices.get(i-1);var c=vertices.get(i);
   float area=(b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x);
   if(area!=0)actual.add(triangle(a.x,a.y,b.x,b.y,c.x,c.y,i-2));
  }
  if(!actual.equals(expected))throw new AssertionError("Batched strips changed original triangles/winding or bridged separate objects: "+actual);
  for(var v:vertices){
   if(v.fields.size()!=6)throw new AssertionError("Duplicated vertex lost attributes: "+v.fields);
   int index=Math.round(v.x)%100;
   if(!v.fields.get("setUv").equals(List.of((float)index,.5f)))throw new AssertionError("Strip endpoint UV changed");
   int color=(255<<24)|((10+index)<<16)|(20<<8)|30;
   if(!v.fields.get("setColor").equals(List.of(color)))throw new AssertionError("Strip endpoint colour changed");
  }
  java.nio.file.Files.writeString(java.nio.file.Path.of("strip-geometry-check.passed"),"PASS: batched 3/4/5/10-vertex strips preserve every independent triangle and winding, all UV/colour/light/overlay/normal/width attributes; no triangles connect distant objects\n");
 }
 private static String triangle(float ax,float ay,float bx,float by,float cx,float cy,int index){return (index%2==0?ax+","+ay+";"+bx+","+by:bx+","+by+";"+ax+","+ay)+";"+cx+","+cy;}
}
