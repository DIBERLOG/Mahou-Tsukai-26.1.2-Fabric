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
 public static final Supplier<SoundEvent> TAIKOBIG=register("nobu.taikobig");
 public static final Supplier<SoundEvent> TAIKO1=register("nobu.taiko1");
 public static final Supplier<SoundEvent> TAIKO2=register("nobu.taiko2");
 public static final Supplier<SoundEvent> TAIKO3=register("nobu.taiko3");
 public static final Supplier<SoundEvent> TAIKOFIRE=register("nobu.taikofire");
 public static final Supplier<SoundEvent> TAIKOBOSS=register("nobu.taikoboss");
 public static void init() {}
}
