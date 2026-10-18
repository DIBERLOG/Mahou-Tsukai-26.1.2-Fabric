package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record CaliburnJeiRecipe(Identifier id, List<ItemStack> swords, MahoujinJeiIngredient powerConsolidation, ItemStack enchantedBook, ItemStack output)
   implements MahouScrollJeiRecipe {
   public CaliburnJeiRecipe {
      swords = swords.stream().<ItemStack>map(ItemStack::copy).toList();
      enchantedBook = enchantedBook.copy();
      output = output.copy();
   }
}
