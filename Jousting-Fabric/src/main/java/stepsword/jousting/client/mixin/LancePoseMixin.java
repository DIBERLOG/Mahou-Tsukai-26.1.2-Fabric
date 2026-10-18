package stepsword.jousting.client.mixin;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(HumanoidModel.class)
public abstract class LancePoseMixin {
 @Inject(method="poseRightArm",at=@At("HEAD"),cancellable=true)private void lance$right(HumanoidRenderState state,CallbackInfo ci){if(pose(state,true))ci.cancel();}
 @Inject(method="poseLeftArm",at=@At("HEAD"),cancellable=true)private void lance$left(HumanoidRenderState state,CallbackInfo ci){if(pose(state,false))ci.cancel();}
 private boolean pose(HumanoidRenderState state,boolean right){
  if(!state.isUsingItem||!state.isPassenger||state.useItemHand!=net.minecraft.world.InteractionHand.MAIN_HAND)return false;
  boolean usedRight=state.mainArm==net.minecraft.world.entity.HumanoidArm.RIGHT;
  if(right!=usedRight||!((right?state.rightHandItemStack:state.leftHandItemStack).getItem() instanceof stepsword.jousting.item.ItemLance))return false;
  var model=(HumanoidModel<?>)(Object)this;var arm=right?model.rightArm:model.leftArm;arm.xRot=arm.yRot=arm.zRot=0;return true;
 }
}
