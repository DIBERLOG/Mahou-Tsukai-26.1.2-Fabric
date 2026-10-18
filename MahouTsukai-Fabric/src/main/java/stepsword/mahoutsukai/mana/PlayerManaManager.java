package stepsword.mahoutsukai.mana;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.fabricmc.fabric.api.entity.FakePlayer;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.item.attunedgems.AttunedGem;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.effects.Leylines;
import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.item.william.William;
import stepsword.mahoutsukai.entity.fae.FaeEntity;

public class PlayerManaManager {
   public static void onPlayerTick(Player player, long tick) {
      if (!player.level().isClientSide()) stepsword.mahoutsukai.potion.FearPotion.fearPlayerTick(player);
      if (tick % 100L == 0L) regenMana(player, false);
      if (tick % 4L == 0L) stepsword.mahoutsukai.potion.FaySightEyesPotion.faySightEyesPlayerTick(player);
   }

   /** Original wake event flags and ManaEvents.mahouSleep clock/weather checks. */
   public static void onWake(Player player, boolean wakeImmediately, boolean updateLevel) {
      if (!player.level().isClientSide() && !wakeImmediately && !updateLevel) {
         long time = player.level().getOverworldClockTime() % 24000L;
         if ((time > 23998L || time < 12500L) && !player.level().isThundering())
            recoverManaFromSleep(player);
      }
   }

   public static void recoverManaFromSleep(Player player) { regenMana(player, true); }

   public static void regenMana(Player player, boolean sleep) {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         double sleepregen = MTConfig.MANA_RECOVERY_SLEEP;
         int playerMaxMana = mahou.getMaxMana();
         int playerStoredMana = mahou.getStoredMana();
         int punishment = (int)(MTConfig.DEATH_COLLECTION_DRAIN_PER_SOUL * mahou.getDeathCollectionUsesLeft());
         if (playerMaxMana <= playerStoredMana) {
            mahou.setStoredMana(Math.max(0, playerMaxMana - punishment));
         } else {
            int regen;
            if (!sleep) {
               regen = getManaRegen(player);
            } else {
               regen = (int)(sleepregen * playerMaxMana);
            }

            if (punishment > 0 && punishment > mahou.getStoredMana() + regen && MTConfig.DEATH_COLLECTION_NEGATIVE_REGEN_PUNISHMENT) {
               mahou.setDeathCollectionUsesLeft(Math.max(0.0F, (float)(mahou.getDeathCollectionUsesLeft() - MTConfig.DEATH_COLLECTION_SOUL_VALUE_PLAYER)));
               if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.connection != null)
                  player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("mahoutsukai.help.too_many_souls"));
            }

