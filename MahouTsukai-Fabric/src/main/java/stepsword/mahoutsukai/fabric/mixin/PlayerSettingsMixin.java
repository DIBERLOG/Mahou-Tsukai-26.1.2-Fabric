package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.*;
import stepsword.mahoutsukai.dataattachments.settingsmahou.*;
@Mixin(Player.class)
public abstract class PlayerSettingsMixin implements stepsword.mahoutsukai.fabric.SettingsHolder {
 @Unique private final SettingsMahou mahoutsukai$settings=new SettingsMahou();
 public SettingsMahou mahoutsukai$getSettings(){return mahoutsukai$settings;}
 @Inject(method="readAdditionalSaveData",at=@At("TAIL"))
 private void mahoutsukai$readSettings(ValueInput input,CallbackInfo ci){stepsword.mahoutsukai.fabric.LegacyMahouData.attachment(input,"settings_mahou").ifPresent(child->SettingsMahouStorage.read(mahoutsukai$settings,child));}
 @Inject(method="addAdditionalSaveData",at=@At("TAIL"))
 private void mahoutsukai$writeSettings(ValueOutput output,CallbackInfo ci){SettingsMahouStorage.write(mahoutsukai$settings,output.child("mahoutsukai:settings_mahou"));}
}
