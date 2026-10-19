package stepsword.mahoutsukai.item.spells.mystic;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.mystic.CupOfHeavenMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class CupOfHeavenSpellScroll extends SpellScroll {
   public CupOfHeavenSpellScroll() {
      super("cup_of_heaven");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.HEAVENS_CUP_MANA_COST;
   }

   @Override
   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide() && entityLiving instanceof Player user) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (matchCaster(user, scrollMahou)) {
            this.doSpell(user, scrollMahou, stack, consume);
         }
      }
   }

   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack, boolean consume) {
      if (scrollMahou != null) {
         IMahou mahou = Utils.getPlayerMahou(user);
         if (mahou != null) {
            double d = MTConfig.HEAVENS_CUP_START_DISTANCE;
            double minD = d * d * 4.0;
            CupOfHeavenMahoujinTileEntity start = null;
            Vec3 v = user.position();

            for (BlockPos p : Utils.findTilesInRange(user, (int)d, a -> a instanceof CupOfHeavenMahoujinTileEntity)) {
               if (user.level().getBlockEntity(p) instanceof CupOfHeavenMahoujinTileEntity mte
                  && mte.getCasterUUID() != null
                  && mte.getCasterUUID().equals(user.getUUID())) {
                  double curr = distanceTo(p, v, d * d * 4.0);
                  if (curr < minD) {
                     minD = curr;
                     start = mte;
                  }
               }
            }

            if (start == null) {
               return false;
            }

            start.buildNetwork(
               user.getUUID(),
               network -> {
                  if (network.size() > 2) {
                     final List<BlockPos> hull = convexHull(network);
                     int internal = network.size() - hull.size();
                     BlockPos minZ = null;
                     BlockPos minX = null;
                     BlockPos maxX = null;
                     BlockPos maxZ = null;

                     for (BlockPos pxx : network) {
                        if (minX == null || minX.getX() > pxx.getX()) {
                           minX = pxx;
                        }

                        if (maxX == null || maxX.getX() < pxx.getX()) {
                           maxX = pxx;
                        }

                        if (minZ == null || minZ.getZ() > pxx.getZ()) {
                           minZ = pxx;
                        }

                        if (maxZ == null || maxZ.getZ() < pxx.getZ()) {
                           maxZ = pxx;
                        }
                     }

                     final double maxx = maxX.getX();
                     AABB aabb = new AABB(
                        new BlockPos(minX.getX(), user.level().getMinY(), minZ.getZ()).getCenter(),
                        new BlockPos(maxX.getX(), user.level().getMaxY(), maxZ.getZ()).getCenter()
                     );
                     List<LivingEntity> affected = user.level().getEntitiesOfClass(LivingEntity.class, aabb, new Predicate<LivingEntity>() {
                        public boolean apply(@Nullable LivingEntity input) {
                           return input != null && CupOfHeavenSpellScroll.isInside(hull, input.position(), maxx);
                        }
                     });
                     int duration = MTConfig.HEAVENS_CUP_DURATION;
                     int g = internal;
                     int external = hull.size();
                     List<? extends String> effects = MTConfig.HEAVENS_CUP_EFFECTS;
                     int[] aa = new int[effects.size()];

                     for (int e = 0; e < effects.size(); e++) {
                        for (aa[e] = 0; internal % Utils.primes[e] == 0 && internal > 0; internal /= Utils.primes[e]) {
                           aa[e]++;
                        }
                     }

                     for (int e = 0; e < effects.size(); e++) {
                        while (external % Utils.primes[e] == 0 && external > 0) {
                           aa[e]++;
                           external /= Utils.primes[e];
                        }
                     }

                     int manacost = 0;

                     for (int px = 0; px < aa.length; px++) {
                        manacost += aa[px] * Utils.primes[px];
                     }

                     manacost = (int)(manacost * Math.ceil(Math.sqrt((maxZ.getZ() - minZ.getZ()) * (maxX.getX() - minX.getX()))));
                     manacost *= MTConfig.HEAVENS_CUP_MANA_COST;
                     if (PlayerManaManager.drainMana(user, manacost, false, false) == manacost) {
                        for (LivingEntity a : affected) {
                           for (int aq = 0; aq < aa.length; aq++) {
                              if (aa[aq] > 0) {
                                 a.addEffect(new MobEffectInstance(getRegisteredMobEffect(effects.get(aq)), duration, aa[aq] - 1));
                              }
                           }
                        }

                        boolean allzero = true;
                        boolean allnotzero = true;

                        for (int aq = 0; aq < aa.length; aq++) {
                           if (aa[aq] > 0) {
                              allzero = false;
                           }

                           if (aa[aq] <= 0) {
                              allnotzero = false;
                           }
                        }

                        if (allnotzero && aa.length > 0) {
                           ((MahouTrigger)ModTriggers.GREATER_GRAIL).trigger((ServerPlayer)user);
                           user.sendOverlayMessage(
                              Component.literal(ChatFormatting.AQUA + "Greater Grail found. External size: " + hull.size() + ". Internal size: " + g)
                           );
                        } else if (allzero && aa.length > 0) {
                           user.sendOverlayMessage(
                              Component.literal(
                                 ChatFormatting.RED
                                    + "Lesser Grail found, but internal count is imbalanced. External size: "
                                    + hull.size()
                                    + ". Internal size: "
                                    + g
                              )
                           );
                        } else if (aa.length > 0) {
                           ((MahouTrigger)ModTriggers.LESSER_GRAIL).trigger((ServerPlayer)user);
                           user.sendOverlayMessage(
                              Component.literal(ChatFormatting.GREEN + "Lesser Grail found. External size: " + hull.size() + ". Internal size: " + g)
                           );
                        }

                        this.deleteScroll(true, user, consume, stack);
                     } else {
                        user.sendOverlayMessage(
                           Component.literal(
                              ChatFormatting.RED
                                 + "Not enough mana to activate grail. External size: "
                                 + hull.size()
                                 + ". Internal size: "
                                 + g
                                 + ". Mana needed: "
                                 + manacost
                                 + "."
                           )
                        );
                     }
                  }
               }
            );
         }
      }

      return false;
   }

   public void deleteScroll(boolean doSpell, Player user, boolean consume, ItemStack stack) {
      if(doSpell)stepsword.mahoutsukai.fabric.SpellCasting.completed(user,this);
      if (doSpell && (!user.isCreative() || MTConfig.CREATIVE_MODE_SPELLS) && consume) {
         stack.shrink(1);
      }
   }

   public static boolean onSegment(BlockPos p, Vec3 q, BlockPos r) {
      BlockPos bpq = Utils.toBlockPos(q);
      return bpq.getX() <= Math.max(p.getX(), r.getX())
         && bpq.getX() >= Math.min(p.getX(), r.getX())
         && bpq.getZ() <= Math.max(p.getZ(), r.getZ())
         && bpq.getZ() >= Math.min(p.getZ(), r.getZ());
   }

   static boolean isInside(List<BlockPos> polygon, Vec3 p, double maxX) {
      if (polygon.size() < 2) {
         return false;
      }

      if (polygon.size() == 2) {
         return onSegment(polygon.get(0), p, polygon.get(1).offset(1, 0, 1));
      }

      Vec3 extreme = new Vec3(maxX + 5000000.0, 0.0, p.z);
      int count = 0;
      int i = 0;

      do {
         int next = (i + 1) % polygon.size();
         if (doIntersect(EffectUtil.fromBlockPos(polygon.get(i)), EffectUtil.fromBlockPos(polygon.get(next)), p, extreme)) {
            if (orientation(EffectUtil.fromBlockPos(polygon.get(i)), p, EffectUtil.fromBlockPos(polygon.get(next))) == 0) {
               return onSegment(polygon.get(i), p, polygon.get(next));
            }

            count++;
         }

         i = next;
      } while (i != 0);

      return count % 2 == 1;
   }

   public static double distanceTo(BlockPos from, Vec3 to, double d) {
      if (from != null && to != null) {
         BlockPos a = new BlockPos(from.getX(), 0, from.getZ());
         return a.distToCenterSqr(to.x, 0.0, to.z);
      } else {
         return d;
      }
   }

   public static List<BlockPos> convexHull(HashSet<BlockPos> ps) {
      List<BlockPos> ret = new ArrayList<>();
      ArrayList<Vec3> pts = new ArrayList<>();
      Vec3 minV = null;
      HashSet<BlockPos> points = new HashSet<>();
      boolean found = false;

      for (BlockPos p : ps) {
         found = false;

         for (BlockPos z : points) {
            if (z.getZ() == p.getZ() && z.getX() == p.getX()) {
               found = true;
            }
         }

         if (!found) {
            points.add(p);
         }
      }

      for (BlockPos p : points) {
         Vec3 tmpV = EffectUtil.fromBlockPos(p);
         pts.add(tmpV);
      }

      pts.sort((a, bx) -> {
         if (a.z == bx.z) {
            if (a.x > bx.x) {
               return -1;
            } else {
               return a.x < bx.x ? 1 : 0;
            }
         } else if (a.z < bx.z) {
            return -1;
         } else {
            return a.z > bx.z ? 1 : 0;
         }
      });
      Vec3 initial = pts.get(0);
      pts.remove(initial);
      if (initial != null) {
         pts.sort((a, bx) -> {
            double cross = (a.x - initial.x) * (bx.z - initial.z) - (bx.x - initial.x) * (a.z - initial.z);
            if (cross > 0.0) {
               return 1;
            } else if (cross < 0.0) {
               return -1;
            } else {
               double adist = (a.x - initial.x) * (a.x - initial.x) + (a.z - initial.z) * (a.z - initial.z);
               double bdist = (bx.x - initial.x) * (bx.x - initial.x) + (bx.z - initial.z) * (bx.z - initial.z);
               if (adist < bdist) {
                  return 1;
               } else {
                  return adist > bdist ? -1 : 0;
               }
            }
         });
      }

      for (int m = 1; m < pts.size(); m++) {
         Vec3 a = pts.get(m);
         Vec3 b = pts.get(m - 1);
         double cross = (a.x - initial.x) * (b.z - initial.z) - (b.x - initial.x) * (a.z - initial.z);
         double cotanA = -(a.x - initial.x) / (a.z - initial.z);
         double cotanB = -(b.x - initial.x) / (b.z - initial.z);
         if (cross == 0.0) {
            pts.remove(m);
            m--;
         }
      }

      Stack<Vec3> stack = new Stack<>();
      stack.push(initial);

      for (Vec3 pt : pts) {
         while (stack.size() > 1 && ccw(nextToTop(stack), stack.peek(), pt) <= 0) {
            stack.pop();
         }

         stack.push(pt);
      }

      while (!stack.empty()) {
         Vec3 tmp = stack.pop();
         ret.add(Utils.toBlockPos(tmp.x, tmp.y, tmp.z));
      }

      return ret;
   }

   public static Vec3 nextToTop(Stack<Vec3> s) {
      Vec3 tmp = s.pop();
      Vec3 ret = s.peek();
      s.push(tmp);
      return ret;
   }

   public static int ccw(Vec3 a, Vec3 b, Vec3 c) {
      double area2 = (-b.x + a.x) * (c.z - a.z) - (b.z - a.z) * (-c.x + a.x);
      double ball = 1.0E-7;
      if (area2 < ball && area2 > 0.0) {
         return 0;
      } else if (area2 > -ball && area2 < 0.0) {
         return 0;
      } else if (area2 < 0.0) {
         return -1;
      } else {
         return area2 > 0.0 ? 1 : 0;
      }
   }

   public static boolean doIntersect(Vec3 p1, Vec3 q1, Vec3 p2, Vec3 q2) {
      int o1 = orientation(p1, q1, p2);
      int o2 = orientation(p1, q1, q2);
      int o3 = orientation(p2, q2, p1);
      int o4 = orientation(p2, q2, q1);
      if (o1 != o2 && o3 != o4) {
         return true;
      } else if (o1 == 0 && onSegment(Utils.toBlockPos(p1), p2, Utils.toBlockPos(q1))) {
         return true;
      } else if (o2 == 0 && onSegment(Utils.toBlockPos(p1), q2, Utils.toBlockPos(q1))) {
         return true;
      } else {
         return o3 == 0 && onSegment(Utils.toBlockPos(p2), p1, Utils.toBlockPos(q2))
            ? true
            : o4 == 0 && onSegment(Utils.toBlockPos(p2), q1, Utils.toBlockPos(q2));
      }
   }

   public static int orientation(Vec3 p, Vec3 q, Vec3 r) {
      double val = (q.z - p.z) * (-r.x + q.x) - (-q.x + p.x) * (r.z - q.z);
      if (val == 0.0) {
         return 0;
      } else {
         return val > 0.0 ? 1 : 2;
      }
   }

   private static Holder<MobEffect> getRegisteredMobEffect(String id) {
      return (Holder<MobEffect>)BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(id)).orElseThrow();
   }
}
