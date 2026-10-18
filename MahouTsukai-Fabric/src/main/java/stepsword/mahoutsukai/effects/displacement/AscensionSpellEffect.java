package stepsword.mahoutsukai.effects.displacement;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.Utils;

public class AscensionSpellEffect {
   public static boolean teleportEntityToSurface(int x, int z, Entity e, int bottom, Player caster, UUID uuid, boolean chargeMana) {
      if (!e.level().isClientSide()) {
         for (int i = e.level().getMaxY(); i > bottom; i--) {
            BlockPos pos = new BlockPos(x, i, z);
            BlockState bs = e.level().getBlockState(pos);
            if (bs.blocksMotion()
               && !EffectUtil.unchangableBlock(bs.getBlock())
               && Utils.isBlockAir(e.level(), pos.relative(Direction.UP))
               && Utils.isBlockAir(e.level(), pos.relative(Direction.UP, 2))) {
               boolean paid = !chargeMana;
               if (chargeMana) {
                  boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(pos, e.level(), uuid, MTConfig.ASCENSION_SCROLL_MANA_COST);
                  paid = nearbyBatteriesGaveMana
                     || caster != null
                        && PlayerManaManager.drainMana(caster, MTConfig.ASCENSION_SCROLL_MANA_COST, false, false) == MTConfig.ASCENSION_SCROLL_MANA_COST;
               }

               if (paid) {
                  MahouTsukaiTeleporter.teleport(e, x + 0.5, i + 1, z + 0.5, EffectUtil.getDimension(e.level()));
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }
}
