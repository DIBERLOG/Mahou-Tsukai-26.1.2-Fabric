package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.entity.mahoujin.*;
import stepsword.mahoutsukai.effects.projection.*;
public final class SwordMarbleIntegration {
 static ItemEntity lanceOffering;static float lanceDamage,lanceWeight;
 static ServerPlayer p,traveler;static ItemEntity offering;static int initial;static ServerLevel world;static BlockPos marbleSpawn;
 static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 static void add(ServerPlayer player){((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)player.level().getServer().getPlayerList()).mahoutsukai$playersByUUID().put(player.getUUID(),player);}
 static void remove(ServerPlayer player){((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)player.level().getServer().getPlayerList()).mahoutsukai$playersByUUID().remove(player.getUUID());player.discard();}
 public static void start(ServerPlayer fixture){
  world=fixture.level();p=DoomIntegration.player(world,"SwordNative",GameType.SURVIVAL,580);add(p);RemainingFamiliesIntegration.ready(p,580.5,576.5);var center=new BlockPos(580,276,576);
  for(var pos:BlockPos.betweenClosed(center.offset(-7,-1,-7),center.offset(7,0,7)))world.setBlockAndUpdate(pos,pos.getY()<276?Blocks.STONE.defaultBlockState():ModBlocks.murkyFluid.get().defaultBlockState());
  var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS),3);offering=new ItemEntity(world,580.5,276.1,576.5,sword);offering.setThrower(p);offering.setNoGravity(true);world.addFreshEntity(offering);require(Utils.isInMurkyWater(offering) && offering.getOwner()==p,"Actual owned enchanted offering submerged in native murky source");
  lanceOffering=null;
  if(stepsword.mahoutsukai.fabric.JoustingCompatibility.loaded()){
   var lance=new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("jousting","lance_iron")));lance.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS),3);
   var clean=lance.copy();net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(clean,e->e.removeIf(a->true));
   float full=stepsword.mahoutsukai.item.lance.Rhongomyniad.simulateHit(lance,world),base=stepsword.mahoutsukai.item.lance.Rhongomyniad.simulateHit(clean,world);
   lanceDamage=(float)(full-MTConfig.RHONGOMYNIAD_NERF_FACTOR*(full-base))-1;lanceWeight=(float)(((stepsword.jousting.item.ItemLance)lance.getItem()).getWeight(lance)-.02f);
   lanceOffering=new ItemEntity(world,581.5,276.1,576.5,lance);lanceOffering.setThrower(p);lanceOffering.setNoGravity(true);world.addFreshEntity(lanceOffering);
  }
  var data=(ServerLevelData)world.getLevelData();long time=world.getGameTime();data.setGameTime(time-time%Math.max(1,MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE));initial=Utils.getPlayerMahou(p).getStoredMana();try{PowerConsolidationSpellEffect.powerConsolidationWorldTick(world);}finally{data.setGameTime(time);}
  traveler=DoomIntegration.player(world,"MarbleNative",GameType.SURVIVAL,550);add(traveler);RemainingFamiliesIntegration.ready(traveler,550.5,536.5);RemainingFamiliesIntegration.recipe(traveler,"reality_marble","diamond","diamond","diamond");var marble=world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION,RealityMarbleSpellEffect.marble));require(marble!=null,"Native original Reality Marble datapack dimension exists");
  var personalSpawn=new BlockPos(16,120,16);Utils.getWorldMahou(world).changePlayerSpawn(traveler.getUUID(),personalSpawn);Utils.getPlayerMahou(traveler).setPlayerMarbleSpawn(personalSpawn);var scroll=RemainingFamiliesIntegration.scroll("reality_marble");RemainingFamiliesIntegration.cast(traveler,scroll);require(scroll.isEmpty() && Utils.getPlayerMahou(traveler).getStoredMana()==100000-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(MTConfig.MARBLE_MANA_COST),"Reality Marble original ritual/mana consumed");
  marbleSpawn=Utils.getPlayerMahou(traveler).getPlayerMarbleSpawn();
  require(marbleSpawn!=null&&marbleSpawn.distSqr(personalSpawn)>1000L*1000&&personalSpawn.equals(Utils.getWorldMahou(world).getPlayerSpawn(traveler.getUUID())),"A new boss encounter receives fresh terrain without replacing the personal duel allocation");
 }
 public static void verify(){
  if(lanceOffering!=null){require(lanceOffering.isRemoved(),"Actual enchanted Jousting offering converted in native lake tick");var rhongo=world.getEntitiesOfClass(stepsword.mahoutsukai.entity.WeaponProjectileEntity.class,new AABB(570,270,565,590,285,587),e->e.getStack().getItem() instanceof stepsword.mahoutsukai.item.lance.Rhongomyniad).stream().findFirst().orElseThrow();var stats=Utils.getLanceMahou(rhongo.getStack());require(Math.abs(stats.getAttackDamage()-lanceDamage)<.0001 && Math.abs(stats.getWeight()-lanceWeight)<.0001,"Native lake preserves original Rhongomyniad damage/weight formula");rhongo.discard();System.out.println("PASS: JOUSTING_LAKE: real enchanted owned lance -> Rhongomyniad, original damage/weight and actual mana payment");}

  require(offering.isRemoved() && Utils.getPlayerMahou(p).getStoredMana()==initial-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(MTConfig.POWER_CONSOLIDATION_SWORD_MANA_COST)*(lanceOffering==null?1:2),"Lake conversion consumes owned offering and charges original 5000 mana");var produced=world.getEntitiesOfClass(stepsword.mahoutsukai.entity.WeaponProjectileEntity.class,new AABB(570,270,565,590,285,587),e->e.getStack().is(ModItems.caliburn.get())).stream().findFirst().orElseThrow();var sword=produced.getStack().copy();produced.discard();require(Utils.getSwordMahou(sword).getAttackDamage()>0 && Utils.getSwordMahou(sword).getInnateCap()>0,"Lake creates actual Caliburn with simulated enchanted damage and innate cap");p.setItemInHand(InteractionHand.MAIN_HAND,sword);var wolf=RemainingFamiliesIntegration.mob(p,EntityType.WOLF,580,280,578);wolf.setTame(true,false);wolf.setOwner(p);wolf.hurtServer(world,p.damageSources().playerAttack(p),10000);require(p.getMainHandItem().is(ModItems.morgan.get()),"Actual owned wolf death transforms Caliburn to Morgan");
  var village=RemainingFamiliesIntegration.mob(p,EntityType.VILLAGER,581,280,578);var morgan=p.getMainHandItem();float damage=Utils.getSwordMahou(morgan).getAttackDamage();int before=Utils.getPlayerMahou(p).getStoredMana();village.hurtServer(world,p.damageSources().playerAttack(p),10000);require(Utils.getSwordMahou(p.getMainHandItem()).getAttackDamage()>damage && Utils.getPlayerMahou(p).getStoredMana()==before-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(MTConfig.MORGAN_UPGRADE_MANA_COST),"Actual villager death grows Morgan at original mana cost");p.removeAllEffects();var item=(stepsword.mahoutsukai.item.morgan.Morgan)p.getMainHandItem().getItem();before=Utils.getPlayerMahou(p).getStoredMana();item.onUseTick(world,p,p.getMainHandItem(),71999);require(!world.getEntitiesOfClass(MorganBallEntity.class,p.getBoundingBox().inflate(5)).isEmpty() && Utils.getPlayerMahou(p).getStoredMana()==before-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST),"Morgan native ball ability/mana");item.releaseUsing(p.getMainHandItem(),world,p,71999);
  var zombie=RemainingFamiliesIntegration.mob(p,EntityType.ZOMBIE,579,280,577);p.removeAllEffects();p.setItemInHand(InteractionHand.MAIN_HAND,sword);stepsword.mahoutsukai.item.caliburn.Caliburn.caliburnSmites(p,Utils.getSwordMahou(sword));var smite=world.getEntitiesOfClass(SmiteEntity.class,p.getBoundingBox().inflate(10),e->e.target==zombie).getFirst();float hp=zombie.getHealth();for(int n=0;n<40 && !smite.isRemoved();n++)smite.tick();require(zombie.getHealth()<hp,"Caliburn native smite growth/collapse actually damages undead");zombie.discard();
  require(RealityMarbleSpellEffect.inMarble(traveler.level()),"Original scroll teleports player to native Reality Marble");
  var grounded=Utils.getPlayerMahou(traveler).getPlayerMarbleSpawn();
  require(grounded!=null && Math.abs(grounded.getX()-marbleSpawn.getX())<=8 && Math.abs(grounded.getZ()-marbleSpawn.getZ())<=8,"Grounded arrival stays within the allocated arena's bounded horizontal search");
  require(traveler.position().distanceToSqr(new Vec3(grounded.getX()+.5,grounded.getY(),grounded.getZ()+.5))<1,"Native dimension transition uses the stored grounded destination, without a fixed sky Y");
  var support=BlockPos.containing(traveler.getX(),traveler.getY()-.01,traveler.getZ());
  var floor=traveler.level().getBlockState(support).getCollisionShape(traveler.level(),support);
  require(!floor.isEmpty() && traveler.level().getFluidState(support).isEmpty() && Math.abs(support.getY()+floor.max(Direction.Axis.Y)-traveler.getY())<.01,"The player arrives directly on actual solid dry terrain");
  var body=new AABB(traveler.getX()-.3,traveler.getY()+.001,traveler.getZ()-.3,traveler.getX()+.3,traveler.getY()+3,traveler.getZ()+.3);
  require(traveler.level().noCollision(traveler,body) && !traveler.level().containsAnyLiquid(body),"Grounded entry preserves three blocks of collision-free, dry headroom");
  var bosses=traveler.level().getEntitiesOfClass(stepsword.mahoutsukai.entity.RealityMarbleBossEntity.class,new AABB(grounded.offset(12,0,12)).inflate(64),Entity::isAlive);
  var expectedBoss=Utils.getPlayerMahou(traveler).getEnemy();
  var claimed=stepsword.mahoutsukai.dataattachments.worldsave.RealityMarbleEncounters.get(traveler.level()).near(grounded.offset(12,0,12));
  String encounter=" expected="+expectedBoss+", claimed="+claimed+", loaded="+bosses.stream().map(b->b.getUUID()+" at "+b.position()+" arena "+b.arena()).toList();
  require(bosses.size()==1,"Solo scroll entry has exactly one live Archmage after native load reconciliation:"+encounter);
  var boss=bosses.stream().filter(b->b.getUUID().equals(expectedBoss)).findFirst().orElseThrow(()->new AssertionError("Solo scroll entry assigns the dimension's spawned boss as the encounter opponent:"+encounter));
  traveler.invulnerableTime=0;float health=traveler.getHealth();traveler.hurtServer(traveler.level(),traveler.damageSources().fall(),10);require(traveler.getHealth()==health,"Reality Marble original fall immunity");
  traveler.invulnerableTime=0;traveler.hurtServer(traveler.level(),traveler.damageSources().mobAttack(boss),1);
  require(RealityMarbleSpellEffect.inMarble(traveler.level())&&boss.getUUID().equals(Utils.getPlayerMahou(traveler).getEnemy()),"Ordinary boss damage keeps the solo player in Reality Marble while its boss is alive");
  RealityMarbleSpellEffect.realityMarbleLivingHurt(traveler); // Exercise the handler even when the fake transport has not acknowledged teleport.
  System.out.println("PASS: POWER_SWORDS_INTEGRATION: original 150-source lake + owned enchanted offering -> Caliburn with real enchant simulation and 5000 mana, native tamed-wolf transformation, villager growth/mana, Morgan ball and actual Caliburn smite damage");
 }
 public static void requestReturn(){require(RealityMarbleSpellEffect.inMarble(traveler.level()),"Solo boss encounter survives the next queued server tick after ordinary damage");Utils.getPlayerMahou(traveler).setEnemy(null);RealityMarbleSpellEffect.realityMarbleLivingHurt(traveler);}
 public static void finish(){require(traveler.level()==world && traveler.position().distanceToSqr(new Vec3(550,280,536))<5,"Original saved dimension/position return after duel");remove(traveler);remove(p);System.out.println("PASS: REALITY_MARBLE_INTEGRATION: original datapack dimension generation, ritual/cost, allocated native dimension teleport, fall immunity and saved-position return");}
}
