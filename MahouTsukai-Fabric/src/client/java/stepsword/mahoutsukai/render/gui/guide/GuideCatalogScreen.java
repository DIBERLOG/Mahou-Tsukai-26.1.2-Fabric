package stepsword.mahoutsukai.render.gui.guide;

import com.google.gson.*;

import java.util.*;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.minecraft.client.gui.components.Button;

import net.minecraft.client.gui.components.EditBox;

import net.minecraft.client.gui.screens.Screen;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.Identifier;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.ItemStack;

import stepsword.mahoutsukai.item.spells.SpellScroll;

import stepsword.mahoutsukai.mana.PlayerManaManager;

/** Searchable book with server-authoritative, persistent discoveries. */

public final class GuideCatalogScreen extends Screen {

 private static JsonArray received=new JsonArray();

 private final List<JsonObject> entries=new ArrayList<>(),filtered=new ArrayList<>();

 private final List<Button> rows=new ArrayList<>();

 private JsonObject selected;private String query="";private int page,left,top,bookWidth,bookHeight,rowCount,listWidth;private Button previous,next,details;private EditBox search;

 public GuideCatalogScreen(String json){super(Component.translatable("mahoutsukai.catalog.title"));received=JsonParser.parseString(json).getAsJsonArray();for(var value:received){var row=value.getAsJsonObject();if(!row.get("kind").getAsString().equals("research"))entries.add(row);}}

 private static String tr(String key,Object...args){return Component.translatable("mahoutsukai.catalog."+key,args).getString();}

 private static String id(JsonObject row){return row.get("item").getAsString();}

 private static net.minecraft.world.item.Item item(String id){var key=Identifier.parse(id);return BuiltInRegistries.ITEM.containsKey(key)?BuiltInRegistries.ITEM.getValue(key):null;}

 private static String name(JsonObject row){var item=item(id(row));if(item!=null)return new ItemStack(item).getHoverName().getString();var block=BuiltInRegistries.BLOCK.getValue(Identifier.parse(id(row)));return block==null?id(row):block.getName().getString();}

 private String fit(String text,int width){return font.width(text)<=width?text:font.plainSubstrByWidth(text,Math.max(0,width-font.width("...")))+"...";}

 @Override protected void init(){

  bookWidth=Math.min(650,width-16);bookHeight=Math.min(360,height-16);left=(width-bookWidth)/2;top=(height-bookHeight)/2;listWidth=Math.max(100,bookWidth/3);rowCount=Math.max(2,(bookHeight-106)/24);rows.clear();

  search=new EditBox(font,left+12,top+30,listWidth-12,20,Component.translatable("mahoutsukai.catalog.search"));search.setMaxLength(100);search.setHint(Component.translatable("mahoutsukai.catalog.search"));search.setValue(query);search.setResponder(value->{query=value;page=0;refresh();});addRenderableWidget(search);

  for(int i=0;i<rowCount;i++){final int index=i;var button=Button.builder(Component.empty(),b->{int offset=page*rowCount+index;if(offset<filtered.size()){selected=filtered.get(offset);refresh();}}).bounds(left+12,top+58+i*24,listWidth-12,22).build();rows.add(button);addRenderableWidget(button);}

  previous=addRenderableWidget(Button.builder(Component.literal("‹"),b->{page--;refresh();}).bounds(left+12,top+bookHeight-37,30,20).build());next=addRenderableWidget(Button.builder(Component.literal("›"),b->{page++;refresh();}).bounds(left+listWidth-30,top+bookHeight-37,30,20).build());

  addRenderableWidget(Button.builder(Component.translatable("mahoutsukai.handbook.open"),b->Minecraft.getInstance().setScreen(new HandbookScreen(this))).bounds(left+bookWidth-148,top+5,136,20).build());

  addRenderableWidget(Button.builder(Component.translatable("mahoutsukai.research.title"),b->Minecraft.getInstance().setScreen(new ResearchScreen(this))).bounds(left+bookWidth-276,top+5,122,20).build());

  details=addRenderableWidget(Button.builder(Component.translatable("mahoutsukai.catalog.details"),b->openDetails()).bounds(left+listWidth+14,top+bookHeight-37,Math.max(80,bookWidth-listWidth-104),20).build());

  addRenderableWidget(Button.builder(Component.translatable("gui.done"),b->onClose()).bounds(left+bookWidth-78,top+bookHeight-37,66,20).build());refresh();

 }

