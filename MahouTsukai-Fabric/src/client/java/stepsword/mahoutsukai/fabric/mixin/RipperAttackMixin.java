package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Minecraft.class)
public abstract class RipperAttackMixin {
 @Unique private boolean mahoutsukai$lastWasMain;
 @Shadow private int missTime;
 @Inject(method="startAttack",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$dualAttack(CallbackInfoReturnable<Boolean> ci){
  var client=(Minecraft)(Object)this;var player=client.player;
  if(player==null || client.level==null || client.gameMode==null || client.hitResult==null || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || !(player.getMainHandItem().getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper) || !(player.getOffhandItem().getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper))return;
  mahoutsukai$lastWasMain=!mahoutsukai$lastWasMain;var hand=mahoutsukai$lastWasMain?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND;
  switch(client.hitResult.getType()){
   case ENTITY->{var target=((EntityHitResult)client.hitResult).getEntity();player.connection.send(new ServerboundSetCarriedItemPacket(player.getInventory().getSelectedSlot()));net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.RipperUseEntityPacket(hand,target.getId()));if(!player.isSpectator()){stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.attack(player,target);player.resetAttackStrengthTicker();}}
   case BLOCK->{var hit=(net.minecraft.world.phys.BlockHitResult)client.hitResult;if(!client.level.isEmptyBlock(hit.getBlockPos()))client.gameMode.startDestroyBlock(hit.getBlockPos(),hit.getDirection());}
   case MISS->{if(client.gameMode.hasMissTime())missTime=10;player.resetAttackStrengthTicker();}
  }
  player.swing(hand);ci.setReturnValue(true);
 }
}
