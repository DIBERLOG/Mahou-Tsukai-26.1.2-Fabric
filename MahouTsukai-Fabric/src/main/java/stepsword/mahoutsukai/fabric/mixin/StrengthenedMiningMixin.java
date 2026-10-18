package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
@Mixin(Player.class)
public abstract class StrengthenedMiningMixin {
 @ModifyReturnValue(method="getDestroySpeed",at=@At("RETURN"))
 private float mahoutsukai$speed(float speed) { return stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.strengtheningBreakSpeed((Player)(Object)this,speed); }
}
