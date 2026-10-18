package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class ReplicationEffectMixin {
 @Inject(method="onEffectsRemoved",at=@At("TAIL"))
 private void mahoutsukai$removed(java.util.Collection<MobEffectInstance> removed,CallbackInfo ci) {
  for(var effect:removed) if(effect.getEffect().value() instanceof stepsword.mahoutsukai.potion.ExtraPotionInterface extra) extra.onRemove((LivingEntity)(Object)this,effect.getEffect().value());
 }
 @Inject(method="addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",at=@At("RETURN"))
 private void mahoutsukai$added(MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> ci) {
  if(ci.getReturnValue()) {
   stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationPotionAdded((LivingEntity)(Object)this,effect);
   if(effect.getEffect().value() instanceof stepsword.mahoutsukai.potion.ExtraPotionInterface extra) extra.onAdd((LivingEntity)(Object)this,effect.getEffect().value());
  }
 }
 @Inject(method="forceAddEffect",at=@At("TAIL"))
 private void mahoutsukai$forced(MobEffectInstance effect,Entity source,CallbackInfo ci) {
  stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationPotionAdded((LivingEntity)(Object)this,effect);
  if(effect.getEffect().value() instanceof stepsword.mahoutsukai.potion.ExtraPotionInterface extra) extra.onAdd((LivingEntity)(Object)this,effect.getEffect().value());
 }
}
