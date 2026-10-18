package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.projection.ClarentEffect;
import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;

public class GateWeaponProjectileEntity extends Arrow implements IMahoujinEntity {
   public static final EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> MAHOUJIN_DATA=SynchedEntityData.defineId(GateWeaponProjectileEntity.class,stepsword.mahoutsukai.entity.ModEntityDataSerializers.MAHOUJIN);
   public EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> mahoujinDataKey(){return MAHOUJIN_DATA;}
   public static final String entityName = "gate_weapon_projectile_entity";
   private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(GateWeaponProjectileEntity.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.defineId(GateWeaponProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(GateWeaponProjectileEntity.class, EntityDataSerializers.FLOAT);
   private float progress = 0.0F;
   public float lastProgress = 0.0F;

   public GateWeaponProjectileEntity(Level worldIn) {
      super((EntityType)ModEntities.GATE_WEAPON_PROJECTILE.get(), worldIn);
      this.setStack(new ItemStack(Items.IRON_SWORD));
   }

   public GateWeaponProjectileEntity(Level worldIn, double x, double y, double z, ItemStack stack, Entity e, float speed) {
      this(worldIn);
      this.setPos(x, y, z);
      this.setStack(stack);
      this.pickup = Pickup.DISALLOWED;
      this.setOwner(e);
      this.setSpeed(speed);
   }

   public GateWeaponProjectileEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(ITEM, new ItemStack(Items.IRON_SWORD));
      builder.define(PROGRESS, 0.0F);
      builder.define(SPEED, (float)MTConfig.TREASURY_PROJECTION_SPEED);
      IMahoujinEntity.super.defineSync(builder);
      super.defineSynchedData(builder);
   }

   public void addAdditionalSaveData(ValueOutput output) {
      output.store("stack", ItemStack.CODEC, this.getStack() == null ? new ItemStack(Items.IRON_SWORD) : this.getStack());
      output.putFloat("progress", this.progress);
      IMahoujinEntity.super.add(output);
      super.addAdditionalSaveData(output);
   }

   public void readAdditionalSaveData(ValueInput input) {
      ItemStack stack = input.read("stack", ItemStack.CODEC).orElse(new ItemStack(Items.IRON_SWORD));
      stack.setCount(1);
      this.setStack(stack);
      this.progress = input.getFloatOr("progress", 0.0F);
      IMahoujinEntity.super.read(input);
      super.readAdditionalSaveData(input);
   }

   protected ItemStack getPickupItem() {
      return this.getStack();
   }

   public float getProgress() {
      return this.progress;
   }

   public void setProgress(float v) {
      this.progress = v;
   }

   public static boolean shouldAvoid(Entity shooter, Entity target) {
      return target instanceof LivingEntity && shooter instanceof Player
         ? ContractMahoujinTileEntity.isImmuneToSpell(shooter.level(), shooter.getUUID(), target) || GeasEffect.hasGeas((Player)shooter, (LivingEntity)target)
         : false;
   }

   protected void onHitEntity(EntityHitResult result) {
      double d = this.getDeltaMovement().length();
      Entity entity = result.getEntity();
      if (this.level() instanceof ServerLevel && !shouldAvoid(this.getOwner(), entity)) {
         SafeFakePlayer fake_player = SafeFakePlayer.createFakePlayerIfNull((ServerLevel)this.level());
         fake_player.setItemInHand(InteractionHand.MAIN_HAND, this.getPickupItem());
         fake_player.setOnGround(true);
         Caliburn.updateAttr(this.getPickupItem(), fake_player, EquipmentSlot.MAINHAND);
         fake_player.setPos(this.getX(), this.getY(), this.getZ());
         if (!(entity instanceof AbstractArrow)) {
            if (this.getStack().getItem() instanceof Clarent && this.getOwner() instanceof Player) {
               int mana = ClarentEffect.getManaCostClarent(this.getStack(), (Player)this.getOwner());
               PlayerManaManager.donateManaToFakePlayer(fake_player, (Player)this.getOwner(), mana);
            }

            fake_player.attackStrengthTicker = 1000;
            fake_player.attack(entity);
            if (entity instanceof LivingEntity e) {
               this.doPostHurtEffects(e);
            }

            Entity shooter = null;
            if (this.getOwner() != null) {
               shooter = this.getOwner();
               if (entity != shooter && entity instanceof Player && shooter instanceof ServerPlayer) {
                  ((ServerPlayer)shooter).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND, 0.0F));
               }
            }
         }
      }

      this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
   }

   public void tick() {
      this.tickDespawn();
      this.tickDespawn();
      this.lastProgress = this.getProgress();
      float progressCap = this.getProgressCap();
      if (this.getProgress() > progressCap) {
         super.tick();
         if (this.isInGround() && MTConfig.TREASURY_PROJECTION_DESPAWN_AFTER_LAND) {
            this.discard();
         }
      } else {
         if (this.isInGround()) {
            this.setProgress(this.getProgressCap() + 1.0F);
         }

         float speed = this.getProgressSpeed();
         this.setProgress(this.getProgress() + progressCap / speed);
         Vec3 z = this.getDeltaMovement();
         z = z.normalize().scale(progressCap / speed);
         z = z.add(this.position());
         this.setPos(z.x, z.y, z.z);
      }
   }

   public void playerTouch(Player player) {
   }

   public float getSpeed() {
      return (Float)this.getEntityData().get(SPEED);
   }

   public void setSpeed(float s) {
      this.getEntityData().set(SPEED, s);
   }

   public ItemStack getStack() {
      return (ItemStack)this.getEntityData().get(ITEM);
   }

   public void setStack(ItemStack stack) {
      this.getEntityData().set(ITEM, stack);
   }

   public void setProgress() {
      this.setProgress(this.progress);
   }

   public float getProgressCap() {
      return 4.0F;
   }

   public float getProgressSpeed() {
      return (float)Math.max(1.0, Math.max(MTConfig.TREASURY_PROJECTION_SPEED_MIN, this.getSpeed()));
   }
}
