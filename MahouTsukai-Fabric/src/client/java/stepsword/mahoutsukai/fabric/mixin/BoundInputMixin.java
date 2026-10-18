package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(KeyboardInput.class)
public abstract class BoundInputMixin extends ClientInput {
 @Inject(method="tick",at=@At("TAIL"))
 private void mahoutsukai$stop(CallbackInfo ci) {
  var player=Minecraft.getInstance().player;
  if(player!=null && stepsword.mahoutsukai.util.EffectUtil.hasBuff(player,stepsword.mahoutsukai.potion.ModEffects.CONFUSION)){
   var old=keyPresses;keyPresses=new net.minecraft.world.entity.player.Input(old.backward(),old.forward(),old.right(),old.left(),false,old.shift(),old.sprint());
   moveVector=new Vec2(-moveVector.x,-moveVector.y);
  }
  if(player!=null && !stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player))stepsword.mahoutsukai.client.BorrowedAuthorityClientEffect.borrowedAuthorityJumpEffect(player,this);
  if(player!=null)stepsword.mahoutsukai.client.MentalDisplacementClientEffect.mentalDisplacementInputUpdate(this,player);
  if(player!=null)stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityInputUpdate(this,player);
  if(player!=null && stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player)) {
   keyPresses=stepsword.mahoutsukai.potion.BoundPotion.STANDING_STILL;moveVector=Vec2.ZERO;
  }
 }
}
