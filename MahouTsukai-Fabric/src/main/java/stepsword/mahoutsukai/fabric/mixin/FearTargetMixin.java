package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class FearTargetMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void mahoutsukai$fearTarget(LivingEntity target, CallbackInfo ci) {
        var mob = (Mob)(Object)this;
        if (target != null && !mob.level().isClientSide()
                && stepsword.mahoutsukai.util.EffectUtil.hasBuff(mob, stepsword.mahoutsukai.potion.ModEffects.FEAR)) {
            mob.setTarget(null);
            mob.setLastHurtByMob(null);
            ci.cancel();
        }
    }
}
