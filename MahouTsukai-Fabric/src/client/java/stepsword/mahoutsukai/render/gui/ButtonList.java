package stepsword.mahoutsukai.render.gui;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ButtonList extends ObjectSelectionList<ButtonList.ButtonListEntry> {
   public ButtonList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int itemHeightIn) {
      super(mcIn, widthIn, Math.max(1, heightIn - topIn - 8), topIn, itemHeightIn);
   }

   public int addEntry(ButtonList.ButtonListEntry entry) {
      return super.addEntry(entry);
   }

   public class ButtonListEntry extends Entry<ButtonList.ButtonListEntry> {
      Button button;

      public ButtonListEntry(Button b) {
         this.button = b;
      }

      public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
         this.placeButton();
         return this.button.mouseClicked(event, doubleClick) || super.mouseClicked(event, doubleClick);
      }

      public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
         this.placeButton();
         this.button.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      }

      public void visitWidgets(Consumer<AbstractWidget> widgetVisitor) {
         widgetVisitor.accept(this.button);
      }

      public Component getNarration() {
         return this.button.getMessage();
      }

      private void placeButton() {
         this.button.setRectangle(this.getContentWidth(), this.getContentHeight(), this.getContentX(), this.getContentY());
      }
   }
}
