package stepsword.mahoutsukai.render.gui;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.item.william.William;
import stepsword.mahoutsukai.util.Utils;

public class RenderWilliamStats {
   public static void fameStyleToolTip(ItemStack stack, List<Component> tooltip) {
      if (!stack.isEmpty() && stack.getItem() instanceof William) {
         FictionMahou mahou = Utils.getFictionMahou(stack);
         if (mahou != null) {
            MutableComponent famec = Component.translatable("mahoutsukai.william.fame");
            MutableComponent stylec = Component.translatable("mahoutsukai.william.style");
            String fame = famec.getString();
            String style = stylec.getString();
            Component text = Component.literal(ChatFormatting.GREEN + fame + ": " + String.format("%d", mahou.getFame()));
            Component text2 = Component.literal(ChatFormatting.GREEN + style + ": " + String.format("%d", mahou.getStyle()));
            tooltip.add(text);
            tooltip.add(text2);
         }
      }
   }
}
