package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import stepsword.mahoutsukai.fabric.BlackBurningRenderState;

@Mixin(LivingEntityRenderState.class)
public abstract class BlackBurningStateMixin implements BlackBurningRenderState {
    @Unique private boolean mahoutsukai$blackBurning;
    public boolean mahoutsukai$isBlackBurning() {return mahoutsukai$blackBurning;}
    public void mahoutsukai$setBlackBurning(boolean value) {mahoutsukai$blackBurning=value;}
}
