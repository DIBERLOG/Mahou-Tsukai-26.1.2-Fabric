package stepsword.mahoutsukai.render.gui.guide;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import stepsword.mahoutsukai.item.guide.DoubleInfoPage;
import stepsword.mahoutsukai.item.guide.GuidebookInstance;

public class DoubleInfoPageGui extends PageGui {
   DoubleInfoPage page;

   public DoubleInfoPageGui(GuidebookInstance guidebook, DoubleInfoPage page) {
      super(guidebook);
      this.page = page;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      int m = (this.width - this.xSize) / 2;
      int n = (this.height - this.ySize) / 2;
      drawWordWrap(this.font, graphics, Component.literal(ChatFormatting.BOLD + this.page.getTitle()), m + 35, n + 10, this.xSize / 2 - 40, 0);
      drawWordWrap(this.font, graphics, Component.literal(this.page.getDescription()), m + 20, n + 30, this.xSize / 2 - 40, 1118481);
      drawWordWrap(this.font, graphics, Component.literal(ChatFormatting.BOLD + this.page.getTitle2()), m + this.xSize / 2 + 10, n + 10, this.xSize / 2 - 40, 0);
      drawWordWrap(this.font, graphics, Component.literal(this.page.getDescription2()), m + this.xSize / 2 + 10, n + 30, this.xSize / 2 - 40, 1118481);
   }

   public static void drawWordWrap(
      Font font, GuiGraphicsExtractor graphics, FormattedText p_275493_, int p_275609_, int p_275274_, int p_275484_, int p_275332_
   ) {
      graphics.textWithWordWrap(font, p_275493_, p_275609_, p_275274_, p_275484_, opaque(p_275332_), false);
   }

   public static int opaque(int color) {
      return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
   }
}
