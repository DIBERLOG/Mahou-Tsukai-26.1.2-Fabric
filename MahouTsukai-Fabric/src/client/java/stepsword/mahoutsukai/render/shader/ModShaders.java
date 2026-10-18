package stepsword.mahoutsukai.render.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ModShaders {
   public static final RenderPipeline FAE_CORE_PIPELINE = stepsword.mahoutsukai.render.PipelineCopies.copy(RenderPipelines.TEXT)
      .withLocation(Identifier.fromNamespaceAndPath("mahoutsukai", "pipeline/fae_core"))
      .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
      .withCull(false)
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS)
      .build();
   public static final RenderPipeline FAE_GLOW_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.MATRICES_PROJECTION_SNIPPET})
      .withLocation(Identifier.fromNamespaceAndPath("mahoutsukai", "pipeline/fae_glow"))
      .withVertexShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/fae_glow"))
      .withFragmentShader(Identifier.fromNamespaceAndPath("mahoutsukai", "core/fae_glow"))
      .withSampler("Sampler0")
      .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
      .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
      .withCull(false)
      .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS)
      .build();
}
