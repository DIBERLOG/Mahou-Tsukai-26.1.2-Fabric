package stepsword.mahoutsukai.entity.mahoujin;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class ReplicaTeleportMahoujinEntity extends MahoujinEntity {
   private static final String TAG_GOAL_X = "MAHOUTSUKAI_GOAL_X";
   private static final String TAG_GOAL_Y = "MAHOUTSUKAI_GOAL_Y";
   private static final String TAG_GOAL_Z = "MAHOUTSUKAI_GOAL_Z";
   private static final String TAG_GOAL_DIM = "MAHOUTSUKAI_GOAL_DIM";
   private static final String TAG_FRIENDLY_ONLY = "MAHOUTSUKAI_FRIENDLY_ONLY";
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static final EntityDataAccessor<Float> GOAL_X = SynchedEntityData.defineId(ReplicaTeleportMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> GOAL_Y = SynchedEntityData.defineId(ReplicaTeleportMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> GOAL_Z = SynchedEntityData.defineId(ReplicaTeleportMahoujinEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<String> GOAL_DIM = SynchedEntityData.defineId(ReplicaTeleportMahoujinEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Boolean> FRIENDLY_ONLY = SynchedEntityData.defineId(
      ReplicaTeleportMahoujinEntity.class, EntityDataSerializers.BOOLEAN
   );
   public static java.util.function.Consumer<ReplicaTeleportMahoujinEntity> clientParticles=e->{};
   public int casterUUID = 0;
   public boolean doTeleport = true;
   public static final String entityName = "replica_teleport_mahoujin_entity";

   public ReplicaTeleportMahoujinEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public ReplicaTeleportMahoujinEntity(Level world) {
      super((EntityType<?>)ModEntities.REPLICA_TELEPORT.get(), world);
   }

   public ReplicaTeleportMahoujinEntity(Level world, LivingEntity caster, float r, float g, float b, float a, Identifier dim, Vec3 goal, boolean friendly) {
      this(world);
      this.casterUUID = caster.getId();this.setCasterUUID(caster.getUUID());
      this.setColor(r, g, b, a, 1.0F, 1.0F, 1.0F, 1.0F);
      this.setGoal(goal);
      this.setGoalDim(dim);
      this.setFriendlyOnly(friendly);
      this.setCircleSize(0.0F);
   }

   public ReplicaTeleportMahoujinEntity(
      Level world, LivingEntity caster, float r, float g, float b, float a, Identifier dim, Vec3 goal, boolean friendly, boolean doTeleport
   ) {
      this(world, caster, r, g, b, a, dim, goal, friendly);
      this.doTeleport = doTeleport;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(GOAL_X, 1.0F);
      builder.define(GOAL_Y, 1.0F);
      builder.define(GOAL_Z, 1.0F);
      builder.define(GOAL_DIM, "minecraft:overworld");
      builder.define(FRIENDLY_ONLY, true);
   }

   @Override
   public void tick() {
      super.tick();
      int teleportStart = MTConfig.REPLICA_TELEPORT_START_TELEPORTING;
      int teleportEnd = MTConfig.REPLICA_TELEPORT_STOP_TELEPORTING;
      int dieTime = MTConfig.REPLICA_TELEPORT_LIFE;
      float maxRadius = (float)MTConfig.REPLICA_TELEPORT_FRIEND_RANGE;
      this.setDeltaMovement(0.0, 0.0, 0.0);
      if (!this.level().isClientSide()) {
         Entity caster = this.level() instanceof net.minecraft.server.level.ServerLevel world?world.getEntity(this.getCasterUUID()):this.level().getEntity(this.casterUUID);
         if (caster == null || !caster.isAlive()) {
            this.discard();return;
         }

         if (this.getLife() < teleportStart && this.getCircleSize() < maxRadius) {
            this.setCircleSize(this.getCircleSize() + 2.0F * maxRadius / teleportStart);
         }

         if (this.getLife() >= teleportStart && this.getLife() < teleportEnd && this.doTeleport) {
            AABB aabb = new AABB(
               this.getX() - maxRadius,
               this.getY() - maxRadius,
               this.getZ() - maxRadius,
               this.getX() + maxRadius,
               this.getY() + maxRadius,
               this.getZ() + maxRadius
            );
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, aabb);
            Vec3 goal = this.getGoal();
            Identifier res = this.getGoalDim();
            boolean friendlyOnly = this.getFriendlyOnly();

            for (Entity e : entities) {
               if ((
                     e instanceof ItemEntity
                        || e instanceof LivingEntity && (!friendlyOnly || ContractMahoujinTileEntity.isImmuneToSpell(this.level(), caster.getUUID(), e))
                  )
                  && e.position().distanceToSqr(this.position()) < maxRadius * maxRadius) {
                  MahouTsukaiTeleporter.teleport(e, goal.x, goal.y, goal.z, res);
               }
            }
         }

         if (this.getLife() > teleportEnd && this.getLife() < dieTime) {
            this.setCircleSize(Math.max(0.0F, this.getCircleSize() - 2.0F * maxRadius / (dieTime - teleportEnd)));
         }

         if (this.getLife() >= dieTime) {
            this.discard();
         }
      } else {
         clientParticles.accept(this);
      }
   }

   public boolean isPickable() {
      return false;
   }

   public void setGoal(Vec3 vec) {
      this.entityData.set(GOAL_X, (float)vec.x);
      this.entityData.set(GOAL_Y, (float)vec.y);
      this.entityData.set(GOAL_Z, (float)vec.z);
   }

   public Vec3 getGoal() {
      return new Vec3(
         ((Float)this.entityData.get(GOAL_X)).floatValue(),
         ((Float)this.entityData.get(GOAL_Y)).floatValue(),
         ((Float)this.entityData.get(GOAL_Z)).floatValue()
      );
   }

   public void setFriendlyOnly(boolean b) {
      this.entityData.set(FRIENDLY_ONLY, b);
   }

   public boolean getFriendlyOnly() {
      return (Boolean)this.entityData.get(FRIENDLY_ONLY);
   }

   @Nullable

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setGoal(
         new Vec3(input.getFloatOr("MAHOUTSUKAI_GOAL_X", 0.0F), input.getFloatOr("MAHOUTSUKAI_GOAL_Y", 0.0F), input.getFloatOr("MAHOUTSUKAI_GOAL_Z", 0.0F))
      );
      this.setGoalDim(Identifier.parse(input.getStringOr("MAHOUTSUKAI_GOAL_DIM", "minecraft:overworld")));
      this.setFriendlyOnly(input.getBooleanOr("MAHOUTSUKAI_FRIENDLY_ONLY", true));this.doTeleport=input.getBooleanOr("MAHOUTSUKAI_DO_TELEPORT",true);
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      Vec3 goal = this.getGoal();
      output.putFloat("MAHOUTSUKAI_GOAL_X", (float)goal.x);
      output.putFloat("MAHOUTSUKAI_GOAL_Y", (float)goal.y);
      output.putFloat("MAHOUTSUKAI_GOAL_Z", (float)goal.z);
      output.putString("MAHOUTSUKAI_GOAL_DIM", this.getGoalDim().toString());
      output.putBoolean("MAHOUTSUKAI_FRIENDLY_ONLY", this.getFriendlyOnly());output.putBoolean("MAHOUTSUKAI_DO_TELEPORT",this.doTeleport);
   }

   public Identifier getGoalDim() {
      return Identifier.parse((String)this.entityData.get(GOAL_DIM));
   }

   public void setGoalDim(Identifier dim) {
      this.entityData.set(GOAL_DIM, dim.toString());
   }
}
