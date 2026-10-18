package stepsword.mahoutsukai.entity.mahoujin;

import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;

import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class GateChainEntity extends MahoujinEntity {
   private static final String TAG_LENGTH = "MAHOUTSUKAI_LENGTH";
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static float speed = 4.2F;
   public boolean dying = false;
   public int dyingticks = 0;
   public float lastlen = 0.0F;
   private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(GateChainEntity.class, EntityDataSerializers.FLOAT);
   public static final String entityName = "gate_chain_mahoujin_entity";
   public Vec3 target = null;

   public GateChainEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public GateChainEntity(Level world) {
      super((EntityType<?>)ModEntities.GATE_CHAIN.get(), world);
   }

   public GateChainEntity(Level world, float r, float g, float b, float a, Vec3 pos, Vec3 target) {
      this(world);
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
      this.target = target;
      this.setPos(pos.x, pos.y, pos.z);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LENGTH, 0.0F);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setLength(input.getFloatOr("MAHOUTSUKAI_LENGTH", 0.0F));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_LENGTH", this.getLength());
   }

   public float getLength() {
      return (Float)this.entityData.get(LENGTH);
   }

   public void setLength(float l) {
      this.entityData.set(LENGTH, l);
   }

   @Override
   public void tick() {
      if (!this.level().isClientSide()) {
         if (!this.dying) {
            if (this.level().getGameTime() % Math.max(1,MTConfig.TREASURY_PROJECTION_CHAINS_COOLDOWN) == 0L) {
               this.setLife(this.getLife() + 1);
               if (this.getLife() > 20) {
                  this.startDying();
               }
            }

            if (this.target != null) {
               Vec3 tv = this.target;
               if (this.getLength() < tv.distanceTo(this.position())) {
                  this.setLength(this.getLength() + speed);
               } else if (Utils.isBlockAir(this.level(), Utils.toBlockPos(this.target))) {
                  this.startDying();
               }

               this.collideWithNearbyEntities();
            }
         } else {
            this.die();
         }
      }

      this.lastlen = this.getLength();
   }

   protected void collideWithNearbyEntities() {
      float len = this.getLength();
      AABB aabb = new AABB(this.getX() - len, this.getY() - len, this.getZ() - len, this.getX() + len, this.getY() + len, this.getZ() + len);
      List<Entity> list = this.level().getEntities(this, aabb, EntitySelector.pushableBy(this));
      if (!list.isEmpty()) {
         for (int l = 0; l < list.size(); l++) {
            Entity entity = list.get(l);
            if (entity instanceof LivingEntity && this.boxIntersectsLine(entity.getBoundingBox())) {
               this.push(entity);
            }
         }
      }
   }

   public boolean boxIntersectsLine(AABB aabb) {
      return Utils.CheckLineBox(
         new Vec3(aabb.minX, aabb.minY, aabb.minZ),
         new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ),
         this.position(),
         this.position().add(this.target.subtract(this.position()).normalize().scale(this.getLength()))
      );
   }

   public void push(Entity entityIn) {
      if (entityIn instanceof LivingEntity) {
         EffectUtil.magicAttack((LivingEntity)entityIn, (float)MTConfig.TREASURY_PROJECTION_CHAINS_DAMAGE, null);
         Vec3 v = this.position().subtract(entityIn.position());
         this.boop(entityIn, 0.3F, v.x, 0.0, v.z);
      }
   }



   public void startDying() {
      this.dying = true;
   }

   public void die() {
      this.dyingticks++;
      if (this.getLength() <= 0.5F) {
         this.discard();
      } else {
         this.setLength(this.getLength() - speed / 2.0F);
      }
   }

   private void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      if(f<1.0E-6)return;
      Vec3 motion = this.getDeltaMovement();
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

   public void shoot(double x, double y, double z) {
      Vec3 Vec3 = new Vec3(x, y, z).normalize();
      float f = Mth.sqrt((float)Vec3.horizontalDistanceSqr());
      this.yRot = (float)(Mth.atan2(Vec3.x, Vec3.z) * 180.0F / (float)Math.PI);
      this.xRot = (float)(Mth.atan2(Vec3.y, f) * 180.0F / (float)Math.PI);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public boolean isAttackable() {
      return super.isAttackable();
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      if (damage > 4.0F) {
         this.startDying();
         return true;
      } else {
         return false;
      }
   }
}
