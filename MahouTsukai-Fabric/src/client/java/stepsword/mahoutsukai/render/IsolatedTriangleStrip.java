package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

/** Degenerate endpoints prevent separate strips sharing a native batch from joining. */
public final class IsolatedTriangleStrip implements VertexConsumer {
 private final VertexConsumer out;
 private boolean pending,first=true;
 private float x,y,z,u,v,nx,ny,nz,width;
 private int color,overlayU,overlayV,lightU,lightV,fields,count;
 public IsolatedTriangleStrip(VertexConsumer out){this.out=out;}
 private void emit(){
  out.addVertex(x,y,z);
  if((fields&1)!=0)out.setColor(color);
  if((fields&2)!=0)out.setUv(u,v);
  if((fields&4)!=0)out.setUv1(overlayU,overlayV);
  if((fields&8)!=0)out.setUv2(lightU,lightV);
  if((fields&16)!=0)out.setNormal(nx,ny,nz);
  if((fields&32)!=0)out.setLineWidth(width);
 }
 private void flush(){if(pending){if(first){emit();emit();first=false;}emit();}}
 public void finish(){if(pending){flush();emit();emit();if((count&1)!=0)emit();pending=false;}}
 @Override public VertexConsumer addVertex(float x,float y,float z){flush();this.x=x;this.y=y;this.z=z;fields=0;pending=true;count++;return this;}
 @Override public VertexConsumer setColor(int r,int g,int b,int a){return setColor((a&255)<<24|(r&255)<<16|(g&255)<<8|(b&255));}
 @Override public VertexConsumer setColor(int color){this.color=color;fields|=1;return this;}
 @Override public VertexConsumer setUv(float u,float v){this.u=u;this.v=v;fields|=2;return this;}
 @Override public VertexConsumer setUv1(int u,int v){overlayU=u;overlayV=v;fields|=4;return this;}
 @Override public VertexConsumer setUv2(int u,int v){lightU=u;lightV=v;fields|=8;return this;}
 @Override public VertexConsumer setNormal(float x,float y,float z){nx=x;ny=y;nz=z;fields|=16;return this;}
 @Override public VertexConsumer setLineWidth(float width){this.width=width;fields|=32;return this;}
}
