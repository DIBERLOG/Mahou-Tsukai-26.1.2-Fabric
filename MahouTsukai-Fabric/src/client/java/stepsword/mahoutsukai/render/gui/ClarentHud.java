package stepsword.mahoutsukai.render.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.fabric.ClarentGuard;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.util.Utils;

/** Compact power seal above the native experience number; server owns both timers. */
public final class ClarentHud {
 private static long activeUntil,cooldownUntil;
 public static void clear(){activeUntil=cooldownUntil=0;}
 public static void update(int active,int cooldown){var level=Minecraft.getInstance().level;if(level==null)return;long now=level.getGameTime();activeUntil=now+Math.clamp(active,0,300);cooldownUntil=now+Math.clamp(cooldown,0,600);}
 public static int activeTicks(){var l=Minecraft.getInstance().level;return l==null?0:(int)Math.max(0,activeUntil-l.getGameTime());}
 public static int cooldownTicks(){var l=Minecraft.getInstance().level;return l==null?0:(int)Math.max(0,cooldownUntil-l.getGameTime());}
 public static void render(GuiGraphicsExtractor g){
  var mc=Minecraft.getInstance();if(mc.player==null)return;
  var stack=mc.player.getMainHandItem();if(!(stack.getItem() instanceof Clarent))stack=mc.player.getOffhandItem();if(!(stack.getItem() instanceof Clarent))return;
  float power=Math.clamp(Utils.getSwordMahou(stack).getAttackDamage(),0,Clarent.cap());
  int cx=mc.getWindow().getGuiScaledWidth()/2,cy=mc.getWindow().getGuiScaledHeight()-66,r=14,color=0xFF000000|Clarent.auraColor(power);
  int active=activeTicks(),cooldown=cooldownTicks();float fraction=active>0?active/(float)ClarentGuard.ACTIVE_TICKS:cooldown>0?1-cooldown/(float)ClarentGuard.COOLDOWN_TICKS:power/Math.max(1,Clarent.cap());
  for(int y=-r;y<=r;y++)for(int x=-r;x<=r;x++){
   int d=x*x+y*y;if(d>r*r)continue;int c=0xD91A1523;
   if(d>=(r-2)*(r-2)){double angle=(Math.atan2(y,x)+Math.PI*2.5)%(Math.PI*2);c=angle<=fraction*Math.PI*2?color:0xFF514252;}
   g.fill(cx+x,cy+y,cx+x+1,cy+y+1,c);
  }
  String value=power<100?String.format(java.util.Locale.ROOT,"%.1f",power):Integer.toString(Math.round(power));
  var pose=g.pose();pose.pushMatrix();pose.translate(cx,cy-4);float scale=Math.min(.9F,23F/Math.max(1,mc.font.width(value)));pose.scale(scale,scale);g.text(mc.font,value,-mc.font.width(value)/2,0,0xFFF4E7D9,true);pose.popMatrix();
  pose.pushMatrix();pose.translate(cx,cy+6);pose.scale(.55F,.55F);g.centeredText(mc.font,Component.translatable("mahoutsukai.clarent.power"),0,0,0xFFBDB0C5);pose.popMatrix();
  if(active>0||cooldown>0)g.centeredText(mc.font,Component.translatable(active>0?"mahoutsukai.clarent.guard_time":"mahoutsukai.clarent.cooldown_time",((active>0?active:cooldown)+19)/20),cx,cy-r-11,active>0?0xFFAEE6C4:0xFFC3B0DC);
 }
}
