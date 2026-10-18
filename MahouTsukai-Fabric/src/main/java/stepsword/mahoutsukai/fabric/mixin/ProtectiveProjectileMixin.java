package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Projectile.class)
public abstract class ProtectiveProjectileMixin {
 @Inject(method="hitTargetOrDeflectSelf",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$protect(HitResult hit,CallbackInfoReturnable<ProjectileDeflection> ci) {
  if(stepsword.mahoutsukai.effects.displacement.ProtectiveDisplacementSpellEffect.protectiveDisplacementProjectileImpact((Projectile)(Object)this,hit))
   ci.setReturnValue(ProjectileDeflection.NONE);
 }
}
