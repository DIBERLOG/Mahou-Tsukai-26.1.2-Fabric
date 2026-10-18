package stepsword.mahoutsukai.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;

public class ParticleSphere extends Particle {
 public static final java.util.Set<ParticleSphere> active=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float alpha = 1.0F;
   protected float roll;
   protected float oRoll;
   public String loc;
   public float[] rgba = new float[4];
   public float softness = 0.01F;
   public float rotation = 0.0F;
   public float sz = 0.2F;
   public double slow = 0.6;
   public double grow = 0.4;
   public boolean shaders = false;
   public int image;

   public ParticleSphere(
      ClientLevel worldIn,
      double xCoordIn,
      double yCoordIn,
      double zCoordIn,
      float scale,
      double vx,
      double vy,
      double vz,
      String loc,
      float sz,
      float r,
      float g,
      float b,
      float a,
      boolean shaders,
      double grow
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, vx, vy, vz);
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      this.gCol = 1.0F;
      this.rCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 0.4F;
      float maxAge = 50.0F + sz * 100.0F;
      this.lifetime = (int)(maxAge / (Math.random() * 0.3 + 0.6));
      this.lifetime = (int)(this.lifetime * scale);
      this.rgba[0] = r;
      this.rgba[1] = g;
      this.rgba[2] = b;
      this.rgba[3] = a;
      this.loc = loc;
      this.sz = sz;
      this.image = this.random.nextInt(6) + 1;
      this.shaders = shaders;
      this.slow = 0.6;
      this.grow = grow;active.add(this);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      }

      if (this.rgba[3] <= 0.0F) {
         this.remove();
      }

      this.x = this.x + this.xd;
      this.y = this.y + this.yd;
      this.z = this.z + this.zd;
      this.xd = this.xd * this.slow;
      this.yd = this.yd * this.slow;
      this.zd = this.zd * this.slow;
      this.rgba[3] = Math.max(0.0F, this.rgba[3] - 0.05F);
      this.sz = (float)(this.sz + this.grow);
      this.oRoll = this.roll;if(!isAlive())active.remove(this);
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.NO_RENDER;
   }

   public static void renderParticleSphere(ParticleSphere particle, double d3, double d4, double d5, PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {
      double d0 = particle.xo + (particle.x - particle.xo) * partialTicks;
      double d1 = particle.yo + (particle.y - particle.yo) * partialTicks;
      double d2 = particle.zo + (particle.z - particle.zo) * partialTicks;
      matrix.pushPose();
      matrix.translate(d0 - d3, d1 - d4, d2 - d5);
      Entity e = Minecraft.getInstance().getCameraEntity();
      if (e != null) {
         float r = particle.rgba[0];
         float g = particle.rgba[1];
         float b = particle.rgba[2];
         float a = particle.rgba[3];

         Identifier loc = Identifier.parse(particle.loc);
         RenderType sphere = MahoujinRenderType.createSphereRenderType(loc,0);
         double size = Math.max(0.0, particle.sz - particle.grow + particle.grow * partialTicks);
         RenderUtils.renderSphere(matrix, buf, (float)size, 20, 240, 240, r, g, b, a, sphere,particle.softness,particle.shaders);

      }

      matrix.popPose();
   }
}
