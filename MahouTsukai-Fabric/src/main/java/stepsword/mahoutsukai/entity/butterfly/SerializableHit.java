package stepsword.mahoutsukai.entity.butterfly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SerializableHit {
   public ItemStack stack;
   public ItemStack offstack;
   public Vec3 knockback;
   public Vec3 initialPosition;
   public List<MobEffectInstance> effects;
   public float knockbackStrength;
   public float cooled;
   public float rotyaw;
   public float rotpit;
   public int fire;
   public static Codec<SerializableHit> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(SerializableHit::getStack),
            ItemStack.OPTIONAL_CODEC.fieldOf("offstack").forGetter(SerializableHit::getOffstack),
            Vec3.CODEC.fieldOf("knockback").forGetter(SerializableHit::getKnockback),
            Vec3.CODEC.fieldOf("initialPosition").forGetter(SerializableHit::getInitialPosition),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(SerializableHit::getEffects),
            Codec.FLOAT.fieldOf("knockbackStrength").forGetter(SerializableHit::getKnockbackStrength),
            Codec.FLOAT.fieldOf("cooled").forGetter(SerializableHit::getCooled),
            Codec.FLOAT.fieldOf("rotyaw").forGetter(SerializableHit::getRotyaw),
            Codec.FLOAT.fieldOf("rotpit").forGetter(SerializableHit::getRotpit),
            Codec.INT.fieldOf("fire").forGetter(SerializableHit::getFire)
         )
         .apply(instance, SerializableHit::new)
   );

   public SerializableHit() {
   }

   public SerializableHit(
      ItemStack stack,
      ItemStack offstack,
      Vec3 knockback,
      Vec3 initialPosition,
      List<MobEffectInstance> effects,
      float knockbackStrength,
      float cooled,
      float rotyaw,
      float rotpit,
      int fire
   ) {
      this.stack = stack;
      this.offstack = offstack;
      this.knockback = knockback;
      this.initialPosition = initialPosition;
      this.effects = effects;
      this.knockbackStrength = knockbackStrength;
      this.cooled = cooled;
      this.rotyaw = rotyaw;
      this.rotpit = rotpit;
      this.fire = fire;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public void setStack(ItemStack stack) {
      this.stack = stack;
   }

   public ItemStack getOffstack() {
      return this.offstack;
   }

   public void setOffstack(ItemStack offstack) {
      this.offstack = offstack;
   }

   public Vec3 getKnockback() {
      return this.knockback;
   }

   public void setKnockback(Vec3 knockback) {
      this.knockback = knockback;
   }

   public Vec3 getInitialPosition() {
      return this.initialPosition;
   }

   public void setInitialPosition(Vec3 initialPosition) {
      this.initialPosition = initialPosition;
   }

   public List<MobEffectInstance> getEffects() {
      return this.effects;
   }

   public void setEffects(List<MobEffectInstance> effects) {
      this.effects = effects;
   }

   public float getKnockbackStrength() {
      return this.knockbackStrength;
   }

   public void setKnockbackStrength(float knockbackStrength) {
      this.knockbackStrength = knockbackStrength;
   }

   public float getCooled() {
      return this.cooled;
   }

   public void setCooled(float cooled) {
      this.cooled = cooled;
   }

   public float getRotyaw() {
      return this.rotyaw;
   }

   public void setRotyaw(float rotyaw) {
      this.rotyaw = rotyaw;
   }

   public float getRotpit() {
      return this.rotpit;
   }

   public void setRotpit(float rotpit) {
      this.rotpit = rotpit;
   }

   public int getFire() {
      return this.fire;
   }

   public void setFire(int fire) {
      this.fire = fire;
   }
}
