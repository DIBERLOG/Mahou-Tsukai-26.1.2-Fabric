package stepsword.mahoutsukai.block.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.FaeEssence;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.ScrollGiver;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class SingleUseMahoujinBlockTileEntity<T extends MahoujinTileEntity> extends MahoujinBlockTileEntity<T> {
   public SingleUseMahoujinBlockTileEntity(String name) {
      super(name);
   }

   public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result) {
      if (!world.isClientSide()) {
         BlockEntity te = world.getBlockEntity(pos);
         if (te instanceof ScrollGiver sg && te instanceof MahoujinTileEntity mte && mte.hasCloth()) {
            ItemStack stack = sg.getItemToGive();
            if (stack != null && stack.getItem() instanceof SpellScroll) {
               stack.setCount(1);
               ScrollMahou mahou = Utils.getScrollMahou(stack);
               if (mte.isFay()) {
                  mahou.setCasterUUID(FaeEssence.faeID);
                  mahou.setCasterName("The Fae");
               } else if (mte.getCaster() != null) {
                  mahou.setCasterUUID(mte.getCaster().getUUID());
                  mahou.setCasterName(mte.getCaster().getDisplayName().getString());
               }

               Utils.setScrollMahou(stack, mahou);
               popResource(world, pos, stack);
            }

            world.setBlockAndUpdate(pos, EffectUtil.AIR);
         }
      }

      return InteractionResult.SUCCESS;
   }
}
