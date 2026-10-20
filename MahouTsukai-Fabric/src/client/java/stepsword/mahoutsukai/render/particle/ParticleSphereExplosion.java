package stepsword.mahoutsukai.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class ParticleSphereExplosion extends Particle {
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   public String loc;
   public float yaw = 0.0F;
   public float pitch = 0.0F;
   public float[] rgba = new float[8];
   public float rotation = 0.0F;
   public float sz = 0.2F;
   public boolean shaders = false;
   public int image;
   public float speed = 0.0F;
   public float lastRadius = 0.0F;
   public float radius = 0.0F;
   public float maxRadius = 4.0F;
   public boolean destroy = false;
   public int quality = MTConfig.EXPLOSION_PARTICLE_QUALITY;
   public int delay = 0;
   public int delay2 = 0;

   public ParticleSphereExplosion(ClientLevel worldIn, float[] colors, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      float maxAge = 30.0F;
      this.lifetime = (int)(maxAge / (Math.random() * 0.3 + 0.6));
      this.lifetime = (int)this.lifetime;
      this.rgba = colors;
      this.image = this.random.nextInt(6) + 1;
   }

   public void addDelay(int delay) {
      this.delay += delay;
      this.lifetime += delay;
   }

   public void addDelay2(int delay2) {
      this.delay2 += delay2;
      this.lifetime += delay2;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      }

      if (this.delay > 0) {
         this.delay--;
      } else {
         this.speed = (this.maxRadius - this.radius) * 1.0F / 3.0F;
         if (!this.destroy && this.speed < 0.02F) {
            this.destroy = true;
         }

         if (this.destroy) {
            if (this.delay2 > 0) {
               this.delay2--;
            } else {
               this.speed = -this.maxRadius / 3.0F;
            }
         }

         this.lastRadius = this.radius;
         this.radius = Mth.clamp(this.radius + this.speed, 0.0F, this.maxRadius);
      }
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.NO_RENDER;
   }

   public static void renderParticleSphereExplosion(ParticleSphereExplosion particle, double d3, double d4, double d5, PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {
      double d0 = particle.xo + (particle.x - particle.xo) * partialTicks;
      double d1 = particle.yo + (particle.y - particle.yo) * partialTicks;
      double d2 = particle.zo + (particle.z - particle.zo) * partialTicks;
      matrix.pushPose();
      matrix.translate(d0 - d3, d1 - d4, d2 - d5);
      RenderUtils.rotateQ(particle.yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(particle.pitch, 1.0F, 0.0F, 0.0F, matrix);
      matrix.scale(particle.sz, particle.sz, particle.sz);
      float r = particle.rgba[0];
      float g = particle.rgba[1];
      float b = particle.rgba[2];
      float a = particle.rgba[3];
      float r2 = particle.rgba[4];
      float g2 = particle.rgba[5];
      float b2 = particle.rgba[6];
      float a2 = particle.rgba[7];
      int j = 240;
      int k = 240;
      RenderType sphereType = ((MahoujinRenderType.createSphereRenderType(beam, 1)));
      RenderType sphereType2 = ((MahoujinRenderType.createSphereRenderType(beam, 2)));
      float realage = partialTicks + particle.age;
      float progress = realage / particle.lifetime;
      float realRadius = particle.lastRadius + partialTicks * (particle.radius - particle.lastRadius);
      RenderUtils.renderSphere(matrix, buf, realRadius * (0.9F + 0.07F * progress), particle.quality, j, k, r2, g2, b2, a2, sphereType2);
      RenderUtils.renderSphere(matrix, buf, realRadius, particle.quality, j, k, r, g, b, 0.2F, sphereType);
      matrix.popPose();
   }

   public static float calculateSize(float progress) {
      float p = progress * 2.2F;
      return (float)Math.max(0.0, -1.0 * Math.pow(p - 1.2F, 12.0) + 3.0);
   }
}
