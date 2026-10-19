package stepsword.mahoutsukai.item.attunedgems;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class AttunedGem extends ItemBase {
   private int MAX_MANA;

   public AttunedGem(String gem, int maxMana) {
      super(new Properties().component(DataComponents.GEM_COMPONENT, new GemMahou(0)), 1);
      this.MAX_MANA = maxMana;
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return true;
   }

   public int getBarWidth(ItemStack itemStack) {
      float zz = this.getDuraRatio(itemStack);
      return Math.round(zz * 13.0F);
   }

   public float getDuraRatio(ItemStack stack) {
      GemMahou gemMahou = Utils.getGemMahou(stack);
      float q;
      if (gemMahou != null) {
         q = gemMahou.getStoredMana();
      } else {
         q = this.getMaxMana();
      }

      return q / this.getMaxMana();
   }

   public int getBarColor(ItemStack itemStack) {
      float f = Math.max(0.0F, this.getDuraRatio(itemStack));
      return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
   }

   public int getMaxMana() {
      return this.MAX_MANA;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, display, tooltip, tooltipFlag);
      Item item = stack.getItem();
      if (item instanceof AttunedGem && Utils.getGemMahou(stack) != null) {
         GemMahou gemMahou = Utils.getGemMahou(stack);
         if (gemMahou != null) {
            String maxMana = ((AttunedGem)item).getMaxMana() + "";
            String storedMana = gemMahou.getStoredMana() + "";
            tooltip.accept(Component.literal(storedMana + " / " + maxMana));
         }
      }
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!world.isClientSide() && stack.getItem() instanceof AttunedGem && !player.isShiftKeyDown()) {
         this.charge(stack, player);
         triggerUpdate((ServerPlayer)player, stack);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void charge(ItemStack itemStack, Player player) {
      IMahou playerMahou = Utils.getPlayerMahou(player);
      GemMahou gemMahou = Utils.getGemMahou(itemStack);
      int storedMana = gemMahou.getStoredMana();
      double chargeRate = playerMahou.getChargeRate();
      int playerMaxMana = playerMahou.getMaxMana();
      int manaDifference = (int)Math.ceil(playerMaxMana * chargeRate);
      if (storedMana < this.getMaxMana()) {
         manaDifference = Math.min(manaDifference, this.getMaxMana() - storedMana);
         manaDifference = PlayerManaManager.drainManaExact(player, manaDifference, true, false);
         gemMahou.setStoredMana(storedMana + manaDifference);
      }

      storedMana = gemMahou.getStoredMana();
      if (storedMana >= this.getMaxMana()) {
         gemMahou.setStoredMana(this.getMaxMana());
      }

      Utils.setGemMahou(itemStack, gemMahou);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
      return stack;
   }

   public static void triggerUpdate(ServerPlayer player, ItemStack stack) {
      player.inventoryMenu.sendAllDataToRemote();
   }
}
