package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
@Mixin(MobEffectInstance.class)
public abstract class EffectIconMixin {
 @ModifyReturnValue(method="showIcon",at=@At("RETURN"))
 private boolean mahoutsukai$icon(boolean value) {
  var effect=((MobEffectInstance)(Object)this).getEffect().value();
  return value && (!(effect instanceof stepsword.mahoutsukai.potion.NoShowEffect hidden) || hidden.shouldShow());
 }
}
