package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
@Mixin(LivingEntity.class)
public interface LivingDamageAccessor { @Accessor("lastDamageSource") void mahoutsukai$clearLastDamageSource(DamageSource source); }
