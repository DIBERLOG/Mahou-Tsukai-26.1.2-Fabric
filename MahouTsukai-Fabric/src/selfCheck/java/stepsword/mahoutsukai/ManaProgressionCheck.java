package stepsword.mahoutsukai;

import java.nio.file.Files;
import java.nio.file.Path;
import stepsword.mahoutsukai.dataattachments.mahou.Mahou;
import stepsword.mahoutsukai.fabric.ManaConfig;
import stepsword.mahoutsukai.fabric.ManaProgression;

public final class ManaProgressionCheck {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void run() throws Exception {
        Mahou state = new Mahou();
        ManaProgression.recordSpent(state, 99, ManaConfig.DEFAULTS);
        require(state.getMaxMana() == 100 && state.getManaToUseCounter() == 1, "No growth before threshold");
        ManaProgression.recordSpent(state, 1, ManaConfig.DEFAULTS);
        require(state.getMaxMana() == 101 && state.getManaToUseCounter() == 100, "Exact threshold grows once");
        state = new Mahou();
        ManaProgression.recordSpent(state, 350, ManaConfig.DEFAULTS);
        require(state.getMaxMana() == 103 && state.getManaToUseCounter() == 50, "Overspend carries remainder");
        require(state.getStoredMana() == 100, "Progression does not refill or drain mana");
        state = new Mahou();
        ManaProgression.recordSpent(state, 1000, new ManaConfig.Settings(200000, 100, 0, 100, 1, true, 1));
        require(state.getMaxMana() == 105, "Original logarithmic punishment rounds growth");
        state = new Mahou();
        ManaProgression.recordSpent(state, 100, new ManaConfig.Settings(100, 100, 0, 100, 1, false, 0));
        require(state.getMaxMana() == 100, "Progression obeys cap");
        state = new Mahou();
        state.setMaxMana(1000);
        ManaProgression.recordSpent(state, 150, new ManaConfig.Settings(200000, 100, 0.25, 1000, 1, false, 0));
        require(state.getMaxMana() == 1001 && state.getManaToUseCounter() == 200, "Ratio threshold preserves old counter remainder");

        Path directory = Files.createTempDirectory("mahou-config-check-");
        Path config = directory.resolve("mana.json");
        try {
            require(ManaConfig.load(config).equals(ManaConfig.DEFAULTS), "Default config creation");
            Files.writeString(config, "{\"MANA_UP_COUNTER\":250}");
            var loaded = ManaConfig.load(config);
            require(loaded.MANA_UP_COUNTER() == 250 && loaded.MAX_MANA_CAP() == 200000, "Partial config keeps defaults");
            for (String invalid : new String[]{
                "{\"MANA_UP_COUNTER\":0}", "{\"MANA_INCREASE\":1.5}",
                "{\"MAX_MANA_CAP\":4294967296}", "{\"MANA_UP_COUNTER\":\"100\"}",
                "{\"UNKNOWN_SETTING\":1}", "{\"MANA_INCREASE_LOG_PUNISHMENT\":0}"}) {
                Files.writeString(config, invalid);
                boolean rejected = false;
                try { ManaConfig.load(config); } catch (RuntimeException expected) { rejected = true; }
                require(rejected, "Reject invalid config: " + invalid);
            }
        } finally {
            Files.deleteIfExists(config);
            Files.deleteIfExists(directory);
        }
        System.out.println("PASS: original progression thresholds/carry/cap/logarithmic growth and world config validation");
    }
}
