package stepsword.mahoutsukai.effects.mystic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.item.rulebreaker.ProbabilityAlterRandom;
import stepsword.mahoutsukai.potion.ProbabilityAlterPotion;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class DamageReplicationSpellEffect {
   public static int range = MTConfig.GLOBAL_LOOK_RANGE;
   private static final ThreadLocal<Set<UUID>> DAMAGE_REPLICATION_CHAIN = ThreadLocal.withInitial(HashSet::new);
   private static final ThreadLocal<Set<UUID>> EFFECT_REPLICATION_CHAIN = ThreadLocal.withInitial(HashSet::new);

   public static boolean damageReplication(Player user) {
      Entity target = Utils.selectEntityNearCursor(
         user,
         MTConfig.GLOBAL_LOOK_RANGE,
         user.level(),
         a -> a instanceof LivingEntity, true
      );
      if (target instanceof LivingEntity) {
         IMahou mahou = Utils.getPlayerMahou(user);
         if (mahou != null) {
            mahou.setDamageReplicationTarget(target.getUUID());
            return true;
         }
      }

      return false;
   }

   public static void damageReplicationLivingDamage(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount) {
      Entity entity = victim;
      if (!entity.level().isClientSide()) {
         Set<UUID> chain = DAMAGE_REPLICATION_CHAIN.get();
         if (!chain.add(entity.getUUID())) {
            return;
         }

         try {
            Entity target = findTarget(entity);
            if (target instanceof LivingEntity && !chain.contains(target.getUUID())) {
               ((LivingEntity)target).hurtServer((net.minecraft.server.level.ServerLevel)victim.level(),source,amount);
            }
         } finally {
            chain.remove(entity.getUUID());
            if (chain.isEmpty()) {
               DAMAGE_REPLICATION_CHAIN.remove();
            }
         }
      }
   }

   public static Entity findTarget(Entity entity) {
      Entity target = null;
      if (!entity.level().isClientSide() && entity instanceof Player player) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            UUID enemy = mahou.getDamageReplicationTarget();
            if (enemy != null) {
               Player enemyplayer = player.level().getPlayerByUUID(enemy);
               if (enemyplayer != null) {
                  target = enemyplayer;
               } else {
                  List<LivingEntity> lst = player.level()
                     .getEntitiesOfClass(
                        LivingEntity.class,
                        new AABB(
                           player.getX() - range / 2,
                           player.getY() - range / 2,
                           player.getZ() - range / 2,
                           player.getX() + range / 2,
                           player.getY() + range / 2,
                           player.getZ() + range / 2
                        )
                     );

                  for (int i = 0; i < lst.size(); i++) {
                     if (enemy.equals(lst.get(i).getUUID())) {
                        target = (Entity)lst.get(i);
                        break;
                     }
                  }
               }
            }
         }
      }

      return target;
   }

   public static void damageReplicationLivingDeath(Entity entity) {
      if (entity instanceof Player && !entity.level().isClientSide()) {
         Player player = (Player)entity;
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null && mahou.getDamageReplicationTarget() != null) {
            mahou.setDamageReplicationTarget(null);
         }
      }
   }

   public static void damageReplicationPotionAdded(LivingEntity victim,net.minecraft.world.effect.MobEffectInstance effect) {
      Entity entity = victim;
      if (!entity.level().isClientSide()) {
         Set<UUID> chain = EFFECT_REPLICATION_CHAIN.get();
         if (!chain.add(entity.getUUID())) {
            return;
         }

         try {
            Entity target = findTarget(entity);
            if (target instanceof LivingEntity livingTarget && !chain.contains(target.getUUID())) {
               EffectUtil.buff(
                  livingTarget, effect.getEffect(), false, effect.getDuration(), effect.getAmplifier()
               );
               if (effect.getEffect().value() instanceof ProbabilityAlterPotion && entity.random instanceof ProbabilityAlterRandom) {
                  int d = ((ProbabilityAlterRandom)entity.random).designation;
                  int m = ((ProbabilityAlterRandom)entity.random).modulus;
                  ProbabilityAlterPotion.createProbabilityAlter(livingTarget, d, m);
               }
            }
         } finally {
            chain.remove(entity.getUUID());
            if (chain.isEmpty()) {
               EFFECT_REPLICATION_CHAIN.remove();
            }
         }
      }
   }
}
