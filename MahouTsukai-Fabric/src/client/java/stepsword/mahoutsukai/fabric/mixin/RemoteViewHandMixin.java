package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.blaze3d.vertex.PoseStack;
@Mixin(ItemInHandRenderer.class)
public abstract class RemoteViewHandMixin {
 @Inject(method="renderHandsWithItems",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$hide(float partial,PoseStack pose,SubmitNodeCollector collector,LocalPlayer player,int light,CallbackInfo ci){if(player!=null && (stepsword.mahoutsukai.client.MentalDisplacementClientEffect.mentalDisplacementRenderHand() || stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityRenderHand()))ci.cancel();}
}
