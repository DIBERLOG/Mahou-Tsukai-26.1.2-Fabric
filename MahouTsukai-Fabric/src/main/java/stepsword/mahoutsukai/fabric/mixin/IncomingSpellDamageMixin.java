package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
@Mixin(LivingEntity.class)
public abstract class IncomingSpellDamageMixin {
 @Inject(method="hurtServer",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"),cancellable=true)
 private void mahoutsukai$incoming(ServerLevel level,DamageSource source,float original,CallbackInfoReturnable<Boolean> ci,@Local(argsOnly=true) LocalFloatRef amount) {
  LivingEntity entity=(LivingEntity)(Object)this;
  stepsword.mahoutsukai.item.nobu.Nobu.nobuCreate(source);
  stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity.rhongomyniadLivingHurt(entity,source);
  if(source.is(net.minecraft.world.damagesource.DamageTypes.FALL) && stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect.inMarble(level)){ci.setReturnValue(false);return;}
  original=stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysLivingHurt(entity,source,original);amount.set(original);
  float modified=stepsword.mahoutsukai.potion.ImmunityExchangePotion.immunityExchangeLivingHurt(entity,source,amount.get());
  if(Float.isNaN(modified) || stepsword.mahoutsukai.effects.secret.GeasEffect.geasLivingHurt(entity,source) || stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenLivingDamage(entity,source)) { ci.setReturnValue(false); return; }
  stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationLivingDamage(entity,source,modified);
  stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingAttack(entity,source,modified);
  stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.strengtheningArmorEvent(entity);
  stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.breakContract(entity,source);
  if(entity instanceof net.minecraft.world.entity.player.Player && modified>0 && !stepsword.mahoutsukai.config.MTConfig.BLEEDING_FROM_DAGGER_ONLY)
   stepsword.mahoutsukai.util.EffectUtil.buff(entity,stepsword.mahoutsukai.potion.ModEffects.BLEEDING,false,100);
  modified=stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.strengtheningLivingHurt(entity,source,modified);
  modified=stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingHurt(entity,source,modified);
  stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect.realityMarbleLivingHurt(entity);
  modified=stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.ripperOnHit(entity,source,modified);
  amount.set(stepsword.mahoutsukai.effects.exchange.DamageExchangeSpellEffect.damageExchangeLivingHurt(entity,source,modified));
 }
}
