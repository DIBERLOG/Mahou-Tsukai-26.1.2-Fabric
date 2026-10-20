package stepsword.mahoutsukai.render.gui;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.ManaCapacityScroll;
import stepsword.mahoutsukai.item.spells.SpellScroll;

/** Small native GUI glyphs, independent of spell timing, item components and world rendering. */
public final class ScrollBadges {
 public enum Role {
  ATTACK(0xFFE3C3A7,"....X","...XX","X.X..",".X...","X.X.."),
  DEFENSE(0xFFA5D8F4,"XXXXX","X...X","X.X.X",".XXX.","..X.."),
  SUPPORT(0xFF9BE4AD,"..X..","..X..","XXXXX","..X..","..X.."),
  MOVEMENT(0xFFECC486,"..X..","...X.","XXXXX","...X.","..X.."),
  CONTROL(0xFFC9ACEA,".XX..","X..X.",".XXX.",".X..X","..XX."),
  INSIGHT(0xFFF0B9D8,".....",".XXX.","XX.XX",".XXX.","....."),
  FAMILIAR(0xFFB4F1CD,"X.X.X","XX.XX",".XXX.","..X..",".X.X."),
  MANA(0xFF8ECDF2,"..X..",".XXX.","XXXXX",".XXX.","..X.."),
  RITUAL(0xFFDDA0AD,".XXX.","XX.XX","X.X.X","XX.XX",".XXX.");
  final int color;final String[] pixels;
  Role(int color,String...pixels){this.color=color;this.pixels=pixels;}
  public String translation(){return "mahoutsukai.scroll.role."+name().toLowerCase(java.util.Locale.ROOT);}
 }
 public enum Element {
  FIRE(0xFFFFA155,"..X..",".XX..",".X.X.","XXXXX",".XXX."),
  BLACK_FIRE(0xFFBD8FE3,"X.X..","X.XX.","XX.XX","XXXXX",".XXX."),
  VOID(0xFFAD8CEB,".XXXX","XX...","X.XXX","X...X",".XXXX");
  final int color;final String[] pixels;
  Element(int color,String...pixels){this.color=color;this.pixels=pixels;}
  public String translation(){return "mahoutsukai.scroll.element."+name().toLowerCase(java.util.Locale.ROOT);}
 }
 private static final Map<String,Role> ROLES=new HashMap<>();
 static{
  roles(Role.ATTACK,"gandr weapon_shooter treasury_projection proximity_projection presence_concealment mystic_staff doom fireball fallen_down black_flame probability_alter damage_replication");
  roles(Role.DEFENSE,"rho_aias protective_displacement immunity_exchange damage_exchange boundary_tangible boundary_raise_enclosure butterfly_effect retribution");
  roles(Role.SUPPORT,"strengthening projection borrowed_authority cup_of_heaven familiars_garden recall_familiar death_collection boundary_drain_life");
  roles(Role.MOVEMENT,"mental_displacement projectile_displacement selective_displacement ascension boundary_displacement swap_familiar");
  roles(Role.CONTROL,"mystic_eyes spatial_disorientation geas boundary_gravity reversion_eyes");
  roles(Role.INSIGHT,"insight prediction fay_sight possess_entity boundary_alarm");
  roles(Role.FAMILIAR,"summon_familiar");roles(Role.RITUAL,"reality_marble");
 }
 private static void roles(Role role,String names){for(var name:names.split(" "))if(ROLES.put("scroll_"+name,role)!=null)throw new IllegalStateException("Duplicate scroll badge: "+name);}
 public static boolean supports(ItemStack stack){return !stack.isEmpty()&&(stack.getItem() instanceof SpellScroll||stack.getItem() instanceof ManaCapacityScroll);}
 public static Role role(ItemStack stack){return stack.getItem() instanceof ManaCapacityScroll?Role.MANA:ROLES.get(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());}
 public static Element element(ItemStack stack){return switch(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()){case "scroll_fireball"->Element.FIRE;case "scroll_black_flame"->Element.BLACK_FIRE;case "scroll_doom"->Element.VOID;default->null;};}
 public static void init(){
  ItemTooltipCallback.EVENT.register((stack,context,flag,lines)->{
   if(!supports(stack))return;var role=role(stack);if(role==null)return;
   int at=Math.min(1,lines.size());lines.add(at++,Component.translatable("mahoutsukai.scroll.role",Component.translatable(role.translation())).withStyle(ChatFormatting.GRAY));
   var element=element(stack);if(element!=null)lines.add(at,Component.translatable("mahoutsukai.scroll.element",Component.translatable(element.translation())).withStyle(ChatFormatting.GRAY));
  });
 }
 public static void render(GuiGraphicsExtractor graphics,ItemStack stack,int x,int y){
  if(!supports(stack))return;var role=role(stack);if(role==null)return;
  badge(graphics,x+9,y+9,role.color,role.pixels);
  var element=element(stack);if(element!=null)badge(graphics,x+1,y+9,element.color,element.pixels);
 }
 private static void badge(GuiGraphicsExtractor graphics,int x,int y,int color,String[] pixels){
  graphics.fill(x,y,x+7,y+7,0xF016121C);
  graphics.fill(x+1,y,x+6,y+1,0xFF514656);graphics.fill(x,y+1,x+1,y+6,0xFF3B3344);
  for(int row=0;row<5;row++)for(int col=0;col<5;){
   if(pixels[row].charAt(col)!='X'){col++;continue;}
   int start=col++;while(col<5&&pixels[row].charAt(col)=='X')col++;
   int shade=row<2?color:0xFF000000|(((color>>16&255)*9/10)<<16)|(((color>>8&255)*9/10)<<8)|(color&255)*9/10;
   graphics.fill(x+1+start,y+1+row,x+1+col,y+2+row,shade);
  }
 }
 public static void legend(GuiGraphicsExtractor graphics,Role role,int x,int y){badge(graphics,x,y,role.color,role.pixels);}
}
