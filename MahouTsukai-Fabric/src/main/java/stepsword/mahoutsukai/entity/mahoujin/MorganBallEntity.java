package stepsword.mahoutsukai.entity.mahoujin;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.ModEntityDataSerializers;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class MorganBallEntity extends MahoujinEntity {
   private static final String TAG_SPHERE_SIZE = "MAHOUTSUKAI_SPHERE_SIZE";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final String TAG_SPIKE_LENGTH = "MAHOUTSUKAI_SPIKE_LENGTH";
   private static final String TAG_TARGETS = "MAHOUTSUKAI_TARGETS";
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Float> SPHERE_SIZE = SynchedEntityData.defineId(MorganBallEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(MorganBallEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> SPIKE_LENGTH = SynchedEntityData.defineId(MorganBallEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<List<Integer>> TARGETS = SynchedEntityData.defineId(MorganBallEntity.class, ModEntityDataSerializers.INT_LIST);
   public LivingEntity caster;
   public float damage;
   public int dyingTicks = 0;
   public float prev_spike_len;
   public float prev_sphere_size;
   ArrayList<LivingEntity> targets;
   public ArrayList<Float> yaws;
   public ArrayList<Float> pitchs;
   public ArrayList<Float> ds;
   public boolean nocaster = false;
   public Vec3 origPos;
   public int m = 0;
   public int earlydeath = 0;
   public float rate = 0.4F;
   public static final String entityName = "morgan_ball_entity";
   public float maxsize = 1.3F;

   public MorganBallEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public MorganBallEntity(Level world) {
      super((EntityType<?>)ModEntities.MORGAN_BALL.get(), world);
   }

   public MorganBallEntity(Level world, LivingEntity c, float r, float g, float b, float a, float r2, float g2, float b2, float size, float damage) {
      this(world);
      this.setColor(r, g, b, a, r2, g2, b2, 1.0F);
      this.sizer(size);
      if (c == null) {
         this.nocaster = true;
      } else {
         this.caster = c;
      }

      this.damage = damage;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPHERE_SIZE, 0.0F);
      builder.define(DISTANCE, 0.0F);
      builder.define(SPIKE_LENGTH, 0.0F);
      builder.define(TARGETS, new ArrayList());
   }

   @Override
   public void tick() {
      super.tick();
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      Vec3 newpos = this.position().add(this.getDeltaMovement());
      BlockPos pos = Utils.toBlockPos(newpos.add(this.getDeltaMovement().scale(this.getSphereSize())));
      if (this.level().getBlockState(pos).isAir()) {
         this.teleportTo(newpos.x, newpos.y, newpos.z);
      }

      if (!this.level().isClientSide()) {
         if (!this.nocaster && (this.caster == null || !(this.caster.getMainHandItem().getItem() instanceof Morgan))) {
            this.discard();
            return;
         }

         if (this.nocaster && this.dyingTicks < 10 || !this.nocaster && this.dyingTicks <= 0) {
            if (this.earlydeath > 0) {
               this.setSphereSize(this.getSphereSize() - 0.04F);
               if (this.getSphereSize() <= 0.0F) {
                  this.discard();
               }
            } else {
               this.setSphereSize(Math.min(this.maxsize, this.getSphereSize() + 0.02F));
            }

            if (this.nocaster && this.origPos != null) {
               if (this.position().distanceToSqr(this.origPos) > (MTConfig.MORGAN_MAX_BALL_RANGE - 1) * (MTConfig.MORGAN_MAX_BALL_RANGE - 1)) {
                  this.setDeltaMovement(this.getDeltaMovement().scale(0.3F));
               } else {
                  this.setDeltaMovement(this.getDeltaMovement().scale(1.02F));
               }
            } else if (this.position().distanceToSqr(this.caster.position()) > MTConfig.MORGAN_MAX_BALL_RANGE * MTConfig.MORGAN_MAX_BALL_RANGE) {
               this.setDeltaMovement(this.getDeltaMovement().scale(0.3F));
            } else {
               this.setDeltaMovement(this.getDeltaMovement().scale(1.02F));
            }

            if (this.nocaster) {
               if (this.getSphereSize() > 1.0F && this.earlydeath <= 0) {
                  this.dyingTicks++;
               }
            } else if (this.caster.getMainHandItem().getItem() instanceof Morgan && !(this.caster.getUseItem().getItem() instanceof Morgan)) {
               if (this.getSphereSize() > 1.0F && this.earlydeath <= 0) {
                  this.dyingTicks++;
               } else {
                  this.earlydeath++;
               }
            }
         } else {
            if (this.nocaster) {
               this.setDeltaMovement(this.getDeltaMovement().scale(0.3F));
            }

            if (this.targets == null) {
               int radius = MTConfig.MORGAN_SPIKE_RANGE;
               AABB aabb = new AABB(
                  this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius
               );
               List<LivingEntity> ts = this.level().getEntitiesOfClass(LivingEntity.class, aabb, a -> this.nocaster || !a.equals(this.caster));
               this.targets = new ArrayList<>();
               this.targets.addAll(ts);
               this.setDataTargets(this.setTargets(this.targets));
            } else if (this.m <= 6) {
               if (this.getSpikeLength() >= 1.0F) {
                  this.m++;
               } else {
                  this.setSpikeLength(this.getSpikeLength() + this.rate);
               }
            } else {
               this.setSpikeLength(Math.max(0.0F, this.getSpikeLength() - this.rate));
               if (this.getSpikeLength() == 0.0F) {
                  this.earlydeath++;
               }

               if (this.earlydeath > 0) {
                  this.setSphereSize(this.getSphereSize() - 0.04F);
                  if (this.getSphereSize() <= 0.0F) {
                     this.discard();
                  }
               }

               if (this.m == 7) {
                  ArrayList<LivingEntity> spikeTargets = this.getTargets(this.getDataTargets());

                  for (LivingEntity s : spikeTargets) {
                     if (this.nocaster || !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.caster.getUUID(), s)) {
                        if (this.nocaster) {
                           EffectUtil.magicAttack(s, this.damage / spikeTargets.size(), null);
                        } else {
                           EffectUtil.magicAttack(s, this.damage / spikeTargets.size(), this.caster);
                        }

                        Vec3 d = s.getEyePosition(1.0F).subtract(this.position());
                        boop(s, (float)d.length() / 4.0F, d.x, d.y, d.z);
                     }
                  }
               }

               this.m++;
            }

            this.dyingTicks++;
         }
      } else {
         ArrayList<LivingEntity> spikeTargets = this.getTargets(this.getDataTargets());
         this.yaws = new ArrayList<>();
         this.pitchs = new ArrayList<>();
         this.ds = new ArrayList<>();

         for (LivingEntity e : spikeTargets) {
            this.addYawPitchDist(e);
         }
      }

      this.prev_spike_len = this.getSpikeLength();
      this.prev_sphere_size = this.getSphereSize();
   }

   public void addYawPitchDist(LivingEntity e) {
      if (e != null) {
         Vec3 v = e.getEyePosition(1.0F).subtract(this.position());
         float d = (float)v.length() + 1.5F;
         v = v.normalize();
         float pitch = (float)Math.asin(-v.y);
         float yaw = (float)Math.atan2(v.x, v.z);
         this.yaws.add(EffectUtil.toDegrees(yaw));
         this.pitchs.add(EffectUtil.toDegrees(pitch) + 90.0F);
         this.ds.add(d);
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void sizer(float s) {
      this.setSphereSize(s);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setSphereSize(input.getFloatOr("MAHOUTSUKAI_SPHERE_SIZE", 0.0F));
      this.setSpikeLength(input.getFloatOr("MAHOUTSUKAI_SPIKE_LENGTH", 0.0F));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
      this.setDataTargets(input.read("MAHOUTSUKAI_TARGETS", Codec.list(Codec.INT)).orElse(new ArrayList<>()));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_SPHERE_SIZE", this.getSphereSize());
      output.putFloat("MAHOUTSUKAI_SPIKE_LENGTH", this.getSpikeLength());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
      output.store("MAHOUTSUKAI_TARGETS", Codec.list(Codec.INT), this.getDataTargets());
   }

   public float getSphereSize() {
      return (Float)this.entityData.get(SPHERE_SIZE);
   }

   public void setSphereSize(float s) {
      this.entityData.set(SPHERE_SIZE, s);
   }

   public float getSpikeLength() {
      return (Float)this.entityData.get(SPIKE_LENGTH);
   }

   public void setSpikeLength(float s) {
      this.entityData.set(SPIKE_LENGTH, s);
   }

   public ArrayList<LivingEntity> getTargets(List<Integer> ids) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      if (ids != null) {
         for (int id : ids) {
            if (this.level().getEntity(id) instanceof LivingEntity e) {
               lst.add(e);
            }
         }
      }

      return lst;
   }

   public ArrayList<Integer> setTargets(ArrayList<LivingEntity> targets) {
      return targets != null ? new ArrayList<>(targets.stream().map(Entity::getId).toList()) : new ArrayList<>();
   }

   public List<Integer> getDataTargets() {
      return (List<Integer>)this.entityData.get(TARGETS);
   }

   public void setDataTargets(List<Integer> ids) {
      this.entityData.set(TARGETS, ids);
   }

   public float getDistance() {
      return (Float)this.entityData.get(DISTANCE);
   }

   public void setDistance(float d) {
      this.entityData.set(DISTANCE, d);
   }

   public static void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      double motionX = 0.0;
      double motionY = 0.0;
      double motionZ = 0.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      entity.hurtMarked = true;
      if (entity.onGround()) {
         motionY /= 2.0;
         motionY += 0.3;
         if (motionY > 0.6) {
            motionY = 0.6;
         }
      }

      entity.setDeltaMovement(new Vec3(motionX, motionY, motionZ));
   }
}
