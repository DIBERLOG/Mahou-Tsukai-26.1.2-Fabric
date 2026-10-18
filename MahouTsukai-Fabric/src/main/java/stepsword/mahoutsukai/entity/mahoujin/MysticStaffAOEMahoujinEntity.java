package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;

public class MysticStaffAOEMahoujinEntity extends MahoujinEntity {
   private static final String TAG_SHOOTING_TICKS = "MAHOUTSUKAI_SHOOTING_TICKS";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Integer> SHOOTING_TICKS = SynchedEntityData.defineId(MysticStaffAOEMahoujinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(MysticStaffAOEMahoujinEntity.class, EntityDataSerializers.FLOAT);
   public float startYaw = 0.0F;
   public float startPitch = 0.0F;
   public Vec3 startVelocity = new Vec3(0.0, 0.0, 0.0);
   public static final String entityName = "aoe_mahoujin_entity";

   public MysticStaffAOEMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public MysticStaffAOEMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.AOE_EXPLOSION_MAHOUJIN.get(), world);
   }

   public MysticStaffAOEMahoujinEntity(Level world, Player caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
   }

   public MysticStaffAOEMahoujinEntity(Level world, Player caster, float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
      this(world);
      this.startPitch = caster.xRot;
      this.startYaw = caster.yRot;
      this.startVelocity = caster.getViewVector(1.0F);
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, r2, g2, b2, a2);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOTING_TICKS, -1);
      builder.define(DISTANCE, 0.0F);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.getLife() == 10) {
            float[] colors = this.getColors();
            BeamProjectileEntity bpe = new BeamProjectileEntity(
               this.level(), this.getCasterUUID(), colors[0], colors[1], colors[2], 0.3F, colors[4], colors[5], colors[6], 1.0F, 0.15F, 0.0F
            );
            Vec3 v = this.startVelocity.normalize().scale(7.0);
            bpe.setDeltaMovement(v.x, v.y, v.z);
            bpe.setPos(this.getX(), this.getY(), this.getZ());
            bpe.setRotationYaw(180.0F - this.startYaw);
            bpe.setRotationPitch(90.0F - this.startPitch);
            this.level().addFreshEntity(bpe);
         }

         if (this.getLife() > 20) {
            this.discard();
         }
      }
   }


   public static float toRad(float degrees) {
      return degrees * (float) (Math.PI / 180.0);
   }

   public void sizer(float f) {
      this.setCircleSize(f);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setShootingTicks(input.getIntOr("MAHOUTSUKAI_SHOOTING_TICKS", -1));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("MAHOUTSUKAI_SHOOTING_TICKS", this.getShootingTicks());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return true;
   }

   public boolean shouldRender(double x, double y, double z) {
      return true;
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3 Vec3 = new Vec3(p_70186_1_, p_70186_3_, p_70186_5_)
         .normalize()
         .add(
            this.random.nextGaussian() * 0.0075F * p_70186_8_,
            this.random.nextGaussian() * 0.0075F * p_70186_8_,
            this.random.nextGaussian() * 0.0075F * p_70186_8_
         )
         .scale(p_70186_7_);
      this.setDeltaMovement(Vec3);
      float f = Mth.sqrt((float)Vec3.horizontalDistanceSqr());
      this.yRot = (float)(Mth.atan2(Vec3.x, Vec3.z) * 180.0F / (float)Math.PI);
      this.xRot = (float)(Mth.atan2(Vec3.y, f) * 180.0F / (float)Math.PI);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
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
