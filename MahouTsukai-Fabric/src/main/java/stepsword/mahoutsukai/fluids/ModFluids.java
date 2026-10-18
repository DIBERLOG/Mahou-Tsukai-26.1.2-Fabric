package stepsword.mahoutsukai.fluids;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
public final class ModFluids {
 public static final MurkyFluid.Source MURKY_STILL=Registry.register(BuiltInRegistries.FLUID,Identifier.fromNamespaceAndPath("mahoutsukai","murky_still"),new MurkyFluid.Source());
 public static final MurkyFluid.Flowing MURKY_FLOW=Registry.register(BuiltInRegistries.FLUID,Identifier.fromNamespaceAndPath("mahoutsukai","murky_flow"),new MurkyFluid.Flowing());
 public static void init() {}
}
