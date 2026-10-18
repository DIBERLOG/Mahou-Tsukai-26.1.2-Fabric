package stepsword.mahoutsukai.entity.kodoku;

import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.kodoku.KodokuItem;

public class KodokuEntity extends Animal {
   public int kodoku = 0;
   public String KODOKU_TRACKER = "MAHOUTSUKAI_KODOKU_TRACKER";
   public static final String entityName = "kodoku_entity";

   public KodokuEntity(Level worldIn) {
      super((EntityType)ModEntities.KODOKU.get(), worldIn);
      this.setPersistenceRequired();
   }

   public static Builder registerAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
   }

   public KodokuEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.kodoku = input.getIntOr(this.KODOKU_TRACKER, 0);
   }

   public void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt(this.KODOKU_TRACKER, this.kodoku);
   }

   public boolean isFood(ItemStack itemStack) {
      return false;
   }

   public boolean canControlVehicle() {
      return false;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.getVehicle() instanceof LivingEntity) {
         LivingEntity riding = (LivingEntity)this.getVehicle();
         if (riding.getHealth() < 5.0F && this.level().getGameTime() % 20L == 0L) {
            riding.hurtServer((ServerLevel)this.level(),this.damageSources().mobAttack(this), 2.0F);
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE, this.getSoundVolume(), this.getVoicePitch()
               );
         }
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageable) {
      return null;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (!this.level().isClientSide()) {
         ItemStack stack = new ItemStack((ItemLike)ModItems.REGISTERED.get("kodoku"), 1);
         KodokuItem.setKodoku(stack, this.kodoku);
         this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack));
         this.discard();
      }

      return InteractionResult.SUCCESS;
   }
}
