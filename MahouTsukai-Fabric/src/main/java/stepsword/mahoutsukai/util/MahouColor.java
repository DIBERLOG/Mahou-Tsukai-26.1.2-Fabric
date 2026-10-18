package stepsword.mahoutsukai.util;

public class MahouColor {
   public float r;
   public float g;
   public float b;

   public MahouColor() {
      this.r = 0.0F;
      this.g = 0.0F;
      this.b = 0.0F;
   }

   public MahouColor(float r, float g, float b) {
      this.r = r;
      this.g = g;
      this.b = b;
   }

   @Override
   public int hashCode() {
      return (int)(13.0F * this.r + 11.0F * this.g + 7.0F * this.b);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof MahouColor c) ? false : Float.compare(c.r, this.r) == 0 && Float.compare(c.g, this.g) == 0 && Float.compare(c.b, this.b) == 0;
   }
}
