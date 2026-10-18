package stepsword.mahoutsukai.item.william;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.MahouRecipes;
import stepsword.mahoutsukai.item.ModItems;

public class WilliamCloningRecipe extends CustomRecipe {
   public boolean matches(CraftingInput p_345586_, Level p_43815_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for (int j = 0; j < p_345586_.size(); j++) {
         ItemStack itemstack1 = p_345586_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(ModItems.william.get())) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                  return false;
               }

               i++;
            }
         }
      }

      return !itemstack.isEmpty() && i > 0;
   }

   public ItemStack assemble(CraftingInput p_345803_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for (int j = 0; j < p_345803_.size(); j++) {
         ItemStack itemstack1 = p_345803_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.is(ModItems.william.get())) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                  return ItemStack.EMPTY;
               }

               i++;
            }
         }
      }

      WrittenBookContent writtenbookcontent = (WrittenBookContent)itemstack.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (!itemstack.isEmpty() && i >= 1 && writtenbookcontent != null) {
         WrittenBookContent writtenbookcontent1 = writtenbookcontent.craftCopy();
         ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
         itemstack2.set(DataComponents.WRITTEN_BOOK_CONTENT, writtenbookcontent1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput p_344763_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_344763_.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack itemstack = p_344763_.getItem(i);
         ItemStackTemplate remainder = itemstack.getCraftingRemainder();
         if (remainder != null) {
            nonnulllist.set(i, remainder.create());
         } else if (itemstack.getItem() instanceof WrittenBookItem) {
            nonnulllist.set(i, itemstack.copyWithCount(1));
            break;
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<? extends CustomRecipe> getSerializer() {
      return MahouRecipes.WILLIAM_CLONING_RECIPE.get();
   }

   public RecipeType<CraftingRecipe> getType() {
      return RecipeType.CRAFTING;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= 3 && height >= 3;
   }
}
