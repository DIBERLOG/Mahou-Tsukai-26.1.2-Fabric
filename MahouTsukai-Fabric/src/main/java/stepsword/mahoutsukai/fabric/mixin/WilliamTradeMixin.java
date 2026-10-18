package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.inventory.MerchantResultSlot.class)
public abstract class WilliamTradeMixin {
 @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final private net.minecraft.world.inventory.MerchantContainer slots;
 @org.spongepowered.asm.mixin.injection.Inject(method="onTake",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void mahoutsukai$fame(net.minecraft.world.entity.player.Player player,net.minecraft.world.item.ItemStack stack,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){if(!player.level().isClientSide() && slots.getActiveOffer()!=null)stepsword.mahoutsukai.item.william.William.williamBookTraded(player,slots.getActiveOffer());}
}
