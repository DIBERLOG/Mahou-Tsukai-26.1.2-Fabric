package stepsword.mahoutsukai.datacomponents.strengthening;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class StrengtheningMahou implements CopyComponent<StrengtheningMahou> {
   public boolean unbreakable = false;
   public int uses = 0;

   public StrengtheningMahou() {
   }

   public StrengtheningMahou(boolean b, int u) {
      this.unbreakable = b;
      this.uses = u;
   }

   public boolean isUnbreakable() {
      return this.unbreakable;
   }

   public void setUnbreakable(boolean u) {
      this.unbreakable = u;
   }

   public int getUses() {
      return this.uses;
   }

   public void setUses(int uses) {
      this.uses = uses;
   }

   public StrengtheningMahou copy() {
      return new StrengtheningMahou(this.unbreakable, this.uses);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.unbreakable, this.uses);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof StrengtheningMahou b) ? false : b.unbreakable == this.unbreakable && b.uses == this.uses;
   }
}
