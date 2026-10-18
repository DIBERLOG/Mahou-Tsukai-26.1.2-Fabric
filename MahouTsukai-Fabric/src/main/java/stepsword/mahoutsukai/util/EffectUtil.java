package stepsword.mahoutsukai.util;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
/** Original helpers required by the connected systems. */
public final class EffectUtil {
 public static int getEffectDuration(net.minecraft.world.entity.LivingEntity entity,net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect){var instance=entity.getEffect(effect);return instance==null?0:instance.getDuration();}

 public static float toRad(float degrees) { return degrees * (float)(Math.PI / 180.0); }
   public static void buff(LivingEntity player, Holder<MobEffect> potion, boolean additiveBuff, int ticks) {
      buff(player, potion, additiveBuff, ticks, true, 0);
   }

   public static void buff(LivingEntity player, Holder<MobEffect> potion, boolean additiveBuff, int ticks, boolean showparticles, int level) {
      int time = ticks;
      if (additiveBuff && player.hasEffect(potion)) {
         MobEffectInstance e = player.getEffect(potion);
         if (e != null && e.getDuration() > 0) {
            time += e.getDuration();
         }
      }

      player.addEffect(new MobEffectInstance(potion, time, level, false, showparticles));
   }

   public static void buff(LivingEntity player, Holder<MobEffect> potion, boolean additiveBuff, int ticks, boolean showparticles) {
      buff(player, potion, additiveBuff, ticks, showparticles, 0);
   }

