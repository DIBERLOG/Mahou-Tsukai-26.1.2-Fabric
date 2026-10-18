package stepsword.mahoutsukai.entity.mahoujin;

import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.spells.mystic.SpatialDisorientation.SpatialDisorientationStaff;

public class SpatialDisorientationMahoujinEntity extends MahoujinEntity {
   private static final String TAG_BEAM_SIZE = "MAHOUTSUKAI_BEAM_SIZE";
   private static final String TAG_SHOOTING_TICKS = "MAHOUTSUKAI_SHOOTING_TICKS";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   public static final int loadUpTime = 20;
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Float> BEAM_SIZE = SynchedEntityData.defineId(SpatialDisorientationMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> SHOOTING_TICKS = SynchedEntityData.defineId(
      SpatialDisorientationMahoujinEntity.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(SpatialDisorientationMahoujinEntity.class, EntityDataSerializers.FLOAT);
   public static final String entityName = "spatial_mahoujin_entity";

   public SpatialDisorientationMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public SpatialDisorientationMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.SPATIAL_DISORIENTATION.get(), world);
   }

   public SpatialDisorientationMahoujinEntity(Level world, Player caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
   }

   public SpatialDisorientationMahoujinEntity(Level world, Player caster, float r, float g, float b, float a) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BEAM_SIZE, 0.0F);
      builder.define(SHOOTING_TICKS, -1);
      builder.define(DISTANCE, 0.0F);
   }

   @Override
   public void tick() {
      this.setDeltaMovement(0.0, 0.0, 0.0);
      if (this.getLife() < 20) {
         this.setLife(this.getLife() + 1);
      }

      this.yRotO = this.getRotationYaw();
      if (this.yRotO < 0.0F) {
         this.yRotO += 360.0F;
      }

      this.setRotationYaw(this.getRotationYaw() + this.getRotationSpeed());
      this.yRot = this.getRotationYaw();
      this.xRot = this.getRotationPitch();
      this.setRot(this.yRot, this.xRot);
      if (!this.level().isClientSide()) {
         UUID casterUUID = this.getCasterUUID();
         if (casterUUID == null) {
            this.discard();
         } else {
            Player caster = this.level().getPlayerByUUID(casterUUID);
            if (caster == null || !caster.isAlive() || !(caster.getUseItem().getItem() instanceof SpatialDisorientationStaff)) {
               this.discard();
            }
         }
      }
   }

   public void angleCircleAroundBlock(float circleDegrees, float groundDegrees, float distance, Vec3 pos) {
      double height = distance * Math.cos(toRad(groundDegrees));
      if (height > distance) {
         height = distance;
      }

      double radius = Math.sqrt(distance * distance - height * height);
      if (Double.isNaN(radius)) {
         radius = 0.0;
      }

      double zplus = radius * Math.sin(toRad(circleDegrees));
      double xplus = -radius * Math.cos(toRad(circleDegrees));
      this.setRotationPitch(groundDegrees);
      this.setRotationRoll(circleDegrees - 90.0F);
      this.setPos(pos.x + xplus, pos.y + height, pos.z + zplus);
   }

   public static float toRad(float degrees) {
      return degrees * (float) (Math.PI / 180.0);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setBeamSize(input.getFloatOr("MAHOUTSUKAI_BEAM_SIZE", 0.0F));
      this.setShootingTicks(input.getIntOr("MAHOUTSUKAI_SHOOTING_TICKS", -1));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_BEAM_SIZE", this.getBeamSize());
      output.putInt("MAHOUTSUKAI_SHOOTING_TICKS", this.getShootingTicks());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
   }

   public float getBeamSize() {
      return (Float)this.entityData.get(BEAM_SIZE);
   }

   public void setBeamSize(float b) {
      this.entityData.set(BEAM_SIZE, b);
   }

   public int getShootingTicks() {
      return (Integer)this.entityData.get(SHOOTING_TICKS);
   }

   public void setShootingTicks(int t) {
      this.entityData.set(SHOOTING_TICKS, t);
   }

   public float getDistance() {
      return (Float)this.entityData.get(DISTANCE);
   }

   public void setDistance(float d) {
      this.entityData.set(DISTANCE, d);
   }
}
