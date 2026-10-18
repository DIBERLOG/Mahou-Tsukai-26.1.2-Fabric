package stepsword.mahoutsukai.render.gui;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.util.Utils;

public class RenderPowerConsolidationInnateCap {
   public static void innateCapTooltip(ItemStack stack, List<Component> tooltip) {
      if (!stack.isEmpty() && (stack.getItem() instanceof Caliburn || stack.getItem() instanceof Morgan)) {
         SwordMahou mahou = Utils.getSwordMahou(stack);
         if (mahou != null) {
            MutableComponent innateComponent = Component.translatable("mahoutsukai.caliburn.innate_cap");
            String innate = innateComponent.getString();
            Component text = Component.literal(ChatFormatting.GREEN + innate + ": " + String.format("%.1f", mahou.getInnateCap()));
            tooltip.add(text);
         }
      }
   }
}