            mahou.setStoredMana(Math.min(Math.max(mahou.getStoredMana() + regen - punishment, 0), mahou.getMaxMana()));
         }

         updateClientMahou((ServerPlayer)player, mahou);
      }
   }

   public static int getManaRegen(Player player) {
      int true_MAX_MANA = stepsword.mahoutsukai.fabric.ManaConfig.current.MAX_MANA_CAP();
      int default_MANA_REGEN_RATE_PER_TICK = MTConfig.MANA_REGEN_PER_TICK;
      double scaling_regen_factor = MTConfig.MANA_ADDED_SCALING_REGEN_PER_TICK;
      IMahou mahou = Utils.getPlayerMahou(player);
      FoodData fs = player.getFoodData();
      int hunger = fs.getFoodLevel();
      double saturation = fs.getSaturationLevel();
      double regenRatio = (float)mahou.getMaxMana() / true_MAX_MANA;
      boolean flat = MTConfig.LEY_FLAT;
      float punishment = MTConfig.LEY_PUNISHMENT;
      float leyfactor = (float)MTConfig.LEY_FACTOR;
      int ley = Leylines.nearbyLeyValue(player.blockPosition());
      float ley2 = ley / 19.0F;
      float addonleymana = 0.0F;
      if (MTConfig.LEY_AFFECTS_MANA) {
         if (!flat) {
            addonleymana = punishment + ley2 * mahou.getMaxMana() * leyfactor;
         } else {
            addonleymana = punishment + leyfactor * ley2;
         }
      }

      if (!Leylines.leyDimensionValid(EffectUtil.getDimension(player.level()))) {
         addonleymana = 0.0F;
      } else if (ley >= 13
         && mahou.getFaeSpawn()
         && player.getRandom().nextFloat() < 0.1 * MTConfig.FAE_SPAWN_RATE
         && mahou.hasMagic()
         && !player.level().isClientSide()) {
         FaeEntity fae = new FaeEntity(player.level());
         fae.setPos(player.getX() + player.getRandom().nextInt(10), player.getY(), player.getZ() + player.getRandom().nextInt(10));
         if (Utils.isBlockAir(player.level(), fae.blockPosition())) {
            try {
               player.level().addFreshEntity(fae);
            } catch (Exception var20) {
            }
         }
      }

      return default_MANA_REGEN_RATE_PER_TICK
         + (int)(scaling_regen_factor * mahou.getMaxMana())
         + (int)Math.ceil((hunger + saturation) * regenRatio * MTConfig.MANA_REGEN_HUNGER_FACTOR)
         + (int)addonleymana
         + (int)Math.min(MTConfig.GEAS_MAX_MANA_REGEN, GeasEffect.getUniqueGeasCount(player) * MTConfig.MANA_REGEN_PER_GEAS)
         + (int)(William.itemWilliamPlethora(player) * MTConfig.WILLIAM_PERSONAL_MANA_FACTOR);
   }


   public static void damagePlayer(Player player, int mana) {
      if (player != null && !player.level().isClientSide()) {
         boolean z = false;
         if (z || MTConfig.MAGIC_DAMAGES_PLAYER) {
            double v = 0.0;
            if (MTConfig.MAGIC_DAMAGE_FLAT) {
               v = MTConfig.MAGIC_DAMAGE_VALUE;
            } else {
               v = MTConfig.MAGIC_DAMAGE_VALUE * mana;
            }

            player.hurtServer((net.minecraft.server.level.ServerLevel)player.level(), MTConfig.MAGIC_DAMAGE_GENERIC ? player.damageSources().generic() : player.damageSources().magic(), (float)v);
            ((stepsword.mahoutsukai.fabric.mixin.LivingCombatAccessor)player).mahoutsukai$setLastHurtByPlayerMemoryTime(100);
         }
      }
   }

   public static void donateManaToFakePlayer(SafeFakePlayer fake, Player player, int mana) {
      if (drainMana(player, mana, false, false) == mana) {
         fake.setMana(mana);
      }
   }

   public static int drainMana(Player player, int drain, boolean nogems, boolean nocircuits) {
      return drainMana(player, drain, nogems, nocircuits, true, false);
   }

   public static int drainMana(Player player, int drain, boolean nogems, boolean nocircuits, boolean maxincr, boolean noupdate) {
      return drainMana(player, drain, nogems, nocircuits, maxincr, noupdate, false);
   }

   public static int drainMana(Player player, int drain, boolean nogems, boolean nocircuits, boolean maxincr, boolean noupdate, boolean drainifnotenough) {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (player != null && (!player.isCreative() || !MTConfig.CREATIVE_IGNORES_MANA_COSTS || player.level().isClientSide())) {
         BlockPos p = player.blockPosition();
         if (player instanceof FakePlayer) {
            p = player.blockPosition();
         }

         int playerUsedMana = 0;
         int returnManaUsed = 0;
         if (mahou != null) {
            int playerMana = mahou.getStoredMana();
            if (player instanceof FakePlayer) {
               playerMana = 0;
            }

            if (player instanceof SafeFakePlayer) {
               playerMana = ((SafeFakePlayer)player).getMana();
            }

            int remaining = drain;
            Set<BlockEntity> circuits = null;
            ArrayList<ItemStack> playerGems = null;
            if (!nogems) {
               playerGems = gemsInInventory(player);
            }

            if (!nocircuits) {
               circuits = nearbyBatteries(p, player.level());
            }

            if (playerMana >= drain) {
               playerUsedMana = drain;
               returnManaUsed = drain;
            } else {
               int totalMana = playerMana;
               if (playerGems != null) {
                  totalMana += countManaInGems(playerGems);
               }

               if (circuits != null) {
                  totalMana += countManaInNearbyBatteries(p, player.level(), circuits, player.getUUID());
               }

               if (totalMana < drain) {
                  if (drainifnotenough) {
                     playerUsedMana = playerMana;
                     returnManaUsed = playerMana;
                     int tmp = remaining;
                     if (circuits != null && !nocircuits) {
                        remaining = drainManaFromNearbyBatteries(p, player.level(), circuits, remaining, player.getUUID());
                     }

                     returnManaUsed += tmp - remaining;
                     tmp = remaining;
                     if (playerGems != null && !nogems) {
                        remaining = drainManaFromGems(playerGems, remaining);
                     }

                     returnManaUsed += tmp - remaining;
                  } else if (!nogems) {
                     playerUsedMana = 0;
                     returnManaUsed = 0;
                  } else {
                     playerUsedMana = playerMana;
                     returnManaUsed = playerMana;
                     int tmp = remaining;
                     if (circuits != null) {
                        remaining = drainManaFromNearbyBatteries(p, player.level(), circuits, remaining, player.getUUID());
                     }

                     returnManaUsed += tmp - remaining;
                  }
               } else {
                  remaining = drain - playerMana;
                  playerUsedMana = playerMana;
                  returnManaUsed = drain;
                  if (playerGems != null) {
                     remaining = drainManaFromGems(playerGems, remaining);
                  }

                  if (circuits != null) {
                     remaining = drainManaFromNearbyBatteries(p, player.level(), circuits, remaining, player.getUUID());
                  }
               }
            }

            mahou.setManaFull(false);
            mahou.setStoredMana(Math.max(0, playerMana - playerUsedMana));
            if (maxincr) {
               updateManaCounter((ServerPlayer)player, playerUsedMana);
            }

            if (!noupdate) {
               updateClientMahou((ServerPlayer)player, mahou);
            }

            if (playerUsedMana > 0) {
               damagePlayer(player, playerUsedMana);
            }
         }

         return returnManaUsed;
      } else {
         return drain;
      }
   }

   public static int countMana(Player player) {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou == null) {
         return 0;
      }

      int totalMana = mahou.getStoredMana();
      Set<BlockEntity> circuits = null;
      ArrayList<ItemStack> playerGems = null;
      playerGems = gemsInInventory(player);
      circuits = nearbyBatteries(player.blockPosition(), player.level());
      if (player instanceof SafeFakePlayer) {
         return ((SafeFakePlayer)player).getMana();
      }

      if (playerGems != null) {
         totalMana += countManaInGems(playerGems);
      }

      if (circuits != null) {
         totalMana += countManaInNearbyBatteries(player.blockPosition(), player.level(), circuits, player.getUUID());
      }

      return totalMana;
   }

   public static int chargeCircuit(Player player, int drain) {
      IMahou mahou = Utils.getPlayerMahou(player);
      int playerUsedMana = 0;
      int remaining = drain;
      int ret = 0;
      if (mahou != null) {
         int playerMana = mahou.getStoredMana();
         if (player instanceof FakePlayer) {
            playerMana = 0;
         }

         if (player instanceof SafeFakePlayer) {
            playerMana = ((SafeFakePlayer)player).getMana();
         }

         ArrayList<ItemStack> playerGems = null;
         playerGems = gemsInInventory(player);
         if (playerMana >= drain) {
            playerUsedMana = drain;
            ret = drain;
         } else {
            int totalMana = playerMana;
            if (playerGems != null) {
               totalMana += countManaInGems(playerGems);
            }

            if (totalMana < drain) {
               playerUsedMana = playerMana;
               ret = playerMana;
               if (playerGems != null) {
                  remaining = drainManaFromGems(playerGems, drain - playerUsedMana);
               }

               ret += drain - playerUsedMana - remaining;
            } else {
               remaining = drain - playerMana;
               playerUsedMana = playerMana;
               ret = playerMana;
               if (playerGems != null) {
                  ret += remaining - drainManaFromGems(playerGems, remaining);
               }
            }
         }

         mahou.setManaFull(false);
         mahou.setStoredMana(Math.max(0, playerMana - playerUsedMana));
         updateManaCounter((ServerPlayer)player, playerUsedMana);
         updateClientMahou((ServerPlayer)player, mahou);
         if (playerUsedMana > 0) {
            damagePlayer(player, playerUsedMana);
         }

         return ret;
      } else {
         Utils.debug("Getting Mana from Batteries");
         return getManaFromBatteriesFirst(new BlockPos(player.blockPosition()), player.level(), player.getUUID(), drain) ? drain : 0;
      }
   }

   public static int chargeAnyNearbyBatteries(BlockPos p, Level world, Set<BlockEntity> tes, int charge) {
      Set<BlockEntity> tes2 = tes;
      if (tes2 == null) {
         tes2 = nearbyBatteries(p, world);
      }

      int remaining = charge;

      for (BlockEntity te : tes2) {
         if (te instanceof ManaCircuitTileEntity mte) {
            int tmp = mte.getMaxMana() - mte.getMana();
            if (tmp > 0) {
               if (tmp > remaining) {
                  mte.setMana(mte.getMana() + remaining);
                  remaining = 0;
               } else {
                  mte.setMana(mte.getMaxMana());
                  remaining -= tmp;
               }

               mte.sendUpdates();
               if (remaining <= 0) {
                  break;
               }
            }
         }
      }

      return remaining;
   }

   public static ArrayList<ItemStack> gemsInInventory(Player player) {
      ArrayList<ItemStack> playerGems = new ArrayList<>();

      for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
         ItemStack itemstack = player.getInventory().getItem(slot);
         if (!itemstack.isEmpty() && itemstack.getItem() instanceof AttunedGem) {
            playerGems.add(itemstack);
         }
      }

      return playerGems;
   }

   public static int countManaInGems(ArrayList<ItemStack> playerGems) {
      int ret = 0;

      for (ItemStack itemstack : playerGems) {
         if (!itemstack.isEmpty() && itemstack.getItem() instanceof AttunedGem) {
            GemMahou gemMahou = Utils.getGemMahou(itemstack);
            if (gemMahou != null) {
               ret += gemMahou.getStoredMana();
            }
         }
      }

      return ret;
   }

   public static int drainManaFromGems(ArrayList<ItemStack> playerGems, int drain) {
      int remaining = drain;

      for (ItemStack stack : playerGems) {
         if (remaining <= 0) {
            break;
         }

         if (!stack.isEmpty() && stack.getItem() instanceof AttunedGem) {
            GemMahou gemMahou = Utils.getGemMahou(stack);
            if (gemMahou != null) {
               int tmp = gemMahou.getStoredMana();
               if (remaining >= tmp) {
                  remaining -= tmp;
                  gemMahou.setStoredMana(0);
               } else {
                  gemMahou.setStoredMana(gemMahou.getStoredMana() - remaining);
                  remaining = 0;
               }

               Utils.setGemMahou(stack, gemMahou);
            }
         }
      }

      return remaining;
   }

   public static int countManaInNearbyBatteries(BlockPos p, Level world, Set<BlockEntity> tes, UUID uuid) {
      Set<BlockEntity> tes2 = tes;
      if (tes2 == null) {
         tes2 = nearbyBatteries(p, world);
      }

      int ret = 0;

      for (BlockEntity te : tes2) {
         if (te instanceof ManaCircuitTileEntity mte && uuid != null && (uuid.equals(mte.getOwnerUUID()) || mte.getFae())) {
            ret += mte.getMana();
         }
      }

      return ret;
   }

   public static int drainManaFromNearbyBatteries(BlockPos p, Level world, Set<BlockEntity> tes, int drain, UUID uuid) {
      Set<BlockEntity> tes2 = tes;
      if (tes2 == null) {
         tes2 = nearbyBatteries(p, world);
      }

      int remaining = drain;

      for (BlockEntity te : tes2) {
         if (te instanceof ManaCircuitTileEntity mte) {
            Utils.debug("Getting Mana from Batteries: Fay: " + mte.getFae() + ", UUID: " + uuid + ", Owner UUID:" + mte.getOwnerUUID());
            if (uuid != null && (uuid.equals(mte.getOwnerUUID()) || mte.getFae())) {
               if (remaining <= 0) {
                  break;
               }

               int tmp = mte.getMana();
               if (remaining >= tmp) {
                  remaining -= tmp;
                  mte.setMana(0);
               } else {
                  mte.setMana(mte.getMana() - remaining);
                  remaining = 0;
               }

               mte.sendUpdates();
            }
         }
      }

      return remaining;
   }

   public static Set<BlockEntity> nearbyBatteries(BlockPos p, Level world) {
      int range = MTConfig.MANA_CIRCUIT_RANGE;
      return nearbyBatteries(p, world, range);
   }

   public static Set<BlockEntity> nearbyBatteries(BlockPos p, Level world, int range) {
      return nearbyBatteries(p, world, range, true);
   }

   public static Set<BlockEntity> nearbyBatteries(BlockPos p, Level world, int range, boolean onOnly) {
      BlockPos min = p.offset(-range, -range, -range);
      BlockPos max = p.offset(range, range, range);
      Utils.debug("Getting Mana from Batteries BlockPos:" + p.toString());
      ChunkPos cmin = new ChunkPos(min.getX() >> 4, min.getZ() >> 4);
      ChunkPos cmax = new ChunkPos(max.getX() >> 4, max.getZ() >> 4);
      Set<BlockEntity> tes = new HashSet<>();
      new ArrayList();

      for (int i = Math.min(cmin.x(), cmax.x()); i <= Math.max(cmin.x(), cmax.x()); i++) {
         for (int j = Math.min(cmin.z(), cmax.z()); j <= Math.max(cmin.z(), cmax.z()); j++) {
            LevelChunk c = world.getChunk(i, j);
            List<BlockEntity> tmp = c.getBlockEntities()
               .values()
               .stream()
               .filter(
                  tileEntity -> tileEntity instanceof ManaCircuitTileEntity
                     && (!onOnly || ((ManaCircuitTileEntity)tileEntity).isRedstoneOn())
                     && Math.abs(tileEntity.getBlockPos().getX() - p.getX()) < range
                     && Math.abs(tileEntity.getBlockPos().getZ() - p.getZ()) < range
               )
               .toList();
            tes.addAll(tmp);
         }
      }

      return tes;
   }

   public static boolean getManaFromBatteriesFirst(BlockPos p, Level world, UUID uuid, int drain) {
      Set<BlockEntity> tes = nearbyBatteries(p, world);
      if (countManaInNearbyBatteries(p, world, tes, uuid) >= drain) {
         drainManaFromNearbyBatteries(p, world, tes, drain, uuid);
         return true;
      } else {
         return false;
      }
   }

   public static void updateManaCounter(ServerPlayer player, int mana) {
      stepsword.mahoutsukai.fabric.ManaProgression.recordSpent(Utils.getPlayerMahou(player), mana,
          stepsword.mahoutsukai.fabric.ManaConfig.current);
      stepsword.mahoutsukai.fabric.MahouNetworking.sync(player);
   }
   public static void updateClientMahou(ServerPlayer player, IMahou mahou) {
      if (player != null && mahou != null) stepsword.mahoutsukai.fabric.MahouNetworking.sync(player);
   }
   public static boolean hasMana(Player player, int drain) { return countMana(player) > drain; }
}
