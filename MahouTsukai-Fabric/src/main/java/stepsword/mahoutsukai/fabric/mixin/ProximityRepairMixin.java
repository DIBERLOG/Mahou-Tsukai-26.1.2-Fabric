package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys;

@Mixin(RepairItemRecipe.class)
public abstract class ProximityRepairMixin {
    @Inject(method="canCombine",at=@At("HEAD"),cancellable=true)
    private static void mahoutsukai$noKeyRepair(ItemStack left,ItemStack right,CallbackInfoReturnable<Boolean> ci) {
        if(left.getItem() instanceof ProximityProjectionKeys || right.getItem() instanceof ProximityProjectionKeys || left.getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || right.getItem() instanceof stepsword.mahoutsukai.item.caliburn.Caliburn || left.getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan || right.getItem() instanceof stepsword.mahoutsukai.item.morgan.Morgan) ci.setReturnValue(false);
    }
}
