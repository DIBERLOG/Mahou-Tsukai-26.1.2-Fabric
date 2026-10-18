package stepsword.mahoutsukai.tile.exchange;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class DurabilityExchangeMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   public DurabilityExchangeMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.durabilityExchange.get(), p, bs);
   }

   @Override
   public int blockCycle() {
      return MTConfig.DURABILITY_EXCHANGE_BLOCK_CYCLE;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (ActiveCircleConfig.tryToOperate(this, this.getCasterUUID())) {
         this.consumeDurabilityBelow();
      }
   }

   @Override
   public int checkTicks() {
      return MTConfig.DURABILITY_EXCHANGE_BLOCK_CYCLE;
   }

   @Override
   public List<Class<? extends Item>> checkForItemClass() {
      return Arrays.asList(Item.class);
   }

   @Override
   public boolean preConditions() {
      return this.getCaster() != null;
   }

   @Override
   public boolean firstEntityOnly() {
      return false;
   }

   @Override
   public boolean ifItemFoundAbove(ItemEntity item) {
      return this.consumeDurabilityOnTop(item);
   }

   public void consumeDurabilityBelow() {
      if (this.level != null && !this.level.isClientSide()) {
         BlockEntity te = this.level.getBlockEntity(this.worldPosition.offset(0, -1, 0));
         int restored = 0;
         if (te != null && this.getCaster() != null) {
            IMahou mahou = Utils.getPlayerMahou(this.getCaster());
            var invCap = net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(this.level,this.worldPosition.below(),null);
            if (invCap != null && mahou != null) {
               boolean flag = false;

               for (var slot : invCap) {
                  ItemStack stack = slot.getResource().toStack((int)Math.min(Integer.MAX_VALUE,slot.getAmount()));
                  boolean breakable = stack.isDamageableItem();
                  if (!stack.isEmpty() && breakable && stack.getDamageValue() < stack.getMaxDamage() && !this.blacklisted(stack)) {
                     while (stack.getDamageValue() < stack.getMaxDamage()) {
                        if (mahou.getStoredMana() >= mahou.getMaxMana()) {
                           mahou.setStoredMana(mahou.getMaxMana());
                           flag = true;
                           break;
                        }

                        int tmp = stack.getDamageValue();
                        int mana_restored = 1;
                        double tax = computeTax(stack);
                        if (tax < 1.0 && tax > 0.0) {
                           mana_restored = (int)(1.0 / tax);
                           tax = 1.0;
                        }

                        stack = this.damageInventoryStack(invCap, slot, stack, (int)tax);
                        if (stack.getDamageValue() <= tmp) {
                           break;
                        }

                        mahou.setStoredMana(Math.min(mahou.getMaxMana(), mahou.getStoredMana() + mana_restored));
                        if (++restored >= MTConfig.DURABILITY_EXCHANGE_MANA_GAIN_CAP) {
                           flag = true;
                           break;
                        }
                     }
                  }

                  if (flag) {
                     break;
                  }
               }
            }
         }
      }
   }

   private ItemStack damageInventoryStack(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> inventory,
         net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> slot, ItemStack stack,int damage) {
      ItemStack damagedStack=stack.copy();
      damagedStack.hurtAndBreak(damage,SafeFakePlayer.createFakePlayerIfNotExists((ServerLevel)this.getCaster().level(),"faker",this.getCasterUUID()),EquipmentSlot.MAINHAND);
      try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
         if(slot.extract(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(stack),stack.getCount(),tx)!=stack.getCount()) return stack;
         if(!damagedStack.isEmpty() && inventory.insert(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(damagedStack),damagedStack.getCount(),tx)!=damagedStack.getCount()) return stack;
         tx.commit();return damagedStack;
      }
   }

   public static double computeTax(ItemStack stack) {
      double ret = MTConfig.DURABILITY_EXCHANGE_EFFICIENCY;
      double currDurability = stack.getMaxDamage() - stack.getDamageValue();
      currDurability = Math.max(currDurability, 0.0);
      List<? extends Double> rates = MTConfig.DURABILITY_EXCHANGE_TAX_RATES;
      List<? extends Double> brackets = MTConfig.DURABILITY_EXCHANGE_TAX_BRACKETS;
      double highestbracket = 0.0;
      int tmpi = -1;
      if (brackets.size() > 0) {
         for (int i = 0; i < brackets.size(); i++) {
            if (brackets.get(i) > highestbracket && currDurability >= brackets.get(i)) {
               highestbracket = brackets.get(i);
               tmpi = i;
            }
         }

         if (tmpi >= 0 && tmpi < rates.size()) {
            ret = Math.min(rates.get(tmpi) * currDurability, currDurability);
         }
      }

      return ret;
   }

   public boolean consumeDurabilityOnTop(ItemEntity item) {
      boolean flag = false;
      int restored = 0;
      IMahou mahou = Utils.getPlayerMahou(this.getCaster());
      ItemStack stack = item.getItem();
      boolean breakable = stack.isDamageableItem();
      if (breakable && stack.getDamageValue() < stack.getMaxDamage() && !this.blacklisted(stack)) {
         while (stack.getDamageValue() < stack.getMaxDamage()) {
            if (mahou.getStoredMana() >= mahou.getMaxMana()) {
               mahou.setStoredMana(mahou.getMaxMana());
               flag = true;
               break;
            }

            int tmp2 = stack.getDamageValue();
            int mana_restored = 1;
            double tax = computeTax(stack);
            if (tax < 1.0 && tax > 0.0) {
               mana_restored = (int)(1.0 / tax);
               tax = 1.0;
            }

            stack.hurtAndBreak(
               (int)tax,
               SafeFakePlayer.createFakePlayerIfNotExists((ServerLevel)this.getCaster().level(), "faker", this.getCasterUUID()),
               EquipmentSlot.MAINHAND
            );
            if (stack.getDamageValue() <= tmp2) {
               break;
            }

            mahou.setStoredMana(Math.min(mahou.getMaxMana(), mahou.getStoredMana() + mana_restored));
            restored++;
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
               item.discard();
            }

            if (restored >= MTConfig.DURABILITY_EXCHANGE_MANA_GAIN_CAP) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   public boolean blacklisted(ItemStack stack) {
      boolean itemBlacklisted = EffectUtil.inItemBlacklist(stack.getItem(), MTConfig.DURABILITY_EXCHANGE_BLACKLIST, this.level);
      boolean enchantBlacklisted = false;

      for (Holder<Enchantment> s : ((ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)).keySet()) {
         enchantBlacklisted = enchantBlacklisted || EffectUtil.inEnchantBlacklist(s, MTConfig.DURABILITY_EXCHANGE_BLACKLIST);
      }

      return itemBlacklisted || enchantBlacklisted;
   }
}
