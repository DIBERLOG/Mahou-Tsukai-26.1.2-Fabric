package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(ServerLevel.class)
public abstract class BoundEntityTickMixin {
 @WrapOperation(method="tickNonPassenger",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/Entity;tick()V"))
 private void mahoutsukai$bound(Entity entity,Operation<Void> tick) {
  if(entity instanceof LivingEntity living && stepsword.mahoutsukai.potion.BindingEyesPotion.bindingEyesLivingUpdate(living)) return;
  tick.call(entity);
 }
}
