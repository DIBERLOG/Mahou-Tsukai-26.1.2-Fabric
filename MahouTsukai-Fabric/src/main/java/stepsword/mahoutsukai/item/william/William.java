package stepsword.mahoutsukai.item.william;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.entity.mahoujin.WilliamEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.util.Utils;

public class William extends WrittenBookItem {
   public static HashMap<BlockPos, UUID> awaitingLecterns = new HashMap<>();

   public William() {
      super(ItemBase.withRegisteredId(new Properties()).stacksTo(1));
   }

   public static int itemWilliamPlethora(Player player) {
      int best = 0;
      int worst = 0;

      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack stack = player.getInventory().getItem(i);
         int mana = getManaValue(stack);
         if (mana > 0 && mana > best) {
            best = mana;
         }

         if (mana < 0 && mana < worst) {
            worst = mana;
         }
      }

      return best + worst;
   }

   public static int getManaValue(ItemStack stack) {
      if (stack.is(ModItems.william.get())) {
         FictionMahou mahou = Utils.getFictionMahou(stack);
         int fame = mahou.getFame();
         int style = mahou.getStyle();
         return Math.max(0, fame) * style;
      } else {
         return 0;
      }
   }

   public static void williamBookTraded(Player player,MerchantOffer offer) {
      MahouSavedData data = Utils.getWorldMahou(player.level());
      if (offer.getCostA().getItem() instanceof WrittenBookItem) {
         String s = WrittenEvaluation.getBookText(offer.getCostA());
         data.addFameIfExists(1, s, (ServerLevel)player.level());
      }

      if (offer.getCostB().getItem() instanceof WrittenBookItem) {
         String s = WrittenEvaluation.getBookText(offer.getCostB());
         data.addFameIfExists(1, s, (ServerLevel)player.level());
      }

      if (offer.getResult().getItem() instanceof WrittenBookItem) {
         String s = WrittenEvaluation.getBookText(offer.getResult());
         data.addFameIfExists(1, s, player.getUUID(), (ServerLevel)player.level());
      }
   }

   public static void createWilliamCheck(Player player) {
      if (awaitingLecterns != null) {
         Level level = player.level();
         boolean completeDicts = DictionaryDownloader.allDictionariesFinished();
         if (completeDicts) {
            ArrayList<BlockPos> toRemove = new ArrayList<>();

            for (BlockPos p : awaitingLecterns.keySet()) {
               if (awaitingLecterns.get(p).equals(player.getUUID())) {
                  BlockState bs = level.getBlockState(p);
                  Block b = bs.getBlock();
                  if (b == Blocks.LECTERN && (Boolean)bs.getValue(LecternBlock.HAS_BOOK) && level.getBlockEntity(p) instanceof LecternBlockEntity lbe) {
                     ItemStack stack = lbe.getBook();
                     if (stack.getItem() == Items.WRITTEN_BOOK) {
                        String s = WrittenEvaluation.getBookText(stack);
                        if (!s.isEmpty()) {
                           ItemStack out = Utils.getWorldMahou(level)
                              .addPlayerFictionIfDifferent(
                                 s, awaitingLecterns.get(p), (WrittenBookContent)stack.get(DataComponents.WRITTEN_BOOK_CONTENT), player
                              );
                           if (out != ItemStack.EMPTY) {
                              WrittenBookContent wbc = (WrittenBookContent)stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
                              if (wbc != null) {
                                 out.set(DataComponents.WRITTEN_BOOK_CONTENT, wbc);
                              }

                              lbe.setBook(out);
                           }
                        }
                     }
                  }

                  toRemove.add(p);
               }
            }

            for (BlockPos p : toRemove) {
               awaitingLecterns.remove(p);
            }
         }
      }
   }

   public static void createWilliam(Player player,ItemStack stack,BlockPos p) {
      BlockState state = player.level().getBlockState(p);
      if (state.getBlock() == Blocks.LECTERN
         && player.level().getBlockEntity(p) instanceof LecternBlockEntity lbe
         && stack.getItem() == Items.WRITTEN_BOOK
         && StrengtheningSpellEffect.getStrengthened(stack) > 0
         && !lbe.hasBook()) {
         DictionaryDownloader.startAllDownloaders(a -> {});
         if (!awaitingLecterns.containsKey(p)) {
            awaitingLecterns.put(p, player.getUUID());
         }
      }
   }

   public static void updateFame(Level worldIn, ItemStack stack) {
      if (!worldIn.isClientSide() && stack.getItem() instanceof WrittenBookItem) {
         FictionMahou mahou = Utils.getFictionMahou(stack);
         MahouSavedData data = Utils.getWorldMahou(worldIn);
         mahou.setFame(data.addFameIfExists(0, WrittenEvaluation.getBookText(stack), (ServerLevel)worldIn));
         Utils.setFictionMahou(stack, mahou);
      }
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      ItemStack stack = player.getItemInHand(handIn);
      updateFame(worldIn, stack);
      if (player.isDiscrete() && handIn == InteractionHand.MAIN_HAND && !worldIn.isClientSide()) {
         BlockHitResult result = getPlayerPOVHitResult(worldIn, player, Fluid.SOURCE_ONLY);
         ItemStack r = player.getMainHandItem().copy();
         WilliamEntity william = new WilliamEntity(worldIn, player, 1.0F, r);
         BlockPos p = result.getBlockPos().relative(result.getDirection());
         william.setPos(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
         worldIn.addFreshEntity(william);
         player.getMainHandItem().shrink(1);
         return InteractionResult.SUCCESS;
      } else {
         return super.use(worldIn, player, handIn);
      }
   }

   public static boolean isRlyeh(ItemStack stack) {
      return stack != null
         && stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT)
         && stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT) != null
         && ((FictionMahou)stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT)).style < 0;
   }


}