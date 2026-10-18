package stepsword.mahoutsukai.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import stepsword.mahoutsukai.render.RenderBufferWrapper;

public class MahouGeometry {
   public float r = 1.0F;
   public float g = 1.0F;
   public float b = 1.0F;
   public float a = 1.0F;
   public int lx = 240;
   public int ly = 240;
   public RenderType type = null;

   public float getR() {
      return this.r;
   }

   public MahouGeometry setR(float r) {
      this.r = r;
      return this;
   }

   public float getG() {
      return this.g;
   }

   public MahouGeometry setG(float g) {
      this.g = g;
      return this;
   }

   public float getB() {
      return this.b;
   }

   public MahouGeometry setB(float b) {
      this.b = b;
      return this;
   }

   public float getA() {
      return this.a;
   }

   public MahouGeometry setA(float a) {
      this.a = a;
      return this;
   }

   public int getLx() {
      return this.lx;
   }

   public MahouGeometry setLx(int lx) {
      this.lx = lx;
      return this;
   }

   public int getLy() {
      return this.ly;
   }

   public MahouGeometry setLy(int ly) {
      this.ly = ly;
      return this;
   }

   public float[] getColor() {
      return new float[]{this.r, this.g, this.b, this.a};
   }

   public MahouGeometry setColor(float[] color) {
      if (color.length >= 4) {
         this.r = color[0];
         this.g = color[1];
         this.b = color[2];
         this.a = color[3];
      }

      return this;
   }

   public void vertices(Pose pose, VertexConsumer buffer) {
   }

   public RenderType getType() {
      return this.type;
   }

   public MahouGeometry setType(RenderType type) {
      this.type = type;
      return this;
   }

   public void render(PoseStack matrix, SubmitNodeCollector submitter) {
      if (this.getType() != null) {
         submitter.submitCustomGeometry(matrix, this.getType(), this::vertices);
      }
   }

   public void render(PoseStack matrix, RenderBufferWrapper buffer) {
      if (this.getType() != null) {
         buffer.submit(matrix, this.getType(), this::vertices);
      }
   }

   public void render(PoseStack matrix, MultiBufferSource bufferSource) {
      if (this.getType() != null) {
         matrix.pushPose();
         Pose pose = matrix.last();
         VertexConsumer bb = bufferSource.getBuffer(this.getType());
         this.vertices(pose, bb);
         matrix.popPose();
         if (bufferSource instanceof BufferSource buffer) {
            buffer.endBatch(this.getType());
         }
      }
   }
}
