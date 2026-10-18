package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ItemEntity.class)
public abstract class GeasPickupMixin {
 @Inject(method="playerTouch",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$geasPickup(Player player,CallbackInfo callback) {
  if(!player.level().isClientSide() && stepsword.mahoutsukai.effects.secret.GeasEffect.geasItem(player,(ItemEntity)(Object)this)) callback.cancel();
 }
 @Inject(method="hurtServer",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;onDestroyed(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
 private void mahoutsukai$burnGeas(net.minecraft.server.level.ServerLevel world, net.minecraft.world.damagesource.DamageSource source,
    float amount,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> callback) {
  var entity=(ItemEntity)(Object)this;
  if(entity.getItem().getItem() instanceof stepsword.mahoutsukai.item.spells.secret.GeasSpellScroll scroll) scroll.onDestroyed(entity,source);
 }
}
