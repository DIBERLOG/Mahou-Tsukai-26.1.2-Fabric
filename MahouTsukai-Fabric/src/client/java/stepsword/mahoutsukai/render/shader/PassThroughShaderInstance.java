package stepsword.mahoutsukai.render.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class PassThroughShaderInstance {
   public final RenderPipeline pipeline;

   public PassThroughShaderInstance(RenderPipeline pipeline) {
      this.pipeline = pipeline;
   }
}
