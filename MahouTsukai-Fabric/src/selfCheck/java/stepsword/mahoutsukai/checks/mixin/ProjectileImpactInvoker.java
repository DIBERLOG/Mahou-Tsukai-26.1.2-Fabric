package stepsword.mahoutsukai.checks.mixin;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(Projectile.class)
public interface ProjectileImpactInvoker {
 @Invoker("hitTargetOrDeflectSelf") ProjectileDeflection mahoutsukai$impact(HitResult hit);
}
