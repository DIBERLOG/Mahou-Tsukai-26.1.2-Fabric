package stepsword.mahoutsukai.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.rendertype.RenderType;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;

public class RingGeometry extends MahouGeometry {
   public double starting_y = 0.0;
   public float flatness = 90.0F;
   public float radius;
   public float height;
   public int num_segments = 48;

   public RingGeometry(float radius, float height) {
      this.radius = radius;
      this.height = height;
   }

   public double getStartingY() {
      return this.starting_y;
   }

   public RingGeometry setStartingY(double starting_y) {
      this.starting_y = starting_y;
      return this;
   }

   public float getFlatness() {
      return this.flatness;
   }

   public RingGeometry setFlatness(float flatness) {
      this.flatness = flatness;
      return this;
   }

   public float getRadius() {
      return this.radius;
   }

   public RingGeometry setRadius(float radius) {
      this.radius = radius;
      return this;
   }

   public float getHeight() {
      return this.height;
   }

   public RingGeometry setHeight(float height) {
      this.height = height;
      return this;
   }

   public int getNumSegments() {
      return this.num_segments;
   }

   public RingGeometry setNumSegments(int num_segments) {
      this.num_segments = num_segments;
      return this;
   }

   @Override
   public RenderType getType() {
      return MahoujinRenderType.createScopedRunesRenderType("ring_geometry_default", RenderUtils.runes, 0);
   }

   @Override
   public void vertices(Pose pose, VertexConsumer buffer) {
      double theta = 6.2831852 / this.num_segments;
      double q = this.height * Math.sin(EffectUtil.toRad(this.flatness));
      double p = this.radius + this.height * Math.cos(EffectUtil.toRad(this.flatness));
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      double xb = 0.0;
      double yb = 0.0;
      double zb = 0.0;
      float squeeze = 3.0F;
      float texx = 0.0F;

      for (int i = 0; i < this.num_segments + 1; i++) {
         texx += squeeze / (this.num_segments + 1);
         if (texx >= 1.0F) {
            texx = squeeze / (this.num_segments + 1);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)x, (float)y, (float)z, 0.0F, 0.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, 0.0F, 1.0F, this.lx, this.ly);
         }

         double tt = i * theta;
         x = -this.radius * Math.sin(tt);
         y = this.starting_y;
         z = this.radius * Math.cos(tt);
         xb = -p * Math.sin(tt);
         yb = this.starting_y - q;
         zb = p * Math.cos(tt);
         if (i == 0) {
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)x, (float)y, (float)z, texx, 0.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)x, (float)y, (float)z, texx, 0.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, texx, 1.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, texx, 1.0F, this.lx, this.ly);
         }

         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)x, (float)y, (float)z, texx, 0.0F, this.lx, this.ly);
         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, texx, 1.0F, this.lx, this.ly);
      }

      NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)x, (float)y, (float)z, texx, 0.0F, this.lx, this.ly);
      NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, texx, 1.0F, this.lx, this.ly);
      NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, (float)xb, (float)yb, (float)zb, texx, 1.0F, this.lx, this.ly);
   }
}
