package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.*;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.networking.SelectiveDisplacementPacket;
public final class ReplicationSelectiveIntegration {
 private static void require(boolean value,String message) {if(!value)throw new AssertionError(message);}
 private static ItemStack cast(ServerPlayer player,String name) {var stack=new ItemStack(ModItems.REGISTERED.get("scroll_"+name));((SpellScroll)stack.getItem()).releaseUsing(stack,player.level(),player,71900);return stack;}
 public static void run(ServerPlayer fixture) {
  var world=fixture.level();var primary=DoomIntegration.player(world,"ReplicaCheck",GameType.SURVIVAL,460);primary.setPos(460.5,250,465.5);primary.setYRot(180);primary.setYHeadRot(180);primary.setXRot(8);
  var mahou=((MahouHolder)primary).mahoutsukai$getMahou();mahou.setMaxMana(2000);mahou.setStoredMana(1000);
  var target=EntityType.COW.create(world,EntitySpawnReason.COMMAND);target.setPos(460.5,250,460.5);world.addFreshEntity(target);
  require(cast(primary,"damage_replication").isEmpty() && target.getUUID().equals(mahou.getDamageReplicationTarget()),"Original replication scroll selects aimed living target and consumes");
  require(mahou.getStoredMana()==1000-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(stepsword.mahoutsukai.config.MTConfig.DAMAGE_REPLICATION_MANA_COST),"Replication charges original configured mana");
  primary.setHealth(20);primary.invulnerableTime=0;target.invulnerableTime=0;primary.hurtServer(world,primary.damageSources().generic(),2);
  require(primary.getHealth()==18 && target.getHealth()==8,"Native incoming damage replicates unchanged amount to linked target");
  primary.addEffect(new MobEffectInstance(MobEffects.POISON,200,1));require(target.hasEffect(MobEffects.POISON) && target.getEffect(MobEffects.POISON).getAmplifier()==1 && target.getEffect(MobEffects.POISON).getDuration()==200,"Native effect addition replicates duration and amplifier");
  stepsword.mahoutsukai.potion.ProbabilityAlterPotion.createProbabilityAlter(primary,2,7);primary.addEffect(new MobEffectInstance(ModEffects.PROBABILITY_ALTER,100));
  require(target.getRandom() instanceof stepsword.mahoutsukai.item.rulebreaker.ProbabilityAlterRandom random && random.designation==2 && random.modulus==7,"Replication preserves original Rule Breaker deterministic random designation and modulus");
  var secondary=DoomIntegration.player(world,"ReplicaLoop",GameType.SURVIVAL,464);secondary.setPos(464.5,250,465.5);var secondMahou=((MahouHolder)secondary).mahoutsukai$getMahou();
  mahou.setDamageReplicationTarget(secondary.getUUID());secondMahou.setDamageReplicationTarget(primary.getUUID());primary.invulnerableTime=0;secondary.invulnerableTime=0;
  primary.hurtServer(world,primary.damageSources().generic(),2);
  require(primary.getHealth()==16 && secondary.getHealth()==18,"Mutual native damage links stop at original chain guard");
  primary.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,90,2));require(secondary.hasEffect(MobEffects.RESISTANCE) && secondary.getEffect(MobEffects.RESISTANCE).getAmplifier()==2,"Mutual effect links terminate without recursion");
  mahou.setDamageReplicationTarget(null);secondMahou.setDamageReplicationTarget(target.getUUID());secondary.invulnerableTime=0;secondary.hurtServer(world,secondary.damageSources().genericKill(),1000);
  require(secondMahou.getDamageReplicationTarget()==null,"Actual player death clears original replication link");primary.discard();secondary.discard();target.discard();
  var swapper=DoomIntegration.player(world,"SwapCheck",GameType.SURVIVAL,470);swapper.setPos(470.5,250,460.5);var swapMahou=((MahouHolder)swapper).mahoutsukai$getMahou();swapMahou.setMaxMana(2000);swapMahou.setStoredMana(2000);
  var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(474.5,250,460.5);world.addFreshEntity(cow);var pig=EntityType.PIG.create(world,EntitySpawnReason.COMMAND);pig.setPos(478.5,250,460.5);world.addFreshEntity(pig);
  SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,cow.getId(),0);require(swapper.getX()==470.5 && cow.getX()==474.5,"Selective request without original spell buff does nothing");
  require(cast(swapper,"selective_displacement").isEmpty() && swapMahou.getStoredMana()==2000-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(stepsword.mahoutsukai.config.MTConfig.SELECTIVE_DISPLACEMENT_MANA_COST),"Selective scroll grants ability for original mana cost");
  SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,cow.getId(),cow.getId());
  require(swapper.getX()==474.5 && cow.getX()==470.5 && swapper.hasEffect(ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN),"Original selective packet swaps caster and target and applies cooldown");
  SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,pig.getId(),0);require(swapper.getX()==474.5 && pig.getX()==478.5,"Cooldown rejects repeated selective packet");swapper.removeEffect(ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN);
  SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,cow.getId(),pig.getId());
  require(cow.getX()==478.5 && pig.getX()==470.5 && stepsword.mahoutsukai.config.MandatoryFun.meetsFunReq(swapper,stepsword.mahoutsukai.config.MandatoryFun.FUN_REQS.SWAP_TWO_THINGS),"Selective swaps two other entities and records original progression marker");swapper.removeEffect(ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN);
  var distant=EntityType.COW.create(world,EntitySpawnReason.COMMAND);distant.setPos(580.5,250,460.5);world.addFreshEntity(distant);SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,distant.getId(),0);
  require(swapper.getX()==474.5 && distant.getX()==580.5 && !swapper.hasEffect(ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN),"Server validates original selection bounds instead of trusting arbitrary network entity IDs");
  SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(swapper,-1,0);require(swapper.getX()==474.5,"Stale or invalid entity ID does nothing");
  var wire=io.netty.buffer.Unpooled.buffer();try {var packet=new SelectiveDisplacementPacket(cow.getId(),pig.getId());SelectiveDisplacementPacket.STREAM_CODEC.encode(wire,packet);require(wire.readableBytes()==8 && SelectiveDisplacementPacket.STREAM_CODEC.decode(wire).id1==cow.getId(),"Original selective eight-byte packet format preserved");}finally{wire.release();}
  var pos=new BlockPos(470,250,470);ExchangeCircleIntegration.ritual(swapper,pos,"damage_replication",true,"gold","gold","emerald");world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());ExchangeCircleIntegration.ritual(swapper,pos,"selective_displacement",true,"eye","ender","emerald");world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
  swapper.discard();cow.discard();pig.discard();distant.discard();
  System.out.println("PASS: REPLICATION_SELECTIVE_INTEGRATION: original scrolls/recipes/costs, native damage/effect chains, deterministic RNG, death cleanup, selective swaps/cooldown/progression, bounds/invalid ID validation and wire format");
 }
}
