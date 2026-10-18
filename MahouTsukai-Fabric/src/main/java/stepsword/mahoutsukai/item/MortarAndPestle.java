package stepsword.mahoutsukai.item;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Item.Properties;

public class MortarAndPestle extends ItemBase {
   @Override
   public ItemStackTemplate getCraftingRemainder(net.minecraft.world.item.ItemStack stack) {
      return new ItemStackTemplate(this);
   }
   public MortarAndPestle() {
      super("mortar_and_pestle", new Properties().stacksTo(1));
   }
}
