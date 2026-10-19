package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LivingEntity.class)
public abstract class FoodManaMixin {
 @Unique private int mahoutsukai$nutrition;
 @Inject(method="completeUsingItem",at=@At("HEAD"))
 private void mahoutsukai$beforeFood(CallbackInfo ci){var food=((LivingEntity)(Object)this).getUseItem().get(net.minecraft.core.component.DataComponents.FOOD);mahoutsukai$nutrition=food==null?0:food.nutrition();}
 @Inject(method="completeUsingItem",at=@At("TAIL"))
 private void mahoutsukai$afterFood(CallbackInfo ci){if((Object)this instanceof net.minecraft.world.entity.player.Player p)stepsword.mahoutsukai.fabric.MageProgression.restoreFromFood(p,mahoutsukai$nutrition);mahoutsukai$nutrition=0;}
}
