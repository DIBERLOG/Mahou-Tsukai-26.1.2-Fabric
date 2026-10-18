package stepsword.mahoutsukai.entity.mahoujin;

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
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;

public class MysticStaffMahoujinEntity extends MahoujinEntity {
   private static final String TAG_BEAM = "MAHOUTSUKAI_BEAM";
   private static final String TAG_BEAM_SIZE = "MAHOUTSUKAI_BEAM_SIZE";
   private static final String TAG_BEAM_LENGTH = "MAHOUTSUKAI_BEAM_LENGTH";
   private static final String TAG_BEAM_OFFSET = "MAHOUTSUKAI_BEAM_OFFSET";
   private static final String TAG_SHOOTING_TICKS = "MAHOUTSUKAI_SHOOTING_TICKS";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Float> BEAM_SIZE = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> BEAM = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> BEAM_LENGTH = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> BEAM_OFFSET = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> SHOOTING_TICKS = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(MysticStaffMahoujinEntity.class, EntityDataSerializers.FLOAT);
   Vec3 target;
   public static final String entityName = "mahoujin_entity";

   public MysticStaffMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public MysticStaffMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.EXPLOSION_MAHOUJIN.get(), world);
   }

   public MysticStaffMahoujinEntity(Level world, Player caster, boolean hasBeam, float r, float g, float b, float a) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setHasBeam(hasBeam);
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BEAM, false);
      builder.define(BEAM_SIZE, 0.0F);
      builder.define(BEAM_LENGTH, 0.0F);
      builder.define(BEAM_OFFSET, 0.0F);
      builder.define(SHOOTING_TICKS, -1);
      builder.define(DISTANCE, 0.0F);
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
      this.yRot = this.getRotationYaw();
      this.xRot = this.getRotationPitch();
      this.setRot(this.yRot, this.xRot);
      if (!this.level().isClientSide()) {
         if (this.getCasterUUID() == null) {
            this.discard();
         } else {
            Player caster = this.level().getPlayerByUUID(this.getCasterUUID());
            if (caster == null || !caster.isAlive()) {
               this.discard();
            } else if ((!(caster.getUseItem().getItem() instanceof MysticStaff) || this.getShootingTicks() >= 0) && this.target == null) {
               this.discard();
            }
         }
      }
   }

   public void startShoot(Vec3 target) {
      this.setShootingTicks(0);
      this.target = target;
   }


   public void angleCircleAroundBlock(float circleDegrees, float groundDegrees, float distance, Vec3 pos) {
      double height = distance * Math.cos(toRad(groundDegrees));
      double radius = Math.sqrt(distance * distance - height * height);
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
   public void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setHasBeam(input.getBooleanOr("MAHOUTSUKAI_BEAM", false));
      this.setBeamSize(input.getFloatOr("MAHOUTSUKAI_BEAM_SIZE", 0.0F));
      this.setBeamLength(input.getFloatOr("MAHOUTSUKAI_BEAM_LENGTH", 0.0F));
      this.setBeamOffset(input.getFloatOr("MAHOUTSUKAI_BEAM_OFFSET", 0.0F));
      this.setShootingTicks(input.getIntOr("MAHOUTSUKAI_SHOOTING_TICKS", -1));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
   }

   @Override
   public void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("MAHOUTSUKAI_BEAM", this.hasBeam());
      output.putFloat("MAHOUTSUKAI_BEAM_SIZE", this.getBeamSize());
      output.putFloat("MAHOUTSUKAI_BEAM_LENGTH", this.getBeamLength());
      output.putFloat("MAHOUTSUKAI_BEAM_OFFSET", this.getBeamOffset());
      output.putInt("MAHOUTSUKAI_SHOOTING_TICKS", this.getShootingTicks());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return true;
   }

   public boolean shouldRender(double x, double y, double z) {
      return true;
   }

   public boolean hasBeam() {
      return (Boolean)this.entityData.get(BEAM);
   }

   public void setHasBeam(boolean b) {
      this.entityData.set(BEAM, b);
   }

   public float getBeamSize() {
      return (Float)this.entityData.get(BEAM_SIZE);
   }

   public void setBeamSize(float b) {
      this.entityData.set(BEAM_SIZE, b);
   }

   public float getBeamLength() {
      return (Float)this.entityData.get(BEAM_LENGTH);
   }

   public void setBeamLength(float b) {
      this.entityData.set(BEAM_LENGTH, b);
   }

   public float getBeamOffset() {
      return (Float)this.entityData.get(BEAM_OFFSET);
   }

   public void setBeamOffset(float b) {
      this.entityData.set(BEAM_OFFSET, b);
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
