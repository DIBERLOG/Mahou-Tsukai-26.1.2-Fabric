package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.projection.ProjectionSpellEffect;

@Mixin(AnvilMenu.class)
public abstract class ProjectionAnvilMixin {
    @Shadow @Final private DataSlot cost;

    @Inject(method="createResult",at=@At("HEAD"),cancellable=true)
    private void mahoutsukai$cancelProjectedAnvil(CallbackInfo ci) {
        var menu=(AnvilMenu)(Object)this;
        if(MTConfig.PROJECTION_ANVIL_CANCEL && (ProjectionSpellEffect.getProjected(menu.getSlot(0).getItem())
            || ProjectionSpellEffect.getProjected(menu.getSlot(1).getItem()))) {
            menu.getSlot(2).set(ItemStack.EMPTY);
            cost.set(0);
            menu.broadcastChanges();
            ci.cancel();
        }
    }

    @Inject(method="createResult",at=@At("TAIL"))
    private void mahoutsukai$limitProjectedEnchantments(CallbackInfo ci) {
        var menu=(AnvilMenu)(Object)this;
        var output=menu.getSlot(2).getItem();
        ProjectionSpellEffect.projectionRepairAnvil(menu.getSlot(0).getItem(),menu.getSlot(1).getItem(),output);
        menu.broadcastChanges();
    }
}
