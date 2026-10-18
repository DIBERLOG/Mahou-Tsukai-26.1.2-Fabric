package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.potion.ReversionEyesPotion;

@Mixin(Entity.class)
public abstract class MixinEntityFireImmunity {
   @Inject(method = "fireImmune()Z", at = @At("HEAD"), cancellable = true)
   private void mahouReverseFireImmunity(CallbackInfoReturnable<Boolean> cir) {
      Entity entity = (Entity)(Object)this;
      if (entity instanceof LivingEntity living && ReversionEyesPotion.isEntityReverted(living)) {
         cir.setReturnValue(false);
      }
   }
}
