package stepsword.mahoutsukai.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FearPotion extends ScrollPotion {
   protected FearPotion() {
      super(ModEffects.getColorNumber(255, 255, 240));
   }

   public static void fearLivingUpdate(LivingEntity entity) {
      if (entity instanceof PathfinderMob && !entity.level().isClientSide()) {
         ILivingMahou mahou = Utils.getLivingMahou(entity);
         if (mahou != null && EffectUtil.hasBuff(entity, ModEffects.FEAR) && mahou.getFearPerson() != null) {
            Player player = entity.level().getPlayerByUUID(mahou.getFearPerson());
            if (player != null) {
               Vec3 runThisWay = entity.position().subtract(player.position());
               PathfinderMob creature = (PathfinderMob)entity;
               Vec3 go = creature.position()
                  .add(runThisWay.normalize().scale(3.0))
                  .add(entity.getRandom().nextFloat() * 0.2, entity.getRandom().nextFloat() * 0.2, entity.getRandom().nextFloat() * 0.2);
               creature.setTarget(null);
               creature.setLastHurtByMob(null);
               creature.getMoveControl().setWantedPosition(go.x, go.y, go.z, 1.75);
            }
         }
      }
   }

   public static boolean fearLivingSetAttack(LivingEntity entity, LivingEntity target) {
      if (!entity.level().isClientSide() && entity instanceof Mob && EffectUtil.hasBuff(entity, ModEffects.FEAR) && target != null) {
         ((Mob)entity).setTarget(null);
         entity.setLastHurtByMob(null);
      }

      return EffectUtil.hasBuff(entity, ModEffects.FEAR);
   }

   public static void fearPlayerTick(Player player) {
      if (player.getMainHandItem().getItem() == ModItems.caliburn.get()) {
         int radius = MTConfig.POWER_CONSOLIDATION_FEAR_RADIUS;
         AABB aabb = new AABB(
            Utils.toBlockPos(player.position().add(-radius, -4.0, -radius)).getCenter(),
            Utils.toBlockPos(player.position().add(radius, 4.0, radius)).getCenter()
         );

         for (PathfinderMob entity : player.level().getEntitiesOfClass(PathfinderMob.class, aabb)) {
            if (Caliburn.specialTarget(entity)) {
               ILivingMahou mahou = Utils.getLivingMahou(entity);
               if (mahou != null) {
                  mahou.setFearPerson(player.getUUID());
                  EffectUtil.buff(entity, ModEffects.FEAR, false, 100);
               }
            }
         }
      }
   }
}
