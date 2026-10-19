package stepsword.mahoutsukai.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

/** Animated three-dimensional signatures, built in one bounded mesh per spell. */
public final class SpellActivationRenderer {
 public static void draw(PoseStack pose,SubmitNodeCollector collector,Vec3 position,Vec3 normal,SpellPresentation style,float time,float size,float opacity,boolean release,boolean minimal){
  if(size<=0||opacity<=0)return;
  pose.pushPose();pose.translate(position.x,position.y,position.z);
  if(normal!=null)pose.mulPose(new org.joml.Quaternionf().rotationTo(0,1,0,(float)normal.x,(float)normal.y,(float)normal.z));
  pose.scale(size,size,size);
  if(normal!=null&&style.motif()==SpellPresentation.Motif.EYE){
   pose.translate(0,.65,0);pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));pose.translate(0,-.65,0);
  }
  float rotation=time*.032F+(float)Math.floorMod(style.seed(),29);
  var type=MahoujinRenderType.createMahoujinRenderType(RenderUtils.beam,1,false);
  collector.submitCustomGeometry(pose,type,(matrix,vertices)->{
   var ink=new Ink(vertices,matrix.pose(),style,opacity);int count=4+Math.floorMod(style.seed(),4);
   float pulse=.94F+.06F*(float)Math.sin(time*.18F);double rise=release?.3+Math.min(1,time/24F)*.6:0;
   if(minimal){ink.orbit(.9,.12,rotation,0,24,true);return;}
   switch(style.motif()){
    case BLOSSOM->{
     ink.orbit(.86,.1,rotation,0,40,true);
     for(int petal=0;petal<count;petal++){
      double base=rotation+petal*Math.PI*2/count;Vec3 last=null;
      for(int i=0;i<=20;i++){double t=i*Math.PI*2/20,r=.23+.75*Math.sin(t/2);var point=polar(base+Math.sin(t)*.28,r,.15+rise+Math.sin(t/2)*.75*pulse);if(last!=null)ink.line(last,point,.013,i%5==0);last=point;}
     }
     ink.diamond(new Vec3(0,.55+rise,0),.13,rotation,true);
    }
    case EYE->{
     Vec3 last=null;for(int i=0;i<=48;i++){double a=i*Math.PI*2/48;var point=new Vec3(Math.cos(a)*1.05,.65+rise+Math.sin(a)*.44,Math.sin(a*2)*.04);if(last!=null)ink.line(last,point,.014,true);last=point;}
     ink.orbit(.28,.65+rise,rotation,Math.PI/2,32,false);ink.orbit(.11,.65+rise,-rotation,Math.PI/2,20,true);
     for(int i=0;i<count;i++){double a=i*Math.PI*2/count+rotation;ink.diamond(new Vec3(Math.cos(a)*.82,.65+rise+Math.sin(a)*.5,.08),.045,rotation,true);}
    }
    case WARD->{
     for(int i=0;i<6;i++){double a=i*Math.PI/3+rotation*.2;var bottom=polar(a,1,.1);var top=polar(a,1,.9+rise);ink.line(bottom,top,.012,false);ink.line(top,polar(a+Math.PI/3,1,.9+rise),.012,true);ink.line(bottom,polar(a+Math.PI/3,1,.1),.012,true);}
     ink.orbit(.7,.48+rise*.5,-rotation,0,40,false);
     for(int i=0;i<6;i++)ink.line(polar(i*Math.PI/3+rotation*.2,1,.9+rise),new Vec3(0,1.4+rise,0),.009,false);
    }
    case BLADE->{
     ink.orbit(.95,.15,rotation,0,40,true);
     for(int i=0;i<count;i++){
      double a=i*Math.PI*2/count+rotation*.55;var foot=polar(a,.85,.28+rise);var tip=foot.add(0,.65+Math.sin(a+time*.03)*.12,0);
      ink.line(foot,tip,.018,true);ink.line(tip,foot.add(Math.cos(a)*.1,.45,Math.sin(a)*.1),.014,false);ink.line(foot.add(-Math.sin(a)*.13,.19,Math.cos(a)*.13),foot.add(Math.sin(a)*.13,.19,-Math.cos(a)*.13),.012,true);
     }
    }
    case RIFT->{
     for(int i=0;i<3;i++)ink.orbit(.64+i*.15,.64+rise,rotation*(i%2==0?1:-1)+i,Math.PI/2+i*.5,48,i==1);
     for(int i=0;i<count;i++)ink.diamond(polar(i*Math.PI*2/count+rotation,1,.64+rise),.08,rotation,false);
    }
    case CHAIN->{
     for(int i=0;i<count;i++)for(int link=0;link<5;link++){
      double angle=i*Math.PI*2/count+rotation*.35+link*.14;var center=polar(angle,.75,.15+rise+link*.2);Vec3 last=null;
      for(int k=0;k<=8;k++){double a=k*Math.PI/4;var point=center.add(Math.cos(a)*.075,Math.sin(a)*.12,Math.sin(a)*.035);if(last!=null)ink.line(last,point,.012,link%2==0);last=point;}
     }
     ink.orbit(.8,.1,-rotation,0,40,true);
    }
    case CROWN->{
     ink.orbit(.83,1.05+rise,rotation,0,48,true);ink.orbit(.98,1.17+rise,-rotation,0,48,false);
     for(int i=0;i<count+3;i++){double a=i*Math.PI*2/(count+3)+rotation*.4;ink.line(polar(a,.9,1.12+rise),polar(a,.94,1.55+rise),.014,true);ink.line(polar(a,.94,1.55+rise),polar(a+.2,.9,1.12+rise),.01,false);}
     for(int i=0;i<3;i++)ink.orbit(.45+i*.2,.12+i*.2,rotation*(i%2==0?1:-1),0,40,i==1);
    }
    case EMBER->{
     for(int arm=0;arm<3;arm++){Vec3 last=null;for(int i=0;i<=32;i++){double t=i/32.0,a=rotation*1.8+arm*Math.PI*2/3+t*4.8,r=.85*(1-t)+.07;var point=polar(a,r,.08+rise+t*1.55);if(last!=null)ink.line(last,point,.024*(1-t)+.006,i%7==0);last=point;}}
     ink.diamond(new Vec3(0,1.45+rise,0),.1,pulse,true);
    }
    case SPEAR->{
     for(int i=0;i<4;i++)ink.orbit(.3+i*.18,.2+i*.25,rotation*(i%2==0?1:-1),0,40,i%2==0);
     ink.line(new Vec3(0,.05,0),new Vec3(0,1.5+rise,0),.024,true);
     for(int i=0;i<4;i++)ink.line(new Vec3(0,1.5+rise,0),polar(i*Math.PI/2,.24,1.15+rise),.014,false);
    }
    case TWIN->{
     for(int side=-1;side<=1;side+=2){Vec3 last=null;for(int i=0;i<=40;i++){double a=i*Math.PI*2/40+rotation*side;var point=new Vec3(side*.48+Math.cos(a)*.35,.5+rise+Math.sin(a)*.48,Math.sin(a*2+rotation)*.1);if(last!=null)ink.line(last,point,.014,side>0);last=point;}}
     for(int i=0;i<12;i++){double t=i/11.0;ink.diamond(new Vec3(t*1.4-.7,.5+rise+Math.sin(t*Math.PI*2+rotation)*.12,0),.025,rotation,i%2==0);}
    }
    case ECLIPSE->{
     for(int i=0;i<3;i++)ink.orbit(.8+i*.08,.72+rise,rotation*(i==1?-1:1)+i,Math.PI/3+i*.7,48,i==1);
     for(int i=0;i<9;i++){double a=i*Math.PI*2/9+rotation*.4;ink.line(polar(a,.88,.1),polar(a,1.15,.45+rise),.014,false);}
     ink.orbit(.38,.72+rise,-rotation,Math.PI/2,40,true);
    }
    case ORBIT->{
     for(int i=0;i<3;i++)ink.orbit(.74,.65+rise,rotation*(i%2==0?1:-1)+i*Math.PI/3,Math.PI/3+i*.8,48,i%2==0);
     for(int i=0;i<count;i++)ink.diamond(polar(i*Math.PI*2/count+rotation,.86,.65+rise),.075,rotation,true);
    }
   }
   if(release)for(int i=0;i<count;i++){double a=i*Math.PI*2/count+rotation;ink.line(polar(a,.9,.08),polar(a,1.35,.08+rise*.2),.012,true);}
  });
  pose.popPose();
 }
 private static Vec3 polar(double angle,double radius,double height){return new Vec3(Math.cos(angle)*radius,height,Math.sin(angle)*radius);}
 private record Ink(VertexConsumer vertices,Matrix4f pose,SpellPresentation style,float opacity){
  void line(Vec3 from,Vec3 to,double width,boolean accent){
   var direction=to.subtract(from);if(direction.lengthSqr()<1e-8)return;var side=direction.cross(new Vec3(0,1,0));if(side.lengthSqr()<1e-8)side=new Vec3(1,0,0);side=side.normalize().scale(width);
   int color=accent?style.accent():style.color();quad(from,to,side.scale(3),color,opacity*.09F);quad(from,to,side,color,opacity*.9F);
   var cross=direction.cross(side).normalize().scale(width*.7);quad(from,to,cross,color,opacity*.55F);
  }
  private void quad(Vec3 from,Vec3 to,Vec3 side,int color,float alpha){v(from.add(side),color,alpha,0,0);v(to.add(side),color,alpha,1,0);v(to.subtract(side),color,alpha,1,1);v(from.subtract(side),color,alpha,0,1);}
  private void v(Vec3 point,int color,float alpha,float u,float v){vertices.addVertex(pose,(float)point.x,(float)point.y,(float)point.z).setColor(((color>>16)&255)/255F,((color>>8)&255)/255F,(color&255)/255F,alpha).setUv(u,v).setUv2(240,240);}
  void orbit(double radius,double height,double angle,double tilt,int count,boolean accent){
   Vec3 last=null;for(int i=0;i<=count;i++){double a=i*Math.PI*2/count+angle;var point=new Vec3(Math.cos(a)*radius,height+Math.sin(a)*radius*Math.sin(tilt),Math.sin(a)*radius*Math.cos(tilt));if(last!=null)line(last,point,.009,accent);last=point;}
  }
  void diamond(Vec3 center,double radius,double angle,boolean accent){
   var top=center.add(0,radius*1.4,0);var bottom=center.add(0,-radius*1.4,0);for(int i=0;i<4;i++){var point=center.add(Math.cos(angle+i*Math.PI/2)*radius,0,Math.sin(angle+i*Math.PI/2)*radius);line(top,point,.007,accent);line(bottom,point,.007,accent);}
  }
 }
}
