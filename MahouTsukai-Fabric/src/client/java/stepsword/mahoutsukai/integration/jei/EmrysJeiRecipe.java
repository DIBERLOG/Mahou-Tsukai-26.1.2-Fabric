package stepsword.mahoutsukai.integration.jei;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record EmrysJeiRecipe(Identifier id, ItemStack strengtheningScroll, ItemStack stick, EntityJeiIngredient lightning, ItemStack output)
   implements MahouScrollJeiRecipe {
   public EmrysJeiRecipe {
      strengtheningScroll = strengtheningScroll.copy();
      stick = stick.copy();
      output = output.copy();
   }
}
