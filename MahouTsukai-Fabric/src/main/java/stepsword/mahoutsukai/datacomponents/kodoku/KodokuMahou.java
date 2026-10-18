package stepsword.mahoutsukai.datacomponents.kodoku;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class KodokuMahou implements CopyComponent<KodokuMahou> {
   private int kodoku = 0;

   public KodokuMahou() {
   }

   public KodokuMahou(int k) {
      this.kodoku = k;
   }

   public int getKodoku() {
      return this.kodoku;
   }

   public void setKodoku(int a) {
      this.kodoku = a;
   }

   public KodokuMahou copy() {
      return new KodokuMahou(this.kodoku);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.kodoku);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof KodokuMahou b ? b.kodoku == this.kodoku : false;
   }
}
