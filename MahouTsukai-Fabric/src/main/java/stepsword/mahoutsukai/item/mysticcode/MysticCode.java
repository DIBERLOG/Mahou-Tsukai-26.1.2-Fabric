package stepsword.mahoutsukai.item.mysticcode;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.containers.MysticCodeInventory;
import stepsword.mahoutsukai.containers.MysticCodeContents;
import stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.item.spells.boundary.BoundarySpellScroll;
import stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll;
import stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class MysticCode extends ItemBase implements MenuProvider {
   public MysticCode(String name, Properties prop) {
      super(name, prop);
      this.addPropertyOverrides();
   }

   public MysticCode() {
      super("mystic_code", new Properties().stacksTo(1));
      this.addPropertyOverrides();
   }

   public void addPropertyOverrides() {
   }

   public int getMaxStackSize(ItemStack stack) {
      return 1;
   }

   public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
      if (playerIn.isDiscrete()) {
         if (!worldIn.isClientSide()) {
            playerIn.openMenu(this);
         }

         return InteractionResult.SUCCESS;
      } else {
         playerIn.startUsingItem(handIn);
         return InteractionResult.FAIL;
      }
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.NONE;
   }

   public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      if (!worldIn.isClientSide()) {
         MysticCodeContents inventory = Utils.getMCInventory(stack);
         if (inventory != null) {
            int slot = getSlot(stack);
            if (slot >= 0 && slot < 3) {
               ItemStack ammo = inventory.getItem(slot);
               if (!EffectUtil.inItemBlacklist(ammo.getItem(), this.getBlacklist(), worldIn) && ammo.getItem() instanceof SpellScroll s) {
                  s.useAction(ammo, worldIn, entityLiving, this.consumesScroll());
                  inventory.setItem(slot,ammo);
                  if (this.requiresDura()) {
                     stack.hurtAndBreak(1, entityLiving, entityLiving.getOffhandItem()==stack?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND);
                  }

                  this.saveCapabilityToStack(stack);
                  return true;
               }
            }
         }
      }

      if (!worldIn.isClientSide()) {
         triggerUpdate((ServerPlayer)entityLiving, stack);
      }

      return false;
   }

   public InteractionResult useOn(UseOnContext ctx) {
      Player player = ctx.getPlayer();
      if (player != null) {
         Level worldIn = player.level();
         InteractionHand hand = ctx.getHand();
         BlockPos pos = ctx.getClickedPos();
         Direction facing = ctx.getClickedFace();
         ItemStack stack = player.getItemInHand(hand);
         if (player.isDiscrete()) {
            if (!worldIn.isClientSide()) {
               player.openMenu(this);
            }

            return InteractionResult.SUCCESS;
         } else {
            MysticCodeContents inventory = Utils.getMCInventory(stack);
            if (inventory != null) {
               int slot = getSlot(stack);
               if (slot >= 0 && slot < 3) {
                  ItemStack ammo = inventory.getItem(slot);
                  if (!EffectUtil.inItemBlacklist(ammo.getItem(), this.getBlacklist(), worldIn)) {
                     if (ammo.getItem() instanceof BoundarySpellScroll b) {
                        InteractionResult s = b.boundaryScrollUse(player, worldIn, ammo, pos, facing, this.consumesScroll());
                        inventory.setItem(slot,ammo);
                        if (this.requiresDura()) {
                           stack.hurtAndBreak(1, player, player.getOffhandItem()==stack?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND);
                        }

                        if (!worldIn.isClientSide()) {
                           this.saveCapabilityToStack(stack);
                        }

                        return s;
                     }

                     player.startUsingItem(hand);
                  }
               }
            }

            if (!worldIn.isClientSide()) {
               triggerUpdate((ServerPlayer)player, stack);
            }

            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.FAIL;
      }
   }

   public java.util.List<? extends String> getBlacklist() {
      return new ArrayList<>();
   }

   public boolean requiresDura() {
      return false;
   }

   public boolean consumesScroll() {
      return true;
   }

   public static void setSlot(ItemStack stack, int g) {
      MysticCodeData data = Utils.getMysticCodeData(stack);
      data.setSlot(g);
      Utils.setMysticCodeData(stack, data);
   }

   public static int getSlot(ItemStack stack) {
      MysticCodeData data = Utils.getMysticCodeData(stack);
      return Math.clamp(data.getSlot(),0,2);
   }

   public void setRandomColor(ItemStack stack, RandomSource rand) {
      this.setColor(stack, rand.nextInt(8));
   }

   public void setColor(ItemStack stack, int z) {
      MysticCodeData data = Utils.getMysticCodeData(stack);
      data.setColor(z);
      Utils.setMysticCodeData(stack, data);
   }

   public int getColor(ItemStack stack) {
      MysticCodeData data = Utils.getMysticCodeData(stack);
      return data.getColor();
   }

   public void setSlotsForWheel(ItemStack code, ItemStack s1, ItemStack s2, ItemStack s3) {
      MysticCodeData data = Utils.getMysticCodeData(code);
      data.setSlot1(convertStackToString(s1));
      data.setSlot2(convertStackToString(s2));
      data.setSlot3(convertStackToString(s3));
      Utils.setMysticCodeData(code, data);
   }

   public List<String> getSlotsForWheel(ItemStack code) {
      var stored=code.getOrDefault(net.minecraft.core.component.DataComponents.CONTAINER,net.minecraft.world.item.component.ItemContainerContents.EMPTY);
      var values=net.minecraft.core.NonNullList.withSize(3,ItemStack.EMPTY);stored.copyInto(values);
      return List.of(convertStackToString(values.get(0)),convertStackToString(values.get(1)),convertStackToString(values.get(2)));
   }

   public static String convertStackToString(ItemStack s) {
      return s.isEmpty() ? "SLOT EMPTY" : s.getItem().getName(s).getString();
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      MysticCodeContents inventory = Utils.getMCInventory(stack);
      if (inventory != null) {
         int slot = getSlot(stack);
         if (slot >= 0 && slot < 3) {
            ItemStack ammo = inventory.getItem(slot);
            if (ammo.getItem() instanceof GandrSpellScroll || ammo.getItem() instanceof FallenDownSpellScroll) {
               ammo.getItem().onUseTick(level, player, ammo, count);
               inventory.setItem(slot,ammo);
            }
         }
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return false;
   }

   public static void triggerUpdate(ServerPlayer player, ItemStack stack) {
      player.connection.send(new ClientboundSetHeldSlotPacket(player.getInventory().getSelectedSlot()));
   }

   public Component getDisplayName() {
      return Component.literal("Mystic Code");
   }

   @Nullable
   public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player Player) {
      ItemStack stack;
      if (Player.getMainHandItem().getItem() instanceof MysticCode) {
         stack = Player.getMainHandItem();
      } else {
         stack = Player.getOffhandItem();
      }

      return new MysticCodeInventory(i,playerInventory,stack);
   }

   public void saveCapabilityToStack(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof MysticCode) {
         MysticCodeContents inventory = Utils.getMCInventory(stack);
         if (inventory != null) {
            this.setSlotsForWheel(stack, inventory.getItem(0), inventory.getItem(1), inventory.getItem(2));
         }
      }
   }
 public static boolean isChanneling(LivingEntity user,Class<? extends net.minecraft.world.item.Item> spell){var stack=user.getUseItem();if(spell.isInstance(stack.getItem()))return true;var inventory=Utils.getMCInventory(stack);return inventory!=null && spell.isInstance(inventory.getItem(getSlot(stack)).getItem());}

}
