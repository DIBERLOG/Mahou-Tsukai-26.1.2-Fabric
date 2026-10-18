package stepsword.mahoutsukai.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class BlackBurningScreenMixin {
    @Shadow @Final private MultiBufferSource bufferSource;
    @Inject(method="renderScreenEffect",at=@At("TAIL"))
    private void mahoutsukai$fireOverlay(boolean firstPerson,boolean sleeping,float partialTicks,SubmitNodeCollector collector,boolean renderActivation,CallbackInfo ci) {
        if(firstPerson && !sleeping)
            stepsword.mahoutsukai.render.overlay.RenderBlackBurning.renderOverlayFire(new PoseStack(),partialTicks,bufferSource);
    }
}
