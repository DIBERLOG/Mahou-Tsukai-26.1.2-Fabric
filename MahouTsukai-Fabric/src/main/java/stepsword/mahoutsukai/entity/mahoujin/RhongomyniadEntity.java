package stepsword.mahoutsukai.entity.mahoujin;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.lance.LanceMahou;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class RhongomyniadEntity extends MahoujinEntity {
   private static final String TAG_STACK = "MAHOUTSUKAI_STACK";
   private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(RhongomyniadEntity.class, EntityDataSerializers.ITEM_STACK);
   public static final String entityName = "rhongomyniad_entity";
   public boolean nocaster = false;
   public float py = 0.0F;
   public float yy = 0.0F;

   public RhongomyniadEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public RhongomyniadEntity(Level world) {
      super((EntityType<?>)ModEntities.RHONGOMYNIAD.get(), world);
   }

   public RhongomyniadEntity(Level world, Player caster, float size, ItemStack stack) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.setCircleSize(size);
      this.setStack(stack);
   }

   public RhongomyniadEntity(Level world, float size, ItemStack stack) {
      this(world);
      this.setCircleSize(size);
      this.setStack(stack);
      this.nocaster = true;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STACK, ItemStack.EMPTY);
   }

   public net.minecraft.world.InteractionResult collect(Player player, net.minecraft.world.InteractionHand hand) {
      if (this.nocaster || !player.getUUID().equals(this.getCasterUUID())) return net.minecraft.world.InteractionResult.PASS;
      if (!this.level().isClientSide() && this.isAlive()) {
         if (!this.getStack().isEmpty()) this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getStack().copy()));
         this.setStack(ItemStack.EMPTY);
         this.discard();
      }
      return net.minecraft.world.InteractionResult.SUCCESS;
   }

   @Override
   public void tick() {
      super.tick();
      this.py = this.yy;
      this.yRotO = this.yRot;
      if (this.yRotO < 0.0F) {
         this.yRotO += 360.0F;
      }

      if (this.py < 0.0F) {
         this.py += 360.0F;
      }

      this.yy++;
      this.setRotationYaw(this.getRotationYaw() + this.getRotationSpeed());
      this.yRot = this.getRotationYaw();
      this.xRot = this.getRotationPitch();
      this.setRot(this.yRot, this.xRot);
      if (!this.level().isClientSide() && this.getLife() % 40 == 0) {
         float range = MTConfig.RHONGOMYNIAD_RANGE;
         if (this.nocaster || operate(this.level(), this.getCasterUUID(), this.blockPosition())) {
            AABB aabb = new AABB(this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range);
            List<LivingEntity> lst = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
            int max_smites = MTConfig.RHONGOMYNIAD_MAX_SMITES;
            int smites = 0;

            for (LivingEntity e : lst) {
               if (smites < max_smites) {
                  boolean immune = !this.nocaster
                     && MTConfig.RHONGOMYNIAD_RESPECT_IMMUNE
                     && ContractMahoujinTileEntity.isImmuneToSpell(e.level(), this.getCasterUUID(), e);
                  if (Caliburn.specialTarget(e) && !immune) {
                     strike(e, this);
                     smites++;
                  }
               }
            }
         }
      }
   }

   public static void strike(LivingEntity e, RhongomyniadEntity rhongo) {
      LanceMahou mahou = rhongo.getStack().getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT, new LanceMahou());
      float damage = mahou.getAttackDamage();
      SmiteEntity smite = new SmiteEntity(e.level(), e, 0.9019608F, 0.9019608F, 0.16470589F, 1.0F, 0.1F, damage);
      smite.setPos(e.getX(), e.getY(), e.getZ());
      e.level().addFreshEntity(smite);
   }

   public static boolean operate(Level world, UUID casterUUID, BlockPos pos) {
      int mana_drain = MTConfig.RHONGOMYNIAD_MANA_COST;
      if (casterUUID != null && pos != null && world != null) {
         boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(pos, world, casterUUID, mana_drain);
         Player caster = world.getPlayerByUUID(casterUUID);
         if (nearbyBatteriesGaveMana || caster != null && PlayerManaManager.drainMana(caster, mana_drain, false, true) == mana_drain) {
            return true;
         }
      }

      return false;
   }

   public static void rhongomyniadLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource damageSource) {
      Entity source = damageSource.getEntity();
      if (source instanceof LivingEntity && !Caliburn.specialTarget(target)) {
         float range = MTConfig.RHONGOMYNIAD_RANGE;
         AABB aabb = new AABB(
            source.getX() - range, source.getY() - range, source.getZ() - range, source.getX() + range, source.getY() + range, source.getZ() + range
         );

         for (RhongomyniadEntity e : target.level().getEntitiesOfClass(RhongomyniadEntity.class, aabb)) {
            boolean immune = !e.nocaster
               && MTConfig.RHONGOMYNIAD_RESPECT_IMMUNE
               && ContractMahoujinTileEntity.isImmuneToSpell(e.level(), e.getCasterUUID(), source);
            if (e.nocaster || !immune && operate(source.level(), e.getCasterUUID(), e.blockPosition())) {
               strike((LivingEntity)source, e);
            }
         }
      }
   }

   public boolean isPickable() {
      return true;
   }

   public void playerTouch(Player entityIn) {
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      ItemStack stack = input.read("MAHOUTSUKAI_STACK", ItemStack.CODEC)
         .orElse(ItemStack.EMPTY);
      this.nocaster = input.getBooleanOr("MAHOUTSUKAI_NOCASTER", false);
      if (!stack.isEmpty()) stack.setCount(1);
      this.setStack(stack);
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("MAHOUTSUKAI_NOCASTER", this.nocaster);
      if (!this.getStack().isEmpty()) {
         output.store("MAHOUTSUKAI_STACK", ItemStack.CODEC, this.getStack());
      }
   }

   public ItemStack getStack() {
      return (ItemStack)this.entityData.get(STACK);
   }

   public void setStack(ItemStack s) {
      this.entityData.set(STACK, s);
   }
}
