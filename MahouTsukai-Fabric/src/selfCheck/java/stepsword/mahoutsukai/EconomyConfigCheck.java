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
            Files.writeString(path,"{\"PROXIMITY_PROJECTION_DURABILITY\":30,\"NOBU_DISTANCE_REQUIREMENT\":200,\"POWER_CONSOLIDATION_FOG_RADIUS\":8}");
            String legacy=Files.readString(path);MTConfig.load(path);
            require(MTConfig.PROXIMITY_PROJECTION_DURABILITY==130&&MTConfig.NOBU_DISTANCE_REQUIREMENT==100&&MTConfig.POWER_CONSOLIDATION_FOG_RADIUS==24,"Upgrade old shipped balance defaults");
            require(Files.readString(path.resolveSibling(path.getFileName()+".before-0.26")).equals(legacy),"Migration preserves exact config backup");
            Files.writeString(path,"{\"PROXIMITY_PROJECTION_DURABILITY\":30,\"NOBU_DISTANCE_REQUIREMENT\":175}");MTConfig.load(path);
            require(MTConfig.PROXIMITY_PROJECTION_DURABILITY==30&&MTConfig.NOBU_DISTANCE_REQUIREMENT==175,"Subsequent user edits preserved even when matching old defaults");
            Files.delete(path.resolveSibling(path.getFileName()+".progression-0.26"));
            Files.writeString(path,"{\"PROXIMITY_PROJECTION_DURABILITY\":90,\"NOBU_DISTANCE_REQUIREMENT\":175,\"POWER_CONSOLIDATION_FOG_RADIUS\":12}");MTConfig.load(path);
            require(MTConfig.PROXIMITY_PROJECTION_DURABILITY==90&&MTConfig.NOBU_DISTANCE_REQUIREMENT==175&&MTConfig.POWER_CONSOLIDATION_FOG_RADIUS==12,"First upgrade preserves custom settings");
            Files.writeString(path,"{}");
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
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".clarent-0.27"));
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".before-0.27"));
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".progression-0.26"));
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".before-0.26"));
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".nobu-0.31"));
            Files.deleteIfExists(path.resolveSibling(path.getFileName()+".before-nobu-0.31"));
            Files.deleteIfExists(directory);
        }
        var clientDir=Files.createTempDirectory("mahou-hints-config-");var clientPath=clientDir.resolve("client.json");boolean oldHints=MTConfig.SHOW_CAST_HINTS;
        try {
            require(MTConfig.defaultSettings().get("SHOW_CAST_HINTS").getAsBoolean(),"Hints enabled by shipped default");
            MTConfig.loadClient(clientPath);require(MTConfig.SHOW_CAST_HINTS,"New client enables hints");
            MTConfig.SHOW_CAST_HINTS=false;MTConfig.saveClient(clientPath);MTConfig.SHOW_CAST_HINTS=true;MTConfig.loadClient(clientPath);
            require(!MTConfig.SHOW_CAST_HINTS,"Hint toggle persists across reload");
            require(!MTConfig.serverSettings().has("SHOW_CAST_HINTS"),"Server cannot override local hint preference");
            Files.writeString(clientPath,"{}");MTConfig.loadClient(clientPath);require(MTConfig.SHOW_CAST_HINTS,"Older client configs default hints on");
        }finally{MTConfig.SHOW_CAST_HINTS=oldHints;Files.deleteIfExists(clientPath);Files.deleteIfExists(clientDir);}
        System.out.println("PASS: economy capacity sync, partial config, atomic validation and world reset");
    }
}
