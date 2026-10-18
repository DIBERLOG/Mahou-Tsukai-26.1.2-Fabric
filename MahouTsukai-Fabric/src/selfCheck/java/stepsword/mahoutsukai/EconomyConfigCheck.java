package stepsword.mahoutsukai;

import io.netty.buffer.Unpooled;
import java.nio.file.Files;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.fabric.EconomyConfigPacket;

public final class EconomyConfigCheck {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    public static void run() throws Exception {
        var original = EconomyConfigPacket.current();
        var different = new EconomyConfigPacket(12000, 6000, 250000, 350000, 123, 2000, 70, 60, 120, 12, 16, 13.5);
        var wire = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            EconomyConfigPacket.CODEC.encode(wire, different);
            require(wire.readableBytes() == 52, "Eleven integer settings and original double lake fog radius");
            require(EconomyConfigPacket.CODEC.decode(wire).equals(different) && !wire.isReadable(), "Capacity sync round trip");
            different.apply();
            require(EconomyConfigPacket.current().equals(different), "Client values match server capacities");
        } finally {
            original.apply();
            wire.release();
        }
        var directory = Files.createTempDirectory("mahou-economy-config-");
        var path = directory.resolve("economy.json");
        try {
            MTConfig.load(path);
            Files.writeString(path, "{\"ATTUNED_DIAMOND_CAPACITY\":12000}");
            MTConfig.load(path);
            require(MTConfig.ATTUNED_DIAMOND_CAPACITY == 12000 && MTConfig.MANA_CIRCUIT_RANGE == 10, "Partial world economy override");
            Files.writeString(path, "{\"ATTUNED_DIAMOND_CAPACITY\":12345,\"MANA_CIRCUIT_RANGE\":1.5}");
            boolean rejected = false;
            try { MTConfig.load(path); } catch (RuntimeException expected) { rejected = true; }
            require(rejected && MTConfig.ATTUNED_DIAMOND_CAPACITY == 12000, "Invalid config commits no partial changes");
            Files.writeString(path, "{\"DEATH_COLLECTION_DRAIN_PER_SOUL\":10000001}");
            rejected = false;
            try { MTConfig.load(path); } catch (RuntimeException expected) { rejected = true; }
            require(rejected, "Soul drain uses original ten-million upper bound");
            Files.writeString(path, "{}");
            MTConfig.load(path);
            require(MTConfig.ATTUNED_DIAMOND_CAPACITY == 10000, "World switch resets omitted keys to originals");
        } finally {
            Files.deleteIfExists(path);
            Files.deleteIfExists(directory);
        }
        System.out.println("PASS: economy capacity sync, partial config, atomic validation and world reset");
    }
}
