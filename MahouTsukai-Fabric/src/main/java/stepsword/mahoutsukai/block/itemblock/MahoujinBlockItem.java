package stepsword.mahoutsukai.block.itemblock;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class MahoujinBlockItem extends BlockItem {
   public MahoujinBlockItem(Block block, String name) {
      super(
         block,
         new Properties()
            .useBlockDescriptionPrefix()
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("mahoutsukai", name)))
            .stacksTo(64)
      );
   }
}
