package stepsword.mahoutsukai.entity.mahoujin;

import java.util.ArrayList;
import java.util.HashSet;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.BeamPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class MysticStaffBeamMahoujinEntity extends MahoujinEntity {
   private static final String TAG_BEAM_SIZE = "MAHOUTSUKAI_BEAM_SIZE";
   private static final String TAG_BEAM_LENGTH = "MAHOUTSUKAI_BEAM_LENGTH";
   private static final String TAG_SPHERE_SIZE = "MAHOUTSUKAI_SPHERE_SIZE";
   private static final String TAG_DYING = "MAHOUTSUKAI_DYING";
   public float prev_beam_size;
   public float prev_beam_len;
   public float prev_sphere_size;
   public float prev_mini_roll;
   public static int dyingticks = 6;
   public double prevx;
   public double prevy;
   public double prevz;
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(
      Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY,
      Double.POSITIVE_INFINITY,
      Double.POSITIVE_INFINITY,
      Double.POSITIVE_INFINITY
   );
   private static final EntityDataAccessor<Float> BEAM_SIZE = SynchedEntityData.defineId(MysticStaffBeamMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> BEAM_LENGTH = SynchedEntityData.defineId(MysticStaffBeamMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> SPHERE_SIZE = SynchedEntityData.defineId(MysticStaffBeamMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DYING = SynchedEntityData.defineId(MysticStaffBeamMahoujinEntity.class, EntityDataSerializers.INT);
   public static final String entityName = "mahoujin_beam_entity";
   Player caster = null;

   public MysticStaffBeamMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public MysticStaffBeamMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.BEAM_MAHOUJIN.get(), world);
   }

   public MysticStaffBeamMahoujinEntity(Level world, Player caster, float r, float g, float b) {
      this(world);
      this.caster = caster;
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BEAM_SIZE, 0.0F);
      builder.define(BEAM_LENGTH, 0.0F);
      builder.define(SPHERE_SIZE, 0.0F);
      builder.define(DYING, 0);
   }

   @Override
   public void tick() {
      super.tick();
      this.yRotO = this.yRot;
      int mini_speed_min_time = 15;
      int mini_speed_max_time = 35;
      int sphere_size_min_time = 25;
      int sphere_size_max_time = 35;
      int beam_size_min_time = 40;
      int beam_size_max_time = 45;
      int beam_len_min_time = 40;
      int beam_len_max_time = 45;
      float goal_circle_size = 1.5F;
      float goal_mini_speed = 6.0F;
      float goal_beam_size = 0.7F;
      float goal_beam_len = 64.0F;
      float goal_sphere_size = 0.7F;
      this.prev_beam_size = this.getBeamSize();
      this.prev_beam_len = this.getBeamLength();
      this.prev_sphere_size = this.getSphereSize();
      this.prev_mini_roll = this.getRotationRoll();
      if (this.getLife() == sphere_size_min_time - 3 && !this.level().isClientSide()) {
         for (ServerPlayer p : ((ServerLevel)this.level()).players()) {
            if (p.level() == this.level()) {
               PacketHandler.sendTo(p, new BeamPacket(this.getId()));
            }
         }
      }

      if (this.getCasterUUID() != null && this.caster == null) {
         this.caster = this.level().getPlayerByUUID(this.getCasterUUID());
      }

      if (this.caster != null && this.getDying() <= 0) {
         this.casterPosition(false);
         if (!(this.caster.getUseItem().getItem() instanceof MysticStaff)) {
            this.setDying(1);

         }

         if (this.getLife() == beam_len_min_time + 1) {
            this.boopCaster();
            if (this.caster instanceof ServerPlayer) {
               ModTriggers.BEAM.trigger((ServerPlayer)this.caster);
            }
         } else if (this.getLife() < beam_len_min_time + 1 || this.getLife() > beam_len_max_time + 4) {
            this.holdCaster();
         }
      } else if (this.getDying() <= 0) {
         this.setDying(1);
      }

      int life = this.getLife();
      this.setCircleSize(goal_circle_size);
      if (this.getDying() <= 0) {
         this.setRotationSpeed(
            life < mini_speed_min_time
               ? 0.0F
               : (
                  life > mini_speed_max_time
                     ? goal_mini_speed
                     : goal_mini_speed * ((float)life - mini_speed_min_time) / ((float)mini_speed_max_time - mini_speed_min_time)
               )
         );
         this.setBeamSize(
            life < beam_size_min_time
               ? 0.0F
               : (
                  life > beam_size_max_time
                     ? goal_beam_size
                     : goal_beam_size * ((float)life - beam_size_max_time) / ((float)beam_size_max_time - beam_size_min_time)
               )
         );
         this.setSphereSize(
            life < sphere_size_min_time
               ? 0.0F
               : (
                  life > sphere_size_max_time
                     ? goal_sphere_size
                     : goal_sphere_size * ((float)life - sphere_size_min_time) / ((float)sphere_size_max_time - sphere_size_min_time)
               )
         );
         this.setBeamLength(
            life < beam_len_min_time
               ? 0.0F
               : (life > beam_len_max_time ? goal_beam_len : goal_beam_len * ((float)life - beam_len_min_time) / ((float)beam_len_max_time - beam_len_min_time))
         );
         this.setRotationSpeed(Math.max(this.getRotationSpeed(), 0.0F));
         this.setBeamSize(Math.max(this.getBeamSize(), 0.0F));
         this.setSphereSize(Math.max(this.getSphereSize(), 0.0F));
         this.setBeamLength(Math.max(this.getBeamLength(), 0.0F));
      } else {
         float factor = (dyingticks - 1.0F) / dyingticks;
         this.setRotationSpeed(factor * this.getRotationSpeed());
         this.setBeamSize(factor * this.getBeamSize());
         this.setSphereSize(factor * this.getSphereSize());
         if (this.getDying() > dyingticks * 3) {
            this.discard();
         }

         this.setDying(this.getDying() + 1);
      }

      this.setRotationYaw(this.getRotationYaw() % 360.0F);
      if (this.getRotationYaw() < 0.0F) {
         this.setRotationYaw(this.getRotationYaw() + 360.0F);
      }

      if (this.getRotationYaw() > 360.0F) {
         this.setRotationYaw(this.getRotationYaw() - 360.0F);
      }

      this.setRotationRoll(this.getRotationRoll() + this.getRotationSpeed());
      if (!this.level().isClientSide() && this.getDying() <= 0 && this.getLife() > beam_len_min_time) {
         this.bakuretsu(beam_len_min_time);
      }
   }

   public void holdCaster() {
      this.caster.setDeltaMovement(0.0, 0.0, 0.0);
      this.caster.hurtMarked = true;
   }

   public void boopCaster() {
      double strength = 0.03;
      this.caster.needsSync = true;
      Vec3 v = this.caster.getLookAngle();
      float f = Mth.sqrt((float)(v.x * v.x + v.z * v.z + v.y * v.y));
      double motionX = 0.0;
      double motionY = 0.0;
      double motionZ = 0.0;
      motionX -= v.x / f * strength;
      motionZ -= v.z / f * strength;
      motionY -= v.y / f * strength;
      if (this.caster.onGround()) {
         motionY /= 2.0;
         motionY += 0.3;
         if (motionY > 0.1) {
            motionY = 0.1;
         }
      }

      this.caster.setDeltaMovement(motionX, motionY, motionZ);
      this.caster.hurtMarked = true;
   }

   public void bakuretsu(int beam_start) {
      double mana_factor = MTConfig.MYSTIC_STAFF_BEAM_SCALING_MANA;
      if (Utils.getPlayerMahou(this.caster) != null) {
         mana_factor *= Utils.getPlayerMahou(this.caster).getMaxMana();
      }

      int mana = (int)mana_factor + MTConfig.MYSTIC_STAFF_BEAM_MANA_PER_TICK;
      int seconds = (this.getLife() - beam_start) / 20;
      double damage = MTConfig.MYSTIC_STAFF_BEAM_DAMAGE_FACTOR;
      if (MTConfig.MYSTIC_STAFF_BEAM_DAMAGE_SCALES) {
         IMahou mahou = Utils.getPlayerMahou(this.caster);
         if (mahou != null) {
            damage *= mahou.getMaxMana();
         }
      }

      for (int i = 0; i < seconds; i++) {
         damage *= MTConfig.MYSTIC_STAFF_BEAM_DAMAGE_ACCELERATION;
      }

      if (PlayerManaManager.drainMana(this.caster, mana, false, false) == mana) {
         HashSet<BlockPos> blocks = new HashSet<>();
         int blockspertick = MTConfig.MYSTIC_STAFF_BEAM_MAX_BLOCK_BREAK_PER_TICK;
         float pt = this.getRotationPitch();
         float yw = this.getRotationYaw();
         Vec3 direction = Vec3.directionFromRotation(90.0F - pt, 360.0F - yw + 180.0F).normalize();
         Vec3 pos = this.position();
         boolean keepbreaking = true;
         ArrayList<Entity> lst = new ArrayList<>();

         for (float i = 0.0F; i < this.getBeamLength(); i++) {
            Vec3 diskcenter = pos.add(direction.scale(i));
            AABB aabb = new AABB(Utils.toBlockPos(diskcenter).offset(-2, -2, -2).getCenter(), Utils.toBlockPos(diskcenter).offset(2, 2, 2).getCenter());

            for (Entity e : this.level().getEntities((Entity)null, aabb, Entity::isAlive)) {
               if (e != this.caster
                  && !(e instanceof MysticStaffBeamMahoujinEntity)
                  && e instanceof LivingEntity
                  && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), e)) {
                  lst.add(e);
               }
            }

            if (blocks.size() >= blockspertick) {
               keepbreaking = false;
            }

            if (keepbreaking) {
               for (int j = -3; j <= 3; j++) {
                  for (int k = -3; k <= 3; k++) {
                     for (int m = -3; m <= 3; m++) {
                        BlockPos p = Utils.toBlockPos(j + diskcenter.x, k + diskcenter.y, m + diskcenter.z);
                        if (p.distToCenterSqr(diskcenter.x, diskcenter.y, diskcenter.z) <= 7.0) {
                           BlockState bs = this.level().getBlockState(p);
                           boolean whitelisted = false;
                           if (MTConfig.MYSTIC_STAFF_BEAM_BLOCK_WHITELIST.size() > 0) {
                              whitelisted = EffectUtil.inBlockBlacklist(bs.getBlock(), MTConfig.MYSTIC_STAFF_BEAM_BLOCK_WHITELIST, this.level());
                           }

                           if (!bs.isAir() && !whitelisted) {
                              blocks.add(p);
                           }
                        }
                     }
                  }
               }
            }
         }

         ArrayList bs = new ArrayList<>(blocks);
         EffectUtil.applySpellBlockChanges(bs,this.caster,this.caster,false,lst,this.position(),MTConfig.MYSTIC_STAFF_BEAM_MAX_BLOCK_BREAK_PER_TICK,(state,owner,drop,blockPos)->owner.level().destroyBlock(blockPos,false,owner));

         for (Entity e : lst) {
            EffectUtil.magicAttack((LivingEntity)e, (float)damage, this.caster);
         }
      } else {
         this.setDying(1);
      }
   }

   public void casterPosition(boolean casterpos) {
      this.prevx = this.getX();
      this.prevy = this.getY();
      this.prevz = this.getZ();
      this.xRotO = this.getRotationPitch();
      this.yRotO = this.getRotationYaw();
      this.setRotationPitch(90.0F - this.caster.xRot);
      this.setRotationYaw(360.0F - this.caster.yRot + 180.0F);
      this.yRot = this.getRotationYaw();
      this.xRot = this.getRotationPitch();
      if (casterpos) {
         Vec3 v = this.caster.getLookAngle().scale(1.4F).add(this.caster.getEyePosition(1.0F));
         this.setPos(v.x, v.y, v.z);
      }
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setBeamSize(input.getFloatOr("MAHOUTSUKAI_BEAM_SIZE", 0.0F));
      this.setBeamLength(input.getFloatOr("MAHOUTSUKAI_BEAM_LENGTH", 0.0F));
      this.setDying(input.getIntOr("MAHOUTSUKAI_DYING", 0));
      this.setSphereSize(input.getFloatOr("MAHOUTSUKAI_SPHERE_SIZE", 0.0F));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_BEAM_SIZE", this.getBeamSize());
      output.putFloat("MAHOUTSUKAI_BEAM_LENGTH", this.getBeamLength());
      output.putInt("MAHOUTSUKAI_DYING", this.getDying());
      output.putFloat("MAHOUTSUKAI_SPHERE_SIZE", this.getSphereSize());
   }

   @Nullable

   public void push(Entity entityIn) {
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return true;
   }

   public boolean shouldRender(double x, double y, double z) {
      return true;
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

   public int getDying() {
      return (Integer)this.entityData.get(DYING);
   }

   public void setDying(int d) {
      this.entityData.set(DYING, d);
   }

   public float getSphereSize() {
      return (Float)this.entityData.get(SPHERE_SIZE);
   }

   public void setSphereSize(float d) {
      this.entityData.set(SPHERE_SIZE, d);
   }
}
