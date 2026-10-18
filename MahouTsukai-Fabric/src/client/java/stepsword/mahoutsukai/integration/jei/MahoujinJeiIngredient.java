package stepsword.mahoutsukai.integration.jei;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.util.UnorderedList;

public record MahoujinJeiIngredient(Identifier id, Identifier blockId, List<String> catalysts, boolean cloth) {
   public static final Codec<MahoujinJeiIngredient> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(MahoujinJeiIngredient::id),
            Identifier.CODEC.fieldOf("block").forGetter(MahoujinJeiIngredient::blockId),
            Codec.STRING.listOf().fieldOf("catalysts").forGetter(MahoujinJeiIngredient::catalysts),
            Codec.BOOL.fieldOf("cloth").forGetter(MahoujinJeiIngredient::cloth)
         )
         .apply(instance, MahoujinJeiIngredient::new)
   );

   public MahoujinJeiIngredient {
      catalysts = List.copyOf(catalysts);
   }

   public static MahoujinJeiIngredient create(Identifier blockId, UnorderedList recipe, boolean cloth) {
      Identifier id = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "mahoujin/" + blockId.getPath() + (cloth ? "_cloth" : ""));
      return new MahoujinJeiIngredient(id, blockId, recipe.getOrder(), cloth);
   }

   public Component getName() {
      return (Component)BuiltInRegistries.BLOCK
         .getOptional(this.blockId)
         .map(block -> block.getName())
         .orElseGet(() -> Component.literal(this.blockId.toString()));
   }

   public ItemStack createRenderStack() {
      ItemStack stack = new ItemStack(ModBlocks.mahoujin.get().asItem());
      CompoundTag tag = new CompoundTag();
      ListTag catalystTags = new ListTag();
      this.catalysts.forEach(catalyst -> catalystTags.add(StringTag.valueOf(catalyst)));
      tag.put("MahouJeiCatalysts", catalystTags);
      tag.putBoolean("MahouJeiCloth", this.cloth);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return stack;
   }
}
