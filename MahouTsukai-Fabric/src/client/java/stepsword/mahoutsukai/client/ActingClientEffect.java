package stepsword.mahoutsukai.client;
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
