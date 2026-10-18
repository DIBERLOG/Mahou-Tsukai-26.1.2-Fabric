package stepsword.mahoutsukai.effects.displacement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.Utils;

public class ProjectileDisplacementSpellEffect {
   public static void projectileDisplacementJoinWorld(Entity entity) {
      if (entity instanceof Arrow arrow) {
         if (arrow.getOwner() instanceof Player) {
            Player player = (Player)arrow.getOwner();
            if (player != null && !player.level().isClientSide()) {
               IMahou casterMahou = Utils.getPlayerMahou(player);
               if (casterMahou != null) {
                  casterMahou.setLastArrowShot(arrow.getUUID());
               }
            }
         }
      } else if (entity instanceof ThrownTrident && ((ThrownTrident)entity).getOwner() instanceof Player) {
         Player player = (Player)((ThrownTrident)entity).getOwner();
         if (player != null && !player.level().isClientSide()) {
            IMahou casterMahou = Utils.getPlayerMahou(player);
            if (casterMahou != null) {
               casterMahou.setLastArrowShot(entity.getUUID());
            }
         }
      }
   }

   public static boolean teleportToLastArrow(Player caster) {
      if (!caster.level().isClientSide()) {
         IMahou casterMahou = Utils.getPlayerMahou(caster);
         if (casterMahou != null) {
            UUID uuid = casterMahou.getLastArrowShot();
            HashSet<Entity> entities = new HashSet<>();
            ((ServerLevel)caster.level()).getAllEntities().forEach(entities::add);

            for (Entity e : new ArrayList<>(entities)) {
               if (e instanceof Projectile && e.getUUID().equals(uuid)) {
                  int manaCost = MTConfig.PROJECTILE_DISPLACEMENT_MANA_COST;
                  if (PlayerManaManager.drainMana(caster, manaCost, false, false) == manaCost) {
                     MahouTsukaiTeleporter.teleport(caster, e.getX(), e.getY() + 1.0, e.getZ(), EffectUtil.getDimension(caster.level()));
                     return true;
                  }

                  return false;
               }
            }
         }
      }

      return false;
   }
}
