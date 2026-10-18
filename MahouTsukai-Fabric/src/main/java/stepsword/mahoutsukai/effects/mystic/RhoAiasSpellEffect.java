package stepsword.mahoutsukai.effects.mystic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RhoAiasSpellEffect {
   public static void spawnRhoAias(Player user) {
      if (!user.level().isClientSide()) {
         List<Integer> settings = MahouSettings.getIntSettings(user, MahouSettings.Spell.RHO_AIAS);
         RhoAiasMahoujinEntity rhoAias = new RhoAiasMahoujinEntity(
            user.level(),
            user,
            Utils.colorConvert(settings.get(0)),
            Utils.colorConvert(settings.get(1)),
            Utils.colorConvert(settings.get(2)),
            0.95F,
            Utils.colorConvert(settings.get(3)),
            Utils.colorConvert(settings.get(4)),
            Utils.colorConvert(settings.get(5)),
            4.0F
         );
         rhoAias.setRotationSpeed(0.2F);
         user.level().addFreshEntity(rhoAias);
      }
   }

   public static boolean protects(ServerLevel world,Vec3 point){if(stepsword.mahoutsukai.item.replica.Replica.protects(world,point))return true;for(Entity entity:world.getAllEntities())if(entity instanceof RhoAiasMahoujinEntity shield && !shield.isRemoved() && shield.isPointInRadiusOfCircle(point))return true;return false;}
   public static List<BlockPos> protectBlocks(ServerLevel world,List<BlockPos> blocks){return new ArrayList<>(blocks.stream().filter(p->!protects(world,Vec3.atCenterOf(p))).toList());}
   public static List<Entity> protectEntities(ServerLevel world,List<Entity> entities){return new ArrayList<>(entities.stream().filter(e->!protects(world,e.position()) && !protects(world,e.position().add(0,e.getBbHeight(),0))).toList());}
}
