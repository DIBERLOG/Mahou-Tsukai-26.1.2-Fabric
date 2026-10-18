package stepsword.mahoutsukai.fabric;

import stepsword.mahoutsukai.dataattachments.mahou.IMahou;

/** Original PlayerManaManager.updateManaCounter/increaseMaxMana arithmetic.
 * This is progression only; it does not replace full gem/battery mana draining. */
public final class ManaProgression {
    public static void recordSpent(IMahou mahou, int mana, ManaConfig.Settings config) {
        if (mana < 0) throw new IllegalArgumentException("Spent mana cannot be negative");
        int max = mahou.getMaxMana();
        int manaUpCounter = config.MANA_UP_COUNTER_RATIO() > 0.0
            ? (int) Math.max(Math.min(config.MANA_UP_COUNTER_MAX(), max * config.MANA_UP_COUNTER_RATIO()), config.MANA_UP_COUNTER())
            : config.MANA_UP_COUNTER();
        double punishment = config.MANA_INCREASE_PUNISHMENT_FACTOR();
        punishment *= config.MANA_INCREASE_LOG_PUNISHMENT() ? Math.log(max) : max;
        int counter = mahou.getManaToUseCounter() - mana;
        int leftover = 0;
        if (counter <= 0) {
            int increase = config.MANA_INCREASE();
            if (counter < 0) {
                increase += Math.abs(counter * config.MANA_INCREASE() / manaUpCounter);
                leftover = Math.abs(counter) % manaUpCounter;
            }
            increase = (int) Math.max(1L, Math.round(increase - punishment));
            counter = manaUpCounter - leftover;
            mahou.setMaxMana(Math.min(config.MAX_MANA_CAP(), mahou.getMaxMana() + increase));
        }
        mahou.setManaToUseCounter(counter);
    }
}
