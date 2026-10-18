package stepsword.mahoutsukai.entity.mahoujin;

import java.util.HashSet;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.ModEntityDataSerializers;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RhoAiasMahoujinEntity extends MahoujinEntity {
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final String TAG_LOOK_VEC = "MAHOUTSUKAI_LOOK_VEC";
   public static final int loadUpTime = 20;
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 1.0E-4);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Vec3> LOOK_VEC = SynchedEntityData.defineId(RhoAiasMahoujinEntity.class, ModEntityDataSerializers.VEC3);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(RhoAiasMahoujinEntity.class, EntityDataSerializers.FLOAT);
   public static final String entityName = "rho_aias_mahoujin_entity";

   public RhoAiasMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public RhoAiasMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.RHO_AIAS.get(), world);
   }

   public RhoAiasMahoujinEntity(Level world, Player caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
   }

   public RhoAiasMahoujinEntity(Level world, Player caster, float r, float g, float b, float a, float r2, float g2, float b2, float size) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, r2, g2, b2);
      this.angleForPlayer(caster);
      this.setCircleSize(size);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DISTANCE, 0.0F);
      builder.define(LOOK_VEC, Vec3.ZERO);
   }

   public float getSphereMidRadius() {
      return 7.8F;
   }

   @Override
   public void tick() {
      super.tick();
      this.setDeltaMovement(0.0, 0.0, 0.0);
      this.yRotO = this.getRotationYaw();
      if (this.yRotO < 0.0F) {
         this.yRotO += 360.0F;
      }

      this.setRotationYaw(this.getRotationYaw() + this.getRotationSpeed());
      this.setYRot(this.getRotationYaw());
      this.setXRot(this.getRotationPitch());
      this.setRot(this.getYRot(), this.getXRot());
      if (!this.level().isClientSide()) {
         if (this.getCasterUUID() == null
            || this.level().getPlayerByUUID(this.getCasterUUID()) == null
            || !this.level().getPlayerByUUID(this.getCasterUUID()).isAlive()) {
            this.discard();
         }

         if (this.getLife() >= MTConfig.RHO_AIAS_LIFE) {
            this.discard();
         }
      }

      this.projectileDeletion();
      this.collideWithNearbyEntities();
   }

   public boolean isPickable() {
      return false;
   }

   public void playerTouch(Player entityIn) {
   }

   public void setColor(float r, float g, float b, float a, float r2, float g2, float b2) {
      super.setColor(r, g, b, a, r2, g2, b2, 1.0F);
   }

   @Override
   public float[] getColors() {
      return super.getColors();
   }

   public void angleForPlayer(Player user) {
      Vec3 vec = user.getLookAngle().normalize().scale(5.0);
      this.setLookV(user.getLookAngle().normalize());
      Vec3 eyes = user.getEyePosition(1.0F);
      this.setPos(eyes.x + vec.x, eyes.y + vec.y, eyes.z + vec.z);
      double theta = Math.asin(vec.y / 5.0);
      theta = fromRad(theta);
      this.setRotationPitch((float)(90.0 - theta));
      this.setRotationRoll(360.0F - user.getYRot());
   }

   public static double fromRad(double rad) {
      return rad * 57.2957795131;
   }

   protected void collideWithNearbyEntities() {
      float size = this.getCircleSize();
      AABB maabb = new AABB(this.getX() - size, this.getY() - size, this.getZ() - size, this.getX() + size, this.getY() + size, this.getZ() + size);
      List<Entity> list = this.level().getEntities(this, maabb.inflate(2.0), Entity::isAlive);
      if (!list.isEmpty()) {
         for (Entity entity : list) {
            if (!MTConfig.RHO_AIAS_SNEAK_BOOP || !entity.getUUID().equals(this.getCasterUUID()) || entity.isDiscrete()) {
               int collide = this.doesItCollide(entity);
               if (collide == 3 || collide == 1) {
                  this.collideWithEntity(entity);
               }
            }
         }
      }
   }

   public int doesItCollide(Entity entity) {
      AABB aabb = entity.getBoundingBox();
      boolean flag = false;
      boolean flag1 = false;
      if (aabb != null) {
         Vec3 a1 = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
         Vec3 a2 = new Vec3(aabb.minX, aabb.minY, aabb.maxZ);
         Vec3 a3 = new Vec3(aabb.maxX, aabb.minY, aabb.minZ);
         Vec3 a4 = new Vec3(aabb.maxX, aabb.minY, aabb.maxZ);
         Vec3 b1 = new Vec3(aabb.minX, aabb.maxY, aabb.minZ);
         Vec3 b2 = new Vec3(aabb.minX, aabb.maxY, aabb.maxZ);
         Vec3 b3 = new Vec3(aabb.maxX, aabb.maxY, aabb.minZ);
         Vec3 b4 = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
         int f1 = this.whereIsPoint(a1);
         int f2 = this.whereIsPoint(a2);
         int f3 = this.whereIsPoint(a3);
         int f4 = this.whereIsPoint(a4);
         int f5 = this.whereIsPoint(b1);
         int f6 = this.whereIsPoint(b2);
         int f7 = this.whereIsPoint(b3);
         int f8 = this.whereIsPoint(b4);
         flag = f1 == 1 || f2 == 1 || f3 == 1 || f4 == 1 || f5 == 1 || f6 == 1 || f7 == 1 || f8 == 1;
         flag1 = f1 == 2 || f2 == 2 || f3 == 2 || f4 == 2 || f5 == 2 || f6 == 2 || f7 == 2 || f8 == 2;
      }

      if (flag && flag1) {
         return 3;
      } else if (flag) {
         return 1;
      } else {
         return flag1 ? 2 : 0;
      }
   }

   public void projectileDeletion() {
      if (!this.level().isClientSide()) {
         int radius = MTConfig.RHO_AIAS_KILL_RANGE;
         double posX = this.getX();
         double posY = this.getY();
         double posZ = this.getZ();
         AABB paabb = new AABB(posX - radius, posY - radius, posZ - radius, posX + radius, posY + radius, posZ + radius);
         List<? extends String> killList = MTConfig.RHO_AIAS_ENTITY_KILL_LIST;
         List<? extends String> collideList = MTConfig.RHO_AIAS_COLLIDE_KILL_LIST;
         if (!killList.isEmpty() || !collideList.isEmpty()) {
            HashSet<String> set = new HashSet<>(killList);
            HashSet<String> set2 = new HashSet<>(collideList);

            for (Entity e : this.level().getEntitiesOfClass(Entity.class, paabb)) {
               if (!(e instanceof Player) && !(e instanceof RhoAiasMahoujinEntity)) {
                  String regname = Utils.getRegistryName(e);
                  if (set.contains(regname)) {
                     Vec3 v = e.getDeltaMovement();
                     Vec3 p = e.position();
                     if (this.isPointInRadiusOfCircle(p.add(v)) && !this.isPointInRadiusOfCircle(p)) {
                        this.getRidOfEntity(e);
                     }

                     if (!this.isPointInRadiusOfCircle(p.add(v.scale(-1.0))) && this.isPointInRadiusOfCircle(p)) {
                        this.getRidOfEntity(e);
                     }
                  }

                  if (set2.contains(regname)) {
                     int z = this.doesItCollide(e);
                     if (z == 1 || z == 3) {
                        this.getRidOfEntity(e);
                     }
                  }
               }
            }
         }
      }
   }

   public void getRidOfEntity(Entity e) {
      e.discard();
   }

   protected void collideWithEntity(Entity entityIn) {
      if (entityIn instanceof Arrow arrow) {
         if (arrow.getOwner() == null || !arrow.getOwner().getUUID().equals(this.getCasterUUID())) {
            arrow.setDeltaMovement(0.0, 0.0, 0.0);
            arrow.life = 3;
            arrow.setYRot(this.getRotationYaw());
            arrow.setXRot(this.getRotationPitch());
            arrow.hurtMarked = true;
         }
      } else if (entityIn instanceof Projectile throwable) {
         Entity thrower = throwable.getOwner();
         if (thrower == null || !thrower.getUUID().equals(this.getCasterUUID())) {
            throwable.setDeltaMovement(new Vec3(-throwable.getDeltaMovement().x, -throwable.getDeltaMovement().y, -throwable.getDeltaMovement().z));
            if (throwable instanceof AbstractHurtingProjectile e) {
               e.accelerationPower = -e.accelerationPower;
            }

            if (this.getCasterUUID() != null && this.level().getPlayerByUUID(this.getCasterUUID()) != null) {
               throwable.setOwner(this.level().getPlayerByUUID(this.getCasterUUID()));
            }
         }
      } else if (entityIn instanceof LivingEntity && this.getLookV() != null) {
         Vec3 v = this.getLookV();
         this.boop(entityIn, 1.0F, -v.x, -v.y, -v.z);
      }
   }

   private void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      Vec3 motion = entity.getDeltaMovement();
      double motionX = motion.x();
      double motionY = motion.y();
      double motionZ = motion.z();
      motionX /= 2.0;
      motionZ /= 2.0;
      motionY /= 2.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      entity.hurtMarked = true;
      if (entity.onGround()) {
         motionY /= 2.0;
         motionY += strength;
         if (motionY > 0.4F) {
            motionY = 0.4F;
         }
      }

      entity.setDeltaMovement(motionX, motionY, motionZ);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
      this.setLookV(input.read("MAHOUTSUKAI_LOOK_VEC", Vec3.CODEC).orElse(Vec3.ZERO));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
      output.store("MAHOUTSUKAI_LOOK_VEC", Vec3.CODEC, this.getLookV());
   }

   public boolean isPointInRadiusOfCircle(Vec3 pos) {
      Vec3 sphereMid = this.getSphereMid();
      double distance = pos.distanceTo(sphereMid);
      return distance < this.getSphereMidRadius();
   }

   public boolean isPointInShieldRectangle(Vec3 pos) {
      float xside = 4.0F;
      float yside = 6.0F;
      float zside = 6.0F;
      Vec3 rotatedpos = this.rotateAroundOrigin(pos, EffectUtil.toRad(90.0F - this.getRotationPitch()), EffectUtil.toRad(-this.getRotationRoll() + 90.0F), 0.0F);
      return rotatedpos.x > this.getX() - xside
         && rotatedpos.x < this.getX() + 0.5
         && rotatedpos.y > this.getY() - yside
         && rotatedpos.y < this.getY() + yside
         && rotatedpos.z > this.getZ() - zside
         && rotatedpos.z < this.getZ() + zside;
   }

   public Vec3 getSphereMid() {
      return this.position().subtract(this.getLookV().normalize().scale(this.getSphereMidRadius() - 0.5));
   }

   public int whereIsPoint(Vec3 pos) {
      if (!this.isPointInShieldRectangle(pos)) {
         return 0;
      } else {
         return this.isPointInRadiusOfCircle(pos) ? 1 : 2;
      }
   }

   public Vec3 rotateAroundOrigin(Vec3 pos, float pitch, float roll, float yaw) {
      Vec3 rho = this.position();
      return rho.add(this.rotateRoll(pos.subtract(rho).yRot(roll), pitch));
   }

   public Vec3 rotateRoll(Vec3 v, float roll) {
      float f = Mth.cos(roll);
      float f1 = Mth.sin(roll);
      double d0 = v.x * f + v.y * f1;
      double d1 = v.y * f - v.x * f1;
      double d2 = v.z;
      return new Vec3(d0, d1, d2);
   }

   public float getDistance() {
      return (Float)this.entityData.get(DISTANCE);
   }

   public void setDistance(float d) {
      this.entityData.set(DISTANCE, d);
   }

   public Vec3 getLookV() {
      return (Vec3)this.entityData.get(LOOK_VEC);
   }

   public void setLookV(Vec3 v) {
      this.entityData.set(LOOK_VEC, v);
   }
}
