package stepsword.mahoutsukai.item.nobu;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.selectedtarget.SelectedTargetMahou;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.entity.mahoujin.GunEntity;
import stepsword.mahoutsukai.item.EmptyProjectileWeaponItem;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class Nobu extends EmptyProjectileWeaponItem {
   public static ConcurrentHashMap<UUID, Nobu.NobuUserStorage> player_storage = new ConcurrentHashMap<>();

   public Nobu() {
      super(new Properties().stacksTo(1).durability(MTConfig.NOBU_DURABILITY).enchantable(1));
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public static void clearNobuStorage(Player player) {
      if (player_storage.containsKey(player.getUUID())) {
         Nobu.NobuUserStorage storage = player_storage.get(player.getUUID());

         for (Integer e : storage.guns.keySet()) {
            GunEntity g = storage.guns.get(e);
            if (g != null) {
               g.discard();
            }
         }

         storage.fireMe = 0;
         storage.isFiring = false;
         storage.currentGun = 0;
         storage.origpit = 0.0F;
         storage.origyaw = 0.0F;
         storage.originalBasis2 = null;
         storage.originalBasis = null;
         storage.guns = new HashMap<>();
      }
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      ItemStack itemstack = player.getItemInHand(handIn);
      int mode = Utils.getMode(itemstack);
      Nobu.NobuUserStorage storage = getStorage(player);
      if (!player.isDiscrete()) {
         player.startUsingItem(handIn);
      } else {
         if (!worldIn.isClientSide()) {
            List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.NOBU);
            Utils.color(
               itemstack,
               settings.get(0).intValue(),
               settings.get(1).intValue(),
               settings.get(2).intValue(),
               settings.get(3).intValue(),
               settings.get(4).intValue(),
               settings.get(5).intValue()
            );
            storage.lastMode = mode;
            Utils.setMode(itemstack, (storage.lastMode + 1) % 4);
            mode = Utils.getMode(itemstack);
         }

         MutableComponent tt;
         if (mode == 0) {
            tt = Component.translatable("mahoutsukai.nobu.auto");
         } else if (mode == 1) {
            tt = Component.translatable("mahoutsukai.nobu.targetsummon");
         } else if (mode == 2) {
            tt = Component.translatable("mahoutsukai.nobu.firingsquadcircle");
         } else {
            tt = Component.translatable("mahoutsukai.nobu.firingsquadline");
         }

         player.sendOverlayMessage(tt);
      }

      return InteractionResult.PASS;
   }

   public boolean isEnchantable(ItemStack pStack) {
      return true;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return true;
   }

   public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
      return !(stack.getItem() instanceof Nobu)
         ? false
         : enchantment.is(Enchantments.POWER) || enchantment.is(Enchantments.UNBREAKING) || enchantment.is(Enchantments.MENDING);
   }

   public static void nobuCreate(net.minecraft.world.damagesource.DamageSource source) {
      double NOBU_DISTANCE_REQUIREMENT = MTConfig.NOBU_DISTANCE_REQUIREMENT;
      Entity cause = source.getEntity();
      Entity projectile = source.getDirectEntity();
      if (MTConfig.NOBU_ENABLED
         && projectile instanceof Projectile
         && cause instanceof Player p
         && validNobuItem(p.getMainHandItem(), p.level())
         && StrengtheningSpellEffect.getStrengthened(p.getMainHandItem()) > 0) {
         if (p.position().subtract(projectile.position()).length() > NOBU_DISTANCE_REQUIREMENT) {
            p.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            ItemStack change = new ItemStack((ItemLike)ModItems.nobu.get());
            if (MTConfig.NOBU_IS_UNBREAKABLE) {
               change.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            }

            p.setItemInHand(InteractionHand.MAIN_HAND, change);
            ModTriggers.NOBU.trigger((ServerPlayer)p);
         } else {
            PlayerHelp.sendHelpMessage(p, PlayerHelp.Message.NOBU_FURTHER_AWAY);
         }
      }
   }

   public static boolean validNobuItem(ItemStack s, Level level) {
      return EffectUtil.inItemBlacklist(s.getItem(), MTConfig.NOBU_ALLOWED_BOWS, level);
   }

   public void onUseTick(Level level, LivingEntity living, ItemStack stack, int count) {
      if (!level.isClientSide()) {
         int MAX_GUNS = MTConfig.NOBU_MAX_GUNS;
         if (living instanceof Player player) {
            int mode = Utils.getMode(stack);
            Nobu.NobuUserStorage storage = getStorage(player);
            if (mode != 0 && mode != storage.lastMode && count == 72000) {
               clearNobuStorage(player);
               storage.lastMode = mode;
            }

            if ((mode == 1 || mode == 2 || mode == 3) && count % MTConfig.NOBU_SPAWN_AND_FIRE_FREQ == 0 && (storage.target != null || mode == 3)) {
               if (storage.isFiring) {
                  if (mode == 1) {
                     fireRotation(storage);
                  }
               } else if (mode == 3) {
                  if (storage.guns.size() < MAX_GUNS) {
                     GunEntity ge = this.spawnGun(player, mode, null, storage.currentGun, MAX_GUNS, storage);
                     if (ge != null && (!storage.guns.containsKey(storage.currentGun) || !storage.guns.get(storage.currentGun).isAlive())) {
                        storage.guns.put(storage.currentGun, ge);
                        storage.currentGun++;
                        storage.currentGun %= MAX_GUNS;
                     }
                  }
               } else {
                  storage.target.ifLeft(e -> {
                     if (storage.guns.size() < MAX_GUNS) {
                        if (!storage.guns.containsKey(storage.currentGun) || !storage.guns.get(storage.currentGun).isAlive()) {
                           GunEntity gex = this.spawnGun(player, mode, e, storage.currentGun, MAX_GUNS, storage);
                           if (gex != null) {
                              storage.guns.put(storage.currentGun, gex);
                           }
                        }

                        storage.currentGun++;
                        storage.currentGun %= MAX_GUNS;
                     }
                  });
               }
            }
         }
      }
   }

   public static void nobuPlayerTick(Player player) {
      if (player_storage.containsKey(player.getUUID())) {
         Nobu.NobuUserStorage storage = getStorage(player);
         fireRotation(storage);
      }
   }

   public static void fireRotation(Nobu.NobuUserStorage storage) {
      if (storage != null && storage.isFiring) {
         if (storage.fireMe >= 0 && storage.fireMe < storage.guns.size()) {
            GunEntity ge = new ArrayList<>(storage.guns.values()).get(storage.fireMe);
            if (ge != null && ge.isAlive()) {
               ge.fireBeam();
            }

            storage.fireMe++;
         } else {
            storage.fireMe = 0;
            storage.isFiring = false;
         }
      }
   }

   public void gunSound(Nobu.NobuUserStorage storage, GunEntity ge, int current, int max) {
      if (current == max - 1) {
         ge.sound = 3;
      } else {
         ge.sound = storage.currentSound;
      }

      if (storage.currentSound == 2) {
         storage.soundup = false;
      }

      if (storage.currentSound == 0) {
         storage.soundup = true;
      }

      if (storage.currentSound < 2 && storage.soundup) {
         storage.currentSound++;
      } else if (storage.currentSound > 0 && !storage.soundup) {
         storage.currentSound--;
      }
   }

   public GunEntity spawnGun(Player player, int mode, Entity target, int current, int max, Nobu.NobuUserStorage storage) {
      double distance = MTConfig.NOBU_CIRCLE_RADIUS;
      float[] colors = getColor(player, mode);
      if (mode == 1 || mode == 2) {
         float angle = (float)(current * 2 * Math.PI / max);
         if (current == 0) {
            storage.originalBasis = target.position();
         }

         Vec3 circle_place = new Vec3(1.0, 0.0, 0.0).yRot(angle).normalize().scale(distance);
         float height = this.getHeightRelativeToEntity(target);
         Vec3 spawn = storage.originalBasis.add(0.0, height, 0.0).add(circle_place);
         if (this.canSpawn(player.level(), spawn) && doSummonMana(player)) {
            GunEntity ge = new GunEntity(player.level(), player, colors[0], colors[1], colors[2], 1.0F, colors[3], colors[4], colors[5], 1.0F);
            ge.setPos(spawn);
            ge.setCircleSize(7.0F);
            ge.aimAt(storage.originalBasis);
            if (mode == 1) {
               ge.autonomous = false;
               ge.setTarget(target.getId());
            } else {
               ge.autonomous = true;
            }

            this.gunSound(storage, ge, current, max);
            player.level().addFreshEntity(ge);
            return ge;
         }
      } else if (mode == 3) {
         if (current == 0 && (storage.originalBasis == null || storage.originalBasis2 == null)) {
            storage.originalBasis = player.calculateViewVector(player.xRot, player.yHeadRot).normalize();
            storage.originalBasis2 = player.position();
            storage.origyaw = player.yHeadRot;
            storage.origpit = player.xRot;
         }

         double loc = -MTConfig.NOBU_FIRING_LINE_SPACE / 2.0 + MTConfig.NOBU_FIRING_LINE_SPACE / max * current;
         double height = MTConfig.NOBU_MAX_SPAWN_HEIGHT;
         Vec3 playerLook = storage.originalBasis;
         playerLook = new Vec3(playerLook.x, 0.0, playerLook.z);
         Vec3 spawn = storage.originalBasis2.add(playerLook.scale(height).x, height, playerLook.scale(height).z);
         Vec3 sideways = playerLook.normalize().yRot(org.joml.Math.toRadians(90.0F));
         spawn = spawn.add(sideways.scale(loc));
         if (this.canSpawn(player.level(), spawn) && doSummonMana(player)) {
            GunEntity ge = new GunEntity(player.level(), player, colors[0], colors[1], colors[2], 1.0F, colors[3], colors[4], colors[5], 1.0F);
            ge.setPos(spawn);
            ge.setCircleSize(7.0F);
            ge.setRotationYaw(-storage.origyaw + 180.0F);
            ge.setRotationPitch(storage.origpit);
            ge.setRotationRoll(90.0F);
            ge.autonomous = true;
            this.gunSound(storage, ge, current, max);
            player.level().addFreshEntity(ge);
            return ge;
         }
      }

      return null;
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return false;
   }


   public float getHeightRelativeToEntity(Entity target) {
      Vec3 p = target.position();
      int countup = 0;
      int countdown = 0;
      BlockPos above = Utils.toBlockPos(p.add(0.0, target.getBbHeight(), 0.0));
      BlockPos below = Utils.toBlockPos(p);

      for (int i = 0; i < MTConfig.NOBU_MAX_SPAWN_HEIGHT * 2.0; i++) {
         above = above.above();
         below = below.below();
         if (!target.level().getBlockState(above).isAir()) {
            countdown++;
         }

         if (!target.level().getBlockState(below).isAir()) {
            countdown++;
         }
      }

      return (float)(-MTConfig.NOBU_MAX_SPAWN_HEIGHT + Math.abs(countdown - countup) * 1);
   }

   public boolean canSpawn(Level level, Vec3 propose) {
      return level.getBlockState(Utils.toBlockPos(propose)).isAir();
   }

   public static Nobu.NobuUserStorage getStorage(Player player) {
      Nobu.NobuUserStorage storage;
      if (player_storage.containsKey(player.getUUID())) {
         storage = player_storage.get(player.getUUID());
      } else {
         storage = new Nobu.NobuUserStorage();
         player_storage.put(player.getUUID(), storage);
      }

      return storage;
   }

   public boolean releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
      clearTarget(pStack, pLivingEntity);
      return true;
   }

   public static void clearTarget(ItemStack stack, LivingEntity living) {
      if (stack.getItem() instanceof Nobu && living instanceof Player player) {
         setSelectedTarget(stack, -1);
         Nobu.NobuUserStorage storage = player_storage.get(player.getUUID());
         if(storage!=null)storage.target = null;
      }

      if (Utils.getMode(stack) == 1 && living instanceof Player player) {
         clearNobuStorage(player);
      }
   }

   public static boolean doSummonMana(Player player) {
      boolean ret = PlayerManaManager.drainMana(player, getManaPerConstruct(), false, false) == getManaPerConstruct();
      if (ret && player.getMainHandItem().getItem() instanceof Nobu) {
         player.getMainHandItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
      }

      return ret;
   }

   public static boolean doShotMana(Player player) {
      if (player == null) {
         return false;
      }

      boolean ret = PlayerManaManager.drainMana(player, getManaPerShot(), false, false) == getManaPerShot();
      if (ret && player.getMainHandItem().getItem() instanceof Nobu) {
         player.getMainHandItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
      }

      return ret;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.NOBU_DURABILITY;
   }

   public static int getManaPerShot() {
      return MTConfig.NOBU_MANA_PER_SHOT;
   }

   public static int getManaPerConstruct() {
      return MTConfig.NOBU_MANA_PER_CONSTRUCT;
   }

   public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
      clearTarget(pStack, pLivingEntity);
      return super.finishUsingItem(pStack, pLevel, pLivingEntity);
   }

   public static float[] getColor(Player caster, int mode) {
      float[] ret = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
      MahouSettings.Spell spell = MahouSettings.Spell.NOBU;
      List<Integer> settings = MahouSettings.getIntSettings(caster, spell);
      ret[0] = settings.get(0).intValue() / 255.0F;
      ret[1] = settings.get(1).intValue() / 255.0F;
      ret[2] = settings.get(2).intValue() / 255.0F;
      ret[3] = settings.get(3).intValue() / 255.0F;
      ret[4] = settings.get(4).intValue() / 255.0F;
      ret[5] = settings.get(5).intValue() / 255.0F;
      return ret;
   }

   public static int getSelectedTarget(ItemStack stack) {
      SelectedTargetMahou mahou = Utils.getSelectedTarget(stack);
      return mahou.getTargetId();
   }

   public static void setSelectedTarget(ItemStack stack, int t) {
      SelectedTargetMahou mahou = Utils.getSelectedTarget(stack);
      mahou.setTargetId(t);
      Utils.setSelectedTarget(stack, mahou);
   }

   public static class NobuUserStorage {
      public HashMap<Integer, GunEntity> guns = new HashMap<>();
      public Either<Entity, BlockPos> target = null;
      public int currentGun = 0;
      public boolean isFiring = false;
      public int fireMe = 0;
      public int lastMode = 0;
      public Vec3 originalBasis = null;
      public Vec3 originalBasis2 = null;
      public float origyaw = 0.0F;
      public float origpit = 0.0F;
      public int currentSound = 0;
      public boolean soundup = true;
   }
}
