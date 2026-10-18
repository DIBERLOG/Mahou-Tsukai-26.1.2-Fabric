package stepsword.mahoutsukai.item;

import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.util.Utils;

public interface ModeItem<E extends Enum<E>> {
   Class<E> getModes();

   default int getNumModes() {
      return ((Enum[])this.getModes().getEnumConstants()).length;
   }

   default E switchMode(ItemStack stack) {
      int mode = Utils.getMode(stack);
      if (this.canSwitch(stack)) {
         mode = (mode + 1) % this.getNumModes();
         Utils.setMode(stack, mode);
         this.onSwitch(stack);
      }

      return this.getModes().getEnumConstants()[mode];
   }

   default E getMode(ItemStack stack) {
      return this.getModes().getEnumConstants()[Utils.getMode(stack)];
   }

   default boolean canSwitch(ItemStack stack) {
      return true;
   }

   default void onSwitch(ItemStack stack) {
   }
}
