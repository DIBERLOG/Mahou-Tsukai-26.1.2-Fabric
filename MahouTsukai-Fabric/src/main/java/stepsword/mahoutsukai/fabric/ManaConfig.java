package stepsword.mahoutsukai.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Server-world progression settings, retaining the original names/defaults/ranges. */
public final class ManaConfig {
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Settings DEFAULTS = new Settings(200000, 100, 0, 100, 1, false, 0);
    public static Settings current = DEFAULTS;

    public record Settings(int MAX_MANA_CAP, int MANA_UP_COUNTER, double MANA_UP_COUNTER_RATIO,
                           int MANA_UP_COUNTER_MAX, int MANA_INCREASE,
                           boolean MANA_INCREASE_LOG_PUNISHMENT, double MANA_INCREASE_PUNISHMENT_FACTOR) {
        public Settings {
            range(MAX_MANA_CAP, 0, 100000000, "MAX_MANA_CAP");
            range(MANA_UP_COUNTER, 1, 100000000, "MANA_UP_COUNTER");
            range(MANA_UP_COUNTER_RATIO, 0, 1.0E8, "MANA_UP_COUNTER_RATIO");
            range(MANA_UP_COUNTER_MAX, 0, 100000000, "MANA_UP_COUNTER_MAX");
            range(MANA_INCREASE, 0, 100000000, "MANA_INCREASE");
            range(MANA_INCREASE_PUNISHMENT_FACTOR, -1.0E7, 1.0E7, "MANA_INCREASE_PUNISHMENT_FACTOR");
        }
    }

    private static void range(double value, double min, double max, String name) {
        if (!Double.isFinite(value) || value < min || value > max)
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
    }

    public static Settings load(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, JSON.toJson(DEFAULTS) + "\n", StandardCharsets.UTF_8);
            return DEFAULTS;
        }
        JsonObject values = JSON.toJsonTree(DEFAULTS).getAsJsonObject();
        JsonObject overrides = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
        for (var entry : overrides.entrySet()) {
            if (!values.has(entry.getKey())) throw new IllegalArgumentException("Unknown mana setting: " + entry.getKey());
            var value = entry.getValue();
            var expected = values.get(entry.getKey()).getAsJsonPrimitive();
            if (!value.isJsonPrimitive() || (expected.isBoolean() ? !value.getAsJsonPrimitive().isBoolean() : !value.getAsJsonPrimitive().isNumber()))
                throw new IllegalArgumentException("Invalid type for " + entry.getKey());
            if (switch (entry.getKey()) {
                case "MAX_MANA_CAP", "MANA_UP_COUNTER", "MANA_UP_COUNTER_MAX", "MANA_INCREASE" -> true;
                default -> false;
            }) {
                try { value.getAsBigDecimal().intValueExact(); }
                catch (ArithmeticException exception) {
                    throw new IllegalArgumentException("32-bit integer required for " + entry.getKey(), exception);
                }
            }
            values.add(entry.getKey(), value);
        }
        return JSON.fromJson(values, Settings.class);
    }
}
