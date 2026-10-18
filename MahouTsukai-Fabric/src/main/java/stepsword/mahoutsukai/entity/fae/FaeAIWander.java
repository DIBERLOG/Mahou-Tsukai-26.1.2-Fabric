package stepsword.mahoutsukai.entity.fae;

import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class FaeAIWander extends Goal {
   protected final PathfinderMob entity;
   protected double x;
   protected double y;
   protected double z;
   public int m = 100;
   protected final double speed;
   protected int executionChance;
   protected boolean mustUpdate;

   public FaeAIWander(PathfinderMob creatureIn, double speedIn) {
      this(creatureIn, speedIn, 120);
   }

   public FaeAIWander(PathfinderMob creatureIn, double speedIn, int chance) {
      this.entity = creatureIn;
      this.speed = speedIn;
      this.executionChance = chance;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.mustUpdate && this.m < 100) {
         return true;
      }

      Vec3 Vec3 = null;
      Vec3 = this.getPosition();
      if (Vec3 == null) {
         return false;
      }

      this.x = Vec3.x;
      this.y = Vec3.y;
      this.z = Vec3.z;
      this.mustUpdate = false;
      if (this.m > 100) {
         this.m = 0;
      }

      return true;
   }

   @Nullable
   protected Vec3 getPosition() {
      int r = 10;
      int r2 = 10;
      int yr = 7;
      int p = this.entity.getRandom().nextInt(4) - 2;
      int p2 = this.entity.getRandom().nextInt(4) - 2;
      int p3 = this.entity.getRandom().nextInt(4) - 2;
      if (this.entity.getRandom().nextBoolean()) {
         r = -r;
      }

      if (this.entity.getRandom().nextBoolean()) {
         r2 = -r2;
      }

      return this.entity.position().add(p + r, p3, p2 + r2);
   }

   public void tick() {
      this.m++;
   }

   public boolean canContinueToUse() {
      return !this.entity.getNavigation().isDone();
   }

   public void start() {
      this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public void setExecutionChance(int newchance) {
      this.executionChance = newchance;
   }
}
