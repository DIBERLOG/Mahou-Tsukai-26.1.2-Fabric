package stepsword.mahoutsukai.render.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

final class ProjectorScreenLabels {
   private static final int LABEL_COLOR = 14737632;
   private static final int LABEL_X_OFFSET = -1;

   private ProjectorScreenLabels() {
   }

   static List<StringWidget> createLabels(Font font, List<MahouEditBox> boxes) {
      List<StringWidget> labels = new ArrayList<>(boxes.size());

      for (MahouEditBox box : boxes) {
         Component message = box.getMessage().copy().withStyle(style -> style.withColor(LABEL_COLOR));
         int textWidth = font.width(message.getVisualOrderText());
         StringWidget label = new StringWidget(box.getX() + -1 - textWidth, box.getY(), textWidth, box.getHeight(), message, font);
         labels.add(label);
      }

      return labels;
   }
}
