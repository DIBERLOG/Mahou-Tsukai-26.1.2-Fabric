package stepsword.mahoutsukai.advancements;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
public final class ModTriggers {
 private static final MahouTrigger MAHOU = Registry.register(BuiltInRegistries.TRIGGER_TYPES,
  Identifier.fromNamespaceAndPath("mahoutsukai", "mahoutsukai"), new MahouTrigger());
 public static final Supplier<MahouTrigger> MAHOUTSUKAI = () -> MAHOU;
 public static final MahouTrigger CLARENT=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","clarent"),new MahouTrigger());
 public static final MahouTrigger BOOGIE_WOOGIE=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","boogie_woogie"),new MahouTrigger());
 public static final MahouTrigger GREATER_GRAIL=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","greater_grail"),new MahouTrigger());
 public static final MahouTrigger LESSER_GRAIL=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","lesser_grail"),new MahouTrigger());
 public static final MahouTrigger GANDR=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","gandr"),new MahouTrigger());
 public static final MahouTrigger FLYING_PIGS=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","flying_pigs"),new MahouTrigger());
 public static final MahouTrigger MORGAN=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","morgan"),new MahouTrigger());
 public static final MahouTrigger BREADCRUMBS=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","breadcrumbs"),new MahouTrigger());
 public static final MahouTrigger SECRET_CALIBUR=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","secret_calibur"),new MahouTrigger());
 public static final MahouTrigger SWORD_IN_THE_LAKE=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","sword_in_the_lake"),new MahouTrigger());
 public static final MahouTrigger TREASURY_PROJECTION=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","treasury_projection"),new MahouTrigger());
 public static final MahouTrigger BEAM=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","beam"),new MahouTrigger());
 public static final MahouTrigger BAKURETSU=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","bakuretsu"),new MahouTrigger());
 public static final MahouTrigger BUTTERFLY=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","butterfly"),new MahouTrigger());
 public static final MahouTrigger KODOKU=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","kodoku"),new MahouTrigger());
 public static final MahouTrigger EMRYS=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","emrys"),new MahouTrigger());
 public static final MahouTrigger REPLICA=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","replica"),new MahouTrigger());
 public static final MahouTrigger FINAL_FORTRESS=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","final_fortress"),new MahouTrigger());
 public static final MahouTrigger NOBU=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","nobu"),new MahouTrigger());
 public static void init() {}
}
