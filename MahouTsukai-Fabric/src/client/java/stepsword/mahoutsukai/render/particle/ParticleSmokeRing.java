package stepsword.mahoutsukai.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class ParticleSmokeRing extends Particle {
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float alpha = 1.0F;
   protected float roll;
   protected float oRoll;
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   public static final RenderType RING_TYPE = blend(
      MahoujinRenderType.createSmokeRingRenderType(beam, 1)
   );
   private static RenderType blend(RenderType type){return stepsword.mahoutsukai.render.MahouWrappedRenderTypes.wrapBlend(type,"gandr_smoke_ring");}
   public static final java.util.Set<ParticleSmokeRing> active=java.util.concurrent.ConcurrentHashMap.newKeySet();
   public String loc;
   public float yaw = 0.0F;
   public float pitch = 0.0F;
   public float[] rgba = new float[4];
   public float rotation = 0.0F;
   public float sz = 0.2F;
   public boolean shaders = false;
   public float speed = 0.0F;
   public float radius = 0.0F;
   public float startingRadius = 0.0F;
   public float heightControl = 1.0F;
   public int quality = MTConfig.SMOKE_RING_PARTICLE_QUALITY;
   public float earlyDeath = 1.0E-4F;
   public int delay = 0;
   public float alpha_step = 15.0F;
   public boolean holdSizeAtPeak = false;
   public int trackingEntityId = -1;
   public double trackingYOffset;

   public ParticleSmokeRing(ClientLevel worldIn, Color color, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);active.add(this);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.gCol = 1.0F;
      this.rCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 0.4F;
      float maxAge = 400.0F;
      this.lifetime = (int)(maxAge / (Math.random() * 0.3 + 0.6));
      this.lifetime = (int)this.lifetime;
      this.rgba[0] = color.getRed() / 255.0F;
      this.rgba[1] = color.getGreen() / 255.0F;
      this.rgba[2] = color.getBlue() / 255.0F;
      this.rgba[3] = 1.0F;
   }

   public void setLifetime(int life) {
      this.lifetime = life;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.trackingEntityId >= 0) {
         Entity tracked = this.level.getEntity(this.trackingEntityId);
         if (tracked != null) {
            this.x = tracked.getX();
            this.y = tracked.getY() + this.trackingYOffset;
            this.z = tracked.getZ();
         }
      }

      if (this.delay > 0) {
         this.delay--;
      } else {
         if (this.age++ >= this.lifetime) {
            this.remove();
         }

         this.x = this.x + this.xd;
         this.y = this.y + this.yd;
         this.z = this.z + this.zd;
         this.oRoll = this.roll;
      }
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.NO_RENDER;
   }

   public static void renderSmokeRing(ParticleSmokeRing particle, double d3, double d4, double d5, PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {
      double d0 = particle.xo + (particle.x - particle.xo) * partialTicks;
      double d1 = particle.yo + (particle.y - particle.yo) * partialTicks;
      double d2 = particle.zo + (particle.z - particle.zo) * partialTicks;
      if (particle.delay <= 0) {
         matrix.pushPose();
         matrix.translate(d0 - d3, d1 - d4, d2 - d5);
         RenderUtils.rotateQ(particle.yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(particle.pitch, 1.0F, 0.0F, 0.0F, matrix);
         matrix.scale(particle.sz, particle.sz, particle.sz);
         float r = particle.rgba[0];
         float g = particle.rgba[1];
         float b = particle.rgba[2];
         float a = 1.0F - (float)particle.age / particle.lifetime;
         particle.rgba[3] = a;
         int j = 240;
         int k = 240;

         float realage = partialTicks + particle.age;
         float progress = realage / particle.lifetime;
         float alpha = Math.max(0.0F, Math.min(1.0F, 1.0F - progress * particle.alpha_step));
         float sizeProgress = particle.holdSizeAtPeak ? Math.min(progress, 0.25F) : progress;
         float radius = particle.startingRadius + calculateSize(sizeProgress, particle.heightControl);
         float height = 0.4F * particle.heightControl + 0.7F * progress;
         if (alpha <= particle.earlyDeath) {
            particle.age = particle.lifetime;
         }

         RenderUtils.renderRing(matrix, buf, 0.0, 0.0F, radius, height, particle.quality, j, k, r, g, b, alpha, RING_TYPE, true);
         matrix.popPose();
      }
   }

   public static float calculateSize(float progress, float hc) {
      return 100.0F * hc * progress - progress * progress * 200.0F * hc;
   }
}
