package stepsword.mahoutsukai.integration.jei;

import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public class MahoujinJeiIngredientRenderer implements IIngredientRenderer<MahoujinJeiIngredient> {
   public void render(GuiGraphicsExtractor graphics, MahoujinJeiIngredient ingredient) {
      graphics.fakeItem(ingredient.createRenderStack(), 0, 0);
   }

   public List<Component> getTooltip(MahoujinJeiIngredient ingredient, TooltipFlag tooltipFlag) {
      return List.of(ingredient.getName());
   }

   public int getWidth() {
      return 16;
   }

   public int getHeight() {
      return 16;
   }
}
