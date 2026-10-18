package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stepsword.mahoutsukai.dataattachments.mahou.Mahou;
import stepsword.mahoutsukai.dataattachments.mahou.MahouStorage;
import stepsword.mahoutsukai.fabric.MahouHolder;

/** Uses vanilla save hooks, keeping the original fields and their codecs. */
@Mixin(Player.class)
public abstract class PlayerMahouMixin implements MahouHolder, stepsword.mahoutsukai.doom.DoomHolder {
    @Unique private final Mahou mahoutsukai$mahou = new Mahou();

    @Unique private long mahoutsukai$doomReadyAt;
    public long mahoutsukai$getDoomReadyAt() { return mahoutsukai$doomReadyAt; }
    public void mahoutsukai$setDoomReadyAt(long tick) { mahoutsukai$doomReadyAt = tick; }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void mahoutsukai$wake(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci) {
        stepsword.mahoutsukai.mana.PlayerManaManager.onWake((Player)(Object)this, wakeImmediately, updateLevel);
    }

    @Override
    public Mahou mahoutsukai$getMahou() { return mahoutsukai$mahou; }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void mahoutsukai$read(ValueInput input, CallbackInfo ci) {
        mahoutsukai$doomReadyAt = input.getLongOr("mahoutsukai:doom_ready_at", 0);
        stepsword.mahoutsukai.fabric.LegacyMahouData.attachment(input,"player_mahou")
            .ifPresent(child -> MahouStorage.readNBT(mahoutsukai$mahou, child));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void mahoutsukai$write(ValueOutput output, CallbackInfo ci) {
        output.putLong("mahoutsukai:doom_ready_at", mahoutsukai$doomReadyAt);
        MahouStorage.writeNBT(mahoutsukai$mahou, output.child("mahoutsukai:player_mahou"));
    }
}
