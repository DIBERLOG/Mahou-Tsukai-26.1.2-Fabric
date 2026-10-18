package stepsword.mahoutsukai.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;

public class SpiralGeometry extends MahouGeometry {
   private double starting_y = 0.0;
   private float flatness;
   private float radius;
   private float height;
   private int num_segments = 48;
   private float turns = 1.0F;
   private float lengthen = 0.8F;
   private float inner_radius = 0.3F;
   private float texs = 0.0F;
   private float curve_start = 0.0F;
   private float curve_end = 0.0F;
   private int start_degrees = 0;
   private boolean duplicate_caps = true;

   public SpiralGeometry(float flatness, float radius, float height) {
      this.flatness = flatness;
      this.radius = radius;
      this.height = height;
   }

   public double getStartingY() {
      return this.starting_y;
   }

   public SpiralGeometry setStartingY(double starting_y) {
      this.starting_y = starting_y;
      return this;
   }

   public float getFlatness() {
      return this.flatness;
   }

   public SpiralGeometry setFlatness(float flatness) {
      this.flatness = flatness;
      return this;
   }

   public float getRadius() {
      return this.radius;
   }

   public SpiralGeometry setRadius(float radius) {
      this.radius = radius;
      return this;
   }

   public float getHeight() {
      return this.height;
   }

   public SpiralGeometry setHeight(float height) {
      this.height = height;
      return this;
   }

   public int getNumSegments() {
      return this.num_segments;
   }

   public SpiralGeometry setNumSegments(int num_segments) {
      this.num_segments = num_segments;
      return this;
   }

   public float getTurns() {
      return this.turns;
   }

   public SpiralGeometry setTurns(float turns) {
      this.turns = turns;
      return this;
   }

   public float getLengthen() {
      return this.lengthen;
   }

   public SpiralGeometry setLengthen(float lengthen) {
      this.lengthen = lengthen;
      return this;
   }

   public float getInnerRadius() {
      return this.inner_radius;
   }

   public SpiralGeometry setInnerRadius(float inner_radius) {
      this.inner_radius = inner_radius;
      return this;
   }

   public float getTexs() {
      return this.texs;
   }

   public SpiralGeometry setTexs(float texs) {
      this.texs = texs;
      return this;
   }

   public float getCurveStart() {
      return this.curve_start;
   }

   public SpiralGeometry setCurveStart(float curve_start) {
      this.curve_start = curve_start;
      return this;
   }

   public float getCurveEnd() {
      return this.curve_end;
   }

   public SpiralGeometry setCurveEnd(float curve_end) {
      this.curve_end = curve_end;
      return this;
   }

   public int getStartDegrees() {
      return this.start_degrees;
   }

   public SpiralGeometry setStartDegrees(int start_degrees) {
      this.start_degrees = start_degrees;
      return this;
   }

   public boolean isDuplicateCaps() {
      return this.duplicate_caps;
   }

   public SpiralGeometry setDuplicateCaps(boolean duplicate_caps) {
      this.duplicate_caps = duplicate_caps;
      return this;
   }

   @Override
   public void vertices(Pose pose, VertexConsumer buffer) {
      double theta = this.turns * 2.0F * Math.PI / this.num_segments;
      double curveTheta = (this.curve_end - this.curve_start) / this.num_segments;
      float texx = this.texs;
      float squeeze = 3.0F;
      float segPerTex = 0.015625F;
      float lastInnerX = 0.0F;
      float lastInnerY = 0.0F;
      float lastInnerZ = 0.0F;
      float lastOuterX = 0.0F;
      float lastOuterY = 0.0F;
      float lastOuterZ = 0.0F;
      float lastTexX = this.texs;
      boolean curved = this.curve_start != 0.0F || this.curve_end != 0.0F || this.start_degrees != 0 || this.duplicate_caps || this.turns != 1.0F;

      for (int i = 0; i < this.num_segments + 1; i++) {
         double r2 = this.inner_radius + (this.radius - this.inner_radius) * ((float)(this.num_segments - i) / this.num_segments);
         double h2 = this.height * ((float)(this.num_segments - i) / this.num_segments);
         double p = r2 + h2 * Math.cos(EffectUtil.toRad(this.flatness));
         double q = h2 * Math.sin(EffectUtil.toRad(this.flatness));
         double ygrow = this.starting_y + i * this.lengthen;
         if (curved) {
            texx = this.texs + i * segPerTex;
         } else {
            texx += squeeze / (this.num_segments + 1);
            if (texx >= 1.0F) {
               texx = squeeze / (this.num_segments + 1);
               NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, lastInnerX, lastInnerY, lastInnerZ, 0.0F, 0.0F, this.lx, this.ly);
               NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, lastOuterX, lastOuterY, lastOuterZ, 0.0F, 1.0F, this.lx, this.ly);
            }
         }

         double tt = EffectUtil.toRad(this.start_degrees) + i * theta;
         double x = -r2 * Math.sin(tt);
         double y = ygrow;
         double z = r2 * Math.cos(tt);
         double xb = -p * Math.sin(tt);
         double yb = ygrow - q;
         double zb = p * Math.cos(tt);
         double curveAngle = Math.toRadians(-(i + 1.0F) * curveTheta);
         double curveCos = Math.cos(curveAngle);
         double curveSin = Math.sin(curveAngle);
         float innerX = (float)(x * curveCos - y * curveSin);
         float innerY = (float)(x * curveSin + y * curveCos);
         float outerX = (float)(xb * curveCos - yb * curveSin);
         float outerY = (float)(xb * curveSin + yb * curveCos);
         float innerZ = (float)z;
         float outerZ = (float)zb;
         if (this.duplicate_caps && i == 0) {
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, innerX, innerY, innerZ, texx, 0.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, innerX, innerY, innerZ, texx, 0.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, outerX, outerY, outerZ, texx, 1.0F, this.lx, this.ly);
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, outerX, outerY, outerZ, texx, 1.0F, this.lx, this.ly);
         }

         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, innerX, innerY, innerZ, texx, 0.0F, this.lx, this.ly);
         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, outerX, outerY, outerZ, texx, 1.0F, this.lx, this.ly);
         lastInnerX = innerX;
         lastInnerY = innerY;
         lastInnerZ = innerZ;
         lastOuterX = outerX;
         lastOuterY = outerY;
         lastOuterZ = outerZ;
         lastTexX = texx;
      }

      if (this.duplicate_caps) {
         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, lastInnerX, lastInnerY, lastInnerZ, lastTexX, 0.0F, this.lx, this.ly);
         NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, lastOuterX, lastOuterY, lastOuterZ, lastTexX, 1.0F, this.lx, this.ly);
      }
   }
}
