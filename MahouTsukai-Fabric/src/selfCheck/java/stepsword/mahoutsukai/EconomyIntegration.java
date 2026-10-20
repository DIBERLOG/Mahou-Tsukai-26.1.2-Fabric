package stepsword.mahoutsukai;

import com.mojang.authlib.GameProfile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.fabric.GemData;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.attunedgems.AttunedGem;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;

/** Test-only mod. Not packaged in the production JAR. */
public final class EconomyIntegration implements ModInitializer {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    @Override
    public void onInitialize() {
        if(Boolean.getBoolean("mahoutsukai.battlePreview")){BattleReplay.init();return;}
        if(Boolean.getBoolean("mahoutsukai.audit032")){Audit032ServerChecks.init();return;}
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                if(Boolean.getBoolean("mahoutsukai.progressionOnly")){Progression026Checks.start(server);return;}
                // A failed dense-blast run can leave its stone fixture across other aiming tests.
                // This is the isolated integration world, never a player's world.
                for(var cleanup:BlockPos.betweenClosed(new BlockPos(497,235,497),new BlockPos(527,265,527)))server.overworld().setBlock(cleanup,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),18);
                var progressionFixture=DoomIntegration.player(server.overworld(),"ProgressionFixture",net.minecraft.world.level.GameType.CREATIVE,550);
                Progression026Checks.run(progressionFixture);server.overworld().removePlayerImmediately(progressionFixture,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                ParityMigrationIntegration.run(server);
                ScenarioParityChecks.run(server);
                CombatParityChecks.run(server);
                var world = server.overworld();
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "weather clear");
                world.setRainLevel(0);
                world.setThunderLevel(0);
                var player = new ServerPlayer(server, world,
                    new GameProfile(UUID.fromString("d8c362cb-78c5-407d-a993-112211221122"), "ManaCheck"), ClientInformation.createDefault());
                player.setPos(8, 75, 8);
                var mahou = ((MahouHolder) player).mahoutsukai$getMahou();
                mahou.setMaxMana(1000);
                mahou.setStoredMana(1000);
                mahou.setManaToUseCounter(100);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 0");
                mahou.setStoredMana(100);
                PlayerManaManager.onWake(player, true, false);
                PlayerManaManager.onWake(player, false, true);
                require(mahou.getStoredMana() == 100, "Interrupted wake flags do not restore mana");
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 600, "Native wake hook restores half maximum mana");
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 1000, "Sleep recovery clamps to maximum");
                mahou.setDeathCollectionUsesLeft(20);
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 990, "Full mana still pays original soul penalty");
                mahou.setDeathCollectionUsesLeft(1200);
                mahou.setStoredMana(0);
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 0 && mahou.getDeathCollectionUsesLeft() == 1199,
                    "Negative recovery clamps mana and removes one player soul value");
                mahou.setDeathCollectionUsesLeft(0);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 13000");
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 0, "Night wake is not a completed sleep");
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 0");
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "weather thunder");
                world.setRainLevel(1);
                world.setThunderLevel(1);
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 0, "Thunderstorm wake does not restore mana");
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "weather clear");
                world.setRainLevel(0);
                world.setThunderLevel(0);
                stepsword.mahoutsukai.config.MTConfig.MANA_RECOVERY_SLEEP = 0.25;
                player.stopSleepInBed(false, false);
                require(mahou.getStoredMana() == 250, "Configured sleep recovery factor");
                stepsword.mahoutsukai.config.MTConfig.MANA_RECOVERY_SLEEP = 0.5;
                mahou.setStoredMana(1000);
                ItemStack gem = new ItemStack(ModItems.REGISTERED.get("attuned_diamond"));
                player.getInventory().setItem(0, gem);
                ((AttunedGem) gem.getItem()).charge(gem, player);
                require(GemData.get(gem).getStoredMana() == 100, "Gem received ceil(1000 * 0.1)");
                require(mahou.getStoredMana() == 900 && mahou.getMaxMana() == 1001, "Charging drains personal mana and grows max");
                ItemStack gemCopy = gem.copy();
                var copiedData = GemData.get(gemCopy);
                copiedData.setStoredMana(7);
                require(GemData.get(gem).getStoredMana() == 100 && GemData.get(gemCopy).getStoredMana() == 100,
                    "Native ItemStack copy isolated until explicit writeback");
                GemData.set(gemCopy, copiedData);
                require(GemData.get(gem).getStoredMana() == 100 && GemData.get(gemCopy).getStoredMana() == 7, "Copy writeback isolated");
                BlockPos pos = new BlockPos(8, 70, 8);
                world.setBlockAndUpdate(pos, ModBlocks.circuit.get().defaultBlockState());
                var circuit = (ManaCircuitTileEntity) world.getBlockEntity(pos);
                require(circuit != null, "Registered battery creates original block entity");
                circuit.setOwnerUUID(player.getUUID());
                circuit.setOn(true);
                circuit.setMana(200);
                mahou.setStoredMana(30);
                mahou.setManaToUseCounter(100);
                var gemData = GemData.get(gem);
                gemData.setStoredMana(70);
                GemData.set(gem, gemData);
                int spent = PlayerManaManager.drainManaExact(player, 150, false, false);
                require(spent == 150 && mahou.getStoredMana() == 0, "Spend personal mana first");
                require(GemData.get(gem).getStoredMana() == 0 && circuit.getMana() == 150, "Then gems, then owned batteries");
                require(mahou.getManaToUseCounter() == 70, "Only personal spending grows max mana counter");
                require(PlayerManaManager.drainManaExact(player, 9999, false, false) == 0 && circuit.getMana() == 150,
                    "Insufficient combined mana leaves resources unchanged");
                circuit.setOwnerUUID(UUID.randomUUID());
                require(PlayerManaManager.countMana(player) == 0, "Other owners' batteries cannot be drained");
                circuit.setFae(true);
                require(PlayerManaManager.countMana(player) == 150, "Shared fae battery can be used");
                circuit.setOn(false);
                require(PlayerManaManager.countMana(player) == 0, "Disabled batteries excluded");
                circuit.setOn(true);
                circuit.setOwnerUUID(player.getUUID());
                circuit.setFae(false);
                world.setBlockAndUpdate(pos.above(), net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState());
                require(PlayerManaManager.countMana(player) == 0, "Redstone inverts enabled battery to disabled");
                circuit.setOn(false);
                require(PlayerManaManager.countMana(player) == 150, "Redstone inverts disabled battery to enabled");
                world.setBlockAndUpdate(pos.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                circuit.setOn(true);
                var drops = net.minecraft.world.level.block.Block.getDrops(circuit.getBlockState(), world, pos, circuit, player, ItemStack.EMPTY);
                require(drops.size() == 1, "Original battery loot table produces one item");
                require(drops.getFirst().get(stepsword.mahoutsukai.datacomponents.DataComponents.MANA_CIRCUIT_MANA_COMPONENT) == 150,
                    "Breaking battery keeps stored mana in original item component");
                require(player.getUUID().equals(drops.getFirst().get(stepsword.mahoutsukai.datacomponents.DataComponents.MANA_CIRCUIT_OWNER_COMPONENT)),
                    "Breaking battery keeps original owner component");
                var placedAgain = new ManaCircuitTileEntity(pos, ModBlocks.circuit.get().defaultBlockState());
                placedAgain.applyComponentsFromItemStack(drops.getFirst());
                require(placedAgain.getMana() == 150 && player.getUUID().equals(placedAgain.getOwnerUUID()),
                    "Placing dropped battery restores owner and mana");
                var saved = circuit.saveWithoutMetadata(world.registryAccess());
                require(saved.getIntOr("MAHOUTSUKAI_MANA_TAG", -1) == 150, "Original battery NBT field");
                circuit.setChanged();
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "forceload add 0 0 16 16");
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "forceload add 448 448 592 592");
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "forceload add 600 512 720 576");
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(new net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick() {
                    int ticks;
                    boolean flameReady,woundReady;
                    public void onEndTick(net.minecraft.server.MinecraftServer running) {
                        ++ticks;
                        if(ticks<40) return;
                        try {
                            // Observe periodic fixtures on every world tick while other queued
                            // scenarios progress; extra stages must not skip their first damage pulse.
                            if(ticks>41){
                                if(!flameReady)flameReady=BlackFlameIntegration.verifyNativeTicks();
                                if(!woundReady)woundReady=StrengtheningClarentIntegration.verifyWoundTick();
                            }
                            if(ticks==40) {
                            MentalDisplacementChecks.run(player);
                            Nobu031Checks.run(player);
                            Fireball031Checks.run(player);
                            ArchmageCombat034Checks.run(player);
                            ArchmageStuck040Checks.run(player);
                            MagicIntegration.run(player);
                            RitualIntegration.run(player);
                            DefenseIntegration.run(player);
                            ExchangeCircleIntegration.run(player);
                            BoundaryIntegration.run(player);
                            return;
                            }
                            if(ticks==41) {
                            BoundaryIntegration.verifyDeferred();
                            DisplacementEyesIntegration.run(player);
                            SoulsKodokuIntegration.run(player);
                            ReplicationSelectiveIntegration.run(player);
                            StrengtheningClarentIntegration.run(player);
                            ProjectionIntegration.run(player);
                            ProximityProjectionIntegration.run(player);
                            FamiliarIntegration.run(player);
                            PowerLakeIntegration.run(player);
                            SixFamiliesIntegration.run(player);
                            RemainingFamiliesIntegration.run(player);
                            LateFamiliesIntegration.run(player);
                            RemainingItemsIntegration.run(player);
                            EntitiesProjectorsIntegration.run(player);
                            CompatibilityIntegration.run(player);
                            OptionalIntegrationChecks.run(player);
                            BlackFlameIntegration.run(player);
                            SwordMarbleIntegration.start(player);
                            return;
                            }
                            if(ticks==42){SwordMarbleIntegration.verify();return;}
                            if(ticks==43){SwordMarbleIntegration.requestReturn();return;}
                            if(ticks==44){SwordMarbleIntegration.finish();running.execute(()->MultiplayerMarble036Checks.start(player));return;}
                            if(ticks==45){MultiplayerMarble036Checks.verifyArrived();return;}
                            if(ticks==46){MultiplayerMarble036Checks.verifyFirstReturned();return;}
                            if(ticks==47){MultiplayerMarble036Checks.finish();return;}
                            if(ticks==48){BossVictory037Checks.startReturn(player);return;}
                            if(ticks==49){BossVictory037Checks.expireLootReturn();return;}
                            if(ticks==50)BossVictory037Checks.finishReturn();
                            if(!flameReady||!woundReady) return;
                            DoomIntegration.run(player);
                            running.getCommands().performPrefixedCommand(running.createCommandSourceStack(), "forceload remove 448 448 592 592");
                            running.getCommands().performPrefixedCommand(running.createCommandSourceStack(), "forceload remove 600 512 720 576");
                            running.getCommands().performPrefixedCommand(running.createCommandSourceStack(), "forceload remove 0 0 16 16");
                            Files.writeString(Path.of("economy-checks.passed"), "PASS: economy, regeneration, 53 original ritual families, defense, contracts, barriers, eyes, souls, Kodoku, weapons and DOOM server integration\n");
                            System.out.println("PASS: ECONOMY_INTEGRATION");
                            running.halt(false);
                        } catch (Exception | AssertionError failure) {
                            MultiplayerMarble036Checks.cleanup();
                            throw new IllegalStateException("Deferred gameplay integration failed", failure);
                        }
                    }
                });

            } catch (Exception | AssertionError failure) {
                throw new IllegalStateException("Economy integration failed", failure);
            }
        });
    }
}

