package stepsword.mahoutsukai.entity;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.util.Utils;

public class MahouLightningBoltEntity extends Entity {
   private int lightningState;
   public long boltVertex;
   private int boltLivingTime;
   private final boolean effectOnly;
   @Nullable
   private ServerPlayer caster;
   public static final String entityName = "sky_lightning_bolt_entity";

   public MahouLightningBoltEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
      this.lightningState = 2;
      this.boltVertex = this.random.nextLong();
      this.boltLivingTime = this.random.nextInt(3) + 1;
      this.effectOnly = true;
      Difficulty difficulty = world.getDifficulty();
      if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
         this.igniteBlocks(4);
      }
   }

   public MahouLightningBoltEntity(Level world) {
      super((EntityType)ModEntities.LIGHTNING_BOLT.get(), world);
      this.lightningState = 2;
      this.boltVertex = this.random.nextLong();
      this.boltLivingTime = this.random.nextInt(3) + 1;
      this.effectOnly = true;
      Difficulty difficulty = world.getDifficulty();
      if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
         this.igniteBlocks(4);
      }
   }

   public MahouLightningBoltEntity(Level worldIn, double x, double y, double z, boolean effectOnlyIn) {
      super((EntityType)ModEntities.LIGHTNING_BOLT.get(), worldIn);
      this.moveOrInterpolateTo(new Vec3(x, y, z), 0.0F, 0.0F);
      this.lightningState = 2;
      this.boltVertex = this.random.nextLong();
      this.boltLivingTime = this.random.nextInt(3) + 1;
      this.effectOnly = effectOnlyIn;
      Difficulty difficulty = worldIn.getDifficulty();
      if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
         this.igniteBlocks(4);
      }
   }

   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   public void setCaster(@Nullable ServerPlayer casterIn) {
      this.caster = casterIn;
   }

   public void tick() {
      super.tick();
      if (this.lightningState == 2) {
         this.level()
            .playSound(
               (Player)null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LIGHTNING_BOLT_THUNDER,
               SoundSource.WEATHER,
               10000.0F,
               0.8F + this.random.nextFloat() * 0.2F
            );
         this.level()
            .playSound(
               (Player)null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LIGHTNING_BOLT_IMPACT,
               SoundSource.WEATHER,
               2.0F,
               0.5F + this.random.nextFloat() * 0.2F
            );
      }

      this.lightningState--;
      if (this.lightningState < 0) {
         if (this.boltLivingTime == 0) {
            this.discard();
         } else if (this.lightningState < -this.random.nextInt(10)) {
            this.boltLivingTime--;
            this.lightningState = 1;
            this.boltVertex = this.random.nextLong();
            this.igniteBlocks(0);
         }
      }

      if (this.lightningState >= 0) {
         if (this.level().isClientSide()) {
            this.level().setSkyFlashTime(2);
         } else if (!this.effectOnly) {
            double d0 = 3.0;
            List<Entity> list = this.level()
               .getEntities(
                  this,
                  new AABB(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0),
                  Entity::isAlive
               );

            for (Entity entity : list) {
               LightningBolt fake = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.level(), EntitySpawnReason.EVENT);
               if (fake != null) {
                  fake.moveOrInterpolateTo(new Vec3(this.getX(), this.getY(), this.getZ()));
                  fake.setVisualOnly(false);
                  {
                     entity.thunderHit((ServerLevel)this.level(), fake);
                     entity.setRemainingFireTicks(0);
                  }
               }
            }

            if (this.caster != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.caster, list);
            }
         }
      }
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   private void igniteBlocks(int extraIgnitions) {
      if (MTConfig.EMRYS_BOLT_FIRE && !this.effectOnly && !this.level().isClientSide()) {
         if (this.level() instanceof ServerLevel server && !server.canSpreadFireAround(this.blockPosition())) {
            return;
         }

         BlockState blockstate = Blocks.FIRE.defaultBlockState();
         BlockPos blockpos = Utils.toBlockPos(this.position());
         if (this.level().getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level(), blockpos)) {
            this.level().setBlockAndUpdate(blockpos, blockstate);
         }

         for (int i = 0; i < extraIgnitions; i++) {
            BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (this.level().getBlockState(blockpos1).isAir() && blockstate.canSurvive(this.level(), blockpos1)) {
               this.level().setBlockAndUpdate(blockpos1, blockstate);
            }
         }
      }
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      double d0 = 64.0 * getViewScale();
      return distance < d0 * d0;
   }

   protected void readAdditionalSaveData(ValueInput input) {
   }

   protected void addAdditionalSaveData(ValueOutput output) {
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }
}
