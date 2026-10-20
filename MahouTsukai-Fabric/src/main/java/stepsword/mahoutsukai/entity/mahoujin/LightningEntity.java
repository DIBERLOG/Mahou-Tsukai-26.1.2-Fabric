package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.entity.ModEntities;

import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.emrys.StaffEmrys;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class LightningEntity extends Entity {
   private static final String TAG_BEAM_SIZE = "MAHOUTSUKAI_BEAM_SIZE";
   private static final String TAG_COLOR_R = "MAHOUTSUKAI_COLOR_R";
   private static final String TAG_COLOR_G = "MAHOUTSUKAI_COLOR_G";
   private static final String TAG_COLOR_B = "MAHOUTSUKAI_COLOR_B";
   private static final String TAG_COLOR_A = "MAHOUTSUKAI_COLOR_A";
   private static final String TAG_CASTER = "MAHOUTSUKAI_CASTER";
   private static final String TAG_TARGET = "MAHOUTSUKAI_TARGET";
   private static final String TAG_HAS_BALL = "MAHOUTSUKAI_HAS_BALL";
   private static final String TAG_ZAP = "MAHOUTSUKAI_ZAP";
   public boolean rolled = false;
   private static final EntityDataAccessor<Float> BEAM_SIZE = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_R = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_G = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_B = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_A = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CASTER_ID = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> HAS_BALL = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ZAP = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.BOOLEAN);
   public LivingEntity target;
   public LivingEntity caster;
   private java.util.UUID savedCaster,savedTarget;
   public double prev_x;
   public double prev_y;
   public double prev_z;
   public float[] segmentlengths;
   public float[] offshootlengths;
   public float[] pitchs;
   public float[] reallengths;
   public float[] prevpitchs;
   public float[] prevreallengths;
   public int segments = 0;
   public float yaw = 0.0F;
   public float pitch = 0.0F;
   public static final String entityName = "lightning_entity";

   public LightningEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public LightningEntity(Level world) {
      super((EntityType)ModEntities.LIGHTNING.get(), world);
   }

   public LightningEntity(Level world, LivingEntity caster, LivingEntity target, float r, float g, float b, float a, float size, boolean zap) {
      this(world);
      this.setColor(r, g, b, a);
      this.sizer(size);
      this.caster = caster;
      this.target = target;
      this.entityData.set(TARGET_ID, target.getId());
      this.entityData.set(CASTER_ID, caster.getId());
      this.setZap(zap);
      this.setLookPos(world.isClientSide());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(BEAM_SIZE, 0.0F);
      builder.define(COLOR_R, 1.0F);
      builder.define(COLOR_G, 1.0F);
      builder.define(COLOR_B, 1.0F);
      builder.define(COLOR_A, 0.0F);
      builder.define(CASTER_ID, -1);
      builder.define(TARGET_ID, -1);
      builder.define(HAS_BALL, false);
      builder.define(ZAP, false);
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public void tick() {
      if(this.level() instanceof ServerLevel world){
       if(caster==null && savedCaster!=null && world.getEntity(savedCaster) instanceof LivingEntity living){caster=living;setCasterId(living.getId());}
       if(target==null && savedTarget!=null && world.getEntity(savedTarget) instanceof LivingEntity living){target=living;setTargetId(living.getId());}
      }

      if (this.caster != null && this.target != null) {
         if(!this.level().isClientSide()&&this.caster instanceof stepsword.mahoutsukai.entity.RealityMarbleBossEntity boss&&!boss.canUseEmrys(this.target)){this.discard();return;}
         Vec3 targeteye = this.target.getEyePosition(1.0F).add(0.0, this.getZap() ? -this.target.getBbHeight() / 2.0F : 0.0, 0.0);
         Vec3 start = this.setLookPos(this.level().isClientSide());
         if (!this.level().isClientSide()) {
            if (this.target.isAlive() && this.caster.isAlive()) {
               if (!(targeteye.subtract(start).length() > MTConfig.EMRYS_MAX_RANGE * 2)
                  && (this.getZap() || this.caster.getUseItem().getItem() == ModItems.emrysStaff.get())) {
                  if (!this.getZap() && this.level().getGameTime() % 20L == 0L) {
                     if (this.caster instanceof Player p && this.target instanceof Phantom) {
                        MandatoryFun.completeFunReq(p, MandatoryFun.FUN_REQS.EMRYS_HIT_PHANTOM);
                     }

                     this.target.hurtServer((ServerLevel)this.level(),this.caster.damageSources().lightningBolt(), (float)MTConfig.EMRYS_DAMAGE_FOCUSED_PER_SECOND);
                     this.target.lastHurtByPlayerMemoryTime = 100;
                     if (this.caster instanceof Player) {
                        StaffEmrys.zap(this.target, false, (Player)this.caster);
                     }
                  }
               } else {
                  this.discard();
               }

               if (this.getZap() && this.tickCount > 8) {
                  if(this.caster instanceof stepsword.mahoutsukai.entity.RealityMarbleBossEntity)
                     this.target.hurtServer((ServerLevel)this.level(),this.caster.damageSources().indirectMagic(this,this.caster),(float)MTConfig.EMRYS_DAMAGE_ZAP);
                  else EffectUtil.magicAttack(this.target, (float)MTConfig.EMRYS_DAMAGE_ZAP, this.caster);
                  EffectUtil.buff(this.target, ModEffects.ZAP_COOLDOWN, false, 60);
                  StaffEmrys.zap(this.target, false, null);
                  this.discard();
               }
            } else {
               this.discard();
            }
         } else {
            this.prev_x = this.getX();
            this.prev_y = this.getY();
            this.prev_z = this.getZ();
            Vec3 v2 = targeteye.subtract(start);
            Vec3 v = v2.normalize();
            this.pitch = 90.0F - EffectUtil.toDegrees((float)Math.asin(-v.y));
            this.yaw = 180.0F + EffectUtil.toDegrees((float)Math.atan2(v.x, v.z));
            float len = (float)v2.length();
            if (!this.rolled) {
               this.rerollLightningShape(len);
            }

            if (this.segments > 0) {
               this.copyPrevSegments();
               this.randomLightningMovement();
               this.shrink();
               this.correct(len);
               this.calculateLightningAnglesAndLengths();
            }
         }
      } else if (this.level().isClientSide()) {
         Entity t = this.level().getEntity((Integer)this.entityData.get(TARGET_ID));
         Entity c = this.level().getEntity((Integer)this.entityData.get(CASTER_ID));
         if (t instanceof LivingEntity) {
            this.target = (LivingEntity)t;
         }

         if (c instanceof LivingEntity) {
            this.caster = (LivingEntity)c;
         }
      }
   }

   public void randomLightningMovement() {
      float sidegrowth = 1.0F / this.segments;

      for (int i = 0; i < this.offshootlengths.length - 2; i++) {
         this.offshootlengths[i] = this.offshootlengths[i] + this.random.nextFloat() * sidegrowth;
         this.offshootlengths[i] = this.offshootlengths[i] % 0.4F;
      }

      for (int i = 0; i < this.segmentlengths.length - 1; i++) {
         this.segmentlengths[i] = this.segmentlengths[i] + Math.max(0.0F, this.random.nextFloat() * sidegrowth * 3.0F - sidegrowth / 1.5F);
      }
   }

   public void shrink() {
      if (this.segmentlengths[this.segmentlengths.length - 1] < 0.7F) {
         if (this.offshootlengths[this.offshootlengths.length - 2] < 0.7F) {
            for (int i = this.segmentlengths.length - 1; i > 0; i--) {
               this.segmentlengths[i] = this.segmentlengths[i - 1];
            }

            this.segmentlengths[0] = 1.0E-4F;

            for (int i = this.offshootlengths.length - 2; i > 0; i--) {
               this.offshootlengths[i] = this.offshootlengths[i - 1];
            }

            this.offshootlengths[0] = 1.0E-4F;
         } else {
            this.offshootlengths[this.offshootlengths.length - 2] = this.offshootlengths[this.offshootlengths.length - 2]
               - (0.1F + this.random.nextFloat() * 0.1F);
         }
      }
   }

   public void correct(float totallen) {
      float z = 0.0F;

      for (int i = 0; i < this.segmentlengths.length; i++) {
         z += this.segmentlengths[i];
      }

      float overflow = z - totallen;

      for (int i = this.segmentlengths.length - 1; i >= 0 && overflow > 0.0F; i--) {
         float t = this.segmentlengths[i];
         if (t > overflow) {
            this.segmentlengths[i] = t - overflow;
         } else {
            this.segmentlengths[i] = 0.0F;
         }

         overflow -= t;
      }
   }

   public void copyPrevSegments() {
      this.prevpitchs = new float[this.pitchs.length];

      for (int i = 0; i < this.prevpitchs.length; i++) {
         this.prevpitchs[i] = this.pitchs[i];
      }

      this.prevreallengths = new float[this.reallengths.length];

      for (int i = 0; i < this.prevreallengths.length; i++) {
         this.prevreallengths[i] = this.reallengths[i];
      }
   }

   public Vec3 setLookPos(boolean client) {
      Vec3 look = this.caster.getViewVector(1.0F);
      Vec3 oldlook = look.normalize();
      look = oldlook.scale(1.9F);
      look = look.add(oldlook.yRot(EffectUtil.toRad(-90.0F)).scale(0.3F)).add(oldlook.xRot(EffectUtil.toRad(-90.0F)).scale(0.3F));
      if (this.getZap()) {
         look = new Vec3(0.0, -this.caster.getBbHeight() / 2.0F, 0.0);
      }

      Vec3 start = look.add(this.caster.getEyePosition(1.0F));
      if (!client) {
         this.setPos(start.x, start.y, start.z);
      }

      return start;
   }

   public void rerollLightningShape(float totalLenToTarget) {
      float remaining = totalLenToTarget;
      this.segments = this.random.nextInt(4) + 6;
      this.segmentlengths = new float[this.segments];
      this.offshootlengths = new float[this.segments];

      for (int i = 0; i < this.segments; i++) {
         if (i == this.segments - 1) {
            this.segmentlengths[i] = remaining;
         } else {
            this.segmentlengths[i] = this.random.nextFloat() * remaining * 1.0F / this.segments;
         }

         this.offshootlengths[i] = this.random.nextFloat() * (this.getZap() ? 0.9F : 1.5F);
         remaining -= this.segmentlengths[i];
      }

      this.offshootlengths[this.segments - 1] = 0.0F;
      this.calculateLightningAnglesAndLengths();
      this.rolled = true;
   }

   public void calculateLightningAnglesAndLengths() {
      this.pitchs = new float[this.segments];
      this.reallengths = new float[this.segments];

      for (int i = 0; i < this.segments; i++) {
         float prevb;
         if (i == 0) {
            prevb = 0.0F;
         } else {
            prevb = this.offshootlengths[i - 1];
         }

         float a = this.segmentlengths[i];
         float b = this.offshootlengths[i] - prevb;
         float c = (float)Math.sqrt(a * a + b * b);
         this.reallengths[i] = c;
         this.pitchs[i] = EffectUtil.toDegrees((float)Math.asin(b / c));
      }
   }

   public boolean isPickable() {
      return false;
   }

   public void playerTouch(Player entityIn) {
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void setColor(float r, float g, float b, float a) {
      this.entityData.set(COLOR_R, r);
      this.entityData.set(COLOR_G, g);
      this.entityData.set(COLOR_B, b);
      this.entityData.set(COLOR_A, a);
   }

   protected void onInsideBlock(BlockState p_state) {
   }

   public float[] getColor() {
      return new float[]{
         (Float)this.entityData.get(COLOR_R), (Float)this.entityData.get(COLOR_G), (Float)this.entityData.get(COLOR_B), (Float)this.entityData.get(COLOR_A)
      };
   }

   public void sizer(float s) {
      this.setBeamSize(s);
   }

   protected void readAdditionalSaveData(ValueInput input) {
      this.setBeamSize(input.getFloatOr("MAHOUTSUKAI_BEAM_SIZE", 0.0F));
      this.setColor(
         input.getFloatOr("MAHOUTSUKAI_COLOR_R", 1.0F),
         input.getFloatOr("MAHOUTSUKAI_COLOR_G", 1.0F),
         input.getFloatOr("MAHOUTSUKAI_COLOR_B", 1.0F),
         input.getFloatOr("MAHOUTSUKAI_COLOR_A", 0.0F)
      );
      this.setHasBall(input.getBooleanOr("MAHOUTSUKAI_HAS_BALL", false));
      this.setTargetId(input.getIntOr("MAHOUTSUKAI_TARGET", -1));
      this.setCasterId(input.getIntOr("MAHOUTSUKAI_CASTER", -1));
      this.setZap(input.getBooleanOr("MAHOUTSUKAI_ZAP", false));savedCaster=input.read("MAHOUTSUKAI_CASTER_UUID",net.minecraft.core.UUIDUtil.CODEC).orElse(null);savedTarget=input.read("MAHOUTSUKAI_TARGET_UUID",net.minecraft.core.UUIDUtil.CODEC).orElse(null);
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      output.putFloat("MAHOUTSUKAI_BEAM_SIZE", this.getBeamSize());
      float[] colors = this.getColor();
      output.putFloat("MAHOUTSUKAI_COLOR_R", colors[0]);
      output.putFloat("MAHOUTSUKAI_COLOR_G", colors[1]);
      output.putFloat("MAHOUTSUKAI_COLOR_B", colors[2]);
      output.putFloat("MAHOUTSUKAI_COLOR_A", colors[3]);
      output.putBoolean("MAHOUTSUKAI_HAS_BALL", (Boolean)this.entityData.get(HAS_BALL));
      output.putInt("MAHOUTSUKAI_CASTER", this.getCasterId());
      output.putInt("MAHOUTSUKAI_TARGET", this.getTargetId());
      output.putBoolean("MAHOUTSUKAI_ZAP", this.getZap());output.storeNullable("MAHOUTSUKAI_CASTER_UUID",net.minecraft.core.UUIDUtil.CODEC,caster==null?savedCaster:caster.getUUID());output.storeNullable("MAHOUTSUKAI_TARGET_UUID",net.minecraft.core.UUIDUtil.CODEC,target==null?savedTarget:target.getUUID());
   }

   public float getBeamSize() {
      return (Float)this.entityData.get(BEAM_SIZE);
   }

   public void setBeamSize(float s) {
      this.entityData.set(BEAM_SIZE, s);
   }

   public boolean getHasBall() {
      return (Boolean)this.entityData.get(HAS_BALL);
   }

   public void setHasBall(boolean s) {
      this.entityData.set(HAS_BALL, s);
   }

   public boolean getZap() {
      return (Boolean)this.entityData.get(ZAP);
   }

   public void setZap(boolean s) {
      this.entityData.set(ZAP, s);
   }

   public int getTargetId() {
      return (Integer)this.entityData.get(TARGET_ID);
   }

   public void setTargetId(int id) {
      this.entityData.set(TARGET_ID, id);
   }

   public int getCasterId() {
      return (Integer)this.entityData.get(TARGET_ID);
   }

   public void setCasterId(int id) {
      this.entityData.set(CASTER_ID, id);
   }
}
