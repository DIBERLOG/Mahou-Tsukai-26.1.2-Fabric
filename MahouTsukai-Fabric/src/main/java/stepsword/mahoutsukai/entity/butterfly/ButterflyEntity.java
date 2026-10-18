package stepsword.mahoutsukai.entity.butterfly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.ModEntityDataSerializers;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ButterflyEntity extends Entity {
   public static final String entityName = "butterfly_entity";
   private static final String TAG_SIZE = "MAHOUTSUKAI_SIZE";
   private static final String TAG_RANDOM_SEED = "MAHOUTSUKAI_RANDOM_SEED";
   private static final String TAG_PLACE_BLOCK = "MAHOUTSUKAI_PLACE_BLOCK";
   private static final String TAG_BREAK_BLOCK = "MAHOUTSUKAI_BREAK_BLOCK";
   private static final String TAG_HIT = "MAHOUTSUKAI_HIT";
   private static final String TAG_DYING = "MAHOUTSUKAI_DYING";
   private static final String TAG_CASTER = "MAHOUTSUKAI_CASTER";
   private static final String TAG_EFFECT = "MAHOUTSUKAI_EFFECT";
   public ButterflyEntity.EFFECT effect = ButterflyEntity.EFFECT.NONE;
   boolean colorstarted = false;
   int dying = 0;
   private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(ButterflyEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> RANDOM_SEED = SynchedEntityData.defineId(ButterflyEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DYING = SynchedEntityData.defineId(ButterflyEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<UUID>> CASTER = SynchedEntityData.defineId(ButterflyEntity.class, ModEntityDataSerializers.OPTIONAL_UUID);
   private AABB zero = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public AABB aabb;
   public BlockPos origPos;
   public BlockPos origPos1;
   public float angle = 0.0F;
   public float prevangle = 0.0F;
   public float wingspeed = 0.0F;
   public float topstop = 89.0F;
   public float bottomstop = -29.0F;
   public float defaultSize = 0.3F;
   public Random rng;
   public float[][] colors = new float[6][4];
   public float[][] targets = new float[6][4];
   SerializableBlockPlace placeBlock;
   SerializableBlockBreak breakBlock;
   SerializableHit hit;

   public ButterflyEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public ButterflyEntity(Level world) {
      super((EntityType)ModEntities.BUTTERFLY.get(), world);
   }

   public ButterflyEntity(Level world, Player caster, int seed, float size) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setSize(size);
      this.setSeed(seed);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(SIZE, this.defaultSize);
      builder.define(RANDOM_SEED, 812389532);
      builder.define(DYING, false);
      builder.define(CASTER, Optional.empty());
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.yRotO = this.yRot;
         if (this.rng == null) {
            this.rng = new Random(this.getSeed());
         }

         if (this.getSize() == 0.0F) {
            this.setSize(this.defaultSize);
         }

         if (!this.getDying()) {
            this.initColor();
            this.flap();
            this.colorUpdate(1.0F, true);
         } else {
            if (this.dying == 0) {
               for (int i = 0; i < this.targets.length; i++) {
                  this.targets[i][0] = 255.0F;
                  this.targets[i][1] = 255.0F;
                  this.targets[i][2] = 255.0F;
               }
            }

            this.prevangle = this.angle;
            this.dying++;
            this.colorUpdate(10.0F, false);
         }
      } else {
         if (this.getDying()) {
            this.dying++;
            if (this.dying > 25) {
               this.discard();
            }
         }

         if (this.origPos == null) {
            this.origPos = this.blockPosition();
            this.origPos1 = this.origPos.offset(1, 1, 1);
         }

         this.move();
      }
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void move() {
      this.xRot = -30.0F;
      this.yRot %= 360.0F;
      Vec3 motion = this.getDeltaMovement();
      double motionX = motion.x;
      double motionY = motion.y;
      double motionZ = motion.z;
      Vec3 pos = this.position();
      motionX = -0.01 * this.getLookAngle().x;
      motionZ = 0.01 * this.getLookAngle().z;
      if (pos.x() - this.origPos.getX() < 0.1
         || pos.z() - this.origPos.getZ() < 0.1
         || this.origPos1.getX() - pos.x() < 0.1
         || this.origPos1.getZ() - pos.z() < 0.1) {
         Vec3 v = EffectUtil.fromBlockPos(this.origPos).add(0.5, 0.5, 0.5);
         Vec3 moveTo = v.subtract(this.position());
         float yawto = EffectUtil.toDegrees((float)Math.atan2(moveTo.x, moveTo.z));
         float diff = this.rotateTowards(yawto);
         this.yRot += diff;
         this.yRot = Mth.wrapDegrees(this.yRot);
      }

      if (pos.y() - this.origPos.getY() < 0.1) {
         motionY = 0.02;
      } else {
         if (this.origPos1.getY() - pos.y() < 0.1) {
            motionY = -0.02;
         } else if (this.level().getGameTime() % 30L == 0L) {
            motionY = this.random.nextFloat() * 0.04 - 0.02;
         }

         if (!this.getDying()) {
            if (this.aabb == null && this.origPos != null && this.origPos1 != null) {
               double n = 0.25;
               this.aabb = new AABB(
                  this.origPos.getX() + n,
                  this.origPos.getY() + n,
                  this.origPos.getZ() + n,
                  this.origPos1.getX() - n,
                  this.origPos1.getY() - n,
                  this.origPos1.getZ() - n
               );
            }

            List<Entity> lst = this.level().getEntitiesOfClass(Entity.class, this.aabb);
            if (lst.size() > 0) {
               for (Entity e : lst) {
                  if (e != this && this.collide(e)) {
                     break;
                  }
               }
            }
         }
      }

      this.setDeltaMovement(motionX, motionY, motionZ);
      Vec3 newpos = this.position().add(this.getDeltaMovement());
      this.setPos(newpos.x, newpos.y, newpos.z);
   }

   public float rotateTowards(float yaw) {
      return Math.abs((yaw - this.yRot) % 360.0F) < 10.0F ? this.random.nextFloat() * 2.0F - 1.0F : 5.0F;
   }

   public void startDying() {
      this.setDying(true);
   }

   public void initColor() {
      if (!this.colorstarted) {
         for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
               this.colors[i][j] = this.rng.nextInt(255);
               this.targets[i][j] = this.colors[i][j];
            }
         }

         this.colorstarted = true;
      }
   }

   public void colorUpdate(float step, boolean reroll) {
      for (int i = 0; i < 6; i++) {
         for (int j = 0; j < 3; j++) {
            float f = this.colors[i][j];
            float f1 = this.targets[i][j];
            if (f == f1) {
               if (reroll) {
                  this.targets[i][j] = this.rng.nextInt(255);
               }
            } else if (f > f1) {
               this.colors[i][j] = this.colors[i][j] - Math.min(step, f - f1);
            } else if (f < f1) {
               this.colors[i][j] = this.colors[i][j] + Math.min(step, f1 - f);
            }
         }
      }
   }

   public void flap() {
      this.prevangle = this.angle;
      float wingrange = 120.0F;
      float fastestFlap = 5.0F;
      float slowestFlap = 15.0F;
      float slowestSpeed = wingrange / slowestFlap;
      float fastestSpeed = wingrange / fastestFlap;
      this.angle = this.angle + this.wingspeed;
      if (this.angle <= this.bottomstop || this.wingspeed == 0.0F) {
         this.wingspeed = this.rng.nextFloat() * (fastestSpeed - slowestSpeed) + slowestSpeed;
         this.topstop = 90.0F - this.rng.nextFloat() * 12.0F;
      }

      if (this.angle >= this.topstop) {
         this.wingspeed = -(this.rng.nextFloat() * (fastestSpeed - slowestSpeed) + slowestSpeed);
         this.bottomstop = -30.0F + this.rng.nextFloat() * 6.0F;
      }

      this.angle = this.correctAngle(this.angle, -30.0F, 90.0F);
   }

   public float correctAngle(float f, float bottom, float top) {
      float ret = f % (top - bottom);
      if (ret > top) {
         ret = top;
      }

      if (ret < bottom) {
         ret = bottom;
      }

      return ret;
   }

   public void setPlaceEffect(BlockPos pos, Direction direction, BlockState state, ItemStack stack) {
      this.effect = ButterflyEntity.EFFECT.PLACE;
      this.placeBlock = new SerializableBlockPlace();
      this.placeBlock.loc = pos;
      this.placeBlock.state = state;
      this.placeBlock.stack = stack;
      this.origPos = pos.relative(direction);
      this.origPos1 = this.origPos.offset(1, 1, 1);
      this.setPos(this.origPos.getX() + 0.5, this.origPos.getY() + 0.5, this.origPos.getZ() + 0.5);
   }

   public void setBreakEffect(BlockPos pos, Direction direction, BlockState state) {
      this.effect = ButterflyEntity.EFFECT.BREAK;
      this.breakBlock = new SerializableBlockBreak(pos, state);
      this.origPos = pos.relative(direction);
      this.origPos1 = this.origPos.offset(1, 1, 1);
      this.setPos(this.origPos.getX() + 0.5, this.origPos.getY() + 0.5, this.origPos.getZ() + 0.5);
   }

   public void setHitEffect(Player player, ItemStack stack, ItemStack offstack) {
      this.effect = ButterflyEntity.EFFECT.HIT;
      this.hit = new SerializableHit();
      this.hit.stack = stack.copy();
      this.hit.offstack = offstack.copy();
      this.hit.initialPosition = player.position();
      this.hit.effects = new ArrayList<>();

      for (MobEffectInstance effect : player.getActiveEffects()) {
         this.hit.effects.add(new MobEffectInstance(effect));
      }

      this.hit.knockback = new Vec3(Mth.sin(player.yRot * (float) (Math.PI / 180.0)), 0.0, -Mth.cos(player.yRot * (float) (Math.PI / 180.0)));
      this.hit.cooled = this.getCooledAttackStrength(player);
      this.hit.rotyaw = player.yRot;
      this.hit.rotpit = player.xRot;
      this.origPos = Utils.toBlockPos(player.getEyePosition(1.0F).add(player.getLookAngle().subtract(0.0, player.getLookAngle().y, 0.0)));
      this.origPos1 = this.origPos.offset(1, 1, 1);
      this.setPos(this.origPos.getX() + 0.5, this.origPos.getY() + 0.5, this.origPos.getZ() + 0.5);
   }

   public float getCooledAttackStrength(Player player) {
      return player.getAttackStrengthScale(0.5F);
   }

   public void attack(net.fabricmc.fabric.api.entity.FakePlayer player,Entity target){player.attack(target);}
   public boolean triggerEffect(Entity e) {
      if (this.effect == ButterflyEntity.EFFECT.PLACE) {
         if (this.placeBlock != null && this.placeBlock.loc != null && this.level() != null && this.getCasterUUID() != null) {
            if ((
                  !Utils.isBlockAir(this.level(), this.placeBlock.loc)
                     || !EffectUtil.tryChangeBlockState(
                        false,
                        this.placeBlock.loc,
                        this.placeBlock.state,
                        this.level(),
                        this.level().getPlayerByUUID(this.getCasterUUID()),
                        this.getCasterUUID()
                     )
               )
               && e.level() instanceof ServerLevel server) {
               this.spawnAtLocation(server, this.placeBlock.stack);
            }

            this.startDying();
            return true;
         }
      } else if (this.effect == ButterflyEntity.EFFECT.BREAK) {
         if (this.breakBlock != null && this.breakBlock.getLoc() != null && this.level() != null && this.getCasterUUID() != null) {
            if (this.level().getBlockState(this.breakBlock.loc).equals(this.breakBlock.state)) {
               boolean z = EffectUtil.tryChangeBlockState(
                  false,
                  this.breakBlock.loc,
                  Blocks.AIR.defaultBlockState(),
                  this.level(),
                  this.level().getPlayerByUUID(this.getCasterUUID()),
                  this.getCasterUUID()
               );
               if (z) {
                  if (this.level().getPlayerByUUID(this.getCasterUUID()) != null) {
                     MandatoryFun.completeFunReq(this.level().getPlayerByUUID(this.getCasterUUID()), MandatoryFun.FUN_REQS.BUTTERFLY_FUTURE_BLOCK_REMOVE);
                  }

                  Block.dropResources(this.breakBlock.state, this.level(), this.breakBlock.loc);
               }
            }

            this.startDying();
            return true;
         }
      } else if (this.effect == ButterflyEntity.EFFECT.HIT && e instanceof LivingEntity && this.hit.stack != null) {
         SafeFakePlayer fake = SafeFakePlayer.createFakePlayerIfNull((ServerLevel)this.level());
         fake.setPos(this.hit.initialPosition.x, this.hit.initialPosition.y, this.hit.initialPosition.z);
         fake.setPosRaw(this.hit.initialPosition.x, this.hit.initialPosition.y, this.hit.initialPosition.z);
         fake.yRot = this.hit.rotyaw;
         fake.xRot = this.hit.rotpit;

         try {
            resetFakePlayerEquipment(fake);
            ItemStack mainHand = this.hit.stack.copy();
            ItemStack offHand = this.hit.offstack == null ? ItemStack.EMPTY : this.hit.offstack.copy();
            fake.setItemInHand(InteractionHand.MAIN_HAND, mainHand);
            fake.setItemInHand(InteractionHand.OFF_HAND, offHand);
            Caliburn.updateAttr(mainHand, fake, EquipmentSlot.MAINHAND);
            Caliburn.updateAttr(offHand, fake, EquipmentSlot.OFFHAND);
            fake.attackStrengthTicker = Math.max(0, Mth.ceil(Mth.clamp(this.hit.cooled, 0.0F, 1.0F) * fake.getCurrentItemAttackStrengthDelay() - 0.5F));
            fake.removeAllEffects();
            if (this.hit.effects != null && this.hit.effects.size() > 0) {
               for (MobEffectInstance ei : this.hit.effects) {
                  if (ei.getEffect() != ModEffects.FORESIGHT) {
                     fake.addEffect(new MobEffectInstance(ei));
                  }
               }
            }

            this.attack(fake, e);
         } catch (Exception ex) {
            Utils.err(ex.toString());
         }

         this.startDying();
      }

      return false;
   }

   private static void resetFakePlayerEquipment(net.fabricmc.fabric.api.entity.FakePlayer player) {
      EnchantmentHelper.stopLocationBasedEffects(player.getMainHandItem(), player, EquipmentSlot.MAINHAND);
      EnchantmentHelper.stopLocationBasedEffects(player.getOffhandItem(), player, EquipmentSlot.OFFHAND);
      removeItemAttributes(player.getMainHandItem(), player, EquipmentSlot.MAINHAND);
      removeItemAttributes(player.getOffhandItem(), player, EquipmentSlot.OFFHAND);
      player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
      player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
   }

   private static void removeItemAttributes(ItemStack stack, net.fabricmc.fabric.api.entity.FakePlayer player, EquipmentSlot slot) {
      stack.forEachModifier(slot, (attribute, modifier) -> {
         AttributeInstance attributeInstance = player.getAttributes().getInstance(attribute);
         if (attributeInstance != null) {
            attributeInstance.removeModifier(modifier.id());
         }
      });
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isInvisibleTo(Player player) {
      return !MTConfig.BUTTERFLY_EFFECT_VISIBLE_TO_OTHERS && !player.getUUID().equals(this.getCasterUUID());
   }

   public void setCasterUUID(UUID uuid) {
      this.entityData.set(CASTER, Optional.of(uuid));
   }

   public UUID getCasterUUID() {
      return this.entityData.get(CASTER).orElse(null);
   }

   public boolean collide(Entity e) {
      return !this.level().isClientSide()
            && (this.getCasterUUID() == null || !e.getUUID().equals(this.getCasterUUID()))
            && !(e instanceof ButterflyEntity)
            && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), e)
         ? this.triggerEffect(e)
         : false;
   }

   protected void readAdditionalSaveData(ValueInput input) {
      this.setSize(input.getFloatOr("MAHOUTSUKAI_SIZE", this.defaultSize));
      this.setSeed(input.getIntOr("MAHOUTSUKAI_RANDOM_SEED", 812389532));
      input.read("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC).ifPresent(pos->{this.origPos=pos;this.origPos1=pos.offset(1,1,1);});
      this.setDying(input.getBooleanOr("MAHOUTSUKAI_DYING", false));
      input.read("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC).ifPresent(this::setCasterUUID);
      this.placeBlock = (SerializableBlockPlace)input.read("MAHOUTSUKAI_PLACE_BLOCK", SerializableBlockPlace.CODEC).orElse(null);
      this.breakBlock = (SerializableBlockBreak)input.read("MAHOUTSUKAI_BREAK_BLOCK", SerializableBlockBreak.CODEC).orElse(null);
      this.hit = (SerializableHit)input.read("MAHOUTSUKAI_HIT", SerializableHit.CODEC).orElse(null);
      int effectIdx = input.getIntOr("MAHOUTSUKAI_EFFECT", ButterflyEntity.EFFECT.NONE.ordinal());
      if (effectIdx >=0 && effectIdx < ButterflyEntity.EFFECT.values().length) {
         this.effect = ButterflyEntity.EFFECT.values()[effectIdx];
      }
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      output.putFloat("MAHOUTSUKAI_SIZE", this.getSize());
      output.putInt("MAHOUTSUKAI_RANDOM_SEED", this.getSeed());
      output.putBoolean("MAHOUTSUKAI_DYING", this.getDying());
      output.storeNullable("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC, this.getCasterUUID());
      if (this.placeBlock != null) {
         output.store("MAHOUTSUKAI_PLACE_BLOCK", SerializableBlockPlace.CODEC, this.placeBlock);
      }

      if (this.breakBlock != null) {
         output.store("MAHOUTSUKAI_BREAK_BLOCK", SerializableBlockBreak.CODEC, this.breakBlock);
      }

      if (this.hit != null) {
         output.store("MAHOUTSUKAI_HIT", SerializableHit.CODEC, this.hit);
      }

      output.storeNullable("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC,this.origPos);
      output.putInt("MAHOUTSUKAI_EFFECT", this.effect.ordinal());
   }

   public float getSize() {
      return (Float)this.entityData.get(SIZE);
   }

   public void setSize(float f) {
      this.entityData.set(SIZE, f);
   }

   public int getSeed() {
      return (Integer)this.entityData.get(RANDOM_SEED);
   }

   public void setSeed(int f) {
      this.entityData.set(RANDOM_SEED, f);
   }

   public boolean getDying() {
      return (Boolean)this.entityData.get(DYING);
   }

   public void setDying(boolean f) {
      this.entityData.set(DYING, f);
   }

   public enum EFFECT {
      HIT,
      SHOOT,
      PLACE,
      BREAK,
      NONE;
   }
}