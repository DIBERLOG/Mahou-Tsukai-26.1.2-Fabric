package stepsword.mahoutsukai.datacomponents.itemcolor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;
import stepsword.mahoutsukai.util.MahouColor;

public class ColorMahou implements CopyComponent<ColorMahou> {
   public ArrayList<MahouColor> colors = new ArrayList<>();

   public ColorMahou() {
   }

   public ColorMahou(List<MahouColor> c) {
      this.colors = new ArrayList<>();if(c!=null)for(var color:c)this.colors.add(new MahouColor(color.r,color.g,color.b));
   }

   public ArrayList<MahouColor> getColors() {
      return this.colors;
   }

   public ColorMahou copy() {
      return this.colors == null ? new ColorMahou(null) : new ColorMahou(new ArrayList<>(this.colors));
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.colors);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof ColorMahou b) ? false : this.colors == b.colors || b.colors.equals(this.colors);
   }
}
