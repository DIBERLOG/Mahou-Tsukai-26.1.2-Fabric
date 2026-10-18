package stepsword.mahoutsukai.fabric.mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MysticCodeHandMixin {
 @Inject(method="renderArmWithItem",at=@At("HEAD"),cancellable=true)
 private void mahou$hand(AbstractClientPlayer player,float partial,float pitch,InteractionHand hand,float swing,ItemStack stack,float equip,PoseStack pose,SubmitNodeCollector collector,int light,CallbackInfo ci){
  if(player.isInvisible() || !(player.getItemInHand(hand).getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode))return;
  stepsword.mahoutsukai.client.MysticCodeClientEffect.renderArmFirstPerson(pose,collector,light,0,hand==InteractionHand.MAIN_HAND?swing:0,hand==InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite());
  if(hand==InteractionHand.MAIN_HAND && equip>0)ci.cancel();
 }
}
