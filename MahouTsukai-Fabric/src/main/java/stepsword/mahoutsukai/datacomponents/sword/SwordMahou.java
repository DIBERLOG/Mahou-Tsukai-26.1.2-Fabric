package stepsword.mahoutsukai.datacomponents.sword;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class SwordMahou implements CopyComponent<SwordMahou> {
   public float attackDamage = 3.0F;
   public double innateCap = 10000.0;

   public SwordMahou() {
   }

   public SwordMahou(float a, double i) {
      this.attackDamage = a;
      this.innateCap = i;
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public void setAttackDamage(float a) {
      this.attackDamage = a;
   }

   public void setInnateCap(double a) {
      this.innateCap = a;
   }

   public double getInnateCap() {
      return this.innateCap;
   }

   public SwordMahou copy() {
      return new SwordMahou(this.attackDamage, this.innateCap);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.innateCap, this.attackDamage);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof SwordMahou b) ? false : Float.compare(b.attackDamage, this.attackDamage) == 0 && Double.compare(b.innateCap, this.innateCap) == 0;
   }
}
