package stepsword.mahoutsukai.render.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;

final class ProjectorScreenScrollLayout {
   private static final int SCROLL_STEP = 16;
   private static final int SCROLLBAR_WIDTH = 6;
   private static final int SCROLLBAR_MARGIN = 6;
   private static final int SCROLLBAR_COLOR = -8750470;
   private static final int SCROLLBAR_TRACK_COLOR = -2145246686;
   private final List<AbstractWidget> widgets = new ArrayList<>();
   private final List<Integer> baseY = new ArrayList<>();
   private int viewportTop;
   private int viewportBottom;
   private int contentBottom;
   private int scrollOffset;

   void register(AbstractWidget widget, int originalY) {
      this.widgets.add(widget);
      this.baseY.add(originalY);
      this.contentBottom = Math.max(this.contentBottom, originalY + widget.getHeight());
   }

   void setViewport(int viewportTop, int viewportBottom) {
      this.viewportTop = viewportTop;
      this.viewportBottom = viewportBottom;
   }

   void reset() {
      this.widgets.clear();
      this.baseY.clear();
      this.contentBottom = 0;
      this.scrollOffset = 0;
   }

   boolean scrollBy(double deltaY) {
      int previous = this.scrollOffset;
      this.scrollOffset = this.clampScroll(this.scrollOffset - (int)Math.signum(deltaY) * 16);
      return previous != this.scrollOffset;
   }

   boolean needsScrollbar() {
      return this.maxScroll() > 0;
   }

   void apply() {
      for (int i = 0; i < this.widgets.size(); i++) {
         AbstractWidget widget = this.widgets.get(i);
         int y = this.baseY.get(i) - this.scrollOffset;
         widget.setY(y);
         widget.visible = y + widget.getHeight() >= this.viewportTop && y <= this.viewportBottom;
      }
   }

   void drawScrollbar(GuiGraphicsExtractor graphics, int screenWidth) {
      if (this.needsScrollbar()) {
         int trackLeft = screenWidth - 6 - 6;
         int trackRight = screenWidth - 6;
         int trackTop = this.viewportTop;
         int trackBottom = this.viewportBottom;
         graphics.fill(trackLeft, trackTop, trackRight, trackBottom, -2145246686);
         int viewportHeight = Math.max(1, this.viewportBottom - this.viewportTop);
         int thumbHeight = Math.max(12, (int)((double)viewportHeight * viewportHeight / Math.max(viewportHeight, this.contentBottom)));
         int travel = Math.max(1, viewportHeight - thumbHeight);
         int thumbTop = trackTop + (int)(travel * ((double)this.scrollOffset / this.maxScroll()));
         graphics.fill(trackLeft, thumbTop, trackRight, thumbTop + thumbHeight, -8750470);
      }
   }

   private int maxScroll() {
      return Math.max(0, this.contentBottom - this.viewportBottom);
   }

   private int clampScroll(int value) {
      return Math.max(0, Math.min(value, this.maxScroll()));
   }
}
