package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import stepsword.mahoutsukai.util.Utils;

public final class ClipWrappedRenderLayer {
   public static long boundDraws;
   public static double lastp = 0.0;
   private static final DynamicUniformStorage<ClipWrappedRenderLayer.ClipUniform> CLIP_UNIFORMS = new DynamicUniformStorage(
      "Mahou Clip Wrapped UBO", ClipWrappedRenderLayer.ClipUniform.UBO_SIZE, 32
   );
   private static final Map<RenderType, GpuBufferSlice> ACTIVE_CLIPS = Collections.synchronizedMap(new WeakHashMap<>());
   private static final Map<RenderType, List<RenderType>> WRAPPED_CLIP_TYPES = Collections.synchronizedMap(new WeakHashMap<>());
   private static final Map<RenderType, Integer> FRAME_USE_COUNTS = Collections.synchronizedMap(new WeakHashMap<>());

   private ClipWrappedRenderLayer() {
   }

   public static void close(){CLIP_UNIFORMS.close();}
   public static void endFrame() {
      CLIP_UNIFORMS.endFrame();
      ACTIVE_CLIPS.clear();
      FRAME_USE_COUNTS.clear();
   }

   public static GpuBufferSlice getClipSlice(RenderType renderType) {
      return ACTIVE_CLIPS.get(renderType);
   }

   public static RenderType wrap(boolean flag, double pitch, double yaw, double x, double y, double z, double p, double size, RenderType delegate, Matrix4f m) {
      if (flag && !RenderUtils.turnOffMahouShaders()) {
         Vec3 proj = Minecraft.getInstance().gameRenderer.getMainCamera().position();
         Vec3 v = Utils.vecFromPitchYaw((float)pitch, (float)(180.0 - yaw)).scale(-1.0).normalize();
         Vector4f clipPlane = new Vector4f(0.70710677F, 0.70710677F, 0.0F, 0.0F);
         clipPlane.mul(m);
         clipPlane.normalize();
         float d = (float)(v.x() * (proj.x - x) + v.y() * (proj.y - y) + v.z() * (proj.z - z));
         clipPlane.w = (float)(d + p);
         RenderType wrapped = getWrappedType(delegate);
         ACTIVE_CLIPS.put(wrapped, CLIP_UNIFORMS.writeUniform(new ClipWrappedRenderLayer.ClipUniform(new Matrix4f(m), new Vector4f(clipPlane))));
         lastp = p;
         return wrapped;
      } else {
         lastp = p;
         return delegate;
      }
   }

   private static RenderType getWrappedType(RenderType delegate) {
      List<RenderType> wrappedTypes = WRAPPED_CLIP_TYPES.computeIfAbsent(delegate, ignored -> new ArrayList<>());
      int index = FRAME_USE_COUNTS.getOrDefault(delegate, 0);
      FRAME_USE_COUNTS.put(delegate, index + 1);

      while (wrappedTypes.size() <= index) {
         int slot = wrappedTypes.size();
         wrappedTypes.add(MahouWrappedRenderTypes.wrapClip(delegate, getWouldBeName(delegate) + "_" + slot));
      }

      return wrappedTypes.get(index);
   }

   public static float pointToPlaneDistance(Vec3 normal, Vec3 point, float d) {
      float ret = (float)(normal.x * point.x + normal.y * point.y + normal.z * point.z + d);
      return ret / (float)Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
   }

   public static String getWouldBeName(RenderType delegate) {
      return "mahoutsukai" + delegate.name + "_with_clip";
   }

   private record ClipUniform(Matrix4f modelMatrix, Vector4f clipPlane) implements DynamicUniform {
      private static final int UBO_SIZE = new Std140SizeCalculator().putMat4f().putVec4().get();

      public void write(ByteBuffer byteBuffer) {
         Std140Builder.intoBuffer(byteBuffer).putMat4f(this.modelMatrix).putVec4(this.clipPlane);
      }
   }
}
