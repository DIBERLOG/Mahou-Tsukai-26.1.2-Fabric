package stepsword.mahoutsukai.item.rulebreaker;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class ProbabilityAlterRandom extends XoroshiroRandomSource {
   public int designation = 0;
   public int modulus = 0;

   public ProbabilityAlterRandom(int designation, int modulus) {
      super(designation);
      this.designation = designation % modulus;
      this.modulus = modulus;
   }

   public int nextInt() {
      return this.designation;
   }

   public int nextInt(int bound) {
      return this.designation % bound;
   }

   public long nextLong() {
      return this.designation;
   }

   public boolean nextBoolean() {
      return this.designation % 2 == 0;
   }

   public float nextFloat() {
      return (float)this.designation / this.modulus;
   }

   public double nextDouble() {
      return (double)this.designation / this.modulus;
   }

   public RandomSource fork() {
      return new ProbabilityAlterRandom(this.designation, this.modulus);
   }

   public PositionalRandomFactory forkPositional() {
      return super.forkPositional();
   }

   public int nextIntBetweenInclusive(int p_216333_, int p_216334_) {
      return this.nextInt(p_216334_ - p_216333_ + 1) + p_216333_;
   }

   public double triangle(double p_216329_, double p_216330_) {
      return p_216329_ + p_216330_ * (this.nextDouble() - this.nextDouble());
   }

   public int nextInt(int min, int max) {
      return min + this.nextInt(max - min);
   }

   public synchronized double nextGaussian() {
      return this.nextDouble();
   }
}
