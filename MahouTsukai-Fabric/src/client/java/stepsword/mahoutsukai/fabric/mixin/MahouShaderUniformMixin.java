package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.rendertype.RenderType;
import stepsword.mahoutsukai.render.*;
@Mixin(RenderType.class)
public abstract class MahouShaderUniformMixin {
 @Inject(method="draw",at=@At("HEAD"))
 private void mahoutsukai$depth(com.mojang.blaze3d.vertex.MeshData mesh,CallbackInfo ci){var type=(RenderType)(Object)this;stepsword.mahoutsukai.fabric.IrisCompatibility.register(type.pipeline());FogRenderLayer.copyDepth(type);}
 @Inject(method="draw",at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/systems/RenderPass;setVertexBuffer(ILcom/mojang/blaze3d/buffers/GpuBuffer;)V"))
 private void mahoutsukai$uniforms(com.mojang.blaze3d.vertex.MeshData mesh,CallbackInfo ci,@Local com.mojang.blaze3d.systems.RenderPass pass){
  var type=(RenderType)(Object)this;var clip=ClipWrappedRenderLayer.getClipSlice(type);if(clip!=null){pass.setUniform("MahouClip",clip);ClipWrappedRenderLayer.boundDraws++;}
  var fog=FogRenderLayer.getFogSlice(type);if(fog!=null){pass.setUniform("MahouFog",fog);FogRenderLayer.boundDraws++;if(FogRenderLayer.depth()!=null)pass.bindTexture("Sampler1",FogRenderLayer.depth(),com.mojang.blaze3d.systems.RenderSystem.getSamplerCache().getClampToEdge(com.mojang.blaze3d.textures.FilterMode.NEAREST));}
 }
}
