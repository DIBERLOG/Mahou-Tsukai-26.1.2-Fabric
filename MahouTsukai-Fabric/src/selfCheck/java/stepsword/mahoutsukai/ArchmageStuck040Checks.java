package stepsword.mahoutsukai;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;

/** Native geometry and runtime checks in the isolated, already ticking combat area. */
public final class ArchmageStuck040Checks {
 private static final BlockPos CENTER=new BlockPos(528,270,560);
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 @SuppressWarnings("unchecked")
 private static void synced(RealityMarbleBossEntity boss,String name,int value)throws Exception{
  var field=RealityMarbleBossEntity.class.getDeclaredField(name);field.setAccessible(true);
  boss.getEntityData().set((EntityDataAccessor<Integer>)field.get(null),value);
 }
 private static void ticks(Method tick,RealityMarbleBossEntity boss,ServerLevel level,int count)throws Exception{
  for(int i=0;i<count;i++)tick.invoke(boss,level);
 }
 private static int cooldown(RealityMarbleBossEntity boss)throws Exception{
  var field=RealityMarbleBossEntity.class.getDeclaredField("escapeCooldown");field.setAccessible(true);return field.getInt(boss);
 }
 public static void run(ServerPlayer fixture)throws Exception{
  var level=fixture.level();var origin=Vec3.atBottomCenterOf(CENTER);
  var blocks=new LinkedHashMap<BlockPos,BlockState>();
  var boss=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),level);
  var target=DoomIntegration.player(level,"ArchmageEscape040",GameType.SURVIVAL,CENTER.getX()+12);
  target.setServerLevel(level);target.setNoGravity(true);target.setPos(origin.add(12,0,0));
  var tick=RealityMarbleBossEntity.class.getDeclaredMethod("tickStuckRecovery",ServerLevel.class);tick.setAccessible(true);
  var safe=RealityMarbleBossEntity.class.getDeclaredMethod("safeEscape",ServerLevel.class,Vec3.class);safe.setAccessible(true);
  for(var mutable:BlockPos.betweenClosed(CENTER.offset(-7,-1,-7),CENTER.offset(7,6,7))){
   var pos=mutable.immutable();blocks.put(pos,level.getBlockState(pos));
   level.setBlock(pos,pos.getY()==CENTER.getY()-1?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState(),18);
  }
  try{
   boss.setArena(CENTER);boss.setPos(origin);require(level.addFreshEntity(boss),"Escape fixture registers a native boss");
   ticks(tick,boss,level,80);require(boss.position().equals(origin),"A dormant shrine guard never blinks");
   boss.awaken();boss.setTarget(target);
   ticks(tick,boss,level,80);require(boss.position().equals(origin),"Entrance poses are not stuck movement");synced(boss,"ARRIVAL",0);
   boss.setNoAi(true);ticks(tick,boss,level,80);require(boss.position().equals(origin),"Disabled AI never teleports");boss.setNoAi(false);
   for(var state:java.util.List.of("ATTACK_TICKS","RECOVERY")){
    synced(boss,state,1);ticks(tick,boss,level,80);require(boss.position().equals(origin),"Intentional "+state+" pose never blinks");synced(boss,state,0);
   }
   boss.setNoGravity(true);ticks(tick,boss,level,80);require(boss.position().equals(origin),"Levitation is not stuck pursuit");boss.setNoGravity(false);
   target.setPos(origin.add(1,0,0));ticks(tick,boss,level,80);require(boss.position().equals(origin),"Melee range never triggers an escape");target.setPos(origin.add(12,0,0));
   for(int i=0;i<60;i++){boss.setPos(origin.add(0,0,i*.1));tick.invoke(boss,level);}
   require(boss.position().equals(origin.add(0,0,5.9))&&cooldown(boss)==0,"Normal movement continually resets the stuck observation");
   boss.setPos(origin);boss.setTarget(null);tick.invoke(boss,level);boss.setTarget(target);
   ticks(tick,boss,level,RealityMarbleBossEntity.STUCK_TICKS);
   require(boss.position().equals(origin),"Stationary pursuit preserves the full two-second warning");
   // Let the real entity tick call the watchdog: this also checks that it is wired into gameplay.
   boss.tick();
   var escaped=boss.position();
   require(escaped.distanceToSqr(origin)>=3.9&&escaped.distanceToSqr(origin)<=25.000001,"A real server tick unsticks the boss by at most five blocks");
   require((boolean)safe.invoke(boss,level,escaped)&&boss.getDeltaMovement().lengthSqr()==0&&boss.fallDistance==0,"Escape lands in a dry, supported, collision-free full-body space and clears momentum");
   require(cooldown(boss)==RealityMarbleBossEntity.ESCAPE_COOLDOWN,"Successful escape starts a five-second cooldown");
   ticks(tick,boss,level,80);require(boss.position().equals(escaped),"A stationary boss cannot chain blink before the cooldown ends");

   // Probe the actual native block/entity queries, including partial support and head clearance.
   boss.setPos(origin);var landing=origin.add(3,0,0);var at=BlockPos.containing(landing);
   require((boolean)safe.invoke(boss,level,landing),"A dry, fully supported space is accepted");
   level.setBlock(at.above(2),Blocks.STONE.defaultBlockState(),18);
   require(!(boolean)safe.invoke(boss,level,landing),"A three-block-tall Archmage cannot blink into a low ceiling");level.setBlock(at.above(2),Blocks.AIR.defaultBlockState(),18);
   for(var fluid:java.util.List.of(Blocks.WATER,Blocks.LAVA)){
    level.setBlock(at,fluid.defaultBlockState(),18);require(!(boolean)safe.invoke(boss,level,landing),"Escape rejects "+fluid);level.setBlock(at,Blocks.AIR.defaultBlockState(),18);
   }
   level.setBlock(at.below(),Blocks.AIR.defaultBlockState(),18);
   require(!(boolean)safe.invoke(boss,level,landing),"An unsupported destination is rejected");level.setBlock(at.below(),Blocks.STONE.defaultBlockState(),18);
   var edge=landing.add(.4,0,0);level.setBlock(at.east().below(),Blocks.AIR.defaultBlockState(),18);
   require(!(boolean)safe.invoke(boss,level,edge),"The full boss footprint needs support, not only its center");level.setBlock(at.east().below(),Blocks.STONE.defaultBlockState(),18);
   target.setPos(landing);require(!(boolean)safe.invoke(boss,level,landing),"An occupied player space cannot be used for escape");target.setPos(origin.add(12,0,0));
   require(!(boolean)safe.invoke(boss,level,origin.add(5,1,0)),"The five-block limit includes vertical displacement");
   boss.setArena(CENTER.offset(-159,0,0));require(!(boolean)safe.invoke(boss,level,landing),"Escape cannot cross the encounter boundary");boss.setArena(CENTER);

   // A completely sealed trap must fail safely, then recover once an exit becomes available.
   for(var mutable:BlockPos.betweenClosed(CENTER.offset(-5,0,-5),CENTER.offset(5,5,5)))level.setBlock(mutable,Blocks.STONE.defaultBlockState(),18);
   boss.setTarget(null);tick.invoke(boss,level);boss.setTarget(target);ticks(tick,boss,level,150);
   require(boss.position().equals(origin),"No safe destination leaves the boss in place without breaking blocks");
   for(var mutable:BlockPos.betweenClosed(CENTER.offset(-5,0,-5),CENTER.offset(5,5,5)))level.setBlock(mutable,Blocks.AIR.defaultBlockState(),18);
   ticks(tick,boss,level,RealityMarbleBossEntity.STUCK_TICKS+41);
   require(boss.position().distanceToSqr(origin)>1&&boss.position().distanceToSqr(origin)<=25.000001,"Failed searches can retry after the obstruction is removed");
   System.out.println("ARCHMAGE_ESCAPE_040_OK: runtime hook, movement, poses, cooldown, radius, support, headroom, fluids, occupants, arena boundary, sealed-trap retry");
  }finally{
   boss.discard();level.removePlayerImmediately(target,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
   for(var entry:blocks.entrySet())level.setBlock(entry.getKey(),entry.getValue(),18);
  }
 }
}
