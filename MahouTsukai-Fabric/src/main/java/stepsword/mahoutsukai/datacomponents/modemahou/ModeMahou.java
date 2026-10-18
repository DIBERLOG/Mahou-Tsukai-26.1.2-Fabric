package stepsword.mahoutsukai.datacomponents.modemahou;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class ModeMahou implements CopyComponent<ModeMahou> {
   public int mode = 0;

   public ModeMahou() {
   }

   public ModeMahou(int i) {
      this.mode = i;
   }

   public int getMode() {
      return this.mode;
   }

   public void setMode(int mode) {
      this.mode = mode;
   }

   public ModeMahou copy() {
      return new ModeMahou(this.mode);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.mode);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ModeMahou b ? b.mode == this.mode : false;
   }
}
