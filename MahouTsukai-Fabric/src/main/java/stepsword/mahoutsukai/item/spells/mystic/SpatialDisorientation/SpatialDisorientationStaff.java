package stepsword.mahoutsukai.item.spells.mystic.SpatialDisorientation;

import com.google.common.base.Predicate;
import java.util.HashMap;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class SpatialDisorientationStaff extends ItemBase {
   public static HashMap<UUID, SpatialDisorientationStaff.SpatialDisorientationStorage> staffs = new HashMap<>();

   public SpatialDisorientationStaff() {
      super("spatial_disorientation_staff", 1, 20);
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      float r = 1.0F;
      float g = 0.7372549F;
      float b = 0.12941177F;
      float a = 0.95F;
      if (player instanceof Player) {
         SpatialDisorientationStaff.SpatialDisorientationStorage storage;
         if (staffs.containsKey(player.getUUID())) {
            storage = staffs.get(player.getUUID());
         } else {
            storage = new SpatialDisorientationStaff.SpatialDisorientationStorage();
            staffs.put(player.getUUID(), storage);
         }

         if (storage.target != null) {
            storage.target.setDeltaMovement(0.0, 0.0, 0.0);
            if (!player.level().isClientSide()) {
               if (storage.mahoujin == null) {
                  if (PlayerManaManager.drainMana((Player)player, MTConfig.SPATIAL_DISORIENTATION_MANA_COST_ST, false, false)
                     == MTConfig.SPATIAL_DISORIENTATION_MANA_COST_ST) {
                     SpatialDisorientationMahoujinEntity m = new SpatialDisorientationMahoujinEntity(player.level(), (Player)player, r, g, b, a);
                     m.setCircleSize(storage.target.getBbWidth() * 2.0F);
                     m.setRotationSpeed(0.25F);
                     storage.mahoujin = m;
                     this.reangle((Player)player, storage.target.position(), 2.125F, storage);
                     storage.target.level().addFreshEntity(m);
                  }
               } else if (storage.mahoujin.getRotationSpeed() < 8.0F) {
                  storage.mahoujin.setRotationSpeed(storage.mahoujin.getRotationSpeed() + 0.1F);
               }

               this.reangle((Player)player, storage.target.position(), 2.125F, storage);
            }
         } else if (storage.location != null) {
            if (!player.level().isClientSide()) {
               if (storage.mahoujin == null) {
                  if (PlayerManaManager.hasMana((Player)player, MTConfig.SPATIAL_DISORIENTATION_MANA_COST_AOE * 10)) {
                     SpatialDisorientationMahoujinEntity m = new SpatialDisorientationMahoujinEntity(player.level(), (Player)player, r, g, b, a);
                     m.setCircleSize(3.5F);
                     m.setRotationSpeed(0.25F);
                     storage.mahoujin = m;
                     this.reangle((Player)player, storage.location, 2.125F, storage);
                     player.level().addFreshEntity(m);
                  }
               } else {
                  if (storage.mahoujin.getRotationSpeed() < 8.0F) {
                     storage.mahoujin.setRotationSpeed(storage.mahoujin.getRotationSpeed() + 0.1F);
                  }

                  this.reangle((Player)player, storage.location, 2.125F, storage);
               }

               if (count % 20 == 0
                  && PlayerManaManager.drainMana((Player)player, MTConfig.SPATIAL_DISORIENTATION_MANA_COST_AOE, false, false)
                     == MTConfig.SPATIAL_DISORIENTATION_MANA_COST_AOE) {
                  PlayerManaManager.updateClientMahou((ServerPlayer)player, Utils.getPlayerMahou((Player)player));
               } else if (count % 20 == 0) {
                  player.releaseUsingItem();
               }
            }

            if (storage.mahoujin != null && storage.mahoujin.getRotationSpeed() >= 8.0F && count % 4 == 0) {
               double radius = MTConfig.SPATIAL_DISORIENTATION_AOE_RADIUS;

               for (Entity tmp : storage.mahoujin
                  .level()
                  .getEntitiesOfClass(
                     Entity.class,
                     new AABB(
                        storage.location.x - radius,
                        storage.location.y - radius,
                        storage.location.z - radius,
                        storage.location.x + radius,
                        storage.location.y + radius,
                        storage.location.z + radius
                     ),
                     Entity::isAlive
                  )) {
                  Vec3 vec = Vec3.directionFromRotation(player.xRot, player.yRot);
                  float speed = storage.mahoujin.getRotationSpeed() * 1.2F;
                  this.boop(tmp, (float)Math.min(speed, MTConfig.SPATIAL_DISORIENTATION_SPEED), vec.x, vec.y, vec.z);
               }
            }
         }
      }
   }

   private void reangle(Player player, Vec3 target, float distance, SpatialDisorientationStaff.SpatialDisorientationStorage storage) {
      if (storage.mahoujin != null && player != null && target != null) {
         storage.mahoujin.angleCircleAroundBlock(90.0F - player.yRot, player.xRot + 90.0F, distance, new Vec3(target.x, target.y, target.z));
      }
   }

   private void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      entity.setOnGround(false);
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      if(f<1.0E-6)return;
      Vec3 motion = entity.getDeltaMovement();
      double motionX = motion.x;
      double motionY = motion.y;
      double motionZ = motion.z;
      motionX /= 2.0;
      motionZ /= 2.0;
      motionY /= 2.0;
      motionX = x / f * strength;
      motionZ = z / f * strength;
      motionY = y / f * strength;
      entity.setDeltaMovement(motionX, motionY, motionZ);
      if (entity instanceof LivingEntity
         && (!(entity instanceof Player) || !((Player)entity).isFallFlying())
         && !MTConfig.SPATIAL_DISORIENTATION_AIR_RESISTANCE) {
         EffectUtil.buff((LivingEntity)entity, ModEffects.FLUNG, false, MTConfig.SPATIAL_NO_AIR_RESISTANCE_TIME_LIMIT, false);
      }

      entity.hurtMarked = true;
   }

   @NotNull
   public InteractionResult use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
      staffs.remove(player.getUUID());
      SpatialDisorientationStaff.SpatialDisorientationStorage storage = new SpatialDisorientationStaff.SpatialDisorientationStorage();
      staffs.put(player.getUUID(), storage);
      ItemStack stack = player.getItemInHand(hand);
      Entity t = Utils.selectEntityNearCursor(
         player,
         MTConfig.GLOBAL_LOOK_RANGE,
         player.level(),
         a -> Utils.getPredicate(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR, a, player),
         Utils.nearAllowed(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR)
      );
      if (!(t instanceof LivingEntity) && !(t instanceof PrimedTnt)) {
         storage.target = null;
         HitResult result = player.pick(MTConfig.GLOBAL_LOOK_RANGE, 0.0F, false);
         if (!(result instanceof BlockHitResult)) {
            storage.location = player.getLookAngle().normalize().scale(10.0).add(player.getEyePosition(1.0F));
         } else {
            storage.location = new Vec3(
               ((BlockHitResult)result).getBlockPos().getX() + 0.5,
               ((BlockHitResult)result).getBlockPos().getY() + 5,
               ((BlockHitResult)result).getBlockPos().getZ() + 0.5
            );
         }
      } else {
         storage.target = t;
         storage.location = null;
      }

      player.startUsingItem(hand);
      storage.firstBounce = true;
      storage.mahoujin = null;
      return InteractionResult.SUCCESS;
   }

   public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
      SpatialDisorientationStaff.SpatialDisorientationStorage storage;
      if (staffs.containsKey(entityLiving.getUUID())) {
         storage = staffs.get(entityLiving.getUUID());
      } else {
         storage = new SpatialDisorientationStaff.SpatialDisorientationStorage();
         staffs.put(entityLiving.getUUID(), storage);
      }

      if (storage.mahoujin != null) {
         if (storage.target != null) {
            LivingEntity player = entityLiving;
            Vec3 vec = Vec3.directionFromRotation(player.xRot, player.yRot);
            if (!storage.target.level().isClientSide()
               && player instanceof ServerPlayer
               && storage.target instanceof Pig
               && ((Pig)storage.target).getPassengers().contains(player)) {
               ((MahouTrigger)ModTriggers.FLYING_PIGS).trigger((ServerPlayer)player);
            }

            if (player instanceof Player p) {
               MandatoryFun.completeFunReq(p, MandatoryFun.FUN_REQS.SPATIAL_THROW_SOMETHING);
            }

            this.boop(storage.target, (float)Math.min(storage.mahoujin.getRotationSpeed() * 1.2F, MTConfig.SPATIAL_DISORIENTATION_SPEED), vec.x, vec.y, vec.z);
         }

         if (storage.mahoujin.isAlive()) {
            Utils.schedule(worldIn, () -> storage.mahoujin.discard());
         }
      }

      staffs.remove(entityLiving.getUUID());return true;
   }

   public static Predicate<Entity> getProjectionLookPredicate() {
      return new Predicate<Entity>() {
         public boolean apply(@Nullable Entity e) {
            return e instanceof LivingEntity;
         }
      };
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.NONE;
   }

   public class SpatialDisorientationStorage {
      public Entity target = null;
      public SpatialDisorientationMahoujinEntity mahoujin = null;
      public Vec3 location = null;
      public boolean firstBounce = false;
   }
}
