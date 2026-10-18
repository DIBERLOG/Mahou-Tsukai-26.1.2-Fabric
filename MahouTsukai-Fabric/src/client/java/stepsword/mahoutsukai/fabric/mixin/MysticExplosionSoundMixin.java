package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Original client preference filters this sound regardless of its server source. */
@Mixin(SoundEngine.class)
public abstract class MysticExplosionSoundMixin {
 @Inject(method="play",at=@At("HEAD"),cancellable=true)
 private void mahou$filterExplosion(SoundInstance sound,CallbackInfoReturnable<SoundEngine.PlayResult> result){
  if(!stepsword.mahoutsukai.config.MTConfig.MYSTIC_STAFF_EXPLOSION_SOUND && sound.getIdentifier().equals(stepsword.mahoutsukai.sounds.ModSounds.EXPLOSION.get().location()))
   result.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
 }
}
