package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Original Abyss appearance: substitute sculk only in the chunk rendering view. */
@Mixin(RenderSectionRegion.class)
public abstract class AbyssWorldMixin {
 @Inject(method="getBlockState",at=@At("RETURN"),cancellable=true)
 private void mahoutsukai$abyss(BlockPos pos,CallbackInfoReturnable<BlockState> result){
  if(stepsword.mahoutsukai.client.ClientHandler.tint&&!result.getReturnValue().isAir())result.setReturnValue(Blocks.SCULK.defaultBlockState());
 }
}
