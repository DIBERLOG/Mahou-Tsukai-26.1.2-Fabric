package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ScrollFromMahoujinJeiRecipe(Identifier id, List<ItemStack> catalysts, MahoujinJeiIngredient mahoujin, ItemStack output)
   implements MahouScrollJeiRecipe {
   public ScrollFromMahoujinJeiRecipe {
      catalysts = catalysts.stream().<ItemStack>map(ItemStack::copy).toList();
      output = output.copy();
   }
}
