package stepsword.mahoutsukai.visualcheck.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.client.renderer.ItemInHandRenderer.class)
public abstract class HandTransformCapture {
 @Inject(method="renderArmWithItem",at=@At("HEAD"))
 private void parent(net.minecraft.client.player.AbstractClientPlayer player,float partial,float pitch,net.minecraft.world.InteractionHand hand,float swing,net.minecraft.world.item.ItemStack stack,float equip,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,int light,CallbackInfo ci){stepsword.mahoutsukai.LiveWeaponHandChecks.before(hand,stack,equip,pose);}
 @Inject(method="renderItem",at=@At("HEAD"))
 private void capture(net.minecraft.world.entity.LivingEntity owner,net.minecraft.world.item.ItemStack stack,net.minecraft.world.item.ItemDisplayContext display,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,int light,CallbackInfo ci){
  if(stepsword.mahoutsukai.WeaponHandChecks.capturing)stepsword.mahoutsukai.WeaponHandChecks.matrix=new org.joml.Matrix4f(pose.last().pose());
  stepsword.mahoutsukai.LiveWeaponHandChecks.item(stack,display,pose);
 }
}
