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
      tooltip.accept(Component.translatable(stepsword.mahoutsukai.fabric.SpellCasting.summonsWeapon(stack)?"mahoutsukai.cast.weapon_hint":"mahoutsukai.cast.scroll_hint",stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(stack)/20.0));
      if(getInitialManaCost()>0)tooltip.accept(Component.translatable("mahoutsukai.cast.price",PlayerManaManager.spellCost(getInitialManaCost())));
      if(stack.getItem() instanceof stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll)tooltip.accept(Component.translatable("mahoutsukai.help.fallen_down"));
      if(stack.getItem() instanceof stepsword.mahoutsukai.item.spells.eyes.BlackFlameEyesSpellScroll)tooltip.accept(Component.translatable("mahoutsukai.black_flame.stop_tooltip"));
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
      if(getUseDuration(stack,entityLiving)-timeLeft<stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(stack)){
       if(entityLiving instanceof Player player){stepsword.mahoutsukai.fabric.SpellCasting.cancel(player);if(this instanceof stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll)stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll.cancelCharge(player);}
       return false;
      }
      this.useAction(stack, worldIn, entityLiving, true);
      if(entityLiving instanceof Player player)stepsword.mahoutsukai.fabric.SpellCasting.cancel(player);
      return true;
   }

   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide()) {
         int cost=getInitialManaCost();
         ScrollMahou binding=Utils.getScrollMahou(stack);
         if(entityLiving instanceof Player user && binding!=null && matchCaster(user,binding)){
          if(!PlayerManaManager.canPay(user,cost)){user.sendOverlayMessage(Component.translatable("mahoutsukai.cast.no_mana"));return;}
          CastOutcome[] outcome={new CastOutcome(false,false)};
          if(PlayerManaManager.castPaid(user,cost,()->{outcome[0]=cast(user,binding,stack);return outcome[0].success();})){
           if(consume && outcome[0].consumeScroll() && (!user.isCreative()||MTConfig.CREATIVE_MODE_SPELLS))stack.shrink(1);
           stepsword.mahoutsukai.fabric.SpellCasting.completed(user,this);
          }else user.sendOverlayMessage(Component.translatable("mahoutsukai.cast.invalid_target"));
         }
      }
   }

   @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){stepsword.mahoutsukai.fabric.SpellCasting.tick(level,entity,stack,remaining);}

   public record CastOutcome(boolean success,boolean consumeScroll){}
   public CastOutcome cast(Player user,ScrollMahou binding,ItemStack stack){boolean success=doSpell(user,binding,stack);return new CastOutcome(success,success);}

   public boolean shouldConsume(Player user, ScrollMahou scrollMahou, boolean consume, ItemStack stack) {
      boolean success=this.doSpell(user,scrollMahou,stack);
      if(success)stepsword.mahoutsukai.fabric.SpellCasting.completed(user,this);
      return success && (!user.isCreative() || MTConfig.CREATIVE_MODE_SPELLS) && consume;
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
