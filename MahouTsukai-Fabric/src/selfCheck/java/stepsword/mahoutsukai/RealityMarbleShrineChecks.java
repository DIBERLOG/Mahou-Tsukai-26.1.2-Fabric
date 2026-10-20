package stepsword.mahoutsukai;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.effects.projection.RealityMarbleShrine;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;

/** Native-world regression for permanent torii placement, safe preflight and saved generation. */
public final class RealityMarbleShrineChecks {
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }

    public static void run(ServerPlayer fixture) {
        var level = fixture.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, RealityMarbleSpellEffect.marble));
        require(level != null, "Shrine test needs Reality Marble");
        BlockPos arena = new BlockPos(18000, 81, 18000);
        require(!RealityMarbleShrine.ensure(fixture.level(), arena), "Shrines never generate in the overworld");
        var previous = level.getDataStorage().computeIfAbsent(RealityMarbleShrine.Sites.TYPE);
        var saved = RealityMarbleShrine.Sites.CODEC.encodeStart(NbtOps.INSTANCE, previous).getOrThrow();
        var restored = RealityMarbleShrine.Sites.CODEC.parse(NbtOps.INSTANCE, saved).getOrThrow();
        // Isolated marker data lets this same bounded fixture run again in an existing test world.
        var empty = RealityMarbleShrine.Sites.CODEC.parse(NbtOps.INSTANCE, new net.minecraft.nbt.ListTag()).getOrThrow();
        level.getDataStorage().set(RealityMarbleShrine.Sites.TYPE, empty);
        Map<BlockPos, BlockState> snapshot = new LinkedHashMap<>();
        List<BlockPos> sites = List.of(arena.offset(0, 0, 24), arena.offset(0, 0, 64), arena.offset(40, 0, 32));
        try {
            for (BlockPos origin : sites) {
                for (BlockPos mutable : BlockPos.betweenClosed(origin.offset(-16, -4, -4), origin.offset(16, 18, 24))) {
                    BlockPos pos = mutable.immutable();
                    snapshot.putIfAbsent(pos, level.getBlockState(pos));
                    level.setBlock(pos, pos.getY() == origin.getY() - 1 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
                level.setBlock(origin.above(2), Blocks.CHEST.defaultBlockState(), Block.UPDATE_CLIENTS);
                ((ChestBlockEntity)level.getBlockEntity(origin.above(2))).setItem(0, new ItemStack(Items.DIAMOND, 3));
            }
            require(!RealityMarbleShrine.ensure(level, arena), "No shrine replaces a chest at any candidate site");
            for (BlockPos origin : sites) {
                var chest = (ChestBlockEntity)level.getBlockEntity(origin.above(2));
                require(chest != null && chest.getItem(0).is(Items.DIAMOND) && chest.getItem(0).getCount() == 3,
                    "Existing chest and inventory survive the preflight");
                chest.clearContent();
                level.setBlock(origin.above(2), Blocks.OAK_PLANKS.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            require(!RealityMarbleShrine.ensure(level, arena), "No shrine builds over existing player walls");
            for (BlockPos origin : sites) level.setBlock(origin.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            BlockPos first = sites.getFirst();
            level.setBlock(first.offset(5, -1, 5), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlock(first.offset(5, -2, 5), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlock(first.offset(5, -3, 5), Blocks.RED_SAND.defaultBlockState(), Block.UPDATE_CLIENTS);
            require(RealityMarbleShrine.ensure(level, arena), "Dry empty ground receives the shrine once");
            BlockPos origin = sites.getFirst();
            require(level.getBlockState(origin.offset(10, 8, 0)).is(Blocks.CRIMSON_STEM), "Real crimson torii pillar exists");
            require(level.getBlockState(origin.offset(0, 13, -2)).is(Blocks.CHISELED_POLISHED_BLACKSTONE), "Torii has a carved plaque");
            require(level.getBlockState(origin.offset(0, 14, 17)).is(Blocks.POLISHED_BLACKSTONE_BRICKS), "Layered hall roof exists");
            require(level.getBlockState(origin.offset(0, 0, 6)).is(Blocks.DEEPSLATE_TILES), "Courtyard has a solid dry floor");
            require(level.getBlockState(origin.offset(0, 2, 0)).isAir(), "The gate remains open for players and the boss");
            require(level.getBlockState(arena).isAir(), "Arena center and arrival path remain untouched");
            require(level.getBlockState(origin.offset(5, -1, 5)).is(Blocks.RED_SAND)
                && level.getBlockState(origin.offset(5, -2, 5)).is(Blocks.RED_SAND), "Uneven terrain has a full red-sand foundation");
            require(RealityMarbleShrine.entrance(level, arena).equals(origin.offset(0, 1, -4)), "Actual shrine entrance is exposed for awakening");
            require(RealityMarbleShrine.bossWaitingPosition(level, arena).equals(origin.offset(0, 2, 13)), "Boss waiting spot is grounded inside the open hall");
            require(level.getBlockState(origin.offset(0, 2, 16)).isAir(), "No loot chest exists before victory");

            // Existing 0.38 saves have only the old list marker, with no stored structure origin.
            var legacy = com.mojang.serialization.Codec.LONG.listOf().encodeStart(NbtOps.INSTANCE,
                List.of(BlockPos.asLong(arena.getX(), 0, arena.getZ()))).getOrThrow();
            var legacyStore = RealityMarbleShrine.Sites.CODEC.parse(NbtOps.INSTANCE, legacy).getOrThrow();
            require(legacyStore.contains(arena), "Original list-only generation markers remain readable");
            for(int dy=-2;dy<=18;dy++)level.setBlock(origin.above(dy),Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS);
            level.getDataStorage().set(RealityMarbleShrine.Sites.TYPE, legacyStore);
            BlockPos playerStorage = origin.offset(6, -1, 5);
            level.setBlock(origin.offset(6, -3, 5), Blocks.RED_SAND.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlock(playerStorage, Blocks.CHEST.defaultBlockState(), Block.UPDATE_CLIENTS);
            ((ChestBlockEntity)level.getBlockEntity(playerStorage)).setItem(0, new ItemStack(Items.EMERALD, 4));
            level.setBlock(origin.offset(5, -1, 5), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            require(!RealityMarbleShrine.ensure(level, arena), "Old generated shrine is repaired without creating another structure");
            require(RealityMarbleShrine.entrance(level,arena)!=null&&level.getBlockState(origin).isAir(),"A destroyed centre column cannot hide surviving legacy temple paving or regenerate the destroyed floor");
            require(level.getBlockState(origin.offset(5, -1, 5)).is(Blocks.RED_SAND), "Legacy shrine's missing foundation is repaired");
            var storage = (ChestBlockEntity)level.getBlockEntity(playerStorage);
            require(storage != null && storage.getItem(0).is(Items.EMERALD) && storage.getItem(0).getCount() == 4,
                "Repair preserves a player's chest and inventory beneath the floor");
            require(level.getBlockState(origin.offset(6, -2, 5)).is(Blocks.RED_SAND),
                "Repair also fills the gap below the preserved player chest");
            var store = level.getDataStorage().computeIfAbsent(RealityMarbleShrine.Sites.TYPE);
            var encoded = RealityMarbleShrine.Sites.CODEC.encodeStart(NbtOps.INSTANCE, store).getOrThrow();
            var reloaded = RealityMarbleShrine.Sites.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();
            require(reloaded.contains(arena) && reloaded.contains(arena.above(40)), "Native saved marker survives codec and ignores obsolete height hints");
            level.getDataStorage().set(RealityMarbleShrine.Sites.TYPE, reloaded);
            BlockPos broken = origin.offset(10, 8, 0);
            level.setBlock(broken, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            require(!RealityMarbleShrine.ensure(level, arena), "Recasting never generates a second shrine");
            require(level.getBlockState(broken).isAir(), "Recasting never regenerates a damaged shrine or overwrites later builds");

            BlockPos preferred = origin.offset(0, 2, 16);
            level.setBlock(preferred, Blocks.CHEST.defaultBlockState(), Block.UPDATE_CLIENTS);
            ((ChestBlockEntity)level.getBlockEntity(preferred)).setItem(0, new ItemStack(Items.DIAMOND, 2));
            var defeated = new stepsword.mahoutsukai.entity.RealityMarbleBossEntity(
                stepsword.mahoutsukai.entity.ModEntities.REALITY_MARBLE_BOSS.get(), level);
            defeated.setArena(arena);
            defeated.setPos(origin.getX() + .5, origin.getY() + 2, origin.getZ() + 13.5);
            defeated.setNoAi(true);
            defeated.setHealth(0);
            defeated.die(level.damageSources().genericKill());
            BlockPos reward = origin.offset(-3, 2, 19);
            var treasure = (ChestBlockEntity)level.getBlockEntity(reward);
            require(treasure != null && treasure.getItem(13).is(Items.NETHERITE_INGOT) && treasure.getItem(13).getCount() == 3,
                "Reward chest contains exactly three real netherite ingots");
            int count = 0;
            for (int slot = 0; slot < treasure.getContainerSize(); slot++) count += treasure.getItem(slot).getCount();
            require(count == 3, "Reward has no extra or copied items");
            require(((ChestBlockEntity)level.getBlockEntity(preferred)).getItem(0).getCount() == 2,
                "Victory never replaces or restocks a player's existing chest");
            var rewardedStore = level.getDataStorage().computeIfAbsent(RealityMarbleShrine.Sites.TYPE);
            var rewardedNbt = RealityMarbleShrine.Sites.CODEC.encodeStart(NbtOps.INSTANCE, rewardedStore).getOrThrow();
            level.getDataStorage().set(RealityMarbleShrine.Sites.TYPE,
                RealityMarbleShrine.Sites.CODEC.parse(NbtOps.INSTANCE, rewardedNbt).getOrThrow());
            treasure.clearContent();
            require(!RealityMarbleShrine.rewardChest(level, arena) && treasure.isEmpty(),
                "Saved victory marker prevents duplicate death events and reentry from restocking taken loot");
            defeated.die(level.damageSources().genericKill());
            require(treasure.isEmpty(), "Repeated actual boss death cannot restock its reward");
            defeated.discard();
            require(!RealityMarbleShrine.rewardChest(fixture.level(), arena), "Victory loot cannot be placed outside Reality Marble");
            System.out.println("PASS: REALITY_MARBLE_SHRINE_038: native torii, layered shrine, dry full foundation, original marker migration, grounded waiting/entrance, preserved chests/walls, persistent idempotence, three-ingot victory chest and no restock");
        } finally {
            for (BlockPos pos : snapshot.keySet())
                if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) chest.clearContent();
            for (var entry : snapshot.entrySet()) level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS);
            level.getDataStorage().set(RealityMarbleShrine.Sites.TYPE, restored);
        }
    }
}
