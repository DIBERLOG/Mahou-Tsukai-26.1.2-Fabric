package stepsword.mahoutsukai.fabric.mixin;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hide the secret formula from recipe books without changing its crafting logic. */
@Mixin(ShapelessRecipe.class)
public abstract class DoomRecipeSecrecyMixin implements CraftingRecipe {
 @Shadow public abstract ItemStack assemble(CraftingInput input);
 @Override public boolean isSpecial(){return assemble(CraftingInput.EMPTY).getItem() instanceof stepsword.mahoutsukai.doom.DoomScroll;}
 @Inject(method="display",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$hideDoomDisplay(CallbackInfoReturnable<List<RecipeDisplay>> result){
  if(assemble(CraftingInput.EMPTY).getItem() instanceof stepsword.mahoutsukai.doom.DoomScroll)result.setReturnValue(List.of());
 }
}
