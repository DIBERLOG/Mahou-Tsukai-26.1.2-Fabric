package stepsword.mahoutsukai.integration.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class EntityJeiIngredientHelper implements IIngredientHelper<EntityJeiIngredient> {
   private final IIngredientType<EntityJeiIngredient> type;

   public EntityJeiIngredientHelper(IIngredientType<EntityJeiIngredient> type) {
      this.type = type;
   }

   public IIngredientType<EntityJeiIngredient> getIngredientType() {
      return this.type;
   }

   public String getDisplayName(EntityJeiIngredient ingredient) {
      return ingredient.getName().getString();
   }

   public Object getUid(EntityJeiIngredient ingredient, UidContext context) {
      return ingredient.id();
   }

   public String getDisplayModId(EntityJeiIngredient ingredient) {
      return ingredient.entityTypeId().getNamespace();
   }

   public Identifier getIdentifier(EntityJeiIngredient ingredient) {
      return ingredient.id();
   }

   public ItemStack getCheatItemStack(EntityJeiIngredient ingredient) {
      return ItemStack.EMPTY;
   }

   public EntityJeiIngredient copyIngredient(EntityJeiIngredient ingredient) {
      return ingredient;
   }

   public String getErrorInfo(EntityJeiIngredient ingredient) {
      return ingredient.id().toString();
   }
}
