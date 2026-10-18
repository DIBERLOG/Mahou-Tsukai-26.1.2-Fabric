package stepsword.mahoutsukai.fabric;

import com.google.gson.JsonParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.config.MTConfig;

/** Complete server gameplay settings, excluding local graphics/HUD preferences. */
public record ServerSettingsPacket(String json) implements CustomPacketPayload {
    public static final Type<ServerSettingsPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","server_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ServerSettingsPacket> CODEC=StreamCodec.composite(
        ByteBufCodecs.stringUtf8(262144),ServerSettingsPacket::json,ServerSettingsPacket::new);
    public static ServerSettingsPacket current(){return new ServerSettingsPacket(MTConfig.serverSettings().toString());}
    public void apply(){MTConfig.applyServerSettings(JsonParser.parseString(json).getAsJsonObject());}
    @Override public Type<ServerSettingsPacket> type(){return TYPE;}
}
