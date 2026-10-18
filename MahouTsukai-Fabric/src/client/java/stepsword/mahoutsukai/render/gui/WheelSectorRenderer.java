package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

final class WheelSectorRenderer {
   private WheelSectorRenderer() {
   }

   static void fillSector(GuiGraphicsExtractor graphics, int centerX, int centerY, float startAngle, float endAngle, int radius, int color) {
      int radiusSquared = radius * radius;

      for (int dy = -radius; dy <= radius; dy++) {
         int rowExtent = Mth.floor(Mth.sqrt(radiusSquared - dy * dy));
         int spanStart = Integer.MIN_VALUE;

         for (int dx = -rowExtent; dx <= rowExtent; dx++) {
            boolean insideSector = dx == 0 && dy == 0 || isAngleInSector(Mth.atan2(dy, dx), startAngle, endAngle);
            if (insideSector) {
               if (spanStart == Integer.MIN_VALUE) {
                  spanStart = dx;
               }
            } else if (spanStart != Integer.MIN_VALUE) {
               graphics.fill(centerX + spanStart, centerY + dy, centerX + dx, centerY + dy + 1, color);
               spanStart = Integer.MIN_VALUE;
            }
         }

         if (spanStart != Integer.MIN_VALUE) {
            graphics.fill(centerX + spanStart, centerY + dy, centerX + rowExtent + 1, centerY + dy + 1, color);
         }
      }
   }

   private static boolean isAngleInSector(double rawAngle, float startAngle, float endAngle) {
      double angle = (rawAngle + (Math.PI * 2)) % (Math.PI * 2);
      return angle >= startAngle && angle <= endAngle;
   }
}
