package stepsword.mahoutsukai.render.gui.guide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import stepsword.mahoutsukai.item.guide.ContentsPage;
import stepsword.mahoutsukai.item.guide.GuidebookInstance;

public class ContentsPageGui extends PageGui {
   ContentsPage contents;

   public ContentsPageGui(GuidebookInstance instance, ContentsPage page) {
      super(instance);
      this.contents = page;
      int index = instance.getContentsCount();

      for (String s : this.contents.pageNames) {
         int width = Minecraft.getInstance().font.width(s);
         int height = (int)(9.0 * 1.5);
         this.addExtraButton(new LinkGuiButton(0, 0, width, height, s, page.start + index++, this, this.xSize / 2 - 20));
      }
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      int x2 = i + 20;
      int y2 = j + 28;
      int x = i + this.xSize / 2 + 10;
      int y = j + 28;

      for (AbstractWidget b : this.buttonList) {
         if (b instanceof LinkGuiButton) {
            if (((LinkGuiButton)b).pageLink < this.contents.start + (this.contents.end - this.contents.start) / 2 + this.guidebook.getContentsCount()) {
               b.setX(x2);
               b.setY(y2);
               y2 += b.getHeight();
            } else {
               b.setX(x);
               b.setY(y);
               y += b.getHeight();
            }
         }
      }

      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   protected void actionPerformed(AbstractWidget button) {
      if (button instanceof LinkGuiButton) {
         this.navigateToPage(((LinkGuiButton)button).pageLink);
      }

      super.actionPerformed(button);
   }
}
