package stepsword.mahoutsukai.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Separate from the victory banner so old clients can keep the existing victory protocol. */
public record LootReturnPacket(int remainingTicks) implements CustomPacketPayload {
 public static final Type<LootReturnPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","boss_loot_return"));
 public static final StreamCodec<RegistryFriendlyByteBuf,LootReturnPacket> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,LootReturnPacket::remainingTicks,LootReturnPacket::new);
 public Type<LootReturnPacket> type(){return TYPE;}
}
