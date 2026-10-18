package stepsword.mahoutsukai.entity.mahoujin;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.Bakuretsu;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;
import stepsword.mahoutsukai.networking.BakuretsuPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class BeamProjectileEntity extends Projectile implements IMahoujinEntity {
   private static final EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> MAHOUJIN_DATA=SynchedEntityData.defineId(BeamProjectileEntity.class,stepsword.mahoutsukai.entity.ModEntityDataSerializers.MAHOUJIN);
   @Override public EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> mahoujinDataKey(){return MAHOUJIN_DATA;}
   public static final String entityName = "beam_projectile_entity";
   private static final String TAG_RADIUS = "MAHOUTSUKAI_RADIUS";
   private static final String TAG_LENGTH = "MAHOUTSUKAI_LENGTH";
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(BeamProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(BeamProjectileEntity.class, EntityDataSerializers.FLOAT);
   public float damage = 0.0F;
   public int ticksSinceFired = 0;
   public long timeCreated = -1L;

   public BeamProjectileEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public BeamProjectileEntity(Level world) {
      super((EntityType)ModEntities.BEAM_PROJECTILE.get(), world);
   }

   public BeamProjectileEntity(Level world, UUID caster, float r, float g, float b, float a, float r2, float g2, float b2, float a2, float radius, float length) {
      this(world);
      this.setCasterUUID(caster);
      this.setColor(r, g, b, a, r2, g2, b2, a2);
      this.setRadius(radius);
      this.setLength(length);
   }

   protected void defineSynchedData(Builder builder) {
      IMahoujinEntity.super.defineSync(builder);
      builder.define(RADIUS, 1.0F);
      builder.define(LENGTH, 1.0F);
   }

   protected void readAdditionalSaveData(ValueInput input) {
      IMahoujinEntity.super.read(input);
      this.setLength(input.getFloatOr("MAHOUTSUKAI_LENGTH", 1.0F));
      this.setRadius(input.getFloatOr("MAHOUTSUKAI_RADIUS", 1.0F));
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      IMahoujinEntity.super.add(output);
      output.putFloat("MAHOUTSUKAI_LENGTH", this.getLength());
      output.putFloat("MAHOUTSUKAI_RADIUS", this.getRadius());
   }

   public void tick() {
      super.tick();
      if (this.timeCreated == -1L) {
         this.timeCreated = this.level().getGameTime();
      }

      this.setLife(this.getLife() + 1);
      if (!this.level().isClientSide()) {
         if (this.ticksSinceFired > 400 || this.level().getGameTime() - this.timeCreated > 800L) {
            this.discard();
         }

         this.setLength((float)Math.min(3.0, this.getLength() + this.getDeltaMovement().length() / 5.0));
         this.checkHit();
      }

      this.ticksSinceFired++;
      Vec3 moveDelta = this.getDeltaMovement();
      Vec3 moveTarget = this.position().add(moveDelta);
      this.setPos(moveTarget.x, moveTarget.y, moveTarget.z);
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
         }
      }

      if (raytraceresult != null && raytraceresult.getType() != Type.MISS) {
         this.onHit(raytraceresult);
      }
   }

   public void onHit(HitResult result) {
      Vec3 target = result.getLocation();
      double x = target.x;
      double y = target.y;
      double z = target.z;
      Player caster = this.level().getPlayerByUUID(this.getCasterUUID());
      if (caster != null) {
         float[] colors = this.getColors();
         int radius = MTConfig.MYSTIC_STAFF_AOE_SIZE;
         Bakuretsu explosion = new Bakuretsu(radius, (float)x, (float)y, (float)z, MysticStaff.getExplosionDamage(true, Utils.getPlayerMahou(caster)));
         explosion.explosionA(this.level(), caster);

         for (Player entityplayer : this.level().players()) {
            if (entityplayer.distanceToSqr(x, y, z) < 16384.0) {
               Vec3 vec = explosion.knockback.get(caster);
               if (vec == null) {
                  PacketHandler.sendTo((ServerPlayer)caster, new BakuretsuPacket(x, y, z, radius, 0.0F, 0.0F, 0.0F, colors));
               } else {
                  PacketHandler.sendTo((ServerPlayer)caster, new BakuretsuPacket(x, y, z, radius, (float)vec.x, (float)vec.y, (float)vec.z, colors));
               }
            }

            caster.level()
               .playSound(
                  null, Utils.toBlockPos(target), (SoundEvent)ModSounds.EXPLOSION.get(), SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME * 12.0F, 1.0F
               );
         }
      }

      this.discard();
   }

   public Vec3 setLookPos(boolean client, Entity caster, boolean add) {
      Vec3 look = caster.getLookAngle();
      Vec3 oldlook = look.normalize();
      look = oldlook.scale(1.1F);
      Vec3 start = look.add(caster.position());
      this.yRot = caster.getYRot();
      this.xRot = caster.getXRot();
      if (add) {
         this.xRot = 90.0F - this.xRot;
      }

      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
      if (!client && add) {
         this.setPos(start.x, start.y, start.z);
      }

      return start;
   }

   @Nullable
   public Entity getShooter() {
      return this.getCasterUUID() != null && this.level() instanceof ServerLevel ? ((ServerLevel)this.level()).getEntity(this.getCasterUUID()) : null;
   }

   @Nullable
   protected EntityHitResult rayTraceEntities(Vec3 startVec, Vec3 endVec) {
      return ProjectileUtil.getEntityHitResult(
         this.level(),
         this,
         startVec,
         endVec,
         this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0),
         p_213871_1_ -> !p_213871_1_.isSpectator()
            && p_213871_1_.isAlive()
            && p_213871_1_.isPickable()
            && !(p_213871_1_ instanceof BeamProjectileEntity)
            && (!p_213871_1_.getUUID().equals(this.getCasterUUID()) || this.ticksSinceFired >= 5)
      );
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
}