 public static final net.minecraft.world.level.block.state.BlockState AIR = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
 public static void debuff(LivingEntity entity, Holder<MobEffect> effect) { if (hasBuff(entity,effect)) entity.removeEffect(effect); }
 public static boolean inBlockBlacklist(net.minecraft.world.level.block.Block block, List<? extends String> values, Level level) {
  if (block == null) return false;
  var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
  return inGenericBlacklist(id.toString(),values) || inGenericBlacklist(id.getNamespace(),values);
 }
 public static boolean inItemBlacklist(net.minecraft.world.item.Item item, List<? extends String> values, Level level) {
  if (item == null) return false;
  var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
  return inGenericBlacklist(id.toString(),values) || inGenericBlacklist(id.getNamespace(),values);
 }
 public static boolean inEntityBlacklist(net.minecraft.world.entity.Entity entity,List<? extends String> values) {
  if(entity==null) return false;
  var id=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
  return inGenericBlacklist(id.toString(),values) || inGenericBlacklist(id.getNamespace(),values);
 }
 public static boolean inEnchantBlacklist(Holder<net.minecraft.world.item.enchantment.Enchantment> enchant,List<? extends String> values) {
  return enchant.unwrapKey().map(key->values.contains(key.identifier().toString()) || values.contains(key.identifier().getNamespace())).orElse(false);
 }
 public static void tryChangeBlockState(boolean killTE,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState replacement,Level world,net.minecraft.world.entity.player.Player owner) {
  if(!(world instanceof net.minecraft.server.level.ServerLevel level) || unchangableBlock(world.getBlockState(pos).getBlock()) || !killTE && world.getBlockEntity(pos)!=null) return;
  var player=owner == null ? stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNull(level)
    : stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNotExists(level,"faker",owner.getUUID());
  var state=world.getBlockState(pos);
  if(net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,player,pos,state,world.getBlockEntity(pos))) world.setBlockAndUpdate(pos,replacement);
 }
 public static void magicAttack(LivingEntity entity,float damage,LivingEntity caster) { genericAttack(entity,damage,entity.damageSources().magic(),caster); }
 public static void genericAttack(LivingEntity entity,float damage,net.minecraft.world.damagesource.DamageSource source,LivingEntity caster) {
  if(caster instanceof net.minecraft.world.entity.player.Player player) entity.setLastHurtByPlayer(player,100);
  if(entity.level() instanceof net.minecraft.server.level.ServerLevel world) entity.hurtServer(world,source,damage);
 }
   public static double pointToLineDistance(net.minecraft.world.phys.Vec3 point, net.minecraft.world.phys.Vec3 line1, net.minecraft.world.phys.Vec3 line2) {
      double d = line2.subtract(line1).cross(line1.subtract(point)).length();
      return d / line2.distanceTo(line1);
   }
   public static boolean inRange(LivingEntity victim, LivingEntity binder, net.minecraft.world.phys.Vec3 vicPos, int range, boolean facingRequired) {
      net.minecraft.world.phys.Vec3 look = binder.getLookAngle();
      net.minecraft.world.phys.Vec3 eyes = binder.getEyePosition(1.0F);
      if (!(pointToLineDistance(vicPos, eyes, look.add(eyes)) < range)) {
         return false;
      }

      if (!facingRequired) {
         return isLookingAtMe(victim, binder);
      }

      float facing = victim.getYRot() - binder.getYRot();
      facing %= 360.0F;
      if (facing < 0.0F) {
         facing += 360.0F;
      }

      return facing > 90.0F && facing < 270.0F;
   }
   public static boolean isLookingAtMe(LivingEntity me, LivingEntity player) {
      return isLookingAtMe(me, player, 0);
   }
   public static boolean isLookingAtMe(net.minecraft.world.entity.Entity me, LivingEntity player, int limit) {
      net.minecraft.world.phys.Vec3 mv = me.position();
      float angleToMe = (float)Math.atan2(mv.z - player.getZ(), mv.x - player.getX()) * 57.2958F;
      float yaw = player.getYRot();
      yaw %= 360.0F;
      angleToMe %= 360.0F;
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (angleToMe < 0.0F) {
         angleToMe += 360.0F;
      }

      float boundUpper = (yaw + 90.0F + 90.0F - limit) % 360.0F;
      float boundLower = (yaw + limit) % 360.0F;
      if (boundLower < 0.0F) {
         boundLower += 360.0F;
      }

      if (boundUpper < 0.0F) {
         boundUpper += 360.0F;
      }

      return angleToMe < boundUpper && angleToMe > boundLower ? true : boundUpper < boundLower && (angleToMe < boundUpper || angleToMe > boundLower);
   }
 public static net.minecraft.world.phys.Vec3 fromBlockPos(net.minecraft.core.BlockPos pos) { return new net.minecraft.world.phys.Vec3(pos.getX(),pos.getY(),pos.getZ()); }
 public static float toDegrees(float radians) {return radians*180.0F/(float)Math.PI;}
 public static void buff(LivingEntity entity,Holder<MobEffect> effect,boolean additive,int ticks,int amplifier) {buff(entity,effect,additive,ticks,true,amplifier);}
 public static boolean hasBuff(LivingEntity entity, Holder<MobEffect> effect) { return entity != null && (entity.hasEffect(effect) || effect==stepsword.mahoutsukai.potion.ModEffects.REPLICA_SHOCKWAVE_COOLDOWN && entity.hasEffect(stepsword.mahoutsukai.potion.ModEffects.LEGACY_REPLICA_SHOCKWAVE_COOLDOWN)); }
 public static boolean unchangableBlock(net.minecraft.world.level.block.Block block,java.util.List<net.minecraft.world.level.block.Block> extra) {return unchangableBlock(block) || extra.contains(block);}
 public static boolean unchangableBlock(net.minecraft.world.level.block.Block block) {
  return block == net.minecraft.world.level.block.Blocks.BEDROCK || block == net.minecraft.world.level.block.Blocks.END_GATEWAY
   || block == net.minecraft.world.level.block.Blocks.END_PORTAL || block == net.minecraft.world.level.block.Blocks.END_PORTAL_FRAME;
 }
 public static net.minecraft.server.level.ServerLevel getNewDimensionByName(net.minecraft.server.level.ServerLevel world, Identifier id) {
  return world.getServer().getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id));
 }
 public static Identifier getDimension(Level world) { return world.dimension().identifier(); }
 public static boolean compareDimensions(Identifier a, Identifier b) { return a.equals(b); }
 public static boolean inGenericBlacklist(String value, List<? extends String> values) { return value != null && values != null && values.contains(value); }
 @FunctionalInterface public interface SpellBlockOperation {boolean apply(net.minecraft.world.level.block.state.BlockState state,net.minecraft.world.entity.Entity entity,boolean drop,net.minecraft.core.BlockPos pos);}
 /** Native block-break permission and shield checks replace the original NeoForge explosion events. */
 public static void applySpellBlockChanges(java.util.List<net.minecraft.core.BlockPos> affected,net.minecraft.world.entity.Entity target,net.minecraft.world.entity.player.Player caster,boolean drop,java.util.ArrayList<net.minecraft.world.entity.Entity> entities,net.minecraft.world.phys.Vec3 location,int limit,SpellBlockOperation operation){
  if(!(target.level() instanceof net.minecraft.server.level.ServerLevel world))return;
  entities.removeIf(e->stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(world,e.position()) || stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(world,e.position().add(0,e.getBbHeight(),0)));
  var owner=caster!=null?caster:stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNull(world);int changed=0;
  for(var original:affected){if(limit>=0 && changed>=limit)break;var pos=original.immutable();var state=world.getBlockState(pos);
   if(unchangableBlock(state.getBlock()) || stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(world,net.minecraft.world.phys.Vec3.atCenterOf(pos)))continue;
   if(!net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,owner,pos,state,world.getBlockEntity(pos)))continue;
   if(operation.apply(state,target,drop,pos))changed++;
  }
 }

 public static boolean noMoreSoaring(LivingEntity player){return player.onGround() || player.isFallFlying() || player.isUnderWater();}

 public static boolean tryChangeBlockState(boolean killTE,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState state,Level world,net.minecraft.world.entity.player.Player owner,java.util.UUID ownerId){
  if(!(world instanceof net.minecraft.server.level.ServerLevel level) || unchangableBlock(world.getBlockState(pos).getBlock()) || !killTE && world.getBlockEntity(pos)!=null || stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(level,pos.getCenter()))return false;
  var actor=stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNotExists(level,"faker",owner!=null?owner.getUUID():ownerId);
  if(!world.mayInteract(actor,pos) || !net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,actor,pos,world.getBlockState(pos),world.getBlockEntity(pos)))return false;
  return world.setBlockAndUpdate(pos,state);
 }

 public static int getBuffLevel(LivingEntity living,Holder<MobEffect> effect){var instance=living.getEffect(effect);return instance==null?0:instance.getAmplifier();}

 public static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> toHolder(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,Level level){return level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(key);}
 public static int getEnchantmentLevel(net.minecraft.world.item.ItemStack stack,net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment){return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantment,stack);}
 public static boolean hasEnchantment(net.minecraft.world.item.ItemStack stack,net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,Level level){return getEnchantmentLevel(stack,toHolder(key,level))>0;}

}
