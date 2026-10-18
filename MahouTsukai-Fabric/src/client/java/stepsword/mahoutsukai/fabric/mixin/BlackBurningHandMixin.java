package stepsword.mahoutsukai.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Retain the original RenderHandEvent hurt transform at the native hand-render entry point. */
@Mixin(ItemInHandRenderer.class)
public abstract class BlackBurningHandMixin {
    @Inject(method="renderHandsWithItems",at=@At("HEAD"))
    private void mahoutsukai$hurtHands(float partialTicks,PoseStack pose,SubmitNodeCollector collector,LocalPlayer player,int light,CallbackInfo ci) {
        if(player!=null && !player.isSleeping() && Minecraft.getInstance().options.getCameraType().isFirstPerson()
            && player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING))
            stepsword.mahoutsukai.render.overlay.RenderBlackBurning.hurtCameraEffect(pose,partialTicks);
    }
}
