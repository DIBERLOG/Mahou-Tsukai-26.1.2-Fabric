package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ReplicaJeiRecipe(Identifier id, List<ItemStack> inputs, ItemStack output) implements MahouScrollJeiRecipe {
   public ReplicaJeiRecipe {
      inputs = inputs.stream().<ItemStack>map(ItemStack::copy).toList();
      output = output.copy();
   }
}
