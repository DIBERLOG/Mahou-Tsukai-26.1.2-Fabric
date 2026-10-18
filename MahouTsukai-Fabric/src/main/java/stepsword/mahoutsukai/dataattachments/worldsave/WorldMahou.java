package stepsword.mahoutsukai.dataattachments.worldsave;

import org.jetbrains.annotations.NotNull;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public class WorldMahou {
   public MahouSavedData data;

   public WorldMahou(@NotNull Level level) {
      if (level.getServer() != null) {
         this.data = this.loadWorldData(level.getServer());
      }
   }

   public MahouSavedData loadWorldData(MinecraftServer server) {
      return (MahouSavedData)server.getDataStorage().computeIfAbsent(MahouSavedData.TYPE);
   }
}
