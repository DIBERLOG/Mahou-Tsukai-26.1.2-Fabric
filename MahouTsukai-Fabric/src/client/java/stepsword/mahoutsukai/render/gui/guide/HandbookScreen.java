package stepsword.mahoutsukai.render.gui.guide;

import java.util.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import stepsword.mahoutsukai.item.guide.*;

/** Readable, searchable-by-chapter companion to the server recipe catalogue. */
public final class HandbookScreen extends Screen {
 private record Chapter(String title,String body) {}
 private final Screen parent;
 private final List<Chapter> chapters=new ArrayList<>();
 private int chapter,offset,left,top,bookWidth,bookHeight;
 private Button previous,next,up,down;
 public HandbookScreen(Screen parent){this(parent,"start");}
 public HandbookScreen(Screen parent,String openingChapter){
  super(Component.translatable("mahoutsukai.handbook.open"));this.parent=parent;
  for(String key:List.of("start","familiar","world","changes","doom","fireball","william")){if(key.equals(openingChapter))chapter=chapters.size();chapters.add(new Chapter(tr(key+".title"),tr(key+".body")));}
  for(Page page:new GuidebookInstance().getPages()){
   if(page instanceof SpellPage spell){
    // Recipe disclosure remains governed by the server catalogue.
    chapters.add(new Chapter(spell.getTitle(),GuideCatalogScreen.knows(spell.getRecipe())?spell.getDescription():tr("locked")));
   }else if(page instanceof InfoPage info){if(info.getTitle().equals(Component.translatable("mahoutsukai.book."+openingChapter+".name").getString()))chapter=chapters.size();chapters.add(new Chapter(info.getTitle(),info.getDescription()));}
   else if(page instanceof DoubleInfoPage info){chapters.add(new Chapter(info.getTitle(),info.getDescription()));chapters.add(new Chapter(info.getTitle2(),info.getDescription2()));}
  }
 }
 private static String tr(String key){return Component.translatable("mahoutsukai.handbook."+key).getString();}
 @Override protected void init(){
  bookWidth=Math.min(650,width-16);bookHeight=Math.min(360,height-16);left=(width-bookWidth)/2;top=(height-bookHeight)/2;
  var search=new EditBox(font,left+160,top+bookHeight-30,Math.max(80,bookWidth-264),20,Component.translatable("mahoutsukai.catalog.search"));
  search.setHint(Component.translatable("mahoutsukai.catalog.search"));search.setResponder(value->{if(!value.isBlank())for(int i=0;i<chapters.size();i++)if(chapters.get(i).title().toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))){chapter=i;offset=0;break;}});addRenderableWidget(search);
  previous=addRenderableWidget(Button.builder(Component.literal("‹"),b->{chapter=Math.max(0,chapter-1);offset=0;}).bounds(left+12,top+bookHeight-30,30,20).build());
  next=addRenderableWidget(Button.builder(Component.literal("›"),b->{chapter=Math.min(chapters.size()-1,chapter+1);offset=0;}).bounds(left+48,top+bookHeight-30,30,20).build());
  up=addRenderableWidget(Button.builder(Component.literal("↑"),b->offset=Math.max(0,offset-6)).bounds(left+bookWidth-44,top+38,30,20).build());
  down=addRenderableWidget(Button.builder(Component.literal("↓"),b->offset=Math.min(maxOffset(),offset+6)).bounds(left+bookWidth-44,top+bookHeight-62,30,20).build());
  addRenderableWidget(Button.builder(Component.translatable("gui.back"),b->onClose()).bounds(left+bookWidth-90,top+bookHeight-30,76,20).build());
 }
 private List<FormattedCharSequence> lines(){return font.split(Component.literal(chapters.get(chapter).body()),bookWidth-76);}
 private int visibleLines(){return Math.max(1,(bookHeight-94)/(font.lineHeight+3));}
 private int maxOffset(){return Math.max(0,lines().size()-visibleLines());}
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){
  g.fill(left,top,left+bookWidth,top+bookHeight,0xF0211B27);g.fill(left,top,left+bookWidth,top+2,0xFF9C7EB2);
  String heading=chapters.get(chapter).title();g.text(font,font.plainSubstrByWidth(heading,bookWidth-30),left+14,top+15,0xFFF0E1F4);
  var lines=lines();offset=Math.clamp(offset,0,maxOffset());for(int i=0;i<visibleLines()&&i+offset<lines.size();i++)g.text(font,lines.get(i+offset),left+16,top+42+i*(font.lineHeight+3),0xFFD3C7D9);
  previous.active=chapter>0;next.active=chapter+1<chapters.size();up.active=offset>0;down.active=offset<maxOffset();
  g.text(font,(chapter+1)+" / "+chapters.size(),left+90,top+bookHeight-24,0xFFB7A7C0);super.extractRenderState(g,mx,my,dt);
 }
 @Override public boolean mouseScrolled(double x,double y,double dx,double dy){offset=Math.clamp(offset+(dy>0?-3:3),0,maxOffset());return true;}
 @Override public void onClose(){minecraft.setScreen(parent);}
 @Override public boolean isPauseScreen(){return false;}
}
