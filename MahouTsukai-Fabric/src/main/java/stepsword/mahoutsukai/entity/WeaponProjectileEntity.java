package stepsword.mahoutsukai.entity;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
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
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.effects.projection.ClarentEffect;

import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.mana.PlayerManaManager;

public class WeaponProjectileEntity extends Arrow {
   public static final String entityName = "weapon_projectile_entity";
   public static Field a1 = null;
   public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   public int wpe_life = 0;
   private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(WeaponProjectileEntity.class, EntityDataSerializers.ITEM_STACK);

   public WeaponProjectileEntity(Level worldIn) {
      super((EntityType)ModEntities.WEAPON_PROJECTILE.get(), worldIn);
      this.setStack(new ItemStack(Items.IRON_SWORD));
   }

   public WeaponProjectileEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn);
      this.setPos(x, y, z);
      this.setStack(stack);
      this.pickup = Pickup.ALLOWED;
   }

   public WeaponProjectileEntity(Level worldIn, LivingEntity shooter, ItemStack stack) {
      this(worldIn, shooter.getX(), shooter.getY() + shooter.getEyeHeight() - 0.1F, shooter.getZ(), stack);
      this.setOwner(shooter);
      this.pickup = Pickup.ALLOWED;
      this.wpe_life = 0;
   }

   public boolean shouldBeSaved() {
      return !this.level().dimension().identifier().toString().equals("mahoutsukai:reality_marble");
   }

   public WeaponProjectileEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(ITEM, new ItemStack(Items.IRON_SWORD));
      super.defineSynchedData(builder);
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("wpe_life", this.wpe_life);
      output.storeNullable("stack", ItemStack.CODEC, this.getStack());
   }

   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.wpe_life = input.getInt("wpe_life").orElse(0);
      ItemStack stack = input.read("stack", ItemStack.CODEC).orElse(Items.IRON_SWORD.getDefaultInstance());
      stack.setCount(1);
      this.setStack(stack);
   }

   protected ItemStack getPickupItem() {
      return this.getStack();
   }

   protected void onHitEntity(EntityHitResult result) {
      Entity entity = result.getEntity();
      if (this.level() instanceof ServerLevel && !GateWeaponProjectileEntity.shouldAvoid(this.getOwner(), entity)) {
         SafeFakePlayer fake_player = SafeFakePlayer.createFakePlayerIfNull((ServerLevel)this.level());
         ItemStack stack = this.getPickupItem();
         fake_player.setItemInHand(InteractionHand.MAIN_HAND, stack);
         Caliburn.updateAttr(stack, fake_player, EquipmentSlot.MAINHAND);
         fake_player.getAttributes()
            .getInstance(Attributes.ATTACK_DAMAGE)
            .addOrUpdateTransientModifier(
               new AttributeModifier(
                  Identifier.fromNamespaceAndPath("mahoutsukai", "weapon_shooter_config"),
                  MTConfig.WEAPON_SHOOTER_DAMAGE_MULTIPLIER - 1.0,
                  Operation.ADD_MULTIPLIED_TOTAL
               )
            );
         if (!(entity instanceof AbstractArrow)) {
            if (this.getOwner() instanceof Player p) {
               MandatoryFun.completeFunReq(p, MandatoryFun.FUN_REQS.SHOOT_A_SWORD);
            }

            if (this.getStack().getItem() instanceof Clarent && this.getOwner() instanceof Player) {
               int mana = ClarentEffect.getManaCostClarent(this.getStack(), (Player)this.getOwner());
               PlayerManaManager.donateManaToFakePlayer(fake_player, (Player)this.getOwner(), mana);
            }

            fake_player.attackStrengthTicker = 1000;
            fake_player.attack(entity);
            if (entity instanceof LivingEntity) {
               this.doPostHurtEffects((LivingEntity)entity);
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
   }

   public void tick() {
      if (this.life > 5) {
         this.life = 4;
      }

      this.wpe_life++;
      if (this.wpe_life > 3600) {
         this.discard();
      }

      super.tick();
   }

   public void playerTouch(Player player) {
      super.playerTouch(player);
   }

   protected boolean tryPickup(Player player) {
      if (this.pickup == Pickup.CREATIVE_ONLY) {
         return player.hasInfiniteMaterials();
      }

      if (this.pickup != Pickup.ALLOWED) {
         return false;
      }

      ItemStack stack = this.getPickupItem().copy();
      stack.setCount(1);
      stack.hurtAndBreak(2, player, EquipmentSlot.MAINHAND);
      return player.getInventory().add(stack);
   }

   public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
      float f = -Mth.sin(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
      float f1 = -Mth.sin(pitch * (float) (Math.PI / 180.0));
      float f2 = Mth.cos(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
      this.shoot(f, f1, f2, velocity, inaccuracy);
      this.setDeltaMovement(
         this.getDeltaMovement().add(shooter.getDeltaMovement().x, shooter.onGround() ? 0.0 : shooter.getDeltaMovement().y, shooter.getDeltaMovement().z)
      );
   }

   public ItemStack getStack() {
      return (ItemStack)this.getEntityData().get(ITEM);
   }

   public void setStack(ItemStack stack) {
      this.getEntityData().set(ITEM, stack);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 4096.0;
   }
}
