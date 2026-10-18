package stepsword.mahoutsukai.render;

import net.minecraft.client.renderer.rendertype.RenderType;

public final class ColorWrappedRenderLayer {
   private ColorWrappedRenderLayer() {
   }

   public static RenderType wrap(RenderType delegate, float r, float g, float b, float a) {
      return delegate;
   }

   public static String getWouldBeName(RenderType delegate) {
      return "mahoutsukai" + delegate.name + "_with_color0";
   }
}
