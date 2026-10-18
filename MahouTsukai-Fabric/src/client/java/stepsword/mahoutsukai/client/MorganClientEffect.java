package stepsword.mahoutsukai.client;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.particle.ModParticles;
import stepsword.mahoutsukai.util.EffectUtil;

public class MorganClientEffect {
   public static void morganParticlesLivingUpdate(LivingEntity player) {
      if (player.level().isClientSide() && EffectUtil.hasBuff(player, ModEffects.RAGE)) {
         Vec3 v = player.getEyePosition(1.0F);
         RandomSource rand = player.getRandom();
         Vec3 q = new Vec3(1.0, 0.0, 0.0);
         float yaw = Math.toRadians(rand.nextInt(360));
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.RED_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
         q = new Vec3(1.0, 0.0, 0.0);
         yaw = rand.nextInt(360);
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.RED_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
         q = new Vec3(1.0, 0.0, 0.0);
         yaw = rand.nextInt(360);
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.RED_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
      }
   }
}
