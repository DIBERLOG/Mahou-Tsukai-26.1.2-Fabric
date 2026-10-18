package stepsword.mahoutsukai.mana;

import com.google.common.collect.HashMultimap;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.util.Utils;

public class ActiveCircleConfig {
   public static HashMap<String, Integer> limitationMap = null;
   public static HashMap<UUID, HashMultimap<String, ActiveCircleConfig.ActiveMahoujin>> currentActiveMap = null;

   public static void clearActiveCircles() {
      currentActiveMap = null;
   }

   public static boolean tryToOperate(@NotNull BlockEntity be, @NotNull UUID casterUUID) {
      if (!isLimited()) {
         return true;
      }

      buildLimitationMap();
      String name = Utils.getRegistryName(be.getBlockState().getBlock(), be.getLevel());
      if (limitationMap.containsKey(name)) {
         if (currentActiveMap == null) {
            currentActiveMap = new HashMap<>();
         }

         HashMultimap<String, ActiveCircleConfig.ActiveMahoujin> castermap;
         if (currentActiveMap.containsKey(casterUUID)) {
            castermap = currentActiveMap.get(casterUUID);
         } else {
            castermap = HashMultimap.create();
            currentActiveMap.put(casterUUID, castermap);
         }

         Set<ActiveCircleConfig.ActiveMahoujin> activeCircles = castermap.get(name);
         boolean found = false;
         long now = be.getLevel().getGameTime();
         int TICKS_TO_BE_REPLACED = MTConfig.ACTIVE_MAGIC_CIRCLE_REPLACEMENT_TIME;
         long oldest = Long.MAX_VALUE;
         ActiveCircleConfig.ActiveMahoujin oldestMahoujin = null;

         for (ActiveCircleConfig.ActiveMahoujin active : activeCircles) {
            if (active.pos.equals(be.getBlockPos())) {
               active.lastActive = now;
               found = true;
            }

            if (active.lastActive < oldest) {
               oldest = active.lastActive;
               oldestMahoujin = active;
            }
         }

         if (!found) {
            if (activeCircles.size() < limitationMap.get(name)) {
               ActiveCircleConfig.ActiveMahoujin active = new ActiveCircleConfig.ActiveMahoujin();
               active.pos = be.getBlockPos();
               active.lastActive = now;
               activeCircles.add(active);
               return true;
            }

            if (oldestMahoujin != null) {
               if (TICKS_TO_BE_REPLACED < now - oldestMahoujin.lastActive) {
                  activeCircles.remove(oldestMahoujin);
               }

               if (activeCircles.size() < limitationMap.get(name)) {
                  ActiveCircleConfig.ActiveMahoujin active = new ActiveCircleConfig.ActiveMahoujin();
                  active.pos = be.getBlockPos();
                  active.lastActive = now;
                  activeCircles.add(active);
                  return true;
               }

               return false;
            }

            return false;
         }
      }

      return true;
   }

   public static boolean isLimited() {
      return !MTConfig.ACTIVE_MAGIC_CIRCLE_LIMITS.isEmpty() && !MTConfig.ACTIVE_MAGIC_CIRCLE_NAMES.isEmpty();
   }

   public static void buildLimitationMap() {
      if (limitationMap == null || limitationMap.size() != MTConfig.ACTIVE_MAGIC_CIRCLE_NAMES.size()) {
         int top = Math.min(MTConfig.ACTIVE_MAGIC_CIRCLE_LIMITS.size(), MTConfig.ACTIVE_MAGIC_CIRCLE_NAMES.size());
         limitationMap = new HashMap<>();

         for (int m = 0; m < top; m++) {
            limitationMap.put(MTConfig.ACTIVE_MAGIC_CIRCLE_NAMES.get(m), MTConfig.ACTIVE_MAGIC_CIRCLE_LIMITS.get(m));
         }
      }
   }

   public static class ActiveMahoujin {
      public BlockPos pos = null;
      public long lastActive = -1L;
   }
}
