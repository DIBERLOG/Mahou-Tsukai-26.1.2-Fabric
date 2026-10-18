package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** NeoForge LivingDeathEvent runs after vanilla totem protection. */
@Mixin(LivingEntity.class)
public abstract class DeathProtectionMixin {
 @Inject(method="checkTotemDeathProtection",at=@At("RETURN"),cancellable=true)
 private void mahoutsukai$afterTotem(DamageSource source,CallbackInfoReturnable<Boolean> ci){
  if(ci.getReturnValueZ())return;
  LivingEntity entity=(LivingEntity)(Object)this;
  if(stepsword.mahoutsukai.potion.DeathCollectionEyesPotion.deathCollectionEyesLivingDeath(entity,source)){ci.setReturnValue(true);return;}
  stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationLivingDeath(entity);
  stepsword.mahoutsukai.effects.projection.MorganEffect.morganLivingDeath(entity,source);
  stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath(entity,source);
 }
}
