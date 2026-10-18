package stepsword.mahoutsukai.item.spells.mystic.MysticStaff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.BakuretsuPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class MysticStaff extends ItemBase {
   private String MODE_TAG = "MAHOUTSUKAI_AOE_MODE_ON";
   private static final int beamGrowthStart = 35;
   private static final float beamGrowthRate = 1.026F;
   private static final float highestRadius = 35.0F;
   public static HashMap<UUID, MysticStaff.MysticStaffUserStorage> staffs = new HashMap<>();

   public MysticStaff() {
      super("mystic_staff", 1, 20);
   }

   @NotNull
   public InteractionResult use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      staffs.remove(player.getUUID());
      MysticStaff.MysticStaffUserStorage storage = new MysticStaff.MysticStaffUserStorage();
      storage.growthStopped = false;
      storage.mahoujins = null;
      storage.target = null;
      storage.aoeTargets = null;
      storage.aoeSpawnIndex = 0;
      staffs.put(player.getUUID(), storage);
      if (player.isDiscrete()) {
         if (!world.isClientSide()) {
            Utils.setMode(stack, (Utils.getMode(stack) + 1) % 3);
            int mode = Utils.getMode(stack);
            MutableComponent tt;
            if (mode == 0) {
               tt = Component.translatable("mahoutsukai.mysticstaff.big");
            } else if (mode == 1) {
               tt = Component.translatable("mahoutsukai.mysticstaff.aoe");
            } else {
               tt = Component.translatable("mahoutsukai.mysticstaff.beam");
            }

            player.sendOverlayMessage(tt);
         }

         return InteractionResult.SUCCESS;
      } else {
         player.startUsingItem(hand);
         return InteractionResult.SUCCESS;
      }
   }

   public static float getBeamSize(MysticStaffMahoujinEntity entity, float partialTicks) {
      float last = entity.getBeamSize() / 1.026F;
      return last + partialTicks * (entity.getBeamSize() - last);
   }

   public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
      if (player instanceof Player) {
         MysticStaff.MysticStaffUserStorage storage = null;
         if (staffs.containsKey(player.getUUID())) {
            storage = staffs.get(player.getUUID());
         }

         if (storage == null) {
            storage = new MysticStaff.MysticStaffUserStorage();
            staffs.put(player.getUUID(), storage);
         }

         if (Utils.getMode(stack) == 0) {
            player.setDeltaMovement(0.0, player.getDeltaMovement().y * 0.3, 0.0);
            int charge = this.getRemainingUseDuration(stack, player, count);
            if (storage.mahoujins == null) {
               storage.mahoujins = new ArrayList<>();
            }

            if (!player.level().isClientSide()) {
               if (storage.target == null) {
                  storage.target = this.getTargetBlock((Player)player);
               }

               if (charge == 1) {
                  this.createFloorCircle(stack, (Player)player, new Vec3(player.getX(), player.getY(), player.getZ()), 3.0F, 0.5F, storage);
                  this.createFloorCircle(stack, (Player)player, storage.target.add(new Vec3(0.0, 1.0, 0.0)), 12.0F, 0.5F, storage);
               }

               if (charge == 5) {
                  storage.lowest = this.createCircleAtAngle(
                     stack, (Player)player, 10.0F, 1.5F, 10.0F, 30.0F, 15.0F, storage.target, false, 0.1F, false, storage
                  );
               }

               if (charge == 8) {
                  this.createCircleAtAngle(stack, (Player)player, 15.0F, 0.5F, 10.0F, 30.0F, 22.0F, storage.target, false, 0.0F, false, storage);
               }

               if (charge == 13) {
                  this.createCircleAtAngle(stack, (Player)player, 20.0F, 2.0F, 10.0F, 30.0F, 30.0F, storage.target, false, 0.0F, false, storage);
               }

               if (charge == 15) {
                  storage.highest = this.createCircleAtAngle(
                     stack, (Player)player, 35.0F, 1.0F, 10.0F, 30.0F, 37.0F, storage.target, true, 0.1F, false, storage
                  );
               }

               if (charge > 15 && storage.highest != null && MTConfig.MYSTIC_STAFF_BIG_RAIN) {
                  float thunderChance = player.getRandom().nextFloat();
                  if (thunderChance < 0.1) {
                     int angle = player.getRandom().nextInt(360);
                     float radius = player.getRandom().nextInt(20) + 35.0F;
                     double addX = -radius * Math.cos(MysticStaffMahoujinEntity.toRad(angle));
                     double addZ = radius * Math.sin(MysticStaffMahoujinEntity.toRad(angle));
                     double addY = player.getRandom().nextInt(10) - 5;
                     LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(player.level(), EntitySpawnReason.TRIGGERED);
                     lightning.moveOrInterpolateTo(new Vec3(storage.highest.getX() + addX, storage.highest.getY() + addY, storage.highest.getZ() + addZ));
                     lightning.setVisualOnly(true);
                     ((ServerLevel)player.level()).addFreshEntity(lightning);
                  }
               }

               if (charge > 35 && storage.lowest != null && storage.highest != null) {
                  if (storage.highest.getBeamSize() < 0.6F) {
                     storage.lowest.setBeamSize(storage.lowest.getBeamSize() * 1.026F);
                     storage.highest.setBeamSize(storage.highest.getBeamSize() * 1.026F);
                  } else {
                     storage.growthStopped = true;
                  }
               }

               if (MTConfig.MYSTIC_STAFF_BIG_RAIN) {
                  ((ServerLevel)player.level()).rainLevel = Math.min(charge / 40.0F, 2.4F);
                  ((ServerLevel)player.level()).thunderLevel = Math.min(charge / 40.0F, 2.4F);
               }
            }
         } else if (Utils.getMode(stack) == 1) {
            if (!player.level().isClientSide()) {
               double mana_factor = MTConfig.MYSTIC_STAFF_AOE_SCALING_MANA;
               if (Utils.getPlayerMahou((Player)player) != null) {
                  mana_factor *= Utils.getPlayerMahou((Player)player).getMaxMana();
               }

               int mana = (int)mana_factor + MTConfig.MYSTIC_STAFF_AOE_MANA_COST;
               if (PlayerManaManager.drainMana((Player)player, mana, false, false) == mana) {
                  Vec3 look = player.getLookAngle();
                  Vec3 playerpos = player.position();
                  Vec3 playerperp = playerpos;
                  playerperp = playerperp.add(look.normalize().scale(2.0));
                  if (player.getRandom().nextBoolean()) {
                     playerperp = playerperp.add(
                        Vec3.directionFromRotation(0.0F, player.yRot + 90.0F).normalize().scale(player.getRandom().nextDouble() * 3.0 - 0.3).scale(2.0)
                     );
                  } else {
                     playerperp = playerperp.add(
                        Vec3.directionFromRotation(0.0F, player.yRot - 90.0F).normalize().scale(player.getRandom().nextDouble() * 3.0 - 0.3).scale(2.0)
                     );
                  }

                  playerperp = playerperp.add(
                     Vec3.directionFromRotation(player.xRot - 90.0F, player.yRot).normalize().scale(player.getRandom().nextDouble() * 3.0 - 0.2).scale(2.0)
                  );
                  if (count % MTConfig.MYSTIC_STAFF_AOE_DELAY == 0) {
                     float[] colors = this.getColor((Player)player, 1);
                     MysticStaffAOEMahoujinEntity circle = new MysticStaffAOEMahoujinEntity(
                        player.level(), (Player)player, colors[0], colors[1], colors[2], 1.0F, 1.0F, 1.0F, 1.0F, 1.0F
                     );
                     circle.setPos(playerperp.x, playerperp.y, playerperp.z);
                     circle.xRot = player.xRot;
                     circle.yRot = player.yRot;
                     Vec3 pos = circle.position().add(player.getLookAngle());
                     Vec3 aim = circle.position().subtract(pos);
                     circle.setCircleSize(1.5F);
                     circle.shoot(-aim.x, -aim.y, -aim.z, 3.0F, 4.0F);
                     player.level().addFreshEntity(circle);
                  }
               }
            }
         } else if (!player.level().isClientSide()) {
            if (this.getRemainingUseDuration(stack, player, count) > MysticStaffBeamMahoujinEntity.dyingticks * 2 && storage.beamMahoujinEntity == null) {
               float[] colors = this.getColor((Player)player, 2);
               storage.beamMahoujinEntity = new MysticStaffBeamMahoujinEntity(player.level(), (Player)player, colors[0], colors[1], colors[2]);
               storage.beamMahoujinEntity.casterPosition(true);
               player.level().addFreshEntity(storage.beamMahoujinEntity);
            }

            player.setDeltaMovement(0.0, 0.0, 0.0);
         }
      }
   }

   public int getRemainingUseDuration(ItemStack stack, LivingEntity e, int charge) {
      return this.getUseDuration(stack, e) - charge;
   }

   public Vec3 getTargetBlock(Player player) {
      HitResult result = player.pick(MTConfig.GLOBAL_LOOK_RANGE, 1.0F, false);
      Vec3 target;
      if (result instanceof BlockHitResult) {
         target = new Vec3(
            ((BlockHitResult)result).getBlockPos().getX() + 0.5,
            ((BlockHitResult)result).getBlockPos().getY(),
            ((BlockHitResult)result).getBlockPos().getZ() + 0.5
         );
      } else {
         Vec3 v = player.getViewVector(0.0F);
         Vec3 v2 = new Vec3(v.x * MTConfig.GLOBAL_LOOK_RANGE, v.y * MTConfig.GLOBAL_LOOK_RANGE, v.z * MTConfig.GLOBAL_LOOK_RANGE);
         Vec3 v3 = player.getEyePosition(1.0F).add(v2);

         while (Utils.isBlockAir(player.level(), Utils.toBlockPos(v3)) && v3.y > 0.0) {
            v3 = v3.add(new Vec3(0.0, -1.0, 0.0));
         }

         target = v3;
      }

      return target;
   }

   public void createFloorCircle(ItemStack stack, Player player, Vec3 pos, float size, float speed, MysticStaff.MysticStaffUserStorage storage) {
      float[] colors = this.getColor(player, 0);
      MysticStaffMahoujinEntity floorMahoujin = new MysticStaffMahoujinEntity(player.level(), player, false, colors[0], colors[1], colors[2], 0.95F);
      floorMahoujin.setPos(pos.x, pos.y + 0.001, pos.z);
      floorMahoujin.setCircleSize(size);
      floorMahoujin.setRotationSpeed(speed);
      floorMahoujin.setRotationYaw(0.0F);
      floorMahoujin.setRotationPitch(0.0F);
      storage.mahoujins.add(floorMahoujin);
      player.level().addFreshEntity(floorMahoujin);
   }

   public MysticStaffMahoujinEntity createCircleAtAngle(
      ItemStack stack,
      Player player,
      float size,
      float speed,
      float circle,
      float ground,
      float distance,
      Vec3 pos,
      boolean beam,
      float beamSize,
      boolean aoe,
      MysticStaff.MysticStaffUserStorage storage
   ) {
      float[] colors = this.getColor(player, aoe ? 1 : 0);
      MysticStaffMahoujinEntity m;
      if (aoe) {
         m = new MysticStaffMahoujinEntity(player.level(), player, beam, colors[0], colors[1], colors[2], 0.95F);
         m.setBeamLength(0.0F);
         m.setBeamOffset(0.0F);
      } else {
         m = new MysticStaffMahoujinEntity(player.level(), player, beam, colors[0], colors[1], colors[2], 0.95F);
         m.setBeamLength(240.0F);
         m.setBeamOffset(-distance);
      }

      m.setDistance(distance);
      m.setCircleSize(size);
      m.setRotationSpeed(speed);
      m.angleCircleAroundBlock(circle, ground, distance, pos);
      storage.mahoujins.add(m);
      player.level().addFreshEntity(m);
      if (beam) {
         m.setBeamSize(beamSize);
      }

      return m;
   }

   public Vec3 getBlockPerpToLook(Vec3 target, float playerYaw, float distance) {
      float yaw = playerYaw;
      yaw += 90.0F;
      double plusZ = distance * Math.sin(MysticStaffMahoujinEntity.toRad(yaw));
      double plusX = -distance * Math.cos(MysticStaffMahoujinEntity.toRad(yaw));
      return new Vec3(plusX, 0.0, plusZ).add(target);
   }

   public Vec3 getMiddleCircle(Vec3 target, Vec3 target2, float playerYaw, float distance) {
      double xhalf = (target2.x - target.x) / 2.0;
      double zhalf = (target2.z - target.z) / 2.0;
      double plusZ = distance * Math.sin(MysticStaffMahoujinEntity.toRad(playerYaw));
      double plusX = -distance * Math.cos(MysticStaffMahoujinEntity.toRad(playerYaw));
      plusZ += zhalf + target.z;
      plusX += xhalf + target.x;
      return new Vec3(plusX, target.y, plusZ);
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BOW;
   }

   public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level world, LivingEntity caster, int useTicks) {
      Player player = (Player)caster;
      MysticStaff.MysticStaffUserStorage storage;
      if (staffs.containsKey(player.getUUID())) {
         storage = staffs.get(player.getUUID());
      } else {
         storage = new MysticStaff.MysticStaffUserStorage();
         staffs.put(player.getUUID(), storage);
      }

      if (storage.mahoujins != null && !world.isClientSide()) {
         int radius = MTConfig.MYSTIC_STAFF_BIG_SIZE;
         if (storage.growthStopped) {
            if (Utils.getMode(stack) != 0) {
               if (Utils.getMode(stack) == 1) {
               }
            } else {
               double mana_factor = MTConfig.MYSTIC_STAFF_SCALING_MANA;
               if (Utils.getPlayerMahou(player) != null) {
                  mana_factor *= Utils.getPlayerMahou(player).getMaxMana();
               }

               int mana = (int)mana_factor;
               if (PlayerManaManager.drainMana(player, MTConfig.MYSTIC_STAFF_BIG_MANA_COST + mana, false, false) == MTConfig.MYSTIC_STAFF_BIG_MANA_COST + mana) {
                  double x = storage.target.x;
                  double y = storage.target.y;
                  double z = storage.target.z;
                  Bakuretsu explosion = new Bakuretsu(
                     radius, (float)x, (float)y + (radius / 2 + 2), (float)z, getExplosionDamage(false, Utils.getPlayerMahou(player))
                  );
                  explosion.explosionA(world, player);
                  ModTriggers.BAKURETSU.trigger((ServerPlayer)player);
                  float[] colors = this.getColor(player, 0);

                  for (Player Player : world.players()) {
                     if (Player.distanceToSqr(x, y, z) < 16384.0) {
                        Vec3 vec = null;
                        if (explosion.knockback.containsKey(player)) {
                           vec = explosion.knockback.get(player);
                        }

                        if (vec == null) {
                           PacketHandler.sendTo((ServerPlayer)Player, new BakuretsuPacket(x, y + (radius / 2 + 2), z, radius, 0.0F, 0.0F, 0.0F, colors));
                        } else {
                           PacketHandler.sendTo(
                              (ServerPlayer)Player, new BakuretsuPacket(x, y + (radius / 2 + 2), z, radius, (float)vec.x, (float)vec.y, (float)vec.z, colors)
                           );
                        }
                     }

                     caster.level()
                        .playSound(
                           null,
                           Utils.toBlockPos(storage.target),
                           (SoundEvent)ModSounds.EXPLOSION.get(),
                           SoundSource.NEUTRAL,
                           (float)MTConfig.NOBU_DRUM_VOLUME * 12.0F,
                           1.0F
                        );
                  }

                  for (MysticStaffMahoujinEntity e : storage.mahoujins) {
                     e.discard();
                  }
               }
            }
         }

         storage.mahoujins = new ArrayList<>();
      }

      if (!world.isClientSide()) {
         if (storage.beamMahoujinEntity != null) {
            storage.beamMahoujinEntity.setDying(1);
            storage.beamMahoujinEntity = null;
         }
      }

      return true;
   }

   public static float getExplosionDamage(boolean aoe, IMahou mahou) {
      float factor;
      boolean scales;
      if (aoe) {
         factor = (float)MTConfig.MYSTIC_STAFF_AOE_FACTOR;
         scales = MTConfig.MYSTIC_STAFF_AOE_SCALES;
      } else {
         factor = (float)MTConfig.MYSTIC_STAFF_BIG_FACTOR;
         scales = MTConfig.MYSTIC_STAFF_BIG_SCALES;
      }

      return scales && mahou != null ? mahou.getMaxMana() * factor : factor;
   }

   public float[] getColor(Player caster, int mode) {
      float[] ret = new float[]{0.0F, 0.0F, 0.0F};
      MahouSettings.Spell spell = MahouSettings.Spell.MYSTIC_STAFF_BIG;
      if (mode == 1) {
         spell = MahouSettings.Spell.MYSTIC_STAFF_AOE;
      }

      if (mode == 2) {
         spell = MahouSettings.Spell.MYSTIC_STAFF_BEAM;
      }

      List<Integer> settings = MahouSettings.getIntSettings(caster, spell);
      ret[0] = settings.get(0).intValue() / 255.0F;
      ret[1] = settings.get(1).intValue() / 255.0F;
      ret[2] = settings.get(2).intValue() / 255.0F;
      return ret;
   }

   public static List<BlockPos> getAffectedBlocks(Level world, BlockPos target, int size) {
      List<BlockPos> lst = new ArrayList<>();
      int r = size / 2;
      Stream<BlockPos> box = BlockPos.betweenClosedStream(target.offset(-r, -r, -r), target.offset(r, r, r));
      box.forEach(tmp -> {
         if (getDistance(EffectUtil.fromBlockPos(tmp), EffectUtil.fromBlockPos(target)) < r) {
            lst.add(new BlockPos(tmp.getX(), tmp.getY(), tmp.getZ()));
         }
      });
      return lst;
   }

   public static int getDistance(Vec3 pos1, Vec3 pos2) {
      double x = pos1.x - pos2.x;
      double y = pos1.y - pos2.y;
      double z = pos1.z - pos2.z;
      return (int)Math.abs(Math.ceil(Math.sqrt(x * x + y * y + z * z)));
   }

   public class MysticStaffUserStorage {
      public ArrayList<MysticStaffMahoujinEntity> mahoujins;
      public boolean growthStopped = false;
      public Vec3 target = null;
      public List<Vec3> aoeTargets = null;
      public MysticStaffMahoujinEntity highest = null;
      public MysticStaffMahoujinEntity lowest = null;
      public float playerLook = 0.0F;
      public float playerPitch = 0.0F;
      public int aoeSpawnIndex = 0;
      public MysticStaffBeamMahoujinEntity beamMahoujinEntity = null;
   }
}
