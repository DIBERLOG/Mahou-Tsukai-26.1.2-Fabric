package stepsword.mahoutsukai.util;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class MahouTsukaiTeleporter {
   public static void teleport(Entity entity, double x, double y, double z, Identifier dim) {
      teleport(entity, dim, new Vec3(x, y, z));
   }

   public static void teleport(Entity entity, Identifier destination, Vec3 pos) {
      if (entity != null) {
         if (EffectUtil.compareDimensions(EffectUtil.getDimension(entity.level()), destination)) {
            entity.teleportTo(pos.x(), pos.y(), pos.z());
         } else if (!entity.level().isClientSide()) {
            ((ServerLevel)entity.level()).getServer().execute(() -> {
               ServerLevel sw = EffectUtil.getNewDimensionByName((ServerLevel)entity.level(), destination);
               if(sw==null || entity.isRemoved())return;
               if (entity instanceof ServerPlayer) {
                  ((ServerPlayer)entity).teleportTo(sw, pos.x(), pos.y(), pos.z(), java.util.Set.of(), entity.getYRot(), entity.getXRot(), false);
               } else {
                  entity.teleport(new TeleportTransition(sw, pos, new Vec3(0.0, 0.0, 0.0), entity.getYRot(), entity.getXRot(), java.util.Set.of(), entity1 -> {}));
               }
            });
         }
      }
   }
}
