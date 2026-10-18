package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.HashMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.render.shader.FogShaderInstance;
import stepsword.mahoutsukai.render.shader.PassThroughShaderInstance;

public final class MahoujinRenderType {
 public static RenderType wrapClipLayer(RenderType type,boolean enabled,double pitch,double yaw,double x,double y,double z,double progress,double size,Matrix4f matrix){return ClipWrappedRenderLayer.wrap(enabled,pitch,yaw,x,y,z,progress,size,type,matrix);}

   public static HashMap<String, RenderType> renderTypeCache = new HashMap<>();
   public static Builder QUAD_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET})
      .withVertexShader("core/rendertype_text")
      .withFragmentShader("core/rendertype_text")
      .withSampler("Sampler0")
      .withSampler("Sampler2")
      .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS);
   public static Builder BUILTIN_TEXT_QUAD_PIPELINE = PipelineCopies.copy(RenderPipelines.TEXT)
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS);
   public static Builder SIMPLE_QUAD_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET})
      .withVertexShader("core/rendertype_text")
      .withFragmentShader("core/rendertype_text")
      .withSampler("Sampler0")
      .withSampler("Sampler2")
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS);
   public static Builder TRIANGLE_STRIP_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET})
      .withVertexShader("core/rendertype_text")
      .withFragmentShader("core/rendertype_text")
      .withSampler("Sampler0")
      .withSampler("Sampler2")
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.TRIANGLE_STRIP);
   public static Builder LINES_PIPELINE = PipelineCopies.copy(RenderPipelines.LINES).withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, Mode.LINES);
   public static DepthStencilState LEQUAL = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true);
   public static DepthStencilState NONE = new DepthStencilState(CompareOp.ALWAYS_PASS, true);

   private MahoujinRenderType() {
   }

   public static RenderPipeline withStandardOverrides(RenderPipeline pipeline, DepthStencilState depthStencilState) {
      return PipelineCopies.copy(pipeline).withDepthStencilState(depthStencilState).withCull(false).build();
   }

   private static Identifier pipelineLocation(String name) {
      return Identifier.fromNamespaceAndPath("mahoutsukai", "pipeline/" + name);
   }

   public static RenderSetupBuilder defaultTypeSettings(String pipelineName, Builder pipeline, boolean crumbling, boolean sort) {
      RenderSetupBuilder builder = RenderSetup.builder(pipeline.withLocation(pipelineLocation(pipelineName)).build());
      builder = builder.bufferSize(256).useLightmap();
      if (crumbling) {
         builder = builder.affectsCrumbling();
      }

      if (sort) {
         builder = builder.sortOnUpload();
      }

      return builder;
   }

   public static RenderSetupBuilder defaultTypeSettings(RenderPipeline pipeline, boolean crumbling, boolean sort) {
      RenderSetupBuilder builder = RenderSetup.builder(pipeline);
      builder = builder.bufferSize(256).useLightmap();
      if (crumbling) {
         builder = builder.affectsCrumbling();
      }

      if (sort) {
         builder = builder.sortOnUpload();
      }

      return builder;
   }

   public static RenderSetupBuilder defaultTypeSettings(String pipelineName, RenderPipeline pipeline, boolean crumbling, boolean sort) {
      return defaultTypeSettings(PipelineCopies.copy(pipeline).withLocation(pipelineLocation(pipelineName)).build(), crumbling, sort);
   }

   public static RenderType createCachedRenderType(String name, RenderSetup state) {
      if (renderTypeCache.containsKey(name)) {
         return renderTypeCache.get(name);
      }

      RenderType cacheme = RenderType.create(name, state);
      renderTypeCache.put(name, cacheme);
      return cacheme;
   }

   public static RenderType createHollowSquareRenderType(Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_hollow_square" + r.getPath() + x,
         defaultTypeSettings("mahou_hollow_square_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createBoxRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_box" + r.getPath() + x,
         defaultTypeSettings("mahou_box_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createMahoujinRenderType(Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_mahoujin" + r.getPath() + x,
         defaultTypeSettings("mahou_mahoujin_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createScopedMahoujinRenderType(String scope, Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_mahoujin_" + scope + "_" + r.getPath() + x,
         defaultTypeSettings("mahou_mahoujin_" + scope + "_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createBuiltInTextMahoujinRenderType(String scope, Identifier r, int x, boolean blur) {
      String name = "mahou_mahoujin_builtin_text_" + scope + "_" + r.getPath() + x;
      RenderType cached = renderTypeCache.get(name);
      return cached != null
         ? cached
         : createCachedRenderType(
            name,
            defaultTypeSettings(name, BUILTIN_TEXT_QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
               .withTexture("Sampler0", r)
               .useLightmap()
               .createRenderSetup()
         );
   }

   public static RenderType createLayeredMahoujinRenderType(String scope, Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_mahoujin_layered_" + scope + "_" + r.getPath() + x,
         defaultTypeSettings(
               "mahou_mahoujin_layered_" + scope + "_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false
            )
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createNoDepthMahoujinRenderType(String scope, Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_mahoujin_no_depth_" + scope + "_" + r.getPath() + x,
         defaultTypeSettings(
               "mahou_mahoujin_no_depth_" + scope + "_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(NONE).withCull(false), false, false
            )
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createMahoujinNoFogRenderType(Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_mahoujin_no_fog" + r.getPath() + x,
         defaultTypeSettings("mahou_mahoujin_no_fog_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createShadedRenderType(Identifier r, int x, boolean blur) {
      return createCachedRenderType(
         "mahou_shaded" + r.getPath() + x,
         defaultTypeSettings("mahou_shaded_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createMahoujinRenderType(Identifier r, int x, boolean blur, Supplier<PassThroughShaderInstance> shader) {
      PassThroughShaderInstance shaderInstance = shader.get();
      RenderPipeline pipeline = shaderInstance != null
         ? withStandardOverrides(shaderInstance.pipeline, LEQUAL)
         : QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false).build();
      return createCachedRenderType(
         "mahou_circle" + r.getPath() + x,
         defaultTypeSettings("mahou_circle_" + r.getPath() + "_" + x, pipeline, false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .useLightmap()
            .createRenderSetup()
      );
   }

   public static RenderType createFogRenderType(Identifier r, int x, boolean blur, Supplier<FogShaderInstance> shader) {
      FogShaderInstance shaderInstance = shader.get();
      RenderPipeline pipeline = shaderInstance != null
         ? withStandardOverrides(shaderInstance.pipeline, LEQUAL)
         : QUAD_PIPELINE.withDepthStencilState(LEQUAL).withCull(false).build();
      return createCachedRenderType(
         "mahou_fog" + r.getPath() + x,
         defaultTypeSettings("mahou_fog_" + r.getPath() + "_" + x, pipeline, false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createRunesRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_runes" + r.getPath() + x,
         defaultTypeSettings("mahou_runes_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createScopedRunesRenderType(String scope, Identifier r, int x) {
      return createCachedRenderType(
         "mahou_runes_" + scope + "_" + r.getPath() + x,
         defaultTypeSettings(
               "mahou_runes_" + scope + "_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false
            )
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createSmokeRingRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_smoke_ring" + r.getPath() + x,
         defaultTypeSettings("mahou_smoke_ring_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createCharacterRenderType(Identifier r, int x) {
      return RenderTypes.textPolygonOffset(r);
   }

   public static RenderType createWilliamRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_william" + r.getPath() + x,
         defaultTypeSettings("mahou_william_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createLightningRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_lightning" + r.getPath() + x,
         defaultTypeSettings("mahou_lightning_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createRunesPRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_runes_ley" + r.getPath() + x,
         defaultTypeSettings("mahou_runes_ley_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createBeamRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_beam" + r.getPath() + x,
         defaultTypeSettings("mahou_beam_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createSphereRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_sphere" + r.getPath() + x,
         defaultTypeSettings("mahou_sphere_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createSphereRenderType2(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_sphere_2" + r.getPath() + x,
         defaultTypeSettings("mahou_sphere_2_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createLinesNoDepthRenderType() {
      return createCachedRenderType(
         "familiar_ore_lines",
         defaultTypeSettings("familiar_ore_lines", LINES_PIPELINE.withDepthStencilState(NONE).withCull(false), false, false).createRenderSetup()
      );
   }

   public static RenderType createLinesRenderType(double width, int index) {
      return createCachedRenderType(
         "mahou_lines" + index,
         defaultTypeSettings("mahou_lines_" + index, LINES_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false).createRenderSetup()
      );
   }

   public static RenderType createTriangleRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_triangle" + r.getPath() + x,
         defaultTypeSettings("mahou_triangle_" + r.getPath() + "_" + x, TRIANGLE_STRIP_PIPELINE.withDepthStencilState(LEQUAL).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createRunesNoDepthRenderType(Identifier r, int x) {
      return createCachedRenderType(
         "mahou_runes_no_depth" + r.getPath() + x,
         defaultTypeSettings("mahou_runes_no_depth_" + r.getPath() + "_" + x, QUAD_PIPELINE.withDepthStencilState(NONE).withCull(false), false, false)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType createScopedRunesNoDepthTriangleStripRenderType(String scope, Identifier r, int x) {
      return createCachedRenderType(
         "mahou_runes_no_depth_strip_" + scope + "_" + r.getPath() + x,
         defaultTypeSettings(
               "mahou_runes_no_depth_strip_" + scope + "_" + r.getPath() + "_" + x,
               TRIANGLE_STRIP_PIPELINE.withDepthStencilState(NONE).withCull(false),
               false,
               false
            )
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .withTexture("Sampler0", r)
            .createRenderSetup()
      );
   }

   public static RenderType checkCached(RenderType type) {
      if (renderTypeCache.containsKey(type.name)) {
         return renderTypeCache.get(type.name);
      }

      renderTypeCache.put(type.name, type);
      return type;
   }

   public static RenderType checkCached(String name, Supplier<RenderType> sup) {
      if (renderTypeCache.containsKey(name)) {
         return renderTypeCache.get(name);
      }

      RenderType type = sup.get();
      renderTypeCache.put(type.name, type);
      return type;
   }

}
