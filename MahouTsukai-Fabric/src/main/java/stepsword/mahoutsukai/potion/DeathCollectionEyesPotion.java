package stepsword.mahoutsukai.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class DeathCollectionEyesPotion extends EyesPotion {
   protected DeathCollectionEyesPotion() {
      super(ModEffects.getColorNumber(50, 168, 82));
   }

   public static boolean deathCollectionEyesLivingDeath(LivingEntity entity, DamageSource source) {
      boolean ret = false;
      if (!entity.level().isClientSide()) {
         if (entity instanceof Player player) {
            IMahou mahou = Utils.getPlayerMahou(player);
            if (mahou != null
               && mahou.getDeathCollectionUsesLeft() >= 1.0F
               && !(source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD))) {
               ret = true;
               mahou.setDeathCollectionUsesLeft(mahou.getDeathCollectionUsesLeft() - 1.0F);
               player.setHealth(Mth.clamp((float)MTConfig.DEATH_COLLECTION_REVIVE_VALUE, 1.0F, player.getMaxHealth()));
            } else {
               notifyNearby(player);
            }
         } else {
            notifyNearby(entity);
         }
      }

      return ret;
   }

   public static void notifyNearby(LivingEntity me) {
      int range = MTConfig.DEATH_COLLECTION_RANGE_FROM_USER;
      AABB aabb = new AABB(me.getX() - range, me.getY() - range, me.getZ() - range, me.getX() + range, me.getY() + range, me.getZ() + range);
      if (me != null && !me.isRemoved()) {
         for (Player p : me.level().getEntitiesOfClass(Player.class, aabb, Entity::isAlive)) {
            if (p != null && p.isAlive() && !p.getUUID().equals(me.getUUID()) && EffectUtil.isLookingAtMe(me, p)) {
               IMahou mahou = Utils.getPlayerMahou(p);
               if (mahou != null && EffectUtil.hasBuff(p, ModEffects.DEATH_COLLECTION_EYES)) {
                  if (me instanceof Player) {
                     mahou.setDeathCollectionUsesLeft((float)(mahou.getDeathCollectionUsesLeft() + MTConfig.DEATH_COLLECTION_SOUL_VALUE_PLAYER));
                  } else {
                     mahou.setDeathCollectionUsesLeft((float)(mahou.getDeathCollectionUsesLeft() + MTConfig.DEATH_COLLECTION_SOUL_VALUE_MOB));
                  }

                  mahou.setDeathCollectionUsesLeft((float)Math.min(mahou.getDeathCollectionUsesLeft(), MTConfig.DEATH_COLLECTION_MAX));
                  p.sendOverlayMessage(
                     Component.literal(ChatFormatting.RED + "Death collected. Total number of souls collected: " + mahou.getDeathCollectionUsesLeft())
                  );
               }
            }
         }
      }
   }
}
