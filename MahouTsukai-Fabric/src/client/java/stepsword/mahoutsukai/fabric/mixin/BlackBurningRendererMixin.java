package stepsword.mahoutsukai.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stepsword.mahoutsukai.fabric.BlackBurningRenderState;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.overlay.RenderBlackBurning;

@Mixin(LivingEntityRenderer.class)
public abstract class BlackBurningRendererMixin {
    @Inject(method="extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",at=@At("TAIL"))
    private void mahoutsukai$extract(LivingEntity entity,LivingEntityRenderState state,float partialTicks,CallbackInfo ci) {
        ((BlackBurningRenderState)(Object)state).mahoutsukai$setBlackBurning(entity.hasEffect(ModEffects.BLACK_BURNING));
    }
    @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At("HEAD"))
    private void mahoutsukai$render(LivingEntityRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo ci) {
        RenderBlackBurning.renderEntityFire(state,pose,collector);
    }
}
