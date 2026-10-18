package stepsword.mahoutsukai.dataattachments.mahoujinentity;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class MahoujinData {
   public static final StreamCodec<ByteBuf, MahoujinData> STREAM_CODEC = new StreamCodec<ByteBuf, MahoujinData>() {
      public MahoujinData decode(ByteBuf buf) {
         return new MahoujinData(
            (Optional<UUID>)ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC).decode(buf),
            (Integer)ByteBufCodecs.INT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Float)ByteBufCodecs.FLOAT.decode(buf),
            (Boolean)ByteBufCodecs.BOOL.decode(buf)
         );
      }

      public void encode(ByteBuf buf, MahoujinData data) {
         ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC).encode(buf, data.getCasterUuid());
         ByteBufCodecs.INT.encode(buf, data.getLife());
         ByteBufCodecs.FLOAT.encode(buf, data.getRotationYaw());
         ByteBufCodecs.FLOAT.encode(buf, data.getRotationRoll());
         ByteBufCodecs.FLOAT.encode(buf, data.getRotationPitch());
         ByteBufCodecs.FLOAT.encode(buf, data.getRotationSpeed());
         ByteBufCodecs.FLOAT.encode(buf, data.getCircleSize());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorR());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorG());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorB());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorA());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorR2());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorG2());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorB2());
         ByteBufCodecs.FLOAT.encode(buf, data.getColorA2());
         ByteBufCodecs.BOOL.encode(buf, data.isFired());
      }
   };
   private UUID casterUuid;
   private int life;
   private float rotationYaw;
   private float rotationRoll;
   private float rotationPitch;
   private float rotationSpeed;
   private float circleSize;
   private float colorR = 1.0F;
   private float colorG = 1.0F;
   private float colorB = 1.0F;
   private float colorA = 1.0F;
   private float colorR2 = 1.0F;
   private float colorG2 = 1.0F;
   private float colorB2 = 1.0F;
   private float colorA2 = 1.0F;
   private boolean fired;

   public MahoujinData() {
   }

   public MahoujinData(
      Optional<UUID> casterUuid,
      int life,
      float rotationYaw,
      float rotationRoll,
      float rotationPitch,
      float rotationSpeed,
      float circleSize,
      float colorR,
      float colorG,
      float colorB,
      float colorA,
      float colorR2,
      float colorG2,
      float colorB2,
      float colorA2,
      boolean fired
   ) {
      this.casterUuid = casterUuid.orElse(null);
      this.life = life;
      this.rotationYaw = rotationYaw;
      this.rotationRoll = rotationRoll;
      this.rotationPitch = rotationPitch;
      this.rotationSpeed = rotationSpeed;
      this.circleSize = circleSize;
      this.colorR = colorR;
      this.colorG = colorG;
      this.colorB = colorB;
      this.colorA = colorA;
      this.colorR2 = colorR2;
      this.colorG2 = colorG2;
      this.colorB2 = colorB2;
      this.colorA2 = colorA2;
      this.fired = fired;
   }

   public MahoujinData copy() {
      return new MahoujinData(
         Optional.ofNullable(this.casterUuid),
         this.life,
         this.rotationYaw,
         this.rotationRoll,
         this.rotationPitch,
         this.rotationSpeed,
         this.circleSize,
         this.colorR,
         this.colorG,
         this.colorB,
         this.colorA,
         this.colorR2,
         this.colorG2,
         this.colorB2,
         this.colorA2,
         this.fired
      );
   }

   public Optional<UUID> getCasterUuid() {
      return Optional.ofNullable(this.casterUuid);
   }

   public void setCasterUuid(UUID casterUuid) {
      this.casterUuid = casterUuid;
   }

   public int getLife() {
      return this.life;
   }

   public void setLife(int life) {
      this.life = life;
   }

   public float getRotationYaw() {
      return this.rotationYaw;
   }

   public void setRotationYaw(float rotationYaw) {
      this.rotationYaw = rotationYaw;
   }

   public float getRotationRoll() {
      return this.rotationRoll;
   }

   public void setRotationRoll(float rotationRoll) {
      this.rotationRoll = rotationRoll;
   }

   public float getRotationPitch() {
      return this.rotationPitch;
   }

   public void setRotationPitch(float rotationPitch) {
      this.rotationPitch = rotationPitch;
   }

   public float getRotationSpeed() {
      return this.rotationSpeed;
   }

   public void setRotationSpeed(float rotationSpeed) {
      this.rotationSpeed = rotationSpeed;
   }

   public float getCircleSize() {
      return this.circleSize;
   }

   public void setCircleSize(float circleSize) {
      this.circleSize = circleSize;
   }

   public float getColorR() {
      return this.colorR;
   }

   public void setColorR(float colorR) {
      this.colorR = colorR;
   }

   public float getColorG() {
      return this.colorG;
   }

   public void setColorG(float colorG) {
      this.colorG = colorG;
   }

   public float getColorB() {
      return this.colorB;
   }

   public void setColorB(float colorB) {
      this.colorB = colorB;
   }

   public float getColorA() {
      return this.colorA;
   }

   public void setColorA(float colorA) {
      this.colorA = colorA;
   }

   public float getColorR2() {
      return this.colorR2;
   }

   public void setColorR2(float colorR2) {
      this.colorR2 = colorR2;
   }

   public float getColorG2() {
      return this.colorG2;
   }

   public void setColorG2(float colorG2) {
      this.colorG2 = colorG2;
   }

   public float getColorB2() {
      return this.colorB2;
   }

   public void setColorB2(float colorB2) {
      this.colorB2 = colorB2;
   }

   public float getColorA2() {
      return this.colorA2;
   }

   public void setColorA2(float colorA2) {
      this.colorA2 = colorA2;
   }

   public boolean isFired() {
      return this.fired;
   }

   public void setFired(boolean fired) {
      this.fired = fired;
   }
}
