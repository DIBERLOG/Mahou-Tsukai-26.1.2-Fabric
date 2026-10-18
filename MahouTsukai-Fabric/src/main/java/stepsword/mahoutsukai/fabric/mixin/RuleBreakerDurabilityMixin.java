package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Native equivalent of the original NeoForge Item.getMaxDamage(stack) extension. */
@Mixin(ItemStack.class)
public abstract class RuleBreakerDurabilityMixin {
 @Inject(method="isDamageableItem",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$weaponBow(CallbackInfoReturnable<Boolean> result){if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow)result.setReturnValue(false);}
 @Inject(method="getMaxDamage", at=@At("HEAD"), cancellable=true)
 private void mahoutsukai$durability(CallbackInfoReturnable<Integer> result) {
  if (((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.rulebreaker.RuleBreaker)
   result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.RULE_BREAKER_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys)
   result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.PROXIMITY_PROJECTION_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.RIPPER_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || ((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan || ((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.emrys.StaffEmrys)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.WEAPON_SHOOTER_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.FIRST_SORCERY_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.replica.Replica)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.REPLICA_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.NOBU_DURABILITY);
  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)
   result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.CLARENT_DURABILITY);
 }
}
