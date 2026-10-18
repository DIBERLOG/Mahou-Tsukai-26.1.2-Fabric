package stepsword.mahoutsukai.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ParticlePetal extends SingleQuadParticle {
   private final SpriteSet petalSprite;

   protected ParticlePetal(ClientLevel world, double x, double y, double z, float scale, SpriteSet sprite) {
      super(world, x, y, z, sprite.get(RandomSource.create()));
      this.petalSprite = sprite;
      this.xd *= 0.1F;
      this.yd *= 0.1500000014901161;
      this.zd *= 0.1F;
      this.rCol = 1.0F;
      this.gCol = 0.7137255F;
      this.bCol = 0.75686276F;
      this.quadSize *= 0.75F;
      this.quadSize *= scale;
      float maxAge = 180.0F;
      this.lifetime = (int)(maxAge / (Math.random() * 0.3 + 0.6));
      this.lifetime = (int)(this.lifetime * scale);
      RandomSource random = RandomSource.create();
      this.roll = random.nextInt(360) * 0.0174533F;
      this.oRoll = this.roll;
      this.pickSprite(this.petalSprite);
   }

   private void pickSprite(SpriteSet spriteSet) {
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.pickSprite(this.petalSprite);
      if (this.age++ + this.random.nextInt(40) >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
         }

         this.oRoll = this.roll;
         this.roll -= 0.0174533F;
         this.xd *= 0.96F;
         this.yd *= 0.96F;
         this.zd *= 0.96F;
         if (Math.abs(this.yd) < 0.1) {
            this.yd -= 0.05;
         }

         if (this.yd < 0.4) {
            float factor = 0.05F;
            float f8 = this.roll;
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity != null) {
               Vec3 cameraViewDir = cameraEntity.getViewVector(0.0F);
               float f11 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.y;
               this.yd = -factor * f11;
            }
         }

         if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
         }
      }
   }

   protected Layer getLayer() {
      return Layer.TRANSLUCENT;
   }
}
