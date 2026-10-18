package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.renderer.entity.EntityRenderDispatcher.class)
public abstract class ActingRenderMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="extractEntity",at=@org.spongepowered.asm.mixin.injection.At("HEAD"),cancellable=true)
 private void mahoutsukai$actor(net.minecraft.world.entity.Entity actor,float partial,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.client.renderer.entity.state.EntityRenderState> ci){
  if(stepsword.mahoutsukai.client.ActingClientEffect.rendering.get() || !(actor instanceof net.minecraft.world.entity.LivingEntity living))return;
  var proxy=stepsword.mahoutsukai.client.ActingClientEffect.proxy(living);if(proxy==null)return;
  stepsword.mahoutsukai.client.ActingClientEffect.rendering.set(true);
  try{ci.setReturnValue(((net.minecraft.client.renderer.entity.EntityRenderDispatcher)(Object)this).extractEntity(proxy,partial));}finally{stepsword.mahoutsukai.client.ActingClientEffect.rendering.set(false);}
 }
}
