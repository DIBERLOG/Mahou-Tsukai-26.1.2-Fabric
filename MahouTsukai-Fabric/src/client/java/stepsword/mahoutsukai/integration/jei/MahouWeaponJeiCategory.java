package stepsword.mahoutsukai.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MahouWeaponJeiCategory implements IRecipeCategory<MahouWeaponJeiRecipe> {
   private final IDrawable icon;

   public MahouWeaponJeiCategory(IDrawable icon) {
      this.icon = icon;
   }

   public IRecipeType<MahouWeaponJeiRecipe> getRecipeType() {
      return MahouJeiPlugin.WEAPON_RECIPES;
   }

   public Component getTitle() {
      return Component.translatable("jei.category.mahoutsukai.weapons");
   }

   public int getWidth() {
      return 78;
   }

   public int getHeight() {
      return 70;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, MahouWeaponJeiRecipe recipe, IFocusGroup focuses) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 26).add(recipe.scroll())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(58, 26).add(recipe.weapon())).setOutputSlotBackground();
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, MahouWeaponJeiRecipe recipe, IFocusGroup focuses) {
      builder.addRecipeArrowWidget().setPosition(26, 26);
   }

   public Identifier getIdentifier(MahouWeaponJeiRecipe recipe) {
      return recipe.id();
   }
}
