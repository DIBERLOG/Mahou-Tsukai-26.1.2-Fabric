package stepsword.mahoutsukai.render;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

public final class ClipSubmitNodeCollector {
   private ClipSubmitNodeCollector() {
   }

   public static SubmitNodeCollector wrap(
      SubmitNodeCollector delegate, boolean clip, double pitch, double yaw, double x, double y, double z, double progress, double size, Matrix4f clipMatrix
   ) {
      return (SubmitNodeCollector)Proxy.newProxyInstance(
         SubmitNodeCollector.class.getClassLoader(),
         new Class[]{SubmitNodeCollector.class},
         new ClipSubmitNodeCollector.Handler(delegate, clip, pitch, yaw, x, y, z, progress, size, new Matrix4f(clipMatrix))
      );
   }

   private record Handler(
      Object delegate, boolean clip, double pitch, double yaw, double x, double y, double z, double progress, double size, Matrix4f clipMatrix
   ) implements InvocationHandler {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this.delegate, args);
         }

         // Native item submissions carry material-bearing quads rather than a RenderType argument.
         if (this.clip && !RenderUtils.turnOffMahouShaders() && method.getName().equals("submitItem")) {
            var pose = (com.mojang.blaze3d.vertex.PoseStack)args[0];
            int light = (Integer)args[2], overlay = (Integer)args[3];
            int[] tints = (int[])args[5];
            @SuppressWarnings("unchecked")
            var quads = (java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad>)args[6];
            var groups = new java.util.LinkedHashMap<RenderType,java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad>>();
            for (var quad : quads) groups.computeIfAbsent(quad.materialInfo().itemRenderType(), ignored -> new java.util.ArrayList<>()).add(quad);
            var collector = (OrderedSubmitNodeCollector)this.delegate;
            for (var group : groups.entrySet()) {
               var type = MahoujinRenderType.wrapClipLayer(group.getKey(), true, this.pitch, this.yaw, this.x, this.y, this.z, this.progress, this.size, this.clipMatrix);
               collector.submitCustomGeometry(pose,type,(nativePose,vertices)->{
                  var instance = new com.mojang.blaze3d.vertex.QuadInstance();
                  instance.setLightCoords(light); instance.setOverlayCoords(overlay);
                  for (var quad : group.getValue()) {
                     var material=quad.materialInfo();int tint=material.tintIndex();
                     instance.setColor(material.isTinted()&&tint>=0&&tint<tints.length?tints[tint]:-1);
                     vertices.putBakedQuad(nativePose,quad,instance);
                  }
               });
            }
            return null;
         }

         if (args != null) {
            for (int i = 0; i < args.length; i++) {
               if (args[i] instanceof RenderType renderType) {
                  args[i] = MahoujinRenderType.wrapClipLayer(
                     renderType, this.clip, this.pitch, this.yaw, this.x, this.y, this.z, this.progress, this.size, this.clipMatrix
                  );
               }
            }
         }

         Object result = method.invoke(this.delegate, args);
         return method.getName().equals("order") && result instanceof OrderedSubmitNodeCollector
            ? Proxy.newProxyInstance(
               SubmitNodeCollector.class.getClassLoader(),
               new Class[]{OrderedSubmitNodeCollector.class},
               new ClipSubmitNodeCollector.Handler(result, this.clip, this.pitch, this.yaw, this.x, this.y, this.z, this.progress, this.size, this.clipMatrix)
            )
            : result;
      }
   }
}
