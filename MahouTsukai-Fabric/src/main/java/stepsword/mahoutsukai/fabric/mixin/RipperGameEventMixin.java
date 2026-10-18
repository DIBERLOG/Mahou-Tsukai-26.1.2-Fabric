package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
@Mixin(ServerLevel.class)
public abstract class RipperGameEventMixin {
 @Inject(method="gameEvent",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$silence(Holder<GameEvent> event,Vec3 position,GameEvent.Context context,CallbackInfo ci){if(stepsword.mahoutsukai.potion.RipperInvisPotion.presenceConcealmentNoSound(context.sourceEntity()))ci.cancel();}
}
