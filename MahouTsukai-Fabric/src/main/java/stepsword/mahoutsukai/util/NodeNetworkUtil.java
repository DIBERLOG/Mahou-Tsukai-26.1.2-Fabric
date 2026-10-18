package stepsword.mahoutsukai.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

public class NodeNetworkUtil {
   public static void buildNetworkIterative(
      UUID uuid,
      ArrayList<BlockPos> toExplore,
      HashSet<BlockPos> explored,
      BlockPos pos,
      Level level,
      int distance,
      int maxIters,
      Predicate<BlockEntity> predicate,
      Consumer<HashSet<BlockPos>> callback
   ) {
      if (toExplore == null) {
         toExplore = new ArrayList<>();
      }

      if (explored == null) {
         explored = new HashSet<>();
      }

      if (pos != null && !explored.contains(pos)) {
         toExplore.add(pos);
      }

      boolean cont = true;

      for (int i = 0; i < maxIters; i++) {
         cont = explore(toExplore, explored, level, distance, predicate);
         if (!cont) {
            break;
         }
      }

      if (cont) {
         MahouJobs.addJob(uuid, new JobType.NodeNetworkJob(uuid, toExplore, explored, pos, level, distance, maxIters, predicate, callback));
      } else {
         callback.accept(verifyNetwork(level, explored, predicate));
      }
   }

   public static boolean explore(ArrayList<BlockPos> toExplore, HashSet<BlockPos> explored, Level level, int distance, Predicate<BlockEntity> pred) {
      if (toExplore.size() == 0) {
         return false;
      }

      BlockPos explore = toExplore.remove(0);
      ChunkAccess pp = level.getChunk(explore.offset(distance, 0, distance));
      ChunkAccess mm = level.getChunk(explore.offset(-distance, 0, -distance));
      int maxx = pp.getPos().x();
      int maxz = pp.getPos().z();
      int minx = mm.getPos().x();
      int minz = mm.getPos().z();

      for (int i = minx; i < maxx + 1; i++) {
         for (int j = minz; j < maxz + 1; j++) {
            LevelChunk chunk = level.getChunk(i, j);
            Map<BlockPos, BlockEntity> map = chunk.getBlockEntities();

            for (BlockPos p : map.keySet()) {
               if (!explored.contains(p)
                  && !explore.equals(p)
                  && p.getX() < explore.getX() + distance
                  && p.getX() > explore.getX() - distance
                  && p.getZ() < explore.getZ() + distance
                  && p.getZ() > explore.getZ() - distance) {
                  BlockEntity te = map.get(p);
                  if (pred.test(te) && !toExplore.contains(p)) {
                     toExplore.add(p);
                  }
               }
            }
         }
      }

      explored.add(explore);
      return true;
   }

   public static HashSet<BlockPos> verifyNetwork(Level level, HashSet<BlockPos> network, Predicate<BlockEntity> pred) {
      return new HashSet<>(network.stream().filter(te -> pred.test(level.getBlockEntity(te))).collect(Collectors.toSet()));
   }
}
