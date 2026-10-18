package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stepsword.mahoutsukai.potion.ReversionEyesPotion;

@Mixin(WitherBoss.class)
public abstract class MixinWitherBoss {
   @Redirect(
      method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
   )
   private Entity mahouAllowPoweredWitherProjectileDamage(DamageSource source) {
      Entity directEntity = source.getDirectEntity();
      WitherBoss wither = (WitherBoss)(Object)this;
      return (directEntity instanceof AbstractArrow || directEntity instanceof WindCharge) && ReversionEyesPotion.isEntityReverted(wither)
         ? null
         : directEntity;
   }
}
