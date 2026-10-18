package stepsword.mahoutsukai;
import stepsword.mahoutsukai.item.william.WilliamCloningRecipe;
public final class MahouRecipes {
 private static final net.minecraft.world.item.crafting.RecipeSerializer<WilliamCloningRecipe> CLONING=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","william_bookcloning"),new net.minecraft.world.item.crafting.RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(WilliamCloningRecipe::new),net.minecraft.network.codec.StreamCodec.of((output,recipe)->{},input->new WilliamCloningRecipe())));
 public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeSerializer<WilliamCloningRecipe>> WILLIAM_CLONING_RECIPE=()->CLONING;
 public static void init(){}
}
