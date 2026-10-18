package stepsword.mahoutsukai.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.GardenPacket;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.particle.ModParticles;
import stepsword.mahoutsukai.tickets.ModTickets;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FamiliarEntity extends ShoulderRidingEntity implements FlyingAnimal {
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   public static final int exploreRadius = 5;
   SitWhenOrderedToGoal sitGoal;
   private UUID familiarId = null;
   public int r;
   public int g;
   public int b;
   private static final String TAG_COLOR_R = "MAHOUTSUKAI_COLOR_R";
   private static final String TAG_COLOR_G = "MAHOUTSUKAI_COLOR_G";
   private static final String TAG_COLOR_B = "MAHOUTSUKAI_COLOR_B";
   private static final String TAG_GARDEN_TICKS = "MAHOUTSUKAI_GARDEN_TICKS";
   private static final String TAG_HOLIDAY = "MAHOUTSUKAI_HOLIDAY";
   private static final String TAG_SEARCH_BLOCK = "MAHOUTSUKAI_SEARCH_BLOCK";
   private static final EntityDataAccessor<Float> COLOR_R = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_G = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLOR_B = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> HOLIDAY = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> GARDEN_TICKS = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
   private String searchBlock = null;
   public static final String entityName = "familiar_entity";
   private static final int CHUNK_TICKET_RADIUS = 1;
   @Nullable
   private ChunkPos ticketedChunk = null;
   BlockPos lastInteresting = null;

   public FamiliarEntity(Level worldIn) {
      this(ModEntities.FAMILIAR.get(),worldIn);
   }

   public FamiliarEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setPersistenceRequired();
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MTConfig.FAMILIAR_HEALTH);
      this.setHealth(this.getMaxHealth());
   }

   protected void registerGoals() {
      this.sitGoal = new SitWhenOrderedToGoal(this);
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
   }

   public static Builder registerAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, MTConfig.FAMILIAR_HEALTH)
         .add(Attributes.FLYING_SPEED, 0.8F)
         .add(Attributes.MOVEMENT_SPEED, 0.4F);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
      flyingpathnavigator.setCanOpenDoors(true);
      flyingpathnavigator.setCanFloat(true);
      flyingpathnavigator.setCanOpenDoors(true);
      return flyingpathnavigator;
   }

   public void aiStep() {
      super.aiStep();
      this.calculateFlapping();
      if (!this.level().isClientSide()) {
         this.updateMap();
         this.loadChunks();
         this.reportSurroundings();
         this.gardenUpdate();
      }
   }

   public void activateGarden() {
      this.setGardenTicks(MTConfig.FAMILIARS_GARDEN_TIME);
      this.acknowledge();
   }

   public void acknowledge() {
      if (this.getOwner() != null) {
         int z = this.getRandom().nextInt(4) + 1;
         MutableComponent s = Component.translatable("mahoutsukai.acknowledge" + z);
         if (this.getOwner() instanceof Player p) {
            if (MTConfig.FAMILIAR_ACTION_BAR_MESSAGES) {
               p.sendOverlayMessage(Component.literal(this.getMyName() + ": " + s.getString()).withStyle(ChatFormatting.AQUA));
            } else {
               p.sendSystemMessage(Component.literal(this.getMyName() + ": " + s.getString()).withStyle(ChatFormatting.AQUA));
            }
         }
      }
   }

   public void gardenUpdate() {
      int gardenTime = MTConfig.FAMILIARS_GARDEN_TIME;
      int tick = this.getGardenTicks();
      int startup = Math.max(1, Math.min(20, gardenTime / 2));
      float colorRatio = 1.0F;
      float gr = 232.0F;
      float gg = 86.0F;
      float gb = 186.0F;
      float nr = 91.0F;
      float ng = 209.0F;
      float nb = 252.0F;
      if (this.getHoliday()) {
         float[] fs = this.holidayToColor();
         nr = fs[0];
         ng = fs[1];
         nb = fs[2];
      }

      float red;
      float green;
      float blue;
      if (tick > 0) {
         if (tick < startup) {
            colorRatio = (float)tick / startup;
         } else if (tick > gardenTime - startup) {
            colorRatio = (float)(gardenTime - tick) / startup;
         }

         colorRatio = Mth.clamp(colorRatio, 0.0F, 1.0F);
         red = (gr - nr) * colorRatio + nr;
         green = (gg - ng) * colorRatio + ng;
         blue = (gb - nb) * colorRatio + nb;
         if (tick % 100 == 0) {
            this.gardenEffectA();
         }

         this.setGardenTicks(tick - 1);
      } else {
         red = nr;
         green = ng;
         blue = nb;
      }

      if (tick < 0) {
         this.setGardenTicks(0);
      }

      this.setColor(red / 255.0F, green / 255.0F, blue / 255.0F);
   }

   public void gardenEffectA() {
      int range = MTConfig.FAMILIARS_GARDEN_RANGE;
      List<LivingEntity> nearby = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range)
         );

      for (Player Player : this.level().players()) {
         if (Player.distanceToSqr(this.position()) < 16384.0) {
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send((ServerPlayer)Player, new GardenPacket(this.getX(), this.getY(), this.getZ()));
         }
      }

      for (LivingEntity entity : nearby) {
         EffectUtil.buff(entity, ModEffects.PEACEFUL_GARDEN, false, 120);
      }
   }

   public static void gardenEffectB(BlockPos pos, Level world) {
      int range = MTConfig.FAMILIARS_GARDEN_RANGE;
      Stream<BlockPos> lst = BlockPos.betweenClosedStream(pos.offset(range, range / 2, range), pos.offset(-range, -range / 2, -range));
      lst.forEach(p -> {
         if (Utils.isBlockAir(world, pos)) {
            float x = p.getX();
            float y = p.getY();
            float z = p.getZ();
            RandomSource random = world.getRandom();
            double d = pos.distSqr(p);
            double rat = d / ((double)range * range);
            if (rat > 1.0) {
               rat = 1.0;
            }

            float lessenPetals = 0.4F;
            if (random != null) {
               if (random.nextDouble() > rat && random.nextDouble() > lessenPetals) {
                  world.addParticle((ParticleOptions)ModParticles.PETAL.get(), x + 0.2, y + 0.2, z + 0.2, 0.0, 0.0, 0.0);
               }

               if (random.nextDouble() > rat && random.nextDouble() > lessenPetals) {
                  world.addParticle((ParticleOptions)ModParticles.PETAL.get(), x + 0.8, y + 0.2, z + 0.8, 0.0, 0.0, 0.0);
               }

               if (random.nextDouble() > rat && random.nextDouble() > lessenPetals) {
                  world.addParticle((ParticleOptions)ModParticles.PETAL.get(), x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
               }

               if (random.nextDouble() > rat && random.nextDouble() > lessenPetals) {
                  world.addParticle((ParticleOptions)ModParticles.PETAL.get(), x + 0.2, y + 0.8, z + 0.8, 0.0, 0.0, 0.0);
               }

               if (random.nextDouble() > rat && random.nextDouble() > lessenPetals) {
                  world.addParticle((ParticleOptions)ModParticles.PETAL.get(), x + 0.8, y + 0.8, z + 0.2, 0.0, 0.0, 0.0);
               }
            }
         }
      });
   }

   public int getGardenTicks() {
      return (Integer)this.entityData.get(GARDEN_TICKS);
   }

   public void setGardenTicks(int t) {
      this.entityData.set(GARDEN_TICKS, t);
   }

   public void toggleHoliday() {
      this.setHoliday(!this.getHoliday());
   }

   public void setHoliday(boolean h) {
      this.entityData.set(HOLIDAY, h);
   }

   public boolean getHoliday() {
      return (Boolean)this.entityData.get(HOLIDAY);
   }

   public void setSearchBlock(String s) {
      this.searchBlock = s;
   }

   public String getSearchBlock() {
      return this.searchBlock;
   }

   public float[] holidayToColor() {
      float[] ret = new float[3];
      if (this.whichHoliday() == 1) {
         ret[0] = 51.0F;
         ret[1] = 204.0F;
         ret[2] = 51.0F;
      } else if (this.whichHoliday() == 2) {
         ret[0] = 140.0F;
         ret[1] = 0.0F;
         ret[2] = 255.0F;
      } else if (this.whichHoliday() == 3) {
         ret[0] = 23.0F;
         ret[1] = 209.0F;
         ret[2] = 54.0F;
      } else if (this.whichHoliday() == 4) {
         ret[0] = 255.0F;
         ret[1] = 91.0F;
         ret[2] = 25.0F;
      } else {
         ret[0] = 91.0F;
         ret[1] = 209.0F;
         ret[2] = 252.0F;
      }

      return ret;
   }

   public int whichHoliday() {
      if (Calendar.getInstance().get(2) == 11) {
         return 1;
      } else if (Calendar.getInstance().get(2) == 1) {
         return 2;
      } else if (Calendar.getInstance().get(2) == 2) {
         return 3;
      } else {
         return Calendar.getInstance().get(2) == 9 ? 4 : 0;
      }
   }

   public void updateMap() {
      if (this.getOwnerReference() != null) {
         UUID owner = this.getOwnerReference().getUUID();
         if (SummonFamiliarSpellEffect.familiarMap.containsKey(owner)) {
            FamiliarEntity e = SummonFamiliarSpellEffect.familiarMap.get(owner);
            if (e == null || e.getFamiliarId() == null || !e.getFamiliarId().equals(this.getFamiliarId())) {
               this.discard();
            } else if (!e.isAlive()) {
               SummonFamiliarSpellEffect.familiarMap.put(owner, this);
            } else if (e != this) {
               this.discard();
            }
         } else {
            this.discard();
         }
      }
   }

   public void reportSurroundings() {
      if ((this.level().getGameTime() % 50000L) % MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME == 0L && !this.level().isClientSide()) {
         Player owner = (Player)this.getOwner();
         if (owner != null) {
            org.slf4j.LoggerFactory.getLogger("mahoutsukai")
               .info("[FamiliarDebug] Report cycle familiar {} owner {} at {}", this.getId(), owner.getName().getString(), this.blockPosition());
            AABB aabb = new AABB(this.blockPosition().offset(-5, -5, -5).getCenter(), this.blockPosition().offset(5, 5, 5).getCenter());
            MutableComponent toSend = null;
            RandomSource rand = owner.getRandom();
            if (rand.nextBoolean()) {
               List<Player> players = this.level().getEntitiesOfClass(Player.class, aabb);
               if (!players.isEmpty()) {
                  toSend = this.reportPlayer(players.get(rand.nextInt(players.size())), owner);
                  org.slf4j.LoggerFactory.getLogger("mahoutsukai").info("[FamiliarDebug] Player branch candidates={} producedMessage={}", players.size(), toSend != null);
               }
            }

            if (toSend == null) {
               if (rand.nextBoolean()) {
                  List<LivingEntity> things = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
                  if (!things.isEmpty()) {
                     toSend = this.reportEntity(things.get(rand.nextInt(things.size())), owner);
                     org.slf4j.LoggerFactory.getLogger("mahoutsukai").info("[FamiliarDebug] Entity branch candidates={} producedMessage={}", things.size(), toSend != null);
                  }
               }

               if (toSend == null) {
                  List<BlockPos> blocks = new ArrayList<>();
                  Stream<BlockPos> lst = BlockPos.betweenClosedStream(
                     (int)this.getX() - 5, (int)this.getY() - 5, (int)this.getZ() - 5, (int)this.getX() + 5, (int)this.getY() + 5, (int)this.getZ() + 5
                  );
                  lst.forEach(tmp -> {
                     BlockState bt = this.level().getBlockState(tmp);
                     if (this.getSearchBlock() == null) {
                        if (isInterestingBlock(bt)) {
                           blocks.add(new BlockPos(tmp.getX(), tmp.getY(), tmp.getZ()));
                        }
                     } else if (Utils.getRegistryName(bt.getBlock(), this.level()).equals(this.getSearchBlock())) {
                        blocks.add(new BlockPos(tmp.getX(), tmp.getY(), tmp.getZ()));
                     }
                  });
                  if (!blocks.isEmpty()) {
                     this.lastInteresting = blocks.get(rand.nextInt(blocks.size()));
                     org.slf4j.LoggerFactory.getLogger("mahoutsukai")
                        .info(
                           "[FamiliarDebug] Block branch candidates={} selected={} block={}",
                           blocks.size(),
                           this.lastInteresting,
                           this.level().getBlockState(this.lastInteresting).getBlock()
                        );
                     if (this.getOwner() instanceof Player) {
                        IMahou mahou = Utils.getPlayerMahou((Player)this.getOwner());
                        if (mahou != null) {
                           mahou.setFamiliarInterestingBlock(this.lastInteresting);
                           EffectUtil.buff(this.getOwner(), ModEffects.INTERESTING_BLOCK, false, MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME, false);
                           PlayerManaManager.updateClientMahou((ServerPlayer)this.getOwner(), mahou);
                           org.slf4j.LoggerFactory.getLogger("mahoutsukai")
                              .info(
                                 "[FamiliarDebug] Sent interesting block {} and effect duration {} to owner {}",
                                 this.lastInteresting,
                                 MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME,
                                 owner.getName().getString()
                              );
                        }
                     }

                     toSend = this.reportBlock(this.level().getBlockState(this.lastInteresting).getBlock(), owner);
                  }
               }
            }

            if (toSend != null && !toSend.getString().equals("")) {
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").info("[FamiliarDebug] Sending familiar message to {}: {}", owner.getName().getString(), toSend.getString());
               if (MTConfig.FAMILIAR_ACTION_BAR_MESSAGES) {
                  owner.sendOverlayMessage(Component.literal(this.getMyName() + ": " + toSend.getString()).withStyle(ChatFormatting.AQUA));
               } else {
                  owner.sendSystemMessage(Component.literal(this.getMyName() + ": " + toSend.getString()).withStyle(ChatFormatting.AQUA));
               }
            } else {
               org.slf4j.LoggerFactory.getLogger("mahoutsukai").info("[FamiliarDebug] Report cycle produced no message");
            }
         } else {
            org.slf4j.LoggerFactory.getLogger("mahoutsukai")
               .info("[FamiliarDebug] Report cycle familiar {} has no resolved owner; owner reference={}", this.getId(), this.getOwnerReference());
         }
      }
   }

   public String getMyName() {
      return this.hasCustomName() ? this.getCustomName().getString() : "Familiar";
   }

   public static boolean isInterestingBlock(BlockState state) {
      return state.is(BlockTags.DIAMOND_ORES)
         || state.is(BlockTags.EMERALD_ORES)
         || state.is(BlockTags.LAPIS_ORES)
         || state.is(BlockTags.GOLD_ORES)
         || state.is(Blocks.BOOKSHELF)
         || state.is(Blocks.OBSIDIAN)
         || state.is(Blocks.PRISMARINE)
         || state.is(Blocks.END_PORTAL_FRAME)
         || state.is(Blocks.ANCIENT_DEBRIS);
   }

   public MutableComponent ouch(Player owner) {
      RandomSource rand = owner.getRandom();
      int textNum = 1 + rand.nextInt(7);
      return Component.translatable("mahoutsukai.hurt" + textNum);
   }

   public MutableComponent getGreeting(Player owner) {
      MutableComponent tct = Component.translatable("");
      RandomSource rand = owner.getRandom();
      int textNum = rand.nextInt(7);
      if (textNum > 0) {
         if (this.getHoliday() && this.whichHoliday() != 0) {
            tct.append(Component.translatable("mahoutsukai.holiday" + this.whichHoliday() + "greeting" + textNum));
         } else {
            tct.append(Component.translatable("mahoutsukai.greeting" + textNum));
         }

         tct.append(Component.literal(" "));
      }

      tct.append(Component.translatable("mahoutsukai.masta"));
      tct.append(Component.literal(", "));
      return tct;
   }

   public MutableComponent reportBlock(Block thing, Player owner) {
      MutableComponent tct = null;
      RandomSource rand = owner.getRandom();
      if (thing != null) {
         tct = this.getGreeting(owner);
         int textNum = 1 + rand.nextInt(3);
         tct.append(Component.translatable("mahoutsukai.found" + textNum));
         tct.append(Component.literal(" "));
         tct.append(Component.translatable(thing.getDescriptionId())).getString();
         tct.append(Component.literal(" "));
         tct.append(Component.translatable("mahoutsukai.foundF" + textNum));
         tct.append(Component.literal(" "));
         textNum = 1 + rand.nextInt(4);
         tct.append(Component.translatable("mahoutsukai.followup" + textNum));
      }

      return tct;
   }

   public MutableComponent reportEntity(LivingEntity thing, Player owner) {
      MutableComponent tct = null;
      RandomSource rand = owner.getRandom();
      if (thing != null && !(thing instanceof Player) && thing != this) {
         tct = this.getGreeting(owner);
         int textNum = 1 + rand.nextInt(3);
         tct.append(Component.translatable("mahoutsukai.entityFound" + textNum));
         tct.append(Component.literal(" "));
         tct.append(Component.translatable(thing.getName().getString()));
         tct.append(Component.literal(" "));
         tct.append(Component.translatable("mahoutsukai.entityFoundF" + textNum));
         tct.append(Component.literal(" "));
         textNum = 1 + rand.nextInt(7);
         tct.append(Component.translatable("mahoutsukai.entityFoundFollowUp" + textNum));
      }

      return tct;
   }

   public MutableComponent reportPlayer(Player player, Player owner) {
      MutableComponent tct = null;
      RandomSource rand = owner.getRandom();
      if (player != null && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getOwner().getUUID(), player)) {
         tct = this.getGreeting(owner);
         BlockPos position = player.blockPosition();
         int textNum = 1 + rand.nextInt(3);
         tct.append(Component.translatable("mahoutsukai.playerFound" + textNum));
         tct.append(Component.literal(" "));
         tct.append(Component.translatable(player.getName().getString()));
         tct.append(Component.translatable("mahoutsukai.playerFoundF" + textNum));
         tct.append(Component.literal(" "));
         tct.append(Component.literal("[" + position.getX() + "," + position.getY() + "," + position.getZ() + "]. "));
         textNum = 1 + rand.nextInt(7);
         tct.append(Component.translatable("mahoutsukai.entityFoundFollowUp" + textNum));
      }

      return tct;
   }

   public void loadChunks() {
      if (this.level() instanceof ServerLevel serverLevel) {
         if (!this.isRemoved() && MTConfig.FAMILIAR_CHUNKLOADS && this.getOwnerReference() != null) {
            ChunkPos currentChunk = this.chunkPosition();
            if (!currentChunk.equals(this.ticketedChunk)) {
               this.releaseChunks();
               serverLevel.getChunkSource().addTicketWithRadius((TicketType)ModTickets.FAMILIAR_TICKET.get(), currentChunk, 1);
               this.ticketedChunk = currentChunk;
            }
         } else {
            this.releaseChunks();
         }
      }
   }

   public ChunkPos add(int x, int z, ChunkPos pos) {
      return new ChunkPos(pos.x() + x, pos.z() + z);
   }

   private void releaseChunks() {
      if (this.ticketedChunk != null && this.level() instanceof ServerLevel serverLevel) {
         serverLevel.getChunkSource().removeTicketWithRadius((TicketType)ModTickets.FAMILIAR_TICKET.get(), this.ticketedChunk, 1);
         this.ticketedChunk = null;
      }
   }

   public void remove(RemovalReason reason) {
      this.releaseChunks();
      super.remove(reason);
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)(this.flapSpeed + (this.onGround() ? -1 : 4) * 0.3);
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround() && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)(this.flapping * 0.9);
      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         Vec3 motion = this.getDeltaMovement();
         this.setDeltaMovement(motion.x, motion.y * 0.6, motion.z);
      }

      this.flap = this.flap + this.flapping * 2.0F;
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   public boolean causeFallDamage(double distance, float damageMultiplier, DamageSource source) {
      return false;
   }

   protected void checkFallDamage(double y, boolean groundIn, BlockState state, BlockPos pos) {
   }

   public boolean canMate(Animal otherAnimal) {
      return false;
   }

   public boolean isPushable() {
      return true;
   }

   protected void doPush(Entity entityIn) {
      if (!(entityIn instanceof Player)) {
         super.doPush(entityIn);
      }
   }

   protected void actuallyHurt(ServerLevel level, DamageSource source, float dmg) {
      if (!this.isInvulnerable()) {
         if (this.getOwner() != null
            && !this.level().isClientSide()
            && this.getRandom().nextDouble() < MTConfig.FAMILIAR_OUCH_CHANCE
            && this.getOwner() instanceof Player p) {
            p.sendOverlayMessage(Component.literal(this.getMyName() + ": " + this.ouch(p).withStyle(ChatFormatting.AQUA).getString()));
         }

         super.actuallyHurt(level, source, dmg);
      }
   }

   public UUID getFamiliarId() {
      return this.familiarId;
   }

   public void setFamiliarId(UUID f) {
      this.familiarId = f;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      builder.define(COLOR_R, 1.0F);
      builder.define(COLOR_G, 1.0F);
      builder.define(COLOR_B, 1.0F);
      builder.define(GARDEN_TICKS, 0);
      builder.define(HOLIDAY, true);
      super.defineSynchedData(builder);
   }

   public void setColor(float r, float g, float b) {
      this.entityData.set(COLOR_R, r);
      this.entityData.set(COLOR_G, g);
      this.entityData.set(COLOR_B, b);
   }

   public float[] getColor() {
      return new float[]{(Float)this.entityData.get(COLOR_R), (Float)this.entityData.get(COLOR_G), (Float)this.entityData.get(COLOR_B)};
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      if (this.familiarId != null) {
         output.putString("mahoutsukai_familiar_id", this.familiarId.toString());
      }

      float[] colors = this.getColor();
      output.putFloat("MAHOUTSUKAI_COLOR_R", colors[0]);
      output.putFloat("MAHOUTSUKAI_COLOR_G", colors[1]);
      output.putFloat("MAHOUTSUKAI_COLOR_B", colors[2]);
      output.putInt("MAHOUTSUKAI_GARDEN_TICKS", this.getGardenTicks());
      output.putBoolean("MAHOUTSUKAI_HOLIDAY", this.getHoliday());
      if (this.searchBlock != null) {
         output.putString("MAHOUTSUKAI_SEARCH_BLOCK", this.searchBlock);
      }
   }

   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      input.getString("mahoutsukai_familiar_id").ifPresent(s -> this.familiarId = UUID.fromString(s));
      this.setColor(input.getFloatOr("MAHOUTSUKAI_COLOR_R", 1.0F), input.getFloatOr("MAHOUTSUKAI_COLOR_G", 1.0F), input.getFloatOr("MAHOUTSUKAI_COLOR_B", 1.0F));
      this.setGardenTicks(input.getIntOr("MAHOUTSUKAI_GARDEN_TICKS", 0));
      this.setHoliday(input.getBooleanOr("MAHOUTSUKAI_HOLIDAY", true));
      this.searchBlock = (String)input.getString("MAHOUTSUKAI_SEARCH_BLOCK").orElse(null);
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageableEntity) {
      return null;
   }

   public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason spawnReason) {
      return false;
   }
}
