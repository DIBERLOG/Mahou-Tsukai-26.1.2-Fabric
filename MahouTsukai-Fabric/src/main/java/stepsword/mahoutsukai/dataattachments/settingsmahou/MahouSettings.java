package stepsword.mahoutsukai.dataattachments.settingsmahou;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.SettingsMahouPacket;
import stepsword.mahoutsukai.util.Utils;

public class MahouSettings {
   public static ConcurrentHashMap<Integer, List<Integer>> intsettings;
   public static HashSet<MahouSettings.Spell> primaryColor = new HashSet<>(
      Arrays.asList(
         MahouSettings.Spell.RHO_AIAS,
         MahouSettings.Spell.GANDR,
         MahouSettings.Spell.TREASURY_WEAPONS,
         MahouSettings.Spell.TREASURY_CHAINS,
         MahouSettings.Spell.MORGAN,
         MahouSettings.Spell.MORGAN_BALL,
         MahouSettings.Spell.MYSTIC_STAFF_BIG,
         MahouSettings.Spell.MYSTIC_STAFF_BEAM,
         MahouSettings.Spell.MYSTIC_STAFF_AOE,
         MahouSettings.Spell.WEAPON_PROJECTILE,
         MahouSettings.Spell.NOBU,
         MahouSettings.Spell.WILLIAM_BOOK,
         MahouSettings.Spell.WILLIAM_MAGIC,
         MahouSettings.Spell.RLYEH_BOOK,
         MahouSettings.Spell.RLYEH_MAGIC
      )
   );
   public static HashSet<MahouSettings.Spell> secondaryColor = new HashSet<>(
      Arrays.asList(
         MahouSettings.Spell.RHO_AIAS,
         MahouSettings.Spell.GANDR,
         MahouSettings.Spell.MORGAN_BALL,
         MahouSettings.Spell.WEAPON_PROJECTILE,
         MahouSettings.Spell.NOBU,
         MahouSettings.Spell.WILLIAM_BOOK,
         MahouSettings.Spell.WILLIAM_MAGIC,
         MahouSettings.Spell.RLYEH_BOOK,
         MahouSettings.Spell.RLYEH_MAGIC
      )
   );

   public static ConcurrentHashMap<Integer, List<Integer>> getIntDefaults() {
      intsettings = new ConcurrentHashMap<>();
      intsettings.put(MahouSettings.Spell.RHO_AIAS.ordinal(), Arrays.asList(255, 94, 204, 255, 94, 204));
      intsettings.put(MahouSettings.Spell.GANDR.ordinal(), Arrays.asList(216, 0, 0, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.TREASURY_WEAPONS.ordinal(), Arrays.asList(255, 255, 150, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.TREASURY_CHAINS.ordinal(), Arrays.asList(244, 237, 64, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.MORGAN.ordinal(), Arrays.asList(255, 50, 50, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.MORGAN_BALL.ordinal(), Arrays.asList(150, 0, 0, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.MYSTIC_STAFF_BIG.ordinal(), Arrays.asList(224, 64, 0, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.MYSTIC_STAFF_AOE.ordinal(), Arrays.asList(166, 125, 224, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.MYSTIC_STAFF_BEAM.ordinal(), Arrays.asList(140, 82, 191, 0, 0, 0));
      intsettings.put(MahouSettings.Spell.WEAPON_PROJECTILE.ordinal(), Arrays.asList(179, 255, 255, 179, 255, 255));
      intsettings.put(MahouSettings.Spell.NOBU.ordinal(), Arrays.asList(255, 255, 179, 255, 255, 255));
      intsettings.put(MahouSettings.Spell.WILLIAM_BOOK.ordinal(), Arrays.asList(69, 5, 171, 196, 170, 149));
      intsettings.put(MahouSettings.Spell.WILLIAM_MAGIC.ordinal(), Arrays.asList(100, 49, 181, 173, 122, 255));
      intsettings.put(MahouSettings.Spell.RLYEH_BOOK.ordinal(), Arrays.asList(0, 51, 64, 90, 90, 90));
      intsettings.put(MahouSettings.Spell.RLYEH_MAGIC.ordinal(), Arrays.asList(100, 255, 255, 100, 255, 255));
      return intsettings;
   }

   public static boolean hasPrimary(MahouSettings.Spell spell) {
      return primaryColor.contains(spell);
   }

   public static boolean hasSecondary(MahouSettings.Spell spell) {
      return secondaryColor.contains(spell);
   }

   public static List<Integer> getIntSettings(Player player, MahouSettings.Spell spell) {
      ISettingsMahou settings = Utils.getSettingsMahou(player);
      if (settings != null) {
         try {
            List<Integer> ret = settings.getIntSettings(spell.ordinal());
            List<Integer> defaults = getIntDefaults().get(spell.ordinal());
            if (ret.size() < defaults.size()) {
               for (int i = ret.size(); i < defaults.size(); i++) {
                  ret.add(defaults.get(i));
               }
            }

            return ret;
         } catch (Exception var6) {
         }
      }

      return getIntDefaults().get(spell.ordinal());
   }

   public static void updateClientMahou(ServerPlayer player) {
      if (player != null) {
         ISettingsMahou mahou = Utils.getSettingsMahou(player);
         if (mahou != null) {
            PacketHandler.sendTo(player, new SettingsMahouPacket(mahou));
         }
      }
   }

   public enum Spell {
      RHO_AIAS,
      GANDR,
      TREASURY_WEAPONS,
      TREASURY_CHAINS,
      MORGAN,
      MORGAN_BALL,
      MYSTIC_STAFF_BIG,
      MYSTIC_STAFF_BEAM,
      MYSTIC_STAFF_AOE,
      WEAPON_PROJECTILE,
      NOBU,
      WILLIAM_BOOK,
      WILLIAM_MAGIC,
      RLYEH_BOOK,
      RLYEH_MAGIC;
   }
}
