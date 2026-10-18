package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.*;
@Mixin(net.minecraft.client.renderer.entity.state.LivingEntityRenderState.class)
public abstract class PresenceStateMixin implements stepsword.mahoutsukai.fabric.PresenceRenderState {
 @Unique private boolean mahoutsukai$concealed;
 public boolean mahoutsukai$isConcealed(){return mahoutsukai$concealed;}
 public void mahoutsukai$setConcealed(boolean value){mahoutsukai$concealed=value;}
}
