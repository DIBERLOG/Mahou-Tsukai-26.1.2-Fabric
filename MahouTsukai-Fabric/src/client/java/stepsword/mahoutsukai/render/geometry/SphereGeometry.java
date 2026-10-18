package stepsword.mahoutsukai.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.rendertype.RenderType;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderUtils;

public class SphereGeometry extends MahouGeometry {
   public float radius;
   public float height;
   public int gradation = 48;
   final float PI = 3.141592F;

   public SphereGeometry(float radius) {
      this.radius = radius;
   }

   public float getRadius() {
      return this.radius;
   }

   public SphereGeometry setRadius(float radius) {
      this.radius = radius;
      return this;
   }

   public float getHeight() {
      return this.height;
   }

   public SphereGeometry setHeight(float height) {
      this.height = height;
      return this;
   }

   public int getGradation() {
      return this.gradation;
   }

   public SphereGeometry setGradation(int gradation) {
      this.gradation = gradation;
      return this;
   }

   @Override
   public RenderType getType() {
      return MahoujinRenderType.createSphereRenderType(RenderUtils.beam, 0);
   }

   @Override
   public void vertices(Pose pose, VertexConsumer buffer) {
      float alpha = 0.0F;

      while (alpha < 3.141592F) {
         for (float beta = 0.0F; beta < 6.3146F; beta += 3.141592F / this.gradation) {
            float x = (float)(this.radius * Math.cos(beta) * Math.sin(alpha));
            float y = (float)(this.radius * Math.sin(beta) * Math.sin(alpha));
            float z = (float)(this.radius * Math.cos(alpha));
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, x, y, z, 0.0F, 1.0F, this.lx, this.ly);
            x = (float)(this.radius * Math.cos(beta) * Math.sin(alpha + 3.141592F / this.gradation));
            y = (float)(this.radius * Math.sin(beta) * Math.sin(alpha + 3.141592F / this.gradation));
            z = (float)(this.radius * Math.cos(alpha + 3.141592F / this.gradation));
            NewRenderUtils.addVertex(pose, buffer, this.r, this.g, this.b, this.a, x, y, z, 0.0F, 1.0F, this.lx, this.ly);
         }

         alpha += 3.141592F / this.gradation;
      }
   }
}
