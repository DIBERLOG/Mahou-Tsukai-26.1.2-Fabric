package stepsword.mahoutsukai.doom;
/** Per-player persistent cooldown, in overworld game ticks. */
public interface DoomHolder {
 long mahoutsukai$getDoomReadyAt();
 void mahoutsukai$setDoomReadyAt(long tick);
}
