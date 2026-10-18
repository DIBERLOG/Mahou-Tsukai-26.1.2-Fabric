package stepsword.mahoutsukai.datacomponents.projected;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class ProjectedMahou implements CopyComponent<ProjectedMahou> {
   boolean projected = false;

   public ProjectedMahou() {
   }

   public ProjectedMahou(boolean p) {
      this.projected = p;
   }

   public boolean getProjected() {
      return this.projected;
   }

   public void setProjected(boolean p) {
      this.projected = p;
   }

   public ProjectedMahou copy() {
      return new ProjectedMahou(this.projected);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.projected);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ProjectedMahou b ? b.projected == this.projected : false;
   }
}
