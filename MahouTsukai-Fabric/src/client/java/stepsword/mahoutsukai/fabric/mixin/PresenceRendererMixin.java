package stepsword.mahoutsukai.fabric.mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LivingEntityRenderer.class)
public abstract class PresenceRendererMixin {
 @Inject(method="extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",at=@At("TAIL"))
 private void mahoutsukai$extract(LivingEntity entity,LivingEntityRenderState state,float partial,CallbackInfo ci){((stepsword.mahoutsukai.fabric.PresenceRenderState)(Object)state).mahoutsukai$setConcealed(entity instanceof net.minecraft.world.entity.player.Player && entity.hasEffect(stepsword.mahoutsukai.potion.ModEffects.RIPPER_INVISIBILITY));}
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$hide(LivingEntityRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo ci){if(((stepsword.mahoutsukai.fabric.PresenceRenderState)(Object)state).mahoutsukai$isConcealed())ci.cancel();}
}
