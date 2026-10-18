package stepsword.mahoutsukai.item;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.Mahoujin;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class FaeEssence extends ItemBase {
   public static UUID faeID = UUID.fromString("64422db4-221d-425a-8d2b-27922662f22e");

   public FaeEssence() {
      super("fae_essence");
   }

   public InteractionResult useOn(UseOnContext ctx) {
      Level worldIn = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      Direction facing = ctx.getClickedFace();
      Player player = ctx.getPlayer();
      InteractionHand hand = ctx.getHand();
      if (!worldIn.isClientSide()) {
         BlockPos placeHere = pos.relative(facing);
         BlockState phbs = worldIn.getBlockState(placeHere);
         BlockState pbs = worldIn.getBlockState(pos);
         if (pbs.getBlock() == ModBlocks.spellClothBlock.get()) {
            this.drawFayCircle(worldIn, player, pos);
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof FaeEssence) {
               stack.shrink(1);
            }

            return InteractionResult.SUCCESS;
         } else if (Utils.isBlockAir(worldIn, placeHere)) {
            this.drawFayCircle(worldIn, player, placeHere);
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof FaeEssence) {
               stack.shrink(1);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void drawFayCircle(final Level world, final Player p, final BlockPos placeHere) {
      if (!world.isClientSide()) {
         ((ServerLevel)world).getServer().execute(new Runnable() {
            @Override
            public void run() {
               if (Utils.isBlockAir(world, placeHere) || world.getBlockState(placeHere).getBlock() == ModBlocks.spellClothBlock.get()) {
                  boolean clothFlag = world.getBlockState(placeHere).getBlock() == ModBlocks.spellClothBlock.get();
                  world.setBlockAndUpdate(placeHere, ((Mahoujin)ModBlocks.mahoujin.get()).defaultBlockState());
                  BlockEntity te = world.getBlockEntity(placeHere);
                  if (te instanceof MahoujinTileEntity) {
                     ((MahoujinTileEntity)te).setCasterUUID(FaeEssence.faeID);
                     ((MahoujinTileEntity)te).setCloth(clothFlag);
                     ((MahoujinTileEntity)te).setFay(true);
                  }

                  if (p != null) {
                     IMahou mahou = Utils.getPlayerMahou(p);
                     if (mahou != null) {
                        mahou.setHasMagic(true);
                        ((MahouTrigger)ModTriggers.MAHOUTSUKAI.get()).trigger((ServerPlayer)p);
                        PlayerManaManager.updateClientMahou((ServerPlayer)p, mahou);
                     }
                  }
               }
            }
         });
      }
   }
}
