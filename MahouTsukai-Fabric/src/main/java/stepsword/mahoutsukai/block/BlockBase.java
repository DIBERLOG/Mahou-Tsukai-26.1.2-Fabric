package stepsword.mahoutsukai.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockBase extends Block {
   protected String name;

   public BlockBase(Properties p, String name) {
      super(p.setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("mahoutsukai", name))));
      this.name = name;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[0]);
   }

   public BlockItem createItemBlock() {
      return new BlockItem(
         this,
         new net.minecraft.world.item.Item.Properties()
            .useBlockDescriptionPrefix()
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("mahoutsukai", this.name)))
            .stacksTo(64)
      );
   }
}
