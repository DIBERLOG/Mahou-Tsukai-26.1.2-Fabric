package stepsword.mahoutsukai.fabric;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket;
import stepsword.mahoutsukai.render.particle.ParticleMagic;
import stepsword.mahoutsukai.render.particle.ParticleSmokeRing;
public final class GandrClientParticles {
   public static void magicParticleHandler(MagicCircleBoomParticlePacket message) {
      Vec3 look = new Vec3(message.lookX, message.lookY, message.lookZ).normalize();
      if (!message.big && !message.randomspeed && message.boom_mode == 0) {
         spawnGandrLaunchSmokeRing(message, look);
      } else {
         Vec3 base = look.xRot(EffectUtil.toRad(90.0F)).normalize();
         int particleCount = Math.min(message.particles, 25);

         for (int i = 0; i < particleCount; i++) {
            Vec3 v = base;
            double angle = Math.random() * 360.0;
            if (message.boom_mode == 0) {
               v = rotateVecAroundVec(v, look, (float)angle);
            } else if (message.boom_mode == 1) {
               v = Vec3.directionFromRotation((float)angle, (float)Math.random() * 360.0F).normalize();
            }

            if (message.randomspeed) {
               v = v.scale(Math.random() * message.speed);
            } else {
               v = v.scale(message.speed);
            }

            float r;
            float g;
            float b;
            if (message.color == 1) {
               r = message.r;
               g = message.g;
               b = message.b;
            } else {
               r = message.r2;
               g = message.g2;
               b = message.b2;
            }

            float sz;
            if (message.big) {
               sz = 1.2F;
            } else {
               sz = 0.2F;
            }

            ParticleMagic p = new ParticleMagic(
               Minecraft.getInstance().level,
               message.posX,
               message.posY,
               message.posZ,
               0.5F,
               v.x,
               v.y,
               v.z,
               "textures/particle/white_magic_particle",
               sz,
               r,
               g,
               b,
               1.0F,
               true
            );
            Minecraft.getInstance().particleEngine.add(p);
         }
      }
   }
   private static void spawnGandrLaunchSmokeRing(MagicCircleBoomParticlePacket message, Vec3 look) {
      if (message.color == 1) {
         float r = message.color == 1 ? message.r : message.r2;
         float g = message.color == 1 ? message.g : message.g2;
         float b = message.color == 1 ? message.b : message.b2;

         for (int i = 0; i < 2; i++) {
            ParticleSmokeRing smoke = new ParticleSmokeRing(
               Minecraft.getInstance().level,
               new Color(r, g, b),
               message.posX + look.x * i * 0.16,
               message.posY + look.y * i * 0.16,
               message.posZ + look.z * i * 0.16
            );
            smoke.yaw = (float)Math.toDegrees(Math.atan2(look.x, look.z));
            smoke.pitch = (float)Math.toDegrees(Math.acos(look.y));
            smoke.delay = i * 4;
            Minecraft.getInstance().particleEngine.add(smoke);
         }
      }
   }
   public static Vec3 rotateVecAroundVec(Vec3 v1, Vec3 v2, float angle) {
      Vec3 z = v1.cross(v2).normalize();
      return v1.scale(Math.cos(EffectUtil.toRad(angle))).add(z.scale(Math.sin(EffectUtil.toRad(angle)))).normalize();
   }
}
