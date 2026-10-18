package stepsword.mahoutsukai.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.block.ModBlocks;

public class MahouCircleJeiCategory implements IRecipeCategory<MahouCircleJeiRecipe> {
   private final IDrawable icon;

   public MahouCircleJeiCategory(IDrawable icon) {
      this.icon = icon;
   }

   public IRecipeType<MahouCircleJeiRecipe> getRecipeType() {
      return MahouJeiPlugin.MAHOUJIN_RECIPES;
   }

   public Component getTitle() {
      return Component.translatable("jei.category.mahoutsukai.mahoujin");
   }

   public int getWidth() {
      return 134;
   }

   public int getHeight() {
      return 70;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, MahouCircleJeiRecipe recipe, IFocusGroup focuses) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 26).add(ModBlocks.mahoujin.get().asItem())).setStandardSlotBackground();

      for (int i = 0; i < recipe.catalysts().size(); i++) {
         ((IRecipeSlotBuilder)builder.addInputSlot(58, i * 26).add(recipe.catalysts().get(i))).setStandardSlotBackground();
      }

      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 26).add(MahouJeiPlugin.MAHOUJIN_INGREDIENT, recipe.output()))
         .setOutputSlotBackground();
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, MahouCircleJeiRecipe recipe, IFocusGroup focuses) {
      builder.addRecipeArrowWidget().setPosition(26, 26);
      builder.addRecipeArrowWidget().setPosition(84, 26);
   }

   public Identifier getIdentifier(MahouCircleJeiRecipe recipe) {
      return recipe.id();
   }
}
