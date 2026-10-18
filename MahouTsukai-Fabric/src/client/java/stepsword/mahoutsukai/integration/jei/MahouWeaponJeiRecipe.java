package stepsword.mahoutsukai.integration.jei;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record MahouWeaponJeiRecipe(Identifier id, ItemStack scroll, ItemStack weapon) {
   public MahouWeaponJeiRecipe {
      scroll = scroll.copy();
      weapon = weapon.copy();
   }
}
