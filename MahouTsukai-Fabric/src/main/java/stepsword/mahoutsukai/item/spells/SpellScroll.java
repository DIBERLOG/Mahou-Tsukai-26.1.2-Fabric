package stepsword.mahoutsukai.item.spells;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.fabric.MahouIds;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class SpellScroll extends ItemBase {
   public SpellScroll(String name) {
      super(new Properties().component(DataComponents.SCROLL_COMPONENT, new ScrollMahou()));
   }

   public SpellScroll(Properties prop) {
      super(prop.component(DataComponents.SCROLL_COMPONENT, new ScrollMahou()));
   }

   public void appendHoverText(ItemStack stack, TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag pTooltipFlag) {
      super.appendHoverText(stack, pContext, display, tooltip, pTooltipFlag);
      Item item = stack.getItem();
      if (item instanceof SpellScroll && Utils.getScrollMahou(stack) != null) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (scrollMahou != null) {
            Optional<String> name = scrollMahou.getCasterName();
            if (name.isPresent()) {
               tooltip.accept(Component.literal("Bound to " + name.get()));
            } else {
               tooltip.accept(Component.literal("Bound to: ???"));
            }
         }
      }
   }

   public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
      playerIn.startUsingItem(handIn);
      return InteractionResult.SUCCESS;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BOW;
   }

   public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      this.useAction(stack, worldIn, entityLiving, true);
      return true;
   }

   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide()) {
         int cost = this.getInitialManaCost();
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (entityLiving instanceof Player user
            && scrollMahou != null
            && (
               scrollMahou.getCasterUUID().isEmpty()
                  || user.getUUID().equals(scrollMahou.getCasterUUID().get())
                  || scrollMahou.getCasterUUID().get().equals(MahouIds.FAE_UUID)
                  || user.isCreative()
            )
            && PlayerManaManager.drainMana(user, cost, false, false) == cost
            && this.shouldConsume(user, scrollMahou, consume, stack)) {
            stack.shrink(1);
         }
      }
   }

   public boolean shouldConsume(Player user, ScrollMahou scrollMahou, boolean consume, ItemStack stack) {
      return this.doSpell(user, scrollMahou, stack) && (!user.isCreative() || MTConfig.CREATIVE_MODE_SPELLS) && consume;
   }

   public int getInitialManaCost() {
      return 0;
   }

   public boolean doSpell(Player user, ScrollMahou mahou, ItemStack stack) {
      return false;
   }

   public static boolean matchCaster(Player user, ScrollMahou mahou) {
      if (mahou == null) {
         return true;
      } else if (user == null || user.isCreative()) {
         return true;
      } else if (mahou.getCasterUUID().isEmpty()) {
         return true;
      } else if (mahou.getCasterUUID().get().equals(new UUID(0L, 0L))) {
         return true;
      } else {
         return mahou.getCasterUUID().get().equals(MahouIds.FAE_UUID) ? true : user.getUUID().equals(mahou.getCasterUUID().get());
      }
   }
}
