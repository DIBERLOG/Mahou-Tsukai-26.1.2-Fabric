package stepsword.mahoutsukai.datacomponents.fiction;

import java.util.Objects;
import java.util.UUID;
import stepsword.mahoutsukai.datacomponents.CopyComponent;
import stepsword.mahoutsukai.fabric.MahouIds;

public class FictionMahou implements CopyComponent<FictionMahou> {
   public int fame = 0;
   public int style = 0;
   public UUID caster = MahouIds.FAE_UUID;

   public FictionMahou() {
   }

   public FictionMahou(int f, int s, UUID uuid) {
      this.fame = f;
      this.style = s;
      this.caster = uuid;
   }

   public int getFame() {
      return this.fame;
   }

   public int getStyle() {
      return this.style;
   }

   public void setFame(int f) {
      this.fame = f;
   }

   public void setStyle(int s) {
      this.style = s;
   }

   public UUID getCaster() {
      return this.caster;
   }

   public void setCaster(UUID uuid) {
      this.caster = uuid;
   }

   public FictionMahou copy() {
      return new FictionMahou(this.fame, this.style, this.caster);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.fame, this.style, this.caster);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof FictionMahou b)
         ? false
         : Float.compare(b.fame, this.fame) == 0 && Double.compare(b.style, this.style) == 0 && java.util.Objects.equals(b.caster,this.caster);
   }
}
