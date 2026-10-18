package stepsword.mahoutsukai.item;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Item.Properties;

public class Hammer extends ItemBase {
   @Override
   public ItemStackTemplate getCraftingRemainder(net.minecraft.world.item.ItemStack stack) {
      return new ItemStackTemplate(this);
   }
   public Hammer() {
      super("hammer", new Properties().stacksTo(1));
   }
}
