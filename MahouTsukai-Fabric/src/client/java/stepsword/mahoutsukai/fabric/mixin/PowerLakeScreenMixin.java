package stepsword.mahoutsukai.fabric.mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ScreenEffectRenderer.class)
public abstract class PowerLakeScreenMixin {
 @Inject(method="renderWater",at=@At("HEAD"),cancellable=true)
 private static void mahoutsukai$murkyOverlay(Minecraft client,PoseStack pose,MultiBufferSource buffer,CallbackInfo ci){
  if(stepsword.mahoutsukai.util.Utils.isInMurkyWater(client.getCameraEntity()))ci.cancel();
 }
}
