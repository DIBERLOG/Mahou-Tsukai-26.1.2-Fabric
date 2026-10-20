package stepsword.mahoutsukai.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BossVictoryPacket(int theme,Component bossName) implements CustomPacketPayload {
 public static final Type<BossVictoryPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","boss_victory"));
 public static final StreamCodec<RegistryFriendlyByteBuf,BossVictoryPacket> STREAM_CODEC=StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.VAR_INT,BossVictoryPacket::theme,ComponentSerialization.STREAM_CODEC,BossVictoryPacket::bossName,BossVictoryPacket::new);
 public Type<BossVictoryPacket> type(){return TYPE;}
}
