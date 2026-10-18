package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(net.minecraft.world.entity.LivingEntity.class)
public abstract class ReplicaDamageMixin {
 @Inject(method="hurtServer",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$replica(net.minecraft.server.level.ServerLevel world,net.minecraft.world.damagesource.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){var self=(net.minecraft.world.entity.LivingEntity)(Object)this;if(self.isAlive() && amount>0 && Float.isFinite(amount)){stepsword.mahoutsukai.item.replica.Replica.replicaDamageSource(self,source);if(stepsword.mahoutsukai.item.replica.Replica.replicaLivingHurt(self,source))ci.setReturnValue(false);}}
}
