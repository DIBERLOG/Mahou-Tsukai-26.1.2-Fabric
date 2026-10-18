package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.screens.inventory.BookViewScreen.class)
public abstract class WilliamBookPageMixin {
 @org.spongepowered.asm.mixin.Shadow private int currentPage;
 @org.spongepowered.asm.mixin.injection.Inject(method={"pageForward","pageBack"},at=@org.spongepowered.asm.mixin.injection.At("TAIL"))
 private void mahoutsukai$page(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.TurnedPagePacket(currentPage));}
 @org.spongepowered.asm.mixin.injection.Inject(method="setPage",at=@org.spongepowered.asm.mixin.injection.At("RETURN"))
 private void mahoutsukai$link(int page,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> ci){if(ci.getReturnValueZ())net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.TurnedPagePacket(currentPage));}
}
