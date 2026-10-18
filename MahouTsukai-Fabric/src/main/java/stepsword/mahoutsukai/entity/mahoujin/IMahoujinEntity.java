package stepsword.mahoutsukai.entity.mahoujin;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData;

public interface IMahoujinEntity {
   String TAG_CASTER = "MAHOUTSUKAI_CASTER";
   String TAG_LIFE = "MAHOUTSUKAI_LIFE";
   String TAG_ROTATION_YAW = "MAHOUTSUKAI_ROTATION_YAW";
   String TAG_ROTATION_ROLL = "MAHOUTSUKAI_ROTATION_ROLL";
   String TAG_ROTATION_PITCH = "MAHOUTSUKAI_ROTATION_PITCH";
   String TAG_ROTATION_SPEED = "MAHOUTSUKAI_ROTATION_SPEED";
   String TAG_CIRCLE_SIZE = "MAHOUTSUKAI_CIRCLE_SIZE";
   String TAG_COLOR_R = "MAHOUTSUKAI_COLOR_R";
   String TAG_COLOR_G = "MAHOUTSUKAI_COLOR_G";
   String TAG_COLOR_B = "MAHOUTSUKAI_COLOR_B";
   String TAG_COLOR_A = "MAHOUTSUKAI_COLOR_A";
   String TAG_COLOR_R2 = "MAHOUTSUKAI_COLOR_R2";
   String TAG_COLOR_G2 = "MAHOUTSUKAI_COLOR_G2";
   String TAG_COLOR_B2 = "MAHOUTSUKAI_COLOR_B2";
   String TAG_COLOR_A2 = "MAHOUTSUKAI_COLOR_A2";

   default net.minecraft.network.syncher.EntityDataAccessor<MahoujinData> mahoujinDataKey(){return AbstractMahoujinEntity.MAHOUJIN_DATA;}
   default Entity asEntity() {
      return (Entity)this;
   }

   default MahoujinData getMahoujinData() {
      return this.asEntity().getEntityData().get(this.mahoujinDataKey());
   }

   default void updateMahoujinData(Consumer<MahoujinData> updater) {
      MahoujinData data = this.getMahoujinData().copy();
      updater.accept(data);
      this.asEntity().getEntityData().set(this.mahoujinDataKey(),data);
   }

   default void defineSync(Builder builder) {builder.define(this.mahoujinDataKey(),new MahoujinData());
   }

   default UUID getCasterUUID() {
      return this.getMahoujinData().getCasterUuid().orElse(null);
   }

   default void setCasterUUID(UUID uuid) {
      this.updateMahoujinData(data -> data.setCasterUuid(uuid));
   }

   default int getLife() {
      return this.getMahoujinData().getLife();
   }

   default void setLife(int l) {
      this.updateMahoujinData(data -> data.setLife(l));
   }

   default float getRotationYaw() {
      return this.getMahoujinData().getRotationYaw();
   }

   default void setRotationYaw(float y) {
      this.updateMahoujinData(data -> data.setRotationYaw(y));
   }

   default float getRotationRoll() {
      return this.getMahoujinData().getRotationRoll();
   }

   default void setRotationRoll(float r) {
      this.updateMahoujinData(data -> data.setRotationRoll(r));
   }

   default float getRotationPitch() {
      return this.getMahoujinData().getRotationPitch();
   }

   default void setRotationPitch(float p) {
      this.updateMahoujinData(data -> data.setRotationPitch(p));
   }

   default float getRotationSpeed() {
      return this.getMahoujinData().getRotationSpeed();
   }

   default void setRotationSpeed(float s) {
      this.updateMahoujinData(data -> data.setRotationSpeed(s));
   }

   default float getCircleSize() {
      return this.getMahoujinData().getCircleSize();
   }

   default void setCircleSize(float s) {
      this.updateMahoujinData(data -> data.setCircleSize(s));
   }

   default boolean getFired() {
      return this.getMahoujinData().isFired();
   }

   default void setFired(boolean f) {
      this.updateMahoujinData(data -> data.setFired(f));
   }

   default void setColor(float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
      this.updateMahoujinData(data -> {
         data.setColorR(r);
         data.setColorG(g);
         data.setColorB(b);
         data.setColorA(a);
         data.setColorR2(r2);
         data.setColorG2(g2);
         data.setColorB2(b2);
         data.setColorA2(a2);
      });
   }

   default float[] getColors() {
      MahoujinData data = this.getMahoujinData();
      return new float[]{
         data.getColorR(), data.getColorG(), data.getColorB(), data.getColorA(), data.getColorR2(), data.getColorG2(), data.getColorB2(), data.getColorA2()
      };
   }

   default void read(ValueInput input) {
      if (input.child("neoforge:attachments").isPresent() && input.getInt("MAHOUTSUKAI_LIFE").isEmpty()) input=stepsword.mahoutsukai.fabric.LegacyMahouData.attachment(input,"mahoujin_entity").orElse(input);
      MahoujinData data=new MahoujinData();stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinDataStorage.read(data,input);data.setFired(input.getBooleanOr("MAHOUTSUKAI_FIRED",false));this.asEntity().getEntityData().set(this.mahoujinDataKey(),data);
   }

   default void add(ValueOutput output) {stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinDataStorage.write(getMahoujinData(),output);output.putBoolean("MAHOUTSUKAI_FIRED",getFired());
   }
}
