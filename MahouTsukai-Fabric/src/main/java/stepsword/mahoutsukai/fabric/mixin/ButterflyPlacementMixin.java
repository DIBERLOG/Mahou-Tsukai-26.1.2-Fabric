package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
@Mixin(BlockItem.class)
public abstract class ButterflyPlacementMixin {
 @WrapMethod(method="place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;")
 private InteractionResult mahoutsukai$place(BlockPlaceContext context,Operation<InteractionResult> original){
  var p=context.getPlayer();var world=context.getLevel();var pos=context.getClickedPos();var before=world.getBlockState(pos);var consumed=context.getItemInHand().copyWithCount(1);
  boolean foresight=p!=null && !world.isClientSide() && p.hasEffect(stepsword.mahoutsukai.potion.ModEffects.FORESIGHT);
  var result=original.call(context);var placed=world.getBlockState(pos);
  if(foresight && result.consumesAction() && !before.equals(placed) && stepsword.mahoutsukai.potion.ForesightPotion.deferPlace(p,pos,context.getClickedFace(),placed,consumed))world.setBlockAndUpdate(pos,before);
  return result;
 }
}
