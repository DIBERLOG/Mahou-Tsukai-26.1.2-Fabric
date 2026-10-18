package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(FogRenderer.class)
public abstract class PowerLakeFogMixin {
 @Inject(method="setupFog",at=@At("RETURN"))
 private void mahoutsukai$lakeFog(Camera camera,int distance,DeltaTracker delta,float darken,ClientLevel world,CallbackInfoReturnable<FogData> result){
  stepsword.mahoutsukai.render.overlay.RenderPowerConsolidation.apply(camera.entity(),result.getReturnValue());
  stepsword.mahoutsukai.fabric.ProjectorClient.applyFog(camera.entity(),result.getReturnValue());
 }
}
