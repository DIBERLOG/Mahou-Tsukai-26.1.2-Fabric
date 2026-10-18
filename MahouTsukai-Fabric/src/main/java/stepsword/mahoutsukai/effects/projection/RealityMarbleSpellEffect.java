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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
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
      IMahou casterMahou = Utils.getPlayerMahou(user);
      if (casterMahou != null) {
         Identifier userdimension = EffectUtil.getDimension(user.level());
         boolean inmarble = inMarble(user.level());
         if (userdimension != null && !inmarble) {
            casterMahou.setPlayerOldDimension(userdimension.toString());
         } else {
            casterMahou.setPlayerOldDimension(Level.OVERWORLD.identifier().toString());
         }

         if (userdimension != null && !inmarble) {
            casterMahou.setPlayerSavedSpawn(user.blockPosition());
         }

         if (casterMahou.getPlayerMarbleSpawn() == null) {
            casterMahou.setPlayerMarbleSpawn(getRealityMarbleSpawn(user.level(), user.getUUID()));
         }

         BlockPos pos = casterMahou.getPlayerMarbleSpawn();
         if (pos == null) {
            return false;
         }

         LivingEntity e = getEnemy(user);
         PlayerManaManager.updateClientMahou((ServerPlayer)user, casterMahou);
         MahouTsukaiTeleporter.teleport(user, marble, new Vec3(pos.getX(), 120.0, pos.getZ()));
         if (e != null) {
            if (e instanceof ServerPlayer && !inmarble) {
               IMahou enemyMahou = Utils.getPlayerMahou((Player)e);
               if (enemyMahou != null) {
                  enemyMahou.setPlayerOldDimension(EffectUtil.getDimension(e.level()).toString());
                  enemyMahou.setPlayerSavedSpawn(e.blockPosition());
                  PlayerManaManager.updateClientMahou((ServerPlayer)e, enemyMahou);
                  enemyMahou.setEnemy(user.getUUID());
               }
            }

            casterMahou.setEnemy(e.getUUID());
            MahouTsukaiTeleporter.teleport(e, marble, new Vec3(pos.getX() + 20, 120.0, pos.getZ()));
         }
      }

      return true;
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