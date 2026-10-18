package stepsword.mahoutsukai.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JobType {
   UUID uuid;
   Level level;

   public JobType(UUID uuid, Level level) {
      this.uuid = uuid;
      this.level = level;
   }

   public void run() {
   }

   public static class NodeNetworkJob extends JobType {
      ArrayList<BlockPos> toExplore;
      HashSet<BlockPos> explored;
      BlockPos pos;
      int distance;
      int maxIters;
      Predicate<BlockEntity> predicate;
      Consumer<HashSet<BlockPos>> callback;

      public NodeNetworkJob(
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
         super(uuid, level);
         this.toExplore = toExplore;
         this.explored = explored;
         this.pos = pos;
         this.distance = distance;
         this.maxIters = maxIters;
         this.predicate = predicate;
         this.callback = callback;
      }

      @Override
      public void run() {
         if (this.toExplore != null && this.level != null && this.predicate != null) {
            NodeNetworkUtil.buildNetworkIterative(
               this.uuid, this.toExplore, this.explored, this.pos, this.level, this.distance, this.maxIters, this.predicate, this.callback
            );
         }
      }
   }
}
