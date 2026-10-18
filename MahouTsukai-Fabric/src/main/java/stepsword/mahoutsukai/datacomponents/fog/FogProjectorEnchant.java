package stepsword.mahoutsukai.datacomponents.fog;

import java.util.ArrayList;
import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;
import stepsword.mahoutsukai.util.FogProjectorValues;

public class FogProjectorEnchant implements CopyComponent<FogProjectorEnchant> {
   FogProjectorValues pv = new FogProjectorValues();

   public FogProjectorEnchant() {
   }

   public FogProjectorEnchant(FogProjectorValues p) {
      this.pv = p;
   }

   public FogProjectorValues getPv() {
      return this.pv;
   }

   public void setPv(FogProjectorValues pv) {
      this.pv = pv;
   }

   public FogProjectorEnchant copy() {
      return this.pv == null
         ? new FogProjectorEnchant(null)
         : new FogProjectorEnchant(
            new FogProjectorValues(new ArrayList<>(this.pv.getAllInts()), new ArrayList<>(this.pv.getAllFloats()), new ArrayList<>(this.pv.getAllBools()))
         );
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.pv);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof FogProjectorEnchant b) ? false : b.pv == this.pv || b.pv.equals(this.pv);
   }
}
