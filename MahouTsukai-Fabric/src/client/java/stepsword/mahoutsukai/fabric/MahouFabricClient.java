package stepsword.mahoutsukai.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import stepsword.mahoutsukai.networking.PlayerMahouPacket;
import org.slf4j.LoggerFactory;

public final class MahouFabricClient implements ClientModInitializer {
    /** Once per session: explain a hidden mana HUD instead of letting it look lost. */
    static boolean hudHiddenHintShown = false;
    /** Once per join: log the mana HUD preconditions, so a missing HUD can be diagnosed from the log. */
    static boolean hudDiagnosticsLogged = false;

    @Override
    public void onInitializeClient() {
        stepsword.mahoutsukai.render.gui.PauseMenuBranding.init();
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.BossVictoryPacket.TYPE,(packet,context)->stepsword.mahoutsukai.render.gui.VictoryOverlay.show(packet.theme(),packet.bossName()));
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.LootReturnPacket.TYPE,(packet,context)->stepsword.mahoutsukai.render.gui.VictoryOverlay.lootReturn(packet.remainingTicks()));
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->stepsword.mahoutsukai.render.gui.VictoryOverlay.clear());
        try {stepsword.mahoutsukai.config.MTConfig.loadClient(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("mahoutsukai-fabric-client.json"));}
        catch(java.io.IOException | RuntimeException e){throw new IllegalStateException("Cannot load Mahou client settings",e);}
        ClientPlayNetworking.registerGlobalReceiver(ServerSettingsPacket.TYPE,(packet,context)->packet.apply());
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{stepsword.mahoutsukai.config.MTConfig.resetServerSettings();hudHiddenHintShown=false;hudDiagnosticsLogged=false;});
        SixFamilyClient.init();
        SpellVisuals.init();
        ProjectorClient.init();
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.cupOfHeaven.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.clairvoyanceEyes.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.insightEyes.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.powerConsolidation.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry.register(stepsword.mahoutsukai.fluids.ModFluids.MURKY_STILL,stepsword.mahoutsukai.fluids.ModFluids.MURKY_FLOW,
            new net.minecraft.client.renderer.block.FluidModel.Unbaked(new net.minecraft.client.resources.model.sprite.Material(stepsword.mahoutsukai.fluids.MurkyFluid.STILL,true),new net.minecraft.client.resources.model.sprite.Material(stepsword.mahoutsukai.fluids.MurkyFluid.FLOW,true),null,state->stepsword.mahoutsukai.fluids.MurkyFluid.COLOR));

        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.blackFlameEyes.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.BlackFirePacket.TYPE,(packet,context)-> {
            var entity=context.client().level.getEntity(packet.entityids);
            if(entity instanceof net.minecraft.world.entity.LivingEntity living) {
                if(packet.onFire) stepsword.mahoutsukai.util.EffectUtil.buff(living,stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING,false,180);
                else living.removeEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING);
            }
        });
        net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(stepsword.mahoutsukai.render.particle.ModParticles.PETAL.get(),stepsword.mahoutsukai.render.particle.PetalParticleType.PetalFactory::new);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.FAMILIAR.get(),stepsword.mahoutsukai.render.entity.familiar.RenderFamiliarEntity::new);
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.GardenPacket.TYPE,(packet,context)->stepsword.mahoutsukai.entity.FamiliarEntity.gardenEffectB(stepsword.mahoutsukai.util.Utils.toBlockPos(packet.posX,packet.posY,packet.posZ),context.client().level));
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.familiarsGarden.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.swapFamiliar.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.recallFamiliar.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.summonFamiliar.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.proximityProjection.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.projection.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        stepsword.mahoutsukai.Keybinds.init();
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client->stepsword.mahoutsukai.render.overlay.RenderInterestingBlock.updateInterestingBlocks());
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context-> {
            var client=net.minecraft.client.Minecraft.getInstance();
            if(client.player==null || client.screen!=null) return;
            stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.selectedEntity(client.getCameraEntity());
            stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.renderSelectedEntity(context);
            stepsword.mahoutsukai.render.overlay.RenderInterestingBlock.interestingBlockOverlay(context);
        });
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client->stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.releaseSelection());
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.selectiveDisplacement.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.damageReplication.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.strengthening.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(stepsword.mahoutsukai.render.entity.kodoku.ModelKodoku.KODOKU_LAYER,stepsword.mahoutsukai.render.entity.kodoku.ModelKodoku::createBodyLayer);
        net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(stepsword.mahoutsukai.render.entity.archmage.ArchmageModel.LAYER,stepsword.mahoutsukai.render.entity.archmage.ArchmageModel::createBodyLayer);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.KODOKU.get(),stepsword.mahoutsukai.render.entity.kodoku.RenderKodoku::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.deathCollectionEyes.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.projectileDisplacement.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.retribution.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.bindingEyes.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.equivalentDisplacement.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.orderedDisplacement.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.invisibleBarrier.get(),stepsword.mahoutsukai.render.tile.InvisibleBarrierBlockRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.contract.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.tangibleBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.raiseEnclosureBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.drainLifeBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.displacementBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.gravityBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.alarmBoundary.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.durabilityExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.alchemicalExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.catalystExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.chronalExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.protectiveDisplacement.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.immunityExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.damageExchange.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.geas.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (stepsword.mahoutsukai.Keybinds.drawMahoujin.consumeClick())
                if (client.player != null && ClientPlayNetworking.canSend(stepsword.mahoutsukai.networking.KeyPressPacket.TYPE))
                    ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.KeyPressPacket("key.drawMahoujin"));
        });
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.mahoujin.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.faySightEyes.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.ascension.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.probabilityAlter.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
        stepsword.mahoutsukai.render.RenderBaseItem.init();
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(),
            stepsword.mahoutsukai.render.entity.DoomRenderer::new);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.FIREBALL.get(),
            stepsword.mahoutsukai.render.entity.FireballRenderer::new);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.FAE.get(),
            stepsword.mahoutsukai.render.entity.RenderFaeEntity::new);
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.FaeNoisePacket.TYPE, (packet, context) -> {
            var level = context.client().level;
            if (level != null && stepsword.mahoutsukai.config.MTConfig.FAE_NOISE)
                level.playSound(context.player(), packet.x, packet.y, packet.z, packet.sound, packet.cat, packet.vol, packet.pit);
        });
        ClientPlayNetworking.registerGlobalReceiver(EconomyConfigPacket.TYPE, (packet, context) -> packet.apply());
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
            new EconomyConfigPacket(10000, 5000, 100000, 100000).apply());
        net.minecraft.client.renderer.special.SpecialModelRenderers.ID_MAPPER.put(
            stepsword.mahoutsukai.render.item.MahouSpecialItemModel.TYPE_ID,
            stepsword.mahoutsukai.render.item.MahouSpecialItemModel.Unbaked.MAP_CODEC);
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client ->
            stepsword.mahoutsukai.client.ClientHandler.clientTickCounter =
                (stepsword.mahoutsukai.client.ClientHandler.clientTickCounter + 1) % 50000L);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
            stepsword.mahoutsukai.tile.ModTileEntities.circuit.get(), stepsword.mahoutsukai.render.tile.ManaCircuitRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
            stepsword.mahoutsukai.tile.ModTileEntities.circuitMagitech.get(), stepsword.mahoutsukai.render.tile.ManaCircuitMagitechRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.GuideCatalogPacket.TYPE,(packet,context)->context.client().setScreen(new stepsword.mahoutsukai.render.gui.guide.GuideCatalogScreen(packet.json())));
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SpellStatusPacket.TYPE,(packet,context)->stepsword.mahoutsukai.render.gui.MahouGui.doomTicks=packet.doomTicks());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(c->{if(stepsword.mahoutsukai.render.gui.MahouGui.doomTicks>0)stepsword.mahoutsukai.render.gui.MahouGui.doomTicks--;});
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,c)->stepsword.mahoutsukai.render.gui.MahouGui.doomTicks=0);
        ClientPlayNetworking.registerGlobalReceiver(PlayerMahouPacket.TYPE, (packet, context) -> {
            ((MahouHolder) context.player()).mahoutsukai$getMahou().copyMahou(packet.state());
            LoggerFactory.getLogger("mahoutsukai").info("Received original player mahou state: {}/{}",
                packet.state().getStoredMana(), packet.state().getMaxMana());
            // The mana HUD can be hidden with /showmahou; explain it once instead of leaving a "lost" HUD.
            if (packet.state().hasMagic() && !packet.state().getVisible() && !stepsword.mahoutsukai.fabric.MahouFabricClient.hudHiddenHintShown) {
                stepsword.mahoutsukai.fabric.MahouFabricClient.hudHiddenHintShown = true;
                context.player().sendSystemMessage(net.minecraft.network.chat.Component.translatable("mahoutsukai.hud.hidden_hint"));
            }
            if (!stepsword.mahoutsukai.fabric.MahouFabricClient.hudDiagnosticsLogged) {
                stepsword.mahoutsukai.fabric.MahouFabricClient.hudDiagnosticsLogged = true;
                var window = context.client().getWindow();
                LoggerFactory.getLogger("mahoutsukai").info(
                    "Mana HUD preconditions: hasMagic={} visible={} stored={} max={} placement={},{} screen={}x{}",
                    packet.state().hasMagic(), packet.state().getVisible(), packet.state().getStoredMana(), packet.state().getMaxMana(),
                    stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X, stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y,
                    window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        });
    }
}
