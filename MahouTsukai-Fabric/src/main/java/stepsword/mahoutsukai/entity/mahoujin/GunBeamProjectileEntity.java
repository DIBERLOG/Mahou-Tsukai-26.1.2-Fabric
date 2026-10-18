package stepsword.mahoutsukai.entity.mahoujin;

import com.google.common.primitives.Floats;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jspecify.annotations.Nullable;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.SparkExplosionPacket;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class GunBeamProjectileEntity extends MahoujinEntity {
   public static java.util.function.Consumer<GunBeamProjectileEntity> clientLightning=e->{};
   public static java.util.function.Consumer<GunBeamProjectileEntity> clientSmoke=e->{};
   public static final String entityName = "beam_projectile_entity";
   private static final String TAG_FIRED = "MAHOUTSUKAI_FIRED";
   private static final String TAG_TARGET = "MAHOUTSUKAI_TARGET";
   private static final String TAG_RADIUS = "MAHOUTSUKAI_RADIUS";
   private static final String TAG_LENGTH = "MAHOUTSUKAI_LENGTH";
   private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(GunBeamProjectileEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> FIRED = SynchedEntityData.defineId(GunBeamProjectileEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(GunBeamProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(GunBeamProjectileEntity.class, EntityDataSerializers.FLOAT);
   public int ticksSinceFired = 0;
   public long timeCreated = -1L;
   public int hitCD = 5;
   boolean hit = false;
   public boolean autonomous = false;
   public GunEntity owner = null;

   public GunBeamProjectileEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public GunBeamProjectileEntity(Level world) {
      super((EntityType<?>)ModEntities.GUN_BEAM_PROJECTILE.get(), world);
   }

   public GunBeamProjectileEntity(
      Level world, UUID caster, float r, float g, float b, float a, float r2, float g2, float b2, float a2, float radius, float length, int targetID
   ) {
      this(world);
      this.setCasterUUID(caster);
      this.setColor(r, g, b, a, r2, g2, b2, a2);
      this.setRadius(radius);
      this.setLength(length);
      this.setTarget(targetID);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FIRED, false);
      builder.define(RADIUS, 1.0F);
      builder.define(LENGTH, 1.0F);
      builder.define(TARGET, 0);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setFired(input.getBooleanOr("MAHOUTSUKAI_FIRED", false));
      this.setLength(input.getFloatOr("MAHOUTSUKAI_LENGTH", 1.0F));
      this.setRadius(input.getFloatOr("MAHOUTSUKAI_RADIUS", 1.0F));
      this.setTarget(input.getIntOr("MAHOUTSUKAI_TARGET", 0));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("MAHOUTSUKAI_FIRED", this.getFired());
      output.putFloat("MAHOUTSUKAI_LENGTH", this.getLength());
      output.putFloat("MAHOUTSUKAI_RADIUS", this.getRadius());
      output.putInt("MAHOUTSUKAI_TARGET", this.getTarget());
   }

   public void setDeltaMovement(Vec3 pDeltaMovement) {
      super.setDeltaMovement(pDeltaMovement);
   }

   @Override
   public void tick() {
      super.tick();
      boolean client = this.level().isClientSide();
      long time = this.level().getGameTime();
      boolean fired = this.getFired();
      Entity target = this.getTarget() > 0 ? this.level().getEntity(this.getTarget()) : null;
      boolean hasTarget = target != null && target.isAlive();
      if (this.timeCreated == -1L) {
         this.timeCreated = time;
      }

      if (fired) {
         this.ticksSinceFired++;
      }

      if (client) {
         this.setRotationSpeed((float)this.getDeltaMovement().length());
         clientLightning.accept(this);
         if (this.ticksSinceFired == 1 || this.ticksSinceFired == 2) {
            clientSmoke.accept(this);
         }
      } else {
         this.hitCD--;
         if (this.ticksSinceFired > 400 || time - this.timeCreated > 800L) {
            this.discard();
         }
      }

      if (!client) {
         this.seppuku(fired, hasTarget);
         if (hasTarget && fired && !this.hit) {
            this.aimAt(target.getEyePosition());
            Vec3 adjustVector = this.position().subtract(target.getEyePosition()).normalize().scale(-5.0);
            Vec3 currentDelta = this.getDeltaMovement();
            if (adjustVector.x * currentDelta.x >= 0.0 && adjustVector.y * currentDelta.y >= 0.0 && adjustVector.z * currentDelta.z >= 0.0) {
               this.setDeltaMovement(this.position().subtract(target.getEyePosition()).normalize().scale(-5.0));
            }
         }

         this.setLength((float)Math.min(1.0, this.getLength() + this.getDeltaMovement().length() / 5.0));
         if(fired)this.checkHit();
      }

      Vec3 moveDelta = this.getDeltaMovement();
      Vec3 moveTarget = this.position().add(moveDelta);
      this.setPos(moveTarget.x, moveTarget.y, moveTarget.z);
   }

   public void seppuku(boolean fired, boolean hasTarget) {
      Player caster = this.getCaster();
      if (caster == null) {
         this.discard();
      } else {
         if (!this.autonomous) {
            if ((!fired || !hasTarget) && (!(caster.getUseItem().getItem() instanceof Nobu) || Utils.getMode(caster.getUseItem()) != 0)) {
               this.discard();
            }
         } else if (Nobu.player_storage.containsKey(this.getCasterUUID())) {
            Nobu.NobuUserStorage storage = Nobu.player_storage.get(this.getCasterUUID());
            if (this.owner == null || !storage.guns.containsValue(this.owner) || !this.owner.isAlive()) {
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
      this.setRotationYaw((float)(Mth.atan2(Vec3.x, Vec3.z) * 180.0F / (float)Math.PI));
      this.setRotationPitch((float)(Mth.atan2(Vec3.y, f) * 180.0F / (float)Math.PI));
   }

   public void checkHit() {
      Vec3 vec3d = this.getDeltaMovement();
      Vec3 vec3d2 = this.position();
      Vec3 vec3d3 = vec3d2.add(vec3d);
      HitResult raytraceresult = this.level().clip(new ClipContext(vec3d2, vec3d3, Block.COLLIDER, Fluid.NONE, this));
      if (raytraceresult.getType() != Type.MISS) {
         vec3d3 = raytraceresult.getLocation();
      }

      EntityHitResult entityraytraceresult = this.rayTraceEntities(vec3d2, vec3d3);
      if (entityraytraceresult != null) {
         raytraceresult = entityraytraceresult;
      }

      if (raytraceresult != null && raytraceresult.getType() == Type.ENTITY && raytraceresult instanceof EntityHitResult) {
         Entity entity = ((EntityHitResult)raytraceresult).getEntity();
         Entity entity1 = this.getShooter();
         if (entity instanceof Player
            && entity1 instanceof Player
            && !((Player)entity1).canHarmPlayer((Player)entity)
            && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), entity)) {
            raytraceresult = null;
            entityraytraceresult = null;
         }
      }

      if (this.hitCD < 0 && raytraceresult != null && raytraceresult.getType() != Type.MISS) {
         this.onHit(raytraceresult);
         this.hitCD = 1;
      }

      raytraceresult = null;
   }

   public void aimAt(Vec3 pos) {
      Vec3 aim = pos.subtract(this.position());
      this.setRotationPitch(EffectUtil.toDegrees((float)(-Math.atan2(Math.sqrt(aim.z * aim.z + aim.x * aim.x), aim.y))));
      this.setRotationYaw(-EffectUtil.toDegrees((float)Math.atan2(aim.z, aim.x)) + 270.0F);
   }

   public void onHit(HitResult result) {
      Vec3 target = result.getLocation();
      Player caster = this.getCaster();
      float damage = (float)MTConfig.NOBU_BULLET_DAMAGE;
      if (caster != null
         && caster.getMainHandItem().getItem() instanceof Nobu
         && EffectUtil.hasEnchantment(caster.getMainHandItem(), Enchantments.POWER, this.level())) {
         damage += (float)(
            MTConfig.NOBU_POWER_DAMAGE_FACTOR * EffectUtil.getEnchantmentLevel(caster.getMainHandItem(), EffectUtil.toHolder(Enchantments.POWER, this.level()))
         );
      }

      if (result instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity b) {
         if (this.getCasterUUID() == null || !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), b)) {
            EffectUtil.magicAttack(b, damage, caster);
         }

         PacketHandler.sendTracking(
            this, new SparkExplosionPacket(ehr.getEntity().position().add(0.0, ehr.getEntity().getEyeHeight() / 2.0F, 0.0), Floats.asList(this.getColors()))
         );
         this.setTarget(-1);
         this.hit = true;
      }

      if (result instanceof BlockHitResult) {
         PacketHandler.sendTracking(
            this,
            new SparkExplosionPacket(
               ((BlockHitResult)result).getBlockPos().relative(((BlockHitResult)result).getDirection()).getCenter(), Floats.asList(this.getColors())
            )
         );
         if (MTConfig.NOBU_AOE_HIT > 0.0) {
            for (Entity e : this.level()
               .getEntities(
                  this,
                  new AABB(
                     this.position().subtract(MTConfig.NOBU_AOE_HIT, MTConfig.NOBU_AOE_HIT, MTConfig.NOBU_AOE_HIT),
                     this.position().add(MTConfig.NOBU_AOE_HIT, MTConfig.NOBU_AOE_HIT, MTConfig.NOBU_AOE_HIT)
                  ),
                  a -> a instanceof LivingEntity && !a.getUUID().equals(this.getCasterUUID())
               )) {
               if (this.getCasterUUID() == null || !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), e)) {
                  EffectUtil.magicAttack((LivingEntity)e, (float)(MTConfig.NOBU_AOE_FACTOR * damage), caster);
               }
            }
         }

         this.discard();
      }
   }

   public void specialDiscard() {
      PacketHandler.sendTracking(this, new SparkExplosionPacket(this.position(), Floats.asList(this.getColors())));
      this.discard();
   }

   public boolean canCollideWith(Entity pEntity) {
      return false;
   }

   public boolean canBeCollidedWith(@Nullable Entity other) {
      return true;
   }

   public boolean isColliding(BlockPos pPos, BlockState pState) {
      return true;
   }

   @org.jetbrains.annotations.Nullable
   public Entity getShooter() {
      return this.getCasterUUID() != null && this.level() instanceof ServerLevel ? ((ServerLevel)this.level()).getEntity(this.getCasterUUID()) : null;
   }

   @org.jetbrains.annotations.Nullable
   protected EntityHitResult rayTraceEntities(Vec3 startVec, Vec3 endVec) {
      return ProjectileUtil.getEntityHitResult(
         this.level(),
         this,
         startVec,
         endVec,
         this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.3),
         p_213871_1_ -> !p_213871_1_.isSpectator()
            && p_213871_1_.isAlive()
            && p_213871_1_.isPickable()
            && !(p_213871_1_ instanceof GunBeamProjectileEntity)
            && (!p_213871_1_.getUUID().equals(this.getCasterUUID()) || this.ticksSinceFired >= 5),
         1.0F
      );
   }

   @Override
   public boolean getFired() {
      return (Boolean)this.entityData.get(FIRED);
   }

   @Override
   public void setFired(boolean f) {
      if(f && !getFired())this.hitCD=0;
      this.entityData.set(FIRED, f);
   }

   public float getLength() {
      return (Float)this.entityData.get(LENGTH);
   }

   public void setLength(float f) {
      this.entityData.set(LENGTH, f);
   }

   public float getRadius() {
      return (Float)this.entityData.get(RADIUS);
   }

   public void setRadius(float f) {
      this.entityData.set(RADIUS, f);
   }

   public int getTarget() {
      return (Integer)this.entityData.get(TARGET);
   }

   public void setTarget(int p) {
      this.entityData.set(TARGET, p);
   }
}
