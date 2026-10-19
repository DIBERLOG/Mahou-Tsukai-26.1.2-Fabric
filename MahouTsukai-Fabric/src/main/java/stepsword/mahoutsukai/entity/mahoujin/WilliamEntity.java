package stepsword.mahoutsukai.entity.mahoujin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.FaeEssence;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;

import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class WilliamEntity extends MahoujinEntity {
   private static final String TAG_STACK = "MAHOUTSUKAI_STACK";
   private static final float DEFAULT_ROTATION_PITCH = -45.0F;
   private static final AABB ZERO_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public static final AABB bb = new AABB(-64.0, Double.NEGATIVE_INFINITY, -64.0, 64.0, Double.POSITIVE_INFINITY, 64.0);
   private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(WilliamEntity.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Vector3fc> STARTING_POINT = SynchedEntityData.defineId(WilliamEntity.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Float> COVER_OPENNESS = SynchedEntityData.defineId(WilliamEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> PAGE_OPENNESS = SynchedEntityData.defineId(WilliamEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> THEATER_RUNNING = SynchedEntityData.defineId(WilliamEntity.class, EntityDataSerializers.BOOLEAN);
   public static java.util.function.Consumer<WilliamEntity> clientTick=e->{};
   public Object last_br,br;
   public long clientTicks = 0L;
   public float[] page_colors = null;
   public float[] cover_colors = null;
   public float[] text_colors = null;
   public float[] circle_colors = null;
   boolean nocaster = false;
   public float py = 0.0F;
   public float yy = 0.0F;
   boolean down = true;
   public float goToYaw = -1.0F;
   public Vec3 goToPlace = null;
   public int unreachable = 0;

   public WilliamEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
      this.setRotationPitch(-45.0F);
   }

   public WilliamEntity(Level world) {
      super((EntityType<?>)ModEntities.WILLIAM.get(), world);
      this.setRotationPitch(-45.0F);
   }

   public WilliamEntity(Level world, Player caster, float size, ItemStack stack) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.sizer(size);
      this.setStack(stack);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STACK, ItemStack.EMPTY);
      builder.define(STARTING_POINT, new Vector3f());
      builder.define(COVER_OPENNESS, 90.0F);
      builder.define(PAGE_OPENNESS, 0.5F);
      builder.define(THEATER_RUNNING, true);
   }

   public static void williamRightClick(Player player,Entity e,InteractionHand hand) {
      if (e instanceof WilliamEntity w && !e.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
         if (player.isDiscrete() && player.getUUID().equals(w.getCasterUUID())) {
            w.setTheaterRunning(!w.getTheaterRunning());
            if (!w.getTheaterRunning()) {
               PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.THEATER_OFF);
            } else {
               PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.THEATER_ON);
            }
         } else {
            Utils.schedule(w.level(), () -> {
               if (w.getStack() != null && w.isAlive()) {
                  ItemStack drop = w.getStack().copyWithCount(1);
                  w.level().addFreshEntity(new ItemEntity(w.level(), w.getX(), w.getY(), w.getZ(), drop));
                  w.discard();
               } else {
                  w.discard();
               }
            });
         }
      }
   }

   public Player getCaster() {
      UUID casterUUID = this.getCasterUUID();
      if (casterUUID != null && !casterUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000")) && !casterUUID.equals(FaeEssence.faeID)) {
         return (Player)(this.level() instanceof ServerLevel server
            ? server.getServer().getPlayerList().getPlayer(casterUUID)
            : this.level().getPlayerByUUID(casterUUID));
      } else {
         return null;
      }
   }

   @Override
   public void tick() {
      super.tick();
      // Previous roll (not the unused yy field) so the client interpolates a smooth spin instead of
      // interpolating from zero every frame.
      this.py = this.getRotationRoll();
      if (((Vector3fc)this.entityData.get(STARTING_POINT)).equals(new Vector3f())) {
         this.entityData.set(STARTING_POINT, this.position().toVector3f());
      }

      this.yRotO = this.yRot;
      if (this.yRotO < 0.0F) {
         this.yRotO += 360.0F;
      }

      if (this.py < 0.0F) {
         this.py += 360.0F;
      }

      this.setRotationYaw(this.getRotationYaw() + this.getRotationSpeed());
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      int style = 0;
      int fame = 0;
      ItemStack stack = this.getStack();
      if (stack != null && !stack.isEmpty() && stack.has(DataComponents.FICTION_COMPONENT)) {
         FictionMahou mahou = Utils.getFictionMahou(stack);
         style = mahou.getStyle();
         fame = mahou.getFame();
      }

      this.placesToBe();
      this.peopleToSee(style, fame);
      this.thingsToDo();
      this.plethora(style, fame);
      this.yRot = this.getRotationRoll();
      this.xRot = this.getRotationPitch();
      this.setRot(this.yRot, this.xRot);
      this.move(MoverType.SELF, this.getDeltaMovement());
      if (!this.level().isClientSide()) {
         this.hover();
      } else {
         clientTick.accept(this);
         this.clientTicks++;
      }
   }

   public void move(MoverType type, Vec3 v) {
      // Bound both the sweep and the physical box before invoking vanilla's block iterator.
      // Old saves/external impulses must not turn a hovering book into a multi-chunk sweep.
      if (!Double.isFinite(v.x) || !Double.isFinite(v.y) || !Double.isFinite(v.z)) v=Vec3.ZERO;
      if (v.lengthSqr() > 0.25) v=v.normalize().scale(0.5);
      this.setDeltaMovement(v);
      this.setBoundingBox(new AABB(this.getX()-.5,this.getY(),this.getZ()-.5,this.getX()+.5,this.getY()+1,this.getZ()+.5));
      if (!this.level().hasChunkAt(BlockPos.containing(this.position().add(v)))) return;
      v = this.collide(v);
      Vec3 go = this.position().add(v);
      this.setPos(go);
   }

   public void hover() {
      Vec3 curr = this.getDeltaMovement();
      double factor = 0.05;
      double mult = 0.02;
      if (curr.y < -1.0 * mult && this.down) {
         this.down = false;
      } else if (curr.y > 1.0 * mult && !this.down) {
         this.down = true;
      }

      this.setDeltaMovement(this.getDeltaMovement().x, curr.y + factor * mult * (this.down ? -1 : 1), this.getDeltaMovement().z);
   }

   public void placesToBe() {
      if (!this.level().isClientSide()) {
         float roll = this.getRotationRoll();
         this.goToYaw = Utils.mod(this.goToYaw, 360);
         boolean spin = false;
         if (this.goToPlace != null) {
            spin = Math.abs(this.goToYaw - roll) > 11.0F;
            if (!spin && Math.abs(this.goToYaw - roll) > 1.0F) {
               this.setRotationRoll(this.goToYaw);
            }
         }

         if (spin) {
            float spinspeed = 10.0F;
            boolean goUp = Utils.yawUp(roll, this.goToYaw);
            if (!goUp) {
               spinspeed *= -1.0F;
            }

            float newroll = Utils.mod(roll + spinspeed, 360);
            this.setRotationRoll(newroll);
         } else {
            boolean just = false;
            if (this.random.nextFloat() < 0.005F && this.goToPlace == null) {
               Vector3fc start = (Vector3fc)this.entityData.get(STARTING_POINT);
               Vec3 startv = new Vec3(start);
               Vec3 towards = startv.subtract(this.position());
               towards = new Vec3(towards.x + this.random.nextDouble() * 0.01, 0.0, towards.z + this.random.nextDouble() * 0.01);
               double min = towards.length() + 1.0;
               towards = towards.normalize().yRot(EffectUtil.toRad(this.random.nextInt(-90, 90))).scale(Math.max(min, this.random.nextDouble() * 2.0 + 1.0));
               this.goToYaw = EffectUtil.toDegrees((float)Math.atan2(towards.x, towards.z)) + 180.0F;
               this.goToPlace = this.position().add(towards);
               just = true;
               this.unreachable = 0;
            }

            if (this.goToPlace != null && !just) {
               float speed = 0.1F;
               Vec3 direction = this.goToPlace.subtract(this.position());
               direction = new Vec3(direction.x, 0.0, direction.z);
               if (direction.length() < speed * 5.0F) {
                  this.goToPlace = null;
                  this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
               } else {
                  direction = direction.normalize().scale(speed);
                  this.setDeltaMovement(direction.x, this.getDeltaMovement().y, direction.z);
               }

               this.unreachable++;
               if (this.unreachable > 50) {
                  this.goToPlace = null;
               }
            }
         }
      }
   }

   public void thingsToDo() {
      if (!this.level().isClientSide()) {
         if (this.random.nextDouble() < 0.003 || Math.abs((Float)this.entityData.get(COVER_OPENNESS) - 90.0F) < 0.01) {
            this.entityData.set(COVER_OPENNESS, (float)this.random.nextInt(10, 60));
         }

         if (this.random.nextDouble() < 0.1) {
            this.entityData.set(PAGE_OPENNESS, this.random.nextFloat() * 0.4F + 0.3F);
         }
      }
   }

   public void plethora(int style, int fame) {
      int range = MTConfig.WILLIAM_NEARBY_MANA_RANGE;
      int remainingManaToGive = (int)(style * Math.abs(Math.max(0, fame) * MTConfig.WILLIAM_NEARBY_MANA_FACTOR));
      if (this.level().getGameTime() % MTConfig.WILLIAM_PLETHORA_FREQUENCY == 0L) {
         if (remainingManaToGive > 0) {
            Set<BlockEntity> bes = PlayerManaManager.nearbyBatteries(Utils.toBlockPos(this.position()), this.level(), range, false);
            ArrayList<BlockEntity> be_list = new ArrayList<>(bes);
            Collections.shuffle(be_list);
            if (!be_list.isEmpty()) {
               for (BlockEntity be : be_list) {
                  if (be instanceof ManaCircuitTileEntity mte
                     && mte.getOwnerUUID().equals(this.getCasterUUID())
                     && remainingManaToGive > 0
                     && mte.getMana() < mte.getMaxMana()) {
                     int space = mte.getMaxMana() - mte.getMana();
                     if (remainingManaToGive > space) {
                        mte.setMana(mte.getMaxMana());
                        remainingManaToGive -= space;
                     } else {
                        mte.setMana(Math.min(mte.getMaxMana(), mte.getMana() + remainingManaToGive));
                        remainingManaToGive = 0;
                     }
                  }
               }
            }
         } else if (remainingManaToGive < 0) {
            List<Entity> players = this.level()
               .getEntities(
                  this, new AABB(this.position().add(-range, -range, -range), this.position().add(range, range, range)), entity -> entity instanceof Player
               );
            if (!players.isEmpty()) {
               remainingManaToGive /= players.size();

               for (Entity e : players) {
                  if (e instanceof ServerPlayer p && !EffectUtil.hasBuff(p, ModEffects.MANA_TAKEN_COOLDOWN)) {
                     IMahou mahou = Utils.getPlayerMahou(p);
                     int mana = mahou.getStoredMana();
                     if (mana > 0) {
                        mahou.setStoredMana(Math.max(mana + remainingManaToGive, 0));
                        EffectUtil.buff(p, ModEffects.MANA_TAKEN_COOLDOWN, false, 100, false);
                        PlayerManaManager.updateClientMahou(p, mahou);
                     }
                  }
               }
            }
         }
      }
   }

   public void peopleToSee(int style, int fame) {
      if (!this.level().isClientSide()) {
         this.setLife(this.getLife() + 1);
         if (this.getLife() % 40 == 0 && this.getTheaterRunning()) {
            float range = MTConfig.WILLIAM_THEATER_RANGE;
            if (this.nocaster || operate(this.level(), this.getCasterUUID(), this.blockPosition())) {
               AABB aabb = new AABB(
                  this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range
               );
               List<LivingEntity> lst = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
               Player player = this.getCaster();
               boolean includeCaster = lst.contains(player);
               if (player != null) {
                  lst.remove(player);
                  lst.addFirst(player);
               }

               if (lst.size() > 1) {
                  RandomSource random;
                  if (player != null) {
                     random = player.getRandom();
                  } else {
                     random = this.getRandom();
                  }

                  List<Integer> ints = new ArrayList<>();

                  for (int m = 0; m < lst.size(); m++) {
                     ints.add(random.nextInt(lst.size()));
                  }

                  for (int n = 0; n < lst.size(); n++) {
                     LivingEntity living = lst.get(n);
                     if (includeCaster || living != player) {
                        if (style > 0) {
                           ILivingMahou mahou = Utils.getLivingMahou(living);
                           Entity e = this.level().getEntity(mahou.getActing());
                           if (!(e instanceof LivingEntity) || !e.isAlive()) {
                              LivingEntity target = lst.get(ints.get(n));
                              mahou.setActing(target.getId());
                           }

                           EffectUtil.buff(living, ModEffects.ACTING, false, 200, true);
                        } else if (style < 0) {
                           EffectUtil.buff(living, ModEffects.ABYSS, false, 200, true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean operate(Level world, UUID casterUUID, BlockPos pos) {
      return true;
   }

   public boolean isPickable() {
      return true;
   }

   public void playerTouch(Player entityIn) {
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void sizer(float s) {
      this.setCircleSize(s);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      ItemStack stack = input.read("MAHOUTSUKAI_STACK", ItemStack.CODEC).orElse(ItemStack.EMPTY);
      if (!stack.isEmpty()) {
         stack.setCount(1);
         this.setStack(stack);

      }

      this.nocaster = input.getBooleanOr("NO_CASTER", false);
      this.setTheaterRunning(input.getBooleanOr("THEATER_RUNNING", true));
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("MAHOUTSUKAI_STACK", ItemStack.CODEC, this.getStack());
      output.putBoolean("NO_CASTER", this.nocaster);
      output.putBoolean("THEATER_RUNNING", this.getTheaterRunning());
   }


   public ItemStack getStack() {
      return (ItemStack)this.entityData.get(STACK);
   }

   public void setStack(ItemStack s) {
      this.entityData.set(STACK, s == null ? ItemStack.EMPTY : s.copyWithCount(1));
   }

   public void setTheaterRunning(boolean b) {
      this.entityData.set(THEATER_RUNNING, b);
   }

   public boolean getTheaterRunning() {
      return (Boolean)this.entityData.get(THEATER_RUNNING);
   }
 public float coverOpenness(){return entityData.get(COVER_OPENNESS);} public float pageOpenness(){return entityData.get(PAGE_OPENNESS);}

}
