package stepsword.mahoutsukai.datacomponents.mysticcode;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class MysticCodeData implements CopyComponent<MysticCodeData> {
   int slot = 0;
   int color = 0;
   String slot1 = "SLOT_EMPTY";
   String slot2 = "SLOT_EMPTY";
   String slot3 = "SLOT_EMPTY";

   public MysticCodeData() {
   }

   public MysticCodeData(int i, int i2, String s1, String s2, String s3) {
      this.slot = i;
      this.color = i2;
      this.slot1 = s1;
      this.slot2 = s2;
      this.slot3 = s3;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int i) {
      this.slot = i;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int i) {
      this.color = i;
   }

   public String getSlot1() {
      return this.slot1;
   }

   public void setSlot1(String s) {
      this.slot1 = s;
   }

   public String getSlot2() {
      return this.slot2;
   }

   public void setSlot2(String s) {
      this.slot2 = s;
   }

   public String getSlot3() {
      return this.slot3;
   }

   public void setSlot3(String s) {
      this.slot3 = s;
   }

   public MysticCodeData copy() {
      return new MysticCodeData(this.slot, this.color, this.slot1, this.slot2, this.slot3);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.slot, this.color, this.slot1, this.slot2, this.slot3);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof MysticCodeData b)
         ? false
         : b.slot == this.slot
            && b.color == this.color
            && (b.slot1 == this.slot1 || b.slot1.equals(this.slot1))
            && (b.slot2 == this.slot2 || b.slot2.equals(this.slot2))
            && (b.slot3 == this.slot3 || b.slot3.equals(this.slot3));
   }
}
