package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Optional;

/** Equivalent builder for the pipeline properties exposed by vanilla 26.1.2. */
public final class PipelineCopies {
    public static RenderPipeline.Builder copy(RenderPipeline pipeline) {
        var builder = RenderPipeline.builder().withLocation(pipeline.getLocation())
            .withVertexShader(pipeline.getVertexShader()).withFragmentShader(pipeline.getFragmentShader())
            .withColorTargetState(pipeline.getColorTargetState())
            .withDepthStencilState(Optional.ofNullable(pipeline.getDepthStencilState()))
            .withPolygonMode(pipeline.getPolygonMode()).withCull(pipeline.isCull())
            .withVertexFormat(pipeline.getVertexFormat(), pipeline.getVertexFormatMode());
        for (String flag : pipeline.getShaderDefines().flags()) builder.withShaderDefine(flag);
        for (var define : pipeline.getShaderDefines().values().entrySet()) {
            try { builder.withShaderDefine(define.getKey(), Integer.parseInt(define.getValue())); }
            catch (NumberFormatException ignored) { builder.withShaderDefine(define.getKey(), Float.parseFloat(define.getValue())); }
        }
        for (String sampler : pipeline.getSamplers()) builder.withSampler(sampler);
        for (var uniform : pipeline.getUniforms()) {
            if (uniform.textureFormat() == null) builder.withUniform(uniform.name(), uniform.type());
            else builder.withUniform(uniform.name(), uniform.type(), uniform.textureFormat());
        }
        return builder;
    }
}
