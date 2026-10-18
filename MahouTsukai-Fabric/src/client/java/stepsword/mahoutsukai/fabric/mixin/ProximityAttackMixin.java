package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys;
import stepsword.mahoutsukai.networking.LongDistanceAttackPacket;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.Utils;

/** Original left-click-empty ability: a block hit or normal entity hit follows vanilla handling. */
@Mixin(Minecraft.class)
public abstract class ProximityAttackMixin {
    @Inject(method="startAttack",at=@At("HEAD"))
    private void mahoutsukai$remoteAttack(CallbackInfoReturnable<Boolean> ci) {
        var client=(Minecraft)(Object)this;var player=client.player;
        if(player==null || client.screen!=null || client.hitResult==null || client.hitResult.getType()!=HitResult.Type.MISS
            || player.hasEffect(ModEffects.BOUND) || !(player.getMainHandItem().getItem() instanceof ProximityProjectionKeys)) return;
        var selection=new java.util.HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.WEAPON));
        var target=Utils.selectEntityNearCursor(player,MTConfig.GLOBAL_LOOK_RANGE,player.level(),e->Utils.getPredicate(selection,e,player),true);
        if(target instanceof net.minecraft.world.entity.LivingEntity)
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new LongDistanceAttackPacket(target));
    }
}
