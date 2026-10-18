package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import stepsword.mahoutsukai.block.BlackFireBlock;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.networking.BlackFirePacket;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class BlackFlameEyesPotion extends EyesPotion implements ExtraPotionInterface {
   protected BlackFlameEyesPotion() {
      super(ModEffects.getColorNumber(116, 232, 0));
   }

   @Override
   public void onRemove(LivingEntity player, MobEffect effect) {
      if (effect instanceof BlackFlameEyesPotion && player instanceof Player) {
         IMahou mahou = Utils.getPlayerMahou((Player)player);
         mahou.setPlayerPrevYaw(0.0F);
         mahou.setPlayerPrevPitch(0.0F);
         mahou.setPlayerPrevEyePos(null);
      }
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
   }

   public static void blackFlameEyesPlayerTick(Player player) {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null && EffectUtil.hasBuff(player, ModEffects.BLACK_FLAME_EYES)) {
         Entity e = Utils.selectEntityNearCursor(
            player,
            MTConfig.BLACK_FLAME_RANGE_FROM_USER,
            player.level(),
            a -> Utils.getPredicate(new java.util.HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR)), a, player),
            Utils.nearAllowed(new java.util.HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR)))
         );
         if (e instanceof LivingEntity) {
            EffectUtil.buff((LivingEntity)e, ModEffects.BLACK_BURNING, false, MTConfig.BLACK_FLAME_DEBUFF_TIME);
            notifyIfBurning((LivingEntity)e);
         } else {
            float prevYaw = mahou.getPlayerPrevYaw();
            float prevPitch = mahou.getPlayerPrevPitch();
            Vec3 prevEye = mahou.getPlayerPrevEyePos();
            if (prevYaw == 0.0F) {
               prevYaw = player.getYRot();
            }

            if (prevPitch == 0.0F) {
               prevPitch = player.getXRot();
            }

            if (prevEye == null) {
               prevEye = player.getEyePosition(1.0F);
            }

            float yawDistance = player.getYRot() - prevYaw;
            float pitchDistance = player.getXRot() - prevPitch;
            Vec3 eyeDistance = player.getEyePosition(1.0F).subtract(prevEye);

            for (int i = 1; i < 21; i++) {
               BlockHitResult result = rayTrace(
                  MTConfig.BLACK_FLAME_RANGE_FROM_USER,
                  player,
                  eyeDistance.scale(i / 20.0F).add(prevEye),
                  yawDistance * i / 20.0F + prevYaw,
                  pitchDistance * i / 20.0F + prevPitch
               );
               if (result != null && result.getType() != Type.MISS) {
                  BlockPos pos = result.getBlockPos();
                  Direction hit = result.getDirection();
                  if (Utils.isBlockAir(player.level(), pos.relative(hit)) && player.level().getBlockState(pos).getBlock() != ModBlocks.blackFireBlock.get()) {
                     EffectUtil.tryChangeBlockState(
                        false, pos.relative(hit), ((BlackFireBlock)ModBlocks.blackFireBlock.get()).defaultBlockState(), player.level(), player
                     );
                  }
               }
            }

            mahou.setPlayerPrevYaw(player.getYRot());
            mahou.setPlayerPrevPitch(player.getXRot());
            mahou.setPlayerPrevEyePos(player.getEyePosition(1.0F));
         }
      }
   }

   public static void blackFlameEyesLivingUpdate(LivingEntity entity) {
      if (entity != null
         && entity.isAlive()
         && !entity.level().isClientSide()
         && (entity.level().getGameTime() % 50000L) % 20L == 0L
         && EffectUtil.hasBuff(entity, ModEffects.BLACK_BURNING)) {
         entity.hurtServer((ServerLevel)entity.level(),entity.damageSources().wither(), (float)MTConfig.BLACK_FLAME_DAMAGE);
         ((stepsword.mahoutsukai.fabric.mixin.LivingCombatAccessor)entity).mahoutsukai$setLastHurtByPlayerMemoryTime(100);
      }
   }

   public static BlockHitResult rayTrace(double blockReachDistance, Player player, Vec3 v, float yaw, float pitch) {
      Vec3 look = Vec3.directionFromRotation(pitch, yaw);
      Vec3 end = v.add(look.scale(blockReachDistance));
      return player.level().clip(new ClipContext(v, end, Block.OUTLINE, Fluid.NONE, player));
   }

   public static void notifyIfBurning(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (EffectUtil.hasBuff(e, ModEffects.BLACK_BURNING)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotBurning(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            for(var observer:net.fabricmc.fabric.api.networking.v1.PlayerLookup.tracking(e)) net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(observer,new BlackFirePacket(e.getId(),r));
         }
      }
   }
}
