package stepsword.mahoutsukai.item.spells.secret;

import java.util.HashMap;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.entity.mahoujin.FallenDownBeamEntity;
import stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class FallenDownSpellScroll extends SpellScroll {
   public static HashMap<UUID, FallenDownSpellScroll.FallenDownUserStorage> storages = new HashMap<>();
   private String MODE_TAG = "MAHOUTSUKAI_BEAM_MODE_ON";

   public FallenDownSpellScroll() {
      super("fallen_down");
   }

   @Override
   public int getInitialManaCost() {
      return 0;
   }

   @NotNull
   @Override
   public InteractionResult use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isDiscrete()) {
         if (!world.isClientSide()) {
            Utils.setMode(stack, (Utils.getMode(stack) + 1) % 2);
            int mode = Utils.getMode(stack);
            MutableComponent tt;
            if (mode == 0) {
               tt = Component.translatable("mahoutsukai.fallendown.burial");
            } else {
               tt = Component.translatable("mahoutsukai.fallendown.beam");
            }

            player.sendOverlayMessage(tt);
         }

         return InteractionResult.SUCCESS;
      } else {
         player.startUsingItem(hand);
         return InteractionResult.SUCCESS;
      }
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      if (!player.level().isClientSide()) {
         if (storages.containsKey(player.getUUID())) {
            FallenDownSpellScroll.FallenDownUserStorage storage = storages.get(player.getUUID());
            if ((storage.beam == null || !storage.beam.isAlive()) && (storage.fd == null || !storage.fd.isAlive())) {
               storages.remove(player.getUUID());
            }
         }

         if (storages.containsKey(player.getUUID())) {
            FallenDownSpellScroll.FallenDownUserStorage storage = storages.get(player.getUUID());
         } else if (Utils.getMode(stack) == 0) {
            FallenDownSpellScroll.FallenDownUserStorage storage = new FallenDownSpellScroll.FallenDownUserStorage();
            storages.put(player.getUUID(), storage);
            storage.fd = new FallenDownEntity(player.level(), player);
            storage.fd.setPos(player.getX(), player.getY(), player.getZ());
            player.level().addFreshEntity(storage.fd);
         } else {
            FallenDownSpellScroll.FallenDownUserStorage storage = new FallenDownSpellScroll.FallenDownUserStorage();
            storages.put(player.getUUID(), storage);
            storage.beam = new FallenDownBeamEntity(player.level(), player);
            storage.beam.setPos(player.getX(), player.getY(), player.getZ());
            player.level().addFreshEntity(storage.beam);
         }
      }
   }

   @Override
   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide()) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (entityLiving instanceof Player user && matchCaster(user, scrollMahou) && this.shouldConsume(user, scrollMahou, consume, stack)) {
            stack.shrink(1);
         }
      }
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      if (storages.containsKey(user.getUUID())) {
         FallenDownSpellScroll.FallenDownUserStorage storage = storages.get(user.getUUID());
         if (storage.fd != null) {
            storage.fd.discard();
         }

         if (storage.beam != null) {
            storage.beam.discard();
         }

         storages.remove(user.getUUID());
         return true;
      } else {
         return false;
      }
   }

   public class FallenDownUserStorage {
      FallenDownEntity fd;
      FallenDownBeamEntity beam;
   }
}
