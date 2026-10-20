package stepsword.mahoutsukai.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import stepsword.mahoutsukai.networking.PlayerMahouPacket;

public final class MahouNetworking {
    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.BossVictoryPacket.TYPE,stepsword.mahoutsukai.networking.BossVictoryPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.LootReturnPacket.TYPE,stepsword.mahoutsukai.networking.LootReturnPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SpellVisualPacket.TYPE,stepsword.mahoutsukai.networking.SpellVisualPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.GuideCatalogPacket.TYPE,stepsword.mahoutsukai.networking.GuideCatalogPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.StopMagicPacket.TYPE,stepsword.mahoutsukai.networking.StopMagicPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.StopMagicPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.StopMagicPacket.handle(context.player()));
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SpellStatusPacket.TYPE,stepsword.mahoutsukai.networking.SpellStatusPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.TurnedPagePacket.TYPE,stepsword.mahoutsukai.networking.TurnedPagePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.TurnedPagePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.TurnedPagePacket.handle(packet,context.player()));

        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ReplicaSwitchPacket.TYPE,stepsword.mahoutsukai.networking.ReplicaSwitchPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ReplicaSwitchPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.ReplicaSwitchPacket.handle(packet,context.player()));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.NobuSetTargetPacket.TYPE,stepsword.mahoutsukai.networking.NobuSetTargetPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.NobuSetTargetPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.NobuSetTargetPacket.handle(packet,context.player()));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.NobuBeamCreatePacket.TYPE,stepsword.mahoutsukai.networking.NobuBeamCreatePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.NobuBeamCreatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.NobuBeamCreatePacket.handle(packet,context.player()));
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.ActingPacket.TYPE,stepsword.mahoutsukai.networking.ActingPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SavePagePacket.TYPE,stepsword.mahoutsukai.networking.SavePagePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SavePagePacket.TYPE,(packet,context)->{if(packet.page()<0 || packet.page()>4096)return;var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(context.player());mahou.setGuidePage(packet.page());stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(context.player(),mahou);});

        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.RagePacket.TYPE,stepsword.mahoutsukai.networking.RagePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.BakuretsuPacket.TYPE,stepsword.mahoutsukai.networking.BakuretsuPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.BeamPacket.TYPE,stepsword.mahoutsukai.networking.BeamPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.ScryingEntityPacket.TYPE,stepsword.mahoutsukai.networking.ScryingEntityPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.DataManagerPacket.TYPE,stepsword.mahoutsukai.networking.DataManagerPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.ReplicaKnockbackParticlePacket.TYPE,stepsword.mahoutsukai.networking.ReplicaKnockbackParticlePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SparkExplosionPacket.TYPE,stepsword.mahoutsukai.networking.SparkExplosionPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.TYPE,stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.MysticCodeSwitchPacket.handleSwitch(packet,context.player()));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ButterflyHitPacket.TYPE,stepsword.mahoutsukai.networking.ButterflyHitPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ButterflyHitPacket.TYPE,(packet,context)->{if(!RemoteViewActions.leave(context.player()))stepsword.mahoutsukai.potion.ForesightPotion.butterflyHit(context.player());});
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SweepAttackPacket.TYPE,stepsword.mahoutsukai.networking.SweepAttackPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SweepAttackPacket.TYPE,(packet,context)->{var p=context.player();if(!stepsword.mahoutsukai.fabric.RemoteViewActions.leave(p))stepsword.mahoutsukai.networking.SweepAttackPacket.doAttack(p,p.level().getEntity(packet.entityId));});
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ItemAbilityPacket.TYPE,stepsword.mahoutsukai.networking.ItemAbilityPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ItemAbilityPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.ItemAbilityPacket.handleNative(packet,context.player()));
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.PossessEntityPacket.TYPE,stepsword.mahoutsukai.networking.PossessEntityPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.DonePossessingPacket.TYPE,stepsword.mahoutsukai.networking.DonePossessingPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.DonePossessingPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.DonePossessingPacket.handleDonePossess(packet,context.player()));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.DoneMentalPacket.TYPE,stepsword.mahoutsukai.networking.DoneMentalPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.MentalDisplacementUpdatePacket.TYPE,stepsword.mahoutsukai.networking.MentalDisplacementUpdatePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.DoneMentalPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.DoneMentalPacket.onMessage(packet,context.player()));
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.MentalDisplacementUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.MentalDisplacementUpdatePacket.handleNative(packet,context.player()));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.AuthorityAttackPacket.TYPE,stepsword.mahoutsukai.networking.AuthorityAttackPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.AuthorityStickPacket.TYPE,stepsword.mahoutsukai.networking.AuthorityStickPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.AuthorityJumpPacket.TYPE,stepsword.mahoutsukai.networking.AuthorityJumpPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.AuthoritySmokeRingPacket.TYPE,stepsword.mahoutsukai.networking.AuthoritySmokeRingPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.AuthorityPacket.TYPE,stepsword.mahoutsukai.networking.AuthorityPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SpellConfigPacket.TYPE,stepsword.mahoutsukai.networking.SpellConfigPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SettingsMahouPacket.TYPE,stepsword.mahoutsukai.networking.SettingsMahouPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket.TYPE,stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.RipperInvisiblePacket.TYPE,stepsword.mahoutsukai.networking.RipperInvisiblePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.FogPacket.TYPE,stepsword.mahoutsukai.networking.FogPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.RipperUseEntityPacket.TYPE,stepsword.mahoutsukai.networking.RipperUseEntityPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.RipperUseEntityPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.RipperUseEntityPacket.handle(context.player(),packet.id,packet.hand));
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.RevertedPacket.TYPE,stepsword.mahoutsukai.networking.RevertedPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.InsightPacket.TYPE,stepsword.mahoutsukai.networking.InsightPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.EntityPathPacket.TYPE,stepsword.mahoutsukai.networking.EntityPathPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.BlackFirePacket.TYPE,stepsword.mahoutsukai.networking.BlackFirePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.GardenPacket.TYPE,stepsword.mahoutsukai.networking.GardenPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.LongDistanceAttackPacket.TYPE,stepsword.mahoutsukai.networking.LongDistanceAttackPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.LongDistanceAttackPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.LongDistanceAttackPacket.doAttack(context.player(),context.player().level().getEntity(packet.entityId)));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE,stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(context.player(),packet.id1,packet.id2));
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.KeyPressPacket.TYPE, stepsword.mahoutsukai.networking.KeyPressPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.KeyPressPacket.TYPE,
            (packet, context) -> stepsword.mahoutsukai.networking.KeyPressPacket.handle(packet, context.player()));
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.FaeNoisePacket.TYPE,
            stepsword.mahoutsukai.networking.FaeNoisePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ServerSettingsPacket.TYPE,ServerSettingsPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EconomyConfigPacket.TYPE, EconomyConfigPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerMahouPacket.TYPE, PlayerMahouPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.AuthorityAttackPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.AuthorityAttackPacket.doAttack(context.player(),context.player().level().getEntity(packet.entityId)));
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.AuthorityStickPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.AuthorityStickPacket.authorityStick(packet,context.player()));
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.AuthorityJumpPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.AuthorityJumpPacket.authorityJump(packet,context.player()));
    }

    public static void sync(ServerPlayer player) {
        if (player.connection == null || player instanceof net.fabricmc.fabric.api.entity.FakePlayer) return;
        if(ServerPlayNetworking.canSend(player,ServerSettingsPacket.TYPE))ServerPlayNetworking.send(player,ServerSettingsPacket.current());
        if(ServerPlayNetworking.canSend(player,stepsword.mahoutsukai.networking.SpellConfigPacket.TYPE))ServerPlayNetworking.send(player,stepsword.mahoutsukai.networking.SpellConfigPacket.current());
        if (ServerPlayNetworking.canSend(player, EconomyConfigPacket.TYPE))
            ServerPlayNetworking.send(player, EconomyConfigPacket.current());
        if (ServerPlayNetworking.canSend(player, PlayerMahouPacket.TYPE)) {
            ServerPlayNetworking.send(player, new PlayerMahouPacket(((MahouHolder) player).mahoutsukai$getMahou()));
        }
    }
}
