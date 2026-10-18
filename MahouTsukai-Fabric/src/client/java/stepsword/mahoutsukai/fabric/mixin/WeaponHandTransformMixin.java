package stepsword.mahoutsukai.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Original applyForgeHandTransform keeps these active items at the base arm translation. */
@Mixin(ItemInHandRenderer.class)
public abstract class WeaponHandTransformMixin {
 @WrapOperation(method="renderArmWithItem",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;"))
 private ItemUseAnimation mahou$animation(ItemStack stack,Operation<ItemUseAnimation> original,@Local(argsOnly=true) AbstractClientPlayer player){
  if(player.isUsingItem() && player.getUseItemRemainingTicks()>0 && player.getUseItem().equals(stack) &&
   (stack.getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent || stack.getItem() instanceof stepsword.mahoutsukai.item.replica.Replica || stack.getItem() instanceof stepsword.mahoutsukai.item.spells.projection.TreasuryProjection.TreasuryProjectionGauntlet))return ItemUseAnimation.NONE;
  return original.call(stack);
 }
}
