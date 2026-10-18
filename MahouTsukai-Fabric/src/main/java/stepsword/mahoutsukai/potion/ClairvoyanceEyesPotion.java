package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.networking.EntityPathPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.util.EffectUtil;

public class ClairvoyanceEyesPotion extends EyesPotion {
   public static ConcurrentHashMap<PathfinderMob, Path> predictionMap = new ConcurrentHashMap<>();
   public static ConcurrentHashMap<PathfinderMob, CompoundTag> entityNBTMap = new ConcurrentHashMap<>();
   public static Iterator<PathfinderMob> iterator = null;

   protected ClairvoyanceEyesPotion() {
      super(ModEffects.getColorNumber(59, 204, 209));
   }

   public static void clairvoyanceEyesPlayerTick(Player player) {if(player instanceof ServerPlayer sp && EffectUtil.hasBuff(player,ModEffects.CLAIRVOYANCE))PacketHandler.sendTo(sp,predict(player));}
   public static EntityPathPacket predict(Player player) {
      int range = MTConfig.CLAIRVOYANCE_RANGE;
      Level world = player.level();
      AABB aabb = new AABB(player.blockPosition().offset(-range, -range, -range).getCenter(), player.blockPosition().offset(range, range, range).getCenter());
      if (EffectUtil.hasBuff(player, ModEffects.CLAIRVOYANCE)) {
         List<PathfinderMob> lst = world.getEntitiesOfClass(PathfinderMob.class, aabb);
         ArrayList<Integer> entities = new ArrayList<>();
         ArrayList<Path> paths = new ArrayList<>();

         for (PathfinderMob e : lst) {
            Path p = e.getNavigation().getPath();
            if (p != null && p.getNodeCount() > 0 && !p.isDone()) {
               entities.add(e.getId());
               paths.add(p);
            }
         }

         return new EntityPathPacket(entities, paths);
      }
      return new EntityPathPacket(new ArrayList<>(),new ArrayList<>());
   }
}
