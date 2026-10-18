package stepsword.mahoutsukai.render;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class ColorSubmitNodeCollector {
   private static final Map<RenderType, RenderType> BLENDED_TYPES = new IdentityHashMap<>();

   private ColorSubmitNodeCollector() {
   }

   public static SubmitNodeCollector wrap(SubmitNodeCollector delegate, float red, float green, float blue, float alpha) {
      return (SubmitNodeCollector)Proxy.newProxyInstance(
         SubmitNodeCollector.class.getClassLoader(),
         new Class[]{SubmitNodeCollector.class},
         new ColorSubmitNodeCollector.Handler(delegate, red, green, blue, alpha)
      );
   }

   private static OrderedSubmitNodeCollector wrapOrdered(OrderedSubmitNodeCollector delegate, float red, float green, float blue, float alpha) {
      return (OrderedSubmitNodeCollector)Proxy.newProxyInstance(
         OrderedSubmitNodeCollector.class.getClassLoader(),
         new Class[]{OrderedSubmitNodeCollector.class},
         new ColorSubmitNodeCollector.Handler(delegate, red, green, blue, alpha)
      );
   }

   private static RenderType blended(RenderType renderType) {
      synchronized (BLENDED_TYPES) {
         return BLENDED_TYPES.computeIfAbsent(renderType, type -> MahouWrappedRenderTypes.wrapBlend(type, ColorWrappedRenderLayer.getWouldBeName(type)));
      }
   }

   private static int[] multiplyColors(int[] colors, float red, float green, float blue, float alpha) {
      int[] result = (int[])colors.clone();

      for (int i = 0; i < result.length; i++) {
         result[i] = multiplyColor(result[i], red, green, blue, alpha);
      }

      return result;
   }

   private static int multiplyColor(int color, float red, float green, float blue, float alpha) {
      int sourceAlpha = color >>> 24 & 0xFF;
      int sourceRed = color >>> 16 & 0xFF;
      int sourceGreen = color >>> 8 & 0xFF;
      int sourceBlue = color & 0xFF;
      return scale(sourceAlpha, alpha) << 24 | scale(sourceRed, red) << 16 | scale(sourceGreen, green) << 8 | scale(sourceBlue, blue);
   }

   private static int scale(int value, float factor) {
      return Math.max(0, Math.min(255, Math.round(value * factor)));
   }

   private record Handler(Object delegate, float red, float green, float blue, float alpha) implements InvocationHandler {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this.delegate, args);
         }

         if (args != null) {
            for (int i = 0; i < args.length; i++) {
               if (args[i] instanceof RenderType renderType) {
                  args[i] = ColorSubmitNodeCollector.blended(renderType);
               }
            }

            if (method.getName().equals("submitModel") && args.length == 10 && args[6] instanceof Integer color) {
               args[6] = ColorSubmitNodeCollector.multiplyColor(color, this.red, this.green, this.blue, this.alpha);
            } else if (method.getName().equals("submitModelPart") && args.length == 11 && args[8] instanceof Integer color) {
               args[8] = ColorSubmitNodeCollector.multiplyColor(color, this.red, this.green, this.blue, this.alpha);
            } else if (method.getName().equals("submitBlockModel") && args.length == 7 && args[3] instanceof int[] colors) {
               args[3] = ColorSubmitNodeCollector.multiplyColors(colors, this.red, this.green, this.blue, this.alpha);
            } else if (method.getName().equals("submitItem") && args.length == 8 && args[5] instanceof int[] colors) {
               args[5] = ColorSubmitNodeCollector.multiplyColors(colors, this.red, this.green, this.blue, this.alpha);
            } else if (method.getName().equals("submitCustomGeometry") && args.length == 3 && args[2] instanceof CustomGeometryRenderer renderer) {
               args[2] = (CustomGeometryRenderer)(pose, buffer) -> renderer.render(
                  pose, ColorRenderTypeBuffer.tint(buffer, this.red, this.green, this.blue, this.alpha)
               );
            }
         }

         Object result = method.invoke(this.delegate, args);
         return method.getName().equals("order") && result instanceof OrderedSubmitNodeCollector ordered
            ? ColorSubmitNodeCollector.wrapOrdered(ordered, this.red, this.green, this.blue, this.alpha)
            : result;
      }
   }
}
