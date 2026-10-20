package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.effects.projection.ClarentEffect;
import stepsword.mahoutsukai.item.clarent.Clarent;

/** Cancel before armor, bypass tags, retaliation and spell side effects; no frontal arc restriction. */
@Mixin(value=LivingEntity.class,priority=2000)
public abstract class ClarentGuardMixin {
 @Inject(method="hurtServer",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$guard(ServerLevel level,DamageSource source,float amount,CallbackInfoReturnable<Boolean> result){
  var self=(LivingEntity)(Object)this;
  if(self.isAlive()&&amount>0&&self.isUsingItem()&&self.getUseItem().getItem() instanceof Clarent){
   ClarentEffect.clarentLivingAttack(self,source,Float.isFinite(amount)?amount:Clarent.cap());
   result.setReturnValue(false);
  }
 }
}
