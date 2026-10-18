package stepsword.mahoutsukai.effects.projection;

import io.netty.util.internal.ConcurrentSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;

import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.fluids.MurkyWaterBlock;

import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.mana.PlayerManaManager;

import stepsword.mahoutsukai.networking.PacketHandler;

import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class PowerConsolidationSpellEffect {
   public static void powerConsolidationWorldTick(final ServerLevel world) {
      if (!world.isClientSide() && world.getGameTime() % Math.max(1,MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE) == 0L) {
         Entity failed = null;

         try {
            final int manaCost = MTConfig.POWER_CONSOLIDATION_SWORD_MANA_COST;
            double nerfFactor = MTConfig.POWER_CONSOLIDATION_NERF_FACTOR;
            Iterator<Entity> iter = world.getAllEntities().iterator();
            final List<ItemEntity> toDelete = new ArrayList<>();
            final List<WeaponProjectileEntity> toSpawn = new ArrayList<>();

            while (iter.hasNext()) {
               Entity e = iter.next();
               if (e instanceof ItemEntity item) {
                  failed = e;
                  if(stepsword.mahoutsukai.fabric.JoustingCompatibility.loaded())stepsword.mahoutsukai.fabric.JoustingCompatibility.powerConsolidation(world,item,toDelete,toSpawn,manaCost);
                  if (Utils.isSword(item.getItem())) {
                     boolean inMurkyWater = Utils.isInMurkyWater(e);
                     boolean isAllowed = isItemAllowed(item.getItem(), e.level());
                     boolean isEnchanted = item.getItem().isEnchanted();
                     boolean hasOwner = item.getOwner() instanceof Player;
                     if (!hasOwner && inMurkyWater) {
                        PlayerHelp.sendHelpMessageNearby(e.blockPosition(), e.level(), PlayerHelp.Message.CALIBURN_NO_OWNER);
                     }

                     if (hasOwner && inMurkyWater) {
                        Player player = (Player)item.getOwner();
                        if (!isAllowed) {
                           PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.CALIBURN_SWORD_BANNED);
                        } else if (!isEnchanted) {
                           PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.CALIBURN_NOT_ENCHANTED);
                        } else {
                           ConcurrentSet<BlockPos> connected = new ConcurrentSet();
                           boolean lt = lakeThreshhold(item.blockPosition(), connected, world);
                           if (!lt) {
                              PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.CALIBURN_LAKE_TOO_SMALL);
                           } else if (MandatoryFun.meetsAllFunReqs(player)) {
                              if (PlayerManaManager.hasMana(player, manaCost)) {
                                 BlockPos p = findCenter(connected);
                                 if (!Utils.isBlockAir(world, p)) {
                                    p = p.below();
                                 }

                                 ItemStack stack = new ItemStack((ItemLike)ModItems.caliburn.get());
                                 SwordMahou mahou = Utils.getSwordMahou(stack);
                                 double lb = getLimitBreakInArea(item);
                                 if (item.getItem().getItem() instanceof Caliburn) {
                                    SwordMahou orig = Utils.getSwordMahou(item.getItem());
                                    if (orig != null) {
                                       lb = Math.max(orig.getInnateCap(), lb);
                                    }

                                    if (item.getItem().has(DataComponents.CUSTOM_NAME)) {
                                       Component name = item.getItem().getHoverName();
                                       stack.set(DataComponents.CUSTOM_NAME, name);
                                    }
                                 }

                                 mahou.setInnateCap(lb);
                                 float damage = Caliburn.simulateHit(item.getItem(), world);
                                 ItemStack stackCopy = item.getItem().copy();
                                 EnchantmentHelper.updateEnchantments(stackCopy, mutableEnchants -> mutableEnchants.removeIf(a -> true));
                                 if(MTConfig.POWER_CONSOLIDATION_UNSOCKETS_APOTH_GEMS)stepsword.mahoutsukai.fabric.ApotheosisCompatibility.unsocket(stackCopy);

                                 float damageWithoutEnchant = Caliburn.simulateHit(stackCopy, world);
                                 damage = (float)(damage - nerfFactor * (damage - damageWithoutEnchant));
                                 mahou.setAttackDamage(Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, --damage));
                                 Utils.setSwordMahou(stack, mahou);
                                 Utils.debug("Advancement Time");
                                 ((MahouTrigger)ModTriggers.SWORD_IN_THE_LAKE).trigger((ServerPlayer)player);
                                 WeaponProjectileEntity wpe = new WeaponProjectileEntity(world, p.getX(), p.getY(), p.getZ(), stack);
                                 wpe.setDeltaMovement(0.0, -1.0, 0.0);
                                 wpe.setOwner(player);
                                 toSpawn.add(wpe);
                                 toDelete.add(item);
                              } else {
                                 PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.NOT_ENOUGH_MANA);
                              }
                           } else {
                              var inventory = player.getInventory();
                              int sizeInventory = inventory.getContainerSize();
                              ItemStack stack = new ItemStack((ItemLike)ModItems.funbook.get());
                              boolean hasBook = false;
                              boolean hasSpace = false;

                              for (int i = 0; i < sizeInventory; i++) {
                                 ItemStack slot = inventory.getItem(i);
                                 if (ItemStack.isSameItem(slot, stack)) {
                                    hasBook = true;
                                 }

                                 if (slot.isEmpty()) {
                                    hasSpace = true;
                                 }
                              }

                              if (!hasBook && hasSpace) {
                                 Utils.giveOrDrop(player, stack);
                              } else if (!hasSpace && !hasBook) {
                                 PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.NO_FUN_NO_SPACE);
                              }

                              PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.NOT_ENOUGH_FUN);
                           }
                        }
                     }
                  }
               }
            }

            if (toDelete.size() > 0) {
               ((ServerLevel)world)
                  .getServer()
                  .execute(
                     new Runnable() {
                        @Override
                        public void run() {
                           for (int i = 0; i < toDelete.size(); i++) {
                              ItemEntity e = toDelete.get(i);
                              Utils.debug("Item alive:" + e.isAlive() + "... Thrower:" + e.getOwner());
                              if (e.isAlive()
                                 && e.getOwner() instanceof Player
                                 && i < toSpawn.size()
                                 && PlayerManaManager.drainMana((Player)e.getOwner(), manaCost, false, false) == manaCost) {
                                 Utils.debug("Mana charged and item spawning.");
                                 world.addFreshEntity((Entity)toSpawn.get(i));
                                 e.discard();
                              }
                           }
                        }
                     }
                  );
            }
         } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger("mahoutsukai").error("Lake sword conversion failed",e);
            if (failed != null) {
               PlayerHelp.sendHelpMessageNearby(failed.blockPosition(), failed.level(), PlayerHelp.Message.CALIBURN_INTERNAL_ERROR);
            }
         }
      }
   }

   public static double getLimitBreakInArea(ItemEntity entity) {
      Vec3 vec = entity.position();
      int radius = 10;
      AABB aabb = new AABB(vec.x - radius, vec.y - radius, vec.z - radius, vec.x + radius, vec.y + radius, vec.z + radius);
      int limit_breaks = 0;

      for (ItemEntity e : entity.level().getEntitiesOfClass(ItemEntity.class, aabb)) {
         if (EffectUtil.inItemBlacklist(e.getItem().getItem(), MTConfig.POWER_CONSOLIDATION_TIER_UPGRADE_ITEM, e.level())) {
            limit_breaks += e.getItem().getCount();
            e.discard();
         }
      }

      double ret = MTConfig.POWER_CONSOLIDATION_ATTACK_CAP;
      if (MTConfig.POWER_CONSOLIDATION_TIERS.size() > 0) {
         ret = MTConfig.POWER_CONSOLIDATION_TIERS.get(0);
         if (limit_breaks > 0) {
            ret = MTConfig.POWER_CONSOLIDATION_TIERS.get(Math.min(MTConfig.POWER_CONSOLIDATION_TIERS.size() - 1, limit_breaks));
         }
      }

      return ret;
   }

   public static boolean isItemAllowed(ItemStack s, Level level) {
      boolean blacklist = MTConfig.POWER_CONSOLIDATION_CALIBURN_BLACKLIST_TOGGLE;
      List<? extends String> list = MTConfig.POWER_CONSOLIDATION_BLACKLIST;
      boolean match = false;

      for (int i = 0; i < list.size(); i++) {
         if (list.get(i) != null
            && s != null
            && Utils.getRegistryKey(s.getItem(), level) != null
            && list.get(i).equals(Utils.getRegistryName(s.getItem(), level))) {
            match = true;
            break;
         }
      }

      if (s != null && !MTConfig.POWER_CONSOLIDATION_PROJECTIONS_ALLOWED && ProjectionSpellEffect.getProjected(s) && s.getItem() instanceof Caliburn) {
         return false;
      } else if (match && blacklist) {
         return false;
      } else {
         return match && !blacklist ? true : !match && blacklist;
      }
   }

   public static BlockPos findCenter(ConcurrentSet<BlockPos> connected) {
      int minX = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int minZ = Integer.MAX_VALUE;
      int maxZ = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (BlockPos p : connected) {
         if (p.getX() < minX) {
            minX = p.getX();
         }

         if (p.getX() > maxX) {
            maxX = p.getX();
         }

         if (p.getZ() < minZ) {
            minZ = p.getZ();
         }

         if (p.getZ() > maxZ) {
            maxZ = p.getZ();
         }

         if (p.getY() > maxY) {
            maxY = p.getY();
         }
      }

      return new BlockPos(average(maxX, minX), maxY + 1, average(maxZ, minZ));
   }

   public static int average(int a, int b) {
      return (a + b) / 2;
   }

   public static boolean lakeThreshhold(BlockPos p, ConcurrentSet<BlockPos> connected, Level world) {
      int threshhold = MTConfig.POWER_CONSOLIDATION_LAKE_THRESHHOLD;
      if (connected.size() > threshhold) {
         return true;
      }

      if (connected.contains(p)) {
         return false;
      }

      boolean ret = false;
      if (world != null) {
         if (world.getBlockState(p).getBlock() instanceof MurkyWaterBlock) {
            connected.add(p);
         }

         if (world.getBlockState(p.above()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.above(), connected, world);
            if (ret) {
               return true;
            }
         }

         if (world.getBlockState(p.below()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.below(), connected, world);
            if (ret) {
               return true;
            }
         }

         if (world.getBlockState(p.south()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.south(), connected, world);
            if (ret) {
               return true;
            }
         }

         if (world.getBlockState(p.west()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.west(), connected, world);
            if (ret) {
               return true;
            }
         }

         if (world.getBlockState(p.east()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.east(), connected, world);
            if (ret) {
               return true;
            }
         }

         if (world.getBlockState(p.north()).getBlock() instanceof MurkyWaterBlock) {
            ret = lakeThreshhold(p.north(), connected, world);
            if (ret) {
               return true;
            }
         }
      }

      return ret;
   }

}
