package stepsword.mahoutsukai.entity.fae;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

import net.minecraft.world.phys.Vec3;

public class FaeMoveControl extends MoveControl {
   private static final double SPEED_SCALE = 0.25;
   private static final double MAX_ACCELERATION = 0.008;
   private static final float MAX_YAW_CHANGE = 6.0F;

   public FaeMoveControl(Mob mob) {
      super(mob);
   }

   public void tick() {
      this.mob.setNoGravity(true);
      if (this.operation == Operation.MOVE_TO) {
         this.operation = Operation.WAIT;
         Vec3 offset = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
         double distance = offset.length();
         if (!(distance < 1.0E-4)) {
            double desiredSpeed = this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED) * 0.25;
            desiredSpeed *= Math.min(1.0, distance / 2.0);
            Vec3 desiredVelocity = offset.scale(desiredSpeed / distance);
            Vec3 steering = desiredVelocity.subtract(this.mob.getDeltaMovement());
            if (steering.lengthSqr() > 6.4E-5) {
               steering = steering.normalize().scale(0.008);
            }

            Vec3 newVelocity = this.mob.getDeltaMovement().add(steering);
            this.mob.setDeltaMovement(newVelocity);
            double horizontalSpeedSqr = newVelocity.x * newVelocity.x + newVelocity.z * newVelocity.z;
            if (horizontalSpeedSqr > 1.0E-6) {
               float desiredYaw = (float)(Mth.atan2(newVelocity.z, newVelocity.x) * 180.0F / (float)Math.PI) - 90.0F;
               this.mob.setYRot(this.rotlerp(this.mob.getYRot(), desiredYaw, 6.0F));
               this.mob.yBodyRot = this.mob.getYRot();
            }
         }
      }
   }
}
