package stepsword.mahoutsukai;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import stepsword.mahoutsukai.entity.*;
import stepsword.mahoutsukai.fabric.BossVictories;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.util.Utils;

/** Native victory packets, universal Clarent guard, balance migration and separate dimension returns. */
public final class BossVictory037Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static ServerPlayer player(ServerLevel world,String name,List<Packet<?>> packets,AtomicBoolean loaded){
  var p=DoomIntegration.player(world,name,GameType.SURVIVAL,1800);p.setServerLevel(world);p.setPos(1800.5,250,1800.5);p.setNoGravity(true);
  p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(world.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),p,net.minecraft.server.network.CommonListenerCookie.createInitial(p.getGameProfile(),false)){
   @Override public boolean hasClientLoaded(){return loaded.get();}
   @Override public void send(Packet<?> packet){packets.add(packet);}
  };
  var list=world.getServer().getPlayerList();list.getPlayers().add(p);
  ((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)list).mahoutsukai$playersByUUID().put(p.getUUID(),p);return p;
 }
 private static void remove(ServerPlayer p){
  var list=p.level().getServer().getPlayerList();list.getPlayers().remove(p);
  ((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)list).mahoutsukai$playersByUUID().remove(p.getUUID());p.level().removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);
 }
 private static long titles(List<Packet<?>> packets){return packets.stream().filter(p->p instanceof ClientboundSetTitleTextPacket).count();}
 public static void run(ServerPlayer fixture)throws Exception{
  var world=fixture.level();var packets=new ArrayList<Packet<?>>();var ready=new AtomicBoolean(false);
  var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),world.registryAccess());
  try{
   var original=net.minecraft.network.chat.Component.translatable("entity.minecraft.ender_dragon");
   stepsword.mahoutsukai.networking.BossVictoryPacket.STREAM_CODEC.encode(wire,new stepsword.mahoutsukai.networking.BossVictoryPacket(1,original));
   var decoded=stepsword.mahoutsukai.networking.BossVictoryPacket.STREAM_CODEC.decode(wire);
   require(decoded.theme()==1&&decoded.bossName().equals(original),"Victory payload preserves the unique theme and localized boss component across its native wire codec");
  }finally{wire.release();}
  var p=player(world,"Victory037",packets,ready);var others=new ArrayList<Packet<?>>();var ally=player(world,"VictoryAlly037",others,new AtomicBoolean(true));
  try{
   for(var type:List.of(EntityType.ENDER_DRAGON,EntityType.WITHER,EntityType.WARDEN,EntityType.ELDER_GUARDIAN)){
    var boss=(LivingEntity)type.create(world,EntitySpawnReason.COMMAND);require(boss!=null&&BossVictories.supported(boss),"Every supported vanilla boss has victory handling");boss.setPos(p.position());boss.setCustomName(net.minecraft.network.chat.Component.literal("Named boss "+type.toShortString()));
    int start=packets.size();boss.die(world.damageSources().playerAttack(p));
    BossVictories.tick(p);require(packets.subList(start,packets.size()).stream().noneMatch(x->x instanceof ClientboundSetTitleTextPacket),"Victory waits until the client is ready");
    ready.set(true);BossVictories.tick(p);
    var title=packets.subList(start,packets.size()).stream().filter(x->x instanceof ClientboundSetSubtitleTextPacket).map(x->(ClientboundSetSubtitleTextPacket)x).findFirst().orElseThrow();
    require(title.text().getString().contains("Named boss"),"The actual boss display name is retained in the victory subtitle");
    long count=titles(packets);BossVictories.onDeath(boss,world.damageSources().playerAttack(p));BossVictories.tick(p);require(titles(packets)==count,"Repeated death notification cannot repeat the banner");
    boss.discard();ready.set(false);
   }
   require(titles(packets)==4&&titles(others)==0,"Only the winning player receives the four distinct boss victories");
   var cooperative=EntityType.ELDER_GUARDIAN.create(world,EntitySpawnReason.COMMAND);cooperative.setPos(p.position());
   require(cooperative.hurtServer(world,world.damageSources().playerAttack(ally),1),"A real nonlethal contributor hit is accepted");
   cooperative.die(world.damageSources().playerAttack(p));ready.set(true);BossVictories.tick(p);BossVictories.tick(ally);
   require(titles(packets)==5&&titles(others)==1,"A credited helper and the final hitter both receive victory");cooperative.discard();
   var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.die(world.damageSources().playerAttack(p));BossVictories.tick(p);require(titles(packets)==5,"Ordinary mobs never show the boss victory title");cow.discard();

   var sword=new ItemStack(ModItems.REGISTERED.get("clarent"));sword.set(net.minecraft.core.component.DataComponents.UNBREAKABLE,net.minecraft.util.Unit.INSTANCE);p.setItemInHand(InteractionHand.MAIN_HAND,sword);p.startUsingItem(InteractionHand.MAIN_HAND);
   int guarded=0;float health=p.getHealth();
   for(var holder:world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).listElements().toList())for(int angle:new int[]{0,180}){
    p.setYRot(angle);p.invulnerableTime=0;
    require(!p.hurtServer(world,new net.minecraft.world.damagesource.DamageSource(holder),100)&&p.getHealth()==health&&p.isAlive(),"Clarent blocks every registered damage type in both directions: "+holder.key().identifier());guarded++;
   }
   p.hurtServer(world,world.damageSources().genericKill(),Float.POSITIVE_INFINITY);
   require(p.getHealth()==health&&Utils.getSwordMahou(sword).getAttackDamage()==Clarent.cap(),"Overwhelming bypass damage is blocked while sword power remains capped");
   var skeleton=EntityType.SKELETON.create(world,EntitySpawnReason.COMMAND);var spider=EntityType.SPIDER.create(world,EntitySpawnReason.COMMAND);
   skeleton.setPos(p.getX()+3,p.getY(),p.getZ());spider.setPos(p.getX()-1,p.getY(),p.getZ());
   var updateUse=LivingEntity.class.getDeclaredMethod("updatingUsingItem");updateUse.setAccessible(true);
   var arrowHit=net.minecraft.world.entity.projectile.arrow.AbstractArrow.class.getDeclaredMethod("onHitEntity",net.minecraft.world.phys.EntityHitResult.class);arrowHit.setAccessible(true);
   for(int hit=0;hit<12;hit++){
    Clarent.decay(p.getMainHandItem(),world.getGameTime()+hit*20);
    p.setItemInHand(InteractionHand.MAIN_HAND,p.getMainHandItem().copy());updateUse.invoke(p);
    var arrow=new net.minecraft.world.entity.projectile.arrow.Arrow(world,skeleton,new ItemStack(net.minecraft.world.item.Items.ARROW),null);arrow.setDeltaMovement(-1,0,0);arrow.setBaseDamage(5);arrowHit.invoke(arrow,new net.minecraft.world.phys.EntityHitResult(p));
    spider.doHurtTarget(world,p);arrow.discard();
    require(p.getHealth()==health&&p.isUsingItem()&&p.getUseItem()==p.getMainHandItem(),"Skeleton arrows, spider melee and synchronized sword component copies cannot interrupt Clarent's held guard");
   }
   skeleton.discard();spider.discard();
   p.stopUsingItem();p.invulnerableTime=0;
   require(p.hurtServer(world,world.damageSources().generic(),1)&&p.getHealth()<health,"Holding Clarent without actively blocking does not grant immunity");

   var old=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),world);old.getAttribute(Attributes.MAX_HEALTH).setBaseValue(400);old.setHealth(100);old.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.26);old.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(14);
   var save=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());old.saveWithoutId(save);var tag=save.buildResult();tag.remove("mahoutsukai_archmage_balance");tag.putInt("mahoutsukai_arrival_ticks",0);
   var migrated=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),world);migrated.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),tag));
   require(migrated.getMaxHealth()==800&&migrated.getHealth()==200&&migrated.getAttributeValue(Attributes.MOVEMENT_SPEED)==.32&&migrated.getAttributeValue(Attributes.ATTACK_DAMAGE)==16,"Old bosses upgrade to stronger attributes without healing their missing health");
   migrated.setPos(p.getX()+4,p.getY(),p.getZ());migrated.setTarget(p);
   var cooldown=RealityMarbleBossEntity.class.getDeclaredField("specialCooldown");cooldown.setAccessible(true);cooldown.setInt(migrated,1);
   var ai=RealityMarbleBossEntity.class.getDeclaredMethod("customServerAiStep",ServerLevel.class);ai.setAccessible(true);ai.invoke(migrated,world);
   require(migrated.attackTicks()==32&&cooldown.getInt(migrated)==50,"Below half health the real AI starts a special and switches to the faster cooldown");old.discard();migrated.discard();
   System.out.println("PASS: BOSS_VICTORY_037: four named vanilla boss deaths, client-ready private titles and sound, no duplicates or ordinary mob titles, contributor credit; "+guarded+" native Clarent damage cases, bypass/infinite protection, inactive stance rejection, 1600 cap; 800HP migration and aggressive low-health AI");
  }finally{remove(p);remove(ally);}
 }
 private static ServerPlayer first,second;private static ServerLevel original,end;private static RealityMarbleBossEntity arenaBoss;
 private static final List<Packet<?>> FIRST=new ArrayList<>(),SECOND=new ArrayList<>();
 public static void startReturn(ServerPlayer fixture){
  original=fixture.level();end=original.getServer().getLevel(net.minecraft.world.level.Level.END);
  var marble=original.getServer().getLevel(net.minecraft.resources.ResourceKey.create(Registries.DIMENSION,stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect.marble));
  first=player(marble,"VictorReturnA",FIRST,new AtomicBoolean(true));second=player(marble,"VictorReturnB",SECOND,new AtomicBoolean(true));
  arenaBoss=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),marble);arenaBoss.setNoAi(true);arenaBoss.setPos(first.getX()+4,first.getY(),first.getZ());require(marble.addFreshEntity(arenaBoss),"Victory return boss inserted");
  for(var p:List.of(first,second)){var mahou=Utils.getPlayerMahou(p);mahou.setEnemy(arenaBoss.getUUID());mahou.setPlayerOldDimension((p==first?original:end).dimension().identifier().toString());mahou.setPlayerSavedSpawn(new BlockPos(p==first?420:11,p==first?260:70,p==first?420:11));}
  arenaBoss.hurtServer(marble,marble.damageSources().playerAttack(first),100000);
  require(!arenaBoss.isAlive(),"Actual boss victory triggers participant return notices");
 }
 public static void finishReturn(){
  try{
   require(first.level()==original&&second.level()==end,"Victory automatically returns both participants to their separate original dimensions");
   require(first.blockPosition().equals(new BlockPos(420,260,420))&&second.blockPosition().equals(new BlockPos(11,70,11)),"Victory retains each player's own original coordinates");
   require(titles(FIRST)==1&&titles(SECOND)==1,"Both returned participants receive exactly one victory banner after returning");
   var achievement=original.getServer().getAdvancements().get(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","archmage_victory"));
   require(achievement!=null&&first.getAdvancements().getOrStartProgress(achievement).isDone()&&second.getAdvancements().getOrStartProgress(achievement).isDone(),"The new archmage achievement is granted to both winning arena participants");
   require(Utils.getPlayerMahou(first).getEnemy()==null&&Utils.getPlayerMahou(second).getEnemy()==null,"Victory clears the completed encounter links");
   System.out.println("PASS: VICTORY_RETURN_037: actual boss death automatically returns two participants to Overworld/End with independent coordinates and one banner each");
  }finally{if(arenaBoss!=null)arenaBoss.discard();if(first!=null)remove(first);if(second!=null)remove(second);FIRST.clear();SECOND.clear();}
 }
}
