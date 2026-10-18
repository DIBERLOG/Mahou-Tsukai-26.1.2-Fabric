package stepsword.mahoutsukai.item.spells.projection.WeaponProjectile;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou;
import stepsword.mahoutsukai.effects.projection.ProjectionSpellEffect;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.spells.projection.ProjectionSpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class WeaponProjectileBow extends BowItem {
   public static String AMMO_STACK = "mahoutsukai_ammo_stack";
   private String MODE_TAG = "MAHOUTSUKAI_WPBOW_MODE";

   public WeaponProjectileBow() {
      super(ItemBase.withRegisteredId(new Properties()).stacksTo(1).durability(MTConfig.WEAPON_SHOOTER_DURABILITY));
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.WEAPON_SHOOTER_DURABILITY;
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      new ItemStack(Items.AIR);
      new ItemStack(Items.AIR);
      ItemStack s;
      ItemStack weapon;
      if (Utils.getMode(stack) == 1) {
         s = this.findAmmo(player, stack, true);
         if (s == null || s.isEmpty() || s.getItem() == Items.ARROW) {
            ItemStack scroll = this.findProjection(player, stack);
            if (scroll.getItem() instanceof ProjectionSpellScroll) {
               ((ProjectionSpellScroll)scroll.getItem()).useAction(scroll, world, player, true);
            }

            s = this.findAmmo(player, stack, true);
            if (world.isClientSide() && (s == null || s.isEmpty() || s.getItem() == Items.ARROW) && scroll.getItem() instanceof ProjectionSpellScroll) {
               StackHolderMahou mahou = Utils.getStackHolderMahou(scroll);
               if (mahou != null && mahou.getStack().isPresent()) {
                  s = mahou.getStack().get();
               }
            }
         }

         weapon = s;
      } else {
         s = this.findAmmo(player, stack, false);
         weapon = s;
      }

      boolean flag = s != null && !s.isEmpty() && s.getItem() != Items.ARROW;
      boolean flag2 = weapon != null && !weapon.isEmpty() && weapon.getItem() != Items.ARROW;
      if ((!flag && !world.isClientSide() || !flag2 && world.isClientSide()) && !player.isDiscrete()) {
         return InteractionResult.FAIL;
      }

      if (!player.isDiscrete()) {
         Utils.setAmmoStack(stack, weapon);
         player.startUsingItem(hand);
         return InteractionResult.SUCCESS;
      }

      Utils.setMode(stack, (Utils.getMode(stack) + 1) % 2);
      int mode = Utils.getMode(stack);
      MutableComponent tt;
      if (mode == 0) {
         tt = Component.translatable("mahoutsukai.weaponprojectiles.normal");
      } else {
         tt = Component.translatable("mahoutsukai.weaponprojectiles.projections");
      }

      player.sendOverlayMessage(tt);
      return InteractionResult.SUCCESS;
   }

   public ItemStack findAmmo(Player player, ItemStack shootable, boolean projectedOnly) {
      if (!(shootable.getItem() instanceof WeaponProjectileBow)) {
         return ItemStack.EMPTY;
      }

      Predicate<ItemStack> prepredicate = ((WeaponProjectileBow)shootable.getItem()).getPreInventoryAmmoPredicate();
      Predicate<ItemStack> predicate = ((WeaponProjectileBow)shootable.getItem()).getSupportedHeldProjectiles();
      ItemStack itemstack = getHeldProjectile(player, predicate);
      if (!itemstack.isEmpty()) {
         return itemstack;
      }

      prepredicate = ((WeaponProjectileBow)shootable.getItem()).getPreInventoryAmmoPredicate();
      predicate = ((WeaponProjectileBow)shootable.getItem()).getAllSupportedProjectiles();

      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack itemstack1 = player.getInventory().getItem(i);
         if (prepredicate.test(itemstack1)) {
            return itemstack1;
         }
      }

      if (!projectedOnly) {
         for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemstack1 = player.getInventory().getItem(i);
            if (predicate.test(itemstack1)) {
               return itemstack1;
            }
         }
      }

      return player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
   }

   public ItemStack findProjection(Player player, ItemStack shootable) {
      if (!(shootable.getItem() instanceof WeaponProjectileBow)) {
         return ItemStack.EMPTY;
      }

      Predicate<ItemStack> predicate = a -> a.getItem() instanceof ProjectionSpellScroll;
      ItemStack itemstack = getHeldProjectile(player, predicate);
      if (!itemstack.isEmpty()) {
         return itemstack;
      }

      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack itemstack1 = player.getInventory().getItem(i);
         if (predicate.test(itemstack1)) {
            return itemstack1;
         }
      }

      return player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BOW;
   }

   public Predicate<ItemStack> getPreInventoryAmmoPredicate() {
      return s -> this.isArrow(s) && ProjectionSpellEffect.getProjected(s);
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return s -> this.isArrow(s);
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity shooter, int useTicks) {
      if (shooter instanceof Player player) {
         new ItemStack(Items.AIR);
         ItemStack projectile;
         if (Utils.getMode(stack) == 1) {
            projectile = this.findAmmo(player, stack, true);
         } else {
            projectile = this.findAmmo(player, stack, false);
         }

         int i = this.getUseDuration(stack, shooter) - useTicks;
         if (i < 0) {
            return true;
         }

         if (!player.level().isClientSide()) {
            List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.WEAPON_PROJECTILE);
            Utils.color(
               stack,
               settings.get(0).intValue(),
               settings.get(1).intValue(),
               settings.get(2).intValue(),
               settings.get(3).intValue(),
               settings.get(4).intValue(),
               settings.get(5).intValue()
            );
         }

         if (projectile != ItemStack.EMPTY) {
            float f = getPowerForTime(i);
            if (f >= 0.1) {
               if (!world.isClientSide()) {
                  if (!this.isArrow(projectile)) {
                     return true;
                  }

                  WeaponProjectileEntity projectileEntity = new WeaponProjectileEntity(world, shooter, projectile.copy());
                  projectileEntity.shoot(shooter, shooter.xRot, shooter.yRot, 0.0F, (float)(f * MTConfig.WEAPON_SHOOTER_PROJECTILE_SPEED), 1.0F);
                  projectileEntity.setOwner(shooter);
                  world.addFreshEntity(projectileEntity);
                  stack.hurtAndBreak(1, shooter, EquipmentSlot.MAINHAND);
               }

               if (!player.isCreative()) {
                  projectile.shrink(1);
                  if (stack.isEmpty()) {
                     player.getInventory().removeItem(projectile);
                  }
               }

               world.playSound(
                  null,
                  shooter.blockPosition(),
                  SoundEvents.ARROW_SHOOT,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F / (shooter.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
               );
            }
         }
      }

      return true;
   }

   protected boolean isArrow(ItemStack stack) {
      Item tmpi = stack.getItem();
      return Utils.isSword(stack)
         || Utils.isTool(stack)
         || tmpi instanceof ShieldItem
         || tmpi instanceof ShearsItem
         || tmpi instanceof FishingRodItem
         || tmpi instanceof TridentItem;
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return false;
   }
}
