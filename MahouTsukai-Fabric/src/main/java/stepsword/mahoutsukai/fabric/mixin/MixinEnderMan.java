package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stepsword.mahoutsukai.potion.ReversionEyesPotion;

@Mixin(EnderMan.class)
public abstract class MixinEnderMan {
   @Redirect(
      method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
   )
   private boolean mahouAllowProjectileDamage(DamageSource source, TagKey<DamageType> tag) {
      EnderMan enderman = (EnderMan)(Object)this;
      return tag.equals(DamageTypeTags.IS_PROJECTILE) && ReversionEyesPotion.isEntityReverted(enderman) ? false : source.is(tag);
   }

   @Redirect(
      method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
   )
   private Entity mahouSkipThrownProjectileDodge(DamageSource source) {
      Entity directEntity = source.getDirectEntity();
      EnderMan enderman = (EnderMan)(Object)this;
      return directEntity instanceof Projectile && ReversionEyesPotion.isEntityReverted(enderman) ? null : directEntity;
   }

   @Redirect(
      method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;")
   )
   private Entity mahouPreventPostHitProjectileTeleport(DamageSource source) {
      Entity sourceEntity = source.getEntity();
      EnderMan enderman = (EnderMan)(Object)this;
      return (Entity)(sourceEntity == null && source.getDirectEntity() instanceof Projectile && ReversionEyesPotion.isEntityReverted(enderman)
         ? enderman
         : sourceEntity);
   }
}
