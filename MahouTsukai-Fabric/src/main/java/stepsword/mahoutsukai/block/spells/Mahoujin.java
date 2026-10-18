package stepsword.mahoutsukai.block.spells;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.UnorderedList;
import stepsword.mahoutsukai.util.Utils;

public class Mahoujin extends MahoujinBlockTileEntity<MahoujinTileEntity> {
   public Mahoujin() {
      super("mahoujin");
   }

   protected InteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!level.isClientSide()) {
         MahoujinTileEntity mahoujin = this.getTileEntity(level, pos);
         if (mahoujin != null && stack.getItem() instanceof PowderedCatalyst && mahoujin.getNumCatalysts() < 3) {
            mahoujin.setCasterUUID(player.getUUID());
            if (mahoujin.addCatalyst((PowderedCatalyst)stack.getItem())) {
               stack.shrink(1);
               this.transformToSpell(mahoujin, level, player, pos);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.TRY_WITH_EMPTY_HAND;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         MahoujinTileEntity mahoujin = this.getTileEntity(level, pos);
         if (mahoujin != null && mahoujin.getNumCatalysts() == 0) {
            IMahou mahou = Utils.getPlayerMahou(player);
            if (mahou != null && mahou.getLastRecipe() != null && mahou.getLastRecipe().size() == 3) {
               if (stepsword.mahoutsukai.fabric.RitualInventory.repeat(player, mahoujin, mahou.getLastRecipe(), mahou.getLastRecipeCloth())) {
                  this.transformToSpell(mahoujin, level, player, pos);
                  return InteractionResult.SUCCESS;
               }
            }
         }
      }

      return InteractionResult.FAIL;
   }

   public void transformToSpell(MahoujinTileEntity mahoujin, Level worldIn, Player playerIn, BlockPos pos) {
      if (mahoujin.getNumCatalysts() == 3) {
         BlockBase newBlock = MahoujinRecipeRegistrar.recipes.get(mahoujin.getCatalysts());
         boolean banned = EffectUtil.inBlockBlacklist(newBlock, MTConfig.SPELL_BLACKLIST, worldIn);
         if (newBlock != null && !banned) {
            UnorderedList catalystsCarry = mahoujin.getCatalysts();
            UUID casterUUIDCarry = mahoujin.getCasterUUID();
            boolean cloth = mahoujin.hasCloth();
            boolean needsCloth = MahoujinRecipeRegistrar.needsCloth.containsKey(catalystsCarry) && MahoujinRecipeRegistrar.needsCloth.get(catalystsCarry);
            boolean fay = mahoujin.isFay();
            if (!needsCloth || cloth) {
               worldIn.setBlockAndUpdate(pos, newBlock.defaultBlockState());
               MahoujinTileEntity mte = (MahoujinTileEntity)worldIn.getBlockEntity(pos);
               if (mte != null) {
                  mte.setCatalysts(catalystsCarry);
                  mte.setCasterUUID(casterUUIDCarry);
                  mte.setCloth(cloth);
                  mte.setFay(fay);
               }

               if(mte instanceof stepsword.mahoutsukai.tile.displacement.OrderedDisplacementMahoujinTileEntity ordered) ordered.setTeleporterId(worldIn.getGameTime());
               if(mte instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity chronal) chronal.setTime(0L);
               if(mte instanceof stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity alchemical)
                  alchemical.setPlacedTime(Math.floorMod(Math.floorMod(worldIn.getOverworldClockTime(),24000)/20*20+100,24000));
               IMahou mahou = Utils.getPlayerMahou(playerIn);
               if (mahou != null) {
                  mahou.setLastRecipeCloth(cloth);
                  mahou.setLastRecipe(catalystsCarry);
                  PlayerManaManager.updateClientMahou((ServerPlayer)playerIn, mahou);
               }
            }
         }
      }
   }

   public static String getClassOfSpell(UnorderedList lst) {
      if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_diamond"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_diamond")).getCatalystName()) >= 2) {
         return "mahoutsukai_projection";
      } else if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_ender"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_ender")).getCatalystName()) >= 2) {
         return "mahoutsukai_displacement";
      } else if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_quartz"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_quartz")).getCatalystName()) >= 2) {
         return "mahoutsukai_familiar";
      } else if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_gold"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_gold")).getCatalystName()) >= 2) {
         return "mahoutsukai_mystic";
      } else if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_iron"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_iron")).getCatalystName()) >= 2) {
         return "mahoutsukai_boundary";
      } else if (hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_emerald"))
         && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_emerald")).getCatalystName()) >= 2) {
         return "mahoutsukai_exchange";
      } else {
         return hasCatalyst(lst, (PowderedCatalyst)ModItems.REGISTERED.get("powdered_eye"))
               && lst.get(((PowderedCatalyst)ModItems.REGISTERED.get("powdered_eye")).getCatalystName()) >= 2
            ? "mahoutsukai_mystic_eyes"
            : "mahoutsukai_secret";
      }
   }

   public static boolean hasCatalyst(UnorderedList lst, PowderedCatalyst catalyst) {
      return lst != null && lst.getKeys() != null && catalyst != null ? lst.getKeys().contains(catalyst.getCatalystName()) : false;
   }

   @Override
   public Class<MahoujinTileEntity> getTileEntityClass() {
      return MahoujinTileEntity.class;
   }

   @Nullable
   public MahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MahoujinTileEntity(blockPos, blockState);
   }
}
