package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
@Mixin(ItemStack.class)
public abstract class StrengthenedItemMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",at=@At("TAIL"))
 private void mahoutsukai$wear(int amount,net.minecraft.server.level.ServerLevel level,net.minecraft.server.level.ServerPlayer entity,java.util.function.Consumer<net.minecraft.world.item.Item> broken,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){
  ItemStack stack=(ItemStack)(Object)this;
  if(amount>0 && stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.getStrengthened(stack)>0)
   stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.reduceStrengthened(stack,1);
 }

 @ModifyReturnValue(method="isCorrectToolForDrops",at=@At("RETURN"))
 private boolean mahoutsukai$harvest(boolean original,BlockState state) {
  return stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.strengtheningHarvestCheck((ItemStack)(Object)this,state,original);
 }
 @ModifyExpressionValue(method={"forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V","forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V"},at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
 private Object mahoutsukai$sword(Object original) {
  return original instanceof ItemAttributeModifiers modifiers ? stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.attributes((ItemStack)(Object)this,stepsword.mahoutsukai.item.GrowSwordItem.attributes((ItemStack)(Object)this,modifiers)) : original;
 }
}
