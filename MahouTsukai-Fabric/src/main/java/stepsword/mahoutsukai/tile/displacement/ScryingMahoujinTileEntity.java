package stepsword.mahoutsukai.tile.displacement;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.block.spells.displacement.ScryingMahoujin;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.kodoku.KodokuItem;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.DataManagerPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.ScryingEntityPacket;
import stepsword.mahoutsukai.potion.MisfortunePotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.Utils;

public class ScryingMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   public static java.util.function.Function<ScryingMahoujinTileEntity,LivingEntity> clientPlayer;
   public String viewTargetName(){return viewTarget;}
   public UUID targetUUID(){return scryUniqueID;}
   public LivingEntity scryTarget = null;
   public CompoundTag scryNBT = null;
   private float yaw = 0.0F;
   private float hyaw = 0.0F;
   private float ryaw = 0.0F;
   private float pitch = 0.0F;
   private float pyaw = 0.0F;
   private float phyaw = 0.0F;
   private float pryaw = 0.0F;
   private float ppitch = 0.0F;
   private double velx = 0.0;
   private double vely = 0.0;
   private double velz = 0.0;
   public Vec3 targetPos = null;
   public float prevSwingProgress = 0.0F;
   public float swingProgress = 0.0F;
   private UUID scryUniqueID = null;
   public float limbSwingAmount = 0.0F;
   public float prevLimbSwingAmount = 0.0F;
   public float limbSwing = 0.0F;
   public ArrayList<ScryingMahoujinTileEntity.BlockPosInfo> lst = null;
   public List<DataValue<?>> scryDataManager = null;
   private int scryTimer = 0;
   private boolean refreshTargetOnLoad = false;
   private String viewTarget = "";
   private String VIEW_TARGET = "MAHOUTSUKAI_VIEW_TARGET";
   private String SCRY_ENTITY = "MAHOUTSUKAI_SCRY_ENTITY";
   private String SCRY_RENDER_YAW = "MAHOUTSUKAI_RENDER_YAW";
   private String SCRY_YAW = "MAHOUTSUKAI_YAW";
   private String SCRY_HEAD_YAW = "MAHOUTSUKAI_HYAW";
   private String SCRY_PITCH = "MAHOUTSUKAI_PITCH";
   private String SCRY_VEL_X = "MAHOUTSUKAI_VEL_X";
   private String SCRY_VEL_Y = "MAHOUTSUKAI_VEL_Y";
   private String SCRY_VEL_Z = "MAHOUTSUKAI_VEL_Z";
   private String SCRY_UNIQ = "MAHOUTSUKAI_SCRY_UNIQ";
   private String LIMB_SWING = "MAHOUTSUKAI_LS";
   private String LIMB_SWING_AMOUNT = "MAHOUTSUKAI_LSA";
   private String LIMB_SWING_PROGRESS = "MAHOUTSUKAI_LSP";
   private String SCRY_LOC_X = "MAHOUTSUKAI_LOC_X";
   private String SCRY_LOC_Y = "MAHOUTSUKAI_LOC_Y";
   private String SCRY_LOC_Z = "MAHOUTSUKAI_LOC_Z";
   private String SCRY_BLOCK = "MAHOUTSUKAI_BLOCK_";
   public boolean doRead = false;
   public CompoundTag last_nbt = null;

   public ScryingMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.scrying.get(), p, bs);
   }

   public LivingEntity getScryTarget() {
      return this.scryTarget;
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      output.putInt("MAHOUTSUKAI_SCRY_TIMER",this.scryTimer);
      output.putString(this.VIEW_TARGET, this.viewTarget);
      if (this.scryTarget != null) {
         CompoundTag savedTarget = this.writeScryTarget(this.scryTarget);
         if (savedTarget != null) {
            this.scryNBT = savedTarget;
         }

         output.putFloat(this.SCRY_PITCH, this.scryTarget.getXRot());
         output.putFloat(this.SCRY_YAW, this.scryTarget.getYRot());
         output.putFloat(this.SCRY_HEAD_YAW, this.scryTarget.yHeadRot);
         output.putFloat(this.SCRY_RENDER_YAW, this.scryTarget.yBodyRot);
         output.putFloat(this.SCRY_RENDER_YAW, this.scryTarget.yBodyRot);
         output.putFloat(this.LIMB_SWING, this.scryTarget.walkAnimation.position());
         output.putFloat(this.LIMB_SWING_AMOUNT, this.scryTarget.walkAnimation.speed());
         output.putFloat(this.LIMB_SWING_PROGRESS, this.scryTarget.attackAnim);
         Vec3 epos = this.scryTarget.position();
         Vec3 emot = this.scryTarget.getDeltaMovement();
         output.putDouble(this.SCRY_VEL_X, emot.x);
         output.putDouble(this.SCRY_VEL_Y, emot.y);
         output.putDouble(this.SCRY_VEL_Z, emot.z);
         output.putDouble(this.SCRY_LOC_X, epos.x);
         output.putDouble(this.SCRY_LOC_Y, epos.y);
         output.putDouble(this.SCRY_LOC_Z, epos.z);
         output.store(this.SCRY_UNIQ, UUIDUtil.CODEC, this.scryTarget.getUUID());
         int index = 0;

         for (int i = (int)Math.floor(epos.x - 1.0); i <= epos.x + 1.0; i++) {
            for (int j = (int)Math.floor(epos.z - 1.0); j <= epos.z + 1.0; j++) {
               for (int k = (int)Math.floor(epos.y - 1.0); k <= epos.y + 2.0; k++) {
                  BlockPos p = new BlockPos(i, k, j);
                  ScryingMahoujinTileEntity.BlockPosInfo info = new ScryingMahoujinTileEntity.BlockPosInfo(p, this.level);
                  CompoundTag infonbt = info.write();
                  output.store(this.SCRY_BLOCK + index, CompoundTag.CODEC, infonbt);
                  index++;
               }
            }
         }
      }

      if (this.scryNBT != null) {
         output.store(this.SCRY_ENTITY, CompoundTag.CODEC, this.scryNBT);
      }

      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.scryTimer=input.getIntOr("MAHOUTSUKAI_SCRY_TIMER",0);
      this.viewTarget = input.getStringOr(this.VIEW_TARGET, "");
      this.refreshTargetOnLoad = !this.viewTarget.isEmpty();
      this.yaw = input.getFloatOr(this.SCRY_YAW, this.yaw);
      this.pitch = input.getFloatOr(this.SCRY_PITCH, this.pitch);
      this.hyaw = input.getFloatOr(this.SCRY_HEAD_YAW, this.hyaw);
      this.ryaw = input.getFloatOr(this.SCRY_RENDER_YAW, this.ryaw);
      this.limbSwing = input.getFloatOr(this.LIMB_SWING, this.limbSwing);
      this.limbSwingAmount = input.getFloatOr(this.LIMB_SWING_AMOUNT, this.limbSwingAmount);
      this.swingProgress = input.getFloatOr(this.LIMB_SWING_PROGRESS, this.swingProgress);
      this.velx = input.getDoubleOr(this.SCRY_VEL_X, this.velx);
      this.vely = input.getDoubleOr(this.SCRY_VEL_Y, this.vely);
      this.velz = input.getDoubleOr(this.SCRY_VEL_Z, this.velz);
      this.scryUniqueID = input.read(this.SCRY_UNIQ, UUIDUtil.CODEC).orElse(this.scryUniqueID);
      int index = 0;
      this.lst = new ArrayList<>();

      while (true) {
         Optional<CompoundTag> infoNbt = input.read(this.SCRY_BLOCK + index, CompoundTag.CODEC);
         if (infoNbt.isEmpty()) {
            var targetX = input.read(this.SCRY_LOC_X, Codec.DOUBLE);
            Optional<Double> targetY = input.read(this.SCRY_LOC_Y, Codec.DOUBLE);
            Optional<Double> targetZ = input.read(this.SCRY_LOC_Z, Codec.DOUBLE);
            if (targetX.isPresent() && targetY.isPresent() && targetZ.isPresent()) {
               this.targetPos = new Vec3(targetX.get(), targetY.get(), targetZ.get());
            }

            this.scryNBT = (CompoundTag)input.read(this.SCRY_ENTITY, CompoundTag.CODEC).orElse(null);
            this.doRead = true;
            super.loadAdditional(input);
            return;
         }

         ScryingMahoujinTileEntity.BlockPosInfo info = new ScryingMahoujinTileEntity.BlockPosInfo();
         info.read(infoNbt.get(), this.level);
         this.lst.add(info);
         index++;
      }
   }

   @Nullable
   private CompoundTag writeScryTarget(Entity target) {
      TagValueOutput output = this.level != null
         ? TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level.registryAccess())
         : TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
      if (target instanceof Player) {
         output.putString("id", "minecraft:player");
         target.saveWithoutId(output);
         return output.buildResult();
      } else {
         return target.saveAsPassenger(output) ? output.buildResult() : null;
      }
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (!level.isClientSide()) {
         if (this.refreshTargetOnLoad) {
            this.refreshTargetOnLoad = false;
            this.refreshScryTarget();
            this.sendUpdates();
         }

         if (this.scryTimer >= MTConfig.SCRYING_DURATION && this.scryTarget!=null) {
            this.viewTarget = "";
            this.scryTarget = null;
            this.scryNBT = null;
            this.targetPos = null;
            this.sendUpdates();
         }

         if (this.scryTarget != null && this.scryTarget.isAlive()) {
            this.targetPos = this.scryTarget.position();
            boolean checkDirty = this.scryDataManager != null;
            List<DataValue<?>> entries = this.getAll(this.scryTarget.getEntityData(), checkDirty);
            if (entries != null) {
               List<DataValue<?>> entriesCopy = new ArrayList<>(entries);
               List<DataValue<?>> toSend = new ArrayList<>(entriesCopy);
               this.scryDataManager = entriesCopy;
               if (!toSend.isEmpty()) {
                  int r = 32;
                  AABB aabb = new AABB(
                     this.worldPosition.getX() - r,
                     this.worldPosition.getY() - r,
                     this.worldPosition.getZ() - r,
                     this.worldPosition.getX() + r,
                     this.worldPosition.getY() + r,
                     this.worldPosition.getZ() + r
                  );

                  for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb)) {
                     PacketHandler.sendTo(player, new DataManagerPacket(this.worldPosition, toSend));
                  }
               }
            }

            CompoundTag savedTarget = this.writeScryTarget(this.scryTarget);
            if (savedTarget != null) {
               this.scryNBT = savedTarget;
            }

            this.sendUpdates();
         }

         this.scryTimer++;
      }
   }

   @Override
   public void mahoujinClientTick(Level level, BlockPos pos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (this.scryNBT == null) {
         this.scryTarget = null;
         this.targetPos = null;
      }

      if (this.doRead && this.scryNBT != null) {
         String entityId = this.scryNBT.getStringOr("id", "");
         EntityType<?> zz = entityId.isEmpty() ? null : (EntityType)BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(entityId));
         if (this.scryNBT != null && zz != null && zz == EntityType.PLAYER) {
            this.scryTarget = clientPlayer==null?null:clientPlayer.apply(this);
         } else if (this.scryNBT != null && !this.scryNBT.isEmpty()) {
            try {
               if (EntityType.loadEntityRecursive(this.scryNBT, level, EntitySpawnReason.LOAD, loadedEntity -> loadedEntity) instanceof LivingEntity livingEntity
                  )
                {
                  this.scryTarget = livingEntity;
               }
            } catch (Exception var10) {
            }
         }

         if (this.scryTarget != null && this.targetPos == null) {
            this.targetPos = this.scryTarget.position();
         }

         this.doRead = false;
      }

      if (this.scryTarget != null) {
         if (this.targetPos == null) {
            return;
         }

         this.scryTarget.yHeadRot = this.hyaw;
         this.scryTarget.yHeadRotO = this.phyaw;
         this.scryTarget.setYRot(this.yaw);
         this.scryTarget.yRotO = this.pyaw;
         this.scryTarget.setXRot(this.pitch);
         this.scryTarget.xRotO = this.ppitch;
         this.scryTarget.yBodyRot = this.ryaw;
         this.scryTarget.yBodyRotO = this.pryaw;
         this.scryTarget.setDeltaMovement(new Vec3(this.velx, this.vely, this.velz));
         this.scryTarget.walkAnimation.position = this.limbSwing;
         this.scryTarget.walkAnimation.setSpeed(this.limbSwingAmount);
         this.scryTarget.attackAnim = this.swingProgress;
         this.scryTarget.oAttackAnim = this.prevSwingProgress;
         this.scryTarget.setPos(this.targetPos.x, this.targetPos.y, this.targetPos.z);
         this.prevSwingProgress = this.scryTarget.attackAnim;
         this.pyaw = this.scryTarget.getYRot();
         this.pryaw = this.scryTarget.yBodyRot;
         this.ppitch = this.scryTarget.getXRot();
         this.phyaw = this.scryTarget.yHeadRot;
         if (this.scryDataManager != null) {
            try {
               this.scryTarget.getEntityData().assignValues(this.scryDataManager);
            } catch (Exception var9) {
            }
         }
      }
   }

   @Override
   public List<Item> checkForItem() {
      return Arrays.asList(
         Items.NAME_TAG,
         Items.BLAZE_POWDER,
         Items.ROTTEN_FLESH,
         Items.RABBIT_FOOT,
         (Item)ModItems.REGISTERED.get("kodoku"),
         Items.GLOWSTONE_DUST,
         Items.ENDER_PEARL,
         Items.NETHER_WART
      );
   }

   @Override
   public List<Class<? extends Item>> checkForItemClass() {
      return Arrays.asList(SplashPotionItem.class);
   }

   @Override
   public int checkTicks() {
      return MTConfig.SCRYING_BLOCK_CYCLE;
   }

   @Override
   public boolean preConditions() {
      return ActiveCircleConfig.tryToOperate(this, this.getCasterUUID());
   }

   @Override
   public boolean firstEntityOnly() {
      return true;
   }

   @Override
   public boolean ifItemFoundAbove(ItemEntity item) {
      boolean changedName = this.nameTag(item);
      this.sympathetic(item);
      if (this.scryTarget == null || !this.scryTarget.isAlive() || changedName || this.doRead) {
         if (this.viewTarget != null && !this.viewTarget.equals("")) {
            this.refreshScryTarget();
            this.sendUpdates();
         }

         if (this.scryTarget != null && !this.scryTarget.isAlive()) {
            this.scryTarget = null;
            this.sendUpdates();
         }

         this.doRead = false;
      }

      if (changedName && this.scryTarget != null) {
         item.getItem().shrink(1);
      }

      return false;
   }

   private void refreshScryTarget() {
      this.scryTarget = null;
      this.scryNBT = null;
      this.targetPos = null;
      if (this.viewTarget != null && !this.viewTarget.isEmpty() && this.level != null) {
         LivingEntity playerTarget = getPlayerByName(this.viewTarget, this.level);
         if (playerTarget != null) {
            this.scryTarget = playerTarget;
            this.targetPos = playerTarget.position();
         } else if (MTConfig.SCRY_NON_PLAYERS && this.level instanceof ServerLevel serverLevel) {
            var loaded = serverLevel.getAllEntities();
            if (loaded != null) {
               for (Entity entity : loaded) {
                  if (entity instanceof LivingEntity livingEntity
                     && entity.getCustomName() != null
                     && entity.getCustomName().getString().equals(this.viewTarget)) {
                     this.scryTarget = livingEntity;
                     this.targetPos = livingEntity.position();
                     return;
                  }
               }

               for (Entity entity : loaded) {
                  if (entity instanceof LivingEntity livingEntity && entity.getDisplayName().getString().equals(this.viewTarget)) {
                     this.scryTarget = livingEntity;
                     this.targetPos = livingEntity.position();
                     return;
                  }
               }
            }
         }
      }
   }

   public List<DataValue<?>> getAll(SynchedEntityData data, boolean checkDirty) {
      List<DataValue<?>> list = null;
      if (true) {
         for (DataItem<?> dataitem : data.itemsById) {
            if (true) {

               if (list == null) {
                  list = new ArrayList<>();
               }

               list.add(dataitem.value());
            }
         }
      }

      return list;
   }

   public static Player getPlayerByName(String name, Level world) {
      Player ret = null;
      if (world != null) {
         for (Player p : world.players()) {
            if (p.getName().getString().equals(name)) {
               ret = p;
            }
         }
      }

      return ret;
   }

   public boolean nameTag(ItemEntity item) {
      Player caster = this.getCaster();
      boolean ret = false;
      if (item.getItem().getItem() == Items.NAME_TAG) {
         boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
            this.worldPosition, this.level, this.getCasterUUID(), MTConfig.SCRYING_MANA_COST
         );
         if (nearbyBatteriesGaveMana
            || caster != null && PlayerManaManager.drainMana(caster, MTConfig.SCRYING_MANA_COST, false, false) == MTConfig.SCRYING_MANA_COST) {
            String s = item.getItem().getHoverName().getString();
            if (s != null) {
               this.viewTarget = s;
            } else {
               this.viewTarget = "";
            }

            this.scryTimer = 0;
            ret = true;
            this.sendUpdates();
         }
      }

      return ret;
   }

   public void sympathetic(ItemEntity e) {
      double firechance = MTConfig.KODOKU_FIRE_CHANCE;
      double splashchance = MTConfig.KODOKU_SPLASH_CHANCE;
      double hungerchance = MTConfig.KODOKU_HUNGER_CHANCE;
      double hopchance = MTConfig.KODOKU_HOP_CHANCE;
      double glowchance = MTConfig.KODOKU_GLOW_CHANCE;
      double telechance = MTConfig.KODOKU_TELEPORT_CHANCE;
      double confusechance = MTConfig.KODOKU_CONFUSE_CHANCE;
      if (this.scryTarget != null && this.scryTarget.isAlive() && this.level != null) {
         BlockEntity down = this.level.getBlockEntity(this.worldPosition.relative(Direction.DOWN));
         if (down != null) {
            var inventory = net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(this.level,this.worldPosition.below(),Direction.NORTH);
            if (inventory != null) {
               int kodoku = 0;
               int targetkodoku = MisfortunePotion.getMaxKodoku(this.scryTarget);
               ItemStack s = null;
               net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> kodokuSlot=null;
               for(var view:inventory){if(view.isResourceBlank() || view.getAmount()<1)continue;s=view.getResource().toStack((int)Math.min(view.getAmount(),Integer.MAX_VALUE));if(s.getItem() instanceof KodokuItem){kodoku=KodokuItem.getKodoku(s);kodokuSlot=view;break;}}
               LivingEntity realTarget = this.scryTarget;
               if (targetkodoku > kodoku * 2) {
                  realTarget = this.getCaster();
               } else {
                  kodoku -= targetkodoku;
               }

               if (kodoku > 0 && s != null) {
                  boolean success = false;
                  boolean consume = false;
                  boolean actualconsume = false;
                  if (this.getCaster() != null) {
                     if (e.getItem().getItem() == Items.BLAZE_POWDER) {
                        if (this.getCaster().getRandom().nextDouble() < firechance * Math.sqrt(kodoku)) {
                           realTarget.igniteForSeconds(7.0F);
                           success = true;
                        }

                        consume = true;
                        actualconsume = true;
                     } else if (e.getItem().getItem() instanceof SplashPotionItem) {
                        if (this.getCaster().getRandom().nextDouble() < splashchance * Math.sqrt(kodoku)) {
                           ItemStack pot = e.getItem().copyWithCount(1);
                           ThrownSplashPotion entitypotion = new ThrownSplashPotion(this.level, realTarget.getX(), realTarget.getY(), realTarget.getZ(), pot);
                           this.level.addFreshEntity(entitypotion);
                           success = true;
                        }

                        consume = true;
                        actualconsume = true;
                     } else if (e.getItem().getItem() == Items.ROTTEN_FLESH) {
                        if (realTarget instanceof Player) {
                           if (this.getCaster().getRandom().nextDouble() < hungerchance * Math.sqrt(kodoku)) {
                              ((Player)realTarget).getFoodData().setFoodLevel(Math.max(((Player)realTarget).getFoodData().getFoodLevel() - 5, 0));
                              success = true;
                           }

                           consume = true;
                           actualconsume = true;
                        }
                     } else if (e.getItem().getItem() == Items.RABBIT_FOOT) {
                        if (this.getCaster().getRandom().nextDouble() < hopchance * Math.sqrt(kodoku)) {
                           realTarget.setDeltaMovement(realTarget.getDeltaMovement().x, 1.1F, realTarget.getDeltaMovement().z);
                           realTarget.hurtMarked = true;
                           realTarget.needsSync = true;
                           success = true;
                        }

                        actualconsume = true;
                        consume = true;
                     } else if (e.getItem().getItem() == ModItems.REGISTERED.get("kodoku")) {
                        if (KodokuItem.getKodoku(e.getItem()) > 0) {
                           EffectUtil.buff(realTarget, ModEffects.MISFORTUNE, true, 600, KodokuItem.getKodoku(e.getItem()));
                           success = true;
                        }

                        consume = true;
                        actualconsume = true;
                     } else if (e.getItem().getItem() == Items.GLOWSTONE_DUST) {
                        if (this.getCaster().getRandom().nextDouble() < glowchance * Math.sqrt(kodoku)) {
                           EffectUtil.buff(realTarget, MobEffects.GLOWING, true, 600);
                           success = true;
                        }

                        actualconsume = true;
                        consume = true;
                     } else if (e.getItem().getItem() != Items.ENDER_PEARL) {
                        if (e.getItem().getItem() == Items.NETHER_WART) {
                           if (this.getCaster().getRandom().nextDouble() < confusechance * Math.sqrt(kodoku)) {
                              EffectUtil.buff(realTarget, ModEffects.CONFUSION, true, 240);
                              success = true;
                           }

                           consume = true;
                           actualconsume = true;
                        }
                     } else {
                        if (this.getCaster().getRandom().nextDouble() < telechance * Math.sqrt(kodoku)) {
                           if (realTarget instanceof Player
                              && !ContractMahoujinTileEntity.isImmuneToSpell(realTarget.level(), this.getCasterUUID(), realTarget)) {
                              this.getCaster().sendOverlayMessage(Component.translatable("mahoutsukai.teleport_kodoku.failed"));
                              success = true;
                           } else {
                              MahouTsukaiTeleporter.teleport(
                                 realTarget,
                                 this.worldPosition.getX() + 0.5,
                                 this.worldPosition.getY(),
                                 this.worldPosition.getZ() + 0.5,
                                 EffectUtil.getDimension(this.getCaster().level())
                              );
                              success = true;
                           }
                        }

                        actualconsume = true;
                        consume = true;
                     }

                     if (!success && consume) {
                        this.getCaster().sendOverlayMessage(Component.translatable("mahoutsukai.kodoku.failed"));
                     }

                     if (success) {
                        ModTriggers.KODOKU.trigger((ServerPlayer)this.getCaster());
                     }

                     if (actualconsume && extract(kodokuSlot)) {
                        e.getItem().shrink(1);
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      if (this.scryTarget != null) {
         CompoundTag nbt = this.writeScryTarget(this.scryTarget);
         if (nbt == null) {
            nbt = new CompoundTag();
            EntityType<?> entitytype = this.scryTarget.getType();
            Identifier Identifier = EntityType.getKey(entitytype);
            nbt.putString("id", Identifier.toString());
         }

         Set<String> toRemove = new HashSet<>();

         for (String key : nbt.keySet()) {
            Tag zz = nbt.get(key);
            if (zz != null && zz.toString().length() > 15000) {
               toRemove.add(key);
            }

            if (key.equals("ForgeCaps")) {
               toRemove.add(key);
            }
         }

         for (String key : toRemove) {
            nbt.remove(key);
         }

         if (!nbt.equals(this.last_nbt)) {
            this.last_nbt = nbt;
            PacketHandler.sendTrackingBlock(
               this.getBlockPos(),
               this.level,
               new ScryingEntityPacket(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), Optional.of(nbt))
            );
         }
      } else if (this.last_nbt != null) {
         PacketHandler.sendTrackingBlock(
            this.getBlockPos(),
            this.level,
            new ScryingEntityPacket(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), Optional.empty())
         );
      }

      return super.getUpdatePacket();
   }

   private static boolean extract(net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> view){if(view==null)return false;try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){if(view.extract(view.getResource(),1,tx)!=1)return false;tx.commit();return true;}}
   public class BlockPosInfo {
      public BlockState state;
      public BlockEntity te;

      public BlockPosInfo(BlockPos pos, Level world) {
         this.state = world.getBlockState(pos);
         this.te = null;
      }

      public BlockPosInfo() {
         this.state = null;
         this.te = null;
      }

      public CompoundTag write() {
         if (this.state.getBlock() != Blocks.AIR && !(this.state.getBlock() instanceof ScryingMahoujin)) {
            CompoundTag nbt = new CompoundTag();
            new CompoundTag();
            new CompoundTag();
            if (this.state != null) {
               CompoundTag nbts = NbtUtils.writeBlockState(this.state);
               nbt.put("SCRY_STATE", nbts);
            }

            return nbt;
         } else {
            return new CompoundTag();
         }
      }

      public void read(CompoundTag nbt, Level level) {
         if (nbt.contains("SCRY_STATE") && level != null) {
            this.state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), nbt.getCompoundOrEmpty("SCRY_STATE"));
         }
      }
   }
}