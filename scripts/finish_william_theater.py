from remaining_port_helpers import *
import json
p=main/'networking/ActingPacket.java';s=p.read_text(encoding='utf-8').replace('   int entityids;','   public int entityids;').replace('   int copy_id;','   public int copy_id;').replace('   boolean acting;','   public boolean acting;');write(p,s)
write(client/'client/ActingClientEffect.java','''package stepsword.mahoutsukai.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.squid.Squid;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.Utils;
public final class ActingClientEffect {
 private record Copy(java.util.UUID target,LivingEntity proxy){}
 private static final java.util.Map<Integer,Copy> copies=new java.util.HashMap<>();
 public static final ThreadLocal<Boolean> rendering=ThreadLocal.withInitial(()->false);
 public static void reset(){copies.clear();}
 public static LivingEntity proxy(LivingEntity actor){
  var mc=Minecraft.getInstance();boolean abyss=mc.getCameraEntity() instanceof LivingEntity viewer && viewer.hasEffect(ModEffects.ABYSS);
  LivingEntity target=actor.hasEffect(ModEffects.ACTING) && actor.level().getEntity(Utils.getLivingMahou(actor).getActing()) instanceof LivingEntity living?living:null;
  if(!abyss && target==null){copies.remove(actor.getId());return null;}
  var uuid=abyss?new java.util.UUID(0,0):target.getUUID();var cached=copies.get(actor.getId());LivingEntity proxy;
  if(cached!=null && cached.target().equals(uuid))proxy=cached.proxy();else{
   if(abyss)proxy=new Squid(EntityType.SQUID,mc.level);
   else if(target instanceof AbstractClientPlayer player)proxy=new RemotePlayer(mc.level,player.getGameProfile());
   else if(target.getType().create(mc.level,EntitySpawnReason.LOAD) instanceof LivingEntity living)proxy=living;else return null;
   copies.put(actor.getId(),new Copy(uuid,proxy));
  }
  if(target!=null && !abyss)for(var slot:EquipmentSlot.VALUES)proxy.setItemSlot(slot,target.getItemBySlot(slot).copy());
  proxy.snapTo(actor.getX(),actor.getY()+(abyss?1:0),actor.getZ(),actor.getYRot(),actor.getXRot());
  proxy.xo=proxy.xOld=actor.xo;proxy.yo=proxy.yOld=actor.yo+(abyss?1:0);proxy.zo=proxy.zOld=actor.zo;
  proxy.yRotO=actor.yRotO;proxy.xRotO=actor.xRotO;proxy.yHeadRot=actor.yHeadRot;proxy.yHeadRotO=actor.yHeadRotO;proxy.yBodyRot=actor.yBodyRot;proxy.yBodyRotO=actor.yBodyRotO;
  proxy.hurtTime=actor.hurtTime;proxy.hurtDuration=actor.hurtDuration;proxy.deathTime=actor.deathTime;
  proxy.walkAnimation.update(actor.walkAnimation.speed(),1,1);return proxy;
 }
}
''')
write(client/'fabric/mixin/ActingRenderMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
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
''')
p=res/'mahoutsukai.fabric.mixins.json';data=json.loads(p.read_text(encoding='utf-8'));data['client'].append('ActingRenderMixin');write(p,json.dumps(data,indent=2)+'\n')
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  ClientPlayNetworking.registerGlobalReceiver(ActingPacket.TYPE,(packet,context)->{if(context.client().level.getEntity(packet.entityids) instanceof net.minecraft.world.entity.LivingEntity living){stepsword.mahoutsukai.util.Utils.getLivingMahou(living).setActing(packet.acting?packet.copy_id:0);if(packet.acting)stepsword.mahoutsukai.util.EffectUtil.buff(living,ModEffects.ACTING,false,200);else living.removeEffect(ModEffects.ACTING);}});
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,mc)->{stepsword.mahoutsukai.client.ActingClientEffect.reset();stepsword.mahoutsukai.client.NobuClientEffect.clearTargets();stepsword.mahoutsukai.render.particle.ParticleSphere.active.clear();});
''');write(p,s)
# Native enchantment predicates: only the original Nobu set; First Sorcery never accepts enchantments.
p=main/'fabric/mixin/ProximityEnchantmentMixin.java';s=p.read_text(encoding='utf-8')
for anchor in ['        if(stack.getItem() instanceof ProximityProjectionKeys) ci.setReturnValue(true);','        if(stack.getItem() instanceof ProximityProjectionKeys)\n']:
 code='''        if(stack.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery){ci.setReturnValue(false);return;}
        if(stack.getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){var description=((Enchantment)(Object)this).description();ci.setReturnValue(java.util.List.of("power","unbreaking","mending").stream().anyMatch(id->description.equals(net.minecraft.network.chat.Component.translatable("enchantment.minecraft."+id))));return;}
'''
 s=s.replace(anchor,code+anchor)
write(p,s)
