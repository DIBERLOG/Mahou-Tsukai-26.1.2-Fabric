package stepsword.mahoutsukai.effects.projection;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.Utils;

public class RealityMarbleSpellEffect {
   public static final int distance = 3000;
   public static Random random = new Random(53L);
   public static int counter = 0;
   public static Identifier marble = Identifier.fromNamespaceAndPath("mahoutsukai", "reality_marble");

   public static boolean inMarble(Level world) {
      return EffectUtil.getDimension(world).toString().equals(marble.toString());
   }

   public static LivingEntity getEnemy(Player player) {
      Entity e = Utils.selectEntityNearCursor(
         player,
         MTConfig.GLOBAL_LOOK_RANGE,
         player.level(),
         a -> Utils.getPredicate(Utils.MAHOU_SELECTION.SCROLL_REALITY_MARBLE, a, player),
         Utils.nearAllowed(Utils.MAHOU_SELECTION.SCROLL_REALITY_MARBLE)
      );
      return e instanceof LivingEntity && !EffectUtil.inEntityBlacklist(e, MTConfig.MARBLE_MOB_BLACKLIST) ? (LivingEntity)e : null;
   }

   public static BlockPos getRealityMarbleSpawn(Level world, UUID uuid) {
      MahouSavedData save = Utils.getWorldMahou(world);
      BlockPos pos = save.getPlayerSpawn(uuid);
      if (pos == null) {
         pos = generateRealityMarbleSpawn(world, save, uuid);
      }

      return pos;
   }

   public static BlockPos generateRealityMarbleSpawn(Level world, MahouSavedData save, UUID uuid) {
      ConcurrentHashMap<UUID, BlockPos> spawns = save.getAllSpawns();
      int maxX = 0;
      int maxZ = 0;
      if (spawns != null) {
         for (UUID tmp : spawns.keySet()) {
            BlockPos pos = spawns.get(tmp);
            if (pos.getX() > maxX) {
               maxX = pos.getX();
            }

            if (pos.getZ() > maxZ) {
               maxZ = pos.getZ();
            }
         }

         if (Math.random() < 0.5) {
            maxX += 3000;
         } else {
            maxZ += 3000;
         }

         BlockPos pos = new BlockPos(maxX, 120, maxZ);
         save.changePlayerSpawn(uuid, pos);
         return pos;
      } else {
         return null;
      }
   }

   public static boolean goToMarble(Player user) {
      if (!(user instanceof ServerPlayer serverUser)) return false;
      IMahou casterMahou = Utils.getPlayerMahou(user);
      if (casterMahou != null) {
         var marbleKey = net.minecraft.resources.ResourceKey.create(Registries.DIMENSION, marble);
         var marbleLevel = serverUser.level().getServer().getLevel(marbleKey);
         if (marbleLevel == null) return false;
         LivingEntity e = getEnemy(user);
         // A selected opponent keeps the original duel. A solo cast joins this server's live
         // Archmage encounter, including the first caster's not-yet-executed dimension transfer.
         SharedEncounter shared = e == null ? activeEncounter(serverUser, marbleLevel) : null;
         boolean existingReturn = casterMahou.getPlayerSavedSpawn() != null
            && RealityMarbleBossEntity.liveFor(marbleLevel, casterMahou.getEnemy()) != null;
         BlockPos hint = shared == null ? casterMahou.getPlayerMarbleSpawn() : shared.spawn();
         if (shared == null && !inMarble(user.level())) {
            BlockPos allocated = Utils.getWorldMahou(user.level()).getPlayerSpawn(user.getUUID());
            if (allocated != null) hint = allocated;
         }
         if (hint == null) hint = getRealityMarbleSpawn(user.level(), user.getUUID());
         if (hint == null) return false;
         Vec3 arrival = safeMarbleSpawn(marbleLevel, hint, user.getBbWidth(), Math.max(3, user.getBbHeight()));
         if (arrival == null) return false;
         Vec3 enemyArrival = e == null ? null : safeMarbleSpawn(marbleLevel, hint.offset(20, 0, 0), e.getBbWidth(), Math.max(3, e.getBbHeight()));
         if (e != null && enemyArrival == null) return false;
         BlockPos pos = BlockPos.containing(arrival);
         BlockPos bossCenter = shared == null ? pos.offset(12, 0, 12)
            : shared.boss().arena() == null ? shared.boss().blockPosition() : shared.boss().arena();
         LivingEntity boss = RealityMarbleBossEntity.spawnFor(marbleLevel, bossCenter);
         if (e == null && boss == null) return false;
         Identifier userdimension = EffectUtil.getDimension(user.level());
         boolean inmarble = inMarble(user.level());
         if (!inmarble && !existingReturn) {
            casterMahou.setPlayerOldDimension(userdimension != null ? userdimension.toString() : Level.OVERWORLD.identifier().toString());
            casterMahou.setPlayerSavedSpawn(user.blockPosition());
         }

         // This is the current encounter position; the player's allocated saved-world spawn
         // remains untouched and is available again after this shared fight.
         casterMahou.setPlayerMarbleSpawn(pos);
         user.setDeltaMovement(Vec3.ZERO);
         user.fallDistance = 0;
         MahouTsukaiTeleporter.teleport(user, marble, arrival);
         if (e != null) {
            if (e instanceof ServerPlayer && !inMarble(e.level())) {
               IMahou enemyMahou = Utils.getPlayerMahou((Player)e);
               if (enemyMahou != null) {
                  enemyMahou.setPlayerOldDimension(EffectUtil.getDimension(e.level()).toString());
                  enemyMahou.setPlayerSavedSpawn(e.blockPosition());
                  PlayerManaManager.updateClientMahou((ServerPlayer)e, enemyMahou);
                  enemyMahou.setEnemy(user.getUUID());
               }
            }

            casterMahou.setEnemy(e.getUUID());
            e.setDeltaMovement(Vec3.ZERO);
            e.fallDistance = 0;
            MahouTsukaiTeleporter.teleport(e, marble, enemyArrival);
         } else {
            // Solo visits are a boss encounter: ordinary damage must not end the arena early.
            casterMahou.setEnemy(boss == null ? null : boss.getUUID());
         }
         PlayerManaManager.updateClientMahou((ServerPlayer)user, casterMahou);
      }

      return casterMahou != null;
   }

