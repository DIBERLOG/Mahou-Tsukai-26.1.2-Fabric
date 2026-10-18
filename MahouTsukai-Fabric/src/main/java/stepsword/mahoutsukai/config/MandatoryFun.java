package stepsword.mahoutsukai.config;

import java.util.HashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class MandatoryFun {
   public static HashMap<String, Boolean> configReqs = null;

   public static void completeFunReq(Player player, MandatoryFun.FUN_REQS req) {
      if (player instanceof ServerPlayer) {
         IMahou mahou = Utils.getPlayerMahou(player);
         HashMap<String, Boolean> fun = mahou.getMandatoryFun();
         if (fun == null) {
            fun = new HashMap<>();
         }

         fun.put(req.name(), true);
         mahou.setMandatoryFun(fun);
         PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
      }
   }

   public static boolean meetsFunReq(Player player, MandatoryFun.FUN_REQS req) {
      IMahou mahou = Utils.getPlayerMahou(player);
      HashMap<String, Boolean> fun = mahou.getMandatoryFun();
      return fun != null && fun.containsKey(req.name()) && fun.get(req.name());
   }

   public static boolean meetsAllFunReqs(Player player) {
      if (MTConfig.CALIBURN_FUN_REQUIRED && !MTConfig.CALIBURN_MANDATORY_FUN_REQUIREMENTS.isEmpty()) {
         if (configReqs == null) {
            configReqs = getConfigRequirements();
         }

         for (MandatoryFun.FUN_REQS req : MandatoryFun.FUN_REQS.values()) {
            if (configReqs.containsKey(req.name()) && configReqs.get(req.name()) && !meetsFunReq(player, req)) {
               return false;
            }
         }
      }

      return true;
   }

   public static HashMap<String, Boolean> getConfigRequirements() {
      HashMap<String, Boolean> map = new HashMap<>();

      for (MandatoryFun.FUN_REQS req : MandatoryFun.FUN_REQS.values()) {
         if (MTConfig.CALIBURN_MANDATORY_FUN_REQUIREMENTS.contains(req.name())) {
            map.put(req.name(), true);
         } else {
            map.put(req.name(), false);
         }
      }

      return map;
   }

   public enum FUN_REQS {
      EMRYS_HIT_PHANTOM,
      SHOOT_A_SWORD,
      SPATIAL_THROW_SOMETHING,
      BORROWED_AUTHORITY_PUNCH,
      BUTTERFLY_FUTURE_BLOCK_REMOVE,
      GEAS_PICKUP_CANCEL,
      CREEPER_BINDING_EYES,
      CREEPER_POSSESSION,
      NOBU_FIRE_SQUAD,
      SWAP_TWO_THINGS;
   }
}
