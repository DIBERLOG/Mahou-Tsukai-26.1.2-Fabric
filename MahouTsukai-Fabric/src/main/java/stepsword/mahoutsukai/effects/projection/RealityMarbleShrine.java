package stepsword.mahoutsukai.effects.projection;

import com.mojang.serialization.Codec;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/** A permanent, north-facing torii and open shrine behind a Reality Marble encounter. */
public final class RealityMarbleShrine {
    private RealityMarbleShrine() {}

    // These are the dimension's natural terrain, not blocks used by the shrine or player storage.
    private static final Set<Block> GROUND = Set.of(Blocks.RED_SAND, Blocks.SAND, Blocks.GRAVEL,
        Blocks.RED_SANDSTONE, Blocks.SANDSTONE, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK,
        Blocks.TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BEDROCK);
    private static final List<BlockPos> OFFSETS = List.of(new BlockPos(0, 0, 24), new BlockPos(0, 0, 64), new BlockPos(40, 0, 32));

    /**
     * Generates once per arena, only in Reality Marble. Existing buildings, storage, liquids and
     * occupied sites are refused before the first write; later casts never repair a damaged shrine.
     * The arena and its arrival point remain untouched, twenty blocks in front of the platform.
     */
    public static boolean ensure(ServerLevel level, BlockPos bossArena) {
        if (!RealityMarbleSpellEffect.inMarble(level)) return false;
        Sites sites = level.getDataStorage().computeIfAbsent(Sites.TYPE);
        if (sites.contains(bossArena)) {
            BlockPos origin = locate(level, bossArena, sites);
            if (origin != null) repairFoundation(level, origin);
            return false;
        }
        for (BlockPos offset : OFFSETS) {
            BlockPos origin = groundOrigin(level, bossArena.offset(offset));
            if (origin == null) continue;
            Map<BlockPos, BlockState> plan = blueprint(origin);
            if (!clearSite(level, origin, plan)) continue;
            // The complete preflight above makes this a single server-thread mutation without
            // clearing any volume. Block entities and existing solid blocks are never replaced.
            for (var entry : plan.entrySet()) level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS);
            long key = BlockPos.asLong(bossArena.getX(), 0, bossArena.getZ());
            sites.generated.add(key);
            sites.origins.put(key, origin);
            sites.setDirty();
            repairFoundation(level, origin);
            return true;
        }
        return false;
    }

    /** Grounded approach point just in front of the torii, also for the boss's awakening trigger. */
    @Nullable
    public static BlockPos entrance(ServerLevel level, BlockPos bossArena) {
        if (!RealityMarbleSpellEffect.inMarble(level)) return null;
        BlockPos origin = locate(level, bossArena, level.getDataStorage().computeIfAbsent(Sites.TYPE));
        return origin == null ? null : origin.offset(0, 1, -4);
    }

    /** A three-block-high clear standing point in the hall; never relocates an existing entity. */
    @Nullable
    public static BlockPos bossWaitingPosition(ServerLevel level, BlockPos bossArena) {
        if (!RealityMarbleSpellEffect.inMarble(level)) return null;
        BlockPos origin = locate(level, bossArena, level.getDataStorage().computeIfAbsent(Sites.TYPE));
        if (origin == null) return null;
        for (int z : new int[]{13, 14, 15}) for (int x : new int[]{0, -2, 2}) {
            BlockPos feet = origin.offset(x, 2, z);
            AABB body = new AABB(feet.getX() + .02, feet.getY() + .001, feet.getZ() + .02,
                feet.getX() + .98, feet.getY() + 3.2, feet.getZ() + .98);
            if (!level.getBlockState(feet.below()).getCollisionShape(level, feet.below()).isEmpty()
                && level.noCollision(null, body) && !level.containsAnyLiquid(body)) return feet;
        }
        return null;
    }

    /** Called only by the actual Archmage victory path: a single shared, never-restocked reward. */
    public static boolean rewardChest(ServerLevel level, BlockPos bossArena) {
        if (!RealityMarbleSpellEffect.inMarble(level)) return false;
        Sites sites = level.getDataStorage().computeIfAbsent(Sites.TYPE);
        Long key = sites.keyFor(bossArena);
        if (key == null || sites.rewarded.contains(key)) return false;
        BlockPos origin = locate(level, bossArena, sites);
        if (origin == null) return false;
        for (BlockPos offset : List.of(new BlockPos(0, 2, 16), new BlockPos(-3, 2, 19), new BlockPos(3, 2, 19))) {
            BlockPos pos = origin.offset(offset);
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()
                || level.getBlockEntity(pos) != null || !level.getFluidState(pos).isEmpty()
                || level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty()) continue;
            boolean adjacentChest = false;
            for (Direction side : Direction.Plane.HORIZONTAL)
                adjacentChest |= level.getBlockEntity(pos.relative(side)) instanceof ChestBlockEntity;
            if (adjacentChest) continue;
            if (!level.setBlock(pos, Blocks.CHEST.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), Block.UPDATE_ALL)) continue;
            if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) return false;
            chest.setItem(13, new ItemStack(Items.NETHERITE_INGOT, 3));
            chest.setChanged();
            sites.rewarded.add(key);
            sites.setDirty();
            return true;
        }
        return false;
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, BlockPos arena, Sites sites) {
        Long key = sites.keyFor(arena);
        if (key == null) return null;
        BlockPos origin = sites.origins.get(key);
        if (origin != null) return origin;
        // 0.38 originally stored only arena X/Z. Recognise surviving original paving at a
        // known candidate site; do not recreate any missing floor, roof, decoration or player build.
        BlockPos oldArena = BlockPos.of(key);
        for (BlockPos offset : OFFSETS) {
            BlockPos candidate = oldArena.offset(offset);
            level.getChunk(candidate.getX() >> 4, candidate.getZ() >> 4);
            // A crater may put the centre column below surviving paving at the edges.
            for (int y = level.getMaxY() - 18; y > level.getMinY(); y--) {
                BlockPos probe = new BlockPos(candidate.getX(), y, candidate.getZ());
                int matches = 0;
                for (BlockPos sample : List.of(new BlockPos(-16, 0, -4), new BlockPos(16, 0, -4),
                    new BlockPos(-16, 0, 24), new BlockPos(16, 0, 24), new BlockPos(0, 0, 6),
                    new BlockPos(3, 0, 7), new BlockPos(-3, 0, 8))) {
                    if (level.getBlockState(probe.offset(sample)).equals(paving(sample.getX(), sample.getZ()))) matches++;
                }
                if (matches < 5) continue;
                sites.origins.put(key, probe);
                sites.setDirty();
                return probe;
            }
        }
        return null;
    }

    private static void repairFoundation(ServerLevel level, BlockPos origin) {
        List<LivingEntity> occupants = level.getEntitiesOfClass(LivingEntity.class,
            new AABB(origin.getX() - 16, level.getMinY(), origin.getZ() - 4,
                origin.getX() + 17, origin.getY(), origin.getZ() + 25), LivingEntity::isAlive);
        List<BlockPos> empty = new ArrayList<>();
        for (int x = -16; x <= 16; x++) for (int z = -4; z <= 24; z++) {
            BlockPos floor = origin.offset(x, 0, z);
            if (!level.getBlockState(floor).equals(paving(x, z))) continue;
            empty.clear();
            for (BlockPos below = floor.below(); below.getY() > level.getMinY(); below = below.below()) {
                BlockState state = level.getBlockState(below);
                if (GROUND.contains(state.getBlock()) && !state.getCollisionShape(level, below).isEmpty()) break;
                if ((!state.isAir() && !state.canBeReplaced()) || !level.getFluidState(below).isEmpty()
                    || level.getBlockEntity(below) != null) continue;
                boolean occupied = false;
                AABB cell = new AABB(below);
                for (LivingEntity entity : occupants) occupied |= entity.getBoundingBox().intersects(cell);
                if (!occupied) empty.add(below);
            }
            // Sand is supported immediately: fill from the ground upwards, preserving all solids.
            for (int index = empty.size() - 1; index >= 0; index--)
                level.setBlock(empty.get(index), Blocks.RED_SAND.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static BlockState paving(int x, int z) {
        if (z == -4 && Math.abs(x) <= 8) return Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        if (Math.abs(x) == 4 && z >= -3 && z <= 23)
            return (z % 4 == 0 ? Blocks.GILDED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS).defaultBlockState();
        boolean border = Math.abs(x) == 16 || z == -4 || z == 24;
        return (border ? Blocks.POLISHED_BLACKSTONE_BRICKS
            : Math.floorMod(x * 31 + z * 17, 11) == 0 ? Blocks.CRACKED_DEEPSLATE_TILES : Blocks.DEEPSLATE_TILES).defaultBlockState();
    }

    private static BlockPos groundOrigin(ServerLevel level, BlockPos hint) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int x = -16; x <= 16; x++) for (int z = -4; z <= 24; z++) {
            int wx = hint.getX() + x, wz = hint.getZ() + z;
            level.getChunk(wx >> 4, wz >> 4);
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, wx, wz);
            BlockPos floor = new BlockPos(wx, top - 1, wz);
            if (top <= level.getMinY() || !level.getFluidState(floor).isEmpty()
                || !GROUND.contains(level.getBlockState(floor).getBlock())) return null;
            min = Math.min(min, top);
            max = Math.max(max, top);
        }
        if (max - min > 4 || max + 18 >= level.getMaxY()) return null;
        return new BlockPos(hint.getX(), max, hint.getZ());
    }

    private static boolean clearSite(ServerLevel level, BlockPos origin, Map<BlockPos, BlockState> plan) {
        AABB bounds = new AABB(origin.getX() - 16, origin.getY() - 4, origin.getZ() - 4,
            origin.getX() + 17, origin.getY() + 19, origin.getZ() + 25);
        if (!level.getWorldBorder().isWithinBounds(bounds)
            || !level.getEntitiesOfClass(LivingEntity.class, bounds, LivingEntity::isAlive).isEmpty()) return false;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-16, -4, -4), origin.offset(16, 18, 24))) {
            if (level.getBlockEntity(pos) != null) return false;
        }
        for (var entry : plan.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState existing = level.getBlockState(pos);
            if (!level.getFluidState(pos).isEmpty() || (!existing.isAir() && !existing.canBeReplaced())) return false;
        }
        return true;
    }

    private static Map<BlockPos, BlockState> blueprint(BlockPos origin) {
        Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
        // Low, complete courtyard; no gaps, dangerous drops or hidden void under the fighting path.
        for (int x = -16; x <= 16; x++) for (int z = -4; z <= 24; z++) {
            put(blocks, origin, x, 0, z, paving(x, z));
        }
        // Broad entrance steps and gold inlays lead the eye through the gate to the open sanctuary.
        for (int x = -8; x <= 8; x++) put(blocks, origin, x, 0, -4,
            Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        for (int z = -3; z <= 23; z++) for (int x : new int[]{-4, 4}) put(blocks, origin, x, 0, z,
            z % 4 == 0 ? Blocks.GILDED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);

        // Monumental 33-block torii: stone feet, paired crimson posts, two beams and upswept caps.
        for (int side : new int[]{-1, 1}) {
            box(blocks, origin, side * 10 - 2, 1, -2, side * 10 + 2, 2, 2, Blocks.POLISHED_BLACKSTONE_BRICKS);
            box(blocks, origin, side * 10 - 1, 3, -1, side * 10 + 1, 14, 1, Blocks.CRIMSON_STEM);
            box(blocks, origin, side * 10 - 1, 4, -1, side * 10 + 1, 4, 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
            box(blocks, origin, side * 10 - 1, 11, -1, side * 10 + 1, 11, 1, Blocks.RED_NETHER_BRICKS);
            put(blocks, origin, side * 10, 10, -2, Blocks.GILDED_BLACKSTONE);
            box(blocks, origin, side * 14 - 1, 16, -1, side * 14 + 1, 16, 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
            put(blocks, origin, side * 16, 17, 0, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        }
        box(blocks, origin, -14, 12, -1, 14, 12, 1, Blocks.CRIMSON_PLANKS);
        box(blocks, origin, -15, 15, -1, 15, 15, 1, Blocks.RED_NETHER_BRICKS);
        box(blocks, origin, -15, 16, 0, 15, 16, 0, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        box(blocks, origin, -1, 13, -1, 1, 14, 1, Blocks.CRIMSON_PLANKS);
        put(blocks, origin, 0, 13, -2, Blocks.CHISELED_POLISHED_BLACKSTONE);
        put(blocks, origin, 0, 14, -2, Blocks.GILDED_BLACKSTONE);
        for (int x : new int[]{-6, 6}) {
            put(blocks, origin, x, 11, 0, Blocks.IRON_CHAIN);
            put(blocks, origin, x, 10, 0, Blocks.LANTERN.defaultBlockState().setValue(BlockStateProperties.HANGING, true));
        }

        // An open hall, not a sealed dungeon: visible altar, red pillars and black terraced roof.
        box(blocks, origin, -9, 1, 10, 9, 1, 23, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x = -5; x <= 5; x++) put(blocks, origin, x, 1, 9,
            Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        for (int x : new int[]{-7, 7}) for (int z : new int[]{12, 21}) {
            box(blocks, origin, x, 2, z, x, 8, z, Blocks.CRIMSON_STEM);
            put(blocks, origin, x, 2, z, Blocks.GILDED_BLACKSTONE);
            put(blocks, origin, x, 7, z, Blocks.GILDED_BLACKSTONE);
        }
        box(blocks, origin, -7, 8, 12, 7, 8, 12, Blocks.RED_NETHER_BRICKS);
        box(blocks, origin, -7, 8, 21, 7, 8, 21, Blocks.RED_NETHER_BRICKS);
        box(blocks, origin, -7, 8, 12, -7, 8, 21, Blocks.RED_NETHER_BRICKS);
        box(blocks, origin, 7, 8, 12, 7, 8, 21, Blocks.RED_NETHER_BRICKS);
        box(blocks, origin, -7, 2, 22, 7, 7, 22, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x = -6; x <= 6; x += 3) put(blocks, origin, x, 5, 21, Blocks.GILDED_BLACKSTONE);
        for (int tier = 0; tier < 5; tier++) {
            int half = 10 - tier, front = 10 + tier, back = 24 - tier;
            box(blocks, origin, -half, 9 + tier, front, half, 9 + tier, back, Blocks.DEEPSLATE_TILES);
            for (int x = -half; x <= half; x++) {
                put(blocks, origin, x, 9 + tier, front, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
                put(blocks, origin, x, 9 + tier, back, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
            }
        }
        box(blocks, origin, -5, 14, 16, 5, 14, 18, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x : new int[]{-6, 6}) put(blocks, origin, x, 15, 17, Blocks.GILDED_BLACKSTONE);
        box(blocks, origin, -2, 2, 18, 2, 2, 20, Blocks.CHISELED_POLISHED_BLACKSTONE);
        put(blocks, origin, 0, 3, 19, Blocks.CHISELED_DEEPSLATE);
        put(blocks, origin, 0, 4, 19, Blocks.GILDED_BLACKSTONE);

        // Four lantern monuments frame the courtyard without obstructing the central aisle.
        for (int x : new int[]{-13, 13}) for (int z : new int[]{5, 20}) {
            box(blocks, origin, x - 1, 1, z - 1, x + 1, 1, z + 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
            put(blocks, origin, x, 2, z, Blocks.CHISELED_POLISHED_BLACKSTONE);
            put(blocks, origin, x, 3, z, Blocks.LANTERN);
            put(blocks, origin, x, 4, z, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        }
        return blocks;
    }

    private static void box(Map<BlockPos, BlockState> plan, BlockPos origin,
                            int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
        for (int x = x1; x <= x2; x++) for (int y = y1; y <= y2; y++) for (int z = z1; z <= z2; z++)
            put(plan, origin, x, y, z, block);
    }

    private static void put(Map<BlockPos, BlockState> plan, BlockPos origin, int x, int y, int z, Block block) {
        put(plan, origin, x, y, z, block.defaultBlockState());
    }

    private static void put(Map<BlockPos, BlockState> plan, BlockPos origin, int x, int y, int z, BlockState state) {
        plan.put(origin.offset(x, y, z), state);
    }

    public static final class Sites extends SavedData {
        public static final Codec<Sites> CODEC = Codec.either(Codec.LONG.listOf(), CompoundTag.CODEC)
            .xmap(value -> value.map(Sites::new, Sites::load), sites -> Either.right(sites.save()));
        public static final SavedDataType<Sites> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("mahoutsukai", "reality_marble_shrines"), Sites::new, CODEC, null);
        private final Set<Long> generated;
        private final Set<Long> rewarded = new HashSet<>();
        private final Map<Long, BlockPos> origins = new LinkedHashMap<>();

        private Sites() { this(List.of()); }
        private Sites(List<Long> generated) { this.generated = new HashSet<>(generated); }

        private static Sites load(CompoundTag tag) {
            Sites sites = new Sites(tag.read("generated", Codec.LONG.listOf()).orElse(List.of()));
            sites.rewarded.addAll(tag.read("rewarded", Codec.LONG.listOf()).orElse(List.of()));
            tag.getCompound("origins").ifPresent(origins -> {
                for (String key : origins.keySet()) {
                    try { sites.origins.put(Long.parseLong(key), BlockPos.of(origins.getLongOr(key, 0))); }
                    catch (NumberFormatException ignored) { /* Ignore malformed old location keys. */ }
                }
            });
            return sites;
        }

        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.store("generated", Codec.LONG.listOf(), new ArrayList<>(generated));
            tag.store("rewarded", Codec.LONG.listOf(), new ArrayList<>(rewarded));
            CompoundTag positions = new CompoundTag();
            origins.forEach((key, pos) -> positions.putLong(Long.toString(key), pos.asLong()));
            tag.put("origins", positions);
            return tag;
        }

        public boolean contains(BlockPos arena) {
            return keyFor(arena) != null;
        }

        @Nullable
        private Long keyFor(BlockPos arena) {
            for (long key : generated) {
                BlockPos pos = BlockPos.of(key);
                long dx = (long)pos.getX() - arena.getX(), dz = (long)pos.getZ() - arena.getZ();
                if (dx * dx + dz * dz <= 64L * 64L) return key;
            }
            return null;
        }
    }
}
