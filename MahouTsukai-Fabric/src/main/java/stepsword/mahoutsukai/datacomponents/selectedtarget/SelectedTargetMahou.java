package stepsword.mahoutsukai.datacomponents.selectedtarget;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class SelectedTargetMahou implements CopyComponent<SelectedTargetMahou> {
   public int targetId = -1;

   public SelectedTargetMahou() {
   }

   public SelectedTargetMahou(int t) {
      this.targetId = t;
   }

   public int getTargetId() {
      return this.targetId;
   }

   public void setTargetId(int t) {
      this.targetId = t;
   }

   public SelectedTargetMahou copy() {
      return new SelectedTargetMahou(this.targetId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.targetId);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof SelectedTargetMahou b ? this.targetId == b.targetId : false;
   }
}
