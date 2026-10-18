package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EmrysLightningMixin {
 @Inject(method="thunderHit",at=@At("HEAD")) private void mahoutsukai$emrys(net.minecraft.server.level.ServerLevel world,net.minecraft.world.entity.LightningBolt bolt,CallbackInfo ci){stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysStruckByLightning((net.minecraft.world.entity.Entity)(Object)this);}
}
