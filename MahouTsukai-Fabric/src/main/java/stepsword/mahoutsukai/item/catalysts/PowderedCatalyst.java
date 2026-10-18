package stepsword.mahoutsukai.item.catalysts;

import java.awt.Color;
import stepsword.mahoutsukai.item.ItemBase;

public class PowderedCatalyst extends ItemBase {
   public Color COLOR;
   private String catalystName;

   public PowderedCatalyst(String catalyst) {
      super("powdered_" + catalyst);
      this.catalystName = catalyst;
   }

   public String getCatalystName() {
      return this.catalystName;
   }
}
