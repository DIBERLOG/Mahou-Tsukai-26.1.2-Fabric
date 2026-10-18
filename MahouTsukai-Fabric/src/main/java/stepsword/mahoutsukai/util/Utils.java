package stepsword.mahoutsukai.util;
import net.minecraft.util.Mth;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;
import stepsword.mahoutsukai.fabric.GemData;
import stepsword.mahoutsukai.fabric.MahouHolder;

/** Adapted helpers used by the currently connected mana economy. */
public final class Utils {
    public static stepsword.mahoutsukai.datacomponents.lance.LanceMahou getLanceMahou(ItemStack stack) {
        return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT,new stepsword.mahoutsukai.datacomponents.lance.LanceMahou());
    }
    public static void setLanceMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.lance.LanceMahou value) {
        stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT,value);
    }
 public static boolean isArmor(net.minecraft.world.item.ItemStack stack){var equipped=stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);return equipped!=null&&equipped.slot().getType()==net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR;}
 public static stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant getProjectorEnchant(net.minecraft.world.item.ItemStack stack){return stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);}
 public static void setProjectorEnchant(net.minecraft.world.item.ItemStack stack,stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant v){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT,v.copy());}
 public static boolean hasProjectorEnchant(net.minecraft.world.item.ItemStack stack){return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);}

    public static stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou getLivingMahou(net.minecraft.world.entity.LivingEntity entity) {
        return ((stepsword.mahoutsukai.fabric.LivingMahouHolder)entity).mahoutsukai$getLivingMahou();
    }
    public static IMahou getPlayerMahou(Player player) {
        return player instanceof MahouHolder holder ? holder.mahoutsukai$getMahou() : null;
    }
    public static stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou getScrollMahou(ItemStack stack) {
        var type = stepsword.mahoutsukai.datacomponents.DataComponents.SCROLL_COMPONENT;
        var value = stack.get(type);
        if (value == null) {
            value = new stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou();
            stack.set(type, value);
            return value;
        }
        return value.copy();
    }
    public static stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou getRuleBreakerMahou(ItemStack stack) {
        var type = stepsword.mahoutsukai.datacomponents.DataComponents.RULE_BREAKER_COMPONENT;
        var value = stack.get(type);
        if (value == null) { value = new stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou(); stack.set(type, value); return value; }
        return value.copy();
    }
    public static void setRuleBreakerMahou(ItemStack stack, stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou value) {
        stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.RULE_BREAKER_COMPONENT, value);
    }
    public static GemMahou getGemMahou(ItemStack stack) { return GemData.get(stack); }
    public static GemMahou setGemMahou(ItemStack stack, GemMahou value) { return GemData.set(stack, value); }
    public static boolean isBlockAir(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos) {
        var block = world.getBlockState(pos);
        return block.isAir() || block.is(net.minecraft.world.level.block.Blocks.AIR)
            || block.is(net.minecraft.world.level.block.Blocks.CAVE_AIR) || block.is(net.minecraft.world.level.block.Blocks.VOID_AIR);
    }
    public static stepsword.mahoutsukai.datacomponents.fiction.FictionMahou getFictionMahou(ItemStack stack) {
        var value = stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT);
        if (value == null) {
            value = new stepsword.mahoutsukai.datacomponents.fiction.FictionMahou();
            stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT, value);
            return value;
        }
        return value.copy();
    }
   public static float avgStep(float a, float b, float steps, float step) {
      float diff = Math.abs(b - a);
      float onestep = diff / steps;
      return a < b ? a + onestep * step : a - onestep * step;
   }

   public static float[][] permute(float[][] in, int offset) {
      float[][] ret = new float[in.length][in[0].length];

      for (int i = 0; i < in.length; i++) {
         for (int j = 0; j < in[0].length; j++) {
            ret[Math.floorMod(i + offset, in.length)][j] = in[i][j];
         }
      }

      return ret;
   }

    public static void setScrollMahou(ItemStack stack, stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou value) {
        stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SCROLL_COMPONENT, value);
    }
    public static net.minecraft.core.BlockPos toBlockPos(net.minecraft.world.phys.Vec3 pos) { return net.minecraft.core.BlockPos.containing(pos); }
    public static String getRegistryName(net.minecraft.world.level.block.Block block, net.minecraft.world.level.Level level) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
    }
    public static boolean giveOrDrop(Player player, ItemStack stack) { if(player==null || stack.isEmpty()) return false; if (!player.getInventory().add(stack)) player.drop(stack, false); return true; }
    public static java.util.ArrayList<net.minecraft.core.BlockPos> findTilesInRange(net.minecraft.world.entity.LivingEntity player, int range,
            java.util.function.Predicate<net.minecraft.world.level.block.entity.BlockEntity> pred) {
        var min = net.minecraft.world.level.ChunkPos.containing(new net.minecraft.core.BlockPos((int)(player.getX()-range),0,(int)(player.getZ()-range)));
        var max = net.minecraft.world.level.ChunkPos.containing(new net.minecraft.core.BlockPos((int)(player.getX()+range),0,(int)(player.getZ()+range)));
        var found = new java.util.ArrayList<net.minecraft.core.BlockPos>();
        for(int x=min.x();x<=max.x();x++) for(int z=min.z();z<=max.z();z++)
            player.level().getChunk(x,z).getBlockEntities().forEach((pos,entity)-> { if(pred.test(entity)) found.add(pos); });
        return found;
    }
   public static boolean GetIntersection(double fDst1, double fDst2, Vec3 P1, Vec3 P2, Utils.Vec3H Hit) {
      if (fDst1 * fDst2 >= 0.0) {
         return false;
      }

      if (fDst1 == fDst2) {
         return false;
      }

      Hit.v = P1.add(P2.subtract(P1).scale(-fDst1 / (fDst2 - fDst1)));
      return true;
   }

   public static boolean InBox(Vec3 Hit, Vec3 B1, Vec3 B2, int Axis) {
      if (Axis == 1 && Hit.z > B1.z && Hit.z < B2.z && Hit.y > B1.y && Hit.y < B2.y) {
         return true;
      } else {
         return Axis == 2 && Hit.z > B1.z && Hit.z < B2.z && Hit.x > B1.x && Hit.x < B2.x
            ? true
            : Axis == 3 && Hit.x > B1.x && Hit.x < B2.x && Hit.y > B1.y && Hit.y < B2.y;
      }
   }

   public static boolean CheckLineBox(Vec3 B1, Vec3 B2, Vec3 L1, Vec3 L2, Utils.Vec3H Hit) {
      if (L2.x < B1.x && L1.x < B1.x) {
         return false;
      } else if (L2.x > B2.x && L1.x > B2.x) {
         return false;
      } else if (L2.y < B1.y && L1.y < B1.y) {
         return false;
      } else if (L2.y > B2.y && L1.y > B2.y) {
         return false;
      } else if (L2.z < B1.z && L1.z < B1.z) {
         return false;
      } else if (L2.z > B2.z && L1.z > B2.z) {
         return false;
      } else if (L1.x > B1.x && L1.x < B2.x && L1.y > B1.y && L1.y < B2.y && L1.z > B1.z && L1.z < B2.z) {
         Hit.v = L1;
         return true;
      } else {
         return GetIntersection(L1.x - B1.x, L2.x - B1.x, L1, L2, Hit) && InBox(Hit.v, B1, B2, 1)
            || GetIntersection(L1.y - B1.y, L2.y - B1.y, L1, L2, Hit) && InBox(Hit.v, B1, B2, 2)
            || GetIntersection(L1.z - B1.z, L2.z - B1.z, L1, L2, Hit) && InBox(Hit.v, B1, B2, 3)
            || GetIntersection(L1.x - B2.x, L2.x - B2.x, L1, L2, Hit) && InBox(Hit.v, B1, B2, 1)
            || GetIntersection(L1.y - B2.y, L2.y - B2.y, L1, L2, Hit) && InBox(Hit.v, B1, B2, 2)
            || GetIntersection(L1.z - B2.z, L2.z - B2.z, L1, L2, Hit) && InBox(Hit.v, B1, B2, 3);
      }
   }

   public static boolean CheckLineBox(Vec3 B1, Vec3 B2, Vec3 L1, Vec3 L2) {
      return CheckLineBox(B1, B2, L1, L2, new Utils.Vec3H(L1));
   }

   public static boolean blockBetween(Vec3 e1, Vec3 e2, Level world) {
      Vec3 subtracted = e2.subtract(e1);
      Vec3 unit = subtracted.normalize();
      if (subtracted.length() > 1.0) {
         for (int i = 0; i < subtracted.length(); i++) {
            BlockPos pos = toBlockPos(e1.add(unit.scale(i)));
            BlockState bs = world.getBlockState(pos);
            if (!bs.isAir() && !bs.propagatesSkylightDown()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static class Vec3H {
      public Vec3 v;

      public Vec3H(Vec3 vec) {
         this.v = vec;
      }
   }
    /** Original SCROLL_GEAS targeting branch: strict ray/box hit, opaque-block check, nearest entity. */
    public static Entity selectGeasTarget(Player player) {
        return selectStrictTarget(player,entity -> !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST)
            && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity));
    }
    /** Original strict cursor branch, shared by Geas and Immunity Exchange. */
    public static Entity selectStrictTarget(Player player,java.util.function.Predicate<Entity> predicate) {
        double distance=stepsword.mahoutsukai.config.MTConfig.GLOBAL_LOOK_RANGE;
        var eyes=player.getEyePosition(1);var end=eyes.add(player.getLookAngle().scale(distance));
        return player.level().getEntities(player, player.getBoundingBox().inflate(distance), predicate)
            .stream().filter(entity -> {
                var box=entity.getBoundingBox();
                return CheckLineBox(new Vec3(box.minX,box.minY,box.minZ),new Vec3(box.maxX,box.maxY,box.maxZ),eyes,end)
                    && !blockBetween(eyes,box.getCenter(),player.level());
            }).min(java.util.Comparator.comparingDouble(entity->entity.position().distanceTo(eyes))).orElse(null);
    }
    public static String getRegistryName(Entity entity) { return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(); }
    public static net.minecraft.world.level.block.Block getBlockFromRegistryName(String name) {
        var id=net.minecraft.resources.Identifier.tryParse(name);
        return id==null ? net.minecraft.world.level.block.Blocks.AIR : net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(id);
    }
    public static Iterable<net.minecraft.core.Holder<net.minecraft.world.level.block.Block>> getBlockTagFromRegistryName(String name) {
        var id=net.minecraft.resources.Identifier.tryParse(name.startsWith("#") ? name.substring(1) : name);
        if(id==null) return java.util.List.of();
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,id))
            .map(tag->(Iterable<net.minecraft.core.Holder<net.minecraft.world.level.block.Block>>)tag).orElse(java.util.List.of());
    }
    public static void err(String message) { org.slf4j.LoggerFactory.getLogger("mahoutsukai").error(message); }
    private static java.util.Random random;
    public static java.util.Random getRandom(Level level) { if(random==null) random=new java.util.Random(level.getDefaultClockTime());return random; }
    public static stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData getWorldMahou(Level world) {
        return world != null && world.getServer()!=null ? new stepsword.mahoutsukai.dataattachments.worldsave.WorldMahou(world.getServer().overworld()).data : null;
    }
    public static BlockPos toBlockPos(double x,double y,double z) { return BlockPos.containing(x,y,z); }
    public static void schedule(Level world,Runnable task) { if(world instanceof net.minecraft.server.level.ServerLevel server) server.getServer().schedule(new net.minecraft.server.TickTask(1,task)); }
   public static double distanceToLine(Vec3 v, Vec3 w, Vec3 p) {
      Vec3 VW = v.subtract(w);
      if (VW.lengthSqr() == 0.0) {
         return p.distanceTo(v);
      } else {
         Vec3 z = v.subtract(VW.scale(v.subtract(p).dot(VW) / VW.dot(VW)));
         double dv = z.distanceToSqr(v);
         double dw = z.distanceToSqr(w);
         double dvw = v.distanceToSqr(w);
         if (dv > dvw) {
            return p.distanceTo(w);
         } else {
            return dw > dvw ? p.distanceTo(v) : p.distanceTo(z);
         }
      }
   }
   public static Entity selectEntityNearCursor(Player player, double distance, Level world, java.util.function.Predicate<Entity> pred, boolean nearAllowed) {
      java.util.List<Entity> ret = selectEntitiesNearCursor(1, player, distance, 45.0, world, pred, nearAllowed);
      return ret != null && !ret.isEmpty() ? ret.getFirst() : null;
   }
   public static java.util.ArrayList<Entity> selectEntitiesNearCursor(
      int limit, Player player, double distance, double distanceFromLook, Level world, java.util.function.Predicate<Entity> pred, boolean nearAllowed
   ) {
      java.util.ArrayList<Entity> ret = null;
      if (player != null) {
         java.util.List<Entity> lst = world.getEntities(player, player.getBoundingBox().inflate(distance), pred);
         Vec3 eyes = player.getEyePosition(1.0F);
         Vec3 look = player.getLookAngle();
         Vec3 sightEnd = eyes.add(look.scale(distance));
         java.util.ArrayList<Entity> candidates = new java.util.ArrayList<>();

         for (Entity e : lst) {
            net.minecraft.world.phys.AABB aabb = e.getBoundingBox();
            boolean qualifies = false;
            boolean lookingDirectly = CheckLineBox(new Vec3(aabb.minX, aabb.minY, aabb.minZ), new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ), eyes, sightEnd);
            boolean blockBetween = blockBetween(eyes, e.getBoundingBox().getCenter(), e.level());
            boolean lookingAtMe = EffectUtil.isLookingAtMe(e, player, (int)distanceFromLook);
            qualifies = lookingDirectly && !blockBetween || lookingAtMe && !blockBetween && nearAllowed;
            if (qualifies) {
               candidates.add(e);
            }
         }

         if (nearAllowed) {
            candidates.sort(java.util.Comparator.comparingDouble(a -> distanceToLine(eyes, sightEnd, a.getBoundingBox().getCenter())));
         } else {
            candidates.sort(java.util.Comparator.comparingDouble(a -> a.position().distanceTo(eyes)));
         }

         ret = candidates;
         if (limit < ret.size() && !ret.isEmpty()) {
            ret = new java.util.ArrayList<>(ret.subList(0, limit));
         }
      }

      return ret;
   }
    public static stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou getKodokuMahou(ItemStack stack) {
        var component=stepsword.mahoutsukai.datacomponents.DataComponents.KODOKU_COMPONENT;
        var value=stack.get(component);
        if(value==null) {value=new stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou();stack.set(component,value);}
        return value.copy();
    }
    public static void setKodokuMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou value) { stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.KODOKU_COMPONENT,value); }
    public static stepsword.mahoutsukai.datacomponents.sword.SwordMahou getSwordMahou(ItemStack stack) {
        var type=stepsword.mahoutsukai.datacomponents.DataComponents.SWORD_COMPONENT;var value=stack.get(type);
        if(value==null) {value=new stepsword.mahoutsukai.datacomponents.sword.SwordMahou();stack.set(type,value);}return value.copy();
    }
    public static void setSwordMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.sword.SwordMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SWORD_COMPONENT,value);}
    public static stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou getStrengtheningMahou(ItemStack stack) {
        var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);
        return value==null ? new stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou() : value.copy();
    }
    public static void setStrengtheningMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT,value);}
    public static boolean hasStrengtheningMahou(ItemStack stack) {return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);}
    public static boolean isTool(ItemStack stack) {return stack.getItem().components().has(net.minecraft.core.component.DataComponents.TOOL);}
    public static boolean isWeapon(net.minecraft.world.item.Item item) {return item.components().has(net.minecraft.core.component.DataComponents.WEAPON);}
    public enum MAHOU_SELECTION {SCROLL_REALITY_MARBLE,SCROLL_POSSESS_ENTITY,INSIGHT,WEAPON_NO_NEAR,WEAPON,SCROLL_PROJECTION,SELECTIVE_DISPLACEMENT,SCROLL_RETRIBUTION,SCROLL_IMMUNITY_EXCHANGE,SCROLL_DAMAGE_REPLICATION,SCROLL_GEAS}
    public static boolean getPredicate(java.util.HashSet<MAHOU_SELECTION> set,Entity entity,Player player) {
        if(set.contains(MAHOU_SELECTION.SCROLL_PROJECTION)) return entity!=player && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity || entity instanceof net.minecraft.world.entity.decoration.ItemFrame || entity instanceof net.minecraft.world.entity.projectile.arrow.ThrownTrident);
        if(set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT)) return entity!=player;
        if(set.contains(MAHOU_SELECTION.SCROLL_GEAS)) return entity!=player && !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST) && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity);
        return !set.isEmpty() && entity!=player && entity instanceof net.minecraft.world.entity.LivingEntity;
    }
    public static boolean nearAllowed(java.util.HashSet<MAHOU_SELECTION> set) {return set.contains(MAHOU_SELECTION.SCROLL_POSSESS_ENTITY) || set.contains(MAHOU_SELECTION.WEAPON) || set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT) || set.contains(MAHOU_SELECTION.SCROLL_RETRIBUTION) || set.contains(MAHOU_SELECTION.SCROLL_DAMAGE_REPLICATION);}
    public static stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou getProjectedMahou(ItemStack stack) {var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT);return value==null ? new stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou() : value.copy();}
    public static void setProjectedMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT,value);}
    public static boolean hasProjectedMahou(ItemStack stack) {return stack.has(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTED_COMPONENT);}
    public static stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou getStackHolderMahou(ItemStack stack) {var value=stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STACK_HOLDERS_COMPONENT);return value==null ? new stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou() : value.copy();}
    public static void setStackHolderMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou value) {stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.STACK_HOLDERS_COMPONENT,value);}
    public static java.util.ArrayList<ItemStack> getEquipmentSlotList(net.minecraft.world.entity.LivingEntity entity) {var result=new java.util.ArrayList<ItemStack>();for(var slot:net.minecraft.world.entity.EquipmentSlot.VALUES) result.add(entity.getItemBySlot(slot));return result;}
    public static void updateHeldItem(Player player) {if(player instanceof net.minecraft.server.level.ServerPlayer sp) sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket(sp.getInventory().getSelectedSlot()));}
    public static void debug(String message) { org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug(message); }
    public static boolean isInMurkyWater(net.minecraft.world.entity.Entity entity) {
        if(entity==null)return false;
        double eyes=entity.getY()+entity.getEyeHeight();
        var state=entity.level().getBlockState(net.minecraft.core.BlockPos.containing(entity.getEyePosition(1)));
        double filled=state.getBlock() instanceof stepsword.mahoutsukai.fluids.MurkyWaterBlock ? state.getFluidState().getOwnHeight()+1.0/9.0 : 0;
        return eyes-Math.floor(eyes)<filled;
    }
   public static int[] primes = new int[]{
      2,
      3,
      5,
      7,
      11,
      13,
      17,
      19,
      23,
      29,
      31,
      37,
      41,
      43,
      47,
      53,
      59,
      61,
      67,
      71,
      73,
      79,
      83,
      89,
      97,
      101,
      103,
      107,
      109,
      113,
      127,
      131,
      137,
      139,
      149,
      151,
      157,
      163,
      167,
      173,
      179,
      181,
      191,
      193,
      197,
      199,
      211,
      223,
      227,
      229,
      233,
      239,
      241,
      251,
      257,
      263,
      269,
      271,
      277,
      281,
      283,
      293,
      307,
      311,
      313,
      317,
      331,
      337,
      347,
      349,
      353,
      359,
      367,
      373,
      379,
      383,
      389,
      397,
      401,
      409,
      419,
      421,
      431,
      433,
      439,
      443,
      449,
      457,
      461,
      463,
      467,
      479,
      487,
      491,
      499,
      503,
      509,
      521,
      523,
      541,
      547,
      557,
      563,
      569,
      571,
      577,
      587,
      593,
      599,
      601,
      607,
      613,
      617,
      619,
      631,
      641,
      643,
      647,
      653,
      659,
      661,
      673,
      677,
      683,
      691,
      701,
      709,
      719,
      727,
      733,
      739,
      743,
      751,
      757,
      761,
      769,
      773,
      787,
      797,
      809,
      811,
      821,
      823,
      827,
      829,
      839,
      853,
      857,
      859,
      863,
      877,
      881,
      883,
      887,
      907,
      911,
      919,
      929,
      937,
      941,
      947,
      953,
      967,
      971,
      977,
      983,
      991,
      997,
      1009,
      1013,
      1019,
      1021,
      1031,
      1033,
      1039,
      1049,
      1051,
      1061,
      1063,
      1069,
      1087,
      1091,
      1093,
      1097,
      1103,
      1109,
      1117,
      1123,
      1129,
      1151,
      1153,
      1163,
      1171,
      1181,
      1187,
      1193,
      1201,
      1213,
      1217,
      1223,
      1229,
      1231,
      1237,
      1249,
      1259,
      1277,
      1279,
      1283,
      1289,
      1291,
      1297,
      1301,
      1303,
      1307,
      1319,
      1321,
      1327,
      1361,
      1367,
      1373,
      1381,
      1399,
      1409,
      1423,
      1427,
      1429,
      1433,
      1439,
      1447,
      1451,
      1453,
      1459,
      1471,
      1481,
      1483,
      1487,
      1489,
      1493,
      1499,
      1511,
      1523,
      1531,
      1543,
      1549,
      1553,
      1559,
      1567,
      1571,
      1579,
      1583,
      1597,
      1601,
      1607,
      1609,
      1613,
      1619,
      1621,
      1627,
      1637,
      1657,
      1663,
      1667,
      1669,
      1693,
      1697,
      1699,
      1709,
      1721,
      1723,
      1733,
      1741,
      1747,
      1753,
      1759,
      1777,
      1783,
      1787,
      1789,
      1801,
      1811,
      1823,
      1831,
      1847,
      1861,
      1867,
      1871,
      1873,
      1877,
      1879,
      1889,
      1901,
      1907,
      1913,
      1931,
      1933,
      1949,
      1951,
      1973,
      1979,
      1987,
      1993,
      1997,
      1999,
      2003,
      2011,
      2017,
      2027,
      2029,
      2039,
      2053,
      2063,
      2069,
      2081,
      2083,
      2087,
      2089,
      2099,
      2111,
      2113,
      2129,
      2131,
      2137,
      2141,
      2143,
      2153,
      2161,
      2179,
      2203,
      2207,
      2213,
      2221,
      2237,
      2239,
      2243,
      2251,
      2267,
      2269,
      2273,
      2281,
      2287,
      2293,
      2297,
      2309,
      2311,
      2333,
      2339,
      2341,
      2347,
      2351,
      2357,
      2371,
      2377,
      2381,
      2383,
      2389,
      2393,
      2399,
      2411,
      2417,
      2423,
      2437,
      2441,
      2447,
      2459,
      2467,
      2473,
      2477,
      2503,
      2521,
      2531,
      2539,
      2543,
      2549,
      2551,
      2557,
      2579,
      2591,
      2593,
      2609,
      2617,
      2621,
      2633,
      2647,
      2657,
      2659,
      2663,
      2671,
      2677,
      2683,
      2687,
      2689,
      2693,
      2699,
      2707,
      2711,
      2713,
      2719,
      2729,
      2731,
      2741,
      2749,
      2753,
      2767,
      2777,
      2789,
      2791,
      2797,
      2801,
      2803,
      2819,
      2833,
      2837,
      2843,
      2851,
      2857,
      2861,
      2879,
      2887,
      2897,
      2903,
      2909,
      2917,
      2927,
      2939,
      2953,
      2957,
      2963,
      2969,
      2971,
      2999,
      3001,
      3011,
      3019,
      3023,
      3037,
      3041,
      3049,
      3061,
      3067,
      3079,
      3083,
      3089,
      3109,
      3119,
      3121,
      3137,
      3163,
      3167,
      3169,
      3181,
      3187,
      3191,
      3203,
      3209,
      3217,
      3221,
      3229,
      3251,
      3253,
      3257,
      3259,
      3271,
      3299,
      3301,
      3307,
      3313,
      3319,
      3323,
      3329,
      3331,
      3343,
      3347,
      3359,
      3361,
      3371,
      3373,
      3389,
      3391,
      3407,
      3413,
      3433,
      3449,
      3457,
      3461,
      3463,
      3467,
      3469,
      3491,
      3499,
      3511,
      3517,
      3527,
      3529,
      3533,
      3539,
      3541,
      3547,
      3557,
      3559,
      3571
   };
 public static net.minecraft.world.phys.HitResult rayTraceEyes(net.minecraft.world.entity.LivingEntity entity,double length){var start=entity.getEyePosition(1);return entity.level().clip(new net.minecraft.world.level.ClipContext(start,start.add(entity.getLookAngle().scale(length)),net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY,entity));}

 public static stepsword.mahoutsukai.dataattachments.settingsmahou.SettingsMahou getSettingsMahou(Player player){return ((stepsword.mahoutsukai.fabric.SettingsHolder)player).mahoutsukai$getSettings();}
 public static String getRegistryName(net.minecraft.world.effect.MobEffect effect,net.minecraft.world.level.Level world){return net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect).toString();}
 public static java.util.List<Integer> readIntListFromBuffer(io.netty.buffer.ByteBuf buffer){int n=buffer.readInt();if(n<0 || n>1024)throw new IllegalArgumentException("Invalid settings list length");var values=new java.util.ArrayList<Integer>();for(int i=0;i<n;i++)values.add(buffer.readInt());return values;}
 public static void writeIntListToBuffer(io.netty.buffer.ByteBuf buffer,java.util.List<Integer> values){buffer.writeInt(values.size());for(int value:values)buffer.writeInt(value);}
 public static float colorConvert(int color){return color/255.0f;}
   public static float yawPartial(float y1, float y2, float partialTicks) {
      float ret = y1;
      y1 = mod(y1, 360);
      y2 = mod(y2, 360);
      boolean goUp = yawUp(y1, y2);
      float addme = partialTicks * Math.min(mod(y1 - y2, 360), mod(y2 - y1, 360));
      if (!goUp) {
         addme *= -1.0F;
      }

      ret += addme;
      return mod(ret, 360);
   }
   public static float mod(float n, int m) {
      float ret = n % m;
      return ret < 0.0F ? ret + m : ret;
   }
   public static boolean yawUp(float yaw1, float yaw2) {
      return yaw1 < yaw2 ? yaw1 + 360.0F - yaw2 > yaw2 - yaw1 : yaw2 + 360.0F - yaw1 < yaw1 - yaw2;
   }
 public static net.minecraft.resources.Identifier getRegistryKey(net.minecraft.world.level.block.Block block,net.minecraft.world.level.Level world){return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);}

 public static stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou getModeMahou(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.MODE_COMPONENT,new stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou()).copy();}
 public static int getMode(ItemStack stack){return getModeMahou(stack).getMode();}
 public static void setMode(ItemStack stack,int mode){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.MODE_COMPONENT,new stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou(mode));}

 public static boolean getPredicate(MAHOU_SELECTION selection,Entity entity,Player player){return getPredicate(new java.util.HashSet<>(java.util.Set.of(selection)),entity,player);}
 public static boolean nearAllowed(MAHOU_SELECTION selection){return nearAllowed(new java.util.HashSet<>(java.util.Set.of(selection)));}

 public static net.minecraft.resources.Identifier getRegistryKey(Entity entity,Level world){return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());}

 public static stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou getColorMahou(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.COLORS_COMPONENT,new stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou()).copy();}
 public static stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou getSecondColorMahou(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.SECOND_COLORS_COMPONENT,new stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou()).copy();}
 public static void setColorMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.COLORS_COMPONENT,value.copy());}
 public static void setSecondColorMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SECOND_COLORS_COMPONENT,value.copy());}
 public static void color(ItemStack stack,float r,float g,float b,float r2,float g2,float b2){setColorMahou(stack,new stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou(java.util.List.of(new MahouColor(r,g,b),new MahouColor(r2,g2,b2))));}
 public static void secondcolor(ItemStack stack,float r,float g,float b,float r2,float g2,float b2){setSecondColorMahou(stack,new stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou(java.util.List.of(new MahouColor(r,g,b),new MahouColor(r2,g2,b2))));}
 public static float[] getColor(ItemStack stack){return getColor(stack,false);}
 public static float[] getColor(ItemStack stack,boolean second){var colors=(second?getSecondColorMahou(stack):getColorMahou(stack)).getColors();if(colors.size()<2)return null;var a=colors.get(0);var b=colors.get(1);return new float[]{a.r,a.g,a.b,b.r,b.g,b.b};}

 public static void setAmmoStack(ItemStack bow,ItemStack ammo){setStackHolderMahou(bow,new stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou(java.util.Optional.of(ammo.copy())));}
 public static ItemStack getAmmoStack(ItemStack bow){return getStackHolderMahou(bow).getStack().orElse(ItemStack.EMPTY).copy();}

 public static boolean isSword(ItemStack stack){return stack.getItem().components().has(net.minecraft.core.component.DataComponents.WEAPON);}
 public static boolean isSword(net.minecraft.world.item.Item item){return item.components().has(net.minecraft.core.component.DataComponents.WEAPON);}

 public static net.minecraft.resources.Identifier getRegistryKey(net.minecraft.world.item.Item item,Level world){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);}

 public static String getRegistryName(net.minecraft.world.item.Item item,Level world){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString();}

 public static java.util.List<ItemStack> snapshotNestedItems(ItemStack stack){
  var container=stack.get(net.minecraft.core.component.DataComponents.CONTAINER);if(container!=null)return container.nonEmptyItemCopyStream().toList();
  var bundle=stack.get(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS);if(bundle!=null)return bundle.itemCopyStream().toList();
  var storage=net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.withConstant(stack).find(net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.ITEM);var result=new java.util.ArrayList<ItemStack>();if(storage!=null)for(var view:storage){if(!view.isResourceBlank() && view.getAmount()>0)result.add(view.getResource().toStack((int)Math.min(Integer.MAX_VALUE,view.getAmount())));if(result.size()>=4096)break;}return result;
 }

 public static stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData getMysticCodeData(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.MYSTIC_CODE_COMPONENT,new stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData()).copy();}
 public static void setMysticCodeData(ItemStack stack,stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.MYSTIC_CODE_COMPONENT,value.copy());}
 public static stepsword.mahoutsukai.containers.MysticCodeContents getMCInventory(ItemStack stack){return stack.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode?new stepsword.mahoutsukai.containers.MysticCodeContents(stack):null;}

 public static stepsword.mahoutsukai.datacomponents.replica.ReplicaMahou getReplicaMahou(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.REPLICA_COMPONENT,new stepsword.mahoutsukai.datacomponents.replica.ReplicaMahou()).copy();}
 public static void setReplicaMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.replica.ReplicaMahou data){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.REPLICA_COMPONENT,data.copy());}

 public static stepsword.mahoutsukai.datacomponents.selectedtarget.SelectedTargetMahou getSelectedTarget(ItemStack stack){return stack.getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.SELECTED_TARGET_COMPONENT,new stepsword.mahoutsukai.datacomponents.selectedtarget.SelectedTargetMahou()).copy();}
 public static void setSelectedTarget(ItemStack stack,stepsword.mahoutsukai.datacomponents.selectedtarget.SelectedTargetMahou value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SELECTED_TARGET_COMPONENT,value.copy());}

   public static Vec3 vecFromPitchYaw(float pitch, float yaw) {
      float f = Mth.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f1 = Mth.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f2 = -Mth.cos(-pitch * (float) (Math.PI / 180.0));
      float f3 = Mth.sin(-pitch * (float) (Math.PI / 180.0));
      return new Vec3(f1 * f2, f3, f * f2);
   }


 public static void setFictionMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.fiction.FictionMahou value){stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT,value.copy());}

}
