package stepsword.mahoutsukai.fabric.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Map;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSetup.class)
public interface AccessorRenderSetup {
   @Accessor("pipeline")
   RenderPipeline mahou$getPipeline();

   @Accessor("textures")
   Map mahou$getTextures();

   @Accessor("textureTransform")
   TextureTransform mahou$getTextureTransform();

   @Accessor("outputTarget")
   OutputTarget mahou$getOutputTarget();

   @Accessor("outlineProperty")
   OutlineProperty mahou$getOutlineProperty();

   @Accessor("useLightmap")
   boolean mahou$getUseLightmap();

   @Accessor("useOverlay")
   boolean mahou$getUseOverlay();

   @Accessor("layeringTransform")
   LayeringTransform mahou$getLayeringTransform();
}
