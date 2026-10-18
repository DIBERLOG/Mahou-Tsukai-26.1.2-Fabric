package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class AbstractMahoujinEntity extends Entity implements IMahoujinEntity {
   public static final net.minecraft.network.syncher.EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> MAHOUJIN_DATA=net.minecraft.network.syncher.SynchedEntityData.defineId(AbstractMahoujinEntity.class,stepsword.mahoutsukai.entity.ModEntityDataSerializers.MAHOUJIN);
   public AbstractMahoujinEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData(Builder builder) {
      IMahoujinEntity.super.defineSync(builder);
   }

   protected void readAdditionalSaveData(ValueInput input) {
      IMahoujinEntity.super.read(input);
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      IMahoujinEntity.super.add(output);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.setLife(this.getLife() + 1);
      }
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }
}
