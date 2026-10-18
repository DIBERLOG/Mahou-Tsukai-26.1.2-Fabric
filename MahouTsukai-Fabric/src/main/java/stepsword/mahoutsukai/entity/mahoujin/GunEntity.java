package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class GunEntity extends MahoujinEntity {
   private static final String TAG_SHOOTING_TICKS = "MAHOUTSUKAI_SHOOTING_TICKS";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Integer> SHOOTING_TICKS = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> PITCH_COOLDOWN = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> RECOILING_TICKS = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> RECOVERY_TICKS = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(GunEntity.class, EntityDataSerializers.FLOAT);
   public float startYaw = 0.0F;
   public float startPitch = 0.0F;
   public GunBeamProjectileEntity mybeam = null;
   public Vec3 startVelocity = new Vec3(0.0, 0.0, 0.0);
   public float goalPitch = 0.0F;
   public int sound = 0;
   public Vec3 targetPos = null;
   public boolean autonomous = false;
   public int autonomousLife = 0;

   public GunEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public GunEntity(Level world) {
      super((EntityType<?>)ModEntities.GUN_ENTITY.get(), world);
   }

   public GunEntity(Level world, Player caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
   }

   public GunEntity(Level world, Player caster, float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
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
      builder.define(TARGET, 0);
      builder.define(PITCH_COOLDOWN, 0);
      builder.define(RECOILING_TICKS, 0);
      builder.define(RECOVERY_TICKS, 0);
      builder.define(DISTANCE, 0.0F);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.aimassist();
         this.firing();
         Vec3 moveDelta = this.getDeltaMovement();
         Vec3 moveTarget = this.position().add(moveDelta);
         this.setPos(moveTarget.x, moveTarget.y, moveTarget.z);
      } else {
         this.pitchCD();
         this.aimassist();
         this.firing();
         this.seppuku();
         Vec3 moveDelta = this.getDeltaMovement();
         Vec3 moveTarget = this.position().add(moveDelta);
         this.setPos(moveTarget.x, moveTarget.y, moveTarget.z);
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void adjustPitch(boolean up) {
      this.goalPitch = (float)(!up ? -MTConfig.NOBU_PITCH_INCREMENT : MTConfig.NOBU_PITCH_INCREMENT);
      this.setPitchCooldown((int)MTConfig.NOBU_PITCH_INCREMENT);
   }

   public void pitchCD() {
      float TIME_PER_DEGREE = (float)MTConfig.NOBU_ADJUSTMENT_TICKS_PER_DEGREE;
      float speed = 1.0F / TIME_PER_DEGREE;
      int p = this.getPitchCooldown();
      if (p > 0) {
         this.setPitchCooldown(p - 1);
      }

      if (this.goalPitch > speed) {
         this.goalPitch -= speed;
         this.setRotationPitch(this.getRotationPitch() - 1.0F / TIME_PER_DEGREE);
      } else if (this.goalPitch < -speed) {
         this.goalPitch += speed;
         this.setRotationPitch(this.getRotationPitch() + 1.0F / TIME_PER_DEGREE);
      }
   }

   public void firing() {
      int recoilingTicks = this.getRecoilingTicks();
      int recoveryTicks = this.getRecoveryTicks();
      if (recoilingTicks == 0) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
      }

      if (recoilingTicks > 0) {
         this.setRecoilingTicks(recoilingTicks - 1);
         if (this.targetPos == null) {
            this.moveAlongAxisNoTarget(-1.0);
         } else {
            this.moveAlongAxis(-1.0);
         }

         if (this.getRecoilingTicks() == 0) {
            this.setRecoveryTicks(10 * MTConfig.NOBU_RECOIL_TIME);
         }
      } else if (recoveryTicks > 0) {
         this.setRecoveryTicks(recoveryTicks - 1);
         if (this.targetPos == null) {
            this.moveAlongAxisNoTarget(0.1);
         } else {
            this.moveAlongAxis(0.1);
         }
      } else if (!this.level().isClientSide() && this.getLife() >= 10 && (this.mybeam == null || !this.mybeam.isAlive())) {
         this.spawnBeam();
      }

      if (this.mybeam != null && !this.mybeam.getFired()) {
         this.moveAimBeam(this.mybeam);
      }
   }

   public void aimassist() {
      Entity target = null;
      if (this.getTarget() > 0) {
         target = this.level().getEntity(this.getTarget());
      }

      if (target != null) {
         this.targetPos = target.getEyePosition();
         this.aimAt(this.targetPos);
      }
   }

   public void seppuku() {
      Player caster = this.getCaster();
      if (caster == null) {
         this.discard();
      } else {
         if (!this.autonomous) {
            if (!(caster.getUseItem().getItem() instanceof Nobu) || Utils.getMode(caster.getUseItem()) == 0) {
               this.discard();
            }
         } else {
            if (caster.getUseItem().getItem() instanceof Nobu && Utils.getMode(caster.getUseItem()) != 0) {
               this.autonomousLife = 0;
            } else {
               this.autonomousLife++;
            }

            if (this.autonomousLife > MTConfig.NOBU_AUTONOMOUS_LIFE_TICKS) {
               this.discard();
               if (Nobu.player_storage.containsKey(this.getCasterUUID())) {
                  Nobu.player_storage.get(this.getCasterUUID()).guns.values().remove(this);
               }
            }
         }

         if (Nobu.player_storage.containsKey(this.getCasterUUID())) {
            Nobu.NobuUserStorage storage = Nobu.player_storage.get(this.getCasterUUID());
            if (!storage.guns.containsValue(this)) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   public Player getCaster() {
      return this.getCasterUUID() != null ? this.level().getPlayerByUUID(this.getCasterUUID()) : null;
   }

   public void spawnBeam() {
      Player player = this.getCaster();
      if (player != null) {
         Vec3 spawn = this.position();
         float[] colors = Nobu.getColor(player, 0);
         GunBeamProjectileEntity beam = new GunBeamProjectileEntity(
            this.level(), this.getCasterUUID(), colors[0], colors[1], colors[2], 0.6F, colors[3], colors[4], colors[5], 1.0F, 0.15F, 0.0F, this.getTarget()
         );
         beam.setPos(spawn);
         if (this.targetPos != null) {
            beam.aimAt(this.targetPos);
         } else {
            beam.setRotationPitch(this.getRotationPitch() + 90.0F);
            beam.setRotationYaw(-this.getRotationYaw());
         }

         this.level().addFreshEntity(beam);
         this.mybeam = beam;
         beam.autonomous = true;
         beam.owner = this;
      }
   }

   public void moveAimBeam(GunBeamProjectileEntity beam) {
      Vec3 spawn = this.position();
      beam.setPos(spawn);
      if (this.targetPos != null) {
         beam.aimAt(this.targetPos);
      } else {
         beam.setRotationPitch(this.getRotationPitch() + 90.0F);
         beam.setRotationYaw(-this.getRotationYaw());
      }
   }

   public void fireSound(int n) {
      SoundEvent sound = (SoundEvent)ModSounds.TAIKOBIG.get();
      if (n == 0) {
         sound = (SoundEvent)ModSounds.TAIKO1.get();
      }

      if (n == 1) {
         sound = (SoundEvent)ModSounds.TAIKO2.get();
      }

      if (n == 2) {
         sound = (SoundEvent)ModSounds.TAIKO3.get();
      }

      this.level().playSound(null, Utils.toBlockPos(this.position()), sound, SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME, 1.0F);
   }

   public void fireBeam() {
      if (Nobu.doShotMana(this.getCaster()) && this.mybeam != null && this.getRecoilingTicks() <= 0 && this.getRecoveryTicks() <= 0) {
         this.setRecoilingTicks(MTConfig.NOBU_RECOIL_TIME);
         this.fireSound(this.sound);
         if (!this.mybeam.getFired()) {
            MandatoryFun.completeFunReq(this.getCaster(), MandatoryFun.FUN_REQS.NOBU_FIRE_SQUAD);
            Vec3 spawn = this.mybeam.position();
            this.mybeam.setFired(true);
            Entity target = this.level().getEntity(this.getTarget());
            if (target != null) {
               Vec3 targetPos = target.getEyePosition();
               Vec3 aim = targetPos.subtract(spawn).normalize();
               this.mybeam.setDeltaMovement(aim.x * 5.0, aim.y * 5.0, aim.z * 5.0);
               this.mybeam.aimAt(targetPos);
            } else {
               Vec3 aim = Utils.vecFromPitchYaw(-this.getRotationPitch() + 180.0F, -this.getRotationYaw()).normalize();
               this.mybeam.setDeltaMovement(aim.x * 5.0, aim.y * 5.0, aim.z * 5.0);
               this.mybeam.setRotationPitch(EffectUtil.toDegrees((float)(-Math.atan2(Math.sqrt(aim.z * aim.z + aim.x * aim.x), aim.y))));
               this.mybeam.setRotationYaw(-EffectUtil.toDegrees((float)Math.atan2(aim.z, aim.x)) + 270.0F);
            }

            this.mybeam = null;
         }
      }
   }

   public void moveAlongAxisNoTarget(double scale) {
      this.setDeltaMovement(Utils.vecFromPitchYaw(-this.getRotationPitch() + 180.0F, -this.getRotationYaw()).normalize().scale(scale));
   }

   public void moveAlongAxis(double scale) {
      this.setDeltaMovement(this.position().subtract(this.targetPos).normalize().scale(-scale));
   }


   public void aimAt(Vec3 pos) {
      Vec3 aim = pos.subtract(this.position());
      this.setRotationPitch(EffectUtil.toDegrees((float)Math.atan2(Math.sqrt(aim.z * aim.z + aim.x * aim.x), aim.y)) + 270.0F);
      this.setRotationYaw(-EffectUtil.toDegrees((float)Math.atan2(aim.z, aim.x)) + 270.0F);
      this.setRotationRoll(90.0F);
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

   public int getShootingTicks() {
      return (Integer)this.entityData.get(SHOOTING_TICKS);
   }

   public void setShootingTicks(int t) {
      this.entityData.set(SHOOTING_TICKS, t);
   }

   public int getTarget() {
      return (Integer)this.entityData.get(TARGET);
   }

   public void setTarget(int d) {
      this.entityData.set(TARGET, d);
   }

   public int getPitchCooldown() {
      return (Integer)this.entityData.get(PITCH_COOLDOWN);
   }

   public void setPitchCooldown(int d) {
      this.entityData.set(PITCH_COOLDOWN, d);
   }

   public int getRecoilingTicks() {
      return (Integer)this.entityData.get(RECOILING_TICKS);
   }

   public void setRecoilingTicks(int ticks) {
      this.entityData.set(RECOILING_TICKS, ticks);
   }

   public int getRecoveryTicks() {
      return (Integer)this.entityData.get(RECOVERY_TICKS);
   }

   public void setRecoveryTicks(int ticks) {
      this.entityData.set(RECOVERY_TICKS, ticks);
   }

   public float getDistance() {
      return (Float)this.entityData.get(DISTANCE);
   }

   public void setDistance(float d) {
      this.entityData.set(DISTANCE, d);
   }
}
