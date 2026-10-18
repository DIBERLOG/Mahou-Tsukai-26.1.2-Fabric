package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys;

/** Native equivalents of the original supportsEnchantment/isPrimaryItemFor extensions. */
@Mixin(Enchantment.class)
public abstract class ProximityEnchantmentMixin {
    @Inject(method="isSupportedItem",at=@At("HEAD"),cancellable=true)
    private void mahoutsukai$supportKeys(ItemStack stack,CallbackInfoReturnable<Boolean> ci) {
        if(stack.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery){ci.setReturnValue(false);return;}
        if(stack.getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){var description=((Enchantment)(Object)this).description();ci.setReturnValue(java.util.List.of("power","unbreaking","mending").stream().anyMatch(id->description.equals(net.minecraft.network.chat.Component.translatable("enchantment.minecraft."+id))));return;}
        if(stack.getItem() instanceof ProximityProjectionKeys) ci.setReturnValue(true);
    }
    @Inject(method="isPrimaryItem",at=@At("HEAD"),cancellable=true)
    private void mahoutsukai$ironSwordEnchantments(ItemStack stack,CallbackInfoReturnable<Boolean> ci) {
        if(stack.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery){ci.setReturnValue(false);return;}
        if(stack.getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){var description=((Enchantment)(Object)this).description();ci.setReturnValue(java.util.List.of("power","unbreaking","mending").stream().anyMatch(id->description.equals(net.minecraft.network.chat.Component.translatable("enchantment.minecraft."+id))));return;}
        if(stack.getItem() instanceof ProximityProjectionKeys)
            ci.setReturnValue(((Enchantment)(Object)this).isPrimaryItem(new ItemStack(Items.IRON_SWORD)));
    }
}
