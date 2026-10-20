package stepsword.mahoutsukai;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.state.gui.ColoredRectangleRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.render.gui.ScrollBadges;

/** Inspects real GUI extraction, not a second implementation of the badge renderer. */
public final class ScrollBadge042Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static List<ItemStack> scrolls(){return ModItems.REGISTERED.values().stream().map(ItemStack::new).filter(ScrollBadges::supports).toList();}
 private static ItemStack scroll(String id){return new ItemStack(ModItems.REGISTERED.get("scroll_"+id));}
 public static void run(Minecraft client)throws java.io.IOException{
  int count=0,elements=0;
  for(var scroll:scrolls()){
   require(ScrollBadges.role(scroll)!=null,"A registered scroll has no purpose badge: "+scroll.getHoverName().getString());
   String label=Component.translatable("mahoutsukai.scroll.role",Component.translatable(ScrollBadges.role(scroll).translation())).getString();
   var tooltip=scroll.getTooltipLines(Item.TooltipContext.of(client.level),client.player,TooltipFlag.NORMAL);
   require(tooltip.stream().anyMatch(t->t.getString().equals(label)),"Actual tooltip callback omitted the purpose label");
   var state=new GuiRenderState();var graphics=new GuiGraphicsExtractor(client,state,320,240);
   scroll.setCount(16);var before=scroll.copy();
   graphics.item(scroll,20,20);graphics.itemDecorations(client.font,scroll,20,20);
   var plates=new ArrayList<ColoredRectangleRenderState>();
   state.forEachElement(e->{if(e instanceof ColoredRectangleRenderState r&&r.col1()==0xF016121C)plates.add(r);},GuiRenderState.TraverseRange.ALL);
   int expected=ScrollBadges.element(scroll)==null?1:2;
   require(plates.size()==expected,"Native item extraction must draw "+expected+" independent badges for "+scroll.getHoverName().getString()+", actual="+plates.size());
   require(plates.stream().anyMatch(p->Math.min(p.x0(),p.x1())==29&&Math.min(p.y0(),p.y1())==29&&Math.max(p.x0(),p.x1())==36&&Math.max(p.y0(),p.y1())==36),"Purpose badge must occupy the lower-right corner inside the slot");
   if(expected==2){elements++;require(plates.stream().anyMatch(p->Math.min(p.x0(),p.x1())==21&&Math.min(p.y0(),p.y1())==29),"Element must be visible beside, without replacing, the purpose icon");}
   var text=new ArrayList<GuiTextRenderState>();state.forEachText(text::add);
   require(text.size()==1&&text.getFirst().y==20,"A stacked scroll's count must remain visible above the badge");
   require(scroll.getCount()==16&&ItemStack.isSameItemSameComponents(scroll,before),"Cosmetic rendering must not mutate the scroll or binding data");count++;
  }
  require(ScrollBadges.role(scroll("fireball"))==ScrollBadges.Role.ATTACK&&ScrollBadges.element(scroll("fireball"))==ScrollBadges.Element.FIRE,"Fireball keeps both attack and fire identity");
  require(ScrollBadges.role(scroll("rho_aias"))==ScrollBadges.Role.DEFENSE&&ScrollBadges.role(scroll("strengthening"))==ScrollBadges.Role.SUPPORT,"Shield and support examples are distinguished");
  require(ScrollBadges.role(scroll("summon_familiar"))==ScrollBadges.Role.FAMILIAR,"The actual summon scroll needs its own familiar symbol");
  var state=new GuiRenderState();var graphics=new GuiGraphicsExtractor(client,state,320,240);var sword=new ItemStack(Items.IRON_SWORD,16);
  graphics.item(sword,20,20);graphics.itemDecorations(client.font,sword,20,20);
  var rectangles=new ArrayList<ColoredRectangleRenderState>();state.forEachElement(e->{if(e instanceof ColoredRectangleRenderState r&&r.col1()==0xF016121C)rectangles.add(r);},GuiRenderState.TraverseRange.ALL);
  var text=new ArrayList<GuiTextRenderState>();state.forEachText(text::add);
  require(rectangles.isEmpty()&&text.size()==1&&text.getFirst().y==29,"Ordinary items retain their native lower-right count and receive no badges");
  Files.writeString(Path.of("scroll-badge-check.passed"),"PASS: "+count+" actual registered scrolls with native extracted badge rectangles and translated tooltips; "+elements+" dual role/element badges; familiar symbol, stacked count placement and unchanged components; ordinary item rendering unchanged\n");
 }
 public static final class Preview extends Screen {
  private final List<ItemStack> scrolls=scrolls();
  public Preview(){super(Component.literal("Свитки — назначение и стихия"));for(int i=0;i<scrolls.size();i++)if(i%4==0)scrolls.get(i).setCount(16);}
  @Override public void extractRenderState(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float delta){
   graphics.fill(0,0,width,height,0xF014111D);
   graphics.centeredText(font,title,width/2,12,0xFFECDAC1);
   graphics.centeredText(font,Component.literal("Назначение справа снизу · стихия рядом · число стопки сверху"),width/2,28,0xFFB8AEC6);
   int columns=Math.max(6,Math.min(12,(width-30)/34)),left=(width-columns*34)/2,top=51;
   for(int i=0;i<scrolls.size();i++){
    int x=left+(i%columns)*34+8,y=top+(i/columns)*29;var stack=scrolls.get(i);
    graphics.fill(x-3,y-3,x+20,y+20,0xFF292333);graphics.item(stack,x,y);graphics.itemDecorations(font,stack,x,y);
    if(mouseX>=x-3&&mouseX<x+20&&mouseY>=y-3&&mouseY<y+20)graphics.setTooltipForNextFrame(font,stack,mouseX,mouseY);
   }
   int i=0;for(var role:ScrollBadges.Role.values()){
    var example=scrolls.stream().filter(s->ScrollBadges.role(s)==role).findFirst().orElseThrow();
    int x=15+(i%5)*(width-30)/5,y=height-49+(i/5)*15;
    ScrollBadges.render(graphics,example,x-9,y-9);graphics.text(font,Component.translatable(role.translation()),x+10,y,0xFFC9C0D2);i++;
   }
   graphics.centeredText(font,Component.literal("Esc — вернуться в игру"),width/2,height-13,0xFF90879C);
  }
 }
}