 public static JsonArray research(){var result=new JsonArray();for(var value:received)if(value.getAsJsonObject().get("kind").getAsString().equals("research"))result.add(value);return result;}
 public void filter(String value){search.setValue(value);}
 private void refresh(){

  filtered.clear();for(var row:entries)if((name(row)+" "+id(row)).toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)))filtered.add(row);

  page=Math.clamp(page,0,Math.max(0,(filtered.size()-1)/rowCount));if(selected==null||!filtered.contains(selected))selected=filtered.isEmpty()?null:filtered.get(0);

  for(int i=0;i<rows.size();i++){int offset=page*rowCount+i;var button=rows.get(i);button.visible=offset<filtered.size();if(button.visible){var row=filtered.get(offset);button.setMessage(Component.literal(fit((row.get("unlocked").getAsBoolean()?"":"• ")+name(row),listWidth-24)));button.active=row!=selected;}}

  previous.active=page>0;next.active=(page+1)*rowCount<filtered.size();details.active=selected!=null&&selected.get("unlocked").getAsBoolean()&&(!selected.get("kind").getAsString().equals("craft")||item(id(selected)) instanceof SpellScroll);

 }

 private void openDetails(){

  if(selected==null||!selected.get("unlocked").getAsBoolean())return;

  if(id(selected).equals("mahoutsukai:scroll_doom")){Minecraft.getInstance().setScreen(new HandbookScreen(this,"doom"));return;}
  if(id(selected).equals("mahoutsukai:scroll_fireball")){Minecraft.getInstance().setScreen(new HandbookScreen(this,"fireball"));return;}

  var book=new stepsword.mahoutsukai.item.guide.GuidebookInstance();

  for(int i=0;i<book.getPages().size();i++)if(book.getPages().get(i) instanceof stepsword.mahoutsukai.item.guide.SpellPage spell && matches(spell.getRecipe(),selected)){var gui=new PageGui(book);Minecraft.getInstance().setScreen(gui);gui.navigateToPage(i);return;}

  Minecraft.getInstance().setScreen(new HandbookScreen(this));

 }

 private static boolean matches(stepsword.mahoutsukai.util.UnorderedList recipe,JsonObject row){

  var original=new ArrayList<String>();for(String key:recipe.getKeys())for(int i=0;i<recipe.get(key);i++)original.add(BuiltInRegistries.ITEM.getKey(stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar.ingredients.get(key)).toString());

  var other=new ArrayList<String>();row.getAsJsonArray("ingredients").forEach(v->{if(!v.getAsString().equals("mahoutsukai:spell_cloth"))other.add(v.getAsString());});Collections.sort(original);Collections.sort(other);return original.equals(other);

 }

 public static boolean knows(stepsword.mahoutsukai.util.UnorderedList recipe){for(var value:received){var row=value.getAsJsonObject();if(row.get("kind").getAsString().equals("ritual")&&matches(recipe,row))return row.get("unlocked").getAsBoolean();}return false;}

 private int text(GuiGraphicsExtractor graphics,String text,int x,int y,int width,int bottom,int color){for(var line:font.split(Component.literal(text),width)){if(y+font.lineHeight>bottom)break;graphics.text(font,line,x,y,color);y+=font.lineHeight+2;}return y;}

 private String mana(net.minecraft.world.item.Item item){

  if(item instanceof stepsword.mahoutsukai.doom.DoomScroll)return tr("doom_cost");

  if(item instanceof stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll gandr)return tr("mana",PlayerManaManager.spellCost(gandr.getManaCostScaling(stepsword.mahoutsukai.util.Utils.getPlayerMahou(Minecraft.getInstance().player))));

  if(item instanceof stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll)return tr("channel_cost",PlayerManaManager.spellCost(stepsword.mahoutsukai.config.MTConfig.FALLEN_DOWN_MANA_PER_TICK));

  if(item instanceof SpellScroll scroll)return scroll.getInitialManaCost()>0?tr("mana",PlayerManaManager.spellCost(scroll.getInitialManaCost())):tr("variable_cost");return "";

 }

 @Override public void extractRenderState(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float partialTicks){

  graphics.fill(left,top,left+bookWidth,top+bookHeight,0xF0211B27);graphics.fill(left,top,left+bookWidth,top+2,0xFF9C7EB2);graphics.fill(left+listWidth+5,top+29,left+listWidth+6,top+bookHeight-45,0xFF514357);

  graphics.text(font,title.getString(),left+12,top+12,0xFFE8D8EF);graphics.text(font,(page+1)+" / "+Math.max(1,(filtered.size()+rowCount-1)/rowCount),left+48,top+bookHeight-31,0xFFB7A7C0);

  int x=left+listWidth+16,y=top+32,w=bookWidth-listWidth-30,bottom=top+bookHeight-48;

  if(selected==null)text(graphics,tr("no_results"),x,y,w,bottom,0xFFBBB1C5);

  else{

   var selectedItem=item(id(selected));if(selectedItem!=null)graphics.item(new ItemStack(selectedItem),x,y);y=text(graphics,name(selected),x+22,y,w-22,bottom,0xFFF0E1F4)+8;

   y=text(graphics,mana(selectedItem),x,y,w,bottom,0xFFCCB0DF)+5;

   if(!selected.get("unlocked").getAsBoolean())text(graphics,tr("locked",(selected.getAsJsonArray("ingredients").size()+1)/2),x,y,w,bottom,0xFFC9BDCD);

   else{

    String kind=id(selected).equals("mahoutsukai:scroll_doom")?"secret":selected.get("kind").getAsString();y=text(graphics,tr(kind),x,y,w,bottom,0xFFBCACC7)+4;

    var ingredients=kind.equals("secret")?new JsonArray():selected.getAsJsonArray("ingredients");var counts=new LinkedHashMap<String,Integer>();ingredients.forEach(v->counts.merge(v.getAsString(),1,Integer::sum));

    if(kind.equals("craft")){

     for(int row=0;row<3;row++)for(int col=0;col<3;col++){int gx=x+col*20,gy=y+row*20;graphics.fill(gx-1,gy-1,gx+18,gy+18,0xFF524954);graphics.fill(gx,gy,gx+17,gy+17,0xFF322B36);}
     graphics.text(font,">",x+67,y+23,0xFFCCB4DC);if(selectedItem!=null){var output=new ItemStack(selectedItem,selected.get("output").getAsInt());graphics.item(output,x+88,y+20);if(output.getCount()>1)graphics.text(font,"x"+output.getCount(),x+107,y+25,0xFFDDD0E2);}
     var pattern=selected.getAsJsonArray("pattern");int index=0;

     if(pattern.isEmpty()){for(int i=0;i<ingredients.size();i++){var ingredient=item(ingredients.get(i).getAsString());if(ingredient!=null)graphics.item(new ItemStack(ingredient),x+(i%3)*20,y+(i/3)*20);}}

     else for(int row=0;row<pattern.size();row++){String line=pattern.get(row).getAsString();for(int col=0;col<line.length();col++)if(line.charAt(col)!=' '){var ingredient=item(ingredients.get(index++).getAsString());if(ingredient!=null)graphics.item(new ItemStack(ingredient),x+col*20,y+row*20);}}

     y+=64;

    }

    for(var ingredient:counts.entrySet()){var value=item(ingredient.getKey());String label=value==null?ingredient.getKey():new ItemStack(value).getHoverName().getString();y=text(graphics,ingredient.getValue()+" × "+label,x,y,w,bottom,0xFFD3C7D9);}

    if(kind.equals("ritual"))y=text(graphics,tr(selected.get("cloth").getAsBoolean()?"cloth":"no_cloth"),x,y+5,w,bottom,0xFFBFA6CA);

    if(selectedItem instanceof SpellScroll){var stack=new ItemStack(selectedItem);text(graphics,tr("hold",String.format(java.util.Locale.ROOT,"%.1f",stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(stack)/20.0)),x,y+5,w,bottom,0xFFAFA2BB);}

   }

  }

  super.extractRenderState(graphics,mouseX,mouseY,partialTicks);

 }

 @Override public boolean mouseScrolled(double x,double y,double dx,double dy){if(dy!=0){page+=dy>0?-1:1;refresh();return true;}return super.mouseScrolled(x,y,dx,dy);}

 @Override public boolean isPauseScreen(){return false;}

}
