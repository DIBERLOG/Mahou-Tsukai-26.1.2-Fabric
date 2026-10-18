package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public class ColorRenderTypeBuffer implements MultiBufferSource {
   MultiBufferSource delegate;
   float r;
   float g;
   float b;
   float a;

   public ColorRenderTypeBuffer(MultiBufferSource buffer, float r, float g, float b, float a) {
      this.delegate = buffer;
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      return tint(this.delegate.getBuffer(renderType), this.r, this.g, this.b, this.a);
   }

   public static VertexConsumer tint(VertexConsumer delegate, float r, float g, float b, float a) {
      return new ColorRenderTypeBuffer.TintingVertexConsumer(delegate, r, g, b, a);
   }

   private static final class TintingVertexConsumer implements VertexConsumer {
      private final VertexConsumer delegate;
      private final float r;
      private final float g;
      private final float b;
      private final float a;

      private TintingVertexConsumer(VertexConsumer delegate, float r, float g, float b, float a) {
         this.delegate = delegate;
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = a;
      }

      public VertexConsumer addVertex(float x, float y, float z) {
         return this.delegate.addVertex(x, y, z);
      }

      public VertexConsumer setColor(int r, int g, int b, int a) {
         return this.delegate.setColor(scale(r, this.r), scale(g, this.g), scale(b, this.b), scale(a, this.a));
      }

      public VertexConsumer setColor(int color) {
         int alpha = color >>> 24 & 0xFF;
         int red = color >>> 16 & 0xFF;
         int green = color >>> 8 & 0xFF;
         int blue = color & 0xFF;
         return this.delegate.setColor(scale(red, this.r), scale(green, this.g), scale(blue, this.b), scale(alpha, this.a));
      }

      public VertexConsumer setUv(float u, float v) {
         return this.delegate.setUv(u, v);
      }

      public VertexConsumer setUv1(int u, int v) {
         return this.delegate.setUv1(u, v);
      }

      public VertexConsumer setUv2(int u, int v) {
         return this.delegate.setUv2(u, v);
      }

      public VertexConsumer setNormal(float x, float y, float z) {
         return this.delegate.setNormal(x, y, z);
      }

      public VertexConsumer setLineWidth(float width) {
         return this.delegate.setLineWidth(width);
      }

      private static int scale(int value, float factor) {
         return Math.max(0, Math.min(255, Math.round(value * factor)));
      }
   }
}
