package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
@Mixin(ItemModelResolver.class)
public abstract class NativeItemUseContextMixin {
 @WrapMethod(method="updateForTopItem")
 private void mahoutsukai$context(ItemStackRenderState state,ItemStack stack,ItemDisplayContext display,Level level,ItemOwner owner,int seed,Operation<Void> original){stepsword.mahoutsukai.client.ItemUseRenderBridge.push(owner instanceof LivingEntity living?living:null,stack,display);try{original.call(state,stack,display,level,owner,seed);}finally{stepsword.mahoutsukai.client.ItemUseRenderBridge.popContext();}}
}
