package stepsword.mahoutsukai.integration.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.block.ModBlocks;

public class MahoujinJeiIngredientHelper implements IIngredientHelper<MahoujinJeiIngredient> {
   private final IIngredientType<MahoujinJeiIngredient> type;

   public MahoujinJeiIngredientHelper(IIngredientType<MahoujinJeiIngredient> type) {
      this.type = type;
   }

   public IIngredientType<MahoujinJeiIngredient> getIngredientType() {
      return this.type;
   }

   public String getDisplayName(MahoujinJeiIngredient ingredient) {
      return ingredient.getName().getString();
   }

   public Object getUid(MahoujinJeiIngredient ingredient, UidContext context) {
      return ingredient.id();
   }

   public String getDisplayModId(MahoujinJeiIngredient ingredient) {
      return "mahoutsukai";
   }

   public Identifier getIdentifier(MahoujinJeiIngredient ingredient) {
      return ingredient.id();
   }

   public ItemStack getCheatItemStack(MahoujinJeiIngredient ingredient) {
      return new ItemStack(ModBlocks.mahoujin.get().asItem());
   }

   public MahoujinJeiIngredient copyIngredient(MahoujinJeiIngredient ingredient) {
      return ingredient;
   }

   public String getErrorInfo(MahoujinJeiIngredient ingredient) {
      return ingredient.id().toString();
   }
}
