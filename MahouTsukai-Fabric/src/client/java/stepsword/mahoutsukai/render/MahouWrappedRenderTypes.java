package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.shaders.UniformType;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderSetup;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderSetupBuilder;
import stepsword.mahoutsukai.fabric.mixin.AccessorRenderType;

public final class MahouWrappedRenderTypes {
   private MahouWrappedRenderTypes() {
   }

   public static RenderType wrapCull(RenderType delegate, String name) {
      return wrap(delegate, name, clonePipeline(delegate.pipeline(), "_with_cull_pipeline", builder -> builder.withCull(false)));
   }

   public static RenderType wrapBlend(RenderType delegate, String name) {
      return wrap(delegate, name, clonePipeline(delegate.pipeline(), "_with_blend_pipeline", builder -> {
         ColorTargetState state = delegate.pipeline().getColorTargetState();
         builder.withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), state.writeMask()));
      }));
   }

   public static RenderType wrapNoDepth(RenderType delegate, String name) {
      return wrap(delegate, name, clonePipeline(delegate.pipeline(), "_with_no_depth_pipeline", builder -> builder.withDepthStencilState(Optional.empty())));
   }

   public static RenderType wrapFullBright(RenderType delegate, String name) {
      return wrap(
         delegate,
         name,
         clonePipeline(
            delegate.pipeline(),
            "_with_fullbright_pipeline",
            builder -> builder.withVertexShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/fullbright"))
               .withFragmentShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/fullbright"))
         )
      );
   }

   public static RenderType wrapClip(RenderType delegate, String name) {
      return wrap(
         delegate,
         name,
         clonePipeline(
            delegate.pipeline(),
            "_with_clip_pipeline",
            builder -> builder.withVertexShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/clip"))
               .withFragmentShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/clip"))
               .withUniform("MahouClip", UniformType.UNIFORM_BUFFER)
         )
      );
   }

   private static RenderType wrap(RenderType delegate, String name, RenderPipeline pipeline) {
      RenderSetup delegateState = ((AccessorRenderType)delegate).mahou$getState();
      AccessorRenderSetup state = (AccessorRenderSetup)(Object)delegateState;
      RenderSetupBuilder builder = RenderSetup.builder(pipeline)
         .bufferSize(delegate.bufferSize())
         .setLayeringTransform(state.mahou$getLayeringTransform())
         .setOutputTarget(state.mahou$getOutputTarget())
         .setTextureTransform(state.mahou$getTextureTransform())
         .setOutline(state.mahou$getOutlineProperty());
      if (state.mahou$getUseLightmap()) {
         builder.useLightmap();
      }

      if (state.mahou$getUseOverlay()) {
         builder.useOverlay();
      }

      if (delegate.affectsCrumbling()) {
         builder.affectsCrumbling();
      }

      if (delegate.sortOnUpload()) {
         builder.sortOnUpload();
      }

      ((AccessorRenderSetupBuilder)builder).mahou$getTextures().putAll(state.mahou$getTextures());
      return RenderType.create(name, builder.createRenderSetup());
   }

   private static RenderPipeline clonePipeline(RenderPipeline source, String suffix, Consumer<Builder> mutator) {
      Builder builder = PipelineCopies.copy(source).withLocation(Identifier.fromNamespaceAndPath(source.getLocation().getNamespace(),source.getLocation().getPath()+suffix));
      mutator.accept(builder);
      return builder.build();
   }
}
