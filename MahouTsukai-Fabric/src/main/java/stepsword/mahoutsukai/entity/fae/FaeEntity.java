package stepsword.mahoutsukai.entity.fae;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.effects.Leylines;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.networking.FaeNoisePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FaeEntity extends Animal implements FlyingAnimal {
   public static final String entityName = "fae";
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<Float> COLOR_R = SynchedEntityData.defineId(FaeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_G = SynchedEntityData.defineId(FaeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_B = SynchedEntityData.defineId(FaeEntity.class, EntityDataSerializers.FLOAT);
   private static final String TAG_COLOR_R = "MAHOUTSUKAI_COLOR_R";
   private static final String TAG_COLOR_G = "MAHOUTSUKAI_COLOR_G";
   private static final String TAG_COLOR_B = "MAHOUTSUKAI_COLOR_B";

   public FaeEntity(Level worldIn) {
      super((EntityType)ModEntities.FAE.get(), worldIn);
      this.moveControl = new FaeMoveControl(this);
      this.randomColor();
   }

   public FaeEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
      this.moveControl = new FaeMoveControl(this);
      this.randomColor();
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageable) {
      return new FaeEntity(world);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FaeAIFollow(this, 1.0, 8.0F, 7.0F, 2.0F));
      this.goalSelector.addGoal(2, new FaeAIWander(this, 1.0, 1));
      this.goalSelector.addGoal(0, new BreedGoal(this, 1.0));
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide()) {
         float[] colors = this.getColor();
         if (colors[0] == colors[1] && colors[1] == colors[2]) {
            this.randomColor();
         }
      }
   }

   protected PathNavigation createNavigation(Level worldIn) {
      FlyingPathNavigation pathnavigateflying = new FlyingPathNavigation(this, worldIn) {};
      pathnavigateflying.setCanOpenDoors(false);
      pathnavigateflying.setCanOpenDoors(true);
      return pathnavigateflying;
   }

   public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void pushEntities() {
   }

   public boolean isPickable() {
      return true;
   }

   public boolean isFood(ItemStack stack) {
      Item[] tasty = new Item[]{
         (Item)ModItems.REGISTERED.get("powdered_eye"),
         (Item)ModItems.REGISTERED.get("powdered_emerald"),
         (Item)ModItems.REGISTERED.get("powdered_iron"),
         (Item)ModItems.REGISTERED.get("powdered_diamond"),
         (Item)ModItems.REGISTERED.get("powdered_ender"),
         (Item)ModItems.REGISTERED.get("powdered_quartz"),
         (Item)ModItems.REGISTERED.get("powdered_gold")
      };

      for (Item item : tasty) {
         if (item == stack.getItem()) {
            return true;
         }
      }

      return false;
   }

   public void travel(Vec3 v) {
      Vec3 motion = this.getDeltaMovement();
      double motionX = motion.x;
      double motionY = motion.y;
      double motionZ = motion.z;
      float ff = 0.008F;
      if (!Utils.isBlockAir(this.level(), this.blockPosition().below())) {
         motionY += ff;
      }

      this.setDeltaMovement(motionX, motionY, motionZ);
      if (this.isInWater()) {
         this.moveRelative(0.02F, v);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
      } else if (this.isInLava()) {
         this.moveRelative(0.02F, v);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
      } else {
         float f = 0.91F;
         float f1 = 0.16277136F / (f * f * f);
         this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, v);
         f = 0.91F;
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(f));
      }

      double d1 = this.getX() - this.xo;
      double d0 = this.getZ() - this.zo;
      float f2 = Mth.sqrt((float)(d1 * d1 + d0 * d0)) * 4.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f2 - this.walkAnimation.speed()) * 0.4F);
      this.walkAnimation.position = this.walkAnimation.position + this.walkAnimation.speed();
   }

   public void randomColor() {
      int choice = this.getRandom().nextInt(11);
      float r;
      float g;
      float b;
      if (choice == 0) {
         r = 140.0F;
         g = 0.0F;
         b = 255.0F;
      } else if (choice == 1) {
         r = 51.0F;
         g = 241.0F;
         b = 255.0F;
      } else if (choice == 2) {
         r = 255.0F;
         g = 94.0F;
         b = 0.0F;
      } else if (choice == 3) {
         r = 0.0F;
         g = 222.0F;
         b = 18.0F;
      } else if (choice == 4) {
         r = 255.0F;
         g = 0.0F;
         b = 0.0F;
      } else if (choice == 5) {
         r = 255.0F;
         g = 209.0F;
         b = 41.0F;
      } else if (choice == 6) {
         r = 26.0F;
         g = 5.0F;
         b = 255.0F;
      } else if (choice == 7) {
         r = 68.0F;
         g = 5.0F;
         b = 255.0F;
      } else if (choice == 8) {
         r = 255.0F;
         g = 150.0F;
         b = 201.0F;
      } else if (choice == 9) {
         r = 172.0F;
         g = 255.0F;
         b = 28.0F;
      } else {
         r = 255.0F;
         g = 28.0F;
         b = 77.0F;
      }

      this.setColor(r, g, b);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COLOR_R, 1.0F);
      builder.define(COLOR_G, 1.0F);
      builder.define(COLOR_B, 1.0F);
   }

   public void setColor(float r, float g, float b) {
      this.entityData.set(COLOR_R, r);
      this.entityData.set(COLOR_G, g);
      this.entityData.set(COLOR_B, b);
      this.setSilent(false);
   }

   public float[] getColor() {
      return new float[]{(Float)this.entityData.get(COLOR_R), (Float)this.entityData.get(COLOR_G), (Float)this.entityData.get(COLOR_B)};
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      boolean faesound = MTConfig.FAE_NOISE;
      if (faesound) {
         int z = this.getRandom().nextInt(4);
         SoundEvent[] arr = new SoundEvent[]{
            (SoundEvent)ModSounds.FAE_CHIME_1.get(),
            (SoundEvent)ModSounds.FAE_CHIME_2.get(),
            (SoundEvent)ModSounds.FAE_CHIME_3.get(),
            (SoundEvent)ModSounds.FAE_CHIME_4.get()
         };
         return arr[z];
      } else {
         return null;
      }
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   public void playSound(SoundEvent soundIn, float volume, float pitch) {
      if (!this.isSilent() && !this.level().isClientSide()) {
         this.playSound(this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
      }
   }

   public void playSound(double x, double y, double z, SoundEvent soundIn, SoundSource category, float volume, float pitch) {
      this.sendToAllNear(this.level().getServer().getPlayerList(), x, y, z,
         volume > 1.0F ? 16.0F * volume : 16.0, EffectUtil.getDimension(this.level()),
         new FaeNoisePacket((int)x, (int)y, (int)z, soundIn, category, volume, pitch));
   }

   public void sendToAllNear(PlayerList list, double x, double y, double z, double radius, Identifier dimension, FaeNoisePacket packetIn) {
      if (list != null) {
         for (int i = 0; i < list.getPlayers().size(); i++) {
            ServerPlayer serverPlayer = (ServerPlayer)list.getPlayers().get(i);
            IMahou mahou = Utils.getPlayerMahou(serverPlayer);
            if (mahou != null && mahou.hasMagic() && EffectUtil.compareDimensions(EffectUtil.getDimension(serverPlayer.level()), dimension)) {
               double d0 = x - serverPlayer.getX();
               double d1 = y - serverPlayer.getY();
               double d2 = z - serverPlayer.getZ();
               if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
                  if (serverPlayer.connection != null && ServerPlayNetworking.canSend(serverPlayer, FaeNoisePacket.TYPE))
                     ServerPlayNetworking.send(serverPlayer, packetIn);
               }
            }
         }
      }
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder registerAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.FLYING_SPEED, 0.8F).add(Attributes.MOVEMENT_SPEED, 0.4F);
   }

   public boolean checkSpawnRules(LevelAccessor world, EntitySpawnReason reason) {
      if (reason != EntitySpawnReason.BREEDING && reason != EntitySpawnReason.SPAWN_ITEM_USE && reason != EntitySpawnReason.MOB_SUMMONED) {
         Leylines.LeyLineShape keepshape = Leylines.LeyLineShape.NONE;
         ChunkPos cp = ChunkPos.containing(this.blockPosition());

         for (int i = cp.getMinBlockX(); i <= cp.getMaxBlockX(); i++) {
            for (int j = cp.getMinBlockZ(); j <= cp.getMaxBlockZ(); j++) {
               Leylines.LeyLineShape shape = Leylines.leyOrientation(new BlockPos(i, 0, j));
               if (keepshape == Leylines.LeyLineShape.NONE) {
                  keepshape = shape;
               }

               if (keepshape == Leylines.LeyLineShape.NED
                  || keepshape == Leylines.LeyLineShape.NWD
                  || keepshape == Leylines.LeyLineShape.HORIZ
                  || keepshape == Leylines.LeyLineShape.VERT && shape != Leylines.LeyLineShape.NONE) {
                  keepshape = shape;
               }

               if (keepshape == Leylines.LeyLineShape.CROSS && shape == Leylines.LeyLineShape.STAR) {
                  keepshape = Leylines.LeyLineShape.STAR;
               }
            }
         }

         float thresh = 1.0F;
         if (keepshape == Leylines.LeyLineShape.CROSS) {
            thresh = 0.2F;
         } else if (keepshape == Leylines.LeyLineShape.STAR) {
            thresh = 0.0F;
         } else if (keepshape == Leylines.LeyLineShape.NONE) {
            thresh = 0.7F;
         } else {
            thresh = 0.4F;
         }

         thresh = (float)(thresh * MTConfig.FAE_SPAWN_RATE);
         return Math.random() > thresh && super.checkSpawnRules(world, reason);
      } else {
         return true;
      }
   }

   public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
      return super.isInvulnerableTo(level, source)
         || source == this.damageSources().inWall()
         || source == this.damageSources().flyIntoWall()
         || source == this.damageSources().fall();
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      float[] colors = this.getColor();
      output.putFloat("MAHOUTSUKAI_COLOR_R", colors[0]);
      output.putFloat("MAHOUTSUKAI_COLOR_G", colors[1]);
      output.putFloat("MAHOUTSUKAI_COLOR_B", colors[2]);
   }

   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setColor(input.getFloatOr("MAHOUTSUKAI_COLOR_R", 1.0F), input.getFloatOr("MAHOUTSUKAI_COLOR_G", 1.0F), input.getFloatOr("MAHOUTSUKAI_COLOR_B", 1.0F));
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   public boolean isInvisible() {
      return true;
   }

   public boolean isInvisibleTo(Player player) {
      return !EffectUtil.hasBuff(player, ModEffects.FAY_SIGHT_EYES);
   }
}
