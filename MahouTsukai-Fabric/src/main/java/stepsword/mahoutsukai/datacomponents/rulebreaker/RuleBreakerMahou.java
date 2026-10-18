package stepsword.mahoutsukai.datacomponents.rulebreaker;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class RuleBreakerMahou implements CopyComponent<RuleBreakerMahou> {
   public int modulus = 2;
   public int designation = 0;

   public RuleBreakerMahou() {
   }

   public RuleBreakerMahou(int m, int d) {
      this.modulus = m;
      this.designation = d;
   }

   public int getDesignation() {
      return this.designation;
   }

   public int getModulus() {
      return this.modulus;
   }

   public void setDesignation(int designation) {
      this.designation = designation;
   }

   public void setModulus(int modulus) {
      this.modulus = modulus;
   }

   public RuleBreakerMahou copy() {
      return new RuleBreakerMahou(this.modulus, this.designation);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.designation, this.modulus);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof RuleBreakerMahou b) ? false : b.designation == this.designation && b.modulus == this.modulus;
   }
}
