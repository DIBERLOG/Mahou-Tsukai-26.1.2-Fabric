package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class WeaponPoseMixin {
 private static HumanoidArm usedArm(HumanoidRenderState state){return state.useItemHand==InteractionHand.MAIN_HAND?state.mainArm:state.mainArm.getOpposite();}
 private static net.minecraft.world.item.ItemStack usedStack(HumanoidRenderState state){return usedArm(state)==HumanoidArm.RIGHT?state.rightHandItemStack:state.leftHandItemStack;}
 @Inject(method="setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",at=@At("HEAD"))
 private void mahou$twoHanded(HumanoidRenderState state,CallbackInfo ci){
  if(state.isUsingItem && usedStack(state).getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){
   // This native pose has the same off-hand exclusion as the original extended rifle pose.
   if(usedArm(state)==HumanoidArm.RIGHT)state.rightArmPose=HumanoidModel.ArmPose.CROSSBOW_HOLD;
   else state.leftArmPose=HumanoidModel.ArmPose.CROSSBOW_HOLD;
  }
 }
 @Inject(method="poseRightArm",at=@At("HEAD"),cancellable=true)
 private void mahou$right(HumanoidRenderState state,CallbackInfo ci){if(pose(state,HumanoidArm.RIGHT))ci.cancel();}
 @Inject(method="poseLeftArm",at=@At("HEAD"),cancellable=true)
 private void mahou$left(HumanoidRenderState state,CallbackInfo ci){if(pose(state,HumanoidArm.LEFT))ci.cancel();}
 private boolean pose(HumanoidRenderState state,HumanoidArm arm){
  if(!state.isUsingItem || usedArm(state)!=arm)return false;
  var model=(HumanoidModel<?>)(Object)this;var stack=usedStack(state);
  if(stack.getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){stepsword.mahoutsukai.render.pose.RiflePose.applyTransform(model,state,arm);return true;}
  if(state.useItemHand==InteractionHand.MAIN_HAND && stack.getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent){stepsword.mahoutsukai.render.pose.ClarentPose.applyTransform(model,state,arm);return true;}
  return false;
 }
}
