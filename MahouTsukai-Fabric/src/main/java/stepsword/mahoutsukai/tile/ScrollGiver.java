package stepsword.mahoutsukai.tile;

import net.minecraft.world.item.ItemStack;

public interface ScrollGiver {
   default ItemStack getItemToGive() {
      return ItemStack.EMPTY;
   }
}
