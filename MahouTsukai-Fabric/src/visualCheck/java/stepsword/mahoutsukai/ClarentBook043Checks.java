package stepsword.mahoutsukai;

import java.nio.file.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.render.gui.*;
import stepsword.mahoutsukai.render.gui.guide.*;

/** Real book navigation, search, keyboard focus, compact layout, and native HUD extraction. */
public final class ClarentBook043Checks {
 private static void require(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 private static List<String> texts(GuiRenderState state){var out=new ArrayList<String>();state.forEachText(t->{var line=new StringBuilder();t.text.accept((index,style,code)->{line.appendCodePoint(code);return true;});out.add(line.toString());});return out;}
 private static Object field(Object target,String name)throws Exception{var f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}
 public static void book(Minecraft mc)throws Exception{
  require(mc.screen instanceof GuideCatalogScreen,"Open the real server compendium before running the navigation check");var parent=(GuideCatalogScreen)mc.screen;
  var tab=parent.children().stream().filter(Button.class::isInstance).map(Button.class::cast).filter(b->b.getMessage().equals(Component.translatable("mahoutsukai.scroll.tab"))).findFirst().orElseThrow();tab.onPress(null);
  require(mc.screen instanceof ScrollCatalogScreen,"The real compendium tab opens the production scroll catalogue");var gallery=(ScrollCatalogScreen)mc.screen;
  for(int[] size:new int[][]{{650,360},{320,240}}){
   gallery.resize(size[0],size[1]);
   for(var child:gallery.children())if(child instanceof AbstractWidget widget&&widget.visible)require(widget.getX()>=0&&widget.getY()>=0&&widget.getX()+widget.getWidth()<=size[0]&&widget.getY()+widget.getHeight()<=size[1],"Every real gallery control fits the compact layout");
   var search=gallery.children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast).findFirst().orElseThrow();search.setValue("fireball");
   var matches=(List<?>)field(gallery,"filtered");require(matches.size()==1,"Actual search responder selects only Fireball");
   var state=new GuiRenderState();gallery.extractRenderState(new GuiGraphicsExtractor(mc,state,size[0],size[1]),0,0,0);
   search.setValue("zzzz-no-such-scroll");require(((List<?>)field(gallery,"filtered")).isEmpty(),"Unknown search gives a real empty state");search.setValue("");require(((List<?>)field(gallery,"filtered")).size()==51,"All 51 real registered scrolls are present");
   gallery.keyPressed(new net.minecraft.client.input.KeyEvent(org.lwjgl.glfw.GLFW.GLFW_KEY_TAB,0,0));require(gallery.getFocused()!=null,"Keyboard Tab actually focuses a catalogue control; native Screen returns false after navigation");
  }
  gallery.onClose();require(mc.screen==parent,"Back restores the original book instance and context");
  var book=new HandbookScreen(parent,"clarent");mc.setScreen(book);var render=new GuiRenderState();book.extractRenderState(new GuiGraphicsExtractor(mc,render,book.width,book.height),0,0,0);require(texts(render).stream().anyMatch(s->s.contains("Кларент")||s.contains("Clarent")),"Clarent has a dedicated visible handbook chapter");
  String description=Component.translatable("mahoutsukai.handbook.clarent.body").getString();require(description.contains("15")&&description.contains("30")&&description.contains("1600"),"The handbook explains both timers and the damage cap");
  book.onClose();require(mc.screen==parent,"Clarent chapter returns to its book");
  Files.writeString(Path.of("clarent-book-check.passed"),"PASS: real compendium scroll tab, 51 scrolls, search and empty state, keyboard focus, controls inside 650x360 and 320x240, original parent restored, separate Clarent chapter with 15/30 timing and 1600 cap\n");
 }
 public static void hud(Minecraft mc)throws Exception{
  require(mc.player.getMainHandItem().getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent,"Equip the real server Clarent first");
  var state=new GuiRenderState();ClarentHud.render(new GuiGraphicsExtractor(mc,state,mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight()));
  var strings=texts(state);float power=stepsword.mahoutsukai.util.Utils.getSwordMahou(mc.player.getMainHandItem()).getAttackDamage();String expected=power<100?String.format(Locale.ROOT,"%.1f",power):Integer.toString(Math.round(power));require(strings.contains(expected),"HUD displays the actual synchronized sword power");
  int active=ClarentHud.activeTicks(),cooldown=ClarentHud.cooldownTicks();require(active>0||cooldown>0,"A real server guard/cooldown packet must reach the HUD");
  String timer=Component.translatable(active>0?"mahoutsukai.clarent.guard_time":"mahoutsukai.clarent.cooldown_time",((active>0?active:cooldown)+19)/20).getString();require(strings.contains(timer),"Native extracted HUD contains the synchronized countdown");
  Files.writeString(Path.of("clarent-hud-check.passed"),"PASS: actual synchronized Clarent power "+expected+" and server countdown '"+timer+"' extracted through native GUI rendering\n");
 }
}
