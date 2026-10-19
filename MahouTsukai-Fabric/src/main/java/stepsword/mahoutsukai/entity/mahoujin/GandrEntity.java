package stepsword.mahoutsukai.entity.mahoujin;

import java.util.ArrayList;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;

import stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll;
import stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class GandrEntity extends MahoujinEntity {
   public static java.util.function.Consumer<GandrEntity> clientParticles=entity->{};
   public static final String entityName = "gandr_entity";
   private static final String TAG_DAMAGE = "MAHOUTSUKAI_DAMAGE";
   private static final String TAG_FIRED_PROGRESS_LIFE = "MAHOUTSUKAI_FIRED_PROGRESS_LIFE";
   private static final String TAG_TICKS_SINCE_FIRED = "MAHOUTSUKAI_TICKS_SINCE_FIRED";
   private static final String TAG_TIME_CREATED = "MAHOUTSUKAI_TIME_CREATED";
   private static final String TAG_SWIRL_LIFE = "MAHOUTSUKAI_SWIRL_LIFE";
   public ArrayList<MobEffectInstance> effects = new ArrayList<>();
   public float damage = 0.0F;
   public int swirls = 100;
   public float[] progressstarts = new float[this.swirls];
   public float[] progressends = new float[this.swirls];
   public float[] angles = new float[this.swirls];
   public boolean inited = false;
   public float swirlLife = 9.0F;
   public int firedProgressLife = 3;
   public int ticksSinceFired = 0;
   public long timeCreated = -1L;

   public GandrEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public GandrEntity(Level world) {
      super((EntityType<?>)ModEntities.GANDR.get(), world);
   }

   public GandrEntity(Level world, LivingEntity caster, float r, float g, float b, float a, float r2, float g2, float b2) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, r2, g2, b2, 1.0F);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.effects = new ArrayList<>(input.read("MAHOUTSUKAI_GANDR_EFFECTS",MobEffectInstance.CODEC.listOf()).orElse(java.util.List.of()));
      this.damage = input.getFloatOr("MAHOUTSUKAI_DAMAGE", 0.0F);
      this.firedProgressLife = input.getIntOr("MAHOUTSUKAI_FIRED_PROGRESS_LIFE", 3);
      this.ticksSinceFired = input.getIntOr("MAHOUTSUKAI_TICKS_SINCE_FIRED", 0);
      this.timeCreated = input.getLongOr("MAHOUTSUKAI_TIME_CREATED", -1L);
      this.swirlLife = input.getFloatOr("MAHOUTSUKAI_SWIRL_LIFE", 9.0F);
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("MAHOUTSUKAI_GANDR_EFFECTS",MobEffectInstance.CODEC.listOf(),this.effects);
      output.putFloat("MAHOUTSUKAI_DAMAGE", this.damage);
      output.putInt("MAHOUTSUKAI_FIRED_PROGRESS_LIFE", this.firedProgressLife);
      output.putInt("MAHOUTSUKAI_TICKS_SINCE_FIRED", this.ticksSinceFired);
      output.putLong("MAHOUTSUKAI_TIME_CREATED", this.timeCreated);
      output.putFloat("MAHOUTSUKAI_SWIRL_LIFE", this.swirlLife);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.timeCreated == -1L) {
         this.timeCreated = this.level().getGameTime();
      }

      if (this.level().isClientSide()) {
         if (!this.inited) {
            for (int i = 0; i < this.swirls; i++) {
               this.progressstarts[i] = i * 1.0F / this.swirls;
               this.progressends[i] = this.progressstarts[i] + this.swirlLife / 100.0F;
               this.angles[i] = this.random.nextFloat() * 360.0F;
               if (this.progressends[i] > 1.0F) {
                  this.progressends[i]--;
               }
            }

            this.inited = true;
         }
      } else {
         if (this.ticksSinceFired > 800 || this.level().getGameTime() - this.timeCreated > 6000L) {
            this.discard();
         }

         if (this.getCasterUUID() == null) {
            this.discard();
         } else {
            Player player = this.level().getPlayerByUUID(this.getCasterUUID());
            if (!this.getFired()
               && (player == null || !stepsword.mahoutsukai.item.mysticcode.MysticCode.isChanneling(player,GandrSpellScroll.class) && !stepsword.mahoutsukai.item.mysticcode.MysticCode.isChanneling(player,stepsword.mahoutsukai.doom.DoomScroll.class))) {
               this.discard();
            }
         }
      }

      if (this.getFired()) {
         this.ticksSinceFired++;
         if (this.level().isClientSide()) {
            clientParticles.accept(this);
         } else {
            this.checkHit();
         }
      }

      if (this.getCasterUUID() != null && !this.getFired()) {
         Player player = this.level().getPlayerByUUID(this.getCasterUUID());
         if (player != null) {
            this.setLookPos(player.level().isClientSide(), player, this);
         }
      }

      this.teleportTo(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void checkHit() {
      Vec3 vec3d = this.getDeltaMovement();
      Vec3 vec3d2 = this.position();
      Vec3 vec3d3 = vec3d2.add(vec3d);
      HitResult raytraceresult = this.level().clip(new ClipContext(vec3d2, vec3d3, Block.COLLIDER, Fluid.NONE, this));
      if (raytraceresult.getType() != Type.MISS) {
         vec3d3 = raytraceresult.getLocation();
      }

      while (!this.isRemoved()) {
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

         if (raytraceresult != null && raytraceresult.getType() != Type.MISS) {
            this.onHit(raytraceresult);
         }

         if (entityraytraceresult == null) {
            break;
         }

         raytraceresult = null;
      }
   }

   public void onHit(HitResult result) {
      Vec3 look = this.getViewVector(1.0F);
      int black_particles = MTConfig.GANDR_BLACK_PARTICLES_HIT;
      int red_particles = MTConfig.GANDR_RED_PARTICLES_HIT;
      float radius = (float)MTConfig.GANDR_HIT_RADIUS;
      int duration = MTConfig.GANDR_CLOUD_DURATION;
      AreaEffectCloud area = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      boolean spawn = false;
      if (this.getShooter() instanceof LivingEntity) {
         area.setRadius(radius);
         area.setOwner((LivingEntity)this.getShooter());
         area.setDuration(duration);

         for (MobEffectInstance e : this.effects) {
            if (validEffect(e, this.getShooter().level())) {
               spawn = true;
               area.addEffect(e);
            }
         }
      }

      if (spawn) {
         this.level().addFreshEntity(area);
      }

      AABB aabb = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius);

      for (Entity entity : this.level().getEntities(this.getShooter(), aabb, Entity::isAlive)) {
         if (entity instanceof LivingEntity && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), entity)) {
            EffectUtil.magicAttack((LivingEntity)entity, this.damage, this.level().getPlayerByUUID(this.getCasterUUID()));
            if ((!entity.isAlive() || ((LivingEntity)entity).getHealth() <= 0.0F) && this.getShooter() instanceof ServerPlayer) {
               ((MahouTrigger)ModTriggers.GANDR).trigger((ServerPlayer)this.getShooter());
            }
         }
      }

      float[] rgbs = this.getColors();
      PacketHandler.sendTracking(
         this,
         new MagicCircleBoomParticlePacket(
            this.getX(),
            this.getY(),
            this.getZ(),
            look.x,
            look.y,
            look.z,
            2.0,
            1,
            1,
            black_particles,
            true,
            true,
            rgbs[0],
            rgbs[1],
            rgbs[2],
            rgbs[4],
            rgbs[5],
            rgbs[6]
         )
      );
      PacketHandler.sendTracking(
         this,
         new MagicCircleBoomParticlePacket(
            this.getX(),
            this.getY(),
            this.getZ(),
            look.x,
            look.y,
            look.z,
            2.0,
            0,
            1,
            red_particles,
            true,
            true,
            rgbs[0],
            rgbs[1],
            rgbs[2],
            rgbs[4],
            rgbs[5],
            rgbs[6]
         )
      );
      this.discard();
   }

   public static boolean validEffect(MobEffectInstance e, Level level) {
      Holder<MobEffect> effect = e.getEffect();
      boolean inwhitelist = false;

      for (String s : MTConfig.GANDR_EFFECT_WHITELIST) {
         if (s.equals(Utils.getRegistryName((MobEffect)e.getEffect().value(), level))) {
            inwhitelist = true;
         }
      }

      boolean inblacklist = false;

      for (String s : MTConfig.GANDR_EFFECT_BLACKLIST) {
         if (s.equals(Utils.getRegistryName((MobEffect)e.getEffect().value(), level))) {
            inblacklist = true;
         }
      }

      return inwhitelist || !inblacklist && ((MobEffect)effect.value()).getCategory() == MobEffectCategory.HARMFUL;
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
            && (!p_213871_1_.getUUID().equals(this.getCasterUUID()) || this.ticksSinceFired >= 5),
         1.0F
      );
   }

   public Vec3 setLookPos(boolean client, LivingEntity caster, GandrEntity gandr) {
      Vec3 look = caster.getViewVector(1.0F);
      Vec3 oldlook = look.normalize();
      look = oldlook.scale(1.1F);
      Vec3 start = look.add(caster.getEyePosition(1.0F));
      gandr.setYRot(caster.getYRot());
      gandr.setXRot(caster.getXRot());
      if (!client) {
         gandr.setPos(start.x, start.y, start.z);
      }

      return start;
   }

   public float getGrowingLife() {
      return 50.0F;
   }
}
