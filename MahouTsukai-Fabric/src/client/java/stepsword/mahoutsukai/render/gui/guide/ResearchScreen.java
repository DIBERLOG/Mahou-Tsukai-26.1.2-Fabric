package stepsword.mahoutsukai.render.gui.guide;

import java.util.*;
import com.google.gson.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/** A native, keyboard-accessible research map backed by the player's saved discoveries. */
public final class ResearchScreen extends Screen {
 private final GuideCatalogScreen parent;
 private final List<JsonObject> nodes=new ArrayList<>();
 private final Map<String,JsonObject> byKey=new LinkedHashMap<>();
 private final Map<String,Button> buttons=new LinkedHashMap<>();
 private JsonObject selected;
 private int left,top,bookWidth,bookHeight,canvasRight;
 private double zoom=.85,panX,panY;
 private boolean dragging;
 private Button recipe;
 private int detailOffset;
 private static List<stepsword.mahoutsukai.item.guide.Page> guidePages;
 public ResearchScreen(GuideCatalogScreen parent){
  super(Component.translatable("mahoutsukai.research.title"));this.parent=parent;
  for(var value:GuideCatalogScreen.research()){var node=value.getAsJsonObject();nodes.add(node);byKey.put(key(node),node);}
  selected=nodes.isEmpty()?null:nodes.getFirst();
 }
 private static String key(JsonObject node){return node.get("key").getAsString();}
 private static String tr(String key,Object...args){return Component.translatable("mahoutsukai.research."+key,args).getString();}
 private static ItemStack icon(JsonObject node){var id=Identifier.parse(node.get("item").getAsString());return BuiltInRegistries.ITEM.containsKey(id)?new ItemStack(BuiltInRegistries.ITEM.getValue(id)):ItemStack.EMPTY;}
 private static String itemName(String id){
  var key=Identifier.parse(id);
  if(BuiltInRegistries.ITEM.containsKey(key))return new ItemStack(BuiltInRegistries.ITEM.getValue(key)).getHoverName().getString();
  var block=BuiltInRegistries.BLOCK.getValue(key);
  return block==null?id:block.getName().getString();
 }
 private static String name(JsonObject node){
  String key=key(node);if(key.startsWith("learn_")){var icon=icon(node);if(!icon.isEmpty())return icon.getHoverName().getString();return itemName(node.get("item").getAsString());}
  return tr(key+".title");
 }
 private static boolean done(JsonObject node){return node.get("done").getAsBoolean();}
 private static boolean available(JsonObject node){return node.get("available").getAsBoolean();}
 private static String statusLabel(JsonObject node){return tr(done(node)?"done":available(node)?"available":"locked").replaceFirst("^[✓!×] ?","");}
 private int x(JsonObject node){return (int)(left+panX+node.get("column").getAsInt()*104*zoom);}
 private int y(JsonObject node){return (int)(top+38+panY+node.get("row").getAsInt()*60*zoom);}
 private boolean inside(double x,double y){return x>=left+8&&x<canvasRight-8&&y>=top+34&&y<top+bookHeight-42;}
 private boolean insideDetail(double x,double y){return x>=canvasRight+8&&x<left+bookWidth-8&&y>=top+34&&y<top+bookHeight-40;}
 private static List<stepsword.mahoutsukai.item.guide.Page> guidePages(){
  if(guidePages==null)guidePages=new stepsword.mahoutsukai.item.guide.GuidebookInstance().getPages();
  return guidePages;
 }
 /** Finds the handbook article whose ritual recipe matches this node, so its purpose stays authoritative. */
 private static stepsword.mahoutsukai.item.guide.SpellPage pageFor(JsonObject node){
  if(!node.has("ingredients"))return null;
  for(var page:guidePages())if(page instanceof stepsword.mahoutsukai.item.guide.SpellPage spell&&GuideCatalogScreen.matches(spell.getRecipe(),node))return spell;
  return null;
 }
 /** Builds the concrete acquisition hint from the same recipe data the catalogue uses. */
 private static String acquisition(JsonObject node){
  if(!node.has("ingredients"))return tr("learn.body");
  var counts=new LinkedHashMap<String,Integer>();
  node.getAsJsonArray("ingredients").forEach(v->{String id=v.getAsString();if(!id.equals("mahoutsukai:spell_cloth"))counts.merge(id,1,Integer::sum);});
  var parts=new ArrayList<String>();
  counts.forEach((id,n)->parts.add(n+" × "+itemName(id)));
  String list=String.join(", ",parts);
  boolean craft=node.has("entryKind")&&node.get("entryKind").getAsString().equals("craft");
  if(craft)return tr("learn.craft",list);
  boolean cloth=node.has("cloth")&&node.get("cloth").getAsBoolean();
  return tr(cloth?"learn.ritual_cloth":"learn.ritual",list);
 }
 private String body(JsonObject node){
  String key=key(node);
  if(key.startsWith("learn_")){
   var page=pageFor(node);
   return tr("learn.card",page!=null?page.getDescription():tr("learn.purpose_unknown",name(node)),acquisition(node));
  }
  if(key.startsWith("family_"))return tr("family.body");
  return tr(key+".body");
 }
 @Override protected void init(){
  bookWidth=Math.min(790,width-16);bookHeight=Math.min(410,height-16);left=(width-bookWidth)/2;top=(height-bookHeight)/2;canvasRight=left+bookWidth-Math.min(220,bookWidth/3);panY=(bookHeight-80)/2.0;buttons.clear();detailOffset=0;
  var search=new EditBox(font,left+140,top+6,Math.max(80,canvasRight-left-156),20,Component.translatable("mahoutsukai.catalog.search"));search.setHint(Component.translatable("mahoutsukai.catalog.search"));search.setResponder(value->{if(!value.isBlank())for(var node:nodes)if(name(node).toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))){selected=node;detailOffset=0;center();break;}});addRenderableWidget(search);
  for(var node:nodes){
   class ResearchButton extends Button {
    ResearchButton(){super(0,0,30,30,Component.literal(name(node)+" — "+statusLabel(node)),b->select(node),DEFAULT_NARRATION);}
    @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float dt){
     int color=done(node)?0xFF81CBA4:available(node)?0xFFB08BE2:0xFF655F72;
     if(node==selected||isHoveredOrFocused())circle(g,getX()+15,getY()+15,18,0xFFDEC8F7);
     circle(g,getX()+15,getY()+15,16,color);circle(g,getX()+15,getY()+15,14,0xFF292735);
     var stack=icon(node);if(!stack.isEmpty())g.item(stack,getX()+7,getY()+7);
     else if(GuideGlyphs.itemlessRitual(node))GuideGlyphs.ritual(g,getX()+7,getY()+7);
     else {int cx=getX()+15,cy=getY()+15;circle(g,cx,cy,8,0xFFB5A0D4);circle(g,cx,cy,7,0xFF292735);markStroke(g,cx-5,cy,cx+4,cy,0xFFB5A0D4);markStroke(g,cx,cy-5,cx,cy+4,0xFFB5A0D4);circle(g,cx,cy,3,0xFFE0D1F4);}
     statusBadge(g,getX()+26,getY()+4,done(node),available(node));
    }
   }var button=new ResearchButton();buttons.put(key(node),button);addRenderableWidget(button);
  }
  addRenderableWidget(Button.builder(Component.translatable("mahoutsukai.research.home"),b->{zoom=.85;panX=40;panY=(bookHeight-80)/2.0;selected=nodes.isEmpty()?null:nodes.getFirst();detailOffset=0;}).bounds(left+12,top+bookHeight-30,74,20).build());
  addRenderableWidget(Button.builder(Component.literal("−"),b->zoom(.8)).bounds(left+92,top+bookHeight-30,24,20).build());
  addRenderableWidget(Button.builder(Component.literal("+"),b->zoom(1.25)).bounds(left+120,top+bookHeight-30,24,20).build());
  addRenderableWidget(Button.builder(Component.literal("‹"),b->step(-1)).bounds(left+152,top+bookHeight-30,24,20).build());
  addRenderableWidget(Button.builder(Component.literal("›"),b->step(1)).bounds(left+180,top+bookHeight-30,24,20).build());
  recipe=addRenderableWidget(Button.builder(Component.translatable("mahoutsukai.research.recipe"),b->{if(selected!=null){minecraft.setScreen(parent);parent.showRecipe(selected.get("item").getAsString());}}).bounds(canvasRight+12,top+bookHeight-58,bookWidth-(canvasRight-left)-24,20).build());
  addRenderableWidget(Button.builder(Component.translatable("gui.back"),b->onClose()).bounds(canvasRight+12,top+bookHeight-30,bookWidth-(canvasRight-left)-24,20).build());
 }
 private void select(JsonObject node){if(node!=selected)detailOffset=0;selected=node;}
 public void focusNode(String key){if(byKey.containsKey(key)){selected=byKey.get(key);detailOffset=0;center();}}
 private void step(int direction){if(nodes.isEmpty())return;select(nodes.get(Math.floorMod(nodes.indexOf(selected)+direction,nodes.size())));center();}
 private void center(){if(selected!=null){panX=(canvasRight-left)/2.0-selected.get("column").getAsInt()*104*zoom;panY=(bookHeight-80)/2.0-selected.get("row").getAsInt()*60*zoom;}}
 private void zoom(double factor){zoom=Math.clamp(zoom*factor,.55,1.5);center();}
 private static void circle(GuiGraphicsExtractor g,int x,int y,int radius,int color){for(int dy=-radius;dy<=radius;dy++){int dx=(int)Math.sqrt(radius*radius-dy*dy);g.fill(x-dx,y+dy,x+dx+1,y+dy+1,color);}}
 private static void markStroke(GuiGraphicsExtractor g,int x1,int y1,int x2,int y2,int color){
  int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));for(int i=0;i<=steps;i++){int x=x1+(x2-x1)*i/Math.max(1,steps),y=y1+(y2-y1)*i/Math.max(1,steps);g.fill(x,y,x+2,y+2,color);}
 }
 private static void statusBadge(GuiGraphicsExtractor g,int x,int y,boolean done,boolean available){
  circle(g,x,y+1,8,0x800C0C17);circle(g,x,y,7,0xFF151621);
  int border=done?0xFF8DE0B7:available?0xFFE3C980:0xFF77758B;
  circle(g,x,y,6,border);circle(g,x,y,5,done?0xFF28604F:available?0xFF645237:0xFF353441);
  if(done){
   markStroke(g,x-3,y,x-1,y+2,0xFF112E27);markStroke(g,x-1,y+2,x+3,y-2,0xFF112E27);
   markStroke(g,x-3,y-1,x-1,y+1,0xFFF0FFF6);markStroke(g,x-1,y+1,x+3,y-3,0xFFF0FFF6);
  }else if(available){
   g.fill(x,y-3,x+1,y+4,0xFFFFF2CC);g.fill(x-3,y,x+4,y+1,0xFFFFF2CC);g.fill(x-1,y-1,x+2,y+2,0xFFFFF2CC);
  }else{
   g.fill(x-3,y-1,x+4,y+4,0xFFB9B7CA);g.fill(x-2,y-4,x+3,y-1,0xFFB9B7CA);g.fill(x-1,y-3,x+2,y-1,0xFF353441);g.fill(x,y+1,x+1,y+3,0xFF535161);
  }
  g.fill(x-2,y-5,x+1,y-4,done?0xFFDCFFE9:available?0xFFFFECC4:0xFFA8A6B8);
 }
 private void line(GuiGraphicsExtractor g,int x1,int y1,int x2,int y2,int color){int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));if(steps==0)return;for(int i=0;i<=steps;i+=2){int px=x1+(x2-x1)*i/steps,py=y1+(y2-y1)*i/steps;if(inside(px,py))g.fill(px,py,px+2,py+2,color);}}
 private int text(GuiGraphicsExtractor g,String text,int x,int y,int width,int bottom,int color){for(var line:font.split(Component.literal(text),width)){if(y+font.lineHeight>bottom)break;g.text(font,line,x,y,color);y+=font.lineHeight+3;}return y;}
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){
  g.fill(left,top,left+bookWidth,top+bookHeight,0xF21B1925);g.fill(left,top,left+bookWidth,top+2,0xFFB895D7);g.fill(canvasRight,top+32,canvasRight+1,top+bookHeight-38,0xFF4C405E);
  g.text(font,title.getString(),left+14,top+13,0xFFF1DEF9);long completed=nodes.stream().filter(ResearchScreen::done).count();g.text(font,tr("progress",completed,nodes.size()),canvasRight+12,top+13,0xFFB9ABC5);
  for(int gx=left+12;gx<canvasRight-8;gx+=24)for(int gy=top+38;gy<top+bookHeight-42;gy+=24)g.fill(gx,gy,gx+1,gy+1,0xFF353040);
  for(var node:nodes)for(var value:node.getAsJsonArray("parents")){var from=byKey.get(value.getAsString());if(from!=null){int color=done(node)?0xFF649F84:available(node)?0xFF9271B5:0xFF433C50;line(g,x(from),y(from),x(node),y(node),color);}}
  for(var node:nodes){var button=buttons.get(key(node));int nx=x(node),ny=y(node);button.setPosition(nx-15,ny-15);button.visible=inside(nx-18,ny-18)&&inside(nx+18,ny+18);
   if(button.visible&&(node==selected||button.isHoveredOrFocused()||node.get("column").getAsInt()<4)){String label=name(node);int limit=(int)(100*zoom);if(font.width(label)>limit)label=font.plainSubstrByWidth(label,limit-font.width("..."))+"...";if(inside(nx-font.width(label)/2,ny+20)&&inside(nx+font.width(label)/2,ny+30))g.text(font,label,nx-font.width(label)/2,ny+20,0xFFCEBEDD);}
  }
  int sx=canvasRight+12,sy=top+40,sw=left+bookWidth-sx-12,bottom=top+bookHeight-66;
  if(selected!=null){
   sy=text(g,name(selected),sx,sy,sw,bottom,0xFFF0DDF9)+7;
   statusBadge(g,sx+7,sy+5,done(selected),available(selected));sy=text(g,statusLabel(selected),sx+22,sy,sw-22,bottom,0xFFA8CBB8)+9;
   if(!available(selected)){
    sy=text(g,tr("requires"),sx,sy,sw,bottom,0xFFB39BCA);
    for(var value:selected.getAsJsonArray("parents")){var node=byKey.get(value.getAsString());if(node!=null&&!done(node))sy=text(g,"• "+name(node),sx,sy,sw,bottom,0xFFBBAEC6);}
    sy+=2;
   }
   String body=body(selected);var lines=font.split(Component.literal(body),sw);int lineHeight=font.lineHeight+3;int visible=Math.max(1,(bottom-sy)/lineHeight);
   detailOffset=Math.clamp(detailOffset,0,Math.max(0,lines.size()-visible));
   for(int i=0;i<visible&&i+detailOffset<lines.size();i++)g.text(font,lines.get(i+detailOffset),sx,sy+i*lineHeight,0xFFCCBED6);
   if(lines.size()>visible){int trackTop=sy,trackBottom=bottom;g.fill(sx+sw-1,trackTop,sx+sw,trackBottom,0x553D3548);int barHeight=Math.max(8,(trackBottom-trackTop)*visible/Math.max(1,lines.size()));int barTop=trackTop+(trackBottom-trackTop-barHeight)*detailOffset/Math.max(1,lines.size()-visible);g.fill(sx+sw-1,barTop,sx+sw,barTop+barHeight,0xFFB895D7);}
  }else text(g,tr("empty"),sx,sy,sw,bottom,0xFFCCBED6);
  recipe.active=selected!=null;
  recipe.setMessage(Component.translatable(selected!=null&&key(selected).equals("doom")?"mahoutsukai.catalog.details":"mahoutsukai.research.recipe"));
  g.text(font,font.plainSubstrByWidth(tr("controls"),Math.max(0,canvasRight-left-224)),left+216,top+bookHeight-24,0xFF9F90AD);
  super.extractRenderState(g,mx,my,dt);
 }
 @Override public boolean mouseClicked(MouseButtonEvent event,boolean doubleClick){if(super.mouseClicked(event,doubleClick))return true;if(inside(event.x(),event.y())){dragging=true;return true;}return false;}
 @Override public boolean mouseDragged(MouseButtonEvent event,double dx,double dy){if(dragging){panX+=dx;panY+=dy;return true;}return super.mouseDragged(event,dx,dy);}
 @Override public boolean mouseReleased(MouseButtonEvent event){dragging=false;return super.mouseReleased(event);}
 @Override public boolean mouseScrolled(double x,double y,double dx,double dy){if(insideDetail(x,y)){detailOffset=Math.max(0,detailOffset+(dy>0?-3:3));return true;}if(inside(x,y)){zoom(dy>0?1.12:1/1.12);return true;}return super.mouseScrolled(x,y,dx,dy);}
 @Override public void onClose(){minecraft.setScreen(parent);}
 @Override public boolean isPauseScreen(){return false;}
}
