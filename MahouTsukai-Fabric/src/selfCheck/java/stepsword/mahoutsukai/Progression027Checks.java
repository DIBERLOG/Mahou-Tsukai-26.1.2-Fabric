package stepsword.mahoutsukai;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameType;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.doom.DoomEntity;
public final class Progression027Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void run(net.minecraft.server.level.ServerPlayer fixture){
  var world=fixture.level();var sword=new ItemStack(ModItems.REGISTERED.get("clarent"));var stats=Utils.getSwordMahou(sword);stats.setAttackDamage(300);Utils.setSwordMahou(sword,stats);
  Clarent.decay(sword,100);Clarent.decay(sword,1300);
  require(Math.abs(Utils.getSwordMahou(sword).getAttackDamage()-200)<.001&&Math.abs(Clarent.remembered(sword)-50)<.001,"Half-life: 150 temporary lost, 50 remembered, total 200");
  Clarent.decay(sword,2500);require(Math.abs(Utils.getSwordMahou(sword).getAttackDamage()-150)<.001&&Math.abs(Clarent.remembered(sword)-75)<.001,"Each decay transfers exactly one third into memory");
  var copy=sword.copy();Clarent.decay(copy,100000);require(Math.abs(Utils.getSwordMahou(copy).getAttackDamage()-100)<.01&&Math.abs(Clarent.remembered(copy)-100)<.01,"Only one third survives complete decay, memory and timestamp copy with stack");
  var ops=world.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);var encoded=ItemStack.CODEC.encodeStart(ops,copy).getOrThrow();var restored=ItemStack.CODEC.parse(ops,encoded).getOrThrow();require(Math.abs(Clarent.remembered(restored)-100)<.01,"Permanent memory survives native item serialization");
  Clarent.decay(copy,200000);require(Math.abs(Utils.getSwordMahou(copy).getAttackDamage()-100)<.01,"Permanent floor does not decay");
  stats=Utils.getSwordMahou(sword);stats.setAttackDamage(500000);Utils.setSwordMahou(sword,stats);Clarent.decay(sword,2500);require(Utils.getSwordMahou(sword).getAttackDamage()==1600&&((Clarent)sword.getItem()).getAttack(sword)==1600,"Existing oversized Clarent clamps at 1600");
  var staff=new ItemStack(ModItems.REGISTERED.get("mystic_staff"));for(int mode=0;mode<3;mode++){Utils.setMode(staff,mode);require(stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(staff)==stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff.chargeTicks(staff)&&stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(staff)>0,"Staff HUD shares mode-dependent preparation time");}
  System.out.println("PASS: PROGRESSION_027: Clarent decay/memory/copy/serialization/cap and mode-dependent staff timing");
 }
 public static void runGravity(net.minecraft.server.level.ServerLevel world){
  var sword=new ItemStack(ModItems.REGISTERED.get("clarent"));var stats=Utils.getSwordMahou(sword);stats.setAttackDamage(1600);Utils.setSwordMahou(sword,stats);
  var doom=new DoomEntity(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(),world);doom.setPos(512,250,512);
  var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);var far=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.setPos(592,250,512);far.setPos(590,250,570);world.addFreshEntity(cow);world.addFreshEntity(far);
  var player=DoomIntegration.player(world,"Pull027",GameType.SURVIVAL,590);var creative=DoomIntegration.player(world,"PullCreative027",GameType.CREATIVE,589);var spectator=DoomIntegration.player(world,"PullSpectator027",GameType.SPECTATOR,588);
  try{System.out.println("PULL_CANDIDATES="+world.getEntitiesOfClass(LivingEntity.class,doom.getBoundingBox().inflate(DoomEntity.PULL_RADIUS)).size()+" BOUNDS="+doom.getBoundingBox());doom.attract(world,158);System.out.println("PULL_VELOCITIES cow="+cow.getDeltaMovement()+" player="+player.getDeltaMovement()+" creative="+creative.getDeltaMovement());require(cow.getDeltaMovement().x<0&&player.getDeltaMovement().x<0&&creative.getDeltaMovement().x<0,"Gravity pulls mobs, survival players and creative players beyond lethal range");cow.setNoGravity(true);cow.tick();require(cow.getX()<592,"Native mob physics actually moves inward after attraction");require(far.getDeltaMovement().lengthSqr()==0&&spectator.getDeltaMovement().lengthSqr()==0,"Gravity respects outer radius and spectator mode");
   player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,sword);var result=stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingHurt(cow,player.damageSources().playerAttack(player),99999);require(result==1600,"Actual Clarent damage hook cannot exceed cap");
  }finally{cow.discard();far.discard();for(var p:java.util.List.of(player,creative,spectator))world.removePlayerImmediately(p,Entity.RemovalReason.DISCARDED);}
  System.out.println("PASS: GRAVITY_027: 96-block mob/survival/creative pull, spectator/outer-radius exclusion and actual 1600 damage cap");
 }
}
