package stepsword.mahoutsukai;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.*;
import stepsword.mahoutsukai.util.*;
import stepsword.mahoutsukai.networking.*;
import stepsword.mahoutsukai.entity.mahoujin.GandrEntity;
import stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll;
import stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper;

/** Test-only live native server coverage. No test classes enter the production JAR. */
public final class SixFamiliesIntegration {
 private static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 private static ItemStack scroll(String id){return new ItemStack(ModItems.REGISTERED.get("scroll_"+id));}
 private static void clear(ServerPlayer player){player.stopUsingItem();player.getInventory().clearContent();player.removeAllEffects();Utils.getPlayerMahou(player).setGeas(new HashSet<>());Utils.getPlayerMahou(player).setDamageExchangeUses(0);Utils.getPlayerMahou(player).setMaxMana(20000);Utils.getPlayerMahou(player).setStoredMana(20000);}
 private static void cast(ServerPlayer player,ItemStack stack){((SpellScroll)stack.getItem()).releaseUsing(stack,player.level(),player,71900);}
 private static <T extends Mob>T mob(ServerPlayer player,EntityType<T> type,double x,double y,double z){var mob=type.create(player.level(),EntitySpawnReason.COMMAND);mob.snapTo(x,y,z,0,0);mob.setNoAi(true);player.level().addFreshEntity(mob);return mob;}
 private static void aim(ServerPlayer player,double x,double y,double z,float yaw){player.snapTo(x,y,z,yaw,0);player.setYHeadRot(yaw);}
 private static void rituals(ServerPlayer player){
  String[][] rows={{"insight","eye","eye","ender"},{"prediction","eye","eye","quartz"},{"reversion_eyes","eye","eye","emerald"},{"presence_concealment","ender","iron","diamond"},{"gandr","gold","emerald","diamond"},{"cup_of_heaven","gold","gold","quartz"}};
  for(int i=0;i<rows.length;i++){
   var row=rows[i];var pos=new BlockPos(450+i*2,285,450);
   worldCleanup(player,new AABB(pos).inflate(1));
   player.level().setBlockAndUpdate(pos,ModBlocks.mahoujin.get().defaultBlockState());
   ((stepsword.mahoutsukai.tile.MahoujinTileEntity)player.level().getBlockEntity(pos)).setCloth(true);
   for(int c=1;c<4;c++){
    var powder=new ItemStack(ModItems.REGISTERED.get("powdered_"+row[c]));player.setItemInHand(InteractionHand.MAIN_HAND,powder);
    player.level().getBlockState(pos).useItemOn(powder,player.level(),player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));require(powder.isEmpty(),row[0]+" native catalyst consumption");
   }
   var block=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(player.level().getBlockState(pos).getBlock());
   require(block.getPath().equals("mahoujin_"+row[0]),row[0]+" actual original catalyst recipe");
   player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);player.level().getBlockState(pos).useWithoutItem(player.level(),player,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
   var item=player.level().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(1),e->e.getItem().is(ModItems.REGISTERED.get("scroll_"+row[0]))).stream().findFirst().orElseThrow();
   require(Utils.getScrollMahou(item.getItem()).getCasterUUID().orElseThrow().equals(player.getUUID()),row[0]+" retrieved native scroll bound to ritual caster");item.discard();require(player.level().getBlockState(pos).isAir(),"Clothed ritual is consumed on retrieval");
  }
 }
 private static void worldCleanup(ServerPlayer player,AABB area){for(var entity:player.level().getEntities((Entity)null,area))if(!(entity instanceof net.minecraft.world.entity.player.Player))entity.discard();}
 private static void insight(ServerPlayer player){
  clear(player);aim(player,464.5,280,464.5,0);var stack=scroll("insight");cast(player,stack);
  require(stack.isEmpty() && player.hasEffect(ModEffects.INSIGHT) && player.getEffect(ModEffects.INSIGHT).getDuration()==1200 && Utils.getPlayerMahou(player).getStoredMana()==19840,"Insight original 320 cost, 1200 ticks, consumption");
  var target=mob(player,EntityType.ZOMBIE,464.5,280,469.5);target.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));target.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.DIAMOND_HELMET));target.addEffect(new MobEffectInstance(MobEffects.SPEED,300,1));
  var packet=InsightEyesPotion.inspect(player);require(packet!=null && packet.hasTarget && !packet.block && packet.id==target.getId(),"Insight sees actual aimed mob");
  var inventory=InsightEyesPotion.readInventory(packet.inventory,player.level());require(inventory.size()==2 && InsightEyesPotion.readItem(packet.mainhand,player.level()).is(Items.DIAMOND_SWORD) && packet.mainhandindex>=0,"Native mob equipment and main-hand index visible");
  require(InsightEyesPotion.readEffects(packet.effects,player.level()).stream().anyMatch(e->e.getEffect()==MobEffects.SPEED && e.getAmplifier()==1),"Native active effects preserved");
  var wire=io.netty.buffer.Unpooled.buffer();try{InsightPacket.STREAM_CODEC.encode(wire,packet);var decoded=InsightPacket.STREAM_CODEC.decode(wire);require(decoded.id==target.getId() && InsightEyesPotion.readInventory(decoded.inventory,player.level()).size()==2,"Original Insight packet round trip");}finally{wire.release();}
  var obstacle=new BlockPos(464,281,467);player.level().setBlockAndUpdate(obstacle,Blocks.STONE.defaultBlockState());var lost=InsightEyesPotion.inspect(player);require(lost!=null && !lost.hasTarget,"Insight does not expose mob through closer wall");player.level().removeBlock(obstacle,false);target.discard();
  var chestPos=new BlockPos(464,281,469);player.level().setBlockAndUpdate(chestPos,Blocks.CHEST.defaultBlockState());var chest=(net.minecraft.world.level.block.entity.ChestBlockEntity)player.level().getBlockEntity(chestPos);chest.setItem(0,new ItemStack(Items.EMERALD,12));
  packet=InsightEyesPotion.inspect(player);require(packet!=null && packet.hasTarget && packet.block && InsightEyesPotion.readInventory(packet.inventory,player.level()).getFirst().getCount()==12,"Native Fabric north-side container snapshot");require(chest.getItem(0).getCount()==12,"Insight never extracts or changes inventory");player.level().removeBlock(chestPos,false);player.removeEffect(ModEffects.INSIGHT);require(!InsightEyesPotion.inspect(player).hasTarget && InsightEyesPotion.inspect(player)==null,"Insight sends one clear payload on effect loss");
  System.out.println("PASS: INSIGHT_INTEGRATION: original cost/time, native mob inventory/effects/main hand, container snapshot, wall occlusion, read-only access, original packet and target loss");
 }
 private static void prediction(ServerPlayer player){
  clear(player);aim(player,464.5,280,480.5,0);var stack=scroll("prediction");cast(player,stack);require(stack.isEmpty() && player.hasEffect(ModEffects.CLAIRVOYANCE) && Utils.getPlayerMahou(player).getStoredMana()==19890,"Clairvoyance original 220 cost and buff");
  var target=mob(player,EntityType.ZOMBIE,464.5,280,485.5);var nodes=List.of(new net.minecraft.world.level.pathfinder.Node(464,280,485),new net.minecraft.world.level.pathfinder.Node(465,280,485),new net.minecraft.world.level.pathfinder.Node(466,280,485));var path=new net.minecraft.world.level.pathfinder.Path(nodes,new BlockPos(466,280,485),true);target.getNavigation().moveTo(path,1);
  var packet=ClairvoyanceEyesPotion.predict(player);require(packet.entities.contains(target.getId()) && packet.paths.get(packet.entities.indexOf(target.getId())).getNodeCount()==3,"Prediction captures actual native navigation path");
  var wire=io.netty.buffer.Unpooled.buffer();try{EntityPathPacket.STREAM_CODEC.encode(wire,packet);var decoded=EntityPathPacket.STREAM_CODEC.decode(wire);require(decoded.entities.equals(packet.entities) && decoded.paths.getFirst().getEndNode().x==466,"Original path coordinates wire round trip");}finally{wire.release();}
  target.getNavigation().stop();require(!ClairvoyanceEyesPotion.predict(player).entities.contains(target.getId()),"Stopped paths removed");target.discard();player.removeEffect(ModEffects.CLAIRVOYANCE);
  System.out.println("PASS: CLAIRVOYANCE_INTEGRATION: original cost/time, real native navigation paths, stopped paths and original coordinate packet");
 }
 private static void reversion(ServerPlayer player){
  clear(player);aim(player,480.5,280,464.5,0);var stack=scroll("reversion_eyes");cast(player,stack);require(stack.isEmpty() && player.hasEffect(ModEffects.REVERSION_EYES) && Utils.getPlayerMahou(player).getStoredMana()==19795,"Reversion original cost and scroll");
  var target=mob(player,EntityType.BLAZE,480.5,280,469.5);target.addEffect(new MobEffectInstance(MobEffects.SPEED,500));Utils.getLivingMahou(target).setImmunityDamageType("minecraft:magic");require(target.fireImmune(),"Baseline blaze fire immunity");ReversionEyesPotion.addRevert(player);require(target.hasEffect(ModEffects.REVERTED) && !target.fireImmune(),"Gaze disables native fire immunity");target.tick();require(!target.hasEffect(MobEffects.SPEED) && Utils.getLivingMahou(target).getImmunityDamageType()==null,"Native reverted living tick strips effects and mod immunities");
  target.invulnerableTime=0;float health=target.getHealth();require(target.hurtServer(player.level(),target.damageSources().inFire(),2) && target.getHealth()<health,"Native damage can burn reverted blaze");
  player.setYRot(180);target.tick();require(!target.hasEffect(ModEffects.REVERTED) && target.fireImmune(),"Looking away ends reversion and restores innate immunity");target.discard();
  var enderman=mob(player,EntityType.ENDERMAN,482,280,469);var arrow=new net.minecraft.world.entity.projectile.arrow.Arrow(EntityType.ARROW,player.level());health=enderman.getHealth();enderman.hurtServer(player.level(),player.damageSources().arrow(arrow,player),6);require(enderman.getHealth()==health,"Baseline Enderman ignores arrows");ReversionEyesPotion.setEntityReverted(enderman);enderman.invulnerableTime=0;require(enderman.hurtServer(player.level(),player.damageSources().arrow(arrow,player),6) && enderman.getHealth()<health,"Native Enderman projectile immunity disabled by mixin");enderman.discard();
  var shulker=mob(player,EntityType.SHULKER,484,280,469);health=shulker.getHealth();require(!shulker.hurtServer(player.level(),player.damageSources().arrow(arrow,player),10),"Closed native shulker baseline arrow immunity");ReversionEyesPotion.setEntityReverted(shulker);shulker.invulnerableTime=0;require(shulker.hurtServer(player.level(),player.damageSources().arrow(arrow,player),10) && shulker.getHealth()<health,"Closed native shulker loses arrow immunity");shulker.discard();
  var wither=mob(player,EntityType.WITHER,486,280,469);wither.setHealth(100);health=wither.getHealth();require(!wither.hurtServer(player.level(),player.damageSources().arrow(arrow,player),10),"Powered native wither baseline arrow immunity");ReversionEyesPotion.setEntityReverted(wither);wither.invulnerableTime=0;require(!wither.hasEffect(ModEffects.REVERTED) && !wither.hurtServer(player.level(),player.damageSources().arrow(arrow,player),10),"Original Wither rejects all added effects, so gaze cannot remove its projectile immunity");wither.discard();
  var wolf=mob(player,EntityType.WOLF,480.5,280,469.5);wolf.tame(player);player.setYRot(0);ReversionEyesPotion.addRevert(player);require(!wolf.hasEffect(ModEffects.REVERTED),"Original contract rules protect owner's wolf from reversion");wolf.discard();player.removeEffect(ModEffects.REVERSION_EYES);
  var wire=io.netty.buffer.Unpooled.buffer();try{RevertedPacket.STREAM_CODEC.encode(wire,new RevertedPacket(123,true));require(wire.readableBytes()==5 && RevertedPacket.STREAM_CODEC.decode(wire).reverted,"Original reversion flag packet");}finally{wire.release();}
  System.out.println("PASS: REVERSION_INTEGRATION: native gaze/effect removal, fire/Enderman/shulker immunities and original Wither effect rejection, look-away restoration, contract exclusion and flag codec");
 }
 private static void cup(ServerPlayer player){
  clear(player);aim(player,465.5,280,465.5,0);int batch=MTConfig.HEAVENS_CUP_BATCH_SIZE;MTConfig.HEAVENS_CUP_BATCH_SIZE=1;
  var positions=List.of(new BlockPos(464,279,464),new BlockPos(474,279,464),new BlockPos(474,279,474),new BlockPos(464,279,474),new BlockPos(469,279,469));
  try{
   for(var pos:positions){player.level().setBlockAndUpdate(pos,ModBlocks.cupOfHeavenMahoujin.get().defaultBlockState());((stepsword.mahoutsukai.tile.MahoujinTileEntity)player.level().getBlockEntity(pos)).setCasterUUID(player.getUUID());}
   var inside=mob(player,EntityType.COW,469.5,280,469.5);var outside=mob(player,EntityType.COW,476.5,280,469.5);var stack=scroll("cup_of_heaven");int before=Utils.getPlayerMahou(player).getStoredMana();cast(player,stack);require(!MahouJobs.running.isEmpty() && stack.getCount()==1,"Cup network defers traversal at configured batch bound");for(int i=0;i<20 && !MahouJobs.running.isEmpty();i++)MahouJobs.runJobs();require(MahouJobs.running.isEmpty() && stack.isEmpty(),"Original batched network completes and consumes scroll once");
   require(inside.hasEffect(MobEffects.POISON) && inside.getEffect(MobEffects.POISON).getAmplifier()==1 && inside.getEffect(MobEffects.POISON).getDuration()==1200 && !outside.hasEffect(MobEffects.POISON),"Four hull nodes, one internal node: original prime factor poison II only inside polygon");require(Utils.getPlayerMahou(player).getStoredMana()==before-40,"Original prime factors, area and base factor yield eighty mana");
   player.getInventory().clearContent();Utils.getPlayerMahou(player).setStoredMana(0);var insufficient=scroll("cup_of_heaven");cast(player,insufficient);for(int i=0;i<20 && !MahouJobs.running.isEmpty();i++)MahouJobs.runJobs();require(insufficient.getCount()==1,"Insufficient network mana preserves scroll");inside.discard();outside.discard();
  }finally{MTConfig.HEAVENS_CUP_BATCH_SIZE=batch;MahouJobs.clear();for(var pos:positions)player.level().removeBlock(pos,false);}
  System.out.println("PASS: CUP_OF_HEAVEN_INTEGRATION: native owned nodes, bounded asynchronous traversal, original convex hull/prime-factor effect and area mana cost, polygon exclusion and insufficient-mana preservation");
 }
 private static void ripper(ServerPlayer player){
  clear(player);aim(player,480.5,280,480.5,0);var stack=scroll("presence_concealment");cast(player,stack);int knives=0;for(int i=0;i<player.getInventory().getContainerSize();i++)if(player.getInventory().getItem(i).is(ModItems.theripper.get()))knives+=player.getInventory().getItem(i).getCount();require(stack.isEmpty() && knives==2 && Utils.getPlayerMahou(player).getStoredMana()==19600,"Original 800 mana summons two actual knives");
  player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(ModItems.theripper.get()));player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(ModItems.theripper.get()));for(int i=0;i<30;i++)player.doTick();require(player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)>1 && player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)>.1,"Native held-item damage/speed modifiers");
  var item=(TheRipper)ModItems.theripper.get();int before=Utils.getPlayerMahou(player).getStoredMana();item.use(player.level(),player,InteractionHand.MAIN_HAND);require(player.hasEffect(ModEffects.RIPPER_INVISIBILITY) && player.hasEffect(MobEffects.INVISIBILITY) && player.hasEffect(ModEffects.RIPPER_COOLDOWN) && Utils.getPlayerMahou(player).getStoredMana()==before-100,"Native paired invisibility and smoke cost/cooldown");
  var target=mob(player,EntityType.ZOMBIE,480.5,280,484.5);target.setTarget(player);target.setLastHurtByMob(player);RipperInvisPotion.presenceConcealmentLivingTick(player);require(target.getTarget()==null && target.getLastHurtByMob()==null,"Original concealment clears nearby mob aggression");target.setYRot(0);target.invulnerableTime=20;float damage=TheRipper.ripperOnHit(target,player.damageSources().playerAttack(player),4);require(damage==10 && target.invulnerableTime==0 && !player.hasEffect(ModEffects.RIPPER_INVISIBILITY) && player.hasEffect(ModEffects.RIPPER_INVIS_COOLDOWN),"Backstab adds original six damage, resets hit immunity and breaks concealment");
  player.removeEffect(ModEffects.RIPPER_INVIS_COOLDOWN);require(player.hasEffect(ModEffects.RIPPER_INVISIBILITY),"Cooldown removal automatically restores paired concealment");player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);TheRipper.ripperLivingTick(player);require(!player.hasEffect(ModEffects.RIPPER_INVISIBILITY) && !player.hasEffect(MobEffects.INVISIBILITY),"Removing second knife ends both concealment effects");
  require(!RipperUseEntityPacket.handle(player,target.getId(),InteractionHand.MAIN_HAND),"Forged packet cannot attack without both knives");player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(ModItems.theripper.get()));target.snapTo(480.5,280,490.5,0,0);require(!RipperUseEntityPacket.handle(player,target.getId(),InteractionHand.OFF_HAND),"Server rejects out-of-range knife attack");target.snapTo(480.5,280,483.5,0,0);target.invulnerableTime=0;float health=target.getHealth();for(int i=0;i<30;i++)player.doTick();require(RipperUseEntityPacket.handle(player,target.getId(),InteractionHand.OFF_HAND) && target.getHealth()<health,"Native dual-knife attack actually damages target");target.discard();
  var wire=io.netty.buffer.Unpooled.buffer();try{RipperUseEntityPacket.STREAM_CODEC.encode(wire,new RipperUseEntityPacket(InteractionHand.OFF_HAND,7));require(wire.readableBytes()==8 && RipperUseEntityPacket.STREAM_CODEC.decode(wire).hand==InteractionHand.OFF_HAND,"Original knife packet keeps hand and entity");}finally{wire.release();}
  System.out.println("PASS: PRESENCE_CONCEALMENT_INTEGRATION: native two-knife acquisition/attributes, original mana/smoke/invisibility/cooldowns, aggression clearing, backstab/native combat, hand removal and server packet validation");
 }
 private static void gandr(ServerPlayer player){
  clear(player);aim(player,480.5,280,496.5,0);var stack=scroll("gandr");player.setItemInHand(InteractionHand.MAIN_HAND,stack);stack=player.getMainHandItem();player.startUsingItem(InteractionHand.MAIN_HAND);var item=(GandrSpellScroll)stack.getItem();item.onUseTick(player.level(),player,stack,71999);
  var projectile=player.level().getEntitiesOfClass(GandrEntity.class,player.getBoundingBox().inflate(4),e->player.getUUID().equals(e.getCasterUUID())).stream().findFirst().orElseThrow();require(projectile.getLife()==0 && projectile.getColors()[0]==216/255f,"Native charged entity starts with original player color defaults");
  for(int i=0;i<51;i++)projectile.tick();player.addEffect(new MobEffectInstance(MobEffects.POISON,300));player.addEffect(new MobEffectInstance(MobEffects.SPEED,300));int before=Utils.getPlayerMahou(player).getStoredMana(),cost=item.getManaCostScaling(Utils.getPlayerMahou(player));cast(player,stack);require(stack.isEmpty() && projectile.getFired() && Utils.getPlayerMahou(player).getStoredMana()==before-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(cost) && !player.hasEffect(MobEffects.POISON) && player.hasEffect(MobEffects.SPEED),"Original charge > fifty ticks fires, pays scaled mana and transfers only valid debuffs");player.stopUsingItem();
  var out=TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,player.level().registryAccess());projectile.save(out);var saved=out.buildResult();var copy=new GandrEntity(stepsword.mahoutsukai.entity.ModEntities.GANDR.get(),player.level());copy.load(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,player.level().registryAccess(),saved));require(copy.getFired() && copy.getCasterUUID().equals(player.getUUID()) && copy.getLife()==51 && copy.effects.stream().anyMatch(e->e.getEffect()==MobEffects.POISON),"Native save/reload preserves caster, life, fired flag and carried effects");projectile.discard();copy.setUUID(UUID.randomUUID());require(player.level().addFreshEntity(copy),"Reloaded Gandr enters native world");copy.damage=8;
  var target=mob(player,EntityType.COW,480.5,280,502.5);var wolf=mob(player,EntityType.WOLF,482.5,280,502.5);wolf.tame(player);float wolfHealth=wolf.getHealth();copy.setPos(480.5,281,499.5);copy.setDeltaMovement(0,0,1);float targetHealth=target.getHealth();for(int step=0;step<5 && !copy.isRemoved();step++)copy.tick();require(copy.isRemoved() && target.getHealth()<targetHealth && wolf.getHealth()==wolfHealth,"Native swept collision triggers area damage while owner's wolf is immune");
  require(!player.level().getEntitiesOfClass(AreaEffectCloud.class,target.getBoundingBox().inflate(8)).isEmpty(),"Gandr creates actual native carried-effect cloud");target.discard();wolf.discard();
  var settings=Utils.getSettingsMahou(player);settings.setIntSettings(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.GANDR.ordinal(),List.of(1,2,3,4,5,6));var isolated=new stepsword.mahoutsukai.dataattachments.settingsmahou.SettingsMahou();isolated.copyMahou(settings);isolated.getIntSettings(1).set(0,200);require(settings.getIntSettings(1).getFirst()==1,"Native color settings copy isolation");
  var playerOutput=TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,player.level().registryAccess());player.saveWithoutId(playerOutput);require(playerOutput.buildResult().contains("mahoutsukai:settings_mahou"),"Native player save hook includes original settings fields");
  var wire=io.netty.buffer.Unpooled.buffer();try{SettingsMahouPacket.STREAM_CODEC.encode(wire,new SettingsMahouPacket(settings));require(SettingsMahouPacket.STREAM_CODEC.decode(wire).mahou.getIntSettings(1).equals(List.of(1,2,3,4,5,6)),"Original per-player settings wire format");}finally{wire.release();}
  var noMana=scroll("gandr");player.setItemInHand(InteractionHand.MAIN_HAND,noMana);player.startUsingItem(InteractionHand.MAIN_HAND);item.onUseTick(player.level(),player,player.getMainHandItem(),71999);Utils.getPlayerMahou(player).setStoredMana(0);cast(player,player.getMainHandItem());require(!GandrSpellScroll.gandrs.containsKey(player.getUUID()) && player.getMainHandItem().getCount()==1,"Insufficient mana discards charge and preserves scroll");
  System.out.println("PASS: GANDR_INTEGRATION: original charge/scaled cost/debuff transfer, native swept collision/AOE/cloud and contract exclusion, native entity fired/effect save reload, color persistence/copy/wire and insufficient mana cleanup");
 }
 private static void configWire(ServerPlayer player){
  var before=SpellConfigPacket.current();var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),player.level().registryAccess());
  try{var settings=new SpellConfigPacket(47,2345,4.5,1.25,.075);SpellConfigPacket.STREAM_CODEC.encode(wire,settings);require(wire.readableBytes()==32,"Spell config packet size");var received=SpellConfigPacket.STREAM_CODEC.decode(wire);require(received.equals(settings),"Server spell config wire preserves values");received.apply();require(MTConfig.CLAIRVOYANCE_RANGE==47 && MTConfig.RIPPER_DURABILITY==2345 && MTConfig.RIPPER_DAMAGE==4.5,"Server settings drive client prediction and attributes");boolean rejected=false;try{new SpellConfigPacket(-1,1,1,1,1);}catch(IllegalArgumentException expected){rejected=true;}require(rejected,"Invalid remote spell config rejected");}finally{before.apply();wire.release();}
  System.out.println("PASS: SIX_FAMILY_CONFIG: native wire, server-controlled prediction and knife settings, bounds and restoration");
 }
 public static void run(ServerPlayer fixture){
  var world=fixture.level();var player=DoomIntegration.player(world,"SixFamiliesCheck",GameType.SURVIVAL,464);aim(player,464.5,280,464.5,0);
  player.setNoGravity(true);var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(player.getUUID(),player);
  worldCleanup(player,new AABB(448,276,448,492,290,508));
  try{configWire(player);rituals(player);insight(player);prediction(player);reversion(player);cup(player);ripper(player);gandr(player);}finally{player.stopUsingItem();player.getInventory().clearContent();player.removeAllEffects();players.remove(player.getUUID());player.discard();GandrSpellScroll.gandrs.clear();}
 }
}
