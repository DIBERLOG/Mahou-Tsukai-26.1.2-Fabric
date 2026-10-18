package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stepsword.mahoutsukai.potion.ReversionEyesPotion;

@Mixin(Shulker.class)
public abstract class MixinShulker {
   @Redirect(
      method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
   )
   private Entity mahouAllowClosedShulkerArrowDamage(DamageSource source) {
      Entity directEntity = source.getDirectEntity();
      Shulker shulker = (Shulker)(Object)this;
      return directEntity instanceof AbstractArrow && ReversionEyesPotion.isEntityReverted(shulker) ? null : directEntity;
   }
}
