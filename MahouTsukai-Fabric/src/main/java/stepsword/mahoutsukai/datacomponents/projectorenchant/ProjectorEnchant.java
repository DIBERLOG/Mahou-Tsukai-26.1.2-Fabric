package stepsword.mahoutsukai.datacomponents.projectorenchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import stepsword.mahoutsukai.datacomponents.CopyComponent;
import stepsword.mahoutsukai.render.enchant.ProjectorValues;

public class ProjectorEnchant implements CopyComponent<ProjectorEnchant> {
   List<ProjectorValues> pv = new ArrayList<>();

   public ProjectorEnchant() {
   }

   public ProjectorEnchant(List<ProjectorValues> p) {
      this.pv = p;
   }

   public List<ProjectorValues> getPv() {
      return this.pv;
   }

   public void setPv(List<ProjectorValues> pv) {
      this.pv = pv;
   }

   public ProjectorEnchant copy() {
      ArrayList<ProjectorValues> list = new ArrayList<>();
      if (this.pv != null) {
         for (int m = 0; m < this.pv.size(); m++) {
            ProjectorValues v = this.pv.get(m);
            ProjectorValues p = new ProjectorValues(v.getAllInts(), v.getAllFloats(), v.getAllBools());
            p.read(v.write(new CompoundTag()));
            list.add(p);
         }
      }

      return new ProjectorEnchant(list);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.pv);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof ProjectorEnchant b) ? false : b.pv == this.pv || b.pv.equals(this.pv);
   }
}
