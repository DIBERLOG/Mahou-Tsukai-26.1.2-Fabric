package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ClarentJeiRecipe(Identifier id, ItemStack strengtheningScroll, List<ItemStack> swords, EntityJeiIngredient dragonBreath, ItemStack output)
   implements MahouScrollJeiRecipe {
   public ClarentJeiRecipe {
      strengtheningScroll = strengtheningScroll.copy();
      swords = swords.stream().<ItemStack>map(ItemStack::copy).toList();
      output = output.copy();
   }
}
