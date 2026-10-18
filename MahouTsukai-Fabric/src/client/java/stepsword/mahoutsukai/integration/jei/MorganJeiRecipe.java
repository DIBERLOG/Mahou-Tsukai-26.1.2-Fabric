package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record MorganJeiRecipe(Identifier id, ItemStack caliburn, List<EntityJeiIngredient> killableEntities, ItemStack output) implements MahouScrollJeiRecipe {
   public MorganJeiRecipe {
      caliburn = caliburn.copy();
      killableEntities = List.copyOf(killableEntities);
      output = output.copy();
   }
}
