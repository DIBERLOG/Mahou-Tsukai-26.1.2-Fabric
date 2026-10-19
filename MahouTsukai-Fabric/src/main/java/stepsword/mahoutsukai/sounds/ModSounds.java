package stepsword.mahoutsukai.sounds;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
public final class ModSounds {
 private static Supplier<SoundEvent> register(String name) {
  var id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
  var sound = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
  return () -> sound;
 }
 public static final Supplier<SoundEvent> FAE_CHIME_1 = register("fae.chime1");
 public static final Supplier<SoundEvent> FAE_CHIME_2 = register("fae.chime2");
 public static final Supplier<SoundEvent> FAE_CHIME_3 = register("fae.chime3");
 public static final Supplier<SoundEvent> FAE_CHIME_4 = register("fae.chime4");
 public static final Supplier<SoundEvent> SMITE=register("caliburn.smite");
 public static final Supplier<SoundEvent> CHAINS=register("chain.pull");
 public static final Supplier<SoundEvent> BEAM_TWINKLE=register("beam.twinkle");
 public static final Supplier<SoundEvent> EXPLOSION=register("mystic.explode");
 public static final Supplier<SoundEvent> EXPLOSION_BIG=register("mystic.explosion_big");
 public static final Supplier<SoundEvent> TAIKOBIG=register("nobu.taikobig");
 public static final Supplier<SoundEvent> TAIKO1=register("nobu.taiko1");
 public static final Supplier<SoundEvent> TAIKO2=register("nobu.taiko2");
 public static final Supplier<SoundEvent> TAIKO3=register("nobu.taiko3");
 public static final Supplier<SoundEvent> TAIKOFIRE=register("nobu.taikofire");
 public static final Supplier<SoundEvent> TAIKOBOSS=register("nobu.taikoboss");
 public static final Supplier<SoundEvent> DOOM_ARRIVAL_HORN=register("doom.arrival_horn");
 public static final Supplier<SoundEvent> DOOM_IMPACT_HORN=register("doom.impact_horn");
 public static final Supplier<SoundEvent> DOOM_RUMBLE=register("doom.rumble");
 public static final Supplier<SoundEvent> ARCHMAGE_CHARGE=register("archmage.charge");
 public static final Supplier<SoundEvent> ARCHMAGE_ARRIVAL=register("archmage.arrival");
 public static final Supplier<SoundEvent> ARCHMAGE_DEFEAT_LAUGH=register("archmage.defeat_laugh");
 public static final Supplier<SoundEvent> ARCHMAGE_RELEASE=register("archmage.release");
 public static final Supplier<SoundEvent> ARCHMAGE_SHOCKWAVE=register("archmage.shockwave");
 public static final Supplier<SoundEvent> DOOM_IMPACT=register("doom.impact");
 private static final java.util.Map<String,Supplier<SoundEvent>> CAST_SOUNDS=new java.util.HashMap<>();
 static {for(String family:java.util.List.of("doom","projection","displacement","familiar","eyes","boundary","secret","mystic","exchange"))for(String stage:java.util.List.of("charge","release"))CAST_SOUNDS.put(family+"."+stage,register("cast."+family+"."+stage));}
 public static SoundEvent cast(String family,boolean release){return CAST_SOUNDS.get(family+(release?".release":".charge")).get();}
 private static final java.util.Map<net.minecraft.world.item.Item,SoundEvent[]> SPELL_SOUNDS=new java.util.IdentityHashMap<>();
 public static SoundEvent spellCast(net.minecraft.world.item.Item spell,boolean release){
  var sounds=SPELL_SOUNDS.get(spell);
  return sounds==null?cast(stepsword.mahoutsukai.fabric.SpellPresentation.of(spell).soundKey(),release):sounds[release?1:0];
 }
 public static void init() {
  // Called after ModItems.init(): accessing the items here cannot recurse during their construction.
  for(var entry:stepsword.mahoutsukai.item.ModItems.REGISTERED.entrySet())if(entry.getValue() instanceof stepsword.mahoutsukai.item.spells.SpellScroll&&!SPELL_SOUNDS.containsKey(entry.getValue())){
   String name=entry.getKey().replaceFirst("^scroll_","");
   SPELL_SOUNDS.put(entry.getValue(),new SoundEvent[]{register("spell."+name+".charge").get(),register("spell."+name+".release").get()});
  }
 }
}
