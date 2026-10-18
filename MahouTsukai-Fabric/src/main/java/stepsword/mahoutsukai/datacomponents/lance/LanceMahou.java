package stepsword.mahoutsukai.datacomponents.lance;

import java.util.Objects;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class LanceMahou implements CopyComponent<LanceMahou> {
   private float attackDamage = 3.0F;
   private float weight = 2.0F;

   public LanceMahou() {
   }

   public LanceMahou(float a, float w) {
      this.attackDamage = a;
      this.weight = w;
   }

   public float getWeight() {
      return this.weight;
   }

   public void setWeight(float a) {
      this.weight = Math.max(1.0F, a);
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public void setAttackDamage(float a) {
      this.attackDamage = Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, a);
   }

   public LanceMahou copy() {
      return new LanceMahou(this.attackDamage, this.weight);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.attackDamage, this.weight);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof LanceMahou b) ? false : Float.compare(b.weight, this.weight) == 0 && Float.compare(b.attackDamage, this.attackDamage) == 0;
   }
}
