package stepsword.mahoutsukai.entity.butterfly;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.entity.FakePlayer;

public class SafeFakePlayer extends FakePlayer {
   static SafeFakePlayer faker = null;
   int mana = 0;
   public static HashMap<UUID, SafeFakePlayer> map = new HashMap<>();
   static String MAHOU_SAFE_FAKE_PLAYER_ID = "c2e7e53b-dc66-4561-8a13-cb974b4c162a";

   public SafeFakePlayer(ServerLevel world, String name) {
      super(world, new GameProfile(UUID.fromString(MAHOU_SAFE_FAKE_PLAYER_ID), name));
   }

   public SafeFakePlayer(ServerLevel world, String name, UUID uuid) {
      super(world, new GameProfile(uuid, name));
   }

   public Vec3 position() {
      return new Vec3(this.getX(), this.getY(), this.getZ());
   }

   public void setMana(int m) {
      this.mana = m;
   }

   public int getMana() {
      return this.mana;
   }

   public static SafeFakePlayer createFakePlayerIfNull(ServerLevel world) {
      if (faker == null || faker.level() != world) {
         faker = new SafeFakePlayer(world, "faker");
      }

      return faker;
   }

   public static SafeFakePlayer createFakePlayerIfNotExists(ServerLevel level, String name, UUID uuid) {
      if (map == null) {
         map = new HashMap<>();
      }

      SafeFakePlayer ret = map.getOrDefault(uuid, null);
      if (ret == null || ret.level() != level) {
         ret = new SafeFakePlayer(level, name, uuid);
      }

      map.put(uuid, ret);
      return ret;
   }
}
