package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.Minecraft;
@Mixin(Minecraft.class)
public abstract class RemoteViewActionMixin {
 // Vanilla drains attack clicks without calling startAttack while an item is in use.
 @Inject(method="handleKeybinds",at=@At("HEAD"))
 private void mahoutsukai$nobuTrigger(CallbackInfo ci){
  var mc=Minecraft.getInstance();var p=mc.player;
  if(stepsword.mahoutsukai.Keybinds.fireNobu.isDefault())
   while(stepsword.mahoutsukai.Keybinds.fireNobu.consumeClick())
    if(mc.screen==null && p!=null && p.isUsingItem() && p.getUseItem().getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu)stepsword.mahoutsukai.client.NobuClientEffect.nobuAimFireHandler(p,0);
 }
 private boolean mahoutsukai$leaveRemote(){var player=Minecraft.getInstance().player;return player!=null && (stepsword.mahoutsukai.client.MentalDisplacementClientEffect.mentalDisplacementDisallowedAction(player) || stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityDisallowedAction(player));}
 @Inject(method="startAttack",at=@At("HEAD"),cancellable=true) private void mahoutsukai$attack(CallbackInfoReturnable<Boolean> ci){if(mahoutsukai$leaveRemote()){ci.setReturnValue(false);return;}
  var mc=Minecraft.getInstance();var p=mc.player;
  if(p!=null && p.hasEffect(stepsword.mahoutsukai.potion.ModEffects.FORESIGHT) && mc.hitResult!=null && mc.hitResult.getType()==net.minecraft.world.phys.HitResult.Type.MISS)net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.ButterflyHitPacket());
  if(p!=null && mc.hitResult!=null && mc.hitResult.getType()!=net.minecraft.world.phys.HitResult.Type.ENTITY){
   int radius=stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_RADIUS;
   for(var target:p.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,p.getBoundingBox().inflate(radius)))
    if(stepsword.mahoutsukai.networking.SweepAttackPacket.canAttack(p,target))net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.SweepAttackPacket(target));
  }}
 @Inject(method="startUseItem",at=@At("HEAD"),cancellable=true) private void mahoutsukai$use(CallbackInfo ci){if(mahoutsukai$leaveRemote()){ci.cancel();return;}var p=Minecraft.getInstance().player;if(p!=null)stepsword.mahoutsukai.client.NobuClientEffect.nobuAimFireHandler(p,1);}
}
