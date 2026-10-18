package stepsword.mahoutsukai.client;

import java.util.List;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import stepsword.mahoutsukai.networking.AuthorityAttackPacket;
import stepsword.mahoutsukai.networking.AuthorityJumpPacket;
import stepsword.mahoutsukai.networking.AuthorityStickPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.potion.WallStickPotion;
import stepsword.mahoutsukai.render.particle.ModParticles;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;

public class BorrowedAuthorityClientEffect {
   public static long heldDown = 0L;
   public static boolean startedInAir = false;
   public static boolean holdingLeftClick = false;

   public static void borrowedAuthorityParticlesLivingUpdate(LivingEntity player) {
      if (player.level().isClientSide() && EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY)) {
         Vec3 v = player.getEyePosition(1.0F);
         RandomSource rand = player.getRandom();
         Vec3 q = new Vec3(1.0, 0.0, 0.0);
         float yaw = Math.toRadians(rand.nextInt(360));
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.YELLOW_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
         q = new Vec3(1.0, 0.0, 0.0);
         yaw = rand.nextInt(360);
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.YELLOW_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
         q = new Vec3(1.0, 0.0, 0.0);
         yaw = rand.nextInt(360);
         q = q.yRot(yaw).scale(-0.5 + rand.nextFloat() * 0.1).add(0.0, -rand.nextFloat(), 0.0);
         v = v.add(q);
         player.level().addParticle((ParticleOptions)ModParticles.YELLOW_LIGHTNING.get(), v.x, v.y, v.z, yaw, 0.0, 0.0);
      }
   }

   public static void borrowedAuthorityJumpEffect(Player player, ClientInput event) {
      boolean holdingSpacebar = event.keyPresses.jump();
      boolean borrowedAuthority = EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY);
      if (holdingSpacebar && borrowedAuthority) {
         if (player.onGround()) {
            if (heldDown == 0L) {
               startedInAir = false;
            }
         } else if (heldDown == 0L) {
            startedInAir = true;
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new AuthorityStickPacket(true));
         } else if (startedInAir && WallStickPotion.adjacentWall(player.blockPosition(), player.level())) {
            player.setDeltaMovement(0.0, 0.003 * player.getDeltaMovement().y, 0.0);
         }

         heldDown++;
         event.keyPresses = new Input(
            event.keyPresses.forward(),
            event.keyPresses.backward(),
            event.keyPresses.left(),
            event.keyPresses.right(),
            false,
            event.keyPresses.shift(),
            event.keyPresses.sprint()
         );
         event.moveVector = Vec2.ZERO;
      }

      if (!holdingSpacebar && heldDown > 0L) {
         if (borrowedAuthority) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new AuthorityJumpPacket(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, heldDown));
         }

         net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new AuthorityStickPacket(false));
         heldDown = 0L;
      }
   }

   public static void borrowedAuthorityJumpingPunch(LivingEntity entity) {
      if (entity instanceof Player && entity.level().isClientSide()) {
         Player player = (Player)entity;
         if (holdingLeftClick && EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY)) {
            List<Entity> lst = player.level().getEntities(player, player.getBoundingBox().inflate(1.5), a -> a instanceof LivingEntity && a != player);
            boolean hit = false;

            for (Entity e : lst) {
               if (!ContractMahoujinTileEntity.isImmuneToSpell(player.level(), player.getUUID(), e)) {
                  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new AuthorityAttackPacket(e));
                  hit = true;
               }
            }

            if (hit) {
               player.setDeltaMovement(0.003 * player.getDeltaMovement().x, 0.003 * player.getDeltaMovement().y, 0.003 * player.getDeltaMovement().z);
            }
         }
      }
   }

}
