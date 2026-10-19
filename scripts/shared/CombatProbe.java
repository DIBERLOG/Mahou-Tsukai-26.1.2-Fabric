package local.mahouparity;

import com.google.gson.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;

/** Identical controlled combat inputs, executed through native damage/attack on both loaders. */
public final class CombatProbe {
 private static final java.lang.reflect.Field ATTACK,SWAP,USE;
 static {try{ATTACK=LivingEntity.class.getDeclaredField("attackStrengthTicker");SWAP=LivingEntity.class.getDeclaredField("itemSwapTicker");USE=LivingEntity.class.getDeclaredField("useItemRemaining");for(var f:List.of(ATTACK,SWAP,USE))f.setAccessible(true);}catch(Exception e){throw new ExceptionInInitializerError(e);}}
 private static ServerPlayer player(ServerLevel world,String name,double z){
  var profile=new com.mojang.authlib.GameProfile(UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8)),name);
  var p=new ServerPlayer(world.getServer(),world,profile,ClientInformation.createDefault());
  p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(world.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),p,net.minecraft.server.network.CommonListenerCookie.createInitial(profile,false)){
   @Override public void send(net.minecraft.network.protocol.Packet<?> packet){}
  };
  p.connection.handleAcceptPlayerLoad(new net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket());
  p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);p.setPos(40.5,280,z);p.setOnGround(true);
  world.addNewPlayer(p);return p;
 }
 private static void remove(ServerLevel world,LivingEntity e){if(e instanceof ServerPlayer p)world.removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);else e.discard();}
 private static void effect(LivingEntity target,String id,int amplifier,Entity caster){if(id!=null)target.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(BuiltInRegistries.MOB_EFFECT.getValue(Identifier.parse(id))),200,amplifier),caster);}
 private static void mana(ServerPlayer p){var m=Utils.getPlayerMahou(p);m.setMaxMana(2000);m.setStoredMana(1000);m.setDamageExchangeUses(0);m.setDeathCollectionUsesLeft(0);m.setImmunityDamageType("");m.setGeas(new HashSet<>());}
 private static void armor(LivingEntity e){e.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.NETHERITE_HELMET));e.setItemSlot(EquipmentSlot.CHEST,new ItemStack(Items.NETHERITE_CHESTPLATE));e.setItemSlot(EquipmentSlot.LEGS,new ItemStack(Items.NETHERITE_LEGGINGS));e.setItemSlot(EquipmentSlot.FEET,new ItemStack(Items.NETHERITE_BOOTS));}
 private static DamageSource source(ServerPlayer a,LivingEntity target,String name){return switch(name){
  case "player"->a.damageSources().playerAttack(a);case "magic"->target.damageSources().magic();case "generic"->target.damageSources().generic();case "fall"->target.damageSources().fall();case "void"->target.damageSources().fellOutOfWorld();case "kill"->target.damageSources().genericKill();case "fire"->target.damageSources().onFire();case "lightning"->target.damageSources().lightningBolt();default->throw new IllegalArgumentException(name);
 };}
 private static JsonObject snapshot(LivingEntity target,ServerPlayer attacker,boolean accepted){
  var row=new JsonObject();row.addProperty("accepted",accepted);row.addProperty("health",target.getHealth());row.addProperty("alive",target.isAlive());row.addProperty("absorption",target.getAbsorptionAmount());
  var effects=new JsonObject();target.getActiveEffects().stream().sorted(Comparator.comparing(e->BuiltInRegistries.MOB_EFFECT.getKey(e.getEffect().value()).toString())).forEach(e->{var x=new JsonArray();x.add(e.getAmplifier());x.add(e.getDuration());effects.add(BuiltInRegistries.MOB_EFFECT.getKey(e.getEffect().value()).toString(),x);});row.add("effects",effects);
  var wear=new JsonObject();for(var slot:EquipmentSlot.VALUES){var s=target.getItemBySlot(slot);if(!s.isEmpty())wear.addProperty(slot.toString(),s.getDamageValue());}row.add("target_wear",wear);
  row.addProperty("totem_left",target.getOffhandItem().is(Items.TOTEM_OF_UNDYING));
  if(target instanceof ServerPlayer p){var m=Utils.getPlayerMahou(p);row.addProperty("mana",m.getStoredMana());row.addProperty("exchange",m.getDamageExchangeUses());row.addProperty("souls",m.getDeathCollectionUsesLeft());row.addProperty("immunity",m.getImmunityDamageType());}
  else row.addProperty("immunity",Utils.getLivingMahou(target).getImmunityDamageType());
  row.addProperty("attacker_mana",Utils.getPlayerMahou(attacker).getStoredMana());row.addProperty("weapon_wear",attacker.getMainHandItem().getDamageValue());row.addProperty("weapon_strength",StrengtheningSpellEffect.getStrengthened(attacker.getMainHandItem()));
  return row;
 }
 private static JsonObject trial(ServerLevel world,boolean pvp,String weapon,int flags,String channel,float amount,String first,String second,int facing,boolean strengthened,boolean melee)throws Exception{
  var a=player(world,"CombatAttacker",36.5);LivingEntity t=pvp?player(world,"CombatTarget",40.5):EntityType.COW.create(world,EntitySpawnReason.COMMAND);
  if(t==null)throw new AssertionError("Missing combat target");
  if(!pvp){t.setNoGravity(true);t.setPos(40.5,280,40.5);t.setOnGround(true);world.addFreshEntity(t);}
  try{
   var stack=weapon.equals("empty")?ItemStack.EMPTY:new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(weapon)));a.setItemInHand(InteractionHand.MAIN_HAND,stack);
   if(weapon.equals("mahoutsukai:theripper"))a.setItemInHand(InteractionHand.OFF_HAND,stack.copy());
   if(weapon.equals("mahoutsukai:caliburn")||weapon.equals("mahoutsukai:morgan")||weapon.equals("mahoutsukai:clarent")){var stats=Utils.getSwordMahou(stack);stats.setAttackDamage(8);Utils.setSwordMahou(stack,stats);}
   if(strengthened||(flags&128)!=0)StrengtheningSpellEffect.setStrengthened(stack,world);
   if((flags&32)!=0)armor(t);
   if((flags&8)!=0)t.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(Items.TOTEM_OF_UNDYING));
   if((flags&64)!=0)t.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.SHIELD));
   // Tick once before adding statuses: native equipment detection installs real armor/weapon attributes.
   a.tick();t.tick();a.setPos(40.5,280,36.5);t.setPos(40.5,280,40.5);a.setOnGround(true);t.setOnGround(true);t.setYRot(facing);t.setYHeadRot(facing);t.yBodyRot=facing;t.setXRot(0);a.setYRot(0);a.setYHeadRot(0);a.yBodyRot=0;a.setXRot(0);
   mana(a);if(t instanceof ServerPlayer p)mana(p);t.setHealth(t.getMaxHealth());t.invulnerableTime=0;
   if((flags&1)!=0){effect(t,"mahoutsukai:immunity_exchange",0,a);if(t instanceof ServerPlayer p)Utils.getPlayerMahou(p).setImmunityDamageType("generic");else Utils.getLivingMahou(t).setImmunityDamageType("generic");}
   if(t instanceof ServerPlayer p){if((flags&2)!=0)Utils.getPlayerMahou(p).setDamageExchangeUses(3);if((flags&4)!=0)Utils.getPlayerMahou(p).setDeathCollectionUsesLeft(2);}
   if((flags&16)!=0)t.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,200,3));
   if((flags&64)!=0){t.startUsingItem(InteractionHand.MAIN_HAND);USE.setInt(t,t.getUseItem().getUseDuration(t)-10);if(!t.isBlocking())throw new AssertionError("Shield fixture is not blocking");}
   effect(t,first,0,a);effect(t,second,1,a);ATTACK.setInt(a,100);SWAP.setInt(a,100);
   // Entity death equipment drops must use identical random inputs on both loaders.
   a.getRandom().setSeed(260026L);t.getRandom().setSeed(260026L);
   boolean accepted;
   if(melee){float health=t.getHealth();a.attack(t);accepted=t.getHealth()!=health||t.hurtTime>0;}
   else accepted=t.hurtServer(world,source(a,t,channel),amount);
   return snapshot(t,a,accepted);
  }finally{remove(world,t);remove(world,a);}
 }
 public static JsonObject run(MinecraftServer server)throws Exception{
  var world=server.overworld();var all=new JsonObject();int count=0;
  var effects=BuiltInRegistries.MOB_EFFECT.keySet().stream().filter(id->id.getNamespace().equals("mahoutsukai")&&!id.getPath().equals("replica_shockwave_cooldown")).sorted().map(Object::toString).toList();
  if(effects.size()!=37)throw new AssertionError("Expected all 37 original effects");
  var inputs=new ArrayList<String>();inputs.add(null);inputs.addAll(effects);
  var pairs=new JsonObject();
  for(boolean pvp:new boolean[]{false,true})for(String channel:List.of("generic","magic"))for(String a:inputs)for(String b:inputs){
   String key=(pvp?"player":"cow")+"/"+channel+"/"+a+"/"+b;
   pairs.add(key,trial(world,pvp,"minecraft:diamond_sword",0,channel,4,a,b,180,false,false));count++;
  }
  all.add("ordered_effect_pairs",pairs);System.out.println("COMBAT_PROGRESS pairs="+pairs.size());
  var defenses=new JsonObject();
  for(int mask=0;mask<256;mask++)for(String channel:List.of("generic","player","magic","fall","void","kill","fire","lightning"))for(int amount:new int[]{4,40}){
   String key=mask+"/"+channel+"/"+amount;defenses.add(key,trial(world,true,"minecraft:diamond_sword",mask,channel,amount,null,null,180,false,false));count++;
  }
  all.add("defense_power_set",defenses);System.out.println("COMBAT_PROGRESS defenses="+defenses.size());
  var weapons=BuiltInRegistries.ITEM.keySet().stream().filter(id->id.getNamespace().equals("mahoutsukai")&&!id.getPath().startsWith("scroll_mana_")&&!Set.of("powdered_sculk","scroll_doom","rhongomyniad").contains(id.getPath())).sorted().map(Object::toString).toList();
  var attacks=new JsonObject();
  for(String weapon:weapons)for(boolean pvp:new boolean[]{false,true})for(int flags:new int[]{0,1,2,12,32,64,48,255})for(int facing:new int[]{0,180})for(boolean boost:new boolean[]{false,true}){
   String key=weapon+"/"+pvp+"/"+flags+"/"+facing+"/"+boost;attacks.add(key,trial(world,pvp,weapon,flags,"player",4,null,null,facing,boost,true));count++;
  }
  all.add("native_item_attacks",attacks);all.addProperty("cases",count);all.addProperty("original_effects",effects.size());all.addProperty("original_item_entries",weapons.size());
  System.out.println("COMBAT_PROGRESS attacks="+attacks.size()+" total="+count);return all;
 }
}
