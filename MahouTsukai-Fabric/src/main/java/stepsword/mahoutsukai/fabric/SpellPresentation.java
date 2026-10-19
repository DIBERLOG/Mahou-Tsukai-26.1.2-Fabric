package stepsword.mahoutsukai.fabric;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import stepsword.mahoutsukai.item.ManaCapacityScroll;
import stepsword.mahoutsukai.item.spells.SpellScroll;

/** Cosmetic identity only. Does not decide targeting, payment or spell success. */
public record SpellPresentation(Family family,int color,int accent,int seed) {
 public enum Motif { BLOSSOM,EYE,WARD,BLADE,RIFT,CHAIN,CROWN,EMBER,SPEAR,TWIN,ECLIPSE,ORBIT }
 private static final java.util.Map<Integer,Motif> MOTIFS=new java.util.HashMap<>();
 static {
  motifs(Motif.BLOSSOM,"summon_familiar familiars_garden butterfly_effect");
  motifs(Motif.EYE,"fay_sight insight prediction reversion_eyes mystic_eyes possess_entity");
  motifs(Motif.WARD,"boundary_alarm boundary_displacement boundary_drain_life boundary_gravity boundary_raise_enclosure boundary_tangible protective_displacement immunity_exchange rho_aias");
  motifs(Motif.BLADE,"projection proximity_projection strengthening treasury_projection weapon_shooter presence_concealment probability_alter");
  motifs(Motif.RIFT,"reality_marble mental_displacement projectile_displacement selective_displacement spatial_disorientation ascension recall_familiar");
  motifs(Motif.CHAIN,"geas retribution death_collection");
  motifs(Motif.CROWN,"fallen_down borrowed_authority cup_of_heaven");
  motifs(Motif.EMBER,"black_flame fireball");motifs(Motif.SPEAR,"gandr");motifs(Motif.TWIN,"swap_familiar damage_replication damage_exchange");motifs(Motif.ECLIPSE,"doom");motifs(Motif.ORBIT,"mystic_staff");
 }
 private static void motifs(Motif motif,String names){for(String name:names.split(" "))MOTIFS.put(("scroll_"+name).hashCode(),motif);}
 public enum Family {
  DOOM(0x9029ED,0xE3B7FF),PROJECTION(0xFFBB42,0xFFF3C4),DISPLACEMENT(0x8D6BFF,0x70EEFF),
  FAMILIAR(0x55ECA8,0xEDFFB3),EYES(0xE86FCB,0xA4DFFF),BOUNDARY(0x56B9FF,0xC4F8FF),
  SECRET(0xF2496E,0xFFC980),MYSTIC(0x74CEFF,0xF5EAFF),EXCHANGE(0x34DECF,0xFFF09A);
  public final int color,accent;
  Family(int color,int accent){this.color=color;this.accent=accent;}
 }
 public static boolean supports(Item item){return item instanceof SpellScroll||item instanceof ManaCapacityScroll||item instanceof stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;}
 public static SpellPresentation of(Item item){
  String path=BuiltInRegistries.ITEM.getKey(item).getPath(),type=item.getClass().getPackageName();
  Family family=path.equals("scroll_doom")?Family.DOOM:Family.MYSTIC;
  for(var value:Family.values())if(type.contains(".spells."+value.name().toLowerCase(java.util.Locale.ROOT)))family=value;
  if(item instanceof ManaCapacityScroll)family=Family.EXCHANGE;
  int seed=path.hashCode();
  if(path.equals("scroll_fireball"))return new SpellPresentation(Family.MYSTIC,0xFF731F,0xFFF0A6,seed);
  // Stable subtle tint and rune count distinguish individual spells in one family.
  int color=blend(family.color,family.accent,Math.floorMod(seed,5)*0.055);
  if(path.equals("scroll_black_flame")){color=0x7434A6;return new SpellPresentation(family,color,0xFF9A6A,seed);}
  if(path.equals("scroll_fallen_down"))return new SpellPresentation(family,0xFFE7A3,0xFFFFFF,seed);
  if(path.equals("scroll_familiars_garden"))return new SpellPresentation(family,0xF69CCF,0xFFF1AF,seed);
  return new SpellPresentation(family,color,family.accent,seed);
 }
 private static int blend(int a,int b,double t){int out=0;for(int shift:new int[]{0,8,16})out|=(int)(((a>>shift)&255)*(1-t)+((b>>shift)&255)*t)<<shift;return out;}
 public float pitch(){return 0.91F+Math.floorMod(seed,9)*0.025F;}
 public String soundKey(){return family.name().toLowerCase(java.util.Locale.ROOT);}
 public Motif motif(){return MOTIFS.getOrDefault(seed,switch(family){case DOOM->Motif.ECLIPSE;case PROJECTION->Motif.BLADE;case FAMILIAR->Motif.BLOSSOM;case EYES->Motif.EYE;case BOUNDARY->Motif.WARD;case DISPLACEMENT->Motif.RIFT;default->Motif.ORBIT;});}
 public static boolean hasExplicitMotif(Item item){return MOTIFS.containsKey(BuiltInRegistries.ITEM.getKey(item).getPath().hashCode());}
}
