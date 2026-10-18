package stepsword.mahoutsukai.render.gui;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;

public class RenderStrengthening {
   public static void strengthenTooltip(ItemStack stack, List<Component> tooltip) {
      if (!stack.isEmpty() && StrengtheningSpellEffect.getStrengthened(stack) > 0) {
         MutableComponent mc = Component.translatable("mahoutsukai.strengthened");
         String m = mc.getString();
         Component text = Component.literal(ChatFormatting.GREEN + m);
         tooltip.add(text);
      }
   }
}
