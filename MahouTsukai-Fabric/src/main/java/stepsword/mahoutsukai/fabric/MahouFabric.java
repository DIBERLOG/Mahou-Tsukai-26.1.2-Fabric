package stepsword.mahoutsukai.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import java.io.IOException;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.LoggerFactory;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.commands.GetMana;
import stepsword.mahoutsukai.commands.GetMaxMana;
import stepsword.mahoutsukai.commands.SetMaxMana;
import stepsword.mahoutsukai.commands.AddMaxMana;

public final class MahouFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SettingsUpdatePacket.TYPE,stepsword.mahoutsukai.networking.SettingsUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SettingsUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.SettingsUpdatePacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ProjectorRightClickPacket.TYPE,stepsword.mahoutsukai.networking.ProjectorRightClickPacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ProjectorRightClickPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.ProjectorRightClickPacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,level,hand,hit)->stepsword.mahoutsukai.fabric.ProjectorInteractions.use(player,level,hand,hit));
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)->target instanceof stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity lance && !player.isSpectator()?lance.collect(player,hand):net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.TYPE,stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.TYPE,stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)-> {
            if(player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && stepsword.mahoutsukai.util.EffectUtil.hasBuff(player,stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES)){
             if(!world.isClientSide()){stepsword.mahoutsukai.util.EffectUtil.debuff(player,stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES);player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("mahoutsukai.black_flame.stopped"));}
             return net.minecraft.world.InteractionResult.SUCCESS;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->SpellCasting.cancel(handler.player));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.register(server->SpellCasting.clear());
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)->{if(!world.isClientSide() && !player.isSpectator())stepsword.mahoutsukai.networking.TurnedPagePacket.openedBook(player,player.getItemInHand(hand));return net.minecraft.world.InteractionResult.PASS;});

        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)->{if(!world.isClientSide() && !player.isSpectator())stepsword.mahoutsukai.item.william.William.createWilliam(player,player.getItemInHand(hand),hit.getBlockPos());return net.minecraft.world.InteractionResult.PASS;});
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)->{if(target instanceof stepsword.mahoutsukai.entity.mahoujin.WilliamEntity){if(!player.isSpectator())stepsword.mahoutsukai.entity.mahoujin.WilliamEntity.williamRightClick(player,target,hand);return net.minecraft.world.InteractionResult.SUCCESS;}return net.minecraft.world.InteractionResult.PASS;});

        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->RemoteViewActions.leave(player)?net.minecraft.world.InteractionResult.FAIL:net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)->RemoteViewActions.leave(player)?net.minecraft.world.InteractionResult.FAIL:net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)->RemoteViewActions.leave(player)?net.minecraft.world.InteractionResult.FAIL:net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)->RemoteViewActions.leave(player)?net.minecraft.world.InteractionResult.FAIL:net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player,world,hand,pos,side)->RemoteViewActions.leave(player)?net.minecraft.world.InteractionResult.FAIL:net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD.register(stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect::realityMarbleChunkLoad);
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world,player,pos,state,be)->!stepsword.mahoutsukai.potion.ForesightPotion.deferBreak(world,player,pos,state));
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER.register((world,player,pos,state,be)->stepsword.mahoutsukai.potion.MisfortunePotion.misfortuneBreak(player));
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{if(!stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && (!(target instanceof net.minecraft.world.entity.LivingEntity living) || !stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living)))stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysHit(player,target);return net.minecraft.world.InteractionResult.PASS;});
        stepsword.mahoutsukai.entity.ModEntityDataSerializers.init();
        stepsword.mahoutsukai.tickets.ModTickets.init();
        net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.START_TRACKING.register((entity,player)-> {
            if(entity instanceof net.minecraft.world.entity.LivingEntity authority && authority.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BORROWED_AUTHORITY))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.AuthorityPacket(authority.getId(),true));
            if(entity instanceof net.minecraft.world.entity.LivingEntity invisible && invisible.hasEffect(stepsword.mahoutsukai.potion.ModEffects.RIPPER_INVISIBILITY))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.RipperInvisiblePacket(invisible.getId(),true));
            if(entity instanceof net.minecraft.world.entity.LivingEntity reverted && reverted.hasEffect(stepsword.mahoutsukai.potion.ModEffects.REVERTED))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.RevertedPacket(reverted.getId(),true));
            if(entity instanceof net.minecraft.world.entity.LivingEntity living && living.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING))
                net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player,new stepsword.mahoutsukai.networking.BlackFirePacket(living.getId(),true));
        });
        stepsword.mahoutsukai.render.particle.ModParticles.init();
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)-> {
            if(!stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player)) stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarRightClick(player,target,hand);
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenAttackEntity(target,player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity,world)-> {
            if(entity instanceof stepsword.mahoutsukai.entity.FamiliarEntity familiar && familiar.getOwnerReference()!=null) {
                var owner=familiar.getOwnerReference().getUUID();
                var old=stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.get(owner);
                if(old==null || !old.isAlive()) stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.put(owner,familiar);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.clear());
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{stepsword.mahoutsukai.item.william.William.awaitingLecterns.clear();stepsword.mahoutsukai.networking.TurnedPagePacket.lastOpened.clear();});
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->{var player=handler.player;stepsword.mahoutsukai.item.nobu.Nobu.clearNobuStorage(player);stepsword.mahoutsukai.item.nobu.Nobu.player_storage.remove(player.getUUID());stepsword.mahoutsukai.item.emrys.StaffEmrys.staffs.remove(player.getUUID());stepsword.mahoutsukai.networking.TurnedPagePacket.lastOpened.remove(player.getUUID());});
        net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.START_TRACKING.register((entity,player)->{if(entity instanceof net.minecraft.world.entity.LivingEntity living && living.hasEffect(stepsword.mahoutsukai.potion.ModEffects.ACTING))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.ActingPacket(living.getId(),true,stepsword.mahoutsukai.util.Utils.getLivingMahou(living).getActing()));});
        stepsword.mahoutsukai.datacomponents.DataComponents.init();
        stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.init();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD.register((world,chunk,generated)->stepsword.mahoutsukai.fluids.MurkyWaterBlock.repairLakeOnLoad(world,chunk));
        stepsword.mahoutsukai.fluids.ModFluids.init();
        stepsword.mahoutsukai.containers.ModContainers.init();
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER.register((world,player,pos,state,be)->stepsword.mahoutsukai.item.replica.Replica.replicaBreakBlock(player));
        ModItems.init();
        DungeonScrollLoot.init();
        GuideCatalog.init();
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->
            stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) || target instanceof net.minecraft.world.entity.LivingEntity living && stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living)
                ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)-> {
            if(stepsword.mahoutsukai.potion.BoundPotion.boundRightClickBlock(player)) return net.minecraft.world.InteractionResult.FAIL;
            stepsword.mahoutsukai.effects.displacement.EquivalentDisplacementSpellEffect.equivalentDisplacementShiftRightClick(player,world,hit.getBlockPos(),hand);
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player,world,hand,pos,side)->
            stepsword.mahoutsukai.potion.BoundPotion.boundLeftClickBlock(player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)->
            stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((entity,world)-> {
            if(entity instanceof stepsword.mahoutsukai.tile.displacement.EquivalentDisplacementMahoujinTileEntity circle) circle.releaseChunks();
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity,world)->
            stepsword.mahoutsukai.effects.displacement.ProjectileDisplacementSpellEffect.projectileDisplacementJoinWorld(entity));
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)-> {
            stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentOnRightClick(player);return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world,player,pos,state,entity)-> {
            stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.strengtheningBreakEvent(player);return true;
        });
        stepsword.mahoutsukai.advancements.ModTriggers.init();
        stepsword.mahoutsukai.potion.ModEffects.init();
        stepsword.mahoutsukai.sounds.ModSounds.init();
        ArchmageDefeat.init();
        stepsword.mahoutsukai.entity.ModEntities.init();
        stepsword.mahoutsukai.MahouRecipes.init();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(world -> {
            stepsword.mahoutsukai.effects.projection.PowerConsolidationSpellEffect.powerConsolidationWorldTick(world);
            stepsword.mahoutsukai.fluids.MurkyWaterBlock.murkyUpdateTick(world);
            long tick = world.getGameTime() % 50000L;
            for (var player : world.players()) {
                stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityPlayerTick(player);
                stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect.mentalDisplacementLivingUpdate(player);
                if(stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).getMentalDisplacementPos()!=null && world.getEntitiesOfClass(stepsword.mahoutsukai.entity.MentalDisplacementEntity.class,player.getBoundingBox().inflate(stepsword.mahoutsukai.config.MTConfig.MENTAL_DISPLACEMENT_RANGE*3),entity->player.getUUID().equals(entity.getOwnerId())).isEmpty())stepsword.mahoutsukai.networking.DoneMentalPacket.onMessage(null,player);
                stepsword.mahoutsukai.effects.projection.MorganEffect.secretCalibur(player);
                stepsword.mahoutsukai.item.nobu.Nobu.nobuPlayerTick(player);
                stepsword.mahoutsukai.item.william.William.createWilliamCheck(player);
                if(tick%100==0)stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(player);
                if(tick%10==0){stepsword.mahoutsukai.potion.ReversionEyesPotion.reversionEyesPlayerTick(player);stepsword.mahoutsukai.potion.InsightEyesPotion.insightEyesPlayerTick(player);stepsword.mahoutsukai.potion.ClairvoyanceEyesPotion.clairvoyanceEyesPlayerTick(player);}
                stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player, tick);
                stepsword.mahoutsukai.potion.BindingEyesPotion.bindingEyesPlayerTick(player);
                if(tick % stepsword.mahoutsukai.config.MTConfig.BLACK_FLAME_IGNITION_TICKS==0) stepsword.mahoutsukai.potion.BlackFlameEyesPotion.blackFlameEyesPlayerTick(player);
            }
        });
        stepsword.mahoutsukai.block.ModBlocks.init();
        stepsword.mahoutsukai.tile.ModTileEntities.init();
        stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar.init();
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{stepsword.mahoutsukai.item.emrys.StaffEmrys.staffs.clear();stepsword.mahoutsukai.item.nobu.Nobu.player_storage.clear();});
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.item.replica.Replica.DAMAGE_SOURCE_REGISTRY.clear());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> stepsword.mahoutsukai.mana.ActiveCircleConfig.clearActiveCircles());
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.fluids.MurkyWaterBlock.clear());
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server->stepsword.mahoutsukai.util.MahouJobs.runJobs());
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->stepsword.mahoutsukai.potion.InsightEyesPotion.hadTargets.remove(handler.player));
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{stepsword.mahoutsukai.util.MahouJobs.clear();stepsword.mahoutsukai.potion.InsightEyesPotion.hadTargets.clear();});
        ServerPlayerEvents.JOIN.register(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings::updateClientMahou);
        ServerPlayerEvents.COPY_FROM.register((oldPlayer,newPlayer,alive)->stepsword.mahoutsukai.util.Utils.getSettingsMahou(newPlayer).copyMahou(stepsword.mahoutsukai.util.Utils.getSettingsMahou(oldPlayer)));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(newPlayer));
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll.gandrs.clear());
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll.storages.clear());
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityAttackFromPlayer(player,target);return net.minecraft.world.InteractionResult.PASS;});
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{stepsword.mahoutsukai.item.spells.mystic.SpatialDisorientation.SpatialDisorientationStaff.staffs.clear();stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff.staffs.clear();stepsword.mahoutsukai.item.morgan.Morgan.staffs.clear();});
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{stepsword.mahoutsukai.item.caliburn.Caliburn.upgradeDamageCap();return net.minecraft.world.InteractionResult.PASS;});
        net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register((living,source)->{
            if(living instanceof net.minecraft.world.entity.player.Player player){
                // Death must not leave the owner's summoned Nobu constructs behind.
                stepsword.mahoutsukai.item.nobu.Nobu.clearNobuStorage(player);
                if(stepsword.mahoutsukai.config.MTConfig.ONE_LIFE){
                    var m=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);m.setMaxMana(100);m.setManaToUseCounter(100);m.setDeathCollectionUsesLeft(0);m.setDamageReplicationTarget(null);m.clearBuffs();
                }
            }
        });
        MahouNetworking.register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            try {
                ManaConfig.current = ManaConfig.load(server.getWorldPath(LevelResource.ROOT)
                    .resolve("serverconfig/mahoutsukai-fabric-mana.json"));
                stepsword.mahoutsukai.config.MTConfig.load(server.getWorldPath(LevelResource.ROOT)
                    .resolve("serverconfig/mahoutsukai-fabric-economy.json"));
                stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity.init();
            } catch (IOException | RuntimeException exception) {
                throw new IllegalStateException("Cannot load world mana progression config", exception);
            }
        });
        ServerPlayerEvents.JOIN.register(MahouNetworking::sync);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> MahouNetworking.sync(newPlayer));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ((MahouHolder) newPlayer).mahoutsukai$getMahou().copyMahou(((MahouHolder) oldPlayer).mahoutsukai$getMahou());
            ((stepsword.mahoutsukai.doom.DoomHolder)newPlayer).mahoutsukai$setDoomReadyAt(
                ((stepsword.mahoutsukai.doom.DoomHolder)oldPlayer).mahoutsukai$getDoomReadyAt());
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            stepsword.mahoutsukai.commands.SetMahouX.register(dispatcher);
            stepsword.mahoutsukai.commands.MahouSummon.register(dispatcher,context);
            stepsword.mahoutsukai.commands.CancelBoundaries.register(dispatcher);
            stepsword.mahoutsukai.commands.ClearFictions.register(dispatcher);
            stepsword.mahoutsukai.commands.ClearPlayerFictions.register(dispatcher);
            stepsword.mahoutsukai.commands.SetKodoku.register(dispatcher);
            GetMana.register(dispatcher);
            stepsword.mahoutsukai.commands.SetMahouVisible.register(dispatcher);
            GetMaxMana.register(dispatcher);
            SetMaxMana.register(dispatcher);
            AddMaxMana.register(dispatcher);
            stepsword.mahoutsukai.commands.AddMana.register(dispatcher);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register(entries -> ModItems.REGISTERED.values().forEach(entries::accept));
        LoggerFactory.getLogger("mahoutsukai").warn(
            "INCOMPLETE Fabric port: mana economy/regeneration, all 53 original spell/ritual families, Power Consolidation swords, fae, Kodoku, familiar and custom DOOM are connected. "
            + "All 53 ritual families and the seven remaining original items have native server checks; 81/81 original item IDs are registered. 27/27 original entity types have native server checks; projectors, HUD, Abyss, soft fog and Treasury clipping are connected. Original NeoForge saves are imported with fixture and clean packaged-server checks; JEI is available. Optional local Jousting and Apotheosis Adventure Fabric 0.2.1 integration have focused checks. Full visual and gameplay parity across every combination remains unverified.");
    }
}
