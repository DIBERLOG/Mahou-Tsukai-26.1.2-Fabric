package stepsword.mahoutsukai.render.gui;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.rulebreaker.RuleBreaker;

public class RenderRuleBreakerModulus {
   public static void modulusTooltip(ItemStack stack, List<Component> tooltip) {
      if (!stack.isEmpty() && stack.getItem() instanceof RuleBreaker) {
         MutableComponent dc = Component.translatable("mahoutsukai.rulebreaker.dividend");
         String d = dc.getString();
         MutableComponent mc = Component.translatable("mahoutsukai.rulebreaker.modulus");
         String m = mc.getString();
         Component text1 = Component.literal(ChatFormatting.GREEN + d + ": " + String.format("%d", RuleBreaker.getDesignation(stack)));
         Component text2 = Component.literal(ChatFormatting.AQUA + m + ": " + String.format("%d", RuleBreaker.getModulus(stack)));
         tooltip.add(text1);
         tooltip.add(text2);
      }
   }
}
