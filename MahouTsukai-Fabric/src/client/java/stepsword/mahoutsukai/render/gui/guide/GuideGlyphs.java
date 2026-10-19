package stepsword.mahoutsukai.render.gui.guide;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Drawn icons for catalogue rows that have no inventory item.
 * <p>Ground-only rituals are real registered blocks without an item form. Registering fake items just to
 * get an icon would corrupt creative search and server item lists, so the guide draws a shared ritual
 * sigil instead.</p>
 */
final class GuideGlyphs {
 private GuideGlyphs(){}

 /** True when the row points at a registered block that has no item counterpart (a ground-only ritual). */
 static boolean itemlessRitual(String id){
  var key=Identifier.parse(id);
  return !BuiltInRegistries.ITEM.containsKey(key)&&BuiltInRegistries.BLOCK.containsKey(key);
 }

 static boolean itemlessRitual(JsonObject row){
  return row.has("item")&&itemlessRitual(row.get("item").getAsString());
 }

 private static void disc(GuiGraphicsExtractor g,int cx,int cy,int radius,int color){
  for(int dy=-radius;dy<=radius;dy++){int dx=(int)Math.sqrt(radius*radius-dy*dy);g.fill(cx-dx,cy+dy,cx+dx+1,cy+dy+1,color);}
 }

 /** A ring with a crown tick: readable at 16 px and clearly distinct from an empty slot. */
 static void ritual(GuiGraphicsExtractor g,int x,int y){
  int cx=x+8,cy=y+8;
  disc(g,cx,cy,8,0xFF2A2434);
  disc(g,cx,cy,7,0xFFB49BD6);
  disc(g,cx,cy,5,0xFF2A2434);
  disc(g,cx,cy,4,0xFF6E5A8A);
  disc(g,cx,cy,2,0xFFE7D9F7);
  g.fill(cx-7,cy-10,cx+8,cy-9,0xFFB49BD6);
  g.fill(cx-1,cy-9,cx+2,cy-6,0xFFB49BD6);
 }
}
