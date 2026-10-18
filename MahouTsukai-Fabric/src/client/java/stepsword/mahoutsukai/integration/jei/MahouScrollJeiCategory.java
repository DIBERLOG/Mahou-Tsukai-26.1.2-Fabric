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

public class MahouScrollJeiCategory implements IRecipeCategory<MahouScrollJeiRecipe> {
   private final IDrawable icon;

   public MahouScrollJeiCategory(IDrawable icon) {
      this.icon = icon;
   }

   public IRecipeType<MahouScrollJeiRecipe> getRecipeType() {
      return MahouJeiPlugin.SCROLL_RECIPES;
   }

   public Component getTitle() {
      return Component.translatable("jei.category.mahoutsukai.scrolls");
   }

   public int getWidth() {
      return 250;
   }

   public int getHeight() {
      return 120;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, MahouScrollJeiRecipe recipe, IFocusGroup focuses) {
      switch (recipe) {
         case ScrollFromMahoujinJeiRecipe normal:
            setNormalRecipe(builder, normal);
            break;
         case CaliburnJeiRecipe caliburn:
            setCaliburnRecipe(builder, caliburn);
            break;
         case MorganJeiRecipe morgan:
            setMorganRecipe(builder, morgan);
            break;
         case ReplicaJeiRecipe replica:
            setReplicaRecipe(builder, replica);
            break;
         case ClarentJeiRecipe clarent:
            setClarentRecipe(builder, clarent);
            break;
         case EmrysJeiRecipe emrys:
            setEmrysRecipe(builder, emrys);
            break;
         default:
            throw new MatchException(null, null);
      }
   }

   private static void setNormalRecipe(IRecipeLayoutBuilder builder, ScrollFromMahoujinJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 26).add(ModBlocks.spellClothBlock.get().asItem())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addInputSlot(58, 26).add(ModBlocks.mahoujin.get().asItem())).setStandardSlotBackground();

      for (int i = 0; i < recipe.catalysts().size(); i++) {
         ((IRecipeSlotBuilder)builder.addInputSlot(116, i * 26).add(recipe.catalysts().get(i))).setStandardSlotBackground();
      }

      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 174, 26).add(MahouJeiPlugin.MAHOUJIN_INGREDIENT, recipe.mahoujin()))
         .setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(232, 26).add(recipe.output())).setOutputSlotBackground();
   }

   private static void setCaliburnRecipe(IRecipeLayoutBuilder builder, CaliburnJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 0).addItemStacks(recipe.swords())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 0, 26).add(MahouJeiPlugin.MAHOUJIN_INGREDIENT, recipe.powerConsolidation()))
         .setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 52).add(recipe.enchantedBook())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(58, 26).add(recipe.output())).setOutputSlotBackground();
   }

   private static void setMorganRecipe(IRecipeLayoutBuilder builder, MorganJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 0).add(recipe.caliburn())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 0, 26).addIngredients(MahouJeiPlugin.ENTITY_INGREDIENT, recipe.killableEntities()))
         .setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(58, 26).add(recipe.output())).setOutputSlotBackground();
   }

   private static void setReplicaRecipe(IRecipeLayoutBuilder builder, ReplicaJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 0).add(recipe.inputs().get(0))).setStandardSlotBackground();

      for (int i = 1; i < recipe.inputs().size(); i++) {
         ((IRecipeSlotBuilder)builder.addInputSlot(58, (i - 1) * 26).add(recipe.inputs().get(i))).setStandardSlotBackground();
      }

      ((IRecipeSlotBuilder)builder.addOutputSlot(116, 39).add(recipe.output())).setOutputSlotBackground();
   }

   private static void setClarentRecipe(IRecipeLayoutBuilder builder, ClarentJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 0).add(recipe.strengtheningScroll())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addInputSlot(58, 0).addItemStacks(recipe.swords())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 58, 26).add(MahouJeiPlugin.ENTITY_INGREDIENT, recipe.dragonBreath()))
         .setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(116, 13).add(recipe.output())).setOutputSlotBackground();
   }

   private static void setEmrysRecipe(IRecipeLayoutBuilder builder, EmrysJeiRecipe recipe) {
      ((IRecipeSlotBuilder)builder.addInputSlot(0, 0).add(recipe.strengtheningScroll())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addInputSlot(58, 0).add(recipe.stick())).setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 58, 26).add(MahouJeiPlugin.ENTITY_INGREDIENT, recipe.lightning()))
         .setStandardSlotBackground();
      ((IRecipeSlotBuilder)builder.addOutputSlot(116, 13).add(recipe.output())).setOutputSlotBackground();
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, MahouScrollJeiRecipe recipe, IFocusGroup focuses) {
      if (recipe instanceof ScrollFromMahoujinJeiRecipe) {
         arrow(builder, 26, 26);
         arrow(builder, 84, 26);
         arrow(builder, 142, 26);
         arrow(builder, 200, 26);
      } else if (recipe instanceof ReplicaJeiRecipe) {
         arrow(builder, 26, 0);
         arrow(builder, 84, 39);
      } else if (recipe instanceof ClarentJeiRecipe) {
         arrow(builder, 26, 0);
         arrow(builder, 84, 13);
         builder.addText(Component.literal("Right-click with a strengthened sword while standing in dragon's breath"), 242, 38).setPosition(4, 78);
      } else if (recipe instanceof EmrysJeiRecipe) {
         arrow(builder, 26, 0);
         arrow(builder, 84, 13);
      } else {
         arrow(builder, 26, 26);
      }
   }

   private static void arrow(IRecipeExtrasBuilder builder, int x, int y) {
      builder.addRecipeArrowWidget().setPosition(x, y);
   }

   public Identifier getIdentifier(MahouScrollJeiRecipe recipe) {
      return recipe.id();
   }
}
