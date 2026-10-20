package stepsword.mahoutsukai.dataattachments.worldsave;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

public class WorldMahouStorage {
   public static final String realityMarblePlayers = "MAHOUTSUKAI_REALITY_MARBLE_PLAYER_LIST";
   public static final String marblePlayerID = "MAHOUTSUKAI_REALITY_MARBLE_PLAYER_ID";
   public static final String marblePlayerSpawnX = "MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_X";
   public static final String marblePlayerSpawnY = "MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Y";
   public static final String marblePlayerSpawnZ = "MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Z";
   public static final String contractPlayers = "MAHOUTSUKAI_CONTRACT_PLAYER_LIST";
   public static final String contractPlayerIDA = "MAHOUTSUKAI_CONTRACT_PLAYER_A";
   public static final String contractPlayerIDB = "MAHOUTSUKAI_CONTRACT_PLAYER_B";

   public static CompoundTag writeNBT(MahouSavedData instance) {
      CompoundTag compound = new CompoundTag();
      if (instance.freshMarbleArenaSequence > 0) compound.putLong("MAHOUTSUKAI_FRESH_MARBLE_ARENA", instance.freshMarbleArenaSequence);
      CompoundTag reality = new CompoundTag();
      int z = 0;

      for (UUID tmp : instance.getAllSpawns().keySet()) {
         BlockPos pos = instance.getAllSpawns().get(tmp);
         if (pos != null) {
            CompoundTag nbttmp = new CompoundTag();
            nbttmp.store("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_ID", UUIDUtil.CODEC, tmp);
            nbttmp.putInt("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_X", pos.getX());
            nbttmp.putInt("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Y", pos.getY());
            nbttmp.putInt("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Z", pos.getZ());
            reality.put("MAHOUTSUKAI_PLAYER_ID_" + z, nbttmp);
            z++;
         }
      }

      compound.put("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_LIST", reality);
      CompoundTag contracts = new CompoundTag();
      CompoundTag nbtcctmp = null;
      int c = 0;

      for (UUID tmp : instance.getPlayerContracts().keySet()) {
         for (UUID tmp2 : instance.getPlayerContracts().get(tmp)) {
            nbtcctmp = new CompoundTag();
            nbtcctmp.store("MAHOUTSUKAI_CONTRACT_PLAYER_A", UUIDUtil.CODEC, tmp);
            nbtcctmp.store("MAHOUTSUKAI_CONTRACT_PLAYER_B", UUIDUtil.CODEC, tmp2);
            contracts.put("MAHOUTSUKAI_CONTRACT_ID_" + c, nbtcctmp);
            c++;
         }
      }

      compound.put("MAHOUTSUKAI_CONTRACT_PLAYER_LIST", contracts);
      int ds = 0;

      for (FictionData d : instance.playerCreations.keySet()) {
         compound.put("MAHOUTSUKAI_STORED_FICTION_" + ds, d.toNBT());
         ds++;
      }

      return compound;
   }

   public static void readNBT(MahouSavedData instance, CompoundTag nbt) {
      instance.freshMarbleArenaSequence = Math.clamp(nbt.getLongOr("MAHOUTSUKAI_FRESH_MARBLE_ARENA", 0), 0, 8192L * 8192);
      if (nbt.contains("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_LIST")) {
         CompoundTag reality = nbt.getCompound("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_LIST").orElse(new CompoundTag());

         for (String tmp : reality.keySet()) {
            CompoundTag nbttmp = (CompoundTag)reality.getCompound(tmp).orElse(null);
            if (nbttmp != null && nbttmp.contains("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_ID")) {
               UUID uuid = (UUID)nbttmp.read("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_ID", UUIDUtil.CODEC).orElse(null);
               if (uuid != null) {
                  BlockPos pos = new BlockPos(
                     nbttmp.getIntOr("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_X", 0),
                     nbttmp.getIntOr("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Y", 0),
                     nbttmp.getIntOr("MAHOUTSUKAI_REALITY_MARBLE_PLAYER_SPAWN_Z", 0)
                  );
                  instance.getAllSpawns().put(uuid, pos);
               }
            }
         }
      }

      if (nbt.contains("MAHOUTSUKAI_CONTRACT_PLAYER_LIST")) {
         CompoundTag contracts = nbt.getCompound("MAHOUTSUKAI_CONTRACT_PLAYER_LIST").orElse(new CompoundTag());

         for (String tmp : contracts.keySet()) {
            CompoundTag nbtc = (CompoundTag)contracts.getCompound(tmp).orElse(null);
            if (nbtc != null && nbtc.contains("MAHOUTSUKAI_CONTRACT_PLAYER_A") && nbtc.contains("MAHOUTSUKAI_CONTRACT_PLAYER_B")) {
               UUID uuidA = (UUID)nbtc.read("MAHOUTSUKAI_CONTRACT_PLAYER_A", UUIDUtil.CODEC).orElse(null);
               UUID uuidB = (UUID)nbtc.read("MAHOUTSUKAI_CONTRACT_PLAYER_B", UUIDUtil.CODEC).orElse(null);
               if (uuidA != null && uuidB != null) {
                  instance.getPlayerContracts().put(uuidA, uuidB);
                  instance.getPlayerContracts().put(uuidB, uuidA);
               }
            }
         }
      }

      for (int ds = 0; nbt.contains("MAHOUTSUKAI_STORED_FICTION_" + ds); ds++) {
         nbt.getCompound("MAHOUTSUKAI_STORED_FICTION_" + ds).map(FictionData::fromNBT).ifPresent(d -> instance.playerCreations.put(d, d.uuid));
      }
   }
}
