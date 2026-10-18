package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(LivingEntity.class)
public abstract class BuffClearItemMixin {
    @WrapOperation(method="completeUsingItem",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack mahoutsukai$clearBuffs(ItemStack stack,Level level,LivingEntity entity,Operation<ItemStack> original){
        boolean clear=!level.isClientSide() && stepsword.mahoutsukai.util.EffectUtil.inItemBlacklist(stack.getItem(),stepsword.mahoutsukai.config.MTConfig.BUFF_CLEAR_ITEMS,level);
        ItemStack result=original.call(stack,level,entity);
        if(clear){
            if(entity instanceof net.minecraft.server.level.ServerPlayer player){
                stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).clearBuffs();
                stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(player,stepsword.mahoutsukai.util.Utils.getPlayerMahou(player));
            }
            stepsword.mahoutsukai.util.Utils.getLivingMahou(entity).clearBuffs();
        }
        return result;
    }
}
