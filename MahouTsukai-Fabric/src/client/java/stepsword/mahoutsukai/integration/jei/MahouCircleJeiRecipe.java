package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record MahouCircleJeiRecipe(Identifier id, List<ItemStack> catalysts, MahoujinJeiIngredient output) {
   public MahouCircleJeiRecipe {
      catalysts = catalysts.stream().<ItemStack>map(ItemStack::copy).toList();
   }
}
