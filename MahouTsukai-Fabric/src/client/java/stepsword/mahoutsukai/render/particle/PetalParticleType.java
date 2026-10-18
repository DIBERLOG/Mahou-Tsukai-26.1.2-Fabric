package stepsword.mahoutsukai.render.particle;

import org.jetbrains.annotations.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class PetalParticleType {

   public static class PetalFactory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public PetalFactory(SpriteSet s) {
         this.spriteSet = s;
      }

      @Nullable
      public Particle createParticle(
         SimpleParticleType basicParticleType,
         ClientLevel world,
         double x,
         double y,
         double z,
         double xSpeed,
         double ySpeed,
         double zSpeed,
         RandomSource random
      ) {
         return new ParticlePetal(world, x, y, z, 0.5F, this.spriteSet);
      }
   }
}
