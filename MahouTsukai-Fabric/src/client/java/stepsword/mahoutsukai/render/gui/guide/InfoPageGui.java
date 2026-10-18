package stepsword.mahoutsukai.render.gui.guide;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.item.guide.GuidebookInstance;
import stepsword.mahoutsukai.item.guide.InfoPage;

public class InfoPageGui extends PageGui {
   InfoPage page;

   public InfoPageGui(GuidebookInstance guidebook, InfoPage page) {
      super(guidebook);
      this.page = page;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      int m = (this.width - this.xSize) / 2;
      int n = (this.height - this.ySize) / 2;
      DoubleInfoPageGui.drawWordWrap(
         this.font, graphics, Component.literal(ChatFormatting.BOLD + this.page.getTitle()), m + this.xSize / 2 + 10, n + 10, this.xSize / 2 - 40, 0
      );
      DoubleInfoPageGui.drawWordWrap(
         this.font, graphics, Component.literal(this.page.getDescription()), m + this.xSize / 2 + 10, n + 30, this.xSize / 2 - 40, 1118481
      );
   }
}
