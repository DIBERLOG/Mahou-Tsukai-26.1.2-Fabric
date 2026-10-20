package stepsword.mahoutsukai.render.gui.guide;

import java.util.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.render.gui.ScrollBadges;
import net.minecraft.world.item.ItemStack;

/** The same real item icons used in inventories, with a readable legend and paged layout. */
public final class ScrollCatalogScreen extends Screen {
 private final Screen parent;
 private final List<ItemStack> scrolls=ModItems.REGISTERED.values().stream().map(ItemStack::new).filter(ScrollBadges::supports).toList();
 private List<ItemStack> filtered=List.of();
 private final List<Button> slots=new ArrayList<>();
 private int left,top,w,h,columns,rows,page;
 private String query="";
 private Button previous,next;
 public ScrollCatalogScreen(Screen parent){super(Component.translatable("mahoutsukai.scroll.catalog"));this.parent=parent;}
 @Override protected void init(){
  w=Math.min(650,width-16);h=Math.min(360,height-16);left=(width-w)/2;top=(height-h)/2;
  columns=Math.max(1,Math.min(12,(w-24)/32));int legendRows=(9+Math.max(2,Math.min(5,(w-24)/118))-1)/Math.max(2,Math.min(5,(w-24)/118));rows=Math.max(1,(h-109-legendRows*13)/29);slots.clear();
  var search=new EditBox(font,left+12,top+29,w-24,20,Component.translatable("mahoutsukai.catalog.search"));search.setHint(Component.translatable("mahoutsukai.catalog.search"));search.setValue(query);search.setResponder(value->{query=value;page=0;refresh();});addRenderableWidget(search);
  int gridLeft=left+(w-columns*32)/2;
  for(int i=0;i<columns*rows;i++){final int index=i;var b=Button.builder(Component.empty(),button->{int at=page*slots.size()+index;if(at<filtered.size()&&parent instanceof GuideCatalogScreen catalog){minecraft.setScreen(catalog);catalog.showRecipe(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(filtered.get(at).getItem()).toString());}}).bounds(gridLeft+i%columns*32,top+65+i/columns*29,24,24).build();slots.add(addRenderableWidget(b));}
  previous=addRenderableWidget(Button.builder(Component.literal("‹"),b->{page--;refresh();}).bounds(left+12,top+h-31,26,20).build());
  next=addRenderableWidget(Button.builder(Component.literal("›"),b->{page++;refresh();}).bounds(left+104,top+h-31,26,20).build());
  addRenderableWidget(Button.builder(Component.translatable("gui.back"),b->onClose()).bounds(left+w-86,top+h-31,74,20).build());refresh();
 }
 private void refresh(){
  String needle=query.toLowerCase(Locale.ROOT);filtered=scrolls.stream().filter(s->(s.getHoverName().getString()+" "+net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(s.getItem())+" "+Component.translatable(ScrollBadges.role(s).translation()).getString()).toLowerCase(Locale.ROOT).contains(needle)).toList();
  page=Math.clamp(page,0,Math.max(0,(filtered.size()-1)/slots.size()));
  for(int i=0;i<slots.size();i++){int at=page*slots.size()+i;var b=slots.get(i);b.visible=at<filtered.size();if(b.visible){var s=filtered.get(at);var label=s.getHoverName().copy().append(" · ").append(Component.translatable(ScrollBadges.role(s).translation()));b.setTooltip(Tooltip.create(label));b.setMessage(Component.empty());}}
  previous.active=page>0;next.active=(page+1)*slots.size()<filtered.size();
 }
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){
  g.fill(left,top,left+w,top+h,0xF0211B27);g.fill(left,top,left+w,top+2,0xFF9C7EB2);g.text(font,title,left+12,top+12,0xFFECDAC1);
  g.text(font,Component.translatable("mahoutsukai.scroll.catalog_hint"),left+12,top+53,0xFFB8AEC6);
  super.extractRenderState(g,mx,my,dt);
  for(int i=0;i<slots.size();i++){int at=page*slots.size()+i;var b=slots.get(i);if(!b.visible)continue;var s=filtered.get(at);g.item(s,b.getX()+4,b.getY()+4);if(b.isHoveredOrFocused())g.setTooltipForNextFrame(font,s,mx,my);}
  int i=0,legendColumns=Math.max(2,Math.min(5,(w-24)/118));int legendRows=(9+legendColumns-1)/legendColumns;
  for(var role:ScrollBadges.Role.values()){
   int x=left+12+i%legendColumns*(w-24)/legendColumns,y=top+h-37-legendRows*13+i/legendColumns*13;
   ScrollBadges.legend(g,role,x,y);g.text(font,font.plainSubstrByWidth(Component.translatable(role.translation()).getString(),(w-24)/legendColumns-13),x+11,y,0xFFD3C7D9);i++;
  }
  g.centeredText(font,Component.literal((page+1)+" / "+Math.max(1,(filtered.size()+slots.size()-1)/slots.size())),left+70,top+h-25,0xFFB8AEC6);
  if(filtered.isEmpty())g.centeredText(font,Component.translatable("mahoutsukai.scroll.empty"),left+w/2,top+87,0xFFB8AEC6);
 }
 @Override public void onClose(){minecraft.setScreen(parent);}
}
