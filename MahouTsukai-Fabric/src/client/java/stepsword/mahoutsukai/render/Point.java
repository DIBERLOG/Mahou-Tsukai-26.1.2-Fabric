package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import org.joml.Matrix4f;

public class Point {
   public float x = 0.0F;
   public float y = 0.0F;
   public float z = 0.0F;

   public Point() {
   }

   public Point(float xx, float yy, float zz) {
      this.x = xx;
      this.y = yy;
      this.z = zz;
   }

   public Point clone() {
      return new Point(this.x, this.y, this.z);
   }

   public static Point point() {
      return new Point();
   }

   public Point shiftX(float s) {
      this.x += s;
      return this;
   }

   public Point shiftY(float s) {
      this.y += s;
      return this;
   }

   public Point shiftZ(float s) {
      this.z += s;
      return this;
   }

   public Point shiftX(double s) {
      this.x = (float)(this.x + s);
      return this;
   }

   public Point shiftY(double s) {
      this.y = (float)(this.y + s);
      return this;
   }

   public Point shiftZ(double s) {
      this.z = (float)(this.z + s);
      return this;
   }

   public Point partial(Point last, float partial) {
      return new Point(last.x + (this.x - last.x) * partial, last.y + (this.y - last.y) * partial, last.z + (this.z - last.z) * partial);
   }

   public static VertexConsumer addPointAt(VertexConsumer buf, Matrix4f m, ArrayList<Point> ps, int n, float[] rgba) {
      return buf.addVertex(m, ps.get(n).x, ps.get(n).y, ps.get(n).z).setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
   }
}
