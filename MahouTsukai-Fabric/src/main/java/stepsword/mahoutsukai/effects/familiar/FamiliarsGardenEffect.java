package stepsword.mahoutsukai.effects.familiar;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class FamiliarsGardenEffect {
   public static boolean familiarGarden(Player caster) {
      return !caster.level().isClientSide() ? garden(caster) : false;
   }

   public static boolean garden(Player caster) {
      if (!caster.level().isClientSide()) {
         HashMap<UUID, FamiliarEntity> map = SummonFamiliarSpellEffect.familiarMap;
         if (map.containsKey(caster.getUUID())) {
            FamiliarEntity familiar = map.get(caster.getUUID());
            if (familiar != null && familiar.isAlive()) {
               ChunkPos chunk = ChunkPos.containing(familiar.blockPosition());
               ServerLevel ws = (ServerLevel)caster.level();
               if (ws.getChunkSource().getChunk(chunk.x(), chunk.z(), true) != null) {
                  familiar.activateGarden();
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   public static void familiarsGardenLivingUpdate(LivingEntity target) {
      if (EffectUtil.hasBuff(target, ModEffects.PEACEFUL_GARDEN)) {
         garden(target);
      }
   }

   public static void garden(LivingEntity target) {
      if (target instanceof Mob) {
         ((Mob)target).setTarget(null);
         ((Mob)target).setLastHurtByMob(null);
         ((Mob)target).goalSelector.getAvailableGoals().forEach(goal -> {
            if (goal.getGoal() instanceof NearestAttackableTargetGoal) {
               goal.getGoal().stop();
            }

            if (goal.getGoal() instanceof SwellGoal) {
               goal.getGoal().stop();
            }

            if (goal.getGoal().getClass().toString().toLowerCase(Locale.ENGLISH).contains("attack")) {
               goal.getGoal().stop();
            }
         });
      }
   }

   public static boolean familiarsGardenAttackEntity(Entity target, Player player) {
      boolean ret = false;
      if (target instanceof LivingEntity
         && (EffectUtil.hasBuff((LivingEntity)target, ModEffects.PEACEFUL_GARDEN) || EffectUtil.hasBuff(player, ModEffects.PEACEFUL_GARDEN))) {
         ret = true;
      }

      return ret;
   }

   public static boolean familiarsGardenExplosion(LivingEntity e) {
      boolean ret = false;
      if (e != null && EffectUtil.hasBuff(e, ModEffects.PEACEFUL_GARDEN)) {
         ret = true;
      }

      return ret;
   }

   public static boolean familiarsGardenLivingDamage(LivingEntity target, DamageSource src) {
      boolean ret = false;
      if (src != null) {
         Entity attacker = src.getEntity();
         if (attacker instanceof LivingEntity && EffectUtil.hasBuff((LivingEntity)attacker, ModEffects.PEACEFUL_GARDEN)) {
            ret = true;
         }

         if (target != null && EffectUtil.hasBuff(target, ModEffects.PEACEFUL_GARDEN)) {
            ret = true;
         }
      }

      return ret;
   }
}
