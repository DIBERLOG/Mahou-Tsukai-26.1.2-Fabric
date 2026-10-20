package stepsword.mahoutsukai.render.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Only replaces the pause heading; the native buttons, focus and actions remain intact. */
public final class PauseMenuBranding {
 public static final Identifier LOGO=Identifier.fromNamespaceAndPath("mahoutsukai","textures/gui/pause_logo.png");
 public record Bounds(int left,int top,int width,int height){}
 public static Bounds bounds(Screen screen){
  int firstButton=screen.children().stream().filter(e->e instanceof Button b&&b.visible).mapToInt(e->((Button)e).getY()).min().orElse(60);
  int width=Math.max(1,Math.min(320,Math.min(screen.width-20,(int)((firstButton-12)*2100F/749))));
  int height=Math.max(1,Math.round(width*749F/2100));
  return new Bounds((screen.width-width)/2,Math.max(3,(firstButton-height)/2),width,height);
 }
 public static void init(){
  ScreenEvents.AFTER_INIT.register((client,screen,width,height)->{
   if(!(screen instanceof PauseScreen pause)||!pause.showsPauseMenu())return;
   for(var child:screen.children())if(child instanceof StringWidget label&&label.getMessage().equals(screen.getTitle()))label.visible=false;
   ScreenEvents.afterExtract(screen).register((current,graphics,mouseX,mouseY,delta)->render(graphics,current));
  });
 }
 private static void render(GuiGraphicsExtractor graphics,Screen screen){
  var box=bounds(screen);
  graphics.blit(RenderPipelines.GUI_TEXTURED,LOGO,box.left(),box.top(),0,0,box.width(),box.height(),box.width(),box.height(),box.width(),box.height());
 }
}
