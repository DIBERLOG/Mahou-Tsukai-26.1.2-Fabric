package stepsword.mahoutsukai.entity.mahoujin;

import org.jetbrains.annotations.Nullable;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class SmiteEntity extends MahoujinEntity {
   public record Smoke(SmiteEntity entity,float y,float size,float r,float g,float b){}
   public static java.util.function.Consumer<Smoke> clientSmoke=value->{};
   private static final String TAG_BEAM_SIZE = "MAHOUTSUKAI_BEAM_SIZE";
   private static final String TAG_DISTANCE = "MAHOUTSUKAI_DISTANCE";
   private static final String TAG_BEAM_LENGTH = "MAHOUTSUKAI_BEAM_LENGTH";
   private static final String TAG_COLLAPSING = "MAHOUTSUKAI_COLLAPSING";
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Float> BEAM_SIZE = SynchedEntityData.defineId(SmiteEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(SmiteEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> BEAM_LENGTH = SynchedEntityData.defineId(SmiteEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> COLLAPSING = SynchedEntityData.defineId(SmiteEntity.class, EntityDataSerializers.BOOLEAN);
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public Entity target;
   public float damage;
   public float prev_beam_len;
   public float prev_beam_size;
   public float client_beam_len;
   public float client_beam_size;
   public float maxlen = 256.0F;
   public int m;
   public static final String entityName = "smite_entity";

   public SmiteEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public SmiteEntity(Level world) {
      super((EntityType<?>)ModEntities.SMITE.get(), world);
   }

   public SmiteEntity(Level world, LivingEntity target, float r, float g, float b, float a, float size, float damage) {
      this(world);
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
      this.sizer(size);
      this.target = target;
      this.damage = damage;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BEAM_SIZE, 0.0F);
      builder.define(DISTANCE, 0.0F);
      builder.define(BEAM_LENGTH, 0.0F);
      builder.define(COLLAPSING, false);
   }

   @Override
   public void tick() {
      super.tick();
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      if (!this.level().isClientSide()) {
         if (this.target == null) {
            this.discard();
            return;
         }

         this.setPos(this.target.getX(), this.target.getY(), this.target.getZ());
         if (this.getBeamSize() < this.target.getBbWidth() * 0.75F) {
            this.setBeamSize(Math.min(this.target.getBbWidth() * 0.75F, this.getBeamSize() + 0.1F));
         } else if (this.isCollapsing()) {
            this.setBeamLength(this.getBeamLength() * 0.35F);
            if (this.getBeamLength() <= 1.0F) {
               this.discard();
            }
         } else if (this.getBeamLength() < this.maxlen) {
            this.setBeamLength(Math.min(this.maxlen, this.getBeamLength() + 14.0F));
            if (this.getBeamLength() >= this.maxlen) {
               this.level()
                  .playSound(
                     null, Utils.toBlockPos(this.position()), (SoundEvent)ModSounds.SMITE.get(), SoundSource.NEUTRAL, (float)MTConfig.SMITE_VOLUME, 1.0F
                  );
            }
         } else {
            this.m++;
            if (this.m > 5) {
               EffectUtil.magicAttack((LivingEntity)this.target, this.damage, null);
               this.setCollapsing(true);
            }
         }
      } else {
         this.prev_beam_len = this.client_beam_len;
         this.prev_beam_size = this.client_beam_size;
         this.client_beam_len = this.getBeamLength();
         this.client_beam_size = this.getBeamSize();
         float[] rgb = this.getColors();
         float ringSpacing = 14.0F;
         float startDistance = this.maxlen - 5.0F * ringSpacing;
         if (!this.isCollapsing() && this.client_beam_len > this.prev_beam_len && this.client_beam_len >= startDistance) {
            float descentProgress = Math.min(1.0F, (this.client_beam_len - startDistance) / (this.maxlen - startDistance));
            float ringScale = 0.6F * (float)Math.pow(3.3333333F, descentProgress);
            float ringSize = this.getBeamSize() * ringScale;
            clientSmoke.accept(new Smoke(this,this.maxlen-this.client_beam_len,ringSize,rgb[0],rgb[1],rgb[2]));
         }
      }
   }

   public boolean isPickable() {
      return false;
   }

   public void playerTouch(Player entityIn) {
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }



   public void sizer(float s) {
      this.setBeamSize(s);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setBeamSize(input.getFloatOr("MAHOUTSUKAI_BEAM_SIZE", 0.0F));
      this.setBeamLength(input.getFloatOr("MAHOUTSUKAI_BEAM_LENGTH", 0.0F));
      this.setDistance(input.getFloatOr("MAHOUTSUKAI_DISTANCE", 0.0F));
      this.setCollapsing(input.getBooleanOr("MAHOUTSUKAI_COLLAPSING", false));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putFloat("MAHOUTSUKAI_BEAM_SIZE", this.getBeamSize());
      output.putFloat("MAHOUTSUKAI_BEAM_LENGTH", this.getBeamLength());
      output.putFloat("MAHOUTSUKAI_DISTANCE", this.getDistance());
      output.putBoolean("MAHOUTSUKAI_COLLAPSING", this.isCollapsing());
   }

   public float getBeamSize() {
      return (Float)this.entityData.get(BEAM_SIZE);
   }

   public void setBeamSize(float s) {
      this.entityData.set(BEAM_SIZE, s);
   }

   public float getBeamLength() {
      return (Float)this.entityData.get(BEAM_LENGTH);
   }

   public void setBeamLength(float s) {
      this.entityData.set(BEAM_LENGTH, s);
   }

   public boolean isCollapsing() {
      return (Boolean)this.entityData.get(COLLAPSING);
   }

   public void setCollapsing(boolean collapsing) {
      this.entityData.set(COLLAPSING, collapsing);
   }

   public float getDistance() {
      return (Float)this.entityData.get(DISTANCE);
   }

   public void setDistance(float d) {
      this.entityData.set(DISTANCE, d);
   }
}
