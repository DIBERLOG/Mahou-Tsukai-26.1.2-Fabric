package stepsword.mahoutsukai.util;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;

public class PlayerHelp {
   public static void sendHelpMessage(Player player, PlayerHelp.Message message) {
      String help = "mahoutsukai.help." + message.toString().toLowerCase(Locale.ENGLISH);
      player.sendOverlayMessage(Component.translatable(help));
   }

   public static void sendHelpMessageReplaced(Player player, PlayerHelp.Message message, int... values) {
      int q = 0;
      String help = "mahoutsukai.help." + message.toString().toLowerCase(Locale.ENGLISH);
      help = Component.translatable(help).getString();

      for (int a : values) {
         help = help.replace("%k" + q, a + "");
         q++;
      }

      player.sendOverlayMessage(Component.literal(help));
   }

   public static void sendHelpMessageNearby(BlockPos pos, Level level, PlayerHelp.Message message) {
      AABB aabb = new AABB(pos.offset(-10, -10, -10).getCenter(), pos.offset(10, 10, 10).getCenter());

      for (Player player : level.players()) {
         IMahou mahou = Utils.getPlayerMahou(player);
         if (mahou != null && mahou.hasMagic() && aabb.contains(player.getX(), player.getY(), player.getZ())) {
            sendHelpMessage(player, message);
         }
      }
   }

   public enum Message {
      NOT_ENOUGH_MANA,
      NOT_ENOUGH_FUN,
      NO_FUN_NO_SPACE,
      NOBU_FURTHER_AWAY,
      CIRCLE_TOO_MANY,
      CALIBURN_NOT_ENCHANTED,
      CALIBURN_NO_OWNER,
      CALIBURN_LAKE_TOO_SMALL,
      CALIBURN_SWORD_BANNED,
      CALIBURN_INTERNAL_ERROR,
      PROJECTION_NOT_ALLOWED,
      PROJECTION_NO_UNBREAKABLE,
      REPLICA_NOT_ENOUGH_SOULS,
      REPLICA_NOT_ENOUGH_DAMAGE_ABSORPTION,
      REPLICA_NOT_ENOUGH_ARMOR,
      REPLICA_WRONG_HAND,
      REPLICA_NO_IMMUNITY_EXCHANGE,
      REPLICA_NOT_STRENGTHENED,
      RHONGOMYNIAD_NOT_ENCHANTED,
      RHONGOMYNIAD_NO_OWNER,
      RHONGOMYNIAD_LANCE_BANNED,
      TOO_MANY_SOULS,
      FICTION_RECREATED,
      FICTION_CREATED,
      FICTION_NOT_YOURS,
      FICTION_NOT_ENOUGH_MANA,
      THEATER_OFF,
      THEATER_ON;
   }
}
