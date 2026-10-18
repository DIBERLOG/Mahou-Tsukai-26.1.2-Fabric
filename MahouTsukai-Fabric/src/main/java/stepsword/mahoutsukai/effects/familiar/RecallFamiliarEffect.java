package stepsword.mahoutsukai.effects.familiar;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class RecallFamiliarEffect {
   public static boolean recallFamiliar(Player caster, boolean swap) {
      return !caster.level().isClientSide() ? recallEntity(caster, swap) : false;
   }

   public static boolean recallEntity(Player caster, boolean swap) {
      if (!caster.level().isClientSide()) {
         HashMap<UUID, FamiliarEntity> map = SummonFamiliarSpellEffect.familiarMap;
         BlockPos pos = null;
         Identifier dim = EffectUtil.getDimension(caster.level());
         if (map.containsKey(caster.getUUID())) {
            FamiliarEntity familiar = map.get(caster.getUUID());
            if (familiar != null && familiar.isAlive()) {
               ChunkPos chunk = ChunkPos.containing(familiar.blockPosition());
               ServerLevel ws = (ServerLevel)caster.level();
               if (ws.getChunkSource().getChunk(chunk.x(), chunk.z(), true) != null) {
                  if (swap) {
                     pos = familiar.blockPosition();
                     dim = EffectUtil.getDimension(familiar.level());
                  }

                  MahouTsukaiTeleporter.teleport(familiar, caster.getX(), caster.getY(), caster.getZ(), EffectUtil.getDimension(caster.level()));
                  if (swap) {
                     MahouTsukaiTeleporter.teleport(caster, pos.getX(), pos.getY(), pos.getZ(), dim);
                  }

                  familiar.acknowledge();
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }
}
