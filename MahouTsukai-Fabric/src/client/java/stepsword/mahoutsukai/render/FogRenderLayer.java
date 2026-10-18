package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;
import org.joml.Vector4f;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderSetup;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderSetupBuilder;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderType;

public final class FogRenderLayer {
   public static long boundDraws;
   private static final DynamicUniformStorage<FogRenderLayer.FogUniform> FOG_UNIFORMS = new DynamicUniformStorage(
      "Mahou Fog Wrapped UBO", FogRenderLayer.FogUniform.UBO_SIZE, 32
   );
   private static final Map<RenderType, GpuBufferSlice> ACTIVE_FOGS = Collections.synchronizedMap(new WeakHashMap<>());

   private static final java.util.List<RenderType> TYPE_POOL=new java.util.ArrayList<>();
   private static final java.util.Map<java.util.List<Float>,RenderType> FRAME_TYPES=new java.util.HashMap<>();
   private static com.mojang.blaze3d.textures.GpuTexture depthCopy;
   private static com.mojang.blaze3d.textures.GpuTextureView depthView;
   private static boolean copiedDepth;
   public static com.mojang.blaze3d.textures.GpuTextureView depth(){return depthView;}
   public static void copyDepth(RenderType type){
      if(copiedDepth||getFogSlice(type)==null)return;
      var source=Minecraft.getInstance().getMainRenderTarget().getDepthTexture();if(source==null)return;
      int width=source.getWidth(0),height=source.getHeight(0);
      if(depthCopy==null||depthCopy.getWidth(0)!=width||depthCopy.getHeight(0)!=height){
         if(depthView!=null)depthView.close();if(depthCopy!=null)depthCopy.close();
         depthCopy=com.mojang.blaze3d.systems.RenderSystem.getDevice().createTexture("Mahou fog depth snapshot",com.mojang.blaze3d.textures.GpuTexture.USAGE_TEXTURE_BINDING|com.mojang.blaze3d.textures.GpuTexture.USAGE_COPY_DST,source.getFormat(),width,height,1,1);
         depthView=com.mojang.blaze3d.systems.RenderSystem.getDevice().createTextureView(depthCopy);
      }
      com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source,depthCopy,0,0,0,0,0,width,height);copiedDepth=true;
   }
   private FogRenderLayer() {
   }

   public static void close(){if(depthView!=null){depthView.close();depthView=null;}if(depthCopy!=null){depthCopy.close();depthCopy=null;}FOG_UNIFORMS.close();}
   public static void endFrame() {
      FOG_UNIFORMS.endFrame();ACTIVE_FOGS.clear();FRAME_TYPES.clear();copiedDepth=false;
   }

   public static GpuBufferSlice getFogSlice(RenderType renderType) {
      return ACTIVE_FOGS.get(renderType);
   }

   public static void inherit(RenderType from, RenderType to) {
      GpuBufferSlice slice = ACTIVE_FOGS.get(from);
      if (slice != null) {
         ACTIVE_FOGS.put(to, slice);
      }
   }

   public static RenderType wrap(RenderType delegate, float[] rgba, float softness) {
      var key=java.util.List.of(rgba[0],rgba[1],rgba[2],rgba[3],softness);
      RenderType wrapped=FRAME_TYPES.get(key);
      if(wrapped==null){int slot=FRAME_TYPES.size();while(TYPE_POOL.size()<=slot)TYPE_POOL.add(createWrappedType(delegate));wrapped=TYPE_POOL.get(slot);FRAME_TYPES.put(key,wrapped);}
      Minecraft minecraft = Minecraft.getInstance();
      float width = Math.max(1.0F, minecraft.getWindow().getWidth());
      float height = Math.max(1.0F, minecraft.getWindow().getHeight());
      FogRenderLayer.FogUniform uniform = new FogRenderLayer.FogUniform(
         new Vector4f(rgba[0], rgba[1], rgba[2], rgba[3]), new Vector4f(0.07F, 400.0F, 1.0F / width, 1.0F / height), new Vector4f(softness, 0.0F, 0.0F, 0.0F)
      );
      ACTIVE_FOGS.put(wrapped, FOG_UNIFORMS.writeUniform(uniform));
      return wrapped;
   }

   private static RenderType createWrappedType(RenderType delegate) {
      RenderSetup delegateState = ((AccessorRenderType)delegate).mahou$getState();
      AccessorRenderSetup state = (AccessorRenderSetup)(Object)delegateState;
      RenderPipeline fogPipeline = PipelineCopies.copy(delegate.pipeline())
         .withLocation(Identifier.fromNamespaceAndPath("mahoutsukai", "projector_fog_"+TYPE_POOL.size()))
         .withVertexShader(Identifier.fromNamespaceAndPath("mahoutsukai","core/fog"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("mahoutsukai","core/fog"))
         .withSampler("Sampler1").withUniform("MahouFog",com.mojang.blaze3d.shaders.UniformType.UNIFORM_BUFFER)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
         .build();
      RenderSetupBuilder builder = RenderSetup.builder(fogPipeline)
         .bufferSize(delegate.bufferSize())
         .setLayeringTransform(state.mahou$getLayeringTransform())
         .setOutputTarget(state.mahou$getOutputTarget())
         .setTextureTransform(state.mahou$getTextureTransform())
         .setOutline(state.mahou$getOutlineProperty());
      if (delegate.affectsCrumbling()) {
         builder.affectsCrumbling();
      }

      if (delegate.sortOnUpload()) {
         builder.sortOnUpload();
      }

      if (state.mahou$getUseLightmap()) {
         builder.useLightmap();
      }

      if (state.mahou$getUseOverlay()) {
         builder.useOverlay();
      }

      ((AccessorRenderSetupBuilder)builder).mahou$getTextures().putAll(state.mahou$getTextures());
      return RenderType.create(getWouldBeName(delegate), builder.createRenderSetup());
   }

   public static String getWouldBeName(RenderType delegate) {
      return "mahoutsukai" + delegate.name + "_with_fog0";
   }

   private record FogUniform(Vector4f smokeColor, Vector4f nearFarAndInvertedViewport, Vector4f softness) implements DynamicUniform {
      private static final int UBO_SIZE = new Std140SizeCalculator().putVec4().putVec4().putVec4().get();

      public void write(ByteBuffer byteBuffer) {
         Std140Builder.intoBuffer(byteBuffer).putVec4(this.smokeColor).putVec4(this.nearFarAndInvertedViewport).putVec4(this.softness);
      }
   }
}
