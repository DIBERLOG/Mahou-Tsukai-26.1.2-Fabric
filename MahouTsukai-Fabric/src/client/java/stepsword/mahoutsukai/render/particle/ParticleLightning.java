package stepsword.mahoutsukai.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.Utils;

public class ParticleLightning extends Particle {
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float roll;
   protected float oRoll;
   private static final Identifier beam = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   public Color color;
   public Color color2;
   public int[] branch = new int[3];
   public int[] angles;
   public int[] angles2;
   public float[] sizes;
   public float[] widths;
   public int fullrotate;
   public int yawrot;
   int life;
   private static final RenderType LIGHTNING_RING_TYPE = (MahoujinRenderType.createLightningRenderType(beam, 0));
   private static final RenderType LIGHTNING_RING_TYPE_2 = (MahoujinRenderType.createLightningRenderType(beam, 1));
   private static final RenderType BRANCH_RING_TYPE = (
      MahoujinRenderType.createScopedRunesRenderType("particle_lightning_branch_inner", beam, 1)
   );
   private static final RenderType BRANCH_RING_TYPE_2 = (
      MahoujinRenderType.createScopedRunesRenderType("particle_lightning_branch_outer", beam, 4)
   );

   public ParticleLightning(
      ClientLevel worldIn, Color c, Color c2, double xCoordIn, double yCoordIn, double zCoordIn, float scale, double motionX, double motionY, double motionZ
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.gCol = 0.7137255F;
      this.rCol = 1.0F;
      this.bCol = 0.75686276F;
      float maxAge = 180.0F;
      this.lifetime = (int)(maxAge / (Math.random() * 0.3 + 0.6));
      this.lifetime = (int)(this.lifetime * scale);
      net.minecraft.util.RandomSource random = this.level.getRandom();
      this.roll = random.nextInt(360) * 0.0174533F;
      this.oRoll = this.roll;
      this.color = c;
      this.color2 = c2;
      this.branch[0] = random.nextInt(2) + 1;
      this.branch[1] = random.nextInt(2) + 1;
      this.branch[2] = random.nextInt(3) + 1;
      int r = this.branch.length;

      for (int j = 0; j < this.branch.length; j++) {
         r *= this.branch[j];
      }

      this.angles = new int[r];
      this.angles2 = new int[r];
      this.sizes = new float[r];
      this.widths = new float[r];
      this.sizes[0] = 0.14F;
      this.widths[0] = 0.01F;

      for (int k = 0; k < r; k++) {
         this.angles[k] = random.nextInt(360);
         this.angles2[k] = random.nextInt(50) + 20;
         if (k > 0) {
            this.sizes[k] = (0.7F + random.nextFloat() * 0.2F) * this.sizes[k - 1];
         }

         if (k > 0) {
            this.widths[k] = 1.0F * this.widths[k - 1];
         }
      }

      this.life = random.nextInt(1);
      this.fullrotate = random.nextInt(20) + 80;
      this.fullrotate = random.nextBoolean() ? -this.fullrotate : this.fullrotate;
      this.yawrot = (int)motionX;
   }

   public void tick() {
      this.age++;
      if (this.age > this.life * this.branch.length + 3) {
         this.remove();
      }
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.NO_RENDER;
   }

   public static void renderLightningParticle(ParticleLightning particle, double d3, double d4, double d5, PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {
      double d0 = particle.xo + (particle.x - particle.xo) * partialTicks;
      double d1 = particle.yo + (particle.y - particle.yo) * partialTicks;
      double d2 = particle.zo + (particle.z - particle.zo) * partialTicks;
      matrix.pushPose();
      matrix.translate(d0 - d3, d1 - d4, d2 - d5);
      float r = particle.color.getRed() / 255.0F;
      float g = particle.color.getGreen() / 255.0F;
      float b = particle.color.getBlue() / 255.0F;
      float r2 = particle.color2.getRed() / 255.0F;
      float g2 = particle.color2.getGreen() / 255.0F;
      float b2 = particle.color2.getBlue() / 255.0F;
      float a = 0.3F;
      float w = particle.widths[0];
      float h = particle.sizes[0];
      RenderUtils.rotateQ(-particle.yawrot, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(particle.fullrotate, 0.0F, 0.0F, 1.0F, matrix);

      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w, h, 6, 240, 240, r, g, b, 1.0F, LIGHTNING_RING_TYPE);
      recursiveLightning(matrix, buf, particle, 0, 0);
      RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w * 1.8F, h, 6, 240, 240, r2, g2, b2, a, LIGHTNING_RING_TYPE_2);
      matrix.popPose();
   }

   public static int recursiveLightning(PoseStack matrix, net.minecraft.client.renderer.SubmitNodeCollector buf, ParticleLightning particle, int branchindex, int oindex) {
      float r = particle.color.getRed() / 255.0F;
      float g = particle.color.getGreen() / 255.0F;
      float b = particle.color.getBlue() / 255.0F;
      float r2 = particle.color2.getRed() / 255.0F;
      float g2 = particle.color2.getGreen() / 255.0F;
      float b2 = particle.color2.getBlue() / 255.0F;
      float a = 0.3F;
      int index = oindex;
      if (particle.age <= particle.life * branchindex) {
         return index;
      }

      float agefactor = 1.0F;
      if (branchindex < particle.branch.length && index < particle.sizes.length && index < particle.angles.length) {
         float h = particle.sizes[index] * agefactor;
         float w = particle.widths[oindex];
         matrix.translate(0.0F, -h, 0.0F);
         RenderUtils.rotateQ(particle.angles[index], 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(particle.angles2[index], 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w, h, 6, 240, 240, r, g, b, 1.0F, BRANCH_RING_TYPE);
         if (branchindex < particle.branch.length) {
            for (int i = 0; i < particle.branch[branchindex]; i++) {
               oindex = recursiveLightning(matrix, buf, particle, branchindex + 1, oindex + 1);
            }
         }

         RenderUtils.renderRing(matrix, buf, 0.0, 90.0F, w * 1.8F, h, 6, 240, 240, r2, g2, b2, a, BRANCH_RING_TYPE_2);
         RenderUtils.rotateQ(-particle.angles2[index], 1.0F, 0.0F, 0.0F, matrix);
         RenderUtils.rotateQ(-particle.angles[index], 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(0.0F, h, 0.0F);
         return oindex;
      } else {
         return index;
      }
   }
}
