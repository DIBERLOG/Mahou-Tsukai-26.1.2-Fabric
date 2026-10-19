package stepsword.mahoutsukai.entity;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect;
import stepsword.mahoutsukai.networking.MentalDisplacementUpdatePacket;
import stepsword.mahoutsukai.networking.PacketHandler;

public class MentalDisplacementEntity extends Entity {
   public static java.util.function.Consumer<MentalDisplacementUpdatePacket> clientUpdate=packet->{};
   public static final String entityName = "mental_displacement";
   protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(
      MentalDisplacementEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE
   );
   /** Server-owned remaining life of the controlled form, mirrored to the owner for the countdown. */
   protected static final EntityDataAccessor<Integer> TICKS_LEFT = SynchedEntityData.defineId(
      MentalDisplacementEntity.class, EntityDataSerializers.INT
   );
   public BlockPos origPos;
   public Player player;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYaw;
   private double lerpPitch;
   private boolean backInputDown;
   private boolean forwardInputDown;
   private boolean leftInputDown;
   private boolean rightInputDown;
   private boolean upInputDown;
   private boolean downInputDown;
   public boolean used = false;

   public MentalDisplacementEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public MentalDisplacementEntity(Level world) {
      super((EntityType)ModEntities.MENTAL_DISPLACEMENT.get(), world);
      this.origPos = null;
      this.player = null;
   }

   public MentalDisplacementEntity(Level world, BlockPos o, Player p) {
      this(world);
      this.origPos = o;
      this.player = p;
      this.setPos(this.origPos.getX(), this.origPos.getY(), this.origPos.getZ());
      this.setDeltaMovement(0.0, 0.0, 0.0);
      this.yRot = p.yRot;
      this.xRot = p.xRot;
   }

   private void tickLerp() {
      if (this.lerpSteps > 0) {
         double d0 = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
         double d1 = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
         double d2 = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
         double d3 = Mth.wrapDegrees(this.lerpYaw - this.yRot);
         this.yRot = (float)(this.yRot + d3 / this.lerpSteps);
         this.xRot = (float)(this.xRot + (this.lerpPitch - this.xRot) / this.lerpSteps);
         this.lerpSteps--;
         this.setPos(d0, d1, d2);
         this.setRot(this.yRot, this.xRot);
      }
   }

   protected boolean isLocalClientAuthoritative() {
      return this.level().isClientSide() && this.player != null && this.player.getUUID().equals(this.getOwnerId());
   }

