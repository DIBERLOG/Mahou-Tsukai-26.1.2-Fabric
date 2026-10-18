package stepsword.mahoutsukai.entity.mahoujin;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.ModEntityDataSerializers;

public class AuthorityMahoujinEntity extends MahoujinEntity {
   private static final String TAG_ROTATION_SPEED = "MAHOUTSUKAI_ROTATION_SPEED";
   private static final String TAG_CIRCLE_SIZE = "MAHOUTSUKAI_CIRCLE_SIZE";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final String TAG_LOOK_VEC = "MAHOUTSUKAI_LOOK_VEC";
   public static final int loadUpTime = 20;
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static final EntityDataAccessor<Float> ROTATION_SPEED = SynchedEntityData.defineId(AuthorityMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> CIRCLE_SIZE = SynchedEntityData.defineId(AuthorityMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Vec3> LOOK_VEC = SynchedEntityData.defineId(AuthorityMahoujinEntity.class, ModEntityDataSerializers.VEC3);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(AuthorityMahoujinEntity.class, EntityDataSerializers.FLOAT);
   public int casterUUID = 0;
   public static final String entityName = "authority_mahoujin_entity";

   public AuthorityMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public AuthorityMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.AUTHORITY.get(), world);
   }

   public AuthorityMahoujinEntity(Level world, LivingEntity caster) {
      this(world);
      this.casterUUID = caster.getId();
      this.setCasterUUID(caster.getUUID());
   }

   public AuthorityMahoujinEntity(Level world, LivingEntity caster, float r, float g, float b, float a, float size) {
      this(world);
      this.casterUUID = caster.getId();
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
      this.angleForPlayer(caster);
      this.sizer(size);
   }

   public AuthorityMahoujinEntity(Level world, LivingEntity caster, Vec3 look, Vec3 eyes, float r, float g, float b, float a, float size) {
      this(world);
      this.casterUUID = caster.getId();
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
      this.angleForPlayer2(look, eyes);
      this.sizer(size);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ROTATION_SPEED, 0.0F);
      builder.define(CIRCLE_SIZE, 0.0F);
      builder.define(DISTANCE, 0.0F);
      builder.define(LOOK_VEC, Vec3.ZERO);
   }

   @Override
   public void tick() {
      super.tick();
      this.setDeltaMovement(0.0, 0.0, 0.0);
      this.yRotO = this.getYRot();
      if (this.yRotO < 0.0F) {
         this.yRotO += 360.0F;
      }

      this.setRotationYaw(this.getRotationYaw() + this.getRotationSpeed());
      this.setYRot(this.getRotationYaw());
      this.setXRot(this.getRotationPitch());
      this.setRot(this.getYRot(), this.getXRot());
      if (!this.level().isClientSide()) {
         Entity caster = this.getCasterUUID() == null ? this.level().getEntity(this.casterUUID) : ((ServerLevel)this.level()).getEntity(this.getCasterUUID());
         if (caster == null || !caster.isAlive()) {
            this.discard();
         }

         if (this.getLife() >= 20) {
            this.discard();
         }
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public boolean isPickable() {
      return false;
   }



   public void angleForPlayer(LivingEntity user) {
      Vec3 lookVec = user.getDeltaMovement();
      Vec3 vec = lookVec.normalize();
      this.setLookV(lookVec.normalize());
      Vec3 eyes = user.getEyePosition(1.0F);
      this.setPos(eyes.x, eyes.y, eyes.z);
      double theta = Math.asin(vec.y);
      theta = fromRad(theta);
      double theta2 = Math.atan2(vec.x, vec.z);
      theta2 = fromRad(theta2);
      this.setRotationRoll((float)theta2);
      this.setRotationPitch((float)(360.0 - (theta + 90.0)));
   }

   public void angleForPlayer2(Vec3 lookVec, Vec3 eyes) {
      Vec3 vec = lookVec.normalize();
      this.setLookV(lookVec.normalize());
      this.setPos(eyes.x, eyes.y, eyes.z);
      double theta = Math.asin(vec.y);
      theta = fromRad(theta);
      double theta2 = Math.atan2(vec.x, vec.z);
      theta2 = fromRad(theta2);
      this.setRotationRoll((float)theta2);
      this.setRotationPitch((float)(theta + 90.0));
   }

   public static float toRad(float degrees) {
      return degrees * (float) (Math.PI / 180.0);
   }

   public static double fromRad(double rad) {
      return rad * 57.2957795131;
   }

   public void sizer(float s) {
      this.setCircleSize(s);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setRotationSpeed(input.getFloatOr("MAHOUTSUKAI_ROTATION_SPEED", 0.0F));
      this.setCircleSize(input.getFloatOr("MAHOUTSUKAI_CIRCLE_SIZE", 0.0F));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
      this.setLookV(input.read("MAHOUTSUKAI_LOOK_VEC", Vec3.CODEC).orElse(Vec3.ZERO));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_ROTATION_SPEED", this.getRotationSpeed());
      output.putFloat("MAHOUTSUKAI_CIRCLE_SIZE", this.getCircleSize());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
      output.store("MAHOUTSUKAI_LOOK_VEC", Vec3.CODEC, this.getLookV());
   }

   public Vec3 rotateAroundOrigin(Vec3 pos, float pitch, float roll, float yaw) {
      Vec3 rho = this.position();
      return rho.add(pos.subtract(rho).yRot(roll).xRot(pitch).yRot(yaw));
   }

   @Override
   public float getRotationSpeed() {
      return (Float)this.entityData.get(ROTATION_SPEED);
   }

   @Override
   public void setRotationSpeed(float s) {
      this.entityData.set(ROTATION_SPEED, s);
   }

   @Override
   public float getCircleSize() {
      return (Float)this.entityData.get(CIRCLE_SIZE);
   }

   @Override
   public void setCircleSize(float s) {
      this.entityData.set(CIRCLE_SIZE, s);
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
      if (v != null) {
         this.entityData.set(LOOK_VEC, v);
      }
   }
}
