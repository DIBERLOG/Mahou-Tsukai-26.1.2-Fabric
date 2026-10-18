package stepsword.mahoutsukai.datacomponents.stackholder;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class StackHolderMahou implements CopyComponent<StackHolderMahou> {
   public Optional<ItemStack> stack = Optional.empty();

   public StackHolderMahou() {
   }

   public StackHolderMahou(Optional<ItemStack> s) {
      this.stack = s.isPresent() && !s.get().isEmpty() ? Optional.of(s.get().copy()) : Optional.empty();
   }

   public Optional<ItemStack> getStack() {
      return this.stack;
   }

   public void setStack(ItemStack s) {
      this.stack = s != null && !s.isEmpty() ? Optional.of(s.copy()) : Optional.empty();
   }

   public StackHolderMahou copy() {
      return new StackHolderMahou(!this.stack.isEmpty() && !this.stack.get().isEmpty() ? Optional.of(this.stack.get().copy()) : Optional.empty());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.stack);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof StackHolderMahou b ? b.stack == this.stack : false;
   }
}