   protected void lerpPositionAndRotationStep(int posRotationIncrements, double x, double y, double z, double yaw, double pitch) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYaw = yaw;
      this.lerpPitch = pitch;
      this.lerpSteps = posRotationIncrements;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if(this.player==null && this.getOwnerId()!=null)this.player=this.level().getPlayerByUUID(this.getOwnerId());
         if (this.player == null) {
            this.finish(null);
         } else if (!this.player.isAlive()) {
            this.finish(null);
         } else if (!this.player.level().equals(this.level())) {
            // Dimension change left the spirit behind: end travel without moving the body.
            this.finish(null);
         } else if (MentalDisplacementSpellEffect.checkDistance(this.player.blockPosition(), this.blockPosition()) > MTConfig.MENTAL_DISPLACEMENT_RANGE) {
            // Out of the legal radius: finish at the spirit's furthest legal point.
            this.finish(this.position());
         } else {
            int left = this.entityData.get(TICKS_LEFT) - 1;
            if (left <= 0) {
               this.finish(this.position());
            } else {
               this.entityData.set(TICKS_LEFT, left);
            }
         }
      } else {
         this.setOldPosAndRot();
         if (this.isLocalClientAuthoritative()) {
            this.playerControl();
         } else {
            this.tickLerp();
         }
      }
   }

   /** Ends travel once: optionally delivers the body, clears saved state and removes this entity. */
   private void finish(@Nullable Vec3 destination) {
      Player owner = this.player;
      if (owner instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         stepsword.mahoutsukai.util.Utils.getPlayerMahou(serverPlayer).setMentalDisplacementPos(null);
         stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(serverPlayer, stepsword.mahoutsukai.util.Utils.getPlayerMahou(serverPlayer));
         if (destination != null) MentalDisplacementSpellEffect.arrive(serverPlayer, destination);
      }
      this.discard();
   }

   public int ticksLeft() {
      return this.entityData.get(TICKS_LEFT);
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void playerControl() {
      if (this.level().isClientSide()) {
         float moveSpeed = 0.01F;
         float slowFactor = 0.1F;
         float f = 1.0E-8F;
         float l = 1.0E-8F;
         float u = 0.0F;
         float fm = 0.0F;
         float lm = 0.0F;
         float um = 1.0F;
         if (this.backInputDown) {
            f += -moveSpeed;
         }

         if (this.forwardInputDown) {
            f += moveSpeed;
         }

         if (this.leftInputDown) {
            l += moveSpeed;
         }

         if (this.rightInputDown) {
            l += -moveSpeed;
         }

         if (this.upInputDown) {
            u += moveSpeed;
         }

         if (this.downInputDown) {
            u += -moveSpeed;
         }

         if (!this.forwardInputDown && !this.backInputDown) {
            fm = 1.0F;
         }

         if (!this.leftInputDown && !this.rightInputDown) {
            lm = 1.0F;
         }

         if (!this.upInputDown && !this.downInputDown) {
            um = slowFactor;
         }

         Vec3 motion = this.getDeltaMovement();
         this.setDeltaMovement(
            motion.x + -(Mth.sin(this.yRot * (float) (Math.PI / 180.0)) * f) + Mth.cos(this.yRot * (float) (Math.PI / 180.0)) * l,
            motion.y,
            motion.z + Mth.cos(this.yRot * (float) (Math.PI / 180.0)) * f + Mth.sin(this.yRot * (float) (Math.PI / 180.0)) * l
         );
         motion = this.getDeltaMovement();
         double g = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
         float angle = (float)(-Math.atan2(motion.x, motion.z)) * 57.2958F;
         double c = g * Mth.cos((angle - this.yRot + 90.0F) * (float) (Math.PI / 180.0));
         double c2 = c * Mth.cos(this.yRot * (float) (Math.PI / 180.0));
         double c1 = c * Mth.sin(this.yRot * (float) (Math.PI / 180.0));
         double d = g * Mth.cos((angle - this.yRot) * (float) (Math.PI / 180.0));
         double d2 = -d * Mth.sin(this.yRot * (float) (Math.PI / 180.0));
         double d1 = d * Mth.cos(this.yRot * (float) (Math.PI / 180.0));
         this.setDeltaMovement(motion.x - lm * c2 - fm * d2, um * (u + motion.y), motion.z - lm * c1 - fm * d1);
         Vec3 v = this.position().add(this.getDeltaMovement());
         this.setPos(v.x, v.y, v.z);
         Vec3 dm = this.getDeltaMovement();
         Vec3 pp = this.position();
         clientUpdate.accept(new MentalDisplacementUpdatePacket(this, pp.x, pp.y, pp.z, dm.x, dm.y, dm.z, this.yRot, this.xRot));
      }
   }

   public void updatePacket(MentalDisplacementUpdatePacket packet) {
      this.setPos(packet.x, packet.y, packet.z);
      this.setDeltaMovement(packet.vx, packet.vy, packet.vz);
      this.setRot((float)packet.yaw, (float)packet.pitch);
   }

   public void syncLookToPlayer(Player player) {
      this.setRot(player.getYRot(), player.getXRot());
      this.yRotO = player.yRotO;
      this.xRotO = player.xRotO;
   }

   public void updateInputs(boolean b, boolean f, boolean l, boolean r, boolean u, boolean d) {
      this.backInputDown = b;
      this.forwardInputDown = f;
      this.leftInputDown = l;
      this.rightInputDown = r;
      this.upInputDown = u;
      this.downInputDown = d;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(OWNER_UNIQUE_ID, Optional.empty());
      builder.define(TICKS_LEFT, MTConfig.MENTAL_DISPLACEMENT_MAX_TICKS);
   }

   @Nullable
   public UUID getOwnerId() {
      return this.entityData.get(OWNER_UNIQUE_ID).map(EntityReference::getUUID).orElse(null);
   }

   public void setOwnerId(Player player) {
      this.entityData.set(OWNER_UNIQUE_ID, Optional.of(EntityReference.of(player)));
   }

   protected void readAdditionalSaveData(ValueInput input) {
      this.used = input.getBooleanOr("mahoutsukai_used", false);
      this.entityData.set(TICKS_LEFT, input.getIntOr("mahoutsukai_ticks_left", MTConfig.MENTAL_DISPLACEMENT_MAX_TICKS));
      this.origPos=input.read("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC).orElse(this.blockPosition());
      input.read("MAHOUTSUKAI_OWNER",net.minecraft.core.UUIDUtil.CODEC).ifPresent(uuid->this.entityData.set(OWNER_UNIQUE_ID,Optional.of(EntityReference.of(uuid))));
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      output.putBoolean("mahoutsukai_used", this.used);
      output.putInt("mahoutsukai_ticks_left", this.entityData.get(TICKS_LEFT));
      if(origPos!=null)output.store("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC,origPos);
      if(getOwnerId()!=null)output.store("MAHOUTSUKAI_OWNER",net.minecraft.core.UUIDUtil.CODEC,getOwnerId());
   }


}
