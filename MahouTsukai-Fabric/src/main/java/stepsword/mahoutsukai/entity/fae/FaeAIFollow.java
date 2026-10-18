package stepsword.mahoutsukai.entity.fae;

import com.google.common.base.Predicate;
import java.util.EnumSet;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class FaeAIFollow extends Goal {
   private final PathfinderMob entity;
   private final Predicate<LivingEntity> followPredicate;
   private LivingEntity followingEntity;
   private final double speedModifier;
   private final PathNavigation navigation;
   private final float fard;
   private final float neard;
   private float oldWaterCost;
   private final float areaSize;
   private int timeFollowing;
   private boolean orbitClockwise;
   private static final double CHASE_SPEED = 0.24;
   private static final double ORBIT_SPEED = 0.16;
   private static final double MAX_STEERING_ACCELERATION = 0.012;

   public FaeAIFollow(final PathfinderMob e, double speed, float far, float area, float near) {
      this.entity = e;
      this.followPredicate = new Predicate<LivingEntity>() {
         public boolean apply(@Nullable LivingEntity r) {
            return r != null && e != r;
         }
      };
      this.speedModifier = speed;
      this.navigation = e.getNavigation();
      this.fard = far;
      this.neard = near;
      this.areaSize = area;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.followingEntity == null || Math.random() > 0.05) {
         List<LivingEntity> list = this.entity
            .level()
            .getEntitiesOfClass(LivingEntity.class, this.entity.getBoundingBox().inflate(this.areaSize), this.followPredicate);
         if (!list.isEmpty()) {
            for (LivingEntity creature : list) {
               if (!creature.isInvisible() && (!(creature instanceof Player) || EffectUtil.hasBuff(creature, ModEffects.FAY_SIGHT_EYES))) {
                  this.followingEntity = creature;
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.followingEntity != null
         && this.followingEntity.isAlive()
         && this.timeFollowing <= 200
         && this.entity.distanceToSqr(this.followingEntity) < this.fard * this.fard;
   }

   public void start() {
      this.timeFollowing = 0;
      this.orbitClockwise = this.entity.getRandom().nextBoolean();
      this.oldWaterCost = this.entity.getPathfindingMalus(PathType.WATER);
      this.entity.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public void stop() {
      this.timeFollowing = 0;
      this.followingEntity = null;
      this.navigation.stop();
      this.entity.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
   }

   public void tick() {
      if (this.followingEntity != null && !this.entity.isLeashed()) {
         this.timeFollowing++;
         Vec3 motion = this.entity.getDeltaMovement();
         double motionX = motion.x;
         double motionY = motion.y;
         double motionZ = motion.z;
         this.entity.getLookControl().setLookAt(this.followingEntity, 10.0F, this.entity.getMaxHeadXRot());
         double fromTargetX = this.entity.getX() - this.followingEntity.getX();
         double fromTargetZ = this.entity.getZ() - this.followingEntity.getZ();
         double horizontalDistance = Math.sqrt(fromTargetX * fromTargetX + fromTargetZ * fromTargetZ);
         if (horizontalDistance > 1.0E-4) {
            double outwardX = fromTargetX / horizontalDistance;
            double outwardZ = fromTargetZ / horizontalDistance;
            double desiredX;
            double desiredZ;
            if (horizontalDistance < this.neard) {
               double orbitDirection = this.orbitClockwise ? 1.0 : -1.0;
               double tangentX = -outwardZ * orbitDirection;
               double tangentZ = outwardX * orbitDirection;
               double orbitDistance = this.neard * 0.75;
               double radialSpeed = Math.max(-0.12, Math.min(0.12, -(horizontalDistance - orbitDistance) * 0.08));
               desiredX = (tangentX * 0.16 + outwardX * radialSpeed) * this.speedModifier;
               desiredZ = (tangentZ * 0.16 + outwardZ * radialSpeed) * this.speedModifier;
            } else {
               desiredX = -outwardX * 0.24 * this.speedModifier;
               desiredZ = -outwardZ * 0.24 * this.speedModifier;
            }

            double steeringX = desiredX - motionX;
            double steeringZ = desiredZ - motionZ;
            double steeringLength = Math.sqrt(steeringX * steeringX + steeringZ * steeringZ);
            double maxAcceleration = 0.012 * this.speedModifier;
            if (steeringLength > maxAcceleration) {
               steeringX = steeringX / steeringLength * maxAcceleration;
               steeringZ = steeringZ / steeringLength * maxAcceleration;
            }

            motionX += steeringX;
            motionZ += steeringZ;
         }

         this.entity.setDeltaMovement(motionX, motionY, motionZ);
      }
   }
}
