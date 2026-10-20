package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Quiet gold/purple victory ornament drawn in the game's own pixel style. */
public final class VictoryOverlay {
 private static Component boss;
 private static int theme;
 private static long started;
 public static void show(int style,Component name){theme=Math.clamp(style,0,4);boss=name.copy();started=0;}
 public static void clear(){boss=null;}
 private static int color(int rgb,float alpha){return (Math.clamp((int)(alpha*255),0,255)<<24)|(rgb&0xFFFFFF);}
 private static void diamond(GuiGraphicsExtractor g,int x,int y,int radius,int color){for(int dy=-radius;dy<=radius;dy++){int dx=radius-Math.abs(dy);g.fill(x-dx,y+dy,x+dx+1,y+dy+1,color);}}
 public static void render(GuiGraphicsExtractor g){
  if(boss==null)return;
  var mc=Minecraft.getInstance();
  if(mc.player==null||mc.screen!=null)return;
  if(started==0)started=System.nanoTime();
  double age=(System.nanoTime()-started)/1_000_000_000.0;
  if(age>=7){clear();return;}
  float fade=(float)(Math.clamp(age/.55,0,1)*Math.clamp((7-age)/1.1,0,1));
  if(fade<.02F)return;
  int screenW=mc.getWindow().getGuiScaledWidth(),screenH=mc.getWindow().getGuiScaledHeight();
  int width=Math.min(430,screenW-28),cx=screenW/2,top=Math.max(38,(int)(screenH*.3)),left=cx-width/2,right=cx+width/2,bottom=top+82;
  int[][] palettes={{0xE9BE69,0xFFF0BF,0xC6B1ED,0x9C6BDC},{0xB78CFF,0xF2DDFF,0xD4B5FF,0x6728A0},{0xB9ADB6,0xF3ECED,0xEF8A97,0x9E344B},{0x37D8CF,0xBAFFF0,0x81BCDA,0x167C83},{0xE5B96A,0xFFF5C7,0x8FE3D8,0x288D86}};
  int[] palette=palettes[theme];int gold=color(palette[0],fade),bright=color(palette[1],fade),violet=color(palette[2],fade);
  for(int i=8;i>0;i--)g.fill(left-i,top-i,right+i,bottom+i,color(palette[3],fade*.012F));
  g.fill(left,top,right,bottom,color(0x100D1B,fade*.89F));
  for(int x=left;x<right;x++){
   float edge=1-Math.abs(x-cx)/(width*.5F);
   g.fill(x,top,x+1,top+1,color(palette[0],fade*edge*.7F));g.fill(x,bottom-1,x+1,bottom,color(palette[0],fade*edge*.7F));
  }
  for(int side:new int[]{-1,1}){
   int x=cx+side*(width/2-7);
   g.fill(Math.min(x,x-side*19),top+6,Math.max(x,x-side*19)+1,top+7,gold);
   g.fill(x,top+6,x+1,top+23,gold);
   g.fill(Math.min(x,x-side*19),bottom-7,Math.max(x,x-side*19)+1,bottom-6,gold);
   g.fill(x,bottom-23,x+1,bottom-6,gold);
   diamond(g,cx+side*67,top+13,2,gold);
   diamond(g,cx+side*(78+(int)(Math.sin(age*.8)*4)),top+13,1,color(0xFFF2C7,fade*.6F));
  }
  // Each boss has its own silhouette, not only a differently colored title.
  switch(theme){
   case 1->{
    diamond(g,cx,top+10,7,bright);diamond(g,cx,top+10,4,gold);
    for(int side:new int[]{-1,1})for(int i=8;i<38;i++){int x=cx+side*i,wing=(38-i)/3;g.fill(x,top+8-wing,x+1,top+10+(i%9==0?3:0),gold);}
   }
   case 2->{
    for(int side:new int[]{-1,0,1}){int x=cx+side*15,y=top+6+(side==0?-3:2);g.fill(x-5,y,x+6,y+9,bright);g.fill(x-3,y+3,x-1,y+5,color(0x211522,fade));g.fill(x+2,y+3,x+4,y+5,color(0x211522,fade));g.fill(x-2,y+7,x+3,y+10,gold);}
    g.fill(cx-20,top+18,cx+21,top+19,gold);
   }
   case 3->{
    for(int x=-32;x<=32;x++){int height=(int)(Math.sin(x*.28+age*.8)*Math.exp(-Math.abs(x)/25.0)*7);g.fill(cx+x,top+11+height,cx+x+1,top+13+height,bright);}
    for(int side:new int[]{-1,1}){int x=cx+side*38;g.fill(x,top+5,x+2,top+16,gold);g.fill(x-side*3,top+2,x-side*3+2,top+8,gold);}
   }
   case 4->{
    diamond(g,cx,top+11,11,gold);diamond(g,cx,top+11,8,color(0x132E36,fade));diamond(g,cx,top+11,4,bright);diamond(g,cx,top+11,2,violet);
    for(int side:new int[]{-1,1}){g.fill(cx+side*18-2,top+10,cx+side*18+3,top+12,gold);}
   }
   default->{
    g.fill(cx-12,top+15,cx+13,top+17,gold);g.fill(cx-10,top+7,cx-7,top+15,gold);g.fill(cx-1,top+4,cx+2,top+15,bright);g.fill(cx+8,top+7,cx+11,top+15,gold);diamond(g,cx,top+2,2,bright);
   }
  }
  g.fill(cx-55,top+12,cx-20,top+13,gold);g.fill(cx+20,top+12,cx+56,top+13,gold);
  var heading=Component.translatable("mahoutsukai.victory.heading");
  g.pose().pushMatrix();g.pose().translate(cx,top+25);g.pose().scale(1.7F,1.7F);
  int titleX=-mc.font.width(heading)/2;g.text(mc.font,heading,titleX,1,color(0x6A421D,fade));g.text(mc.font,heading,titleX,0,bright);g.pose().popMatrix();
  var name=Component.translatable("mahoutsukai.victory.over",boss);
  float nameScale=Math.min(1.05F,(width-44F)/Math.max(1,mc.font.width(name)));
  g.pose().pushMatrix();g.pose().translate(cx,top+49);g.pose().scale(nameScale,nameScale);g.text(mc.font,name,-mc.font.width(name)/2,0,violet);g.pose().popMatrix();
  int length=(int)((width-80)*Math.clamp((7-age)/7,0,1));
  g.fill(left+40,bottom-11,right-40,bottom-10,color(0x594568,fade*.5F));g.fill(cx-length/2,bottom-11,cx+(length+1)/2,bottom-10,gold);
 }
}
