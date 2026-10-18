package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.renderer.GameRenderer.class)
public abstract class MahouShaderFrameMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="close",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void mahoutsukai$close(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){stepsword.mahoutsukai.render.ClipWrappedRenderLayer.close();stepsword.mahoutsukai.render.FogRenderLayer.close();}

 @org.spongepowered.asm.mixin.injection.Inject(method="render",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void mahoutsukai$frame(net.minecraft.client.DeltaTracker delta,boolean tick,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){stepsword.mahoutsukai.render.ClipWrappedRenderLayer.endFrame();stepsword.mahoutsukai.render.FogRenderLayer.endFrame();}
}
