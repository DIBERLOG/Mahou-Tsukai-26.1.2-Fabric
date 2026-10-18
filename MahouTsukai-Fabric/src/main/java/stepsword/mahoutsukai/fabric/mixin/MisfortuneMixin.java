package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import stepsword.mahoutsukai.potion.*;
@Mixin(LivingEntity.class)
public abstract class MisfortuneMixin {
 @Inject(method="removeEffect",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$curse(Holder<MobEffect> effect,CallbackInfoReturnable<Boolean> ci){var living=(LivingEntity)(Object)this;if(effect.equals(ModEffects.MISFORTUNE) && !living.getMainHandItem().isEmpty())ci.setReturnValue(false);}
 @WrapMethod(method="removeAllEffects")
 private boolean mahoutsukai$all(Operation<Boolean> original){var living=(LivingEntity)(Object)this;var curse=living.getEffect(ModEffects.MISFORTUNE);boolean preserve=curse!=null && !living.getMainHandItem().isEmpty();boolean result=original.call();if(preserve)living.addEffect(new net.minecraft.world.effect.MobEffectInstance(curse));return result;}
 @WrapMethod(method="dropAllDeathLoot")
 private void mahoutsukai$loot(ServerLevel world,DamageSource source,Operation<Void> original){
  if(!(source.getEntity() instanceof LivingEntity killer) || !killer.hasEffect(ModEffects.MISFORTUNE)){original.call(world,source);return;}
  int loss=stepsword.mahoutsukai.util.EffectUtil.getBuffLevel(killer,ModEffects.MISFORTUNE)/Math.max(1,stepsword.mahoutsukai.config.MTConfig.KODOKU_LOOT_DIVISOR);
  if(loss<=0){original.call(world,source);return;}
  var before=new java.util.HashSet<java.util.UUID>();for(var e:world.getAllEntities())if(e instanceof net.minecraft.world.entity.item.ItemEntity)before.add(e.getUUID());
  original.call(world,source);var drops=new java.util.ArrayList<net.minecraft.world.entity.item.ItemEntity>();for(var e:world.getAllEntities())if(e instanceof net.minecraft.world.entity.item.ItemEntity item && !before.contains(e.getUUID()))drops.add(item);
  while(loss-->0 && !drops.isEmpty())drops.remove(killer.getRandom().nextInt(drops.size())).discard();
 }
}
