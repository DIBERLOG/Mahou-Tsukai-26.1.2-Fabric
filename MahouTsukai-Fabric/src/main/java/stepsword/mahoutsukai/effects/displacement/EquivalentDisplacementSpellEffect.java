package stepsword.mahoutsukai.effects.displacement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.tile.displacement.EquivalentDisplacementMahoujinTileEntity;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class EquivalentDisplacementSpellEffect {
   public static void equivalentDisplacementShiftRightClick(Player player, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, InteractionHand hand) {
      if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
         BlockEntity te = world.getBlockEntity(pos);
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null && player.isDiscrete()) {
            if (te instanceof EquivalentDisplacementMahoujinTileEntity mte) {
               if (player.getUUID().equals(mte.getCasterUUID())) {
                  BlockDim bd = mahou.getMemorizedEDMTE();
                  if (bd != null && bd.pos != null) {
                     if (!bd.pos.equals(mte.getBlockPos()) || !EffectUtil.compareDimensions(bd.dim, EffectUtil.getDimension(player.level()))) {
                        if (EffectUtil.getNewDimensionByName((ServerLevel)player.level(), bd.dim).getBlockEntity(bd.pos) instanceof EquivalentDisplacementMahoujinTileEntity targette
                           )
                         {
                           if (player.getUUID().equals(targette.getCasterUUID())) {
                              targette.setTarget(new BlockDim(pos, player.level().dimension().identifier()));
                           }

                           mte.setTarget(new BlockDim(bd.pos, bd.dim));
                           mahou.setMemorizedEDMTE(null);
                           player.sendOverlayMessage(
                              Component.literal(ChatFormatting.RED + "Binding circle to " + bd.pos.toString() + " in dimension " + bd.dim + ".")
                           );
                        } else {
                           mahou.setMemorizedEDMTE(null);
                           player.sendOverlayMessage(Component.literal(ChatFormatting.RED + "(MahouTsukai) Cleared saved circle for binding."));
                        }
                     }
                  } else {
                     bd = new BlockDim(pos, EffectUtil.getDimension(player.level()));
                     mahou.setMemorizedEDMTE(bd);
                     player.sendOverlayMessage(Component.literal(ChatFormatting.GREEN + "Saving circle for binding."));
                  }
               }
            } else if (mahou.getMemorizedEDMTE() != null) {
               mahou.setMemorizedEDMTE(null);
               player.sendOverlayMessage(Component.literal(ChatFormatting.RED + "(MahouTsukai) Cleared saved circle for binding."));
            }
         }
      }
   }
}
