package stepsword.mahoutsukai.containers;
public final class ModContainers {
 private static final net.minecraft.world.inventory.MenuType<MysticCodeInventory> TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.MENU,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_inventory"),new net.minecraft.world.inventory.MenuType<>(MysticCodeInventory::new,net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
 public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<MysticCodeInventory>> MYSTIC_CODE=()->TYPE;
 public static void init(){}
}
