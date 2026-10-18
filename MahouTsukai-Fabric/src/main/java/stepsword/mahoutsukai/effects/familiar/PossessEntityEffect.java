package stepsword.mahoutsukai.effects.familiar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.networking.DonePossessingPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.PossessEntityPacket;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.mana.PlayerManaManager;

public class PossessEntityEffect {
   public static boolean possessLookingAt(Player caster) {
      Entity e = Utils.selectEntityNearCursor(
         caster,
         MTConfig.GLOBAL_LOOK_RANGE,
         caster.level(),
         a -> Utils.getPredicate(Utils.MAHOU_SELECTION.SCROLL_POSSESS_ENTITY, a, caster),
         Utils.nearAllowed(Utils.MAHOU_SELECTION.SCROLL_POSSESS_ENTITY)
      );
      return !(e instanceof LivingEntity) ? false : possessEntity(caster, e);
   }

   public static boolean possessEntity(Player player, Entity target) {
      if (target instanceof LivingEntity && target!=player && player.level()==target.level() && target.isAlive()) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null) {
            ServerLevel ws = (ServerLevel)player.level();
            ChunkPos chunk = ChunkPos.containing(target.blockPosition());
            if (ws.getChunkSource().hasChunk(chunk.x(), chunk.z())) {
               if (target instanceof Creeper) {
                  MandatoryFun.completeFunReq(player, MandatoryFun.FUN_REQS.CREEPER_POSSESSION);
               }

               mahou.setPossessing(true);
               PlayerManaManager.updateClientMahou((ServerPlayer)player,mahou);
               PacketHandler.sendTo((ServerPlayer)player, new PossessEntityPacket(chunk.x(), chunk.z(), target.getId()));
               return true;
            }
         }
      }

      return false;
   }

   public static boolean possessEntityServer(Player player) {
      boolean ret = false;
      if (player != null) {
         ret = true;
         DonePossessingPacket.handleDonePossess(null, player);
      }

      return ret;
   }
}
