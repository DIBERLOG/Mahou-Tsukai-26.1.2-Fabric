package stepsword.mahoutsukai.item.spells.boundary;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.FaeEssence;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.boundary.BoundaryMahoujinTileEntity;
import stepsword.mahoutsukai.util.UnorderedList;
import stepsword.mahoutsukai.util.Utils;

public class BoundarySpellScroll extends SpellScroll {
   public BoundarySpellScroll(String boundaryName) {
      super("boundary_" + boundaryName);
   }

   @Override
   public int getInitialManaCost() {
      return 0;
   }

   public BlockBase getBoundaryBlock() {
      return null;
   }

   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
   }

   @Override
   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
   }

   @Override
   public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
      return InteractionResult.SUCCESS;
   }

   public InteractionResult useOn(UseOnContext ctx) {
      ItemStack stack = ctx.getPlayer().getItemInHand(ctx.getHand());
      return this.boundaryScrollUse(ctx.getPlayer(), ctx.getLevel(), stack, ctx.getClickedPos(), ctx.getClickedFace(), true);
   }

   public InteractionResult boundaryScrollUse(Player player, Level worldIn, ItemStack stack, BlockPos pos, Direction facing, boolean consume) {
      if (!worldIn.isClientSide()) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (matchCaster(player, scrollMahou)) {
            BlockPos placeHere = pos.relative(facing);
            if (!Utils.isBlockAir(worldIn, placeHere)) {
               return InteractionResult.FAIL;
            }

            this.drawBoundary(worldIn, placeHere, scrollMahou, player);
            if ((!player.isCreative() || MTConfig.CREATIVE_MODE_SPELLS) && stack.getItem() instanceof BoundarySpellScroll && consume) {
               stack.shrink(1);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.SUCCESS;
   }

   public void drawBoundary(final Level world, final BlockPos placeHere, final ScrollMahou mahou, final Player player) {
      if (!world.isClientSide()) {
         ((ServerLevel)world).getServer().execute(new Runnable() {
            @Override
            public void run() {
               if (BoundarySpellScroll.this.getBoundaryBlock() != null && Utils.isBlockAir(world, placeHere)) {
                  world.setBlockAndUpdate(placeHere, BoundarySpellScroll.this.getBoundaryBlock().defaultBlockState());
                  BlockEntity te = world.getBlockEntity(placeHere);
                  UnorderedList m = null;

                  for (UnorderedList r : MahoujinRecipeRegistrar.recipes.keySet()) {
                     if (MahoujinRecipeRegistrar.recipes.get(r) == BoundarySpellScroll.this.getBoundaryBlock()) {
                        m = r;
                     }
                  }

                  if (te instanceof MahoujinTileEntity && m != null && mahou != null) {
                     UUID casterUUID = mahou.getCasterUUID().isEmpty() ? null : mahou.getCasterUUID().get();
                     if ((casterUUID == null || casterUUID.equals(new UUID(0L, 0L))) && player != null) {
                        casterUUID = player.getUUID();
                     }

                     ((MahoujinTileEntity)te).setCasterUUID(casterUUID);
                     ((MahoujinTileEntity)te).setCloth(false);
                     ((MahoujinTileEntity)te).setFay(casterUUID.equals(FaeEssence.faeID));
                     ((MahoujinTileEntity)te).setCatalysts(m);
                  }

                  if (te instanceof BoundaryMahoujinTileEntity && player != null) {
                     ((BoundaryMahoujinTileEntity)te).toggle(player);
                     stepsword.mahoutsukai.fabric.SpellCasting.present(player,BoundarySpellScroll.this,net.minecraft.world.phys.Vec3.atCenterOf(placeHere));
                  }
               }
            }
         });
      }
   }
}