   private record SharedEncounter(BlockPos spawn, RealityMarbleBossEntity boss) {}

   @Nullable
   private static SharedEncounter activeEncounter(ServerPlayer user, ServerLevel marbleLevel) {
      var candidates = new ArrayList<ServerPlayer>();
      candidates.add(user); // Recasting in the current arena must not switch to somebody else's.
      candidates.addAll(user.level().getServer().getPlayerList().getPlayers());
      for (var participant : candidates) {
         if (!participant.isAlive() || participant.isRemoved()) continue;
         IMahou mahou = Utils.getPlayerMahou(participant);
         if (mahou == null || mahou.getPlayerMarbleSpawn() == null) continue;
         var boss = RealityMarbleBossEntity.liveFor(marbleLevel, mahou.getEnemy());
         if (boss == null || boss.arena() == null) continue;
         if (mahou.getPlayerMarbleSpawn().distSqr(boss.arena()) > (long)RealityMarbleBossEntity.ARENA_RADIUS * RealityMarbleBossEntity.ARENA_RADIUS) continue;
         if (inMarble(participant.level()) && !boss.inArena(participant)) continue;
         return new SharedEncounter(mahou.getPlayerMarbleSpawn(), boss);
      }
      return null;
   }

   /** Finds supported, dry feet coordinates on actual terrain; stored arena Y is only an old hint. */
   @Nullable
   public static Vec3 safeMarbleSpawn(ServerLevel level, BlockPos hint, double width, double height) {
      if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0 || width > 32 || height > 32) return null;
      for (int radius = 0; radius <= 8; radius++) {
         for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
            if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
            double x = hint.getX() + dx + .5, z = hint.getZ() + dz + .5, half = width / 2;
            if (!level.getWorldBorder().isWithinBounds(new AABB(x-half, level.getMinY()+1, z-half, x+half, level.getMinY()+1+height, z+half))) continue;
            // Only the few targeted chunks are generated; never retain the obsolete fixed Y=120.
            level.getChunk((hint.getX()+dx) >> 4, (hint.getZ()+dz) >> 4);
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, hint.getX()+dx, hint.getZ()+dz);
            if (top <= level.getMinY() || top + height > level.getMaxY()) continue;
            BlockPos floor = new BlockPos(hint.getX()+dx, top-1, hint.getZ()+dz);
            var shape = level.getBlockState(floor).getCollisionShape(level, floor);
            if (shape.isEmpty() || !level.getFluidState(floor).isEmpty()) continue;
            double y = floor.getY() + shape.max(net.minecraft.core.Direction.Axis.Y);
            var body = new AABB(x-half, y+.001, z-half, x+half, y+height, z+half);
            if (!level.noCollision(null, body) || level.containsAnyLiquid(body)) continue;
            if (!supportedAt(level, x, y, z)
               || !supportedAt(level, x-half+.01, y, z-half+.01)
               || !supportedAt(level, x+half-.01, y, z-half+.01)
               || !supportedAt(level, x-half+.01, y, z+half-.01)
               || !supportedAt(level, x+half-.01, y, z+half-.01)) continue;
            return new Vec3(x, y, z);
         }
      }
      return null;
   }

   private static boolean supportedAt(ServerLevel level, double x, double y, double z) {
      BlockPos below = BlockPos.containing(x, y-.01, z);
      if (!level.getFluidState(below).isEmpty()) return false;
      for (var box : level.getBlockState(below).getCollisionShape(level, below).toAabbs()) {
         if (x >= below.getX()+box.minX && x <= below.getX()+box.maxX && z >= below.getZ()+box.minZ && z <= below.getZ()+box.maxZ
            && Math.abs(below.getY()+box.maxY-y) < .01) return true;
      }
      return false;
   }

   public static void spawnSwords(BlockPos userSpawn, Level world, ChunkAccess chunk, int size_count, int distance) {
      if (random == null) {
         random = new Random(42L);
      }

      int startX = userSpawn.getX();
      int endX = startX;
      int ux = endX;
      int startZ = userSpawn.getZ();
      int endZ = startZ;
      int uz = endZ;
      int y = 83;
      startX -= size_count * distance - random.nextInt(distance);
      startZ -= size_count * distance - random.nextInt(distance);
      endX += size_count * distance + random.nextInt(distance);
      endZ += size_count * distance + random.nextInt(distance);
      ArrayList<WeaponProjectileEntity> spawns = new ArrayList<>();

      for (int i = startX; i <= endX; i += distance) {
         for (int j = startZ; j <= endZ; j += distance) {
            boolean inrange = i <= chunk.getPos().getMaxBlockX()
               && i >= chunk.getPos().getMinBlockX()
               && j <= chunk.getPos().getMaxBlockZ()
               && j >= chunk.getPos().getMinBlockZ();
            if (MTConfig.MARBLE_SWORD_SPAWN_LIST.size() > 0 && inrange) {
               double rand = random.nextDouble();
               String name = MTConfig.MARBLE_SWORD_SPAWN_LIST.get(random.nextInt(MTConfig.MARBLE_SWORD_SPAWN_LIST.size()));
               Item item = getItemFromName(name, world);
               if (item == null) {
                  Utils.err("No item with name " + name + " found.");
               } else {
                  ItemStack stack = new ItemStack(item);
                  if (0.6 < rand && rand < 0.8) {
                     addEnchantment(stack, Enchantments.SHARPNESS, 2, world);
                  } else if (rand >= 0.8) {
                     addEnchantment(stack, Enchantments.KNOCKBACK, 2, world);
                  }

                  stack.setDamageValue(stack.getMaxDamage() - MTConfig.MARBLE_DIMENSION_DURABILITY);
                  WeaponProjectileEntity wpe = new WeaponProjectileEntity(world, i, y, j, stack);
                  double velx;
                  if (i < ux) {
                     velx = -0.6;
                  } else {
                     velx = 0.6;
                  }

                  double velz;
                  if (j < uz) {
                     velz = -0.6;
                  } else {
                     velz = 0.6;
                  }

                  wpe.setDeltaMovement(random.nextDouble() * velx, -2.0, random.nextDouble() * velz);
                  if (random.nextDouble() < MTConfig.MARBLE_DIMENSION_SPAWN_CHANCE) {
                     spawns.add(wpe);
                  }
               }
            }
         }
      }

      spawns.forEach(weapon -> {
         if (world instanceof ServerLevel) {

            ((ServerLevel)world).addFreshEntity(weapon);
         }
      });
   }

   private static void addEnchantment(ItemStack stack,net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,int level,Level world){stack.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key),level);}
   public static Item getItemFromName(String s, Level level) {
      Identifier r = Identifier.parse(s);
      return level.registryAccess().lookupOrThrow(Registries.ITEM).get(r).map(reference -> (Item)reference.value()).orElse(null);
   }

   public static void realityMarbleChunkLoad(ServerLevel world,LevelChunk chunk,boolean generated){
      if(inMarble(world)){var pos=chunk.getPos();spawnSwords(new BlockPos(pos.getMiddleBlockX(),100,pos.getMiddleBlockZ()),world,chunk,1,MTConfig.MARBLE_DIMENSION_SPAWN_RATE);}
   }
   public static void realityMarbleLivingHurt(Entity e) {
      if (e instanceof ServerPlayer && inMarble(e.level())) {
         final IMahou mahou = Utils.getPlayerMahou((Player)e);
         if (mahou.getEnemy() != null && e.level().getPlayerByUUID(mahou.getEnemy()) == null) {
            boolean flag = true;
            double range = 500.0;
            List<LivingEntity> lst = e.level()
               .getEntitiesOfClass(
                  LivingEntity.class,
                  new AABB(
                     e.blockPosition().offset((int)(-range), (int)(-range), (int)(-range)).getCenter(),
                     e.blockPosition().offset((int)range, (int)range, (int)range).getCenter()
                  ),
                  new Predicate<LivingEntity>() {
                     public boolean apply(@Nullable LivingEntity e) {
                        return e != null && e.getUUID().equals(mahou.getEnemy());
                     }
                  }
               );
            flag = lst.isEmpty();
            if (flag) {
               mahou.setEnemy(null);
            }
         }

         if (mahou.getEnemy() == null) {
            BlockPos pos = mahou.getPlayerSavedSpawn();
            String dimension = mahou.getPlayerOldDimension() != null && !mahou.getPlayerOldDimension().equals(marble.toString())
               ? mahou.getPlayerOldDimension()
               : Level.OVERWORLD.identifier().toString();
            if (pos == null) {
               pos = ((Player)e).getSleepingPos().orElse(new BlockPos(0, 0, 0));
            }

            MahouTsukaiTeleporter.teleport(e, pos.getX(), pos.getY(), pos.getZ(), Identifier.parse(dimension));
         }
      }
   }
}
